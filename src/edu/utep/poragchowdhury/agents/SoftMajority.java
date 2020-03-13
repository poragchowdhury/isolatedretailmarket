package edu.utep.poragchowdhury.agents;

import edu.utep.poragchowdhury.agents.base.Agent;
import edu.utep.poragchowdhury.agents.base.AgentID;
import edu.utep.poragchowdhury.simulation.Observer;
import edu.utep.poragchowdhury.simulation.TariffAction;

public class SoftMajority extends Agent {
    private int defectCounter = 0;
    private int coopCounter = 0;

    public SoftMajority() {
        super("SoftMJ", AgentID.SoftMajority);
    }

    @Override
    public TariffAction makeAction(Observer ob) {
        if ((rivalActHistory[ob.timeslot - 1] == TariffAction.D1.index) ||
                (rivalActHistory[ob.timeslot - 1] == TariffAction.D2.index)) // other agent is defecting
            defectCounter++;
        else // other agent is cooperating
            coopCounter++;

        if (ob.timeslot == 1 || coopCounter >= defectCounter)
            return TariffAction.NC;
        else // Defect
            return TariffAction.D1;
    }
}