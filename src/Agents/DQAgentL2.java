/**
 * Author: Jose G. Perez
 * Deep Q-Learning Agent for use in simulations
 */
package Agents;

import java.io.IOException;
import java.util.logging.Logger;

import org.deeplearning4j.rl4j.learning.Learning;
import org.deeplearning4j.rl4j.policy.DQNPolicy;
import org.deeplearning4j.rl4j.space.Encodable;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

import Observer.Observer;
import Configuration.Configuration;

public class DQAgentL2 extends DQAgent {
    private DQNPolicy<DQAgentState> pol;

    public DQAgentL2(){
        this.name = "DQAgentL2";
        try {
        	// Fixed policy
            this.pol = DQNPolicy.load("dqagent2_SMNE2_400.pol");
        } catch (IOException e) {
            Logger.getAnonymousLogger().info("{DQAgent.Constructor} Couldn't load DQN policy from file " + Configuration.DQLEARNING_POLICY_FILENAME);
        }
    }

    @Override
    public void publishTariff(Observer ob) {
        DQAgentState state = new DQAgentState(this);
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
    
    @Override
    public void tariffCheck(Observer ob) {
        // Don't print tariff check log
    }
}