package edu.utep.poragchowdhury.simulation;

import java.io.IOException;
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
import edu.utep.poragchowdhury.agents.deepq.Trainer;
import edu.utep.poragchowdhury.core.Configuration;
import edu.utep.poragchowdhury.core.Gambit;
import edu.utep.poragchowdhury.core.Utilities;

public class RetailMarketManager {
    private static Logger log = Logger.getLogger("retailmarketmanager");

    public Observer ob;
    public double[] lamdaTracker;
    public static int numberofagents = 3;
    public double largestValue = -1;
    public Payoffs[][] gameMatrix = new Payoffs[20][20];
    public Payoffs[][] bestRespMatrix = new Payoffs[20][20];
    public Payoffs[][] winMatrix = new Payoffs[20][20];

    public Payoffs[][] gameMatrixErr = new Payoffs[20][20];
    public Payoffs[][] bestRespMatrixErr = new Payoffs[20][20];
    public Payoffs[][] winMatrixErr = new Payoffs[20][20];

    /**
     * The last RL agent added to the pools. Required to check if the
     * current pure strategy is just playing the last RL agent created
     */
    private Agent lastRLAgent;

    public RetailMarketManager() {
        ob = new Observer();
        lamdaTracker = new double[7];
    }

    public void publishTariffs() {
        for (Agent a : ob.agentPool) {
            try {
                a.publishTariff(ob);
            } catch (Exception ex) {
                log.info("[Agent:" + a.name + " raised an exception while publishing a tariff] " + ex.getMessage() + "\n");
            }
        }
    }

    public void updateAgentsMemory() {
        // updating opponent tariff price in own history
        ob.agentPool.get(0).rivalTariffHistory[ob.timeslot] = ob.agentPool.get(1).tariffPrice;
        ob.agentPool.get(0).rivalTariffPrice = ob.agentPool.get(1).tariffPrice;
        ob.agentPool.get(1).rivalTariffHistory[ob.timeslot] = ob.agentPool.get(0).tariffPrice;
        ob.agentPool.get(1).rivalTariffPrice = ob.agentPool.get(0).tariffPrice;

        // updating opponent action in the own history
        // compare current tariff price with previous publication price
        ob.agentPool.get(0).rivalActHistory[ob.timeslot] = ob.agentPool.get(1).previousAction.index;
        ob.agentPool.get(0).rivalPreviousAction = ob.agentPool.get(1).previousAction;
        ob.agentPool.get(1).rivalActHistory[ob.timeslot] = ob.agentPool.get(0).previousAction.index;
        ob.agentPool.get(1).rivalPreviousAction = ob.agentPool.get(0).previousAction;

        // updating own tariff history
        ob.agentPool.get(0).tariffHistory[ob.timeslot] = ob.agentPool.get(0).tariffPrice;
        ob.agentPool.get(1).tariffHistory[ob.timeslot] = ob.agentPool.get(1).tariffPrice;

        /* Need to know who to setup this features? Accumulated or individual? */
        // updating own accumulated profit history
        // At the end of the game this profit determines the winner
        ob.agentPool.get(0).profitHistory[ob.timeslot] = ob.agentPool.get(0).profit;
        ob.agentPool.get(1).profitHistory[ob.timeslot] = ob.agentPool.get(1).profit;

        // updating own unitcost history
        ob.agentPool.get(0).unitCostHistory[ob.timeslot] = ob.agentPool.get(0).unitcost;
        ob.agentPool.get(1).unitCostHistory[ob.timeslot] = ob.agentPool.get(1).unitcost;

        // updating own cost history
        ob.agentPool.get(0).costHistory[ob.timeslot] = ob.agentPool.get(0).cost;
        ob.agentPool.get(1).costHistory[ob.timeslot] = ob.agentPool.get(1).cost;

        // updating own market share history
        ob.agentPool.get(0).marketShareHistory[ob.timeslot] = ob.agentPool.get(0).marketShare;
        ob.agentPool.get(1).marketShareHistory[ob.timeslot] = ob.agentPool.get(1).marketShare;

        // updating own action in the own history
        ob.agentPool.get(0).actHistory[ob.timeslot] = ob.agentPool.get(0).previousAction.index;
        ob.agentPool.get(1).actHistory[ob.timeslot] = ob.agentPool.get(1).previousAction.index;
    }

