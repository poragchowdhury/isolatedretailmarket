package Agents;

import java.io.IOException;
import java.util.logging.Logger;

import org.deeplearning4j.rl4j.learning.Learning;
import org.deeplearning4j.rl4j.policy.DQNPolicy;
import org.deeplearning4j.rl4j.space.Encodable;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import Observer.Observer;

/**
 * Encodes the current state of a Deep Q-Learning Agent
 * @author Jose G. Perez
 */
class DQAgentState implements Encodable {
    public Agent agent;
    public int timeSlot;
    public long ppts;

    public DQAgentState(Agent agent, Observer ob) {
        this.agent = agent;
        this.timeSlot = ob.timeslot;
        this.ppts = (long) (agent.prevprofit / (double) ob.timeslot - 1);
    }

    @Override
    public double[] toArray() {
        return new double[] { timeSlot, ppts };
    }
}

/**
 * Deep Q-Learning Agent for use in simulations
 * @author Jose G. Perez
 */
public class DQAgent extends Agent {
    private DQNPolicy<DQAgentState> pol;

    public DQAgent(String policyName) {
        super("DeepQ_" + policyName);
        try {
            this.pol = DQNPolicy.load(policyName);
        } catch (IOException e) {
            Logger.getAnonymousLogger().info("{DQAgent.Constructor} Couldn't load DQN policy from file: " + policyName);
        }
    }

    public DQAgent() {
        super("DeepQ_Training");
    }

    @Override
    public void publishTariff(Observer ob) {
        // Feed the current state into the policy's network
        DQAgentState state = new DQAgentState(this, ob);
        INDArray input = Nd4j.create(state.toArray());
        input = input.reshape(Learning.makeShape(1, DQAgentMDP.observationSpace.getShape()));

        int nextAction = pol.nextAction(input);
        if (nextAction == 0) // Defect
            defect(ob);
        else if (nextAction == 1) // Increase
            increase(ob);
        else // No change
            nochange();
    }
}