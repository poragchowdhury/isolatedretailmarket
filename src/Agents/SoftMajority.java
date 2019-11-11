package Agents;

import Observer.Observer;
import Tariff.TariffAction;

public class SoftMajority extends Agent {
    public SoftMajority() {
        super("SoftMJ");
    }

    @Override
    public TariffAction makeAction(Observer ob) {
        if ((rivalActHistory[ob.timeslot-1] == TariffAction.DEFECT.index)) // other agent is defecting
            defectCounter++;
        else // other agent is cooperating
            coopCounter++;

        if (ob.timeslot == 1 || coopCounter >= defectCounter)
            return TariffAction.NOCHANGE;
        else // Defect
            return TariffAction.DEFECT;
    }
}