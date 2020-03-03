package Agents;

import Observer.Observer;
import Tariff.TariffAction;

public class HardMajority extends Agent {

    public HardMajority() {
        super("HardMJ", 6);
    }

    @Override
    public TariffAction makeAction(Observer ob) {
    	
    	//System.out.println("HJ: ts" + ob.timeslot + " defectCounter " + defectCounter);
    	
        if (rivalActHistory[ob.timeslot-1] == TariffAction.D1.index || 
        		rivalActHistory[ob.timeslot-1] == TariffAction.D2.index) // other agent is defecting
            defectCounter++;
        else
            coopCounter++; // other agent is cooperating

        if (ob.timeslot == 1 || defectCounter >= coopCounter) // Defect on first timeslot or if defect counter is higher than coop
        {
        	if((tariffPrice + TariffAction.D1.tariff) > ob.unitcost_pred)
        		return TariffAction.D1;
        	else
        		return TariffAction.NC;
        }

        return TariffAction.NC;

    }
}