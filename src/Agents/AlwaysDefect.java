package Agents;

import Observer.Observer;
import Tariff.TariffAction;

public class AlwaysDefect extends Agent {
	public AlwaysDefect() {
        super("AlD", 12);
    }

    @Override
    public TariffAction makeAction(Observer ob) {
    	if(hitLow)
    		return TariffAction.NC;
    	else if((tariffPrice + TariffAction.D1.tariff) > ob.unitcost_pred)
    		return TariffAction.D1;
    	else if(tariffPrice > ob.unitcost_pred) {
    		hitLow = true;
    		return TariffAction.NC;
    	}
    	else
    		return TariffAction.NC;
    }
}
