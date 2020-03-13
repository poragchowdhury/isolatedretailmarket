package edu.utep.poragchowdhury.experiment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.rl4j.network.dqn.DQN;
import org.deeplearning4j.rl4j.policy.DQNPolicy;

import edu.utep.poragchowdhury.agents.base.Agent;
import edu.utep.poragchowdhury.agents.deepq.DQAgent;
import edu.utep.poragchowdhury.agents.deepq.MDPState;
import edu.utep.poragchowdhury.agents.deepq.NeuralNet;
import edu.utep.poragchowdhury.agents.deepq.Trainer;
import edu.utep.poragchowdhury.core.Plot;
import edu.utep.poragchowdhury.simulation.CaseStudy;
import edu.utep.poragchowdhury.simulation.RetailMarketManager;
import org.jetbrains.annotations.NotNull;

public class SelfPlay {
    private static final String AGENT_FILENAME = "dq_selfplay.pol";

    private static int improvementCount = 0;
    @NotNull
    private static RetailMarketManager rm = new RetailMarketManager();
    @NotNull
    private static List<Agent> trainingOpponentPool = new ArrayList<>();

    private static Plot plot;
    private static Plot.Data data;
    private static String results = "";

    private static void print(String st, Object... args) {
        System.out.printf("*****" + st + "*****\n", args);
    }

    public static void main(String[] args) throws IOException {
        plot = Plot.plot(
                Plot.plotOpts().title("Self Play Results"))
                .xAxis("Iteration", null)
                .yAxis("Profit vs Previous Self Play", null);
        data = Plot.data();

        print("Overwriting selfplay policy with initial default policy");
        createInitialModel();

        // Scanner input = new Scanner(System.in);
        while (true) {
            print("Iteration = %s, Improving Model", improvementCount);
            boolean stopEarly = improveModel();

            // print("> Continue?");
            // String shouldContinue = input.next();
            // if (!shouldContinue.contains("y"))
            // break;

            if (stopEarly)
                break;
        }
        // input.close();
        print("==================== Final Results ====================");
        print(results);

        plot.series("SelfPlaySeries", data, Plot.seriesOpts());
        plot.save("selfplay", "png");
        // Drawing.displayImage("selfplay.png");
    }

    public static void createInitialModel() throws IOException {
        // Prepare random weight network agent
        MultiLayerNetwork mdp = NeuralNet.createMultiLayerNetwork();
        DQN model = new DQN(mdp);
        new DQNPolicy<MDPState>(model).save(AGENT_FILENAME);
    }

    public static boolean improveModel() throws IOException {
        // Load previous model
        DQAgent selfPlayPrevious = new DQAgent(AGENT_FILENAME);

        // Train a new DQAgent against it, then load it
        trainingOpponentPool.clear();
        trainingOpponentPool.add(selfPlayPrevious);
        DQAgent selfPlayNext = Trainer.train(trainingOpponentPool, AGENT_FILENAME);
        improvementCount++;

        // Evaluate
        return evaluate(selfPlayPrevious, selfPlayNext);
    }

    public static boolean evaluate(@NotNull DQAgent selfPlayPrevious, @NotNull DQAgent selfPlayNext) throws IOException {
        selfPlayPrevious.reset();
        selfPlayNext.reset();
        CaseStudy cs = new CaseStudy();
        cs.addP1Strats(selfPlayPrevious);
        cs.addP2Strats(selfPlayNext);
        rm.startSimulation(cs);

        results += "***** Iteration " + improvementCount + "\n";
        results += String.format("\tDQSelfPlay%s Profit = %.3f\n", improvementCount - 1, selfPlayPrevious.profit);
        results += String.format("\tDQSelfPlay%s Profit = %.3f\n", improvementCount, selfPlayNext.profit);

        data.xy(improvementCount, selfPlayNext.profit);

        // Show progress
        print(results);
        plot.series("SelfPlaySeries", data, Plot.seriesOpts());
        plot.save("selfplay", "png");

        boolean stopEarly = false;
        double diff = Math.abs(selfPlayPrevious.profit - selfPlayNext.profit);
        if (diff < 0.01)
            stopEarly = true;

        return stopEarly;
    }
}
