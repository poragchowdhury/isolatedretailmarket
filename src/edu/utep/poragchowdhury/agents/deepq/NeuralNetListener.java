package edu.utep.poragchowdhury.agents.deepq;

import java.io.IOException;
import java.util.logging.Logger;

import org.deeplearning4j.rl4j.learning.sync.listener.SyncTrainingEpochEndEvent;
import org.deeplearning4j.rl4j.learning.sync.listener.SyncTrainingEvent;
import org.deeplearning4j.rl4j.learning.sync.listener.SyncTrainingListener;
import org.deeplearning4j.rl4j.policy.Policy;

import edu.utep.poragchowdhury.agents.AlwaysSame;
import edu.utep.poragchowdhury.agents.base.Agent;
import edu.utep.poragchowdhury.core.Drawing;
import edu.utep.poragchowdhury.core.Plot;
import edu.utep.poragchowdhury.core.Plot.Data;
import edu.utep.poragchowdhury.simulation.CaseStudy;
import edu.utep.poragchowdhury.simulation.RetailMarketManager;
import org.jetbrains.annotations.NotNull;

/**
 * Listener for MultiLayerNetwork training
 */
public class NeuralNetListener implements SyncTrainingListener {
    private static Logger log = Logger.getLogger("retailmarketmanager");

    private Plot plot;
    private Data data;
    private Agent opponentAgent;

    public NeuralNetListener() {
        opponentAgent = new AlwaysSame();

        plot = Plot.plot(
                Plot.plotOpts().title("DQAgent"))
                .xAxis("Epoch", null)
                .yAxis("Reward", null);
        data = Plot.data();
        data.xy(0, 0);
    }

    @NotNull
    @Override
    public ListenerResponse onTrainingStart(SyncTrainingEvent event) {
        return ListenerResponse.CONTINUE;
    }

    @Override
    public void onTrainingEnd() {

    }

    @NotNull
    @Override
    public ListenerResponse onEpochStart(@NotNull SyncTrainingEvent event) {
        int epoch = event.getLearning().getEpochCounter();
        if (epoch % 10 == 0) {
            log.info(" *** Evaluating model *** ");
            Policy<MDPState, Integer> pol = event.getLearning().getPolicy();
            RetailMarketManager rm = new RetailMarketManager();

            // Evaluation Setup
            Agent dq = new DQAgent(pol);
            opponentAgent.reset();

            CaseStudy cs = new CaseStudy();
            cs.addP1Strats(opponentAgent);
            cs.addP2Strats(dq);

            Trainer.isTraining = false;
            rm.startSimulation(cs);
            Trainer.isTraining = true;

            data.xy(epoch, dq.profit);

            if (epoch % 5 == 0) {
                plot.series("DQAgent", data, Plot.seriesOpts());
                try {
                    plot.save("profits", "png");
                    Drawing.displayImage("profits.png");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return ListenerResponse.CONTINUE;
    }

    @NotNull
    @Override
    public ListenerResponse onEpochEnd(SyncTrainingEpochEndEvent event) {
        return ListenerResponse.CONTINUE;
    }
}
