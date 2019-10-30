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
import Tariff.TariffActions;
import Configuration.Configuration;

class DQAgentState implements Encodable {
    public DQAgent agent;
    public int timeSlot;
    public long ppts;
    public int prevaction;
    public DQAgentState(DQAgent agent) {
        this.agent = agent;
        //this.timeSlot = Observer.timeslot;
        this.ppts = (long) (agent.prevprofit);// / (double) Observer.timeslot);
        //this.prevaction = agent.myPrevActionId;
    }

    @Override
    public double[] toArray() {
        return new double[] {ppts};//, prevaction, timeSlot};//, }; timeSlot//  
    }
}

public class DQAgent extends Agent {
    private DQNPolicy<DQAgentState> pol;

    public DQAgent() {
        this.name = "DEEPQ";
        try {
            this.pol = DQNPolicy.load(Configuration.DQLEARNING_POLICY_FILENAME);
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
        System.out.println("Test: TS: " + Observer.timeslot +  " a " + nextAction + " : " + TariffActions.a[nextAction]);
        if (nextAction == TariffActions.action.DEFECT.ordinal()) // Defect
            defect(ob);
        else if (nextAction == TariffActions.action.INCREASE.ordinal()) // Increase
            increase(ob);
        else if (nextAction == TariffActions.action.DEFECT2.ordinal()) // Defect
            defect2(ob);
        else if (nextAction == TariffActions.action.INCREASE2.ordinal()) // Increase
            increase2(ob);
        else // No change
            nochange();
    }
    
    @Override
    public void tariffCheck(Observer ob) {
        // Don't print tariff check log
    }
}