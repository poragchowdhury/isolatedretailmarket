package edu.utep.poragchowdhury.agents;

import edu.utep.poragchowdhury.agents.base.Agent;
import edu.utep.poragchowdhury.agents.base.AgentID;
import edu.utep.poragchowdhury.simulation.Observer;
import edu.utep.poragchowdhury.simulation.TariffAction;

public class AlwaysIncrease extends Agent {
    public AlwaysIncrease() {
        super("AlzIncz", AgentID.AlwaysIncrease);
    }

    @Override
    public TariffAction makeAction(Observer ob) {
        return TariffAction.I1;
    }
}
