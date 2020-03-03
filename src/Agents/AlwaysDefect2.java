package Agents;

import Observer.Observer;
import Tariff.TariffAction;

public class AlwaysDefect2 extends Agent {
    public AlwaysDefect2() {
        super("AlD2", 0);
    }

    @Override
    public TariffAction makeAction(Observer ob) {
    	if((tariffPrice + TariffAction.D2.tariff) > ob.unitcost_pred)
    		return TariffAction.D2;
    	else if((tariffPrice + TariffAction.D1.tariff) > ob.unitcost_pred)
    		return TariffAction.D1;
    	else if(tariffPrice > ob.unitcost_pred)
    		return TariffAction.NC;
    	else if((tariffPrice + TariffAction.I1.tariff) > ob.unitcost_pred)
    		return TariffAction.I1;
    	else
    		return TariffAction.I2;
    }
}
