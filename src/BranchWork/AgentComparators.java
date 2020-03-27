package BranchWork;

import Agents.Agent;

import java.util.Comparator;

public class AgentComparators {
    public static class CompareByWins implements Comparator<Agent> {
        @Override
        public int compare(Agent a0, Agent a1) {
            return Double.compare(a0.wins, a1.wins);
        }
    }

    public static class CompareByProfit implements Comparator<Agent> {
        @Override
        public int compare(Agent a0, Agent a1) {
            return Double.compare(a0.profit, a1.profit);
        }
    }

    public static class CompareByBestResponse implements Comparator<Agent> {
        @Override
        public int compare(Agent a0, Agent a1) {
            return Double.compare(a0.bestResponseCount, a1.bestResponseCount);
        }
    }
}