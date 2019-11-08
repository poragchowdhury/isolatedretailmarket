package edu.utep.poragchowdhury.simulation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.Stack;
import java.util.logging.Logger;

import edu.utep.poragchowdhury.agents.AlwaysDefect;
import edu.utep.poragchowdhury.agents.AlwaysIncrease;
import edu.utep.poragchowdhury.agents.AlwaysSame;
import edu.utep.poragchowdhury.agents.Grim;
import edu.utep.poragchowdhury.agents.HardMajority;
import edu.utep.poragchowdhury.agents.NaiveProber;
import edu.utep.poragchowdhury.agents.Pavlov;
import edu.utep.poragchowdhury.agents.Prober;
import edu.utep.poragchowdhury.agents.Rand;
import edu.utep.poragchowdhury.agents.SoftMajority;
import edu.utep.poragchowdhury.agents.TitForTat;
import edu.utep.poragchowdhury.agents.base.Agent;
import edu.utep.poragchowdhury.agents.base.SMNE;
import edu.utep.poragchowdhury.agents.deepq.DQAgent;
import edu.utep.poragchowdhury.agents.deepq.RetailMDP;
import edu.utep.poragchowdhury.agents.deepq.Trainer;
import edu.utep.poragchowdhury.core.Configuration;
import edu.utep.poragchowdhury.core.Logging;

class Payoffs {
    public double value1;
    public double value2;

    public Payoffs(double val1, double val2) {
        value1 = val1;
        value2 = val2;
    }

    @Override
    public String toString() {
        return String.format("[%.3f, %.3f]", value1, value2);
    }
}

public class RetailMarketManager {
    private static Logger log = Logger.getLogger("retailmarketmanager");

    public Observer ob;
    public double[] lamdaTracker;
    public static int numberofagents = 3;
    public double largestValue = -1;
    public Payoffs[][] gameMatrix = new Payoffs[15][15];

    /**
     * The last RL agent added to the pools. Required to check if the
     * current pure strategy is just playing the last RL agent created
     */
    private Agent lastRLAgent;

    public RetailMarketManager() {
        ob = new Observer();
        /*
         * Tracking the highest lamda score for 1 payoffs
         * 0 : (T-R)
         * 1 : (T-P)
         * 2 : (T-S)
         * 3 : (R-P)
         * 4 : (R-S)
         * 5 : (P-S)
         * 6 : (2R-T-S)
         */
        lamdaTracker = new double[7];
    }

    public void publishTariffs() {
        for (Agent a : ob.agentPool) {
            try {
                a.publishTariff(ob);
            } catch (Exception ex) {
                System.out.printf("[Agent:%s raised an exception while publishing a tariff]\n", a.name);
                ex.printStackTrace();
            }
        }
        updateAgentsMemory();
    }

    public void updateAgentsMemory() {
        // updating opponent values
        ob.agentPool.get(0).rivalPrevPrevPrice = ob.agentPool.get(0).rivalPrevPrice;
        ob.agentPool.get(1).rivalPrevPrevPrice = ob.agentPool.get(1).rivalPrevPrice;

        ob.agentPool.get(0).rivalPrevPrice = ob.agentPool.get(1).tariffPrice;
        ob.agentPool.get(1).rivalPrevPrice = ob.agentPool.get(0).tariffPrice;

        // updating own values
        ob.agentPool.get(0).prevmarketShare = ob.agentPool.get(0).marketShare;
        ob.agentPool.get(1).prevmarketShare = ob.agentPool.get(1).marketShare;

        ob.agentPool.get(0).prevrevenue = ob.agentPool.get(0).revenue;
        ob.agentPool.get(1).prevrevenue = ob.agentPool.get(1).revenue;

        ob.agentPool.get(0).prevprofit = ob.agentPool.get(0).profit;
        ob.agentPool.get(1).prevprofit = ob.agentPool.get(1).profit;

    }

    public void customerTariffEvaluation() {
        ob.fcc.evaluateTariffs();
    }

