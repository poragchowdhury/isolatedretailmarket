package Agents;

import java.util.Random;

import Observer.Observer;
import Tariff.TariffAction;

/*
 * Naive Prober (NP): Like Tit for Tat, but occasionally defects with a small probability.
 */
public class NaiveProber extends Agent {
    public NaiveProber() {
        super("NvPbr", 11);
    }

    @Override
    public TariffAction makeAction(Observer ob) {
        double defectPr = 10;
        Random r = new Random(100);
        double coin = r.nextInt();
        if (coin < defectPr) // defect
        {
        	if((tariffPrice + TariffAction.D1.tariff) > ob.unitcost_pred)
        		return TariffAction.D1;
        	else
        		return TariffAction.NC;
        }
        return TariffAction.NC;
    }
}
