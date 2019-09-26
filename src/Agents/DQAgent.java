/**
 * Author: Jose G. Perez
 */
package Agents;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import org.deeplearning4j.gym.StepReply;
import org.deeplearning4j.rl4j.learning.Learning;
import org.deeplearning4j.rl4j.learning.sync.qlearning.QLearning;
import org.deeplearning4j.rl4j.learning.sync.qlearning.discrete.QLearningDiscreteDense;
import org.deeplearning4j.rl4j.mdp.MDP;
import org.deeplearning4j.rl4j.network.dqn.DQNFactoryStdDense;
import org.deeplearning4j.rl4j.policy.DQNPolicy;
import org.deeplearning4j.rl4j.space.ArrayObservationSpace;
import org.deeplearning4j.rl4j.space.DiscreteSpace;
import org.deeplearning4j.rl4j.space.Encodable;
import org.deeplearning4j.rl4j.space.ObservationSpace;
import org.deeplearning4j.rl4j.util.DataManager;
import org.json.JSONObject;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.Adam;

import Observer.Observer;
import RetailMarketManager.RetailMarketManager;
import Configuration.Configuration;

public class DQAgent extends Agent {

    public static QLearning.QLConfiguration QLConfig = new QLearning.QLConfiguration(123, // Random
            // seed
            1000, // Max step By epoch
            15, // Max step
            1000, // Max size of experience replay
            16, // size of batches
            100, // target update (hard)
            0, // num step noop warmup
            0.05, // reward scaling
            0.99, // gamma
            10.0, // td-error clipping
            0.1f, // min epsilon
            200, // num step for eps greedy anneal
            true // double DQN
    );

    public static DQNFactoryStdDense.Configuration QLNet = DQNFactoryStdDense.Configuration.builder().l2(0.00).updater(new Adam(0.001)).numHiddenNodes(16).numLayer(3).build();

    private static final String policyFilename = "dqpolicy.pol";

    public static void log(String message, Object... args) {
        Logger.getAnonymousLogger().info("[Dev]" + String.format(message, args));
    }

    public static void main(String[] args) throws IOException {
        // Train or load DQAgent's previous policy
        File policyFile = new File(policyFilename);
        DQNPolicy<SimpleState> pol = null;

        // Train
        if (!policyFile.exists()) {
            log("Setting up DeepQ training");
            SimpleMDP mdp = new SimpleMDP();
            DataManager manager = new DataManager(true);
            QLearningDiscreteDense<SimpleState> dql = new QLearningDiscreteDense<SimpleState>(mdp, QLNet, QLConfig, manager);

            log("Training DeepQ");
            dql.train();
            pol = dql.getPolicy();

            log("Saving DeepQ");
            pol.save(policyFilename);
        }
        // Load
        else {
            log("Loading up DeepQ policy from file");
        }

        // Test
        log("Running Porag's simulator");
        RetailMarketManager rm = new RetailMarketManager();
        rm.startSimulationV2();
    }

    private DQNPolicy<SimpleState> pol;

    public DQAgent() {
        this.name = "DEEPQ";
        try {
            this.pol = DQNPolicy.load(policyFilename);
        } catch (IOException e) {
            log("{DQAgent.Constructor} Couldn't load DQN policy (so probably training?)");
        }
    }

    @Override
    public void publishTariff(Observer ob) {
        SimpleState state = new SimpleState(this);
        INDArray input = Nd4j.create(state.toArray());
        input = input.reshape(Learning.makeShape(1, SimpleMDP.observationSpace.getShape()));

        int nextAction = pol.nextAction(input);
        if (nextAction == 0) // Defect
            defect(ob);
        else if (nextAction == 1) // Increase
            increase(ob);
        else // No change
            nochange();
    }

}

class SimpleState implements Encodable {
    public DQAgent agent;
    public int timeSlot;
    public long ppts;

    public SimpleState(DQAgent agent) {
        this.agent = agent;

        this.timeSlot = Observer.timeslot;
        this.ppts = (long) (agent.prevprofit / (double) Observer.timeslot - 1);
    }

    @Override
    public double[] toArray() {
        return new double[] { timeSlot, ppts };
    }
}

class SimpleMDP implements MDP<SimpleState, Integer, DiscreteSpace> {
    private static final int NUM_ACTIONS = 3;

    public static DiscreteSpace actionSpace = new DiscreteSpace(NUM_ACTIONS);
    public static ObservationSpace<SimpleState> observationSpace = new ArrayObservationSpace<SimpleState>(new int[] { 2 });

    public SimpleState currentState;
    public SimpleState startingState;

    private RetailMarketManager retailManager;
    private DQAgent agent;

    public SimpleMDP() {
        retailManager = new RetailMarketManager();

        agent = new DQAgent();
        Agent opponentAgent = new AlwaysDefect();

        retailManager.ob.agentPool.add(agent);
        retailManager.ob.agentPool.add(opponentAgent);

        Observer.timeslot = 0;
        this.currentState = this.startingState = new SimpleState(agent);
    }

    @Override
    public void close() {

    }

    @Override
    public DiscreteSpace getActionSpace() {
        return actionSpace;
    }

    @Override
    public ObservationSpace<SimpleState> getObservationSpace() {
        return observationSpace;
    }

    @Override
    public boolean isDone() {
        return Observer.timeslot >= Configuration.TOTAL_TIME_SLOTS;
    }

    @Override
    public MDP<SimpleState, Integer, DiscreteSpace> newInstance() {
        SimpleMDP mdp = new SimpleMDP();
        return mdp;
    }

    @Override
    public SimpleState reset() {
        return startingState;
    }

    @Override
    public StepReply<SimpleState> step(Integer action) {
        if (action == 0) // Increase
            agent.increase(retailManager.ob);
        else if (action == 1) // Decrease
            agent.defect(retailManager.ob);
        else // No Change
            agent.nochange();

        retailManager.updateAgentsMemory();

        // Run the market evaluation based on the previous action
        // then run the rest of the timeslots so the next call to
        // this function will be a publication cycle
        for (int i = 0; i < Configuration.PUBLICATION_CYCLE + 1; i++) {
            retailManager.customerTariffEvaluation();
            retailManager.updateAgentAccountings();
            Observer.timeslot++;
        }

        double reward = agent.profit - agent.prevprofit;
        SimpleState nextState = new SimpleState(agent);

        JSONObject info = new JSONObject("{}");
        return new StepReply<SimpleState>(nextState, reward, this.isDone(), info);
    }

}