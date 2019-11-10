package Agents;

import java.util.Random;

import Configuration.Configuration;
import Observer.Observer;
import Tariff.TariffAction;

public abstract class Agent {
	public String name;
	public double prevtariffPrice;
	public double tariffPrice;
	public double rivalPrevPrice;
	public double rivalPrevPrevPrice;
	public double revenue;
	public double prevrevenue;
	public double marketShare;
	public double prevmarketShare;
	public double cost;
	public double profit;
	public double prevprofit;
	public double tariffUtility;
	public TariffAction previousAction;
	public String actionHistory;
	public double [] actGDvalues;
	public double bestResponseCount;
	
	// Random walk cost variables
	public double c_max = 0.12;
	public double c_min = 0.03;
	public double tr_min = 0.95;
	public double tr_max = 1/0.95;
	public double unitcost = 0.05;
	public double trend = 1;


	public abstract TariffAction makeAction(Observer ob) throws Exception;

	public Agent(String agentName) {
		this.name = agentName;
		this.reset();
	}

	public void reset() {
		prevtariffPrice = Configuration.DEFAULT_TARIFF_PRICE;
		tariffPrice = Configuration.DEFAULT_TARIFF_PRICE;
		rivalPrevPrice = Configuration.DEFAULT_TARIFF_PRICE;
		rivalPrevPrevPrice = Configuration.DEFAULT_TARIFF_PRICE;
		revenue = 0;
		prevrevenue = 0;
		marketShare = 0;
		prevmarketShare = 0;
		cost = 0;
		profit = 0;
		prevprofit = 0;
		tariffUtility = 0;
		previousAction = TariffAction.NOCHANGE;
		unitcost = 0.05;
		actionHistory = "";
		actGDvalues = new double[TariffAction.values().length];
		bestResponseCount = 0;
	}

	public boolean isOtherAgentCoop(double rivalCurPrice) {
		return rivalPrevPrice >= this.rivalPrevPrevPrice;
	}

	public final void publishTariff(Observer ob) throws Exception {
		TariffAction action = makeAction(ob);
		playAction(ob, action);
	}

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

	@Override
	public String toString() {
		return this.name;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Agent) {
			Agent other = (Agent) obj;
			return other.name.equals(this.name);
		}
		return super.equals(obj);
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
