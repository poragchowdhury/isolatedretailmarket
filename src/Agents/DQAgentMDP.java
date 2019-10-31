/**
 * Author: Jose G. Perez
 * Deep Q-Learning MDP for use with deeplearning4j to train
 */
package Agents;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import org.deeplearning4j.gym.StepReply;
import org.deeplearning4j.rl4j.learning.async.nstep.discrete.AsyncNStepQLearningDiscrete;
import org.deeplearning4j.rl4j.learning.async.nstep.discrete.AsyncNStepQLearningDiscreteDense;
import org.deeplearning4j.rl4j.learning.sync.qlearning.QLearning;
import org.deeplearning4j.rl4j.learning.sync.qlearning.discrete.QLearningDiscreteDense;
import org.deeplearning4j.rl4j.mdp.MDP;
import org.deeplearning4j.rl4j.mdp.toy.SimpleToyState;
import org.deeplearning4j.rl4j.network.dqn.DQNFactoryStdDense;
import org.deeplearning4j.rl4j.policy.DQNPolicy;
import org.deeplearning4j.rl4j.space.ArrayObservationSpace;
import org.deeplearning4j.rl4j.space.DiscreteSpace;
import org.deeplearning4j.rl4j.space.ObservationSpace;
import org.deeplearning4j.rl4j.util.DataManager;
import org.json.JSONObject;
import org.nd4j.linalg.learning.config.Adam;

import Configuration.Configuration;
import RetailMarketManager.RetailMarketManager;
import Tariff.TariffAction;

public class DQAgentMDP implements MDP<DQAgentState, Integer, DiscreteSpace> {
    public static QLearning.QLConfiguration QLConfig = QLearning.QLConfiguration.builder()
            .seed(123)
            .maxEpochStep(Configuration.TOTAL_TIME_SLOTS / Configuration.PUBLICATION_CYCLE) // 6
            .maxStep((Configuration.TOTAL_TIME_SLOTS / Configuration.PUBLICATION_CYCLE) * Configuration.TRAINING_ROUNDS) // 500
            .expRepMaxSize(4) // 10000 
            .batchSize(1) // 64
            .targetDqnUpdateFreq((Configuration.TOTAL_TIME_SLOTS / Configuration.PUBLICATION_CYCLE)) // 50
            .updateStart(0) // 0
            .rewardFactor(0.9) // 10
            .gamma(0.1) // 0.99
            .errorClamp(Double.MAX_VALUE)
            .minEpsilon(1.0f) // 0.1f
            .epsilonNbStep((Configuration.TOTAL_TIME_SLOTS / Configuration.PUBLICATION_CYCLE) * Configuration.TRAINING_ROUNDS) // 3000
            .doubleDQN(true).build();

    public static AsyncNStepQLearningDiscrete.AsyncNStepQLConfiguration ASYNC_QLConfig =
            new AsyncNStepQLearningDiscrete.AsyncNStepQLConfiguration(
                    123,        //Random seed
                    (Configuration.TOTAL_TIME_SLOTS / Configuration.PUBLICATION_CYCLE),     //Max step By epoch
                    ((Configuration.TOTAL_TIME_SLOTS / Configuration.PUBLICATION_CYCLE) * Configuration.TRAINING_ROUNDS),      //Max step
                    8,          //Number of threads
                    5,          //t_max
                    (Configuration.TOTAL_TIME_SLOTS / Configuration.PUBLICATION_CYCLE),        //target update (hard)
                    0,          //num step noop warmup
                    0.9,        //reward scaling
                    0.1,       //gamma
                    0.0,       //td-error clipping
                    0.5f,       //min epsilon
                    ((Configuration.TOTAL_TIME_SLOTS / Configuration.PUBLICATION_CYCLE) * Configuration.TRAINING_ROUNDS)/2        //num step for eps greedy anneal
            );
    
    
    public static DQNFactoryStdDense.Configuration QLNet = DQNFactoryStdDense.Configuration.builder()
            .l2(0.001)
            .updater(new Adam(0.0005))
            .numHiddenNodes(16)
            .numLayer(3).build();

    public static final int NUM_ACTIONS = 3;
    public static final int NUM_OBSERVATIONS = new DQAgentState().toArray().length;

