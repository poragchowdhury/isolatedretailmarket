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
import org.deeplearning4j.rl4j.learning.async.a3c.discrete.A3CDiscrete.A3CConfiguration;
import org.deeplearning4j.rl4j.learning.async.a3c.discrete.A3CDiscrete;
import org.deeplearning4j.rl4j.learning.async.a3c.discrete.A3CDiscreteDense;
import org.deeplearning4j.rl4j.network.ac.ActorCriticFactorySeparateStdDense;
import org.deeplearning4j.rl4j.network.dqn.DQNFactoryStdDense;
import org.deeplearning4j.rl4j.policy.ACPolicy;
import org.deeplearning4j.rl4j.policy.DQNPolicy;
import org.deeplearning4j.rl4j.policy.Policy;
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
            .maxEpochStep(Configuration.TOTAL_PUBLICATIONS_IN_A_GAME) // 6
            .maxStep(Configuration.TOTAL_PUBLICATIONS_IN_A_GAME * Configuration.TRAINING_ROUNDS) // 500
            .expRepMaxSize(Configuration.TOTAL_PUBLICATIONS_IN_A_GAME*100)//*((int)(Configuration.TRAINING_ROUNDS*0.2))) // 10000
            .batchSize(Configuration.TOTAL_PUBLICATIONS_IN_A_GAME*10)//((int)(Configuration.TRAINING_ROUNDS*0.1))) // 64
            .targetDqnUpdateFreq(Configuration.TOTAL_PUBLICATIONS_IN_A_GAME)// * Configuration.TRAINING_ROUNDS) // 50
            .updateStart(0) // 0
            .rewardFactor(1)
            .gamma(0.9) // 0.99
            .errorClamp(Double.MAX_VALUE)
            .minEpsilon(0.1f) // 0.1f
            .epsilonNbStep((Configuration.TOTAL_PUBLICATIONS_IN_A_GAME*(Configuration.TRAINING_ROUNDS))) // 3000
            .doubleDQN(true).build();
    
    public static A3CConfiguration QLConfig2 = A3CConfiguration.builder().
    		seed(123).
    		maxEpochStep(168/6).
    		maxStep(168/6).build();

    private static A3CDiscrete.A3CConfiguration A3C =
            new A3CDiscrete.A3CConfiguration(
                    123,            //Random seed
                    (int)Configuration.TOTAL_TIME_SLOTS / Configuration.PUBLICATION_CYCLE, // 6
                    (int)(Configuration.TOTAL_TIME_SLOTS / Configuration.PUBLICATION_CYCLE) * Configuration.TRAINING_ROUNDS, // 500
                    16,              //Number of threads
                    5,              //t_max
                    0,             //num step noop warmup
                    0.01,           //reward scaling
                    0.99,           //gamma
                    10.0           //td-error clipping
            );
    public static AsyncNStepQLearningDiscrete.AsyncNStepQLConfiguration ASYNC_QLConfig = new AsyncNStepQLearningDiscrete.AsyncNStepQLConfiguration(
            123, // Random seed
            (Configuration.TOTAL_TIME_SLOTS / Configuration.PUBLICATION_CYCLE), // Max step By epoch
            ((Configuration.TOTAL_TIME_SLOTS / Configuration.PUBLICATION_CYCLE) * Configuration.TRAINING_ROUNDS), // Max step
            8, // Number of threads
            5, // t_max
            (Configuration.TOTAL_TIME_SLOTS / Configuration.PUBLICATION_CYCLE), // target update (hard)
            0, // num step noop warmup
            0.9, // reward scaling
            0.1, // gamma
            0.0, // td-error clipping
            0.5f, // min epsilon
            ((Configuration.TOTAL_TIME_SLOTS / Configuration.PUBLICATION_CYCLE) * Configuration.TRAINING_ROUNDS) / 2 // num step for eps greedy anneal
    );

    public static DQNFactoryStdDense.Configuration QLNet = DQNFactoryStdDense.Configuration.builder()
            .l2(0.001)
            .updater(new Adam(0.0005))
            .numHiddenNodes(25)
            .numLayer(3).build();

    public static ActorCriticFactorySeparateStdDense.Configuration QLNet2 = ActorCriticFactorySeparateStdDense.Configuration.builder()
            .l2(0.0001)
            .updater(new Adam(0.0005))
            .numHiddenNodes(16)
            .numLayer(3)
            .build();

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

            QLearningDiscreteDense<DQAgentState> dql = new QLearningDiscreteDense<DQAgentState>(mdp, QLNet, QLConfig, manager);
            A3CDiscreteDense<DQAgentState> dqc = new A3CDiscreteDense<>(mdp, QLNet2, A3C, manager);
            log("QLCONFIG MAXSTEP" + QLConfig.getMaxStep());

            // define the training
            log("Training DeepQ");
            dql.train();
            DQNPolicy<DQAgentState> pol = dql.getPolicy();

            // dqc.train();
            // ACPolicy<DQAgentState> pol = dqc.getPolicy();

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

    public double comReward = 0;
    public int rep = 0;

    @Override
    public StepReply<DQAgentState> step(Integer actionInt) {
        TariffAction action = TariffAction.valueOf(actionInt);
        double before = agent.profit;
        agent.playAction(retailManager.ob, action);

        // log("TS %s: Agent Market %s, Opp Market %s", retailManager.ob.timeslot, agent.marketShare, opponentPool.get(0).marketShare);
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
        double reward = (after - before)/(0.5*Configuration.POPULATION*7);
        // double reward = agent.profit - agent.prevprofit;
        // double reward = agent.profit - opponentPool.get(0).profit;

        // log("AFTER: Agent Market %s, Opp Market %s, Action %s, Reward %s", agent.marketShare, opponentPool.get(0).marketShare, action, reward);
        DQAgentState nextState = new DQAgentState(agent, retailManager.ob);

        JSONObject info = new JSONObject("{}");
        return new StepReply<DQAgentState>(nextState, reward, this.isDone(), info);
    }

}