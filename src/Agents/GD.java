package Agents;

import java.util.Random;
import java.util.logging.Logger;

import Observer.Observer;
import Tariff.TariffAction;

public class GD extends Agent {
	private static Logger log = Logger.getLogger("retailmarketmanager");
	
	public GD() {
        super("GD", 2);
    }

    public void updatePrevActionBelief(Observer ob) {
    	double myPrevTSRevenue = ((tariffPrice-ob.unitcost_pred)*(marketShare/100));
    	double opPrevTSRevenue = ((rivalTariffPrice-ob.unitcost_pred)*(1-(marketShare/100)));
    	double totalRevenue = (myPrevTSRevenue+opPrevTSRevenue);
    	actGDvalues[previousAction.index]=myPrevTSRevenue / totalRevenue;
    	actGDvalues[rivalPreviousAction.index]=opPrevTSRevenue / totalRevenue;
    }
    
    @Override
    public TariffAction makeAction(Observer ob) {
    	
    	// Update beleif about actions
    	updatePrevActionBelief(ob);
    	
    	int bestActionIndex = 0;
    	
    	for(int i = 0; i < TariffAction.values().length; i++) {
    		//log.info(String.format("%.3f", actGDvalues[i]));
    		if(actGDvalues[bestActionIndex] < actGDvalues[i])
    			bestActionIndex = i;
    	}

    	return TariffAction.valueOf(bestActionIndex);
    }
}
