package Agents;

import Observer.Observer;
import Tariff.TariffAction;

public class AlwaysDefect extends Agent {
	public boolean hitLow = false;
    public AlwaysDefect() {
        super("AlD", 0);
    }

    @Override
    public TariffAction makeAction(Observer ob) {
    	
    	if((tariffPrice + TariffAction.D1.tariff) > ob.unitcost_pred)
    		return TariffAction.D1;
    	else if(tariffPrice > ob.unitcost_pred)
    		return TariffAction.NC;
    	else
    		return TariffAction.I1;
    }
}
