package Agents;

import Observer.Observer;

public class AlwaysIncrease extends Agent {
    public AlwaysIncrease() {
        // TODO Auto-generated constructor stub
        super("AlzIncz");
    }

    @Override
    public void publishTariff(Observer ob) {
        // System.out.println(name+" : marketshare " + marketShare);
        increase(ob);

        tariffCheck(ob); /* Tariff Check */
    }
}
