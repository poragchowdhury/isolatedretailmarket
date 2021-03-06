package Agents;

import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Logger;

import org.deeplearning4j.rl4j.learning.Learning;
import org.deeplearning4j.rl4j.policy.ACPolicy;
import org.deeplearning4j.rl4j.policy.DQNPolicy;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import Configuration.Configuration;
import Observer.Observer;
import Tariff.TariffAction;

/**
 * Deep Q-Learning Agent for use in simulations
 * 
 * @author Jose G. Perez
 */
public class DQAgent extends Agent {
	public HashMap<String, String> brMap;
	private static int CURRENT_AGENT_COUNT = 0;
	public static int DEFECT = 0;
	public static int NOC = 0;
	public static int INC = 0;
	public static int DEFECT2 = 0;
	public static int INC2 = 0;
	public DQNPolicy<DQAgentState> pol;
	public int agentNumber = -1;

	public boolean isTraining() {
		return pol == null;
	}

	public DQAgent(DQNPolicy<DQAgentState> pol) {
		super("DeepQ_Training", 3);
		this.pol = pol;
	}
	
	public DQAgent(String policyName) {
		super("DeepQ_Default", 3);
		try {
			this.pol = DQNPolicy.load(policyName);
		} catch (Exception e) {
			Logger.getAnonymousLogger().info("{DQAgent.Constructor} Couldn't load DQN policy from file: " + policyName);
		}
		// Only number testing agents, not the ones used for training
		this.agentNumber = CURRENT_AGENT_COUNT;
		CURRENT_AGENT_COUNT++;

		this.name = "DQAgent" + this.agentNumber + "_" + policyName.substring(0, policyName.length() - 4);
	}
	
	public DQAgent(String name, String policyName) {
		super(name, 3);
		try {
			this.pol = DQNPolicy.load(policyName);
		} catch (Exception e) {
			Logger.getAnonymousLogger().info("{DQAgent.Constructor} Couldn't load DQN policy from file: " + policyName);
		}
		// Only number testing agents, not the ones used for training
//		this.agentNumber = CURRENT_AGENT_COUNT;
//		CURRENT_AGENT_COUNT++;

//		this.name = "DQAgent" + this.agentNumber + "_" + policyName.substring(0, policyName.length() - 4);
	}

	public DQAgent() {
		super("DeepQ_Training", 3);
	}

	public String getSimpleName() {
		return this.name;//"DQAgent" + this.agentNumber;
	}

	@Override
	public TariffAction makeAction(Observer ob) throws IOException {
		// Feed the current state into the policy's network
		DQAgentState state = new DQAgentState(this, ob);
		INDArray input = Nd4j.create(state.toArray());
		input = input.reshape(Learning.makeShape(1, DQAgentMDP.OBSERVATION_SPACE.getShape()));

		INDArray output = pol.getNeuralNet().output(input);
		//System.out.println("NN Input: " + input.toString());
		//System.out.println("NN Output: " + output.toString());

		int nextActionInt = pol.nextAction(input);
		TariffAction nextAction = TariffAction.valueOf(nextActionInt);

		if (!isTraining()) {
			if (nextAction == TariffAction.D1) {
				DEFECT++;
			}
			else if (nextAction == TariffAction.NC) {
				NOC++;
			}
			else if (nextAction == TariffAction.I2) {
				INC2++;
			}
			else if (nextAction == TariffAction.D2) {
				DEFECT2++;
			}
			else {
				INC++;
			}
		}
		
//		actionHistory = actionHistory + " " + nextAction.name().substring(0,1) + (nextActionInt > 2 ? (nextAction.name().substring(nextAction.name().length()-1)) : 0);
//		if(actionHistory.length() == Configuration.TOTAL_PUBLICATIONS_IN_A_GAME*3)
//			Logger.getAnonymousLogger().info(actionHistory);
		
		return nextAction;
	}
}
