package edu.utep.poragchowdhury.agents.deepq;

import java.io.IOException;
import java.util.List;

import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.rl4j.learning.async.a3c.discrete.A3CDiscreteDense;
import org.deeplearning4j.rl4j.learning.sync.qlearning.discrete.QLearningDiscreteDense;
import org.deeplearning4j.rl4j.network.dqn.DQN;
import org.deeplearning4j.rl4j.util.DataManager;

import edu.utep.poragchowdhury.agents.base.Agent;
import edu.utep.poragchowdhury.core.Configuration;
import edu.utep.poragchowdhury.core.Plot;
import edu.utep.poragchowdhury.core.Plot.Data;

public class Trainer {
    public static boolean isTraining = false;

    public static DQAgent train(List<Agent> opponentPool, String policyFilename) {
        // Set-up mdp and network
        RetailMDP mdp = new RetailMDP(opponentPool);
        MultiLayerNetwork model = NeuralNet.createMultiLayerNetwork();

        try {
            // Record the training data in rl4j-data in a new folder
            DataManager manager = new DataManager(true);

            if (Configuration.USE_ACTOR_CRITIC) {
                A3CDiscreteDense<MDPState> dqc = new A3CDiscreteDense<>(mdp, 
                		NeuralNet.A3C_NET_FACTORY_CONFIG, QLearningConfig.ACTOR_CRITIC, manager);

                isTraining = true;
                dqc.train();
                isTraining = false;

                dqc.getPolicy().save(policyFilename);
            } else {
                // Perform learning and save result
                QLearningDiscreteDense<MDPState> dql = new QLearningDiscreteDense<MDPState>(mdp, new DQN(model), QLearningConfig.REGULAR, manager);

                // Set-up my own training listener for plots and such
                NeuralNetListener netListener = new NeuralNetListener(dql);
                model.addListeners(netListener);

                isTraining = true;
                dql.train();
                isTraining = false;

                dql.getPolicy().save(policyFilename);

                // Plotting
                Plot plot = Plot.plot(null);
                Data d = Plot.data();
                for (int i = 0; i < netListener.profits.size(); i++) {
                    double y = netListener.profits.get(i);
                    d.xy(i, y);
                }
                plot.series("Profits", d, null);
                plot.save("profits" + DQAgent.CURRENT_AGENT_COUNT, "png");

            }

            mdp.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return new DQAgent(policyFilename);
    }
}
