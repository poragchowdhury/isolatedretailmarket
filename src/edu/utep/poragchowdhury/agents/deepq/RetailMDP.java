/**
 * Author: Jose G. Perez
 * Deep Q-Learning MDP for use with deeplearning4j to train
 */
package edu.utep.poragchowdhury.agents.deepq;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.deeplearning4j.gym.StepReply;
import org.deeplearning4j.rl4j.mdp.MDP;
import org.deeplearning4j.rl4j.space.ArrayObservationSpace;
import org.deeplearning4j.rl4j.space.DiscreteSpace;
import org.deeplearning4j.rl4j.space.ObservationSpace;
import org.json.JSONObject;

import edu.utep.poragchowdhury.agents.base.Agent;
import edu.utep.poragchowdhury.core.Configuration;
import edu.utep.poragchowdhury.simulation.Observer;
import edu.utep.poragchowdhury.simulation.RetailMarketManager;
import edu.utep.poragchowdhury.simulation.TariffAction;

class Stat implements Comparable<Stat> {
    double N = 0;
    double COM = 0;

    public double calculateV() {
        return COM / N;
    }

    public int compareTo(Stat other) {
        return Double.compare(calculateV(), other.calculateV());
    }
}

public class RetailMDP implements MDP<MDPState, Integer, DiscreteSpace> {
    // private static Logger log = Logger.getLogger("retailmarketmanager");
    public static final int NUM_ACTIONS = 3;
    public static final int NUM_OBSERVATIONS = new MDPState(new DQAgent(), new Observer()).toArray().length;

    public static DiscreteSpace ACTION_SPACE = new DiscreteSpace(NUM_ACTIONS);
    public static ObservationSpace<MDPState> OBSERVATION_SPACE = new ArrayObservationSpace<MDPState>(new int[] { NUM_OBSERVATIONS });

    private RetailMarketManager retailManager;
    private DQAgent agent;
    private List<Agent> opponentPool;

    private String actionHistory = "";
    private double cummulativeReward = 0;
    public Map<String, Stat> statisticsMap = new HashMap<String, Stat>();

    public RetailMDP(List<Agent> opponentPool) {
        this.opponentPool = opponentPool;
        this.reset();
    }

    @Override
    public boolean isDone() {
        if (retailManager.ob.timeslot > 1 && agent.marketShare <= 30)
            return true;
        return retailManager.ob.timeslot >= Configuration.TOTAL_TIME_SLOTS;
    }

    @Override
    public StepReply<MDPState> step(Integer actionInt) {
        // Special case to simulate the 0th time-slot
        // Where both of the agents have the same tariff
        if (retailManager.ob.timeslot == 0) {
            retailManager.ob.updateAgentUnitCost();
            retailManager.customerTariffEvaluation();
            retailManager.updateAgentAccountings();
            retailManager.ob.timeslot++;
        }

        TariffAction action = TariffAction.valueOf(actionInt);
        // double before = agent.profit;
        agent.playAction(retailManager.ob, action);

        // Perform other agent policies
        for (Agent ag : this.opponentPool) {
            try {
                ag.publishTariff(retailManager.ob);
            } catch (Exception ex) {
                System.out.printf("[Agent:%s raised an exception while publishing a tariff]\n", ag.name);
                ex.printStackTrace();
            }
        }

        // Run the market evaluation based on the previous action
        // then run the rest of the timeslots so the next call to
        // this function will be a publication cycle
        for (int i = 0; i < Configuration.PUBLICATION_CYCLE; i++) {
            retailManager.ob.updateAgentUnitCost();
            retailManager.customerTariffEvaluation();
            retailManager.updateAgentAccountings();
            retailManager.ob.timeslot++;
        }
        double after = agent.profit;
        double reward = after;

        actionHistory += agent.previousAction.toString().charAt(0);
        cummulativeReward += reward;

        if (actionHistory.length() == Configuration.TOTAL_PUBLICATIONS_IN_A_GAME) {
            // log.info("Action: " + actionHistory + " CR: " + cummulativeReward);
            if (statisticsMap.containsKey(actionHistory)) {
                Stat s = statisticsMap.get(actionHistory);
                s.N += 1;
                s.COM += cummulativeReward;
            } else {
                Stat s = new Stat();
                s.N = 1;
                s.COM = cummulativeReward;
                statisticsMap.put(actionHistory, s);
            }
        }

        MDPState nextState = new MDPState(agent, retailManager.ob);

        JSONObject info = new JSONObject("{}");
        return new StepReply<MDPState>(nextState, reward, this.isDone(), info);
    }

    @Override
    public MDPState reset() {
        // Properly reset agent variables between training epochs.
        for (Agent ag : opponentPool)
            ag.reset();

        this.agent = new DQAgent();
        this.retailManager = new RetailMarketManager();

        retailManager.ob.agentPool.add(agent);
        retailManager.ob.agentPool.addAll(opponentPool);
        retailManager.ob.timeslot = 0;

        actionHistory = "";
        cummulativeReward = 0;

        return new MDPState(agent, retailManager.ob);
    }

    @Override
    public MDP<MDPState, Integer, DiscreteSpace> newInstance() {
        RetailMDP mdp = new RetailMDP(this.opponentPool);
        return mdp;
    }

    @Override
    public void close() {

    }

    @Override
    public DiscreteSpace getActionSpace() {
        return ACTION_SPACE;
    }

    @Override
    public ObservationSpace<MDPState> getObservationSpace() {
        return OBSERVATION_SPACE;
    }
}
