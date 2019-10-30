package Agents;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.deeplearning4j.rl4j.learning.Learning;
import org.deeplearning4j.rl4j.policy.DQNPolicy;
import org.deeplearning4j.rl4j.space.Encodable;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import Observer.Observer;
import Tariff.TariffActions;

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
    public int lastDefect = 0;
    public int lastCoop = 0;
    public int lastNoChange = 0;

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

/**
 * Deep Q-Learning Agent for use in simulations
 * @author Jose G. Perez
 */
public class DQAgent extends Agent {
    private static int CURRENT_AGENT_COUNT = 0;
    private DQNPolicy<DQAgentState> pol;
    public int agentNumber = -1;

    public DQAgent(String policyName) {
        super("DeepQ_Default");
        try {
            this.pol = DQNPolicy.load(policyName);
        } catch (IOException e) {
            Logger.getAnonymousLogger().info("{DQAgent.Constructor} Couldn't load DQN policy from file: " + policyName);
        }
        // Only number testing agents, not the ones used for training
        this.agentNumber = CURRENT_AGENT_COUNT;
        CURRENT_AGENT_COUNT++;

        this.name = "DQAgent" + this.agentNumber + "_" + policyName.substring(0, policyName.length() - 4);
    }

    public DQAgent() {
        super("DeepQ_Training");
    }

    public String getSimpleName() {
        return "DQAgent" + this.agentNumber;
    }

    public void playAction(int nextAction, Observer ob) {
    	System.out.println("TS " + ob.timeslot + " Action " + TariffActions.a[nextAction]);
        if (nextAction == 0) // Defect
            increase(ob);
        else if (nextAction == 1) // Increase
            defectOnRivalPrice(ob);
        else // No change
            nochange();
    }

    @Override
    public void publishTariff(Observer ob) {
        // Feed the current state into the policy's network
        DQAgentState state = new DQAgentState(this, ob);
        INDArray input = Nd4j.create(state.toArray());
        input = input.reshape(Learning.makeShape(1, DQAgentMDP.OBSERVATION_SPACE.getShape()));

        int nextAction = pol.nextAction(input);
        playAction(nextAction, ob);

    }
}