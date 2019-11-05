package edu.utep.poragchowdhury.agents;

import java.util.Random;

import edu.utep.poragchowdhury.agents.base.Agent;
import edu.utep.poragchowdhury.simulation.Observer;
import edu.utep.poragchowdhury.simulation.TariffAction;

/*
 * Naive Prober (NP): Like Tit for Tat, but occasionally defects with a small probability.
 */
public class NaiveProber extends Agent {
    public NaiveProber() {
        super("NvPbr");
    }

    @Override
    public TariffAction makeAction(Observer ob) {
        double defectPr = 10;
        Random r = new Random(100);
        double coin = r.nextDouble();
        if (coin < defectPr) // defect
            return TariffAction.DEFECT;
        return TariffAction.NOCHANGE;
    }
}
