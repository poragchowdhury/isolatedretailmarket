package edu.utep.poragchowdhury.agents;

import edu.utep.poragchowdhury.agents.base.Agent;
import edu.utep.poragchowdhury.simulation.Observer;
import edu.utep.poragchowdhury.simulation.TariffAction;

/**
 * Prober: Starts with D,C,C and
 * then defects if the opponent has cooperated in the second and third move;
 * otherwise, it plays TFT.
 */
public class Prober extends Agent {
    private int coopCounter = 0;

    public Prober() {
        super("Prober");
    }

    @Override
    public TariffAction makeAction(Observer ob) {
        if (ob.timeslot == 0) // Start with defection
            return TariffAction.DEFECT;
        else if (ob.timeslot == 1 || ob.timeslot == 2) // Coop
            return TariffAction.NOCHANGE;
        else if (coopCounter >= 2) // Other agent cooperated twice, so defect
            return TariffAction.DEFECT;
        else if (this.rivalPrevPrevPrice > this.rivalPrevPrice) { // other agent is defecting
            coopCounter = 0;
            return TariffAction.DEFECT;
        } else {// other agent is cooperating, So coop
            coopCounter++;
            return TariffAction.NOCHANGE;
        }
    }
}
