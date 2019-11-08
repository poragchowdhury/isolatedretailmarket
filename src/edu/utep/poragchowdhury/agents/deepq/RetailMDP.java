/**
 * Author: Jose G. Perez
 * Deep Q-Learning MDP for use with deeplearning4j to train
 */
package edu.utep.poragchowdhury.agents.deepq;

import java.util.List;
import java.util.logging.Logger;

import org.deeplearning4j.gym.StepReply;
import org.deeplearning4j.rl4j.mdp.MDP;
import org.deeplearning4j.rl4j.space.ArrayObservationSpace;
import org.deeplearning4j.rl4j.space.DiscreteSpace;
import org.deeplearning4j.rl4j.space.ObservationSpace;
import org.json.JSONObject;
import org.nd4j.linalg.api.ndarray.INDArray;

import edu.utep.poragchowdhury.agents.base.Agent;
import edu.utep.poragchowdhury.core.Configuration;
import edu.utep.poragchowdhury.simulation.RetailMarketManager;
import edu.utep.poragchowdhury.simulation.TariffAction;

public class RetailMDP implements MDP<MDPState, Integer, DiscreteSpace> {
    private static Logger log = Logger.getLogger("retailmarketmanager");
    public static final int NUM_ACTIONS = 3;
    public static final int NUM_OBSERVATIONS = new MDPState().toArray().length;

    public static DiscreteSpace ACTION_SPACE = new DiscreteSpace(NUM_ACTIONS);
    public static ObservationSpace<MDPState> OBSERVATION_SPACE = new ArrayObservationSpace<MDPState>(new int[] { NUM_OBSERVATIONS });

    private RetailMarketManager retailManager;
    private DQAgent agent;
    private List<Agent> opponentPool;

    public RetailMDP(List<Agent> opponentPool) {
        this.opponentPool = opponentPool;
        this.reset();
    }

    @Override
    public boolean isDone() {
        return retailManager.ob.timeslot >= Configuration.TOTAL_TIME_SLOTS;
    }

    private String H = "";
    private String D = "";

    @Override
    public StepReply<MDPState> step(Integer actionInt) {
        TariffAction action = TariffAction.valueOf(actionInt);

        double before = agent.profit;
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
            retailManager.customerTariffEvaluation();
            retailManager.updateAgentAccountings();
            retailManager.ob.timeslot++;
            retailManager.ob.updateAgentUnitCost();
        }
        double after = agent.profit;
        double d1 = (after - before) / (0.5f * Configuration.POPULATION * 7f);
        d1 -= 0.3;
        double d2 = (agent.marketShare / 100f);
        double reward = ((0.3 * d1) + (0.7 * d2));

        // double reward = after;
        // double reward = (after - before) / (0.5f * Configuration.POPULATION * 7f);

        // double reward = agent.profit - agent.prevprofit;

        // double reward = agent.profit - opponentPool.get(0).profit;

        H += agent.previousAction.toString().charAt(0);
        D += reward + ",";
        if (H.length() == 4)
            log.info(H);
        if (H.equals("NIII") || H.equals("DDDD"))
            log.info(H + " - " + D.substring(0, D.length() - 2));

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

        H = "";
        D = "";

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
