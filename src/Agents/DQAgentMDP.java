/**
 * Author: Jose G. Perez
 * Deep Q-Learning MDP for use with deeplearning4j to train
 */
package Agents;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import org.deeplearning4j.gym.StepReply;
import org.deeplearning4j.rl4j.learning.sync.qlearning.QLearning;
import org.deeplearning4j.rl4j.learning.sync.qlearning.discrete.QLearningDiscreteDense;
import org.deeplearning4j.rl4j.mdp.MDP;
import org.deeplearning4j.rl4j.network.dqn.DQNFactoryStdDense;
import org.deeplearning4j.rl4j.policy.DQNPolicy;
import org.deeplearning4j.rl4j.space.ArrayObservationSpace;
import org.deeplearning4j.rl4j.space.DiscreteSpace;
import org.deeplearning4j.rl4j.space.ObservationSpace;
import org.deeplearning4j.rl4j.util.DataManager;
import org.json.JSONObject;
import org.nd4j.linalg.learning.config.Adam;

import Configuration.Configuration;
import Observer.Observer;
import RetailMarketManager.RetailMarketManager;

public class DQAgentMDP implements MDP<DQAgentState, Integer, DiscreteSpace> {
    public static QLearning.QLConfiguration QLConfig = new QLearning.QLConfiguration(
    		123, // Seed
            10000, // Max step By epoch
            100, // Max step
            1000000, // Max size of experience replay
            32, // size of batches
            10000, // target update (hard)
            100, // num step noop warmup
            0.01, // reward scaling
            0.99, // gamma
            100.0, // td-error clipping
            0.1f, // min epsilon
            10000, // num step for eps greedy anneal
            true // double DQN
    );

    public static DQNFactoryStdDense.Configuration QLNet = DQNFactoryStdDense.Configuration.builder().l2(0.00).updater(new Adam(0.001)).numHiddenNodes(16).numLayer(3).build();

    private static final int NUM_ACTIONS = 3;

    public static DiscreteSpace actionSpace = new DiscreteSpace(NUM_ACTIONS);
    public static ObservationSpace<DQAgentState> observationSpace = new ArrayObservationSpace<DQAgentState>(new int[] { 3 });

    public static void log(String message, Object... args) {
        Logger.getAnonymousLogger().info("[SimpleMDP]" + String.format(message, args));
    }

    public DQAgentState currentState;
    public DQAgentState startingState;

    private RetailMarketManager retailManager;
    private DQAgent agent;
    private List<Agent> opponentPool;

    public DQAgentMDP(List<Agent> opponentPool) {
        this.opponentPool = opponentPool;
        this.reset();
    }

    public static void trainDQAgent(List<Agent> opponentPool) {
        log("Setting up DeepQ training");
        DQAgentMDP mdp = new DQAgentMDP(opponentPool);
        try {
            // record the training data in rl4j-data in a new folder
            DataManager manager = new DataManager(true);
            QLearningDiscreteDense<DQAgentState> dql = new QLearningDiscreteDense<DQAgentState>(mdp, QLNet, QLConfig, manager);

            log("Training DeepQ");
            dql.train();
            DQNPolicy<DQAgentState> pol = dql.getPolicy();

            log("Saving DeepQ policy");
            pol.save(Configuration.DQLEARNING_POLICY_FILENAME);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mdp.close();
    }

    @Override
    public void close() {

    }

    @Override
    public DiscreteSpace getActionSpace() {
        return actionSpace;
    }

    @Override
    public ObservationSpace<DQAgentState> getObservationSpace() {
        return observationSpace;
    }

    @Override
    public boolean isDone() {
        return Observer.timeslot >= Configuration.TOTAL_TIME_SLOTS;
    }

    @Override
    public MDP<DQAgentState, Integer, DiscreteSpace> newInstance() {
        DQAgentMDP mdp = new DQAgentMDP(this.opponentPool);
        return mdp;
    }

    @Override
    public DQAgentState reset() {
        retailManager = new RetailMarketManager();
        agent = new DQAgent();
        retailManager.ob.agentPool.add(agent);
        retailManager.ob.agentPool.addAll(opponentPool);

        Observer.timeslot = 0;
        this.currentState = this.startingState = new DQAgentState(agent);
        return startingState;
    }

    @Override
    public StepReply<DQAgentState> step(Integer action) {
        if (action == 0) // Increase
            agent.increase(retailManager.ob);
        else if (action == 1) // Decrease
            agent.defect(retailManager.ob);
        else // No Change
            agent.nochange();

        // Perform other agent policies
        for (Agent ag : this.opponentPool) {
            ag.publishTariff(retailManager.ob);
        }

        retailManager.updateAgentsMemory();

        // Run the market evaluation based on the previous action
        // then run the rest of the timeslots so the next call to
        // this function will be a publication cycle
        // Run for 6 TS
        for (int i = 0; i < Configuration.PUBLICATION_CYCLE + 1; i++) {
            retailManager.customerTariffEvaluation();
            retailManager.updateAgentAccountings();
            Observer.timeslot++;
        }

        double reward = agent.profit - agent.prevprofit;
        DQAgentState nextState = new DQAgentState(agent);

        JSONObject info = new JSONObject("{}");
        return new StepReply<DQAgentState>(nextState, reward, this.isDone(), info);
    }

}