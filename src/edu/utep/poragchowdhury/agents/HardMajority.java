package edu.utep.poragchowdhury.agents;

import edu.utep.poragchowdhury.agents.base.Agent;
import edu.utep.poragchowdhury.agents.base.AgentID;
import edu.utep.poragchowdhury.simulation.Observer;
import edu.utep.poragchowdhury.simulation.TariffAction;

public class HardMajority extends Agent {
    public int defectCounter = 0;
    public int coopCounter = 0;

    public HardMajority() {
        super("HardMJ", AgentID.HardMajority);
    }

    @Override
    public TariffAction makeAction(Observer ob) {
        if (rivalActHistory[ob.timeslot - 1] == TariffAction.D1.index || rivalActHistory[ob.timeslot - 1] == TariffAction.D2.index) // other agent is defecting
            defectCounter++;
        else
            coopCounter++; // other agent is cooperating

        if (ob.timeslot == 1 || defectCounter >= coopCounter) // Defect on first timeslot
            return TariffAction.D1;

        return TariffAction.NC;
    }
}