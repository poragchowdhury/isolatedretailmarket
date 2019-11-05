/**
 * Author: Jose G. Perez
 * Agent that plays mixed strategies with given probabilities
 */
package edu.utep.poragchowdhury.agents.base;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import edu.utep.poragchowdhury.agents.deepq.DQAgent;
import edu.utep.poragchowdhury.simulation.Observer;
import edu.utep.poragchowdhury.simulation.TariffAction;

public class SMNE extends Agent {
    public static final boolean SHARE_VARIABLES = true;
    private List<Double> strategyProbs;
    private List<Agent> agents;
    private Random rand;
    private double sumOfProbabilities = 0;

    public SMNE() {
        super("SMNE_NO_STRATEGIES");
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
            // If prob is greater than 0, add the agent in the name
            if (prob > 0) {
                if (strategy instanceof DQAgent)
                    tempName += String.format("%.2f%s,", prob, ((DQAgent) strategy).getSimpleName()); // Keeping the name upto 2 decimal
                else
                    tempName += String.format("%.2f%s,", prob, strategy.name); // Keeping the name upto 2 decimal
            }
        }
        if (tempName.isEmpty())
            return "Empty SMNE";

        return tempName.substring(0, tempName.length() - 1);
    }

    @Override
    public TariffAction makeAction(Observer ob) throws Exception {
        // https://stackoverflow.com/questions/9330394/how-to-pick-an-item-by-its-probability
        double p = rand.nextDouble();
        double cumulativeProbability = 0;

        for (int i = 0; i < agents.size(); i++) {
            double prob = strategyProbs.get(i);
            double normalizedProb = prob / sumOfProbabilities;
            Agent strategy = agents.get(i);

            cumulativeProbability += normalizedProb;
            if (p <= cumulativeProbability) {
                // If sharing variables, then copy all of SMNEs to the given strategy
                if (SHARE_VARIABLES)
                    this.copyTo(strategy);

                return strategy.makeAction(ob);
            }
        }

        throw new Exception("SMNE did not run one of its strategies.");
    }
}
