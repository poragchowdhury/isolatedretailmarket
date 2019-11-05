package edu.utep.poragchowdhury.agents;

import edu.utep.poragchowdhury.agents.base.Agent;
import edu.utep.poragchowdhury.simulation.Observer;
import edu.utep.poragchowdhury.simulation.TariffAction;

public class AlwaysSame extends Agent {

    public AlwaysSame() {
        super("AlzSame");
    }

    @Override
    public TariffAction makeAction(Observer ob) {
        return TariffAction.NOCHANGE;
    }
}
