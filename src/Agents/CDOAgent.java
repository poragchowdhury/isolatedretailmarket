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
public class CDOAgent extends Agent {
	public int clusterSize = 6;
	public boolean cdo_agent = false;
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

	public CDOAgent(DQNPolicy<DQAgentState> pol) {
		super("DeepQ_Training", 3);
		this.pol = pol;
	}
	
	public CDOAgent(String policyName) {
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
	
	public CDOAgent(String name, int clusterSize) {
		super(name, 3);
		this.clusterSize = clusterSize;
		
		if(clusterSize == 2) {
			// Group 1
			brMap = new HashMap<String, String>();
			brMap.put(name, "temp11_1.00D11.pol"); // DO-EGTA DQ12
			brMap.put("AlD", "temp11_1.00D11.pol");
			brMap.put("NvPbr", "temp11_1.00D11.pol");
			brMap.put("Pbr", "temp11_1.00D11.pol");
			brMap.put("HardMJ", "temp11_1.00D11.pol");
			brMap.put("GD", "temp11_1.00D11.pol");
			brMap.put("1TF1T", "temp11_1.00D11.pol");
			brMap.put("1TF2T", "temp11_1.00D11.pol");
			brMap.put("2TF1T", "temp11_1.00D11.pol");
			brMap.put("Grim", "temp11_1.00D11.pol");
			brMap.put("ZI", "temp11_1.00D11.pol");
			brMap.put("ZIP", "temp11_1.00D11.pol");
			brMap.put("SoftMJ", "temp11_1.00D11.pol");
			brMap.put("Pavlov", "temp11_1.00D11.pol");
			brMap.put("AlS", "temp11_1.00D11.pol");

			// Group 2			
			brMap.put("AlI", "BR_G1_I0_5acts_0.93AlI,0.07NvI.pol");
			brMap.put("NvI", "BR_G1_I0_5acts_0.93AlI,0.07NvI.pol");
		}
		else if(clusterSize == 4) {
			// Group 1
			brMap = new HashMap<String, String>();
			brMap.put(name, "BR_G0_5acts_4Clst_I0_0.33HardMJ,0.67NvPbr.pol");
			brMap.put("AlD", "BR_G0_5acts_4Clst_I0_0.33HardMJ,0.67NvPbr.pol");
			brMap.put("NvPbr", "BR_G0_5acts_4Clst_I0_0.33HardMJ,0.67NvPbr.pol");
			brMap.put("Pbr", "BR_G0_5acts_4Clst_I0_0.33HardMJ,0.67NvPbr.pol");
			brMap.put("HardMJ", "BR_G0_5acts_4Clst_I0_0.33HardMJ,0.67NvPbr.pol");
			
			// Group 2
			brMap.put("AlI", "BR_G1_I0_5acts_0.93AlI,0.07NvI.pol");
			brMap.put("NvI", "BR_G1_I0_5acts_0.93AlI,0.07NvI.pol");
			brMap.put("GD", "BR_G2_I0_5acts_0.73GD,0.271TF1T.pol");
			
			// Group 3
			brMap.put("1TF1T", "BR_G2_I0_5acts_0.73GD,0.271TF1T.pol");
			brMap.put("1TF2T", "BR_G2_I0_5acts_0.73GD,0.271TF1T.pol");
			brMap.put("2TF1T", "BR_G2_I0_5acts_0.73GD,0.271TF1T.pol");
			brMap.put("Grim", "BR_G2_I0_5acts_0.73GD,0.271TF1T.pol");
			
			// Group 4
			brMap.put("ZI", "BR_G4_I0_5acts_0.91ZI,0.09Pavlov.pol");
			brMap.put("ZIP", "BR_G4_I0_5acts_0.91ZI,0.09Pavlov.pol");
			brMap.put("SoftMJ", "BR_G4_I0_5acts_0.91ZI,0.09Pavlov.pol");
			brMap.put("Pavlov", "BR_G4_I0_5acts_0.91ZI,0.09Pavlov.pol");
			brMap.put("AlS", "BR_G4_I0_5acts_0.91ZI,0.09Pavlov.pol");

		}
		else if(clusterSize == 5) {
			// Group 1
			brMap = new HashMap<String, String>();
			brMap.put(name, "BR_G0_5acts_4Clst_I0_0.33HardMJ,0.67NvPbr.pol");
			brMap.put("AlD", "BR_G0_5acts_4Clst_I0_0.33HardMJ,0.67NvPbr.pol");
			brMap.put("NvPbr", "BR_G0_5acts_4Clst_I0_0.33HardMJ,0.67NvPbr.pol");
			
			// Group 2
			brMap.put("Pbr", "BR_G3_I0_5acts_1.00HardMj.pol");
			brMap.put("HardMJ", "BR_G3_I0_5acts_1.00HardMj.pol");
			
			// Group 3
			brMap.put("AlI", "BR_G1_I0_5acts_0.93AlI,0.07NvI.pol");
			brMap.put("NvI", "BR_G1_I0_5acts_0.93AlI,0.07NvI.pol");
			brMap.put("GD", "BR_G2_I0_5acts_0.73GD,0.271TF1T.pol");
			
			// Group 4
			brMap.put("1TF1T", "BR_G2_I0_5acts_0.73GD,0.271TF1T.pol");
			brMap.put("1TF2T", "BR_G2_I0_5acts_0.73GD,0.271TF1T.pol");
			brMap.put("2TF1T", "BR_G2_I0_5acts_0.73GD,0.271TF1T.pol");
			brMap.put("Grim", "BR_G2_I0_5acts_0.73GD,0.271TF1T.pol");
			
			// Group 5
			brMap.put("ZI", "BR_G4_I0_5acts_0.91ZI,0.09Pavlov.pol");
			brMap.put("ZIP", "BR_G4_I0_5acts_0.91ZI,0.09Pavlov.pol");
			brMap.put("SoftMJ", "BR_G4_I0_5acts_0.91ZI,0.09Pavlov.pol");
			brMap.put("Pavlov", "BR_G4_I0_5acts_0.91ZI,0.09Pavlov.pol");
			brMap.put("AlS", "BR_G4_I0_5acts_0.91ZI,0.09Pavlov.pol");

		}
		else {
			// number of clusters 6
			// Group 1
			brMap = new HashMap<String, String>();
			brMap.put(name, "BR_G0_I0_5acts_1.00NvPbr.pol");
			brMap.put("AlD", "BR_G0_I0_5acts_1.00NvPbr.pol");
			brMap.put("NvPbr", "BR_G0_I0_5acts_1.00NvPbr.pol");
			
			// Group 2
			brMap.put("AlI", "BR_G1_I0_5acts_0.93AlI,0.07NvI.pol");
			brMap.put("NvI", "BR_G1_I0_5acts_0.93AlI,0.07NvI.pol");
			
			// Group 3
			brMap.put("GD", "BR_G2_I0_5acts_0.73GD,0.271TF1T.pol");
			brMap.put("1TF1T", "BR_G2_I0_5acts_0.73GD,0.271TF1T.pol");
			brMap.put("1TF2T", "BR_G2_I0_5acts_0.73GD,0.271TF1T.pol");
			brMap.put("2TF1T", "BR_G2_I0_5acts_0.73GD,0.271TF1T.pol");
			brMap.put("Grim", "BR_G2_I0_5acts_0.73GD,0.271TF1T.pol");
			
			// Group 4
			brMap.put("HardMJ", "BR_G3_I0_5acts_1.00HardMj.pol");
			
			// Group 5
			brMap.put("ZI", "BR_G4_I0_5acts_0.91ZI,0.09Pavlov.pol");
			brMap.put("ZIP", "BR_G4_I0_5acts_0.91ZI,0.09Pavlov.pol");
			brMap.put("SoftMJ", "BR_G4_I0_5acts_0.91ZI,0.09Pavlov.pol");
			brMap.put("Pavlov", "BR_G4_I0_5acts_0.91ZI,0.09Pavlov.pol");
			brMap.put("AlS", "BR_G4_I0_5acts_0.91ZI,0.09Pavlov.pol");
			
			// Group 6
			brMap.put("Pbr", "BR_G5_I0_5acts_1.00Pbr.pol");			
		}
			
		String policyName = brMap.get("AlD");
		try {
			this.pol = DQNPolicy.load(policyName); // defecting group best response
		} catch (Exception e) {
			Logger.getAnonymousLogger().info("{DQAgent.Constructor} Couldn't load DQN policy from file: " + policyName);
		}
		// Only number testing agents, not the ones used for training
//		this.agentNumber = CURRENT_AGENT_COUNT;
//		CURRENT_AGENT_COUNT++;

//		this.name = "DQAgent" + this.agentNumber + "_" + policyName.substring(0, policyName.length() - 4);
	}

	
	public CDOAgent(String name, String policyName) {
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

	public CDOAgent() {
		super("DeepQ_Training", 3);
	}

	public String getSimpleName() {
		return this.name;//"DQAgent" + this.agentNumber;
	}

	@Override
	public TariffAction makeAction(Observer ob) throws IOException {
		if(ob.timeslot == 1) {
			String opponentName = this.name;
			for(Agent a:ob.agentPool)
				if(!a.name.equalsIgnoreCase(opponentName)) {
					opponentName = a.name;
					break;
				}
			this.pol = DQNPolicy.load(brMap.get(opponentName));
		}
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
