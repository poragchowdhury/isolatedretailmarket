package BranchWork;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.rl4j.network.dqn.DQN;
import org.deeplearning4j.rl4j.policy.DQNPolicy;
import Agents.Agent;
import Agents.DQAgent;
import Agents.DQAgentMDP;
import Agents.DQAgentState;
import Agents.NeuralNet;
import BranchWork.Plot.Data;
import Configuration.CaseStudy;
import RetailMarketManager.RetailMarketManager;

public class SelfPlay {
    private static final String AGENT_FILENAME = "dq_selfplay.pol";

    private static int improvementCount = 0;
    private static RetailMarketManager rm = new RetailMarketManager();
    private static List<Agent> trainingOpponentPool = new ArrayList<>();

    private static Plot plot;
    private static Data data;
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
        Drawing.displayImage("selfplay.png");
    }

    @SuppressWarnings("rawtypes")
    public static void createInitialModel() throws IOException {
        // Prepare random weight network agent
        MultiLayerNetwork mdp = NeuralNet.createMultiLayerNetwork();
        DQN model = new DQN(mdp);
        new DQNPolicy<DQAgentState>(model).save(AGENT_FILENAME);
    }

    public static boolean improveModel() throws IOException {
        // Load previous model
        DQAgent selfPlayPrevious = new DQAgent(AGENT_FILENAME);

        // Train a new DQAgent against it
        trainingOpponentPool.clear();
        trainingOpponentPool.add(selfPlayPrevious);
        DQAgentMDP.trainDQAgent(trainingOpponentPool, AGENT_FILENAME, null);

        // Load new agent
        DQAgent selfPlayNext = new DQAgent(AGENT_FILENAME);
        improvementCount++;

        // Evaluate
        return evaluate(selfPlayPrevious, selfPlayNext);
    }

    public static boolean evaluate(DQAgent selfPlayPrevious, DQAgent selfPlayNext) throws IOException {
        selfPlayPrevious.reset();
        selfPlayNext.reset();
        rm.startSimulation(new CaseStudy().addP1Strats(selfPlayPrevious).addP2Strats(selfPlayNext));
        results += "***** Iteration " + improvementCount + "\n";
        results += String.format("\tDQSelfPlay%s Profit = %.3f\n", improvementCount - 1, selfPlayPrevious.profit);
        results += String.format("\tDQSelfPlay%s Profit = %.3f\n", improvementCount, selfPlayNext.profit);

        data.xy(improvementCount, selfPlayNext.profit);

        // show progress
        print(results);
        plot.series("SelfPlaySeries", data, Plot.seriesOpts());
        plot.save("selfplay", "png");
        Drawing.displayImage("selfplay.png");

        boolean stopEarly = false;
        double diff = Math.abs(selfPlayPrevious.profit - selfPlayNext.profit);
        if (diff < 0.001)
            stopEarly = true;

        return stopEarly;
    }
}
