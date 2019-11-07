package edu.utep.poragchowdhury.agents.deepq;

import java.io.IOException;
import java.util.logging.Logger;

import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.rl4j.learning.Learning;
import org.deeplearning4j.rl4j.policy.ACPolicy;
import org.deeplearning4j.rl4j.policy.DQNPolicy;
import org.deeplearning4j.rl4j.policy.Policy;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import edu.utep.poragchowdhury.agents.base.Agent;
import edu.utep.poragchowdhury.simulation.Observer;
import edu.utep.poragchowdhury.simulation.TariffAction;

/**
 * Deep Q-Learning Agent for use in simulations
 * @author Jose G. Perez
 */
public class DQAgent extends Agent {
    public static int CURRENT_AGENT_COUNT = 0;
    public static int DEFECT = 0;
    public static int NOC = 0;
    public static int INC = 0;

    public Policy<MDPState, Integer> pol;
    public MultiLayerNetwork mdp;
    public int agentNumber = -1;

    public DQAgent(MultiLayerNetwork mdp) {
        super("DeepQ_MDP");
        this.mdp = mdp;
    }

    public DQAgent(String policyFilename) {
        super("DeepQ_Default");
        this.loadPolicy(policyFilename);

        // Only number testing agents, not the ones used for training
        this.agentNumber = CURRENT_AGENT_COUNT;
        CURRENT_AGENT_COUNT++;

        this.name = "DQAgent" + this.agentNumber + "_" + policyFilename.substring(0, policyFilename.length() - 4);
    }

    public DQAgent() {
        super("DeepQ_Training");
    }

    public DQAgent(String name, String policyFilename) {
        this(policyFilename);
        this.name = name;
    }

    public String getSimpleName() {
        return this.name;
        // return "DQAgent" + this.agentNumber;
    }

    public void loadPolicy(String policyFilename) {
        try {
            this.pol = DQNPolicy.load(policyFilename);
        } catch (IOException e) {
            try {
                this.pol = ACPolicy.load(policyFilename);
            } catch (IOException ex) {
                Logger.getAnonymousLogger().info("DQAgent couldn't load DQN policy from file: " + policyFilename);
                ex.printStackTrace();
            }
            e.printStackTrace();
        }
    }

    @Override
    public TariffAction makeAction(Observer ob) throws Exception {
        // Convert current state into valid input for neural network or policy
        MDPState state = new MDPState(this, ob);
        INDArray input = Nd4j.create(state.toArray());
        input = input.reshape(Learning.makeShape(1, NeuralNet.INPUT_SHAPE));
        TariffAction nextAction = null;

        // Get the nextAction from the loaded policy
        if (pol != null) {
            int nextActionInt = pol.nextAction(input);
            nextAction = TariffAction.valueOf(nextActionInt);
        }

        // Get the next action from the loaded neural network
        if (mdp != null) {
            int nextActionInt = Nd4j.argMax(mdp.output(input), Integer.MAX_VALUE).getInt(0);
            nextAction = TariffAction.valueOf(nextActionInt);
        }

        if (!Trainer.isTraining) {
            if (nextAction == TariffAction.DEFECT)
                DEFECT++;
            else if (nextAction == TariffAction.NOCHANGE)
                NOC++;
            else
                INC++;
            
            System.out.println(nextAction.name());
        }
        return nextAction;
    }
}
