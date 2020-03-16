package Agents;

import java.util.Random;

import Configuration.Configuration;
import Observer.Observer;
import Tariff.TariffAction;

public class NaiveIncrease extends Agent {
    public NaiveIncrease() {
        // TODO Auto-generated constructor stub
        super("NvI", 15);
    }

    @Override
    public TariffAction makeAction(Observer ob) {
    	double increasePr = 10;
        Random r = new Random(100);
        double coin = r.nextInt();
        if (coin < increasePr) // defect
        {
        	if((tariffPrice + TariffAction.I1.tariff) < Configuration.MAX_TARIFF_PRICE)
        		return TariffAction.I1;
        	else
        		return TariffAction.NC;
        }
        return TariffAction.NC;
    }
}
