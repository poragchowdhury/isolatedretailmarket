package edu.utep.poragchowdhury.agents;

import java.util.Random;

import edu.utep.poragchowdhury.agents.base.Agent;
import edu.utep.poragchowdhury.agents.base.AgentID;
import edu.utep.poragchowdhury.simulation.Observer;
import edu.utep.poragchowdhury.simulation.TariffAction;
import org.jetbrains.annotations.NotNull;

public class Rand extends Agent {
    public Rand() {
        super("Rand", AgentID.Rand);
    }

    @NotNull
    @Override
    public TariffAction makeAction(Observer ob) {
        Random r = new Random();
        int coin = r.nextInt(2);
        if (coin == 0) // Defect
            return TariffAction.D1;
        else // Coop
            return TariffAction.NC;
    }
}
