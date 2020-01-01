package RetailMarketManager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.Stack;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.deeplearning4j.rl4j.policy.DQNPolicy;
import org.deeplearning4j.rl4j.policy.Policy;
import org.nd4j.autodiff.samediff.transform.OpPredicate;

import Agents.Agent;
import Agents.AlwaysDefect;
import Agents.AlwaysIncrease;
import Agents.AlwaysSame;
import Agents.DQAgent;
import Agents.DQAgentMDP;
import Agents.DQAgentState;
import Agents.GD;
import Agents.Grim;
import Agents.HardMajority;
import Agents.NaiveProber;
import Agents.Pavlov;
import Agents.Prober;
import Agents.ZI;
import Agents.ZIP;
import Agents.SMNE;
import Agents.SoftMajority;
import Agents.TitForTat;
import Configuration.CaseStudy;
import Configuration.Configuration;
import Observer.Observer;
import Tariff.TariffAction;

class AgentCompareByWins implements Comparator {
	@Override
	public int compare(Object a, Object b) {
		// TODO Auto-generated method stub
		Agent a0 = (Agent) a;
		Agent a1 = (Agent) b;
		if(a0.wins < a1.wins)
			return -1;
		else if(a0.wins > a1.wins)
			return 1;
		else
			return 0;
	}
}


class AgentCompareByProfit implements Comparator {
	@Override
	public int compare(Object a, Object b) {
		// TODO Auto-generated method stub
		Agent a0 = (Agent) a;
		Agent a1 = (Agent) b;
		if(a0.profit < a1.profit)
			return -1;
		else if(a0.profit > a1.profit)
			return 1;
		else
			return 0;
	}
}