    public void updateAgentAccountings() {
        int hour = ob.timeslot % 24;
        Arrays.fill(ob.money, 0);
        Arrays.fill(ob.cost, 0);
        Arrays.fill(ob.custSubs, 0);

        double tariffPrice[] = { ob.agentPool.get(0).prevtariffPrice, ob.agentPool.get(1).prevtariffPrice };

        for (int i = 0; i < ob.fcc.population; i++) {
            ob.money[ob.fcc.custId[i]] += (tariffPrice[ob.fcc.custId[i]] * ob.fcc.usage[hour]);
            ob.cost[ob.fcc.custId[i]] += (ob.agentPool.get(ob.fcc.custId[i]).unitcost * ob.fcc.usage[hour]);
            ob.custSubs[ob.fcc.custId[i]] += 1;
        }
        ob.agentPool.get(0).revenue += ob.money[0];
        ob.agentPool.get(1).revenue += ob.money[1];
        ob.agentPool.get(0).cost += ob.cost[0];
        ob.agentPool.get(1).cost += ob.cost[1];
        ob.agentPool.get(0).profit += (ob.money[0] - ob.cost[0]);
        ob.agentPool.get(1).profit += (ob.money[1] - ob.cost[1]);
        ob.agentPool.get(0).marketShare = (ob.custSubs[0]);
        ob.agentPool.get(1).marketShare = (ob.custSubs[1]);
    }

    public void printGameMatrix(CaseStudy cs) {
        // Normalizing the values
        numberofagents = cs.pool1.size();
        double[] avgValues = new double[numberofagents];

        log.info("\nTotal Payoffs");
        for (int i = 0; i < numberofagents; i++) {
            for (int k = 0; k < numberofagents; k++) {
                gameMatrix[i][k].value1 = Math.round(gameMatrix[i][k].value1 / largestValue * 100);
                gameMatrix[i][k].value2 = Math.round(gameMatrix[i][k].value2 / largestValue * 100);
                avgValues[i] += gameMatrix[i][k].value1;
            }
            log.info(cs.pool1.get(i).name + " , " + avgValues[i]);
        }
    }

    public double distance(double[] eq1, double[] eq2) {
        double dist = 0;
        int commonLength = Math.min(eq1.length, eq2.length);
        for (int i = 0; i < commonLength; i++)
            dist += (eq1[i] - eq2[i]) * (eq1[i] - eq2[i]);

        dist = Math.sqrt(dist);
        return dist;
    }

    public int countZeros(double[] arr) {
        int zeroCount = 0;
        for (double d : arr) {
            if (d == 0.0d)
                zeroCount++;
        }
        return zeroCount;
    }

    public int argMinZeros(List<double[]> lst) {
        int resultIDX = 0;
        int lowestCount = countZeros(lst.get(0));
        for (int i = 1; i < lst.size(); i++) {
            double[] arr = lst.get(i);
            int zeroCount = countZeros(arr);
            if (zeroCount < lowestCount) {
                lowestCount = zeroCount;
                resultIDX = i;
            }
        }
        return resultIDX;
    }

