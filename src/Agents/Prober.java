package Agents;

import Observer.Observer;

/**
 * Prober: Starts with D,C,C and
 * then defects if the opponent has cooperated in the second and third move;
 * otherwise, it plays TFT.
 */
public class Prober extends Agent {
    private int coopCounter = 0;

    public Prober() {
        // TODO Auto-generated constructor stub
        super("Prober");
    }

    @Override
    public void publishTariff(Observer ob) {

        if (ob.timeslot == 0) // Start with defection
            defect(ob);
        else if (ob.timeslot == 1 || ob.timeslot == 2) {
        } // Coop
        else if (coopCounter >= 2) // Other agent cooperated twice, so defect
            defect(ob);
        else if (this.rivalPrevPrevPrice > this.rivalPrevPrice) { // other agent is defecting
            coopCounter = 0;
            defect(ob);
        } else // other agent is cooperating, So coop
            coopCounter++;

        /* Tariff Check */
        tariffCheck(ob);
    }
}
