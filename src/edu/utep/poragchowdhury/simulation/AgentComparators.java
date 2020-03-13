package edu.utep.poragchowdhury.simulation;

import java.util.Comparator;

import edu.utep.poragchowdhury.agents.base.Agent;
import org.jetbrains.annotations.NotNull;

public class AgentComparators {
    public class CompareByWins implements Comparator<Agent> {
        @Override
        public int compare(@NotNull Agent a0, @NotNull Agent a1) {
            return Double.compare(a0.wins, a1.wins);
        }
    }

    public class CompareByProfit implements Comparator<Agent> {
        @Override
        public int compare(@NotNull Agent a0, @NotNull Agent a1) {
            return Double.compare(a0.profit, a1.profit);
        }
    }

    public class CompareByBestResponse implements Comparator<Agent> {
        @Override
        public int compare(@NotNull Agent a0, @NotNull Agent a1) {
            return Double.compare(a0.bestResponseCount, a1.bestResponseCount);
        }
    }
}
