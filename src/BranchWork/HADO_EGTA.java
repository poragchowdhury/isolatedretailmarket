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
        setupLogging("hado_egta_" + dateString + ".log");

        log.info("Beginning experiment on " + dateString);
        log.info(Configuration.toStringRepresentation());

        Stack<Agent> litStrategies = getLiteratureStrategies();
        List<Agent> strategiesToRemove = new ArrayList<>();
        List<SMNE> smneList = new ArrayList<>();

        // Fig 1. Flowchart describing process
        long startTime = System.currentTimeMillis();
        CaseStudy currentCase = initializeStrategySets();

        int t = -1;
        while (true) {
            t++;
            log.info("****** Iteration: " + t + " *****");

            // ************** Check if we need to remove strategies from last round
            removeStrategies(strategiesToRemove, currentCase);

            // ************** Simulate Payoffs
            simulatePayoffs(currentCase);

            // ************** Find Nash Equilibrium
            log.info("Getting nash equilibrium strategies");
            List<double[]> nashEqInitial = Gambit.getNashEqStrategies(rm, currentCase);
            List<double[]> nashEqPure = Gambit.getPureStrategies(nashEqInitial);
            List<double[]> nashEqMixed = Gambit.getMixedStrategies(nashEqInitial);

            // ************** Strategies with zeros on columns from both pools will be removed in the next iteration
            findStrategiesToRemove(strategiesToRemove, currentCase, nashEqInitial);

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
                log.info("SMNE is picking a pure strategy, specifically, the first one.");
            }
            // ************************************************************************************
            // ************** (Pre-Train) Learn best response against SMNE using DeepQ Agent
            // ************************************************************************************
            // Sigma: σ (SMNEs)
            // Delta: δ (DQAgents)
            // For time t = current iteration
            SMNE sigma_0 = new SMNE();
            for (int idx = 0; idx < currentCase.pool1.size(); idx++) {
                double prob = smneProbs[idx];
                Agent strat = currentCase.pool1.get(idx);
                sigma_0.addStrategy(prob, strat);
            }
            log.info("SMNE: " + sigma_0.name);

            // ************************************************************************************
            // ************** (Fine-Tune)
            // ************************************************************************************
            List<DQAgent> deltas = new ArrayList<>();

            // ** Prepare delta0_t (Pre-Trained against current equilibrium SMNE)
            DQAgent delta_0 = DQAgentMDP.trainDQAgentNoSaving(null, sigma_0);
            deltas.add(delta_0);

            // ** Prepare σ-bar (Using gamma)
            SMNE sigma_bar = new SMNE();

            for (int psi = 0; psi < (t - 1); psi++) {
                SMNE sigma_psi = smneList.get(psi);
                double g = Math.pow(gamma, t - 1 - psi);
                sigma_bar.addStrategy(g, sigma_psi);
            }

            DQAgent delta_kminus1 = delta_0;
            for (int z = 0; z < K; z++) {
                // Pre-Train with delta(k-1, t)
                DQAgent delta_k = DQAgentMDP.trainDQAgentNoSaving(delta_kminus1, sigma_bar);
                deltas.add(delta_k);
                delta_kminus1 = delta_k;
            }

            // Pick best DQAgent out of the deltas
            DQAgent bestDQAgent = null;
            double bestUtility = Double.MIN_VALUE;
            for (DQAgent dq : deltas) {
                // Play against current equilibrium to find utility
                rm.startSimulation(new CaseStudy().addP1Strats(dq).addP2Strats(sigma_0));
                double uSigma0 = dq.profit;

                // Play against sigma-bar?
                rm.startSimulation(new CaseStudy().addP1Strats(dq).addP2Strats(sigma_bar));
                double uSigmaBar = dq.profit;

                // Compute utility using formula in algorithm 6
                double utility = (alpha * uSigma0) + ((1 - alpha) * uSigmaBar);

                // arg-max logic
                if (utility > bestUtility) {
                    bestUtility = utility;
                    bestDQAgent = dq;
                }
            }

            // Save best DQAgent
            String dqFilename = Configuration.DQ_TRAINING + "_" + sigma_0.name + ".pol";
            bestDQAgent.pol.save(dqFilename);

            // Add SMNE to list of previous for future use
            smneList.add(sigma_0);

            // ************** Run test games of SMNE vs RL
            evaluateDQAgent(sigma_0, bestDQAgent);

            // ************** Does RL have a higher payoff than SMNE?
            if (bestDQAgent.profit > sigma_0.profit) {
                log.info("DQAgent profit > SMNE profit, adding DQAgent to the pool");
                log.info("New DQAgent Name: " + bestDQAgent.name);
                if (Configuration.RUN_ONE_ITERATION)
                    break;

                currentCase.addP1Strats(bestDQAgent).addP2Strats(bestDQAgent);
                lastRLAgent = bestDQAgent;

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

    private static void evaluateDQAgent(SMNE sigma_0, DQAgent dqAgent) {
        log.info("Running SMNE vs DQAgent");
        CaseStudy testGame = new CaseStudy().addP1Strats(sigma_0).addP2Strats(dqAgent);
        rm.startSimulation(testGame);

        log.info("SMNE Profit: " + sigma_0.profit + ", DQAgent profit: " + dqAgent.profit);
        log.info("DQ0 [0] Act " + dqAgent.actHistory[0] + " Action: " + dqAgent.getAllHistoryActions());
        log.info("Opp [0] Act " + sigma_0.actHistory[0] + " Action: " + sigma_0.getAllHistoryActions());
        log.info("DQ0 [0] Trf " + dqAgent.tariffHistory[0] + " TrfHis: " + dqAgent.getHistoryByPubCyc(dqAgent.tariffHistory));
        log.info("Opp [0] Trf " + sigma_0.tariffHistory[0] + " TrfHis: " + sigma_0.getHistoryByPubCyc(sigma_0.tariffHistory));
        log.info("DQ0 [0] Mkt " + dqAgent.marketShareHistory[0] + " MktHis: " + dqAgent.getHistoryByPubCyc(dqAgent.marketShareHistory));
        log.info("Opp [0] Mkt " + sigma_0.marketShareHistory[0] + " MktHis: " + sigma_0.getHistoryByPubCyc(sigma_0.marketShareHistory));
        log.info("DQ0 [0] Prf " + dqAgent.profitHistory[0] + " PftHis: " + dqAgent.getHistoryByPubCyc(dqAgent.profitHistory));
        log.info("Opp [0] Prf " + sigma_0.profitHistory[0] + " PftHis: " + sigma_0.getHistoryByPubCyc(sigma_0.profitHistory));
    }

    private static void findStrategiesToRemove(List<Agent> strategiesToRemove, CaseStudy currentCase, List<double[]> nashEqInitial) {
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
    }

    private static void simulatePayoffs(CaseStudy currentCase) {
        log.info("Pool1 Agents: " + currentCase.pool1.toString());
        log.info("Pool2 Agents: " + currentCase.pool2.toString());
        log.info("Starting game round");
        rm.startSimulation(currentCase);
    }

    private static void removeStrategies(List<Agent> strategiesToRemove, CaseStudy currentCase) {
        if (!strategiesToRemove.isEmpty()) {
            for (Agent stratToRemove : strategiesToRemove) {
                log.info("Removing " + stratToRemove + " as determined by the last iteration");
                currentCase.pool1.remove(stratToRemove);
                currentCase.pool2.remove(stratToRemove);
            }
            strategiesToRemove.clear();
        }
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
        cs.addP1Strats(new AlwaysDefect(), new AlwaysIncrease());
        cs.addP2Strats(new AlwaysDefect(), new AlwaysIncrease());

        return cs;
    }

    /**
     * Determines if there is a pure strategy where the RL agent is dominating (zero
     * in all other columns)
     */
    public static boolean isRLPureStrat(CaseStudy cs, List<double[]> pureStrats) {
        if (lastRLAgent == null)
            return false;

        int lastRLIndex = cs.pool1.indexOf(lastRLAgent);
        for (double[] pureStrat : pureStrats) {
            if (pureStrat[lastRLIndex] == 1.0d) {
                return true;
            }
        }
        return false;
    }
}