package edu.utep.poragchowdhury.agents.deepq;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.deeplearning4j.nn.api.Model;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.optimize.api.BaseTrainingListener;
import org.deeplearning4j.rl4j.learning.sync.qlearning.discrete.QLearningDiscreteDense;
import org.nd4j.linalg.api.ndarray.INDArray;

import edu.utep.poragchowdhury.agents.AlwaysDefect;
import edu.utep.poragchowdhury.agents.base.Agent;
import edu.utep.poragchowdhury.simulation.CaseStudy;
import edu.utep.poragchowdhury.simulation.RetailMarketManager;

/**
 * Listener for MultiLayerNetwork training
 */
public class NeuralNetListener extends BaseTrainingListener {
    private static Logger log = Logger.getLogger("retailmarketmanager");

    public QLearningDiscreteDense<MDPState> dql;
    public int prevEpoch = -1;
    public List<Double> profits;
    public double bestProfit = -1;
    public MultiLayerNetwork bestMDP = null;

    public NeuralNetListener(QLearningDiscreteDense<MDPState> dql) {
        this.dql = dql;
        profits = new ArrayList<>();
    }

    @Override
    public void onEpochStart(Model model) {
        // Not called by rl4j
    }

    @Override
    public void onEpochEnd(Model model) {
        // Not called by rl4j
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
            // int step = dql.getStepCounter();

            if (epoch != prevEpoch) {
                prevEpoch = epoch;

                if (epoch % 2 == 0) {
                    log.info(" *** Evaluating model *** ");
                    MultiLayerNetwork mdp = (MultiLayerNetwork) model;
                    RetailMarketManager rm = new RetailMarketManager();

                    // Evaluation Setup
                    Agent dq = new DQAgent(mdp);
                    Agent opponent = new AlwaysDefect();

                    CaseStudy cs = new CaseStudy();
                    cs.addP1Strats(opponent);
                    cs.addP2Strats(dq);

                    rm.startSimulation(cs);
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
