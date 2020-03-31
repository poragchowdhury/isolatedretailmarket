package Agents;

import java.util.Random;

import Observer.Observer;
import Tariff.TariffAction;

public class ZI extends Agent {
    public ZI() {
        super("ZI", 0);
    }

    @Override
    public TariffAction makeAction(Observer ob) {
        Random r = new Random();
        int coin = r.nextInt(3);
        if (coin == 0) // Defect
        {
        	if((tariffPrice + TariffAction.D1.tariff) > ob.unitcost_pred)
        		return TariffAction.D1;
        	else
        		return TariffAction.NC;
        }
        else if(coin == 1) {
        	return TariffAction.NC;
        }
        else // Coop
            return TariffAction.I1;
    }
}
