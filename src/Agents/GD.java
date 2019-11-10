package Agents;

import java.util.Random;

import Observer.Observer;
import Tariff.TariffAction;

public class GD extends Agent {
    public GD() {
        super("GD");
    }

    public void updatePrevActionBelief() {
    	double myPrevTSRevenue = (tariffPrice*(marketShare/100));
    	double opPrevTSRevenue = (rivalPrevPrice*(1-(marketShare/100)));
    	double totalRevenue = (opPrevTSRevenue+opPrevTSRevenue);
    	actGDvalues[previousAction.index]=myPrevTSRevenue / totalRevenue;
    }
    
    @Override
    public TariffAction makeAction(Observer ob) {
    	
    	// Update beleif about actions
    	updatePrevActionBelief();
    	
    	int bestActionIndex = 0;
    	for(int i = 1; i < TariffAction.values().length; i++) {
    		if(actGDvalues[bestActionIndex] < actGDvalues[i])
    			bestActionIndex = i;
    	}
    	
    	return TariffAction.valueOf(bestActionIndex);
    }
}
