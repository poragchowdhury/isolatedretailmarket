package edu.utep.poragchowdhury.agents;

import edu.utep.poragchowdhury.agents.base.Agent;
import edu.utep.poragchowdhury.agents.base.AgentID;
import edu.utep.poragchowdhury.simulation.Observer;
import edu.utep.poragchowdhury.simulation.TariffAction;
import org.jetbrains.annotations.NotNull;

public class AlwaysSame extends Agent {
    public AlwaysSame() {
        super("AlzSame", AgentID.AlwaysSame);
    }

    @NotNull
    @Override
    public TariffAction makeAction(Observer ob) {
        return TariffAction.NC;
    }
}
