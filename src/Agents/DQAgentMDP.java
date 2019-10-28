/**
 * Author: Jose G. Perez
 * Deep Q-Learning MDP for use with deeplearning4j to train
 */
package Agents;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.logging.Logger;

import org.deeplearning4j.gym.StepReply;
import org.deeplearning4j.rl4j.learning.Learning;
import org.deeplearning4j.rl4j.learning.NeuralNetFetchable;
import org.deeplearning4j.rl4j.learning.sync.qlearning.QLearning;
import org.deeplearning4j.rl4j.learning.sync.qlearning.discrete.QLearningDiscreteDense;
import org.deeplearning4j.rl4j.mdp.MDP;
import org.deeplearning4j.rl4j.mdp.toy.SimpleToyState;
import org.deeplearning4j.rl4j.network.dqn.DQNFactoryStdDense;
import org.deeplearning4j.rl4j.network.dqn.IDQN;
import org.deeplearning4j.rl4j.policy.DQNPolicy;
import org.deeplearning4j.rl4j.space.ArrayObservationSpace;
import org.deeplearning4j.rl4j.space.DiscreteSpace;
import org.deeplearning4j.rl4j.space.ObservationSpace;
import org.deeplearning4j.rl4j.util.DataManager;
import org.json.JSONObject;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.learning.config.Adam;

import Configuration.Configuration;
import Observer.Observer;
import RetailMarketManager.RetailMarketManager;
import Tariff.TariffActions;

public class DQAgentMDP implements MDP<DQAgentState, Integer, DiscreteSpace> {
    public static QLearning.QLConfiguration QLConfig = new QLearning.QLConfiguration(
            123,   //Random seed
            Configuration.TOTAL_TIME_SLOTS/Configuration.PUBLICATION_CYCLE,//Max step By epoch
            (Configuration.TOTAL_TIME_SLOTS/Configuration.PUBLICATION_CYCLE)*Configuration.TRAININGROUNDS, //Max step
            Configuration.TOTAL_TIME_SLOTS/6, //Max size of experience replay
            6, //size of batches
            Configuration.TOTAL_TIME_SLOTS/6,   //target update (hard)
            0, //num step noop warmup
            0.9,  //reward scaling
            0.1,  //gamma
            0.0,  //td-error clipping
            0.5f,  //min epsilon
            Configuration.TOTAL_TIME_SLOTS/6,  //num step for eps greedy anneal
            false   //double DQN
    );

    public static DQNFactoryStdDense.Configuration QLNet = DQNFactoryStdDense.Configuration.builder().
    		l2(0.00).updater(new Adam(0.001)).numHiddenNodes(16).numLayer(3).build();

    private static final int NUM_ACTIONS = 5;

    public static DiscreteSpace actionSpace = new DiscreteSpace(NUM_ACTIONS);
    public static ObservationSpace<DQAgentState> observationSpace = new ArrayObservationSpace<DQAgentState>(new int[] {1});
    
    public static void log(String message, Object... args) {
        Logger.getAnonymousLogger().info("[SimpleMDP]" + String.format(message, args));
    }
    
    public DQAgentState currentState;
    public DQAgentState startingState;

    private RetailMarketManager retailManager;
    private DQAgent agent;
    private List<Agent> opponentPool;

    public DQAgentMDP(List<Agent> opponentPool) {
        this.opponentPool = opponentPool;
        this.retailManager = new RetailMarketManager();
        this.reset();
    }

    public static void trainDQAgent(List<Agent> opponentPool) {
    	log("Setting up DeepQ training");
        DQAgentMDP mdp = new DQAgentMDP(opponentPool);
    	try {
            // record the training data in rl4j-data in a new folder
            DataManager manager = new DataManager();
            QLearningDiscreteDense<DQAgentState> dql = new QLearningDiscreteDense<DQAgentState>(mdp, QLNet, QLConfig, manager);
            //Learning<DQAgentState, Integer, DiscreteSpace, IDQN> dql  = new QLearningDiscreteDense<DQAgentState>(mdp, QLNet, QLConfig, manager);
            
            // enable some logging for debug purposes on toy mdp
            //mdp.setFetchable(dql);
            
            log("Training DeepQ");
            dql.train();
            DQNPolicy<DQAgentState> pol = (DQNPolicy<DQAgentState>) dql.getPolicy();

            log("Saving DeepQ policy");
            pol.save(Configuration.DQLEARNING_POLICY_FILENAME);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mdp.close();
    }

    @Override
    public void close() {

    }

    @Override
    public DiscreteSpace getActionSpace() {
        return actionSpace;
    }

    @Override
    public ObservationSpace<DQAgentState> getObservationSpace() {
        return observationSpace;
    }

    @Override
    public boolean isDone() {
    	//log("Checking if it is done!");
    	if(Observer.timeslot >= Configuration.TOTAL_TIME_SLOTS)
    		log("Full sim completed");
    	return Observer.timeslot >= Configuration.TOTAL_TIME_SLOTS;
        //return Observer.rounds >= Configuration.TRAININGROUNDS;
    }

    @Override
    public MDP<DQAgentState, Integer, DiscreteSpace> newInstance() {
        DQAgentMDP mdp = new DQAgentMDP(this.opponentPool);
        return mdp;
    }

    @Override
    public DQAgentState reset() {
    	//log("Inside reset(): rounds " + Observer.rounds + " ob.ts " + retailManager.ob.timeslot);
        retailManager = new RetailMarketManager();
    	//if(retailManager.ob.agentPool != null)
    	//retailManager.ob.agentPool.clear();
        agent = new DQAgent();
        retailManager.ob.agentPool.add(agent);
        retailManager.ob.agentPool.addAll(opponentPool);
        Observer.timeslot = 0;
        this.currentState = this.startingState = new DQAgentState(agent);
        return startingState;
    }

    @Override
    public StepReply<DQAgentState> step(Integer action) {
    	//System.out.println("Training: TS: " + Observer.timeslot +  " a " + action + " : " + TariffActions.a[action] + " profit " + agent.profit + " mktshare " + agent.marketShare);
    	if (action == TariffActions.action.INCREASE.ordinal()) // Increase
            agent.increase(retailManager.ob);
        else if (action == TariffActions.action.DEFECT.ordinal()) // Decrease
            agent.defect(retailManager.ob);
        else if (action == TariffActions.action.DEFECT2.ordinal()) // Defect
            agent.defect2(retailManager.ob);
        else if (action == TariffActions.action.INCREASE2.ordinal()) // Increase
        	agent.increase2(retailManager.ob);
        else // No Change
            agent.nochange();

        // Perform other agent policies
        for (Agent ag : this.opponentPool) {
            ag.publishTariff(retailManager.ob);
        }

        retailManager.updateAgentsMemory();

        // Run the market evaluation based on the previous action
        // then run the rest of the timeslots so the next call to
        // this function will be a publication cycle
        // Run for 6 TS
        for (int i = 0; i < Configuration.PUBLICATION_CYCLE; i++) {
            retailManager.customerTariffEvaluation();
            retailManager.updateAgentAccountings();
            Observer.timeslot++;
        }

        double reward = agent.profit - agent.prevprofit;
        DQAgentState nextState = new DQAgentState(agent);
        
        JSONObject info = new JSONObject("{}");
        return new StepReply<DQAgentState>(nextState, reward, this.isDone(), info);
    }

}