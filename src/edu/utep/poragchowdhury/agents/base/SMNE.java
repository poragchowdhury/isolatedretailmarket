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
import org.jetbrains.annotations.NotNull;

public class SMNE extends Agent {
    public static final boolean SHARE_VARIABLES = true;
    private List<Double> strategyProbs;
    private List<Agent> agents;
    private Random rand;
    private double sumOfProbabilities = 0;
    private int selectedAgentId = 0;

    public SMNE() {
        super("SMNE_NO_STRATEGIES", AgentID.SMNE);
        strategyProbs = new ArrayList<>();
        agents = new ArrayList<>();
        rand = new Random(System.currentTimeMillis());
    }

    public void addStrategy(double probability, Agent strategy) {
        strategyProbs.add(probability);
        agents.add(strategy);
        this.name = generateName();

        sumOfProbabilities += probability;
    }

    private String generateName() {
        StringBuilder tempName = new StringBuilder();
        for (int i = 0; i < agents.size(); i++) {
            double prob = strategyProbs.get(i);
            Agent strategy = agents.get(i);
            // If prob is greater than 0, add the agent in the name
            if (prob > 0) {
                if (strategy instanceof DQAgent)
                    tempName.append(String.format("%.2f%s,", prob, ((DQAgent) strategy).getSimpleName())); // Keeping the name up to 2 decimal
                else
                    tempName.append(String.format("%.2f%s,", prob, strategy.name)); // Keeping the name up to 2 decimal
            }
        }
        if (tempName.length() == 0)
            return "Empty SMNE";

        return tempName.substring(0, tempName.length() - 1);
    }

    @Override
    public TariffAction makeAction(@NotNull Observer ob) throws Exception {
        if (ob.timeslot == 1) {
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
        return agents.get(selectedAgentId).makeAction(ob);
    }
}
