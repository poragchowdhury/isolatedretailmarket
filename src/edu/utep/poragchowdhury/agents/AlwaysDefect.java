package edu.utep.poragchowdhury.agents;

import edu.utep.poragchowdhury.agents.base.Agent;
import edu.utep.poragchowdhury.agents.base.AgentID;
import edu.utep.poragchowdhury.simulation.Observer;
import edu.utep.poragchowdhury.simulation.TariffAction;

public class AlwaysDefect extends Agent {
    public AlwaysDefect() {
        super("AlD", AgentID.AlwaysDefect);
    }

    @Override
    public TariffAction makeAction(Observer ob) {
        return TariffAction.D1;
    }
}
