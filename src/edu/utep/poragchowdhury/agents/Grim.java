package edu.utep.poragchowdhury.agents;

import edu.utep.poragchowdhury.agents.base.Agent;
import edu.utep.poragchowdhury.agents.base.AgentID;
import edu.utep.poragchowdhury.core.Configuration;
import edu.utep.poragchowdhury.simulation.Observer;
import edu.utep.poragchowdhury.simulation.TariffAction;

/*
 * Cooperates, until the opponent defects, and thereafter always defects. [FRI71]
 */

public class Grim extends Agent {
    public boolean booDefect = false;

    public Grim() {
        super("Grim", AgentID.Grim);
    }

    @Override
    public TariffAction makeAction(Observer ob) {
        if (!booDefect) {
            // Rival Agent hasn't defected yet
            if (ob.timeslot == 1)
                return TariffAction.NC;
            else {
                if (this.rivalTariffHistory[ob.timeslot - 1 - Configuration.PUBLICATION_CYCLE] > this.rivalTariffHistory[ob.timeslot - 1]) {
                    // other agent is defecting this round
                    booDefect = true;
                    return TariffAction.D1;
                } else { // Agent hasn't defected: So cooperate
                    return TariffAction.NC;
                }
            }
        } else // Always Defect
            return TariffAction.D1;
    }
}
