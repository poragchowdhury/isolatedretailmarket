package Agents;

import java.util.Random;

import Observer.Observer;

/*
 * Naive Prober (NP): Like Tit for Tat, but occasionally defects with a small probability.
 */
public class NaiveProber extends Agent {
    public NaiveProber() {
        super("NvPbr");
    }

    @Override
    public void publishTariff(Observer ob) {
        double defectPr = 10;
        Random r = new Random(100);
        double coin = r.nextDouble();
        if (coin < defectPr) // defect
            defect(ob);

        /* Tariff Check */
        tariffCheck(ob);
    }
}
