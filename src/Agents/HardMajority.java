package Agents;

import Observer.Observer;

public class HardMajority extends Agent {
    public int defectCounter = 0;
    public int coopCounter = 0;

    public HardMajority() {
        super("HardMJ");
    }

    @Override
    public void publishTariff(Observer ob) {
        if (this.rivalPrevPrevPrice > this.rivalPrevPrice) // other agent is defecting
            defectCounter++;
        else
            coopCounter++; // other agent is cooperating

        if (ob.timeslot == 0) // Defect on first timeslot
            defect(ob);
        else if (defectCounter >= coopCounter) // defect
            defect(ob);
        else {
        } // Coop
        /* Tariff Check */
        tariffCheck(ob);
    }
}