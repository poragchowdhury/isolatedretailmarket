package edu.utep.poragchowdhury.agents.deepq;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.deeplearning4j.api.storage.StatsStorage;
import org.deeplearning4j.nn.api.NeuralNetwork;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.rl4j.learning.async.a3c.discrete.A3CDiscreteDense;
import org.deeplearning4j.rl4j.learning.sync.qlearning.discrete.QLearningDiscreteDense;
import org.deeplearning4j.rl4j.network.dqn.DQN;
import org.deeplearning4j.rl4j.policy.ACPolicy;
import org.deeplearning4j.rl4j.policy.DQNPolicy;
import org.deeplearning4j.rl4j.util.DataManager;
import org.deeplearning4j.ui.api.UIServer;
import org.deeplearning4j.ui.stats.StatsListener;
import org.deeplearning4j.ui.storage.InMemoryStatsStorage;

import edu.utep.poragchowdhury.agents.base.Agent;
import edu.utep.poragchowdhury.core.Configuration;
import edu.utep.poragchowdhury.core.Utilities;

public class Trainer {
    private static Logger log = Logger.getLogger("retailmarketmanager");
    public static boolean isTraining = false;

    @SuppressWarnings("rawtypes")
    public static DQAgent train(List<Agent> opponentPool, String policyFilename) {
        // Set-up mdp and network
        RetailMDP mdp = new RetailMDP(opponentPool);
        MultiLayerNetwork model = NeuralNet.createMultiLayerNetwork();

        try {
            // Record the training data in rl4j-data in a new folder
            DataManager manager = new DataManager(true);

            if (Configuration.USE_ACTOR_CRITIC) {
                // A3CDiscreteDense<MDPState> dqc = new A3CDiscreteDense<>(mdp,
                // NeuralNet.A3C_NET_FACTORY_CONFIG, QLearningConfig.ACTOR_CRITIC, manager);
                //
                // isTraining = true;
                // dqc.train();
                // isTraining = false;
                //
                // ACPolicy<MDPState> pol = dqc.getPolicy();
                // pol.save(policyFilename);
                // return new DQAgent(pol);
                return null;
            } else {
                // Perform learning and save result
                // DQN dqn = new DQN(model);
                // QLearningDiscreteDense<MDPState> dql = new QLearningDiscreteDense<MDPState>(mdp, dqn, QLearningConfig.REGULAR, manager);
                QLearningDiscreteDense<MDPState> dql = new QLearningDiscreteDense<MDPState>(mdp, NeuralNet.REGULAR_NET_FACTORY_CONFIG, QLearningConfig.REGULAR, manager);

                // int startPort = 8080;
                // for (NeuralNetwork net : dql.getNeuralNet().getNeuralNetworks()) {
                // log.info("Net: " + net.toString());
                // log.info("Net Class: " + net.getClass());
                // System.setProperty("org.deeplearning4j.ui.port", "" + startPort);
                //
                // UIServer uiServer = UIServer.getInstance();
                // StatsStorage statsStorage = new InMemoryStatsStorage();
                // uiServer.attach(statsStorage);
                //
                // StatsListener statsListener = new StatsListener(statsStorage);
                // ((MultiLayerNetwork) net).addListeners(statsListener);
                // startPort++;
                // }
                // Set-up my own training listener for plots and such
                // NeuralNetListener netListener = new NeuralNetListener();
                // dql.addListener(netListener);

                isTraining = true;
                dql.train();
                isTraining = false;

                // Summary
                Map<String, Stat> originalMap = mdp.statisticsMap;
                Map<String, Stat> sortedMap = Utilities.sortByValues(originalMap);
                for (String key : sortedMap.keySet()) {
                    Stat s = mdp.statisticsMap.get(key);
                    log.info(String.format("%s -> Average Reward (Per Game) %.3f", key, s.calculateV()));
                }

                DQNPolicy<MDPState> pol = dql.getPolicy();
                pol.save(policyFilename);
                return new DQAgent(pol);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
