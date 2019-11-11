package Agents;

import java.util.Arrays;
import java.util.Random;

import Configuration.Configuration;
import Observer.Observer;
import Tariff.TariffAction;

public abstract class Agent {
	
	// Initial profit, cost, marketshare, unitcost
	public double init_unitcost = 0.05;
	public double init_mkshare = 50.0;
	public double init_cost = 0.05*Configuration.POPULATION/2*8;
	public double init_revenue = Configuration.DEFAULT_TARIFF_PRICE*Configuration.POPULATION/2*8;
	
	public String name;
	public double tariffPrice;
	public double rivalTariffPrice;
	public double revenue;
	public double marketShare;
	public double cost;
	public double profit;

	public double tariffUtility;
	public TariffAction previousAction;
	public TariffAction rivalPreviousAction;
	public double [] actGDvalues;
	public double bestResponseCount;

    public int defectCounter = 0;
    public int coopCounter = 0;
    public int punishCounter = 0;
    public boolean booDefect = false;

    // pavlov probability to increase+nochange
	public double pr = 1; 
	public double prI = 1;

    
	// Random walk cost variables
	public double c_max = 0.12;
	public double c_min = 0.03;
	public double tr_min = 0.95;
	public double tr_max = 1/0.95;
	public double unitcost = 0.05;
	public double trend = 1;

	
	// agent history by timeslot
	public double[] tariffHistory;
	public double[] unitCostHistory;
	public double[] costHistory;
	public double[] profitHistory;
	public double[] marketShareHistory;
	public int[] actHistory;
	
	public double[] rivalTariffHistory;
	public int[] rivalActHistory;

	public abstract TariffAction makeAction(Observer ob) throws Exception;

	public Agent(String agentName) {
		this.name = agentName;
		
		this.tariffHistory = new double[Configuration.TOTAL_TIME_SLOTS];
		this.unitCostHistory = new double[Configuration.TOTAL_TIME_SLOTS];
		this.costHistory = new double[Configuration.TOTAL_TIME_SLOTS];
		this.profitHistory = new double[Configuration.TOTAL_TIME_SLOTS];
		this.marketShareHistory = new double[Configuration.TOTAL_TIME_SLOTS];
		this.actHistory = new int[Configuration.TOTAL_TIME_SLOTS];
		
		this.rivalTariffHistory = new double[Configuration.TOTAL_TIME_SLOTS];
		this.rivalActHistory = new int[Configuration.TOTAL_TIME_SLOTS];
		
		this.reset();
	}

	public void reset() {
		tariffPrice = Configuration.DEFAULT_TARIFF_PRICE;
		rivalTariffPrice = Configuration.DEFAULT_TARIFF_PRICE;
		revenue = 0;
		marketShare = 0;
		cost = 0;
		profit = 0;
		tariffUtility = 0;
		previousAction = TariffAction.NOCHANGE;
		rivalPreviousAction = TariffAction.NOCHANGE;
		unitcost = 0.05;
		actGDvalues = new double[TariffAction.values().length];
		bestResponseCount = 0;
	    defectCounter = 0;
	    punishCounter = 0;
	    coopCounter = 0;
	    booDefect = false;
	    pr = 1;
	    prI = 1;
	    
		Arrays.fill(costHistory, 0.0);
		Arrays.fill(profitHistory, 0.0);
		Arrays.fill(unitCostHistory, 0.0);
		Arrays.fill(tariffHistory, 0.0);
		Arrays.fill(actHistory, 0);
		Arrays.fill(marketShareHistory, 0.0);
		
		Arrays.fill(rivalTariffHistory, 0.0);
		Arrays.fill(rivalActHistory, 0);
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
			this.tariffPrice = newTariff;
			this.previousAction = action;
			this.tariffHistory[ob.timeslot] = this.tariffPrice;
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

	public void randomWalkUnitCost(int ts) {
		double new_unitcost = Math.min(this.c_max, Math.max(this.c_min, this.trend*this.unitcost));
		double new_trend = Math.max(this.tr_min, Math.min(this.tr_max, this.trend+getRandomValInRange(0.01)));
		if((this.trend * this.unitcost) < this.c_min || (this.trend * this.unitcost) > this.c_max)
			this.trend = 1;
		else
			this.trend = new_trend;
		this.unitcost = new_unitcost;
		this.costHistory[ts] = this.unitcost;
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

	public String getAllHistoryActions() {
		StringBuilder sb = new StringBuilder(name);
		for(int ts = 1; ts < Configuration.TOTAL_TIME_SLOTS; ts+=Configuration.PUBLICATION_CYCLE) {
			String actName = TariffAction.valueOf(actHistory[ts]).name();
			actName = actName.substring(0, 1) + (actHistory[ts] > 2 ? (actName.substring(actName.length()-1)) : 0);
			sb.append(" "+ actName + " ");
		}
		return sb.toString();
	}

}
