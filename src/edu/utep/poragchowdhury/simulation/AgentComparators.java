package edu.utep.poragchowdhury.simulation;

import java.util.Comparator;

import edu.utep.poragchowdhury.agents.base.Agent;

public class AgentComparators {
    public class CompareByWins implements Comparator<Agent> {
        @Override
        public int compare(Agent a0, Agent a1) {
            if (a0.wins < a1.wins)
                return -1;
            else if (a0.wins > a1.wins)
                return 1;
            else
                return 0;
        }
    }

    public class CompareByProfit implements Comparator<Agent> {
        @Override
        public int compare(Agent a0, Agent a1) {
            if (a0.profit < a1.profit)
                return -1;
            else if (a0.profit > a1.profit)
                return 1;
            else
                return 0;
        }
    }

    public class CompareByBestResponse implements Comparator<Agent> {
        @Override
        public int compare(Agent a0, Agent a1) {
            if (a0.bestResponseCount < a1.bestResponseCount)
                return -1;
            else if (a0.bestResponseCount > a1.bestResponseCount)
                return 1;
            else
                return 0;
        }
    }
}
