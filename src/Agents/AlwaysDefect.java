package Agents;

import Observer.Observer;

public class AlwaysDefect extends Agent {
    public AlwaysDefect() {
        super("AlD");
    }

    @Override
    public void publishTariff(Observer ob) {
        // System.out.println(name+" : marketshare " + marketShare);
        defect(ob);

        tariffCheck(ob); /* Tariff Check */
    }
}
