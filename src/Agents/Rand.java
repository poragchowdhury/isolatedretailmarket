package Agents;

import java.util.Random;

import Observer.Observer;
import Tariff.TariffAction;

public class Rand extends Agent {
    public Rand() {
        super("Rand");
    }

    @Override
    public TariffAction makeAction(Observer ob) {
        Random r = new Random();
        int coin = r.nextInt(2);
        if (coin == 0) // Defect
            return TariffAction.DEFECT;
        else // Coop
            return TariffAction.NOCHANGE;
    }
}
