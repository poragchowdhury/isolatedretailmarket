package Agents;

import java.util.ArrayList;
import java.util.List;

import org.deeplearning4j.rl4j.space.Encodable;

import Observer.Observer;
import Configuration.Configuration;

/**
 * Encodes the current state of a Deep Q-Learning Agent
 * @author Jose G. Perez
 */
class DQAgentState implements Encodable {
    public Agent agent;
    public Observer ob;
    public int timeSlot;
    public long ppts;
    public double[][] agentPayoffs;
    public double prevProfit;
    public int lastDefect;
    public int lastCoop;
    public int lastNoChange;
    public double marketShare;
    public int prevAction;
    public double prevHourUsage;
    
    public DQAgentState() {
        this(new DQAgent(), new Observer());
    }

    public DQAgentState(Agent agent, Observer ob) {
        this.agent = agent;
        this.ob = ob;

        this.timeSlot = ob.timeslot;
        this.ppts = (long) (agent.prevprofit / (double) (ob.timeslot-6));
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

        this.marketShare = agent.marketShare;
    }

    public double[] one_hot(int i) {
        double[] d = new double[3];
        // for (int x = 0; x < d.length; x++)
        // d[x] = 0;

        d[i] = 1;
        return d;
    }

    @Override
    public double[] toArray() {
        List<Double> features = new ArrayList<>();
        /* ===== NOTE: Add your features here ===== */
        features.add((double) timeSlot / Configuration.TOTAL_TIME_SLOTS);
        // features.add(agent.profit);
        // features.add((double) ppts);
        // features.add(prevProfit);

        for (double d : one_hot(agent.previousAction.index))
            features.add(d);

        features.add((double) lastNoChange);
        features.add((double) lastDefect);
        features.add((double) lastCoop);

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