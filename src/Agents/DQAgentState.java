package Agents;

import java.util.ArrayList;
import java.util.List;

import org.deeplearning4j.rl4j.space.Encodable;

import Observer.Observer;
import Tariff.TariffAction;
import Configuration.Configuration;

/**
 * Encodes the current state of a Deep Q-Learning Agent
 * @author Jose G. Perez
 */
class DQAgentState implements Encodable {
    public Agent agent;
    public Observer ob;
    public int timeSlot;
    public long ppts;
    public double prevPubCycProfit;
    public double prevTSProfit;
    public int rivalPrevPubCycAction;
    public double prevTSMarketShare;
    public double prevPubCycMarketShare;
    public int prevAction;
    public double prevHourUsage;
    public double curHourUsage;
    public double nextHourUsage;
    
    public DQAgentState() {
        this(new DQAgent(), new Observer());
    }

    public DQAgentState(Agent agent, Observer ob) {
        this.agent = agent;
        this.ob = ob;

        this.timeSlot = ob.timeslot;
        this.ppts = (long) (agent.profit / (double) (ob.timeslot+1));
        // previous timeslot's profit
        this.prevTSProfit = agent.profit;
        // previous publication cycle's profit
        this.prevPubCycProfit = (ob.timeslot-1-Configuration.PUBLICATION_CYCLE >= 0) ? agent.profitHistory[ob.timeslot-1-Configuration.PUBLICATION_CYCLE] : (agent.init_revenue-agent.init_cost); 
        
        // Rival's previous publication cycle action
        if(ob.timeslot-1-Configuration.PUBLICATION_CYCLE >= 0) {
	        if (agent.rivalTariffHistory[ob.timeslot-1-Configuration.PUBLICATION_CYCLE] 
	        		== agent.rivalTariffHistory[ob.timeslot-1])
	            rivalPrevPubCycAction = TariffAction.NOCHANGE.index;
	        else if (agent.rivalTariffHistory[ob.timeslot-1-Configuration.PUBLICATION_CYCLE] 
	        		> agent.rivalTariffHistory[ob.timeslot-1])
	        	rivalPrevPubCycAction = TariffAction.DEFECT.index;
	        else
	        	rivalPrevPubCycAction = TariffAction.INCREASE.index;
        }
        
        
        this.prevTSMarketShare = agent.marketShare;
        this.prevPubCycMarketShare = (ob.timeslot-1-Configuration.PUBLICATION_CYCLE >= 0) ? agent.profitHistory[ob.timeslot-1-Configuration.PUBLICATION_CYCLE] : agent.init_mkshare;
        
        this.prevHourUsage = ob.fcc.usage[ob.timeslot == 0? 0:(ob.timeslot-1)%24];
        this.curHourUsage = ob.fcc.usage[ob.timeslot%24];
        this.nextHourUsage = ob.fcc.usage[(ob.timeslot+1)%24];
    }

    public double[] one_hot(int i) {
        double[] d = new double[TariffAction.values().length];
        d[i] = 1;
        return d;
    }

    @Override
    public double[] toArray() {
        List<Double> features = new ArrayList<>();
        /* ===== NOTE: Add your features here ===== */
        // feature 1: timeslot
        features.add((double) timeSlot / Configuration.TOTAL_TIME_SLOTS);
        // feature 2: current profit
        features.add(agent.profit/(Configuration.MAX_TARIFF_PRICE*Configuration.POPULATION*Configuration.TOTAL_TIME_SLOTS*ob.fcc.maxUsage));
        // feature 3: ppts
        features.add((double) ppts/(Configuration.MAX_TARIFF_PRICE*Configuration.POPULATION*ob.fcc.maxUsage));
        // feature 4: previous timeslot's profit
        features.add(prevTSProfit/(Configuration.MAX_TARIFF_PRICE*Configuration.POPULATION*Configuration.TOTAL_TIME_SLOTS*ob.fcc.maxUsage));
        // feature 5,6,7,8,9: agent's previous action
        for (double d : one_hot(agent.previousAction.index))
            features.add(d);
        // feature 10,11,12,13,14: rival agent's previous action
        for (double d : one_hot(agent.rivalPreviousAction.index))
            features.add(d);
        // feature 15: my previous TS market share
        features.add((double) prevTSMarketShare/Configuration.POPULATION);
        // feature 16: my previous market share
        features.add((double) prevPubCycMarketShare/Configuration.POPULATION);
        // feature 17, 18, 19: my customer demand previous, current and next hour
        features.add(prevHourUsage/ob.fcc.maxUsage);
        features.add(curHourUsage/ob.fcc.maxUsage);
        features.add(nextHourUsage/ob.fcc.maxUsage);
        // feature 20: my previous TS tariff price
        features.add(agent.tariffPrice/Configuration.MAX_TARIFF_PRICE);
        // feature 21: opponent previous TS tariff price
        features.add(agent.rivalTariffPrice/Configuration.MAX_TARIFF_PRICE);
        // feature 22: my previous publication cycle's tariff price
        double myPrevPubCycTariffPrice = (ob.timeslot - 1 - Configuration.PUBLICATION_CYCLE) >= 0 ? agent.tariffHistory[ob.timeslot - 1 - Configuration.PUBLICATION_CYCLE] : Configuration.DEFAULT_TARIFF_PRICE;
        features.add(myPrevPubCycTariffPrice/Configuration.MAX_TARIFF_PRICE);
        // feature 23: opponenet's previous publication cycle's tariff price
        double rivalPrevPubCycPrice = (ob.timeslot - 1 - Configuration.PUBLICATION_CYCLE) >= 0 ? agent.rivalTariffHistory[ob.timeslot - 1 - Configuration.PUBLICATION_CYCLE] : Configuration.DEFAULT_TARIFF_PRICE;
        features.add(rivalPrevPubCycPrice/Configuration.MAX_TARIFF_PRICE);

        
        
        
        
        
        
        // Get all the features from the list
        // Convert to a double array for use in the MDP
        double[] result = new double[features.size()];
        for (int i = 0; i < features.size(); i++)
            result[i] = features.get(i);
        return result;
    }

}
