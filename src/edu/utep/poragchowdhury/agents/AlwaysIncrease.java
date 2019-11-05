package edu.utep.poragchowdhury.agents;

import edu.utep.poragchowdhury.agents.base.Agent;
import edu.utep.poragchowdhury.simulation.Observer;
import edu.utep.poragchowdhury.simulation.TariffAction;

public class AlwaysIncrease extends Agent {
    public AlwaysIncrease() {
        // TODO Auto-generated constructor stub
        super("AlzIncz");
    }

    @Override
    public TariffAction makeAction(Observer ob) {
        return TariffAction.INCREASE;
    }
}
