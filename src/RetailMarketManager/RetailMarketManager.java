package RetailMarketManager;

import Agents.*;
import BranchWork.AgentComparators;
import BranchWork.Gambit;
import BranchWork.Utilities;
import Configuration.CaseStudy;
import Configuration.Configuration;
import Observer.Observer;

import java.io.IOException;
import java.util.*;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

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

    public RetailMarketManager() {
        ob = new Observer();
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
    }

    public void updateAgentsMemory() {
        // updating opponent traiff price in own history
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
        ob.agentPool.get(0).unitCostHistory[ob.timeslot] = ob.unitcost;
        ob.agentPool.get(1).unitCostHistory[ob.timeslot] = ob.unitcost;

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
            double tariffPrice = ob.agentPool.get(ob.fcc.custId[i]).tariffPrice;
            tariffPrice = tariffPrice > Configuration.MAX_TARIFF_PRICE ? Configuration.MAX_TARIFF_PRICE : tariffPrice;
            ob.money[ob.fcc.custId[i]] += (tariffPrice * ob.fcc.usage[hour]);
            ob.cost[ob.fcc.custId[i]] += (ob.unitcost
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

    public void printClusterValues(CaseStudy cs, boolean runVsItself) {
        numberofagents = cs.pool1.size();
        StringBuilder sb_names = new StringBuilder();
        StringBuilder sb_scores = new StringBuilder();
        log.info(String.format("Print Clustering values\n"));
        for (int i = 0; i < numberofagents; i++) {
            double selfplayScore = gameMatrix[i][i].value1; // selfplay value
            double randplayScore = gameMatrix[i][0].value1; // against random agent
            sb_scores.append(String.format("[%.3f,%.3f],", selfplayScore, randplayScore));
            sb_names.append("'" + cs.pool1.get(i).name + "',");
        }
        String scoreFeatures = sb_scores.toString();
        String agentNames = sb_names.toString();

        log.info(String.format("X = np.array([%s])", scoreFeatures.substring(0, scoreFeatures.length() - 1)));
        log.info(String.format("words = [%s]", agentNames.substring(0, agentNames.length() - 1)));
    }

    public void logGameMatrix(CaseStudy cs) {
        log.info("\n*********CSV Start: game matrix**********");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < cs.pool2.size(); i++)
            sb.append(String.format("%s,", cs.pool2.get(i).name));
        log.info(sb.toString());

        for (int i = 0; i < cs.pool1.size(); i++) {
            sb = new StringBuilder(cs.pool1.get(i).name + ",");
            for (int k = 0; k < cs.pool2.size(); k++)
                sb.append(String.format("%.3f,", gameMatrix[i][k].value1));
            log.info(sb.toString());
        }
        log.info("*********CSV End: game matrix**********");
    }

    public void printGameMatrix(CaseStudy cs, boolean runVsItself) {
        // Normalizing the values
        numberofagents = cs.pool1.size();
        double[] avgValues = new double[numberofagents];
        double[] avgBestResponse = new double[numberofagents];
        double[] avgValuesErr = new double[numberofagents];
        double[] avgBestResponseErr = new double[numberofagents];
        double[] avgWins = new double[numberofagents];
        log.info("\nTotal Payoffs");
        for (int i = 0; i < numberofagents; i++) {
            for (int k = 0; k < numberofagents; k++) {
                if (!runVsItself && i == k)
                    continue;
                //gameMatrix[i][k].value1 = Math.round(gameMatrix[i][k].value1 / largestValue * 100);
                //gameMatrix[i][k].value2 = Math.round(gameMatrix[i][k].value2 / largestValue * 100);
                avgValues[i] += gameMatrix[i][k].value1;
                avgValuesErr[i] += gameMatrixErr[i][k].value1;
                avgBestResponse[i] += bestRespMatrix[i][k].value1;
                avgBestResponseErr[i] += bestRespMatrixErr[i][k].value1;
                avgWins[i] += winMatrix[i][k].value1;
            }
            int totnumberofagents = runVsItself ? numberofagents : numberofagents - 1;
            cs.pool1.get(i).profit = avgValues[i] / totnumberofagents;
            cs.pool1.get(i).profitErr = avgValuesErr[i] / totnumberofagents;
            cs.pool1.get(i).bestResponseCount = avgBestResponse[i] / totnumberofagents;
            cs.pool1.get(i).bestResponseCountErr = avgBestResponseErr[i] / totnumberofagents;
            cs.pool1.get(i).wins = (avgWins[i] * 100) / (totnumberofagents * Configuration.TEST_ROUNDS);
            log.info(String.format("%s,normpayoff,%.3f,err,%.3f,bestresponse,%.3f,err,%.3f,wins,%.3f", cs.pool1.get(i).name,
                    cs.pool1.get(i).profit, cs.pool1.get(i).profitErr, cs.pool1.get(i).bestResponseCount, cs.pool1.get(i).bestResponseCountErr, avgWins[i]));
        }

        log.info("****************Minimax Regret Strategy**********************");
        int minimaxRegretStrategy = getMinimaxRegretStrategy();
        log.info(String.format("%s", cs.pool1.get(minimaxRegretStrategy).name));

        log.info("****************Sorted by norm profit**********************");
        Collections.sort(cs.pool1, new AgentComparators.CompareByProfit());
        log.info(String.format("Broker,normpayoff,normpayoff.err,bestresponse,bestresponse.err,wins"));
        for (Agent a : cs.pool1)
            log.info(String.format("%s,%.3f,%.3f,%.3f,%.3f,%.3f", a.name, a.profit, a.profitErr, a.bestResponseCount, a.bestResponseCountErr, a.wins));

        log.info("**********************Sorted by best response**********************");
        Collections.sort(cs.pool1, new AgentComparators.CompareByBestResponse());
        log.info(String.format("Broker,normpayoff,normpayoff.err,bestresponse,bestresponse.err,wins"));
        for (Agent a : cs.pool1)
            log.info(String.format("%s,%.3f,%.3f,%.3f,%.3f,%.3f", a.name, a.profit, a.profitErr, a.bestResponseCount, a.bestResponseCountErr, a.wins));

        log.info("****************Sorted by norm wins**********************");
        Collections.sort(cs.pool1, new AgentComparators.CompareByWins());
        log.info(String.format("Broker,normpayoff,normpayoff.err,bestresponse,bestresponse.err,wins"));
        for (Agent a : cs.pool1)
            log.info(String.format("%s,%.3f,%.3f,%.3f,%.3f,%.3f", a.name, a.profit, a.profitErr, a.bestResponseCount, a.bestResponseCountErr, a.wins));
    }

    /**
     * The last RL agent added to the pools. Required to check if the current pure
     * strategy is just playing the last RL agent created
     */
    private Agent lastRLAgent;

    /**
     * Sets up the logger for this experiment This allows me to print to the console
     * AND save to a file at the same time as well as define different logging
     * levels
     */
    public void setupLogging() throws IOException {
        // %1 = Date, %2 = Source, %3 = Logger, %4 = Level, %5 = Message, &6 = Thrown
        // %1$tF = Date -> Y-m-d
        // %1$tT = Date -> 24 hour format
        // %4$s = Log Type (Info, ...)
        // %2$s = Class and Method Call
        // %5$s%6$s = Message
        // {%1$tT} %2$s %5$s%6$s
        System.setProperty("java.util.logging.SimpleFormatter.format", "%5$s%6$s" + "\n");
        FileHandler fh = new FileHandler("experiment.log", true);
        SimpleFormatter formatter = new SimpleFormatter();

        fh.setFormatter(formatter);
        log.addHandler(fh);
    }

    public int getMinimaxRegretStrategy() {
        // calculating regret for colom player

        double[][] regretTable = new double[numberofagents + 1][numberofagents + 1];

        // Create the regret table
        for (int row = 0; row < numberofagents; row++) {
            double maxPayoff = gameMatrix[row][0].value2;
            for (int col = 0; col < numberofagents; col++) {
                regretTable[row][col] = gameMatrix[row][col].value2;
                if (gameMatrix[row][col].value2 > maxPayoff)
                    maxPayoff = gameMatrix[row][col].value2;
            }
            // got the maxPayoff now del from every value from the regrettable
            for (int col = 0; col < numberofagents; col++)
                regretTable[row][col] = maxPayoff - regretTable[row][col];
        }

        // now find maximum regret for each strategy for the column palyer
        for (int col = 0; col < numberofagents; col++) {
            double maxregret = regretTable[0][col];
            for (int row = 0; row < numberofagents; row++)
                if (maxregret < regretTable[row][col])
                    maxregret = regretTable[row][col];
            regretTable[regretTable.length - 1][col] = maxregret;
        }

        // scan the last coloum to get the minimun of maximum regret
        double minRegret = regretTable[regretTable.length - 1][0];
        int minimaxRegretStrategy = 0;
        for (int col = 0; col < numberofagents; col++) {
            if (minRegret > regretTable[regretTable.length - 1][col]) {
                minRegret = regretTable[regretTable.length - 1][col];
                minimaxRegretStrategy = col;
            }
        }

        return minimaxRegretStrategy;
    }

    public void startExperiment(boolean roundrobin, String polToLearnFrom) throws Exception {
        setupLogging();
        Scanner input = new Scanner(System.in);
        log.info("*************** Experimental Run Log ***************");

        // List<Agent> initialMain = new ArrayList<>();
        // initialMain.add(new AlwaysIncrease());
        // DQAgentMDP.trainDQAgent(initialMain, "main.pol", null);

        log.info(Configuration.toStringRepresentation());

        Stack<Agent> litStrategies = roundrobin ? null : getLiteratureStrategies();

        CaseStudy currentCase = roundrobin ? setupRoundRobin() : setupInitialStrategies();
        List<Agent> strategiesToRemove = new ArrayList<>();
        int iterations = 0;
        while (true) {
            iterations++;
            log.info("****************************** Iteration " + iterations + " TraningRounds for DQAgent: "
                    + Configuration.TRAINING_ROUNDS);

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

            // ************** Print the round robin results
            if (roundrobin) {
                printClusterValues(currentCase, true);
                printGameMatrix(currentCase, true);
                logGameMatrix(currentCase);
                break;
            }

            // ************** Compute nash equilibrium strategies
            log.info("Getting nash equilibrium strategies");
            List<double[]> nashEqInitial = Gambit.getNashEqStrategies(this, currentCase);
            List<double[]> nashEqPure = Gambit.getPureStrategies(nashEqInitial);
            List<double[]> nashEqMixed = Gambit.getMixedStrategies(nashEqInitial);

            // ************** Strategies with zeros on columns from both pools will be
            // removed in the next iteration
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
                log.info("[Manual Selection] Selected " + Arrays.toString(smneProbs));
            } else {
                if (nashEqMixed.size() > 0) {
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
            String dqFilename = Configuration.DQ_TRAINING + "_" + smne.name + ".pol";
            DQAgentMDP.trainDQAgent(opponentPool, dqFilename, polToLearnFrom);

            log.info("Training DQ Agent");
            DQAgent dqAgent = new DQAgent(dqFilename);

            // ************** Run test games of SMNE vs RL
            log.info("Running SMNE vs DQAgent");
            CaseStudy testGame = new CaseStudy().addP1Strats(smne).addP2Strats(dqAgent);
            startSimulation(testGame);

            // ************** Does RL have a higher payoff than SMNE?
            log.info("SMNE Profit: " + smne.profit + ", DQAgent profit: " + dqAgent.profit);

            log.info("Feature Size: " + DQAgentMDP.NUM_OBSERVATIONS);
            log.info("Training Rounds: " + Configuration.TRAINING_ROUNDS);
            log.info("Test Rounds: " + Configuration.TEST_ROUNDS);
            log.info(String.format("Def %s, NoC %s, Inc %s Def2 %s Inc2 %s", DQAgent.DEFECT, DQAgent.NOC, DQAgent.INC,
                    DQAgent.DEFECT2, DQAgent.INC2));


            log.info("DQ0 [0] Act " + dqAgent.actHistory[0] + " Action: " + dqAgent.getAllHistoryActions());
            log.info("Opp [0] Act " + smne.actHistory[0] + " Action: " + smne.getAllHistoryActions());
            log.info("DQ0 [0] Trf " + dqAgent.tariffHistory[0] + " TrfHis: " + dqAgent.getHistoryByPubCyc(dqAgent.tariffHistory));
            log.info("Opp [0] Trf " + smne.tariffHistory[0] + " TrfHis: " + smne.getHistoryByPubCyc(smne.tariffHistory));
            log.info("DQ0 [0] Mkt " + dqAgent.marketShareHistory[0] + " MktHis: " + dqAgent.getHistoryByPubCyc(dqAgent.marketShareHistory));
            log.info("Opp [0] Mkt " + smne.marketShareHistory[0] + " MktHis: " + smne.getHistoryByPubCyc(smne.marketShareHistory));
            log.info("DQ0 [0] Prf " + dqAgent.profitHistory[0] + " PftHis: " + dqAgent.getHistoryByPubCyc(dqAgent.profitHistory));
            log.info("Opp [0] Prf " + smne.profitHistory[0] + " PftHis: " + smne.getHistoryByPubCyc(smne.profitHistory));


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

        log.info("***************Total Training Rounds: " + Configuration.TRAINING_ROUNDS
                + " : End of Experiment ***************");
        input.close();
    }

    public CaseStudy setupRoundRobin() {
        log.info("=== Setting up initial pools ===");
		/*
		ArrayList<Agent> literatureStrategies = new ArrayList<>();
		TitForTat TFT = new TitForTat(1, 1);
		TitForTat TF2T = new TitForTat(1, 2);
		TitForTat _2TFT = new TitForTat(2, 1);
		literatureStrategies.add(new DQAgent("DQ11", "DQ11_1.00DQ10.pol"));
//		literatureStrategies.add(new DQAgent("BR_TFT", "BR_TFT.pol"));
		literatureStrategies.add(new HardMajority());
		literatureStrategies.add(_2TFT);
		literatureStrategies.add(TFT);
		literatureStrategies.add(TF2T);
		literatureStrategies.add(new ZI());
		literatureStrategies.add(new Grim());
		literatureStrategies.add(new SoftMajority());
		literatureStrategies.add(new AlwaysSame());
		literatureStrategies.add(new Pavlov());
		literatureStrategies.add(new Prober());
		literatureStrategies.add(new NaiveProber());
		literatureStrategies.add(new AlwaysIncrease());
		literatureStrategies.add(new AlwaysDefect());
		literatureStrategies.add(new ZIP());
		literatureStrategies.add(new GD());

		ArrayList<Agent> literatureStrategiesClone = new ArrayList<>();//(ArrayList<Agent>) literatureStrategies.clone();
		for(Agent a : literatureStrategies)
			literatureStrategiesClone.add(a.clone());
		
		CaseStudy initial = new CaseStudy();
		initial.pool1.addAll(literatureStrategies);
		initial.pool2.addAll(literatureStrategiesClone);
		*/
        //new DQAgent("DQ11", "DQ11_1.00DQ10.pol"),

        /*
         * Roundrobin strategies: The order of the agent is important here
         * */
        CaseStudy initial = new CaseStudy().addP1Strats(new ZI(), new ZIP(), new GD(), new TitForTat(1, 2), new TitForTat(1, 1), new TitForTat(2, 1), new SoftMajority(), new Grim(), new Pavlov(), new HardMajority(), new Prober(), new NaiveProber(), new AlwaysDefect(), new AlwaysIncrease(), new AlwaysSame(), new NaiveIncrease());
        initial.addP2Strats(new ZI(), new ZIP(), new GD(), new TitForTat(1, 2), new TitForTat(1, 1), new TitForTat(2, 1), new SoftMajority(), new Grim(), new Pavlov(), new HardMajority(), new Prober(), new NaiveProber(), new AlwaysDefect(), new AlwaysIncrease(), new AlwaysSame(), new NaiveIncrease());

        /*
         * Running one single strategy to generate payoffs for the regret table
         * */
//		CaseStudy initial = new CaseStudy().addP1Strats(new GD());
//									initial.addP2Strats(new GD(), new ZI(),  new ZIP(), new GD(), new TitForTat(1, 2), new TitForTat(1, 1), new TitForTat(2, 1), new SoftMajority(), new Grim(), new Pavlov(), new HardMajority(), new Prober(), new NaiveProber(), new AlwaysDefect(),new AlwaysIncrease(), new AlwaysSame(), new NaiveIncrease());
        return initial;
    }

    public CaseStudy setupInitialStrategies() {
        //new DQAgent("DQ0", "DQ0_1.00AlD.pol"), new DQAgent("R.DQ0", "BR_TFT.pol"),
        // new Prober(), new NaiveProber(), new AlwaysDefect(),
        CaseStudy initial = new CaseStudy().addP1Strats(new DQAgent("DQ11", "DQ11_1.00DQ10.pol"), new ZIP(), new GD(), new TitForTat(1, 2), new TitForTat(1, 1), new TitForTat(2, 1), new SoftMajority(), new Grim(), new Pavlov(), new HardMajority(), new Prober(), new NaiveProber(), new AlwaysDefect(), new AlwaysIncrease(), new AlwaysSame(), new ZI());// new DQAgent("DQ0", "DQ0_1.00AlD.pol"), new DQAgent("DQ3", "DQ3_1.00DQ2.pol"), new DQAgent("DQ4", "DQ4_1.00DQ3.pol"));
        initial.addP2Strats(new DQAgent("DQ11", "DQ11_1.00DQ10.pol"), new ZIP(), new GD(), new TitForTat(1, 2), new TitForTat(1, 1), new TitForTat(2, 1), new SoftMajority(), new Grim(), new Pavlov(), new HardMajority(), new Prober(), new NaiveProber(), new AlwaysDefect(), new AlwaysIncrease(), new AlwaysSame(), new ZI());// new DQAgent("DQ0", "DQ0_1.00AlD.pol"), new DQAgent("DQ3", "DQ3_1.00DQ2.pol"), new DQAgent("DQ4", "DQ4_1.00DQ3.pol"));
        Configuration.DQ_TRAINING = "R.DQ1";
        log.info("Pool1: " + initial.pool1.toString());
        log.info("Pool2: " + initial.pool2.toString());
        return initial;
    }

    public Stack<Agent> getLiteratureStrategies() {
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

    public void startSimulation(CaseStudy cs) {
        double rationality[] = {Configuration.RATIONALITY};
        double inertia[] = {Configuration.INERTIA};
        double imax = inertia.length;
        double rmax = rationality.length;
        double roundmax = Configuration.TEST_ROUNDS;

        // Round Robin Tournament Set Up
        for (int iagent = 0; iagent < cs.pool1.size(); iagent++) {
            for (int kagent = iagent; kagent < cs.pool2.size(); kagent++) {

                for (int iindex = 0; iindex < imax; iindex++) {
                    for (int rindex = 0; rindex < rmax; rindex++) {
                        for (int round = 0; round < roundmax; round++) {
                            // System.out.println("=== Round " + round);
                            cs.pool1.get(iagent).reset();
                            cs.pool2.get(kagent).reset();

                            ob.agentPool.add(cs.pool1.get(iagent));
                            ob.agentPool.add(cs.pool2.get(kagent));

                            cs.pool1.get(iagent).opponentID = cs.pool2.get(kagent).id;
                            cs.pool2.get(kagent).opponentID = cs.pool1.get(iagent).id;

                            for (ob.timeslot = 0; ob.timeslot < Configuration.TOTAL_TIME_SLOTS; ) {
                                // update agent cost
                                ob.updateAgentUnitCost();

                                // Agents taking Actions
                                // Publish tariff at TS: 1, 7, 13, 19 ... if publication cycle is 6
                                if (ob.timeslot % Configuration.PUBLICATION_CYCLE == 1) {
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
                            // printRevenues() function
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
                        // printAverageRevenues() function

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
     * Determines if there is a pure strategy where the RL agent is dominating (zero
     * in all other columns)
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

    public static void sandboxExperiment() throws IOException, CloneNotSupportedException {
        RetailMarketManager rm = new RetailMarketManager();
        rm.setupLogging();
        log.info(Configuration.toStringRepresentation());
//		Agent alwaysI = new AlwaysIncrease();
//		Agent alwaysS = new AlwaysSame();
//		Agent prober = new Prober();
//		DQAgent DQ0 = new DQAgent("DQ0", "DQ0_1.00Prober.pol");
//		DQAgent DQ1 = new DQAgent("DQ1", "DQ1_0.44AlzIncz,0.56DQ0.pol");
//		SMNE smne = new SMNE();
//		smne.addStrategy(1.0, new HardMajority());
//		smne.addStrategy(0.0, new AlwaysSame());
        Agent opponentAgent = new AlwaysSame();
        List<Agent> oppPool = new ArrayList<>();
        oppPool.add(opponentAgent);

        String policyToLearn = "dq_selfplay_10_iterations.pol";
        //DQAgentMDP.trainDQAgent(oppPool, policyToLearn, null);
        DQAgent dqAgent = new DQAgent("DQ", policyToLearn);  // new DQAgent("DQ0", "DQ0_1Day__1.00Prober.pol");//

        rm.startSimulation(new CaseStudy()
                .addP1Strats(opponentAgent)
                .addP2Strats(dqAgent));

        log.info("Feature Size: " + DQAgentMDP.NUM_OBSERVATIONS);
        log.info("Training Rounds: " + Configuration.TRAINING_ROUNDS);
        log.info("Test Rounds: " + Configuration.TEST_ROUNDS);
        log.info(String.format("Def %s, NoC %s, Inc %s Def2 %s Inc2 %s", DQAgent.DEFECT, DQAgent.NOC, DQAgent.INC,
                DQAgent.DEFECT2, DQAgent.INC2));

        log.info("DQ0 [0] Act " + dqAgent.actHistory[0] + " Action: " + dqAgent.getAllHistoryActions());
        log.info("Opp [0] Act " + opponentAgent.actHistory[0] + " Action: " + opponentAgent.getAllHistoryActions());
        log.info("DQ0 [0] Trf " + dqAgent.tariffHistory[0] + " TrfHis: " + dqAgent.getHistoryByPubCyc(dqAgent.tariffHistory));
        log.info("Opp [0] Trf " + opponentAgent.tariffHistory[0] + " TrfHis: " + opponentAgent.getHistoryByPubCyc(opponentAgent.tariffHistory));
        log.info("DQ0 [0] UCt " + dqAgent.unitCostHistory[0] + " PftHis: " + dqAgent.getHistoryByPubCyc(dqAgent.unitCostHistory));
        log.info("Opp [0] UCt " + opponentAgent.unitCostHistory[0] + " PftHis: " + opponentAgent.getHistoryByPubCyc(opponentAgent.unitCostHistory));
        log.info("DQ0 [0] Mkt " + dqAgent.marketShareHistory[0] + " MktHis: " + dqAgent.getHistoryByPubCyc(dqAgent.marketShareHistory));
        log.info("Opp [0] Mkt " + opponentAgent.marketShareHistory[0] + " MktHis: " + opponentAgent.getHistoryByPubCyc(opponentAgent.marketShareHistory));
        log.info("DQ0 [0] Prf " + dqAgent.profitHistory[0] + " PftHis: " + dqAgent.getHistoryByPubCyc(dqAgent.profitHistory));
        log.info("Opp [0] Prf " + opponentAgent.profitHistory[0] + " PftHis: " + opponentAgent.getHistoryByPubCyc(opponentAgent.profitHistory));

    }

    public static void mainExperiment() throws Exception {
        RetailMarketManager rm = new RetailMarketManager();
        rm.startExperiment(false, null);
        log.info("Feature Size: " + DQAgentMDP.NUM_OBSERVATIONS);
    }

    public static void roundRobinExperiment() throws Exception {
        RetailMarketManager rm = new RetailMarketManager();
        rm.startExperiment(true, null);
        log.info("Feature Size: " + DQAgentMDP.NUM_OBSERVATIONS);
    }

    public static void plotExperiment() throws Exception {
        RetailMarketManager rm = new RetailMarketManager();
        rm.setupLogging();
        log.info("=*****PlotExperiment******=");
    }

    public static void main(String[] args) throws Exception {
        /*
         * The Sandbox Experiment tests DQAgent against a few others We can use this
         * experiment to make sure DQAgent is being trained correctly Or to tweak
         * hyperparameters
         */
        //sandboxExperiment();
        roundRobinExperiment();
        /*
         * The Main Experiment runs the flowchart specified by Porag Basically, the SMNE
         * vs DQAgent stuff with Gambit and such
         */
        //mainExperiment();
    }

}
