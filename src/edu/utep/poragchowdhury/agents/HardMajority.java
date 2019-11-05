package edu.utep.poragchowdhury.agents;

import edu.utep.poragchowdhury.agents.base.Agent;
import edu.utep.poragchowdhury.simulation.Observer;
import edu.utep.poragchowdhury.simulation.TariffAction;

public class HardMajority extends Agent {
    public int defectCounter = 0;
    public int coopCounter = 0;

    public HardMajority() {
        super("HardMJ");
    }

    @Override
    public TariffAction makeAction(Observer ob) {
        if (this.rivalPrevPrevPrice > this.rivalPrevPrice) // other agent is defecting
            defectCounter++;
        else
            coopCounter++; // other agent is cooperating

        if (ob.timeslot == 0 || defectCounter >= coopCounter) // Defect on first timeslot
            return TariffAction.DEFECT;

        return TariffAction.NOCHANGE;

    }
}