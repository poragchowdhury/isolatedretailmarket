package Agents;

import java.io.IOException;
import java.util.logging.Logger;

import org.deeplearning4j.rl4j.learning.Learning;
import org.deeplearning4j.rl4j.policy.DQNPolicy;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import Observer.Observer;
import Tariff.TariffAction;

/**
 * Deep Q-Learning Agent for use in simulations
 * @author Jose G. Perez
 */
public class DQAgent extends Agent {
    private static int CURRENT_AGENT_COUNT = 0;
    private DQNPolicy<DQAgentState> pol;
    public int agentNumber = -1;

    public boolean isTraining() {
        return pol == null;
    }

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

    @Override
    public TariffAction makeAction(Observer ob) {
        // Feed the current state into the policy's network
        DQAgentState state = new DQAgentState(this, ob);
        INDArray input = Nd4j.create(state.toArray());
        input = input.reshape(Learning.makeShape(1, DQAgentMDP.OBSERVATION_SPACE.getShape()));

        int nextActionInt = pol.nextAction(input);
        TariffAction nextAction = TariffAction.valueOf(nextActionInt);

        if (!isTraining())
            System.out.println("TS: " + ob.timeslot + " Action: " + nextAction);

        return nextAction;
    }
}