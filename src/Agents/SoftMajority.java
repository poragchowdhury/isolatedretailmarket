package Agents;

import Observer.Observer;
import Tariff.TariffAction;

public class SoftMajority extends Agent {
    public SoftMajority() {
        super("SoftMJ", 11);
    }

    @Override
    public TariffAction makeAction(Observer ob) {
        if ((rivalActHistory[ob.timeslot-1] == TariffAction.D1.index) || 
        		(rivalActHistory[ob.timeslot-1] == TariffAction.D2.index)) // other agent is defecting
            defectCounter++;
        else // other agent is cooperating
            coopCounter++;

        if (ob.timeslot == 1 || coopCounter >= defectCounter)
            return TariffAction.NC;
        else // Defect
            return TariffAction.D1;
    }
}