    public void customerTariffEvaluation() {
        ob.fcc.evaluateTariffs();
        ob.fcc.randomWalkNoise(ob.timeslot);
    }

    public void updateAgentAccountings() {
        int hour = ob.timeslot % 24;
        // Reset this observer variables to calculate values for each timeslot
        Arrays.fill(ob.money, 0);
        Arrays.fill(ob.cost, 0);
        Arrays.fill(ob.custSubs, 0);

        // Calculating the money(revenue), cost and subscription for this timeslot
        for (int i = 0; i < ob.fcc.population; i++) {
            ob.money[ob.fcc.custId[i]] += (ob.agentPool.get(ob.fcc.custId[i]).tariffPrice * ob.fcc.usage[hour]);
            ob.cost[ob.fcc.custId[i]] += (ob.agentPool.get(ob.fcc.custId[i]).unitcost
                    * (ob.fcc.usage[hour] + ob.fcc.noise));
            ob.custSubs[ob.fcc.custId[i]] += 1;
        }

        // Updating overall revenue, cost, profit, marketShare for this timelsot
        ob.agentPool.get(0).revenue += ob.money[0];
        ob.agentPool.get(1).revenue += ob.money[1];
        ob.agentPool.get(0).cost += ob.cost[0];
        ob.agentPool.get(1).cost += ob.cost[1];
        ob.agentPool.get(0).profit += (ob.money[0] - ob.cost[0]);
        ob.agentPool.get(1).profit += (ob.money[1] - ob.cost[1]);
        // subscriber count is the market share
        ob.agentPool.get(0).marketShare = (ob.custSubs[0]);
        ob.agentPool.get(1).marketShare = (ob.custSubs[1]);

        // count best responses of the agents
        if ((ob.money[0] - ob.cost[0]) > (ob.money[1] - ob.cost[1]))
            ob.agentPool.get(0).bestResponseCount++;
        else if ((ob.money[0] - ob.cost[0]) < (ob.money[1] - ob.cost[1]))
            ob.agentPool.get(1).bestResponseCount++;
        else {
            ob.agentPool.get(0).bestResponseCount++;
            ob.agentPool.get(1).bestResponseCount++;
        }

        // update Agent's memory
        updateAgentsMemory();
    }

    public void printGameMatrix(CaseStudy cs) {
        // Normalizing the values
        int numberOfAgents = cs.pool1.size();
        double[] avgValues = new double[numberOfAgents];

        log.info("\nTotal Payoffs");
        for (int i = 0; i < numberOfAgents; i++) {
            for (int k = 0; k < numberOfAgents; k++) {
                gameMatrix[i][k].value1 = Math.round(gameMatrix[i][k].value1 / largestValue * 100);
                gameMatrix[i][k].value2 = Math.round(gameMatrix[i][k].value2 / largestValue * 100);
                avgValues[i] += gameMatrix[i][k].value1;
            }
            log.info(cs.pool1.get(i).name + " , " + avgValues[i]);
        }
    }