    public void startExperiment(boolean roundRobin) throws IOException {
        Scanner input = new Scanner(System.in);
        log.info("*************** Experimental Run Log ***************");

        Stack<Agent> litStrategies = null;
        CaseStudy currentCase = null;

        if (roundRobin) {
            currentCase = setupRoundRobinStrategies();
        } else {
            litStrategies = getLiteratureStrategies();
            currentCase = setupInitialStrategies();
        }

        List<Agent> strategiesToRemove = new ArrayList<>();
        int iterations = 0;
        while (true) {
            iterations++;
            log.info("****************************** Iteration: " + iterations);
            // ************** Check if we need to remove strategies from last round
            if (strategiesToRemove.size() > 0) {
                for (Agent stratToRemove : strategiesToRemove) {
                    log.info("Removing " + stratToRemove + " as determined by the last iteration");
                    currentCase.pool1.remove(stratToRemove);
                    currentCase.pool2.remove(stratToRemove);
                }
                strategiesToRemove.clear();
            }

            // ************** Run games to populate matrices for
            log.info("Pool1 Agents: " + currentCase.pool1.toString());
            log.info("Pool2 Agents: " + currentCase.pool2.toString());
            log.info("Starting game round");
            startSimulation(currentCase);

            if (roundRobin) {
                printGameMatrix(currentCase);
                break;
            }

            // ************** Compute nash equilibrium strategies
            log.info("Getting nash equilibrium strategies");
            List<double[]> nashEqInitial = computeNashEq(currentCase);
            List<double[]> nashEqPure = getPureStrategies(nashEqInitial);
            List<double[]> nashEqMixed = getMixedStrategies(nashEqInitial);

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
            if (Configuration.MANUAL_NASH_EQ_SELECTION) {
                log.info("[Manual Selection] Please select the desired strategy for SMNE (idx)");
                int selectedIDX = input.nextInt();
                input.nextLine(); // Eat the newline created by pressing enter
                smneProbs = nashEqInitial.get(selectedIDX);
                log.info("[Manual Selection] Selected: " + Arrays.toString(smneProbs));
            } else {
                if (nashEqMixed.size() > 0) {
                    int idx = argMinZeros(nashEqMixed);
                    smneProbs = nashEqMixed.get(idx);
                    log.info("SMNE is picking mixed strategy:" + Arrays.toString(smneProbs));
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
                            currentCase.addP1Strats(newStrat);
                            currentCase.addP2Strats(newStrat.clone());
                            continue;
                        }
                    }
                    smneProbs = nashEqPure.get(0);
                    log.info("SMNE is picking a pure strategy, specifically, the first one");

                }
            }
            // ************** Learn best response against SMNE using DeepQ Agent
            SMNE smne = new SMNE();
            for (int idx = 0; idx < currentCase.pool1.size(); idx++) {
                double prob = smneProbs[idx];
                Agent strat = currentCase.pool1.get(idx);
                smne.addStrategy(prob, strat);
            }

            // Train DQAgent against SMNE
            List<Agent> opponentPool = new ArrayList<>();
            opponentPool.add(smne);

            log.info("Training DQ Agent");
            String dqFilename = Configuration.DQ_TRAINING + "_" + smne.name + ".pol";
            DQAgent dqAgent = Trainer.train(opponentPool, dqFilename);

            // ************** Run test games of SMNE vs RL
            log.info("Running SMNE vs DQAgent");
            CaseStudy testGame = new CaseStudy();
            testGame.addP1Strats(smne);
            testGame.addP2Strats(dqAgent);
            startSimulation(testGame);

