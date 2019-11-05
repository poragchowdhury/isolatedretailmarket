/**
 * Author: Jose G. Perez
 * Deep Q-Learning MDP for use with deeplearning4j to train
 */
package Agents;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.deeplearning4j.api.storage.StatsStorage;
import org.deeplearning4j.gym.StepReply;
import org.deeplearning4j.nn.api.Model;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.DenseLayer;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.nn.weights.WeightInit;
import org.deeplearning4j.optimize.api.BaseTrainingListener;
import org.deeplearning4j.optimize.listeners.EvaluativeListener;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.deeplearning4j.rl4j.learning.async.nstep.discrete.AsyncNStepQLearningDiscrete;
import org.deeplearning4j.rl4j.learning.async.nstep.discrete.AsyncNStepQLearningDiscreteDense;
import org.deeplearning4j.rl4j.learning.sync.qlearning.QLearning;
import org.deeplearning4j.rl4j.learning.sync.qlearning.discrete.QLearningDiscreteDense;
import org.deeplearning4j.rl4j.mdp.MDP;
import org.deeplearning4j.rl4j.learning.async.a3c.discrete.A3CDiscrete.A3CConfiguration;
import org.deeplearning4j.rl4j.learning.Learning;
import org.deeplearning4j.rl4j.learning.async.a3c.discrete.A3CDiscrete;
import org.deeplearning4j.rl4j.learning.async.a3c.discrete.A3CDiscreteDense;
import org.deeplearning4j.rl4j.network.ac.ActorCriticFactorySeparateStdDense;
import org.deeplearning4j.rl4j.network.dqn.DQN;
import org.deeplearning4j.rl4j.network.dqn.DQNFactoryStdDense;
import org.deeplearning4j.rl4j.policy.ACPolicy;
import org.deeplearning4j.rl4j.policy.DQNPolicy;
import org.deeplearning4j.rl4j.policy.Policy;
import org.deeplearning4j.rl4j.space.ArrayObservationSpace;
import org.deeplearning4j.rl4j.space.DiscreteSpace;
import org.deeplearning4j.rl4j.space.ObservationSpace;
import org.deeplearning4j.rl4j.util.Constants;
import org.deeplearning4j.rl4j.util.DataManager;
import org.deeplearning4j.ui.api.UIServer;
import org.deeplearning4j.ui.stats.StatsListener;
import org.deeplearning4j.ui.storage.InMemoryStatsStorage;
import org.json.JSONObject;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.learning.config.Adam;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import Agents.Plot.Data;
import Configuration.CaseStudy;
import Configuration.Configuration;
import RetailMarketManager.RetailMarketManager;
import Tariff.TariffAction;

class MDPListener extends BaseTrainingListener {
    public QLearningDiscreteDense<DQAgentState> dql;
    public int prevEpoch = -1;
    public List<Double> profits;
    public double bestProfit = -1;
    public MultiLayerNetwork bestMDP = null;

    public MDPListener() {
        profits = new ArrayList<>();
    }

    @Override
    public void onEpochStart(Model model) {

    }

    @Override
    public void onEpochEnd(Model model) {
    }

    @Override
    public void onForwardPass(Model model, List<INDArray> activations) {
    }

    @Override
    public void onForwardPass(Model model, Map<String, INDArray> activations) {
    }

    @Override
    public void onGradientCalculation(Model model) {
    }

    @Override
    public void onBackwardPass(Model model) {
    }

    @Override
    public void iterationDone(Model model, int iteration, int epochINVALID) {
        if (dql != null) {
            int epoch = dql.getEpochCounter();
            int step = dql.getStepCounter();

            if (epoch != prevEpoch) {
                prevEpoch = epoch;

                if (epoch % 10 == 0) {
                    Logger.getAnonymousLogger().info("===Evaluating model");
                    MultiLayerNetwork mdp = (MultiLayerNetwork) model;
                    RetailMarketManager rm = new RetailMarketManager();
                    Agent dq = new DQAgent(mdp);
                    Agent opponent = new AlwaysDefect();
                    rm.startSimulation(new CaseStudy().addP1Strats(opponent).addP2Strats(dq));
                    Logger.getAnonymousLogger().info("=== Opp : " + opponent.profit + ", DQ : " + dq.profit);
                    profits.add(dq.profit);

                    if (dq.profit > bestProfit) {
                        bestProfit = dq.profit;
                        bestMDP = mdp;
                    }
                }
            }
            // Logger.getAnonymousLogger().info("On Iteration: " + iteration + ", Epoch " + epoch + ", Step " + step);
        }
    }

}

public class DQAgentMDP implements MDP<DQAgentState, Integer, DiscreteSpace> {
    public static QLearning.QLConfiguration QLConfig = QLearning.QLConfiguration.builder()
            .seed(123)
            .maxEpochStep(Configuration.TOTAL_PUBLICATIONS_IN_A_GAME) // 6
            .maxStep(Configuration.TOTAL_PUBLICATIONS_IN_A_GAME * Configuration.TRAINING_ROUNDS) // 500
            .expRepMaxSize(Configuration.TOTAL_PUBLICATIONS_IN_A_GAME * 100)// *((int)(Configuration.TRAINING_ROUNDS*0.2))) // 10000
            .batchSize(Configuration.TOTAL_PUBLICATIONS_IN_A_GAME * 10)// ((int)(Configuration.TRAINING_ROUNDS*0.1))) // 64
            .targetDqnUpdateFreq(Configuration.TOTAL_PUBLICATIONS_IN_A_GAME*Configuration.TRAINING_ROUNDS/10)// * Configuration.TRAINING_ROUNDS) // 50
            .updateStart(0) // 0
            .rewardFactor(1)
            .gamma(0.9) // 0.99
            .errorClamp(Double.MAX_VALUE)
            .minEpsilon(0.1f) // 0.1f
            .epsilonNbStep((Configuration.TOTAL_PUBLICATIONS_IN_A_GAME * (Configuration.TRAINING_ROUNDS))) // 3000
            .doubleDQN(true).build();

