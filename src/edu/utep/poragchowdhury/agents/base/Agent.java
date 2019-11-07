package edu.utep.poragchowdhury.agents.base;

import java.util.Random;

import edu.utep.poragchowdhury.core.Configuration;
import edu.utep.poragchowdhury.simulation.Observer;
import edu.utep.poragchowdhury.simulation.TariffAction;

public abstract class Agent implements Cloneable {
    public String name;
    public double prevtariffPrice;
    public double tariffPrice;
    public double rivalPrevPrice;
    public double rivalPrevPrevPrice;
    public double revenue;
    public double prevrevenue;
    public double marketShare;
    public double prevmarketShare;
    public double unitcost;
    public double cost;
    public double profit;
    public double prevprofit;
    public double tariffUtility;
    public TariffAction previousAction;

    // Random walk cost variables
    public double c_max = 0.12;
	public double c_min = 0.03;
	public double tr_min = 0.95;
	public double tr_max = 1/0.95;
	public double trend = 1;
	
    public abstract TariffAction makeAction(Observer ob) throws Exception;

    /**
     * Constructs a new agent
     * @param agentName Name of the agent
     */
    public Agent(String agentName) {
        this.name = agentName;
        this.reset();
        this.unitcost = Configuration.DEFAULT_UNITCOST;
    }

    /**
     * Resets all of the agent variables to their default values
     */
    public void reset() {
        prevtariffPrice = Configuration.DEFAULT_TARIFF_PRICE;
        tariffPrice = Configuration.DEFAULT_TARIFF_PRICE;
        rivalPrevPrice = Configuration.DEFAULT_TARIFF_PRICE;
        rivalPrevPrevPrice = Configuration.DEFAULT_TARIFF_PRICE;
        revenue = 0;
        prevrevenue = 0;
        marketShare = 0;
        prevmarketShare = 0;
        unitcost = Configuration.DEFAULT_UNITCOST;
        profit = 0;
        prevprofit = 0;
        tariffUtility = 0;
        previousAction = TariffAction.NOCHANGE;
    }

    /**
     * Tells you whether the opponent agent is cooperating based
     * on its previous tariff price and current price
     * @param rivalCurPrice Current tariff price of the opponent
     * @return Whether the opponent is cooperating
     */
    public boolean isOtherAgentCoop(double rivalCurPrice) {
        return rivalPrevPrice >= this.rivalPrevPrevPrice;
    }

    /**
     * Queries the agent to create its next action and then performs it
     * @param ob Current observer of the retail market
     */
    public final void publishTariff(Observer ob) throws Exception {
        TariffAction action = makeAction(ob);
        playAction(ob, action);
    }

    /**
     * Performs the given action and actually modifies the tariff price
     * @param ob Current observer of the retail market
     * @param action Action to perform
     */
    public final void playAction(Observer ob, TariffAction action) {
        double tariffChange = action.tariff;
        double newTariff = this.tariffPrice + tariffChange;
        boolean validTariff = newTariff < Configuration.MAX_TARIFF_PRICE && newTariff > this.unitcost;

        if (validTariff) {
            this.prevtariffPrice = this.tariffPrice;
            this.tariffPrice = newTariff;
            this.previousAction = action;
        }
    }

    /**
     * Copies all of this agent's fields to another agent
     * Note: The name is not copied
     * @param other Agent to receive field values
     */
    public void copyTo(Agent other) {
        other.prevtariffPrice = prevtariffPrice;
        other.tariffPrice = tariffPrice;
        other.rivalPrevPrice = rivalPrevPrice;
        other.rivalPrevPrevPrice = rivalPrevPrevPrice;
        other.revenue = revenue;
        other.prevrevenue = prevrevenue;
        other.marketShare = marketShare;
        other.prevmarketShare = prevmarketShare;
        other.cost = cost;
        other.profit = profit;
        other.prevprofit = prevprofit;
        other.tariffUtility = tariffUtility;
        other.previousAction = previousAction;
    }

    /**
     * Checks if the given object is the same agent
     * Only compares agent names
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Agent) {
            Agent other = (Agent) obj;
            return other.name.equals(this.name);
        }
        return super.equals(obj);
    }

    @Override
    public String toString() {
        return this.name;
    }

    @Override
    public Agent clone() {
        Agent c = null;
        try {
            // Shallow copy
            c = (Agent) super.clone();
            // Deep copy
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return c;
    }
    
    public void randomWalkCost(int ts) {
		double new_cost = Math.min(this.c_max, Math.max(this.c_min, this.trend*this.cost));
		double new_trend = Math.max(this.tr_min, Math.min(this.tr_max, this.trend+getRandomValInRange(0.01)));
		if((this.trend * this.cost) < this.c_min || (this.trend * this.cost) > this.c_max)
			this.trend = 1;
		else
			this.trend = new_trend;
		this.cost = new_cost;
    }
    
	/*
	 * Generation random values between -max to +max
	 * */
	public double getRandomValInRange(double max) {
		int divisor = 100;
		while(max % 1 != 0) {
			max *= 10;
			divisor *= 10;
		}
		int maxbound = (int) max*100;
		Random r = new Random();
		int randInt = r.nextInt(maxbound+1);
		double val = (double) randInt/divisor;
		int coin = r.nextInt(2);
		if(coin == 0)
			val *= -1;
		return val;
	}

}
