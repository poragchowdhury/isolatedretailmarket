package Agents;

import java.util.Arrays;
import java.util.Random;

import Configuration.Configuration;
import Observer.Observer;
import Tariff.TariffAction;

public abstract class Agent implements Cloneable {

	public String name;
	public int id;
	public double tariffPrice;
	public double rivalTariffPrice;
	public double revenue;
	public double marketShare;
	public double cost;
	public double profit;
	public double profitErr;

	public double tariffUtility;
	public TariffAction previousAction;
	public TariffAction rivalPreviousAction;
	public double[] actGDvalues;
	public double bestResponseCount;
	public double bestResponseCountErr;
	public double wins;

	public int defectCounter = 0;
	public int coopCounter = 0;
	public int punishCounter = 0;
	public boolean booDefect = false;

	// pavlov probability to increase+nochange
	public double pr = 1;
	public double prI = 1;

	// agent history by timeslot
	public double[] tariffHistory;
	public double[] unitCostHistory;
	public double[] costHistory;
	public double[] profitHistory;
	public double[] marketShareHistory;
	public int[] actHistory;

	public double[] rivalTariffHistory;
	public int[] rivalActHistory;
	
	public int opponentID;

	public abstract TariffAction makeAction(Observer ob) throws Exception;

	public Agent(String agentName, int id) {
		this.name = agentName;
		this.id = id;
		
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

	/**
	 * Copies all of this agent's fields to another agent Note: The name is not
	 * copied
	 * 
	 * @param other
	 *            Agent to receive field values
	 */
	public void copyTo(Agent other, Observer ob) {
		other.tariffPrice = tariffPrice;
		other.rivalTariffPrice = rivalTariffPrice;
		other.revenue = revenue;
		other.marketShare = marketShare;
		other.cost = cost;
		other.profit = profit;
		other.profitErr = profitErr;
		other.tariffUtility = tariffUtility;
		other.previousAction = previousAction;
		other.rivalPreviousAction = rivalPreviousAction;
		other.opponentID = opponentID;
		
		if(ob.timeslot == 1) {
			System.arraycopy(actGDvalues, 0, other.actGDvalues, 0, other.actGDvalues.length);
			other.bestResponseCount = bestResponseCount;
			other.bestResponseCountErr = bestResponseCountErr;
			other.wins = wins;
			other.defectCounter = defectCounter;
			other.punishCounter = punishCounter;
			other.coopCounter = coopCounter;
			other.booDefect = booDefect;
			other.pr = pr;
			other.prI = prI;
		}

		other.costHistory[ob.timeslot - 1] = costHistory[ob.timeslot - 1];
		other.profitHistory[ob.timeslot - 1] = profitHistory[ob.timeslot - 1];
		other.unitCostHistory[ob.timeslot - 1] = unitCostHistory[ob.timeslot - 1];
		other.tariffHistory[ob.timeslot - 1] = tariffHistory[ob.timeslot - 1];

		other.actHistory[ob.timeslot - 1] = actHistory[ob.timeslot - 1];
		other.marketShareHistory[ob.timeslot - 1] = marketShareHistory[ob.timeslot - 1];
		other.rivalTariffHistory[ob.timeslot - 1] = rivalTariffHistory[ob.timeslot - 1];
		other.rivalActHistory[ob.timeslot - 1] = rivalActHistory[ob.timeslot - 1];
	}

	public void reset() {
		tariffPrice = Configuration.DEFAULT_TARIFF_PRICE;
		rivalTariffPrice = Configuration.DEFAULT_TARIFF_PRICE;
		revenue = 0;
		marketShare = 0;
		cost = 0;
		profit = 0;
		profitErr = 0;
		tariffUtility = 0;
		previousAction = TariffAction.NC;
		rivalPreviousAction = TariffAction.NC;
		
		actGDvalues = new double[TariffAction.values().length];
		bestResponseCount = 0;
		bestResponseCountErr = 0;
		wins = 0;
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
//		boolean validTariff = newTariff <= Configuration.MAX_TARIFF_PRICE && newTariff > 0;
//		if (validTariff) {
			this.tariffPrice = newTariff;
			this.previousAction = action;
			this.tariffHistory[ob.timeslot] = this.tariffPrice;
//		}
//		else {
//			this.previousAction = TariffAction.NC;
//			this.tariffHistory[ob.timeslot] = this.tariffPrice;
//		}
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


	public String getAllHistoryActions() {
		StringBuilder sb = new StringBuilder();
		for (int ts = 1; ts < Configuration.TOTAL_TIME_SLOTS; ts += Configuration.PUBLICATION_CYCLE) {
			String actName = TariffAction.valueOf(actHistory[ts]).name();
			//actName = actName.substring(0, 1) + (actHistory[ts] > 2 ? (actName.substring(actName.length() - 1)) : 0);
			sb.append(" " + actName + " ");
		}
		return sb.toString();
	}
	
	public String getHistoryByPubCyc(double [] history) {
		StringBuilder sb = new StringBuilder();
		for (int ts = 1; ts < Configuration.TOTAL_TIME_SLOTS; ts += Configuration.PUBLICATION_CYCLE)
			sb.append(" " + Math.round(history[ts]*100)/100f + " ");
		return sb.toString();
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

}
