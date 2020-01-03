package Agents;

import java.util.Random;

import Observer.Observer;
import Tariff.TariffAction;

public class ZI extends Agent {
    public ZI() {
        super("ZI", 13);
    }

    @Override
    public TariffAction makeAction(Observer ob) {
        Random r = new Random();
        int coin = r.nextInt(3);
        if (coin == 0) // Defect
            return TariffAction.NC;
        else if(coin == 1)
        	return TariffAction.I1;
        else // Coop
            return TariffAction.D1;
    }
}
