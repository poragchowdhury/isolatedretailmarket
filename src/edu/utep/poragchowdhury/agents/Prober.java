package edu.utep.poragchowdhury.agents;

import edu.utep.poragchowdhury.agents.base.Agent;
import edu.utep.poragchowdhury.agents.base.AgentID;
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
        super("Prober", AgentID.Prober);
    }

    @Override
    public TariffAction makeAction(Observer ob) {
        if (ob.timeslot == 1) // Start with defection
            return TariffAction.D1;
        else if (ob.timeslot == 7 || ob.timeslot == 13) // Coop
            return TariffAction.NC;
        else if (coopCounter >= 2) // Other agent cooperated twice, so defect
            return TariffAction.D1;
        else if (rivalActHistory[ob.timeslot - 1] == TariffAction.D1.index ||
                rivalActHistory[ob.timeslot - 1] == TariffAction.D2.index) { // other agent is defecting
            coopCounter = 0;
            return TariffAction.D1;
        } else {// other agent is cooperating, So coop
            coopCounter++;
            return TariffAction.NC;
        }
    }
}
