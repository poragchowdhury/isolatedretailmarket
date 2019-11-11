package Agents;

import java.util.Random;

import Observer.Observer;
import Tariff.TariffAction;

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
        double coin = r.nextInt();
        if (coin < defectPr) // defect
            return TariffAction.DEFECT;
        return TariffAction.NOCHANGE;
    }
}
