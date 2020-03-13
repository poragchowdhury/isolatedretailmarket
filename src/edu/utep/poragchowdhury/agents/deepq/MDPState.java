package edu.utep.poragchowdhury.agents.deepq;

import java.util.ArrayList;
import java.util.List;

import org.deeplearning4j.rl4j.space.Encodable;

import edu.utep.poragchowdhury.agents.base.Agent;
import edu.utep.poragchowdhury.core.Configuration;
import edu.utep.poragchowdhury.simulation.Observer;
import edu.utep.poragchowdhury.simulation.TariffAction;

/**
 * Encodes the current state of a Deep Q-Learning Agent
 * @author Jose G. Perez
 */
public class MDPState implements Encodable {
    public static int AGENT_COUNT = 15;
    public Agent agent;
    public Observer ob;
    public int timeSlot;
    public double ppts;
    public double prevPubCycProfit;
    public int rivalPrevPubCycAction;
    // public double prevPubCycMarketShare;
    public int prevAction;
    public double prevHourUsage;
    public double curHourUsage;
    public double nextHourUsage;

    public MDPState(Agent agent, Observer ob) {
        this.agent = agent;
        this.ob = ob;

        this.timeSlot = ob.timeslot;
        this.ppts = (agent.profit / (double) (ob.timeslot + 1));
        // previous publication cycle's profit
        this.prevPubCycProfit = (ob.timeslot - 1 - Configuration.PUBLICATION_CYCLE >= 0)
                ? agent.profitHistory[ob.timeslot - 1 - Configuration.PUBLICATION_CYCLE]
                : (agent.init_revenue - agent.init_cost);

        // Rival's previous publication cycle action
        if (ob.timeslot - 1 - Configuration.PUBLICATION_CYCLE >= 0) {
            if (agent.rivalTariffHistory[ob.timeslot - 1
                    - Configuration.PUBLICATION_CYCLE] == agent.rivalTariffHistory[ob.timeslot - 1])
                rivalPrevPubCycAction = TariffAction.NC.index;
            else if (agent.rivalTariffHistory[ob.timeslot - 1
                    - Configuration.PUBLICATION_CYCLE] > agent.rivalTariffHistory[ob.timeslot - 1])
                rivalPrevPubCycAction = TariffAction.D1.index;
            else
                rivalPrevPubCycAction = TariffAction.I1.index;
        }

        // this.prevPubCycMarketShare = (ob.timeslot-1-Configuration.PUBLICATION_CYCLE
        // >= 0) ? agent.profitHistory[ob.timeslot-1-Configuration.PUBLICATION_CYCLE] :
        // agent.init_mkshare;

        this.prevHourUsage = ob.fcc.usage[ob.timeslot == 0 ? 0 : (ob.timeslot - 1) % 24];
        this.curHourUsage = ob.fcc.usage[ob.timeslot % 24];
        this.nextHourUsage = ob.fcc.usage[(ob.timeslot + 1) % 24];
    }

    public double[] one_hot(int i, int size) {
        double[] d = new double[size];
        d[i] = 1;
        return d;
    }

