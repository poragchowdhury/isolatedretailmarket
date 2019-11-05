package edu.utep.poragchowdhury.agents;

import edu.utep.poragchowdhury.agents.base.Agent;
import edu.utep.poragchowdhury.core.Configuration;
import edu.utep.poragchowdhury.simulation.Observer;
import edu.utep.poragchowdhury.simulation.TariffAction;

public class Test extends Agent {

    public Test() {
        super("Dft_on_2");
    }

    @Override
    public TariffAction makeAction(Observer ob) {
        int pub_cyc_no = ob.timeslot / Configuration.PUBLICATION_CYCLE;
        if (pub_cyc_no == 10) {
            // System.out.println("TS " + ob.timeslot + " Test: DEFECT");
            return TariffAction.DEFECT;
        }
        // System.out.println("TS " + ob.timeslot + " Test: NOCHANGE");
        return TariffAction.NOCHANGE;
    }
}