class AgentCompareByBestResponse implements Comparator {
	@Override
	public int compare(Object a, Object b) {
		// TODO Auto-generated method stub
		Agent a0 = (Agent) a;
		Agent a1 = (Agent) b;
		if(a0.bestResponseCount < a1.bestResponseCount)
			return -1;
		else if(a0.bestResponseCount > a1.bestResponseCount)
			return 1;
		else
			return 0;
	}
}

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
		// TODO Auto-generated constructor stub
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

	public void log(PrintWriter pwOutput) {

		double tariff0 = (double) Math.round(ob.agentPool.get(0).tariffPrice * 10000) / 10000;
		double tariff1 = (double) Math.round(ob.agentPool.get(1).tariffPrice * 10000) / 10000;

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

		System.out.println("TS-" + ob.timeslot + "\t\tCust" + "\tUtlty" + "\tUntCst" + "\tCost" + "\tTariff" + "\tMony"
				+ "\tPrft");
		System.out.println(ob.agentPool.get(0).name + ob.timeslot + "\t\t" + ob.custSubs[0] + "\t" + ob.utility[0]
				+ "\t" + ob.agentPool.get(0).unitcost + "\t" + ob.cost[0] + "\t" + tariff0 + "\t" + ob.money[0] + "\t"
				+ ob.agentPool.get(0).profit);
		pwOutput.print(ob.timeslot + "," + ob.custSubs[0] + "," + ob.utility[0] + "," + ob.money[0] + "," + ob.cost[0]
				+ "," + ob.agentPool.get(0).profit + "," + ob.agentPool.get(0).tariffPrice + ",");
		// System.out.println(ob.agentPool.get(1).name + ob.timeslot + "\t\t" +
		// ob.custSubs[1] + "\t" + ob.utility[1] + "\t" + ob.unitcost + "\t" +
		// ob.cost[1] + "\t" + tariff1 + "\t" + ob.money[1] + "\t" +
		// ob.agentPool.get(1).profit);
		System.out.println();
		// pwOutput.println(ob.custSubs[1] + "," + ob.utility[1] + "," + ob.money[1] +
		// "," + ob.cost[1] + "," + ob.agentPool.get(1).profit + "," +
		// ob.agentPool.get(1).tariffPrice + "," + ob.unitcost);
	}

	public void printRevenues(int round, PrintWriter pwOutput) {
		System.out.print("Round  " + round + "  Rationality  " + Configuration.RATIONALITY + "  Inertia  "
				+ Configuration.INERTIA);
		pwOutput.print(
				"Round," + round + ",Rationality," + Configuration.RATIONALITY + ",Inertia," + Configuration.INERTIA);
		int agentid = 0;
		for (Agent a : ob.agentPool) {
			System.out.print("\t" + a.name + "\t" + a.profit);
			pwOutput.print("," + a.name + "," + a.profit);
			ob.agentPayoffs[agentid][round] = a.profit;
			ob.agentBestResponse[agentid][round] = a.bestResponseCount;
			agentid++;
			// ob.payoffs[ob.payoffcount] = a.profit;
			// ob.payoffcount++;
		}
		System.out.println();
		pwOutput.println();
	}

	public void printAvgRevenues(CaseStudy cs, int iagent, int kagent, PrintWriter pwOutputAvg) {
		double[] payOffVals = ob.calcAvg(cs, ob.agentPayoffs);
		double[] bestRespVals = ob.calcAvg(cs, ob.agentBestResponse);
		try {
			pwOutputAvg.println("Inertia," + Configuration.INERTIA + ",Rationality," + Configuration.RATIONALITY + ","
					+ cs.pool1.get(iagent).name + "," + payOffVals[0] + "," + cs.pool2.get(kagent).name + ","
					+ payOffVals[1] + ",Error1," + payOffVals[2] + ",Error2," + payOffVals[3]);

			// Store the payoffs in the game matrix
			if (iagent != kagent) {
				gameMatrix[iagent][kagent] = new Payoffs(payOffVals[0], payOffVals[1]);
				gameMatrix[kagent][iagent] = new Payoffs(payOffVals[1], payOffVals[0]);
				bestRespMatrix[iagent][kagent] = new Payoffs(bestRespVals[0], bestRespVals[1]);
				bestRespMatrix[kagent][iagent] = new Payoffs(bestRespVals[1], bestRespVals[0]);
			} else {
				double avgVal = (payOffVals[0] + payOffVals[1]) / 2;
				gameMatrix[iagent][kagent] = new Payoffs(avgVal, avgVal);
				double avgBestRespVal = (bestRespVals[0] + bestRespVals[1]) / 2;
				bestRespMatrix[iagent][kagent] = new Payoffs(avgBestRespVal, avgBestRespVal);
			}

			if (largestValue < payOffVals[0])
				largestValue = payOffVals[0];
			if (largestValue < payOffVals[1])
				largestValue = payOffVals[1];
		} catch (Exception ex) {
			System.out.println("Error: " + ex.getMessage());
			ex.printStackTrace();
			System.exit(0);
		}
	}

	public void updateObserver(int round, double inertia, double rationality) {
		ob.SEED = round;
		Configuration.RATIONALITY = rationality;
		Configuration.INERTIA = inertia;
	}

	public void startSimulationV2() {

		String filename = Configuration.LOGFILENAME;

		try {
			FileWriter fwOutput = new FileWriter(filename, true);
			PrintWriter pwOutput = new PrintWriter(new BufferedWriter(fwOutput));
			FileWriter fwOutputAvg = new FileWriter(filename + "-Avg.csv", true);
			PrintWriter pwOutputAvg = new PrintWriter(new BufferedWriter(fwOutputAvg));

			double rationality[] = { 0.8 };
			double inertia[] = { 0.8 };
			double imax = 1;
			double rmax = 1;
			double roundmax = Configuration.TEST_ROUNDS;
			if (!Configuration.DLOGGING) {
				// Detailed logging disabled
				rmax = rationality.length;
				imax = inertia.length;
				roundmax = Configuration.TEST_ROUNDS;
			}

			pwOutput.println(Configuration.toStringRepresentation());
			pwOutputAvg.println(Configuration.toStringRepresentation());

			CaseStudy cs = CaseStudy.getFromConfiguration();

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

								// update the configuration in observer
								// if(!Configuration.DLOGGING)
								// updateObserver(round, inertia[iindex], rationality[rindex]);

								// System.out.println("TS,"+ob.agentPool.get(0).name+
								// "-Cust,"+ob.agentPool.get(0).name+ "-Utility,"+ob.agentPool.get(0).name+
								// "-Money,"+ob.agentPool.get(0).name+ "-Revn,"+ob.agentPool.get(1).name+
								// "-Cust,"+ob.agentPool.get(1).name+ "-Utility,"+ob.agentPool.get(1).name+
								// "-Money,"+ob.agentPool.get(1).name+ "-Reven");
								if (Configuration.DLOGGING)
									pwOutput.println("TS," + ob.agentPool.get(0).name + "-Cust,"
											+ ob.agentPool.get(0).name + "-Utility," + ob.agentPool.get(0).name
											+ "-Money," + ob.agentPool.get(0).name + "-Cost," + ob.agentPool.get(0).name
											+ "-Profit," + ob.agentPool.get(0).name + "-Tariff,"
											+ ob.agentPool.get(1).name + "-Cust," + ob.agentPool.get(1).name
											+ "-Utility," + ob.agentPool.get(1).name + "-Money,"
											+ ob.agentPool.get(1).name + "-Cost," + ob.agentPool.get(1).name
											+ "-Profit," + ob.agentPool.get(1).name + "-Tariff," + "Unitcost");

								// This loop will run for a specific number of publication cycles based on the
								// total timeslots

								for (ob.timeslot = 0; ob.timeslot < Configuration.TOTAL_TIME_SLOTS;) {
									// System.out.println("Timeslot: " + timeslot);
									// log values
									if (Configuration.DLOGGING)
										log(pwOutput);
									// Agents taking Actions
									// Publishing tariffs at TS: 1, 7, 13...
									if (ob.timeslot % Configuration.PUBLICATION_CYCLE == 1) {
										publishTariffs();
										ob.publication_cycle_count++;
									}
									// update agent cost
									ob.updateAgentUnitCost();
									// Customers evaluating tariffs
									customerTariffEvaluation();
									// Update agent accountings
									updateAgentAccountings();
									// Going to next timeslot and updating the cost
									ob.timeslot++;
								}
								// Print Revenues
								printRevenues(round, pwOutput);
								// clear the observer for another simulation round set up
								ob.clear();
							}
							// Print avg result for all the round
							printAvgRevenues(cs, iagent, kagent, pwOutputAvg);
							// clear the observer for another simulation set up with another pair of agents
							ob.allsampleclear();
						}
					}
				}
			}
			printGameMatrix(cs, pwOutputAvg);
			if (Configuration.GET_NASH_EQ)
				nashEqCalc(cs, pwOutputAvg);
			pwOutput.close();
			fwOutput.close();
			pwOutputAvg.close();
			fwOutputAvg.close();
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error: " + e.getMessage());
			e.printStackTrace();
			System.exit(0);
		}
	}

	public void printGameMatrix(CaseStudy cs, PrintWriter pwOutputAvg) {
		// Normalizing the values
		double[] avgValues = new double[numberofagents];
		pwOutputAvg.println("\nActual Payoff Matrix");
		System.out.println("\nTotal Payoffs");
		for (int i = 0; i < numberofagents; i++) {
			for (int k = 0; k < numberofagents; k++) {
//				gameMatrix[i][k].value1 = Math.round(gameMatrix[i][k].value1 / largestValue * 100);
//				gameMatrix[i][k].value2 = Math.round(gameMatrix[i][k].value2 / largestValue * 100);
				pwOutputAvg.print(gameMatrix[i][k].value1 + "," + gameMatrix[i][k].value2 + ",|,");
				System.out.print(gameMatrix[i][k].value1 + "," + gameMatrix[i][k].value2 + "  ");
				avgValues[i] += gameMatrix[i][k].value1;
			}
			pwOutputAvg.println(cs.pool1.get(i).name + " , " + avgValues[i]);
			System.out.println(cs.pool1.get(i).name + " , " + avgValues[i]);
		}
		pwOutputAvg.println();
		createGambitFile(cs, pwOutputAvg);
		createGambitFile(cs, null);
		createCommandLineGambitFile();
	}

	public void printGameMatrix(CaseStudy cs) {
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
				//gameMatrix[i][k].value1 = Math.round(gameMatrix[i][k].value1 / largestValue * 100);
				//gameMatrix[i][k].value2 = Math.round(gameMatrix[i][k].value2 / largestValue * 100);
				avgValues[i] += gameMatrix[i][k].value1;
				avgValuesErr[i] += gameMatrixErr[i][k].value1;
				avgBestResponse[i] += bestRespMatrix[i][k].value1;
				avgBestResponseErr[i] += bestRespMatrixErr[i][k].value1;
				avgWins[i] += winMatrix[i][k].value1;
			}
			cs.pool1.get(i).profit = avgValues[i] / numberofagents;
			cs.pool1.get(i).profitErr = avgValuesErr[i] / numberofagents;
			cs.pool1.get(i).bestResponseCount = avgBestResponse[i] / numberofagents;
			cs.pool1.get(i).bestResponseCountErr = avgBestResponseErr[i] / numberofagents;
			cs.pool1.get(i).wins = (avgWins[i] * 100) / (numberofagents * Configuration.TEST_ROUNDS);
			log.info(String.format(",%s,normpayoff,%.3f,err,%.3f,bestresponse,%.3f,err,%.3f,wins,%.3f", cs.pool1.get(i).name,
					cs.pool1.get(i).profit, cs.pool1.get(i).profitErr, cs.pool1.get(i).bestResponseCount, cs.pool1.get(i).bestResponseCountErr, avgWins[i]));
		}
		log.info("****************Sorted by norm profit**********************");
		Collections.sort(cs.pool1, new AgentCompareByProfit());
		for (Agent a : cs.pool1)
			log.info(String.format(",%s,normpayoff,%.3f,err,%.3f,bestresponse,%.3f,err,%.3f,wins,%.3f", a.name, a.profit, a.profitErr, a.bestResponseCount, a.bestResponseCountErr, a.wins));
		log.info("**********************Sorted by best response**********************");
		Collections.sort(cs.pool1, new AgentCompareByBestResponse());
		for (Agent a : cs.pool1)
			log.info(String.format(",%s,normpayoff,%.3f,err,%.3f,bestresponse,%.3f,err,%.3f,wins,%.3f", a.name, a.profit, a.profitErr, a.bestResponseCount, a.bestResponseCountErr, a.wins));
		log.info("****************Sorted by norm wins**********************");
		Collections.sort(cs.pool1, new AgentCompareByWins());
		for (Agent a : cs.pool1)
			log.info(String.format(",%s,normpayoff,%.3f,err,%.3f,bestresponse,%.3f,err,%.3f,wins,%.3f", a.name, a.profit, a.profitErr, a.bestResponseCount, a.bestResponseCountErr, a.wins));
	}

	public void createCommandLineGambitFile() {

		try {
			FileWriter fw = new FileWriter("Gambit.nfg");
			PrintWriter pw = new PrintWriter(new BufferedWriter(fw));
			pw.println("NFG 1 R \"IPD NFG\" { \"Player 1\" \"Player 2\" } " + "{ " + numberofagents + " "
					+ numberofagents + " }");
			for (int k = 0; k < numberofagents; k++)
				for (int i = 0; i < numberofagents; i++)
					pw.print(gameMatrix[i][k].value1 + " " + gameMatrix[i][k].value2 + " ");
			pw.close();
			fw.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void nashEqCalc(CaseStudy cs, PrintWriter pw) {
		try {
			Process process = new ProcessBuilder("gambit-enummixed.exe", "Gambit.nfg", "-q").start();
			process.waitFor();
			InputStream is = process.getInputStream();
			InputStreamReader isr = new InputStreamReader(is);
			BufferedReader br = new BufferedReader(isr);
			String line;
			int counter = 1;
			System.out.print("Count" + "\t\t\t");
			pw.print("Count" + ",");
			for (int i = 0; i < numberofagents; i++) {// Up to no of strategies
				System.out.print(cs.pool1.get(i).name + "\t\t\t");
				pw.print(cs.pool1.get(i).name + ",");
			}
			System.out.println();
			pw.println();
			while ((line = br.readLine()) != null) {
				String[] arrPr = line.split(",");
				System.out.print(arrPr[0] + "-" + counter + "\t\t\t");
				pw.print(arrPr[0] + "-" + counter + ",");
				for (int i = 1; i <= numberofagents; i++) { // Up to no of strategies
					System.out.print(arrPr[i] + "\t\t\t");
					pw.print(arrPr[i] + ",");
				}
				System.out.println();
				pw.println();
				counter++;
			}
		} catch (Exception ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}
	}

	public void createGambitFile(CaseStudy cs, PrintWriter tpw) {
		try {

			FileWriter fw = (tpw == null) ? new FileWriter("Gambit.gbt") : null;
			PrintWriter pw = null;

			if (tpw == null)
				pw = new PrintWriter(new BufferedWriter(fw));
			else
				pw = tpw;

			pw.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n"
					+ "<gambit:document xmlns:gambit=\"http://gambit.sourceforge.net/\" version=\"0.1\">\r\n"
					+ "<colors>\r\n" + "<player id=\"-1\" red=\"0\" green=\"0\" blue=\"0\" />\r\n"
					+ "<player id=\"0\" red=\"154\" green=\"205\" blue=\"50\" />\r\n"
					+ "<player id=\"1\" red=\"255\" green=\"0\" blue=\"0\" />\r\n"
					+ "<player id=\"2\" red=\"0\" green=\"0\" blue=\"255\" />\r\n" + "</colors>\r\n"
					+ "<font size=\"10\" family=\"74\" face=\"Arial\" style=\"90\" weight=\"92\" />\r\n"
					+ "<numbers decimals=\"4\"/>\r\n" + "<game>\r\n" + "<nfgfile>\r\n"
					+ "NFG 1 R \"Retail Strategic Game\" { \"Player 1\" \"Player 2\" }\n");
			pw.print("{ ");
			for (int pl = 0; pl < 2; pl++) {
				pw.print("{");
				for (int i = 0; i < numberofagents; i++)
					pw.print(" \"" + cs.pool1.get(i).name + "\" ");
				pw.println("}");
			}
			pw.println("}");
			pw.println("\"\"\n");
			// Printing gambit martix
			pw.println("{");
			for (int k = 0; k < numberofagents; k++)
				for (int i = 0; i < numberofagents; i++)
					pw.println("{ \"\" " + gameMatrix[i][k].value1 + ", " + gameMatrix[i][k].value2 + " }");

			pw.println("}");

			for (int i = 1; i <= numberofagents * numberofagents; i++)
				pw.print(i + " ");
			pw.println("</nfgfile>\r\n" + "</game>\r\n" + "</gambit:document>");

			if (tpw == null)
				pw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void printPayoffMatrix() {
		// find the largest value in the payoff matrix
		double maxValue = ob.payoffs[0];
		for (int i = 1; i < 8; i++)
			if (ob.payoffs[i] > maxValue)
				maxValue = ob.payoffs[i];

		// now normalize the payoff matrix and print the maxtrix

		for (int i = 0; i < 8; i++)
			ob.payoffs[i] /= maxValue;

		double T = ob.payoffs[3];
		double R = ob.payoffs[1];
		double P = ob.payoffs[7];
		double S = ob.payoffs[5];

		System.out.println("lamda " + Configuration.RATIONALITY);
		System.out.println("T-R " + (T - R));
		System.out.println("T-P " + (T - P));
		System.out.println("T-S " + (T - S));
		System.out.println("R-P " + (R - P));
		System.out.println("R-S " + (P - S));
		System.out.println("P-S " + (P - S));
		System.out.println("2R-T-S " + (2 * R - T - S));

		/*
		 * System.out.println("========================");
		 * System.out.printf("|%.2f,%.2f |%.2f,%.2f |\n", ob.payoffs[0], ob.payoffs[1],
		 * ob.payoffs[2], ob.payoffs[3]);
		 * System.out.println("========================");
		 * System.out.printf("|%.2f,%.2f |%.2f,%.2f |\n", ob.payoffs[4], ob.payoffs[5],
		 * ob.payoffs[6], ob.payoffs[7]);
		 * System.out.println("========================");
		 */
	}

	private static Logger log = Logger.getLogger("retailmarketmanager");
	// private Logger log = Logger.getLogger("rmm.experiment");

	/**
	 * The last RL agent added to the pools. Required to check if the current pure
	 * strategy is just playing the last RL agent created
	 */
	private Agent lastRLAgent;

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

	/**
	 * Converts a string of the form a/b (fraction) into a double
	 * 
	 * @param fString
	 *            String to convert
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
		System.setProperty("java.util.logging.SimpleFormatter.format", "{%1$tT} %2$s %5$s%6$s" + "\n");
		FileHandler fh = new FileHandler("experiment.log", true);
		SimpleFormatter formatter = new SimpleFormatter();

		fh.setFormatter(formatter);
		log.addHandler(fh);
	}

	public void startExperiment(boolean roundrobin, String polToLearnFrom) throws IOException {
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
				printGameMatrix(currentCase);
				break;
			}

			// ************** Compute nash equilibrium strategies
			log.info("Getting nash equilibrium strategies");
			List<double[]> nashEqInitial = computeNashEq(currentCase);
			List<double[]> nashEqPure = getPureStrategies(nashEqInitial);
			List<double[]> nashEqMixed = getMixedStrategies(nashEqInitial);

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
					int idx = argMinZeros(nashEqMixed);
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
		ArrayList<Agent> literatureStrategies = new ArrayList<>();
		TitForTat TFT = new TitForTat(1, 1);
		TitForTat TF2T = new TitForTat(1, 2);
		TitForTat _2TFT = new TitForTat(2, 1);
//		literatureStrategies.add(new DQAgent("DQ11", "DQ11_1.00DQ10.pol"));
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

		ArrayList<Agent> literatureStrategiesClone = (ArrayList<Agent>) literatureStrategies.clone();
		CaseStudy initial = new CaseStudy();
		initial.pool1.addAll(literatureStrategies);
		initial.pool2.addAll(literatureStrategiesClone);

		return initial;
	}

	public CaseStudy setupInitialStrategies() {

		CaseStudy initial = new CaseStudy().addP1Strats(new Grim(), new DQAgent("DQ9","DQ9_1.00Grim.pol"), new DQAgent("DQ7","DQ7_0.41Grim,0.26DQ5,0.33DQ6.pol"), new DQAgent("DQ11","DQ11_1.00DQ10.pol"), new DQAgent("DQ12","DQ12_1.00DQ11.pol"));
									initial.addP2Strats(new Grim(), new DQAgent("DQ9","DQ9_1.00Grim.pol"), new DQAgent("DQ7","DQ7_0.41Grim,0.26DQ5,0.33DQ6.pol"), new DQAgent("DQ11","DQ11_1.00DQ10.pol"), new DQAgent("DQ12","DQ12_1.00DQ11.pol"));//, new DQAgent("DQ20","DQ20_0.94GD,0.06DQ19.pol"));     
		Configuration.DQ_TRAINING = "DQ23";
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
		double rationality[] = { Configuration.RATIONALITY };
		double inertia[] = { Configuration.INERTIA };
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

							for (ob.timeslot = 0; ob.timeslot < Configuration.TOTAL_TIME_SLOTS;) {
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
							
							if(ob.agentPayoffs[0][round] > ob.agentPayoffs[1][round])
								ob.agentWins[0]++;
							else if(ob.agentPayoffs[0][round] < ob.agentPayoffs[1][round])
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
							
							double avgWins = (ob.agentWins[0] + ob.agentWins[1])/2.0;
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

	public List<double[]> computeNashEq(CaseStudy cs) {
		List<double[]> nashEqStrategies = new ArrayList<>();
		try {
			log.info("Computing game matrix");

			//			for (int i = 0; i < cs.pool1.size(); i++) {
			//				for (int k = 0; k < cs.pool2.size(); k++) {
			//					gameMatrix[i][k].value1 = Math.round(gameMatrix[i][k].value1 / largestValue * 100);
			//					gameMatrix[i][k].value2 = Math.round(gameMatrix[i][k].value2 / largestValue * 100);
			//				}
			//			}

			// Create gambit file
			log.info("Creating Gambit file");
			FileWriter fw = new FileWriter("Gambit.nfg");
			PrintWriter pw = new PrintWriter(new BufferedWriter(fw));
			pw.println("NFG 1 R \"IPD NFG\" { \"Player 1\" \"Player 2\" } " + "{ " + cs.pool1.size() + " "
					+ cs.pool2.size() + " }");
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
		Agent opponentAgent = new AlwaysSame();//new TitForTat(1,1);
		List<Agent> oppPool = new ArrayList<>();
		oppPool.add(opponentAgent);

		String policyToLearn = "DQ11_1.00DQ10.pol";
//		DQAgentMDP.trainDQAgent(oppPool, policyToLearn, null);
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
		log.info("DQ0 [0] Mkt " + dqAgent.marketShareHistory[0] + " MktHis: " + dqAgent.getHistoryByPubCyc(dqAgent.marketShareHistory));
		log.info("Opp [0] Mkt " + opponentAgent.marketShareHistory[0] + " MktHis: " + opponentAgent.getHistoryByPubCyc(opponentAgent.marketShareHistory));
		log.info("DQ0 [0] Prf " + dqAgent.profitHistory[0] + " PftHis: " + dqAgent.getHistoryByPubCyc(dqAgent.profitHistory));
		log.info("Opp [0] Prf " + opponentAgent.profitHistory[0] + " PftHis: " + opponentAgent.getHistoryByPubCyc(opponentAgent.profitHistory));
		
	}

	public static void mainExperiment() throws IOException {
		RetailMarketManager rm = new RetailMarketManager();
		rm.startExperiment(false, null);
		log.info("Feature Size: " + DQAgentMDP.NUM_OBSERVATIONS);
	}

	public static void roundRobinExperiment() throws IOException {
		RetailMarketManager rm = new RetailMarketManager();
		rm.startExperiment(true, null);
		log.info("Feature Size: " + DQAgentMDP.NUM_OBSERVATIONS);
	}

	public static void plotExperiment() throws IOException {
		RetailMarketManager rm = new RetailMarketManager();
		rm.setupLogging();
		log.info("=*****PlotExperiment******=");
	}

	public static void main(String[] args) throws IOException, CloneNotSupportedException {
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