    @Override
    public double[] toArray() {

        int prevPubCycTS = ob.timeslot - 1 - Configuration.PUBLICATION_CYCLE;
        prevPubCycTS = prevPubCycTS >= 0 ? prevPubCycTS : 0;
        int prev2PubCycTS = ob.timeslot - 1 - (Configuration.PUBLICATION_CYCLE * 2);
        prev2PubCycTS = prev2PubCycTS >= 0 ? prev2PubCycTS : 0;

        double maxPossProfitPerGame = (Configuration.MAX_TARIFF_PRICE * Configuration.POPULATION
                * Configuration.TOTAL_TIME_SLOTS * ob.fcc.maxUsage);
        double maxPossProfitPerTS = (Configuration.MAX_TARIFF_PRICE * Configuration.POPULATION * ob.fcc.maxUsage);

        List<Double> features = new ArrayList<>();
        /* ===== NOTE: Add your features here ===== */
        /* 2 Time related features */
        // feature 1: timeslot

        features.add((double) timeSlot / Configuration.TOTAL_TIME_SLOTS);
        // feature 2: hourOfDay, correlates with the demand profile of customer : maxVal
        // = 24
        features.add((double) (timeSlot % 24 + 1) / 24.0);

        /* 11 Price related features */
        // feature 1: previous timeslot's profit
        features.add(agent.profit / maxPossProfitPerGame);
        // feature 2: ppts
        features.add(ppts / maxPossProfitPerTS);
        // feature 3: my previous TS tariff price
        features.add(agent.tariffPrice / Configuration.MAX_TARIFF_PRICE);

        // feature 4: my previous publication cycle's tariff price
        features.add(agent.tariffHistory[prevPubCycTS] / Configuration.MAX_TARIFF_PRICE);
        // feature 5: my previous 2 publication cycle's tariff price
        features.add(agent.tariffHistory[prev2PubCycTS] / Configuration.MAX_TARIFF_PRICE);

        // feature 6: opponent previous TS tariff price
        features.add(agent.rivalTariffPrice / Configuration.MAX_TARIFF_PRICE);
        // feature 7: opponenet's previous publication cycle's tariff price
        features.add(agent.rivalTariffHistory[prevPubCycTS] / Configuration.MAX_TARIFF_PRICE);
        // feature 8: opponenet's previous 2 publication cycle's tariff price
        features.add(agent.rivalTariffHistory[prev2PubCycTS] / Configuration.MAX_TARIFF_PRICE);

        // feature 9: previous timeslot cost
        features.add(agent.unitcost / agent.c_max);
        // feature 10: previous pubCyc cost
        features.add(agent.unitCostHistory[prevPubCycTS] / agent.c_max);
        // feature 11: previous 2 pubCyc cost
        features.add(agent.unitCostHistory[prev2PubCycTS] / agent.c_max);

        /* 10 Action related features */

        // feature 1~5: agent's previous action
        for (double d : one_hot(agent.previousAction.index, TariffAction.values().length))
            features.add(d);
        // feature 5~10: rival agent's previous action
        for (double d : one_hot(agent.rivalPreviousAction.index, TariffAction.values().length))
            features.add(d);

        for (double d : one_hot(agent.actHistory[prevPubCycTS], TariffAction.values().length))
            features.add(d);
        // feature 5~10: rival agent's previous action
        for (double d : one_hot(agent.rivalActHistory[prevPubCycTS], TariffAction.values().length))
            features.add(d);
        for (double d : one_hot(agent.actHistory[prev2PubCycTS], TariffAction.values().length))
            features.add(d);
        // feature 5~10: rival agent's previous action
        for (double d : one_hot(agent.rivalActHistory[prev2PubCycTS], TariffAction.values().length))
            features.add(d);

        /* 3 Market share related features */

        // feature 1: my previous TS market share
        features.add((double) agent.marketShare / Configuration.POPULATION);

        // feature 2: my previous publication cycle market share
        features.add((double) agent.marketShareHistory[prevPubCycTS] / Configuration.POPULATION);
        // feature 3: my previous 2 publication cycle market share
        features.add((double) agent.marketShareHistory[prev2PubCycTS] / Configuration.POPULATION);

        /* 3 Customer demand related features */
        // feature 1,2,3: customer demand features

        features.add(prevHourUsage / ob.fcc.maxUsage);
        features.add(curHourUsage / ob.fcc.maxUsage);
        features.add(nextHourUsage / ob.fcc.maxUsage);

        // for (double d : one_hot(agent.opponentID, AGENT_COUNT))
        // features.add(d);

        // Get all the features from the list
        // Convert to a double array for use in the MDP
        double[] result = new double[features.size()];
        for (int i = 0; i < features.size(); i++)
            result[i] = features.get(i);
        return result;
    }

}