            // ************** Does RL have a higher payoff than SMNE?
            log.info(String.format("SMNE Profit: %.3f, DQAgent profit: %.3f", smne.profit, dqAgent.profit));
            if (dqAgent.profit > smne.profit) {
                log.info("DQAgent profit > SMNE profit");

                if (Configuration.RUN_ONE_ITERATION)
                    break;

                log.info("New DQAgent Name: " + dqAgent.name);
                currentCase.addP1Strats(dqAgent);
                currentCase.addP2Strats(dqAgent.clone());
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
                        currentCase.addP1Strats(newStrat);
                        currentCase.addP2Strats(newStrat.clone());
                        continue;
                    }
                }
            } else {
                log.info("DQAgent could not beat SMNE");
                break;
            }
        }

        log.info("*************** End of Experiment ***************");
        input.close();
    }

    private CaseStudy setupRoundRobinStrategies() {
        log.info("=== Setting up round robin pools ===");
        ArrayList<Agent> strats = new ArrayList<>();
        TitForTat TFT = new TitForTat(1, 1);
        TitForTat TF2T = new TitForTat(1, 2);
        TitForTat _2TFT = new TitForTat(2, 1);

        strats.add(new DQAgent("DQ10", "DQ10_0.461TF2T,0.54DQ9.pol"));
        strats.add(new HardMajority());
        strats.add(_2TFT);
        strats.add(TFT);
        strats.add(TF2T);
        strats.add(new Rand());
        strats.add(new Grim());
        strats.add(new SoftMajority());
        strats.add(new AlwaysSame());
        strats.add(new Pavlov());
        strats.add(new Prober());
        strats.add(new NaiveProber());
        strats.add(new AlwaysIncrease());
        strats.add(new AlwaysDefect());

        CaseStudy initial = new CaseStudy();
        initial.addToBothStrats(strats);

        return initial;
    }

    public CaseStudy setupInitialStrategies() {
        log.info("=== Setting up initial pools ===");

        CaseStudy initialStudy = new CaseStudy();
        initialStudy.addP1Strats(new NaiveProber(), new Grim(), new Prober(), new DQAgent("DQ7", "DQ7_0.54DQ6,0.462TF1T.pol"), new DQAgent("DQ8", "DQ8_0.462TF1T,0.54DQ7.pol"), new TitForTat(1, 2), new DQAgent("DQ9", "DQ9_0.54DQ8,0.461TF2T.pol"));
        initialStudy.addP2Strats(new NaiveProber(), new Grim(), new Prober(), new DQAgent("DQ7", "DQ7_0.54DQ6,0.462TF1T.pol"), new DQAgent("DQ8", "DQ8_0.462TF1T,0.54DQ7.pol"), new TitForTat(1, 2), new DQAgent("DQ9", "DQ9_0.54DQ8,0.461TF2T.pol"));
        Configuration.DQ_TRAINING = "DQ9";

        return initialStudy;
    }

    public Stack<Agent> getLiteratureStrategies() {
        Stack<Agent> literatureStrategies = new Stack<>();
        TitForTat TF2T = new TitForTat(1, 2);
        literatureStrategies.add(TF2T);

        // TitForTat TFT = new TitForTat(1, 1);
        // TitForTat _2TFT = new TitForTat(2, 1);
        // literatureStrategies.add(new HardMajority());
        // literatureStrategies.add(TFT);
        // literatureStrategies.add(_2TFT);
        // literatureStrategies.add(TF2T);
        // literatureStrategies.add(new Rand());
        // literatureStrategies.add(new Grim());
        // literatureStrategies.add(new SoftMajority());
        // literatureStrategies.add(new AlwaysSame());
        // literatureStrategies.add(new Pavlov());
        // literatureStrategies.add(new Prober());
        // literatureStrategies.add(new AlwaysDefect());
        return literatureStrategies;
    }

    public void startSimulation(CaseStudy cs) {
        double rationality[] = { Configuration.RATIONALITY };
        double inertia[] = { Configuration.INERTIA };
        double imax = 1;
        double rmax = 1;
        double roundmax = Configuration.TEST_ROUNDS;
        rmax = rationality.length;
        imax = inertia.length;
        roundmax = Configuration.TEST_ROUNDS;
        // Round Robin Tournament Set Up
        for (int iagent = 0; iagent < cs.pool1.size(); iagent++) {
            for (int kagent = iagent; kagent < cs.pool2.size(); kagent++) {
                for (int iindex = 0; iindex < imax; iindex++) {
                    for (int rindex = 0; rindex < rmax; rindex++) {
                        for (int round = 0; round < roundmax; round++) {
                            cs.pool1.get(iagent).reset();
                            cs.pool2.get(kagent).reset();

                            ob.agentPool.add(cs.pool1.get(iagent));
                            ob.agentPool.add(cs.pool2.get(kagent));

                            for (ob.timeslot = 0; ob.timeslot < Configuration.TOTAL_TIME_SLOTS;) {
                                // log() function
                                ob.utility[0] = (double) Math.round(ob.utility[0] * 1000) / 1000;
                                ob.utility[1] = (double) Math.round(ob.utility[1] * 1000) / 1000;

                                ob.money[0] = (double) Math.round(ob.money[0] * 100) / 100;
                                ob.money[1] = (double) Math.round(ob.money[1] * 100) / 100;

                                ob.cost[0] = (double) Math.round(ob.cost[0] * 100) / 100;
                                ob.cost[1] = (double) Math.round(ob.cost[1] * 100) / 100;

                                // ob.unitcost = (double) Math.round(ob.unitcost * 100) / 100;

                                ob.agentPool.get(0).revenue = (double) Math.round(ob.agentPool.get(0).revenue * 100) / 100;
                                ob.agentPool.get(1).revenue = (double) Math.round(ob.agentPool.get(1).revenue * 100) / 100;
                                ob.agentPool.get(0).profit = (double) Math.round(ob.agentPool.get(0).profit * 100) / 100;
                                ob.agentPool.get(1).profit = (double) Math.round(ob.agentPool.get(1).profit * 100) / 100;

                                // Agents taking Actions
                                if (ob.timeslot % Configuration.PUBLICATION_CYCLE == 0) {
                                    publishTariffs();
                                    ob.publication_cycle_count++;
                                }
                                // Customers evaluating tariffs
                                customerTariffEvaluation();
                                // Update agent bank accountings
                                updateAgentAccountings();
                                // Going to next timeslot and updating the cost
                                ob.timeslot++;
                                ob.updateAgentUnitCost();
                            }
                            // printRevenues() function
                            int agentid = 0;
                            for (Agent a : ob.agentPool) {
                                ob.agentPayoffs[agentid][round] = a.profit;
                                agentid++;
                            }
                            // clear the observer for another simulation set up
                            ob.clear();
                        }
                        // printAverageRevenues() function

                        double[] vals = ob.calcAvg(cs);

                        // Porag added this to his branch
                        cs.pool1.get(iagent).profit = vals[0];
                        cs.pool2.get(kagent).profit = vals[1];

                        // log.info(cs.pool1.get(iagent).name + " " + vals[0] + " " + cs.pool2.get(kagent).name + " " + vals[1] + " Error1 " + vals[2] + " Error2 " + vals[3]);
                        if (iagent != kagent) {
                            gameMatrix[iagent][kagent] = new Payoffs(vals[0], vals[1]);
                            gameMatrix[kagent][iagent] = new Payoffs(vals[1], vals[0]);
                        } else {
                            double avgVal = (vals[0] + vals[1]) / 2;
                            gameMatrix[iagent][kagent] = new Payoffs(avgVal, avgVal);
                        }

                        if (largestValue < vals[0])
                            largestValue = vals[0];
                        if (largestValue < vals[1])
                            largestValue = vals[1];
                        ob.allsampleclear();
                    }
                }
            }
        }
    }

    public List<double[]> getPureStrategies(List<double[]> nashStrats) {
        List<double[]> result = new ArrayList<>();
        for (double[] strat : nashStrats) {
            boolean isPure = true;
            // If all the numbers are integers, then it's a pure strategy
            for (double n : strat) {
                boolean isInteger = (n % 1 == 0);
                if (!isInteger) {
                    isPure = false;
                    break;
                }
            }
            if (isPure)
                result.add(strat);
        }
        return result;
    }

    public List<double[]> getMixedStrategies(List<double[]> nashStrats) {
        List<double[]> result = new ArrayList<>();
        for (double[] strat : nashStrats) {
            boolean isPure = true;
            // If all the numbers are integers, then it's a pure strategy
            for (double n : strat) {
                boolean isInteger = (n % 1 == 0);
                if (!isInteger) {
                    isPure = false;
                    break;
                }
            }
            if (!isPure)
                result.add(strat);
        }
        return result;
    }

    /**
     * Determines if there is a pure strategy where the RL agent is dominating
     * (zero in all other columns)
     */
    public boolean isRLPureStrat(CaseStudy cs, List<double[]> pureStrats) {
        int lastRLIndex = cs.pool1.indexOf(lastRLAgent);
        for (double[] pureStrat : pureStrats) {
            if (pureStrat[lastRLIndex] == 1.0d) {
                return true;
            }
        }
        return false;
    }

    public List<double[]> computeNashEq(CaseStudy cs) {
        List<double[]> nashEqStrategies = new ArrayList<>();
        try {
            log.info("Computing game matrix");
            for (int i = 0; i < cs.pool1.size(); i++) {
                for (int k = 0; k < cs.pool2.size(); k++) {
                    gameMatrix[i][k].value1 = Math.round(gameMatrix[i][k].value1 / largestValue * 100);
                    gameMatrix[i][k].value2 = Math.round(gameMatrix[i][k].value2 / largestValue * 100);
                }
            }

            // Create gambit file
            log.info("Creating Gambit file");
            FileWriter fw = new FileWriter("Gambit.nfg");
            PrintWriter pw = new PrintWriter(new BufferedWriter(fw));
            pw.println("NFG 1 R \"IPD NFG\" { \"Player 1\" \"Player 2\" } " + "{ " + cs.pool1.size() + " " + cs.pool2.size() + " }");
            for (int k = 0; k < cs.pool1.size(); k++)
                for (int i = 0; i < cs.pool2.size(); i++)
                    pw.print(gameMatrix[i][k].value1 + " " + gameMatrix[i][k].value2 + " ");
            pw.close();
            fw.close();

            log.info("Sending file to command-line tool");
            ProcessBuilder pb = new ProcessBuilder("gambit-enummixed", "Gambit.nfg", "-q");
            pb.redirectErrorStream(true);
            Process process = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;

            log.info("Reading from command-line output");
            while ((line = reader.readLine()) != null) {
                String[] nashEqRaw = line.split(",");
                // String nashEqName = nashEqRaw[0];

                double[] nashEqStrategy = new double[cs.pool1.size()];
                for (int i = 1; i <= cs.pool1.size(); i++) {
                    double n = parseFractionString(nashEqRaw[i]);
                    nashEqStrategy[i - 1] = n;
                }

                nashEqStrategies.add(nashEqStrategy);
            }
            process.waitFor();
            log.info("== Nash Eq Strategies ==");
            String header = "";
            for (Agent agent : cs.pool1) {
                if (agent instanceof DQAgent)
                    header += ((DQAgent) agent).getSimpleName() + ", ";
                else
                    header += agent.name + ", ";
            }
            header = header.substring(0, header.length() - 2);

            log.info(header);
            int strategyIDX = 0;
            for (double[] nashEq : nashEqStrategies) {
                String output = strategyIDX + " [";
                for (double d : nashEq)
                    output += String.format("%.3f, ", d);
                output = output.substring(0, output.length() - 2) + "]";
                log.info(output);
                strategyIDX++;
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return nashEqStrategies;
    }

    /**
     * Converts a string of the form a/b (fraction) into a double
     * @param fString String to convert
     * @return double representing a/b
     */
    public double parseFractionString(String fString) {
        try {
            // Try to parse it regularly first, it might not be a fraction after all
            return Double.parseDouble(fString);
        } catch (NumberFormatException ex) {
            String[] split = fString.split("/");
            double numerator = Double.parseDouble(split[0]);
            double denominator = Double.parseDouble(split[1]);
            return numerator / denominator;
        }
    }

    public static void sandboxExperiment() throws IOException {
        RetailMarketManager rm = new RetailMarketManager();

        // Opponent
        Agent opponentAgent = new AlwaysIncrease();

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

    public static void roundRobinExperiment() throws IOException {
        RetailMarketManager rm = new RetailMarketManager();
        rm.startExperiment(true);
    }

    public static void mainExperiment() throws IOException {
        RetailMarketManager rm = new RetailMarketManager();
        rm.startExperiment(false);
    }

    public static void main(String[] args) throws IOException {
        Logging.setupFormat();
        Logging.attachLoggerToFile(log, "experiment.log");
        log.info(Configuration.toStringRepresentation());
        log.info("Feature Size: " + RetailMDP.NUM_OBSERVATIONS);
        /*
         * The Sandbox Experiment tests DQAgent against a few others
         * We can use this experiment to make sure DQAgent is being trained correctly
         * Or to tweak hyperparameters
         */
        sandboxExperiment();

        /*
         * The round robin experiment
         * 
         */
        // roundRobinExperiment();

        /*
         * The Main Experiment runs the flowchart specified by Porag
         * Basically, the SMNE vs DQAgent stuff with Gambit and such
         */
        // mainExperiment();
    }

}