    public static DiscreteSpace ACTION_SPACE = new DiscreteSpace(NUM_ACTIONS);
    public static ObservationSpace<DQAgentState> OBSERVATION_SPACE = new ArrayObservationSpace<DQAgentState>(new int[] { NUM_OBSERVATIONS });

    public static void log(String message, Object... args) {
        Logger.getAnonymousLogger().info("[SimpleMDP]" + String.format(message, args));
    }

    private RetailMarketManager retailManager;
    private DQAgent agent;
    private List<Agent> opponentPool;

    public DQAgentMDP(List<Agent> opponentPool) {
        this.opponentPool = opponentPool;
        this.reset();
    }

    public static void trainDQAgent(List<Agent> opponentPool, String policyFilename) {
        log("Setting up DeepQ training");
        DQAgentMDP mdp = new DQAgentMDP(opponentPool);
        try {
            // record the training data in rl4j-data in a new folder
            DataManager manager = new DataManager(true);
            log("QLCONFIG MAXSTEP" + QLConfig.getMaxStep());
            
            QLearningDiscreteDense<DQAgentState> dql = new QLearningDiscreteDense<DQAgentState>(mdp, QLNet, QLConfig, manager);
            //AsyncNStepQLearningDiscreteDense<DQAgentState> dql = new AsyncNStepQLearningDiscreteDense<DQAgentState>(mdp, QLNet, ASYNC_QLConfig, manager);
            
            //define the training
            log("Training DeepQ");
            dql.train();
            //DQNPolicy<DQAgentState> pol = dql.getPolicy();
            DQNPolicy<DQAgentState> pol = (DQNPolicy<DQAgentState>) dql.getPolicy();
            log("Saving DeepQ policy");
            pol.save(policyFilename);
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
        return ACTION_SPACE;
    }

    @Override
    public ObservationSpace<DQAgentState> getObservationSpace() {
        return OBSERVATION_SPACE;
    }

    @Override
    public boolean isDone() {
        return retailManager.ob.timeslot >= Configuration.TOTAL_TIME_SLOTS;
    }

    @Override
    public MDP<DQAgentState, Integer, DiscreteSpace> newInstance() {
        DQAgentMDP mdp = new DQAgentMDP(this.opponentPool);
        return mdp;
    }

    @Override
    public DQAgentState reset() {
        // Properly reset agent variables between training epochs.
        for (Agent ag : opponentPool)
            ag.reset();

        this.agent = new DQAgent();
        this.retailManager = new RetailMarketManager();

        retailManager.ob.agentPool.add(agent);
        retailManager.ob.agentPool.addAll(opponentPool);
        retailManager.ob.timeslot = 0;

        return new DQAgentState(agent, retailManager.ob);
    }

    @Override
    public StepReply<DQAgentState> step(Integer actionInt) {
        TariffAction action = TariffAction.valueOf(actionInt);
        double before = agent.profit;
        agent.playAction(retailManager.ob, action);

        // Perform other agent policies
        for (Agent ag : this.opponentPool) {
            try {
                ag.publishTariff(retailManager.ob);
            } catch (Exception ex) {
                System.out.printf("[Agent:%s raised an exception while publishing a tariff]\n");
                ex.printStackTrace();
            }
        }

        // Run the market evaluation based on the previous action
        // then run the rest of the timeslots so the next call to
        // this function will be a publication cycle
        for (int i = 0; i < Configuration.PUBLICATION_CYCLE; i++) {
            retailManager.customerTariffEvaluation();
            retailManager.updateAgentAccountings();
            retailManager.ob.timeslot++;
        }
        double after = agent.profit;
        double reward = after - before;
        // double reward = agent.profit - agent.prevprofit;
        // double reward = agent.profit - opponentPool.get(0).profit;

        // log("DQAGENTMDP: Action %s, Reward %s, Agent Market %s, Opp Market %s", action, reward, agent.marketShare, opponentPool.get(0).marketShare);
        DQAgentState nextState = new DQAgentState(agent, retailManager.ob);

        JSONObject info = new JSONObject("{}");
        return new StepReply<DQAgentState>(nextState, reward, this.isDone(), info);
    }

}