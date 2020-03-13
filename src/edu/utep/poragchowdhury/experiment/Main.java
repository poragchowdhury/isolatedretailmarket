package edu.utep.poragchowdhury.experiment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import edu.utep.poragchowdhury.agents.AlwaysIncrease;
import edu.utep.poragchowdhury.agents.AlwaysSame;
import edu.utep.poragchowdhury.agents.Rand;
import edu.utep.poragchowdhury.agents.base.Agent;
import edu.utep.poragchowdhury.agents.deepq.DQAgent;
import edu.utep.poragchowdhury.agents.deepq.RetailMDP;
import edu.utep.poragchowdhury.agents.deepq.Trainer;
import edu.utep.poragchowdhury.core.Configuration;
import edu.utep.poragchowdhury.core.Logging;
import edu.utep.poragchowdhury.simulation.CaseStudy;
import edu.utep.poragchowdhury.simulation.RetailMarketManager;

public class Main {
    private static Logger log = Logger.getLogger("retailmarketmanager");

    public static void main(String[] args) throws IOException {
        Configuration.loadConfiguration();
        Logging.setupFormat();
        Logging.attachLoggerToFile(log, "experiment.log");
        log.info(Configuration.toStringRepresentation());
        log.info("Feature Size: " + RetailMDP.NUM_OBSERVATIONS);

        // Experiment that will be performed by this run
        sandboxExperiment();
    }

    /**
     * The Sandbox Experiment tests DQAgent against a few others
     * We can use this experiment to make sure DQAgent is being trained correctly or to tweak hyperparameters
     */
    public static void sandboxExperiment() {
        RetailMarketManager rm = new RetailMarketManager();

        // Opponent
        Agent opponentAgent = new AlwaysSame();

        // Training opponents
        List<Agent> oppPool = new ArrayList<>();
        oppPool.add(opponentAgent);

        // DQAgent Training
        DQAgent dqAgent = Trainer.train(oppPool, "sandbox.pol");

        // Testing
        opponentAgent.reset();
        CaseStudy testingStudy = new CaseStudy();
        testingStudy.addP1Strats(opponentAgent);
        testingStudy.addP2Strats(dqAgent);

        rm.startSimulation(testingStudy);
        log.info(String.format("[Opponent: %s -> Profit: %.3f] | [DQAgent -> Profit %.3f]", opponentAgent.name, opponentAgent.profit, dqAgent.profit));
        log.info(String.format("Feature Size: %s, Training Rounds %s, Test Rounds %s", RetailMDP.NUM_OBSERVATIONS, Configuration.TRAINING_ROUNDS, Configuration.TEST_ROUNDS));
        log.info(String.format("Defect %s, NoChange %s, Increase %s", DQAgent.DEFECT, DQAgent.NOC, DQAgent.INC));
    }

    public static void roundRobinExperiment() throws IOException, InterruptedException {
        RetailMarketManager rm = new RetailMarketManager();
        rm.startExperiment(true);
    }

    /**
     * The Main Experiment runs the flowchart specified by Porag
     * Basically, the SMNE vs DQAgent stuff with Gambit and such
     * @throws IOException
     * @throws InterruptedException
     */
    public static void mainExperiment() throws IOException, InterruptedException {
        RetailMarketManager rm = new RetailMarketManager();
        rm.startExperiment(false);
    }

    /**
     * The SelfPlay experiment trains a DQAgent named "selfplay.pol" by having it
     * play against itself
     * @throws IOException
     */
    public static void selfPlayExperiment() throws IOException {
        SelfPlay.main(null);
    }
}
