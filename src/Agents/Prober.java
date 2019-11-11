package Agents;

import Observer.Observer;
import Tariff.TariffAction;

/**
 * Prober: Starts with D,C,C and
 * then defects if the opponent has cooperated in the second and third move;
 * otherwise, it plays TFT.
 */
public class Prober extends Agent {
    public Prober() {
        super("Prober");
    }

    @Override
    public TariffAction makeAction(Observer ob) {
        if (ob.timeslot == 1) // Start with defection
            return TariffAction.DEFECT;
        else if (ob.timeslot == 7 || ob.timeslot == 13) // Coop
            return TariffAction.NOCHANGE;
        else if (coopCounter >= 2) // Other agent cooperated twice, so defect
            return TariffAction.DEFECT;
        else if (rivalActHistory[ob.timeslot-1] == TariffAction.DEFECT.index) { // other agent is defecting
            coopCounter = 0;
            return TariffAction.DEFECT;
        } else {// other agent is cooperating, So coop
            coopCounter++;
            return TariffAction.NOCHANGE;
        }
    }
}
