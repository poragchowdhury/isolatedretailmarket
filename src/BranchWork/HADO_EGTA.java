package BranchWork;

import Agents.*;
import Configuration.CaseStudy;
import Configuration.Configuration;
import RetailMarketManager.RetailMarketManager;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * History Aware Double Oracle (HADO)
 * Empirical Game Theoretic Analysis (EGTA)
 *
 * @author Jose G. Perez
 */
public class HADO_EGTA {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");

    private static Logger log = Logger.getLogger("retailmarketmanager");
    private static RetailMarketManager rm = new RetailMarketManager();

    private static int K = 3;
    private static double gamma = 0.7;
    private static double alpha = 1;

    /**
     * The last RL agent added to the pools. Required to check if the current pure
     * strategy is just playing the last RL agent created
     */
    private static Agent lastRLAgent;

    private static void setupLogging(String logFilename) throws IOException {
        // %1 = Date, %2 = Source, %3 = Logger, %4 = Level, %5 = Message, &6 = Thrown
        // %1$tF = Date -> Y-m-d
        // %1$tT = Date -> 24 hour format
        // %4$s = Log Type (Info, ...)
        // %2$s = Class and Method Call
        // %5$s%6$s = Message
        // {%1$tT} %2$s %5$s%6$s
        System.setProperty("java.util.logging.SimpleFormatter.format", "%5$s%6$s" + "\n");
        FileHandler fh = new FileHandler(logFilename, true);
        SimpleFormatter formatter = new SimpleFormatter();

        fh.setFormatter(formatter);
        log.addHandler(fh);
    }

    public static void main(String[] args) throws Exception {
        // Preparation
        String dateString = DATE_FORMAT.format(new Date());
        log.info("Beginning experiment on " + dateString);
        log.info(Configuration.toStringRepresentation());
        setupLogging("hado_egta_" + dateString + ".log");

        Stack<Agent> litStrategies = getLiteratureStrategies();
        List<Agent> strategiesToRemove = new ArrayList<>();
        List<SMNE> smneList = new ArrayList<>();

        // Fig 1. Flowchart describing process
        long startTime = System.currentTimeMillis();
        CaseStudy currentCase = initializeStrategySets();

        int iterations = 0;
        while (true) {
            iterations++;
            log.info("****** Iteration: " + iterations + " *****");

            // ************** Check if we need to remove strategies from last round
            if (!strategiesToRemove.isEmpty()) {
                for (Agent stratToRemove : strategiesToRemove) {
                    log.info("Removing " + stratToRemove + " as determined by the last iteration");
                    currentCase.pool1.remove(stratToRemove);
                    currentCase.pool2.remove(stratToRemove);
                }
                strategiesToRemove.clear();
            }

            // ************** Simulate Payoffs
            log.info("Pool1 Agents: " + currentCase.pool1.toString());
            log.info("Pool2 Agents: " + currentCase.pool2.toString());
            log.info("Starting game round");
            rm.startSimulation(currentCase);

            // ************** Find Nash Equilibrium
            log.info("Getting nash equilibrium strategies");
            List<double[]> nashEqInitial = Gambit.getNashEqStrategies(rm, currentCase);
            List<double[]> nashEqPure = Gambit.getPureStrategies(nashEqInitial);
            List<double[]> nashEqMixed = Gambit.getMixedStrategies(nashEqInitial);

            // ************** Strategies with zeros on columns from both pools will be removed in the next iteration
            for (int col = 0; col < nashEqInitial.get(0).length; col++) {
                boolean allZero = true;
                for (double[] nashEq : nashEqInitial) {
                    double currNumber = nashEq[col];
                    if (currNumber != 0.0)
                        allZero = false;
                }

                if (allZero) {
                    Agent agentToRemove = currentCase.pool1.get(col);
                    log.info(agentToRemove + " will be removed next iteration from the pools");
                    strategiesToRemove.add(agentToRemove);
                }
            }

            // ************** Select the strategy for SMNE (either mixed or pure)
            double[] smneProbs;
            if (!nashEqMixed.isEmpty()) {
                int idx = Utilities.argMinZeros(nashEqMixed);
                smneProbs = nashEqMixed.get(idx);
                log.info("SMNE is picking mixed strategy: " + Arrays.toString(smneProbs));
            } else {
                // ************** Check if one of the pure strategies is the latest RL strategy
                if (isRLPureStrat(currentCase, nashEqPure)) {
                    log.info("RL Pure Strategy is the ==NashEq ==, we need to add a stronger strategy to pool");

                    // ************** Add a strategy from the literature or exit if we have finished
                    if (litStrategies.empty()) {
                        log.info("There are no more strategies in the stack, exiting loop");
                        break;
                    } else {
                        Agent newStrat = litStrategies.pop();
                        log.info("Adding " + newStrat + " to the pool");
                        currentCase.addP1Strats(newStrat).addP2Strats(newStrat);
                        continue;
                    }
                }
                smneProbs = nashEqPure.get(0);
                log.info("SMNE is picking a pure strategy, specifically, the first one");
            }
            // ************** (Pre-Train) Learn best response against SMNE using DeepQ Agent
            SMNE smne = new SMNE();
            for (int idx = 0; idx < currentCase.pool1.size(); idx++) {
                double prob = smneProbs[idx];
                Agent strat = currentCase.pool1.get(idx);
                smne.addStrategy(prob, strat);
            }

            List<Agent> opponentPool = new ArrayList<>();
            opponentPool.add(smne);
            String dqFilename = Configuration.DQ_TRAINING + "_" + smne.name + ".pol";
            DQAgentMDP.trainDQAgent(opponentPool, dqFilename, null);

            log.info("Training DQ Agent");
            DQAgent dqAgent = new DQAgent(dqFilename);

            // ************** (Fine-Tune)

            // ************** Run test games of SMNE vs RL
            log.info("Running SMNE vs DQAgent");
            CaseStudy testGame = new CaseStudy().addP1Strats(smne).addP2Strats(dqAgent);
            rm.startSimulation(testGame);

            // ************** Does RL have a higher payoff than SMNE?
            log.info("SMNE Profit: " + smne.profit + ", DQAgent profit: " + dqAgent.profit);
            if (dqAgent.profit > smne.profit) {
                log.info("DQAgent profit > SMNE profit, adding DQAgent to the pool");
                log.info("New DQAgent Name: " + dqAgent.name);
                if (Configuration.RUN_ONE_ITERATION)
                    break;

                currentCase.addP1Strats(dqAgent).addP2Strats(dqAgent);
                lastRLAgent = dqAgent;

                // Check if we have too many DQAgents, if so, add an agent from the literature
                int dqAgentCount = 0;
                for (Agent agent : currentCase.pool1) {
                    if (agent instanceof DQAgent)
                        dqAgentCount++;
                }

                if (dqAgentCount >= Configuration.MAX_DQ_AGENTS_ALLOWED) {
                    log.info("We have too many DQAgents, specifically, we have " + dqAgentCount);
                    log.info("Attempting to add a strategy from the literature");
                    if (litStrategies.empty()) {
                        log.info("There are no more strategies in the stack, exiting loop");
                        break;
                    } else {
                        Agent newStrat = litStrategies.pop();
                        log.info("Adding " + newStrat + " to the pool");
                        currentCase.addP1Strats(newStrat).addP2Strats(newStrat);
                        continue;
                    }
                }
            } else {
                log.info("DQAgent could not beat SMNE.");
                break;
            }
        }

        long endTime = System.currentTimeMillis();
        long durationMillis = endTime - startTime;
        double durationSec = durationMillis / 1000;
        log.info(String.format("Finished! Took %.2f ", durationSec));
    }

