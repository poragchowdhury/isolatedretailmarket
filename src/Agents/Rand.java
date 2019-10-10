package Agents;

import java.util.Random;

import Observer.Observer;

public class Rand extends Agent {
    public Rand() {
        super("Rand");
    }

    @Override
    public void publishTariff(Observer ob) {

        Random r = new Random();
        int coin = r.nextInt(2);
        if (coin == 0) // Defect
            defect(ob);
        else {
        } // Coop

        /* Tariff Check */
        tariffCheck(ob);
    }
}