    public void startExperiment(boolean roundRobin) throws IOException, InterruptedException {
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

            // Update Game Matrix before performing Gambit operations
            for (int i = 0; i < currentCase.pool1.size(); i++) {
                for (int k = 0; k < currentCase.pool2.size(); k++) {
                    gameMatrix[i][k].value1 = Math.round(gameMatrix[i][k].value1 / largestValue * 100);
                    gameMatrix[i][k].value2 = Math.round(gameMatrix[i][k].value2 / largestValue * 100);
                }
            }

            // ************** Compute nash equilibrium strategies
            log.info("Getting nash equilibrium strategies");

            List<double[]> nashEqInitial = Gambit.getNashEqStrategies(this, currentCase);
            List<double[]> nashEqPure = Gambit.getPureStrategies(nashEqInitial);
            List<double[]> nashEqMixed = Gambit.getMixedStrategies(nashEqInitial);

            String nashString = Gambit.nashEqToString(currentCase, nashEqInitial);
            log.info("== Nash Eq Strategies ==");
            log.info(nashString);

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
                    int idx = Utilities.argMinZeros(nashEqMixed);
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

                            cs.pool1.get(iagent).opponentID = cs.pool2.get(kagent).id;
                            cs.pool2.get(kagent).opponentID = cs.pool1.get(iagent).id;

                            for (ob.timeslot = 0; ob.timeslot < Configuration.TOTAL_TIME_SLOTS;) {
                                ob.updateAgentUnitCost();
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
                            }
                            /* One Simulation Ended */
                            int agentid = 0;
                            for (Agent a : ob.agentPool) {
                                ob.agentPayoffs[agentid][round] = a.profit;
                                ob.agentBestResponse[agentid][round] = a.bestResponseCount;
                                agentid++;
                            }

                            if (ob.agentPayoffs[0][round] > ob.agentPayoffs[1][round])
                                ob.agentWins[0]++;
                            else if (ob.agentPayoffs[0][round] < ob.agentPayoffs[1][round])
                                ob.agentWins[1]++;
                            else {
                                ob.agentWins[0]++;
                                ob.agentWins[1]++;
                            }
                            // clear the observer for another simulation set up
                            // ob.printAgentPath();
                            ob.clear();
                        }
                        double[] vals = ob.calcAvg(cs, ob.agentPayoffs);
                        log.info(String.format("%s,%.3f,%.3f,%s,%.3f,%.3f", cs.pool1.get(iagent).name, vals[0], vals[2],
                                cs.pool2.get(kagent).name, vals[1], vals[3]));
                        cs.pool1.get(iagent).profit = vals[0];
                        cs.pool2.get(kagent).profit = vals[1];

                        double[] bestRespVals = ob.calcAvg(cs, ob.agentBestResponse);
                        cs.pool1.get(iagent).bestResponseCount = bestRespVals[0];
                        cs.pool2.get(kagent).bestResponseCount = bestRespVals[1];

                        if (iagent != kagent) {
                            gameMatrix[iagent][kagent] = new Payoffs(vals[0], vals[1]);
                            gameMatrix[kagent][iagent] = new Payoffs(vals[1], vals[0]);
                            gameMatrixErr[iagent][kagent] = new Payoffs(vals[2], vals[3]);
                            gameMatrixErr[kagent][iagent] = new Payoffs(vals[3], vals[2]);

                            bestRespMatrix[iagent][kagent] = new Payoffs(bestRespVals[0], bestRespVals[1]);
                            bestRespMatrix[kagent][iagent] = new Payoffs(bestRespVals[1], bestRespVals[0]);
                            bestRespMatrixErr[iagent][kagent] = new Payoffs(bestRespVals[2], bestRespVals[3]);
                            bestRespMatrixErr[kagent][iagent] = new Payoffs(bestRespVals[3], bestRespVals[2]);

                            winMatrix[iagent][kagent] = new Payoffs(ob.agentWins[0], ob.agentWins[1]);
                            winMatrix[kagent][iagent] = new Payoffs(ob.agentWins[1], ob.agentWins[0]);
                        } else {
                            double avgVal = (vals[0] + vals[1]) / 2.0;
                            gameMatrix[iagent][kagent] = new Payoffs(avgVal, avgVal);
                            double avgValErr = (vals[2] + vals[3]) / 2.0;
                            gameMatrixErr[iagent][kagent] = new Payoffs(avgValErr, avgValErr);

                            double avgBestRspVal = (bestRespVals[0] + bestRespVals[1]) / 2.0;
                            bestRespMatrix[iagent][kagent] = new Payoffs(avgBestRspVal, avgBestRspVal);
                            double avgBestRspValErr = (bestRespVals[2] + bestRespVals[3]) / 2.0;
                            bestRespMatrixErr[iagent][kagent] = new Payoffs(avgBestRspValErr, avgBestRspValErr);

                            double avgWins = (ob.agentWins[0] + ob.agentWins[1]) / 2.0;
                            winMatrix[iagent][kagent] = new Payoffs(avgWins, avgWins);
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
}