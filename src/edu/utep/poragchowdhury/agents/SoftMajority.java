package edu.utep.poragchowdhury.agents;

import edu.utep.poragchowdhury.agents.base.Agent;
import edu.utep.poragchowdhury.simulation.Observer;
import edu.utep.poragchowdhury.simulation.TariffAction;

public class SoftMajority extends Agent {
    private int defectCounter = 0;
    private int coopCounter = 0;

    public SoftMajority() {
        super("SoftMJ");
    }

    @Override
    public TariffAction makeAction(Observer ob) {
        if (this.rivalPrevPrevPrice > this.rivalPrevPrice) // other agent is defecting
            defectCounter++;
        else // other agent is cooperating
            coopCounter++;

        if (ob.timeslot == 0 || coopCounter >= defectCounter)
            return TariffAction.NOCHANGE;
        else // Defect
            return TariffAction.DEFECT;
    }
}