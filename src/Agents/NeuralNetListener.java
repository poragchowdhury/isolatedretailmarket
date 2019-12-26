package Agents;

import java.io.IOException;
import java.util.logging.Logger;

import org.deeplearning4j.rl4j.learning.sync.listener.SyncTrainingEpochEndEvent;
import org.deeplearning4j.rl4j.learning.sync.listener.SyncTrainingEvent;
import org.deeplearning4j.rl4j.learning.sync.listener.SyncTrainingListener;
import org.deeplearning4j.rl4j.policy.DQNPolicy;
import org.deeplearning4j.rl4j.policy.Policy;

/**
 * Listener for MultiLayerNetwork training
 */
public class NeuralNetListener implements SyncTrainingListener {
    private static Logger log = Logger.getLogger("retailmarketmanager");


    public NeuralNetListener() {

    }

    @Override
    public ListenerResponse onTrainingStart(SyncTrainingEvent event) {
        return ListenerResponse.CONTINUE;
    }

    @Override
    public void onTrainingEnd() {

    }

    @SuppressWarnings("unchecked")
    @Override
    public ListenerResponse onEpochStart(SyncTrainingEvent event) {
    	/*
        int epoch = event.getLearning().getEpochCounter();
        // Evaluate the epoch
        // to stop the training, return ListenerResponse.STOP
        if (epoch % 100000 == 0) {
            log.info(" *** Evaluating model *** ");
            DQNPolicy<DQAgentState> pol = (DQNPolicy<DQAgentState>)event.getLearning().getPolicy();
            DQAgent dqAgent = new DQAgent(pol);
            Agent opponentAgent = new AlwaysIncrease();
            RetailMarketManager.RetailMarketManager rm = new RetailMarketManager.RetailMarketManager();
            rm.startSimulation(new Configuration.CaseStudy()
            		.addP1Strats(dqAgent)
            		.addP2Strats(opponentAgent)
            		);
            log.info("*** Testing at epoch " + epoch);
            log.info(String.format("\tDQAgent Profit=%.2f, Opponent Name=%s, Profit=%.2f", dqAgent.profit, opponentAgent.name, opponentAgent.profit));
        }
        */
        return ListenerResponse.CONTINUE;
    }

    @Override
    public ListenerResponse onEpochEnd(SyncTrainingEpochEndEvent event) {
        return ListenerResponse.CONTINUE;
    }
}