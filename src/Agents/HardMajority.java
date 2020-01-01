package Agents;

import Observer.Observer;
import Tariff.TariffAction;

public class HardMajority extends Agent {

    public HardMajority() {
        super("HardMJ");
    }

    @Override
    public TariffAction makeAction(Observer ob) {
    	
    	//System.out.println("HJ: ts" + ob.timeslot + " defectCounter " + defectCounter);
    	
        if (rivalActHistory[ob.timeslot-1] == TariffAction.D1.index || 
        		rivalActHistory[ob.timeslot-1] == TariffAction.D2.index) // other agent is defecting
            defectCounter++;
        else
            coopCounter++; // other agent is cooperating

        if (ob.timeslot == 1 || defectCounter >= coopCounter) // Defect on first timeslot
            return TariffAction.D1;

        return TariffAction.NC;

    }
}