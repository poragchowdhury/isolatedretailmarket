package Agents;

import java.util.ArrayList;
import java.util.List;

import org.deeplearning4j.rl4j.space.Encodable;

import Observer.Observer;

/**
 * Encodes the current state of a Deep Q-Learning Agent
 * @author Jose G. Perez
 */
class DQAgentState implements Encodable {
    public Agent agent;
    public int timeSlot;
    public long ppts;
    public double[][] agentPayoffs;
    public double prevProfit;
    public int lastDefect;
    public int lastCoop;
    public int lastNoChange;

    public DQAgentState() {
        this(new DQAgent(), new Observer());
    }

    public DQAgentState(Agent agent, Observer ob) {
        this.agent = agent;
        this.timeSlot = ob.timeslot;
        this.ppts = (long) (agent.prevprofit / (double) ob.timeslot);
        this.agentPayoffs = ob.agentPayoffs;
        this.prevProfit = agent.prevprofit;
        if (agent.rivalPrevPrevPrice > agent.rivalPrevPrice)
            this.lastDefect = 1;
        else
            this.lastDefect = 0;
        if (agent.rivalPrevPrevPrice < agent.rivalPrevPrice)
            this.lastCoop = 1;
        else
            this.lastCoop = 0;

        if (agent.rivalPrevPrevPrice == agent.rivalPrevPrice)
            this.lastNoChange = 1;
        else
            this.lastNoChange = 0;

    }

    @Override
    public double[] toArray() {
        List<Double> features = new ArrayList<>();
        /* ===== NOTE: Add your features here ===== */
        features.add((double) timeSlot);
        features.add((double) ppts);
        features.add(prevProfit);
        features.add((double) lastDefect);
        features.add((double) lastCoop);
        features.add((double) lastNoChange);
        // for(double[] arr : agentPayoffs)
        // for(double d : arr)
        // state.add(d);

        // Get all the features from the list
        // Convert to a double array for use in the MDP
        double[] result = new double[features.size()];
        for (int i = 0; i < features.size(); i++)
            result[i] = features.get(i);
        return result;
    }

}