package edu.utep.poragchowdhury.agents;

import edu.utep.poragchowdhury.agents.base.Agent;
import edu.utep.poragchowdhury.agents.base.AgentID;
import edu.utep.poragchowdhury.simulation.Observer;
import edu.utep.poragchowdhury.simulation.TariffAction;
import org.jetbrains.annotations.NotNull;

public class AlwaysDefect extends Agent {
    public AlwaysDefect() {
        super("AlD", AgentID.AlwaysDefect);
    }

    @NotNull
    @Override
    public TariffAction makeAction(Observer ob) {
        return TariffAction.D1;
    }
}