    public static Stack<Agent> getLiteratureStrategies() {
        Stack<Agent> literatureStrategies = new Stack<>();
        TitForTat TFT = new TitForTat(1, 1);
        TitForTat TF2T = new TitForTat(1, 2);
        TitForTat _2TFT = new TitForTat(2, 1);

        literatureStrategies.add(TF2T);
        literatureStrategies.add(_2TFT);
        literatureStrategies.add(TFT);
        literatureStrategies.add(new Prober());
        literatureStrategies.add(new Grim());
        literatureStrategies.add(new NaiveProber());
        literatureStrategies.add(new HardMajority());
        literatureStrategies.add(new Pavlov());
        literatureStrategies.add(new AlwaysSame());
        literatureStrategies.add(new SoftMajority());
        literatureStrategies.add(new ZI());
        literatureStrategies.add(new AlwaysIncrease());
        literatureStrategies.add(new ZIP());
        literatureStrategies.add(new GD());

        return literatureStrategies;
    }

    private static CaseStudy initializeStrategySets() {
        CaseStudy cs = new CaseStudy();
        cs.addP1Strats();
        cs.addP2Strats();

        return cs;
    }

    /**
     * Determines if there is a pure strategy where the RL agent is dominating (zero
     * in all other columns)
     */
    public static boolean isRLPureStrat(CaseStudy cs, List<double[]> pureStrats) {
        int lastRLIndex = cs.pool1.indexOf(lastRLAgent);
        for (double[] pureStrat : pureStrats) {
            if (pureStrat[lastRLIndex] == 1.0d) {
                return true;
            }
        }
        return false;
    }
}