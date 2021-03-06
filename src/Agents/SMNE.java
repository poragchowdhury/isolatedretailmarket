/**
 * Author: Jose G. Perez
 * Agent that plays mixed strategies with given probabilities
 */
package Agents;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import Observer.Observer;
import Tariff.TariffAction;

public class SMNE extends Agent {
	private List<Double> strategyProbs;
	private List<Agent> agents;
	private Random rand;
	private double sumOfProbabilities = 0;
	private static int selectedAgentId = 0;

	public SMNE() {
		super("SMNE_NO_STRATEGIES", 10);
		strategyProbs = new ArrayList<Double>();
		agents = new ArrayList<Agent>();
		rand = new Random(System.currentTimeMillis());
	}

	public void addStrategy(double probability, Agent strategy) {
		strategyProbs.add(probability);
		agents.add(strategy);
		this.name = generateName();
		sumOfProbabilities += probability;
	}

	private String generateName() {
		String tempName = "";
		for (int i = 0; i < agents.size(); i++) {
			double prob = strategyProbs.get(i);
			Agent strategy = agents.get(i);
			if (prob > 0) { // If prob is greater than 0, add the agent in the name
				if (strategy instanceof DQAgent)
					tempName += String.format("%.2f%s,", prob, ((DQAgent) strategy).getSimpleName()); // Keeping the name upto 2 decimal
				else
					tempName += String.format("%.2f%s,", prob, strategy.name); // Keeping the name upto 2 decimal
			}
		}
		return tempName.substring(0, tempName.length() == 0 ? 0 : tempName.length() - 1);
	}

	@Override
	public TariffAction makeAction(Observer ob) throws Exception {
		// https://stackoverflow.com/questions/9330394/how-to-pick-an-item-by-its-probability

		if(ob.timeslot == 1) {
			double p = rand.nextDouble();
			double cumulativeProbability = 0;

			for (int i = 0; i < agents.size(); i++) {
				double prob = strategyProbs.get(i);
				double normalizedProb = prob / sumOfProbabilities;
				cumulativeProbability += normalizedProb;
				if (p <= cumulativeProbability) {
					selectedAgentId = i;
					break;
				}
			}
		}
		
		this.copyTo(agents.get(selectedAgentId), ob);
		
		//System.out.println("SMNE selected " + selectedAgentId);
		return agents.get(selectedAgentId).makeAction(ob);
		//		throw new Exception("SMNE did not run one of its strategies.");
	}
}
