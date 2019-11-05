package edu.utep.poragchowdhury.agents;

import java.util.Random;

import edu.utep.poragchowdhury.agents.base.Agent;
import edu.utep.poragchowdhury.simulation.Observer;
import edu.utep.poragchowdhury.simulation.TariffAction;

public class Rand extends Agent {
    public Rand() {
        super("Rand");
    }

    @Override
    public TariffAction makeAction(Observer ob) {
        Random r = new Random();
        int coin = r.nextInt(2);
        if (coin == 0) // Defect
            return TariffAction.DEFECT;
        else // Coop
            return TariffAction.NOCHANGE;
    }
}
