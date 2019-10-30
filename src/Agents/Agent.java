package Agents;

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
        boolean validTariff = newTariff < Configuration.MAX_TARIFF_PRICE && newTariff > ob.unitcost;

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

}
