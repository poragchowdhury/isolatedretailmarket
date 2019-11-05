package Agents;

import java.io.IOException;
import java.util.logging.Logger;

import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.rl4j.learning.Learning;
import org.deeplearning4j.rl4j.policy.ACPolicy;
import org.deeplearning4j.rl4j.policy.DQNPolicy;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import Observer.Observer;
import Tariff.TariffAction;

/**
 * Deep Q-Learning Agent for use in simulations
 * 
 * @author Jose G. Perez
 */
public class DQAgent extends Agent {
    public static int CURRENT_AGENT_COUNT = 0;
    public static int DEFECT = 0;
    public static int NOC = 0;
    public static int INC = 0;

    public DQNPolicy<DQAgentState> pol;
    public MultiLayerNetwork mdp;
    public int agentNumber = -1;

    public boolean isTraining() {
        return pol == null && mdp == null;
    }

    public DQAgent(MultiLayerNetwork mdp) {
        super("DeepQ_MDP");
        this.mdp = mdp;
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

        // this.name = "DQAgent" + this.agentNumber + "_" + policyName.substring(0, policyName.length() - 4);
    }

    public DQAgent() {
        super("DeepQ_Training");
    }

    public DQAgent(String name, String policyFilename) {
        this(policyFilename);
        this.name = name;
    }

    public String getSimpleName() {
        return "DQAgent" + this.agentNumber;
    }

    @Override
    public TariffAction makeAction(Observer ob) throws Exception {

        // Feed the current state into the policy's network
        DQAgentState state = new DQAgentState(this, ob);
        INDArray input = Nd4j.create(state.toArray());
        input = input.reshape(Learning.makeShape(1, DQAgentMDP.OBSERVATION_SPACE.getShape()));
        TariffAction nextAction = null;
        if (pol != null) {
            INDArray output = pol.getNeuralNet().output(input);
            int nextActionInt = pol.nextAction(input);
            nextAction = TariffAction.valueOf(nextActionInt);
            // System.out.println("NN Input: " + input.toString());
            // System.out.println("NN Output: " + output.toString() + ", " + nextAction);
        }

        if (mdp != null) {
            int nextActionInt = Nd4j.argMax(mdp.output(input), Integer.MAX_VALUE).getInt(0);
            nextAction = TariffAction.valueOf(nextActionInt);
        }

        if (nextAction != null) {
            if (!isTraining()) {
                if (nextAction == TariffAction.DEFECT)
                    DEFECT++;
                else if (nextAction == TariffAction.NOCHANGE)
                    NOC++;
                else
                    INC++;
            }
            return nextAction;
        }
        throw new Exception("Called makeAction on DQAgent without Policy or MDP");
    }
}
