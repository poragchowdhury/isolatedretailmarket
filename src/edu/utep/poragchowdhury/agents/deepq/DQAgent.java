package edu.utep.poragchowdhury.agents.deepq;

import java.io.IOException;
import java.util.logging.Logger;

import org.deeplearning4j.rl4j.learning.Learning;
import org.deeplearning4j.rl4j.policy.ACPolicy;
import org.deeplearning4j.rl4j.policy.DQNPolicy;
import org.deeplearning4j.rl4j.policy.Policy;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import edu.utep.poragchowdhury.agents.base.Agent;
import edu.utep.poragchowdhury.agents.base.AgentID;
import edu.utep.poragchowdhury.simulation.Observer;
import edu.utep.poragchowdhury.simulation.TariffAction;

/**
 * // * Deep Q-Learning Agent for use in simulations
 * @author Jose G. Perez
 */
public class DQAgent extends Agent {
    private static Logger log = Logger.getLogger("retailmarketmanager");
    public static int CURRENT_AGENT_COUNT = 0;
    public static int DEFECT = 0;
    public static int DEFECT2 = 0;
    public static int NOC = 0;
    public static int INC = 0;
    public static int INC2 = 0;

    public Policy<MDPState, Integer> pol;
    public int agentNumber = -1;

    public DQAgent(Policy<MDPState, Integer> pol) {
        super("DeepQ_POL", AgentID.DQAgent);
        this.pol = pol;
    }

    public DQAgent(String policyFilename) {
        super("DeepQ_Default", AgentID.DQAgent);
        this.loadPolicy(policyFilename);

        // Only number testing agents, not the ones used for training
        this.agentNumber = CURRENT_AGENT_COUNT;
        CURRENT_AGENT_COUNT++;

        this.name = "DQAgent" + this.agentNumber + "_" + policyFilename.substring(0, policyFilename.length() - 4);
    }

    public DQAgent() {
        super("DeepQ_Training", AgentID.DQAgent);
    }

    public DQAgent(String name, String policyFilename) {
        this(policyFilename);
        this.name = name;
    }

    public String getSimpleName() {
        return this.name;
    }

    private void loadPolicy(String policyFilename) {
        try {
            this.pol = DQNPolicy.load(policyFilename);
        } catch (IOException e) {
            try {
                this.pol = ACPolicy.load(policyFilename);
            } catch (IOException ex) {
                log.info("DQAgent couldn't load DQN policy from file: " + policyFilename);
                ex.printStackTrace();
            }
            log.info("DQAgent couldn't load DQN policy from file: " + policyFilename);
            e.printStackTrace();
        }
    }

    @Override
    public TariffAction makeAction(Observer ob) throws Exception {
        if (pol == null)
            throw new Exception("Calling makeAction() on DQAgent without a Policy");

        // Convert current state into valid input for neural network or policy
        MDPState state = new MDPState(this, ob);
        INDArray input = Nd4j.create(state.toArray());
        input = input.reshape(Learning.makeShape(1, NeuralNet.INPUT_SHAPE));

        // Get the nextAction from the loaded policy
        int nextActionInt = pol.nextAction(input);
        TariffAction nextAction = TariffAction.valueOf(nextActionInt);

        for (INDArray ind : pol.getNeuralNet().outputAll(input))
            log.info(nextAction.name() + " = " + ind.toString());

        if (!Trainer.isTraining) {
            if (nextAction == TariffAction.D1)
                DEFECT++;
            else if (nextAction == TariffAction.NC)
                NOC++;
            else if (nextAction == TariffAction.I1)
                INC++;
            else if (nextAction == TariffAction.D2)
                DEFECT2++;
            else if (nextAction == TariffAction.I2)
                INC2++;
        }
        return nextAction;
    }
}
