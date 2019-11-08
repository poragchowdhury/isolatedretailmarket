package edu.utep.poragchowdhury.agents.deepq;

import java.util.ArrayList;
import java.util.List;

import org.deeplearning4j.rl4j.space.Encodable;

import edu.utep.poragchowdhury.agents.base.Agent;
import edu.utep.poragchowdhury.core.Configuration;
import edu.utep.poragchowdhury.simulation.Observer;

/**
 * Encodes the current state of a Deep Q-Learning Agent
 * @author Jose G. Perez
 */
public class MDPState implements Encodable {
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
    public double curHourUsage;
    public double nextHourUsage;

    public MDPState() {
        this(new DQAgent(), new Observer());
    }

    public MDPState(Agent agent, Observer ob) {
        this.agent = agent;
        this.ob = ob;

        this.timeSlot = ob.timeslot;
        this.ppts = (long) (agent.prevprofit / (double) (ob.timeslot + 1));
        this.agentPayoffs = ob.agentPayoffs;
        this.prevProfit = agent.prevprofit;

        if (agent.rivalPrevPrevPrice == agent.rivalPrevPrice)
            this.lastNoChange = 1;
        else
            this.lastNoChange = 0;

        if (agent.rivalPrevPrevPrice > agent.rivalPrevPrice)
            this.lastDefect = 1;
        else
            this.lastDefect = 0;
        if (agent.rivalPrevPrevPrice < agent.rivalPrevPrice)
            this.lastCoop = 1;
        else
            this.lastCoop = 0;

        this.marketShare = agent.marketShare;
        this.prevHourUsage = ob.fcc.usage[ob.timeslot == 0 ? 0 : (ob.timeslot - 1) % 24];
        this.curHourUsage = ob.fcc.usage[ob.timeslot % 24];
        this.nextHourUsage = ob.fcc.usage[(ob.timeslot + 1) % 24];
    }

    public double[] one_hot(int i) {
        double[] d = new double[3];
        d[i] = 1;
        return d;
    }

    @Override
    public double[] toArray() {
        List<Double> features = new ArrayList<>();
        /* ===== NOTE: Add your features here ===== */
        features.add((double) timeSlot / Configuration.TOTAL_TIME_SLOTS);
        features.add(agent.profit / (0.5f * Configuration.POPULATION * Configuration.TOTAL_TIME_SLOTS * 7f));
        features.add((double) ppts / (0.5f * Configuration.POPULATION * 7f));
        features.add(prevProfit / (0.5f * Configuration.POPULATION * Configuration.TOTAL_TIME_SLOTS * 7f));

        // for(double[] arr : agentPayoffs)
        // for(double d : arr)
        // state.add(d);
        // for (double d : one_hot(agent.previousAction.index))
        // features.add(d);

        features.add((double) lastNoChange);
        features.add((double) lastDefect);
        features.add((double) lastCoop);
        // features.add((double) marketShare / Configuration.POPULATION);
        // features.add(prevHourUsage / 7f);
        // features.add(curHourUsage / 7f);
        // features.add(nextHourUsage / 7f);
        // Get all the features from the list
        // Convert to a double array for use in the MDP
        double[] result = new double[features.size()];
        for (int i = 0; i < features.size(); i++)
            result[i] = features.get(i);
        return result;
    }

}