    public static A3CConfiguration QLConfig2 = A3CConfiguration.builder().seed(123).maxEpochStep(168 / 6).maxStep(168 / 6).build();

    private static A3CDiscrete.A3CConfiguration A3C = new A3CDiscrete.A3CConfiguration(
            123, // Random seed
            (int) Configuration.TOTAL_TIME_SLOTS / Configuration.PUBLICATION_CYCLE, // 6
            (int) (Configuration.TOTAL_TIME_SLOTS / Configuration.PUBLICATION_CYCLE) * Configuration.TRAINING_ROUNDS, // 500
            16, // Number of threads
            5, // t_max
            0, // num step noop warmup
            0.01, // reward scaling
            0.99, // gamma
            10.0 // td-error clipping
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
        Logger.getAnonymousLogger().info(String.format(message, args));
    }

    private RetailMarketManager retailManager;
    private DQAgent agent;
    private List<Agent> opponentPool;

    public DQAgentMDP(List<Agent> opponentPool) {
        this.opponentPool = opponentPool;
        this.reset();
    }

    public static MultiLayerNetwork getDQN(int[] numInputs, int numOutputs) {
        DQNFactoryStdDense.Configuration conf = QLNet;
        int nIn = 1;
        for (int i : numInputs) {
            nIn *= i;
        }
        NeuralNetConfiguration.ListBuilder confB = new NeuralNetConfiguration.Builder().seed(Constants.NEURAL_NET_SEED)
                .optimizationAlgo(OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT)
                // .updater(Updater.NESTEROVS).momentum(0.9)
                // .updater(Updater.RMSPROP).rho(conf.getRmsDecay())//.rmsDecay(conf.getRmsDecay())
                .updater(conf.getUpdater() != null ? conf.getUpdater() : new Adam())
                .weightInit(WeightInit.XAVIER)
                .l2(conf.getL2())
                .list().layer(0, new DenseLayer.Builder().nIn(nIn).nOut(conf.getNumHiddenNodes())
                        .activation(Activation.RELU).build());

        for (int i = 1; i < conf.getNumLayer(); i++) {
            confB.layer(i, new DenseLayer.Builder().nIn(conf.getNumHiddenNodes()).nOut(conf.getNumHiddenNodes())
                    .activation(Activation.RELU).build());
        }

        confB.layer(conf.getNumLayer(), new OutputLayer.Builder(LossFunctions.LossFunction.MSE).activation(Activation.IDENTITY)
                .nIn(conf.getNumHiddenNodes()).nOut(numOutputs).build());

        MultiLayerConfiguration mlnconf = confB.build();
        MultiLayerNetwork model = new MultiLayerNetwork(mlnconf);
        model.init();
        return model;
    }

    public static DQAgent trainDQAgent(List<Agent> opponentPool, String policyFilename) {
        log("Setting up DeepQ training");
        DQAgentMDP mdp = new DQAgentMDP(opponentPool);
        try {
            // record the training data in rl4j-data in a new folder
            DataManager manager = new DataManager(true);

            // Set-up Network
            MultiLayerNetwork model = getDQN(OBSERVATION_SPACE.getShape(), ACTION_SPACE.getSize());

            // Set-up UI stuff
            // System.setProperty("org.deeplearning4j.ui.port", "80");
            // UIServer uiServer = UIServer.getInstance();
            // StatsStorage statsStorage = new InMemoryStatsStorage();
            // uiServer.attach(statsStorage);

            MDPListener l1 = new MDPListener();
            ScoreIterationListener l2 = new ScoreIterationListener(10);
            // StatsListener l3 = new StatsListener(statsStorage);
            model.setListeners(l1);

            // Set-up DeepQ Learning
            QLearningDiscreteDense<DQAgentState> dql = new QLearningDiscreteDense<DQAgentState>(mdp, new DQN(model), QLConfig, manager);
            A3CDiscreteDense<DQAgentState> dqc = new A3CDiscreteDense<>(mdp, QLNet2, A3C, manager);

            l1.dql = dql;

            log("Training DeepQ");
            dql.train();
            DQNPolicy<DQAgentState> pol = dql.getPolicy();

            // dqc.train();
            // ACPolicy<DQAgentState> pol = dqc.getPolicy();

            log("Saving DeepQ policy");
            pol.save(policyFilename);
            Plot plot = Plot.plot(null);
            Data d = Plot.data();
            for (int i = 0; i < l1.profits.size(); i++) {
                double y = l1.profits.get(i);
                d.xy(i, y);
            }
            plot.series("Profits", d, null);
            plot.save("profits" + DQAgent.CURRENT_AGENT_COUNT, "png");
            return new DQAgent(l1.bestMDP);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mdp.close();
        return new DQAgent(policyFilename);
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
        int t = retailManager.ob.timeslot;
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
        double reward = (after - before) / (0.5 * Configuration.POPULATION * 7);
        // double reward = agent.profit - agent.prevprofit;
        // double reward = agent.profit - opponentPool.get(0).profit;

        // log("TS %s, AFTER: Agent Market %s, Opp Market %s, Action %s, Reward %s", t, agent.marketShare, opponentPool.get(0).marketShare, action, reward);
        DQAgentState nextState = new DQAgentState(agent, retailManager.ob);

        JSONObject info = new JSONObject("{}");
        return new StepReply<DQAgentState>(nextState, reward, this.isDone(), info);
    }

}
