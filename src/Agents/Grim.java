package Agents;

import Configuration.Configuration;
import Observer.Observer;
import Tariff.TariffAction;

/*
 * Cooperates, until the opponent defects, and thereafter always defects. [FRI71]
 */

public class Grim extends Agent {
    public Grim() {
        super("Grim", 5);
    }

    @Override
    public TariffAction makeAction(Observer ob) {
    	
    	if (!booDefect) {
            // Rival Agent hasn't defected yet
        	if(ob.timeslot == 1)
        		return TariffAction.NC;
        	else {
        		if (this.rivalTariffHistory[ob.timeslot-1-Configuration.PUBLICATION_CYCLE] > this.rivalTariffHistory[ob.timeslot-1]) { 
        			// other agent is defecting this round
        	        booDefect = true;
                    return TariffAction.D1;
	            } else { // Agent hasn't defected: So cooperate
	                return TariffAction.NC;
	            }
        	}
        } 
    	else // Always Defect
    	{
    		if((tariffPrice + TariffAction.D1.tariff) > ob.unitcost_pred)
        		return TariffAction.D1;
        	else
        		return TariffAction.NC;
    	}
    }
    
    
    
}
