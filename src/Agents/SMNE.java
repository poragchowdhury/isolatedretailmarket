/**
 * Author: Jose G. Perez
 * Agent that plays mixed strategies with given probabilities
 */
package Agents;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import Observer.Observer;

public class SMNE extends Agent {
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
        String tempName = "SMNE";
        for (int i = 0; i < agents.size(); i++) {
            double prob = strategyProbs.get(i);
            Agent strategy = agents.get(i);
            tempName += String.format("[%.3f,%s]", prob, strategy.name);
        }
        return tempName;
    }

    @Override
    public void publishTariff(Observer ob) {
        // https://stackoverflow.com/questions/9330394/how-to-pick-an-item-by-its-probability

        double p = rand.nextDouble();
        double cumulativeProbability = 0;

        for (int i = 0; i < agents.size(); i++) {
            double prob = strategyProbs.get(i);
            double normalizedProb = prob / sumOfProbabilities;
            Agent strategy = agents.get(i);

            cumulativeProbability += normalizedProb;
            if (p <= cumulativeProbability)
                strategy.publishTariff(ob);
        }

        tariffCheck(ob); /* Tariff Check */
    }
}
