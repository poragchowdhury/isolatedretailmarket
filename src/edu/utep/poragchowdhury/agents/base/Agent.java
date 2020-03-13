package edu.utep.poragchowdhury.agents.base;

import java.util.Arrays;
import java.util.Random;

import edu.utep.poragchowdhury.core.Configuration;
import edu.utep.poragchowdhury.simulation.Observer;
import edu.utep.poragchowdhury.simulation.TariffAction;

public abstract class Agent implements Cloneable {
    public abstract TariffAction makeAction(Observer ob) throws Exception;

    public double init_unitcost = 0.05;
    public double init_mkshare = 50.0;
    public double init_cost = 0.05 * Configuration.POPULATION / 2 * 8;
    public double init_revenue = Configuration.DEFAULT_TARIFF_PRICE * Configuration.POPULATION / 2 * 8;

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

    // Random walk cost variables
    // Random walk cost variables
    public double c_max = 0.15;
    public double c_min = 0.05;
    public double tr_min = 0.95;
    public double tr_max = 1 / 0.95;
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

    public int opponentID;

    /**
     * Constructs a new agent
     * @param agentName Name of the agent
     */
    public Agent(String agentName, int id) {
        this.name = agentName;
        this.id = id;
        this.reset();
        
        this.unitcost = Configuration.INITUNITCOST;
        this.c_max = Configuration.MAX_UNIT_COST;
        this.c_min = Configuration.MIN_UNIT_COST;
        this.tariffHistory = new double[Configuration.TOTAL_TIME_SLOTS];
        this.unitCostHistory = new double[Configuration.TOTAL_TIME_SLOTS];
        this.costHistory = new double[Configuration.TOTAL_TIME_SLOTS];
        this.profitHistory = new double[Configuration.TOTAL_TIME_SLOTS];
        this.marketShareHistory = new double[Configuration.TOTAL_TIME_SLOTS];
        this.actHistory = new int[Configuration.TOTAL_TIME_SLOTS];

        this.rivalTariffHistory = new double[Configuration.TOTAL_TIME_SLOTS];
        this.rivalActHistory = new int[Configuration.TOTAL_TIME_SLOTS];

    }

    /**
     * Resets all of the agent variables to their default values
     */
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
        unitcost = 0.05;
        actGDvalues = new double[TariffAction.values().length];
        bestResponseCount = 0;
        bestResponseCountErr = 0;
        wins = 0;

        Arrays.fill(costHistory, 0.0);
        Arrays.fill(profitHistory, 0.0);
        Arrays.fill(unitCostHistory, 0.0);
        Arrays.fill(tariffHistory, 0.0);
        Arrays.fill(actHistory, 0);
        Arrays.fill(marketShareHistory, 0.0);

        Arrays.fill(rivalTariffHistory, 0.0);
        Arrays.fill(rivalActHistory, 0);
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

        this.tariffPrice = newTariff;
        this.previousAction = action;
        this.tariffHistory[ob.timeslot] = this.tariffPrice;
    }

    /**
     * Copies all of this agent's fields to another agent
     * Note: The name is not copied
     * @param other Agent to receive field values
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
        other.unitcost = unitcost;
        other.opponentID = opponentID;

        if (ob.timeslot == 1) {
            System.arraycopy(actGDvalues, 0, other.actGDvalues, 0, other.actGDvalues.length);
            other.bestResponseCount = bestResponseCount;
            other.bestResponseCountErr = bestResponseCountErr;
            other.wins = wins;
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

    public void randomWalkUnitCost(int ts) {
        double new_unitcost = Math.min(this.c_max, Math.max(this.c_min, this.trend * this.unitcost));
        double new_trend = Math.max(this.tr_min, Math.min(this.tr_max, this.trend + getRandomValInRange(0.01)));
        if ((this.trend * this.unitcost) < this.c_min || (this.trend * this.unitcost) > this.c_max)
            this.trend = 1;
        else
            this.trend = new_trend;
        this.unitcost = new_unitcost;
        this.costHistory[ts] = this.unitcost;
    }

    /*
     * Generation random values between -max to +max
     */
    public double getRandomValInRange(double max) {
        int divisor = 100;
        while (max % 1 != 0) {
            max *= 10;
            divisor *= 10;
        }
        int maxbound = (int) max * 100;
        Random r = new Random();
        int randInt = r.nextInt(maxbound + 1);
        double val = (double) randInt / divisor;
        int coin = r.nextInt(2);
        if (coin == 0)
            val *= -1;
        return val;
    }

    public String getAllHistoryActions() {
        StringBuilder sb = new StringBuilder();
        for (int ts = 1; ts < Configuration.TOTAL_TIME_SLOTS; ts += Configuration.PUBLICATION_CYCLE) {
            String actName = TariffAction.valueOf(actHistory[ts]).name();
            // actName = actName.substring(0, 1) + (actHistory[ts] > 2 ? (actName.substring(actName.length() - 1)) : 0);
            sb.append(" " + actName + " ");
        }
        return sb.toString();
    }

    public String getHistoryByPubCyc(double[] history) {
        StringBuilder sb = new StringBuilder();
        for (int ts = 1; ts < Configuration.TOTAL_TIME_SLOTS; ts += Configuration.PUBLICATION_CYCLE)
            sb.append(" " + Math.round(history[ts] * 100) / 100f + " ");
        return sb.toString();
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
}
