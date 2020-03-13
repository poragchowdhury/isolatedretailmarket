package edu.utep.poragchowdhury.agents;

import edu.utep.poragchowdhury.agents.base.Agent;
import edu.utep.poragchowdhury.agents.base.AgentID;
import edu.utep.poragchowdhury.simulation.Observer;
import edu.utep.poragchowdhury.simulation.TariffAction;
import org.jetbrains.annotations.NotNull;

public class AlwaysIncrease extends Agent {
    public AlwaysIncrease() {
        super("AlzIncz", AgentID.AlwaysIncrease);
    }

    @NotNull
    @Override
    public TariffAction makeAction(Observer ob) {
        return TariffAction.I1;
    }
}
