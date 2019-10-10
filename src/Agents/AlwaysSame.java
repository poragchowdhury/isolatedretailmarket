package Agents;

import Observer.Observer;

public class AlwaysSame extends Agent {

    public AlwaysSame() {
        super("AlzSame");
    }

    @Override
    public void publishTariff(Observer ob) {
        /* Tariff Check */
        // System.out.println(name+" : marketshare " + marketShare);
        tariffCheck(ob);
    }
}
