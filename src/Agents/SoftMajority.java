package Agents;

import Observer.Observer;

public class SoftMajority extends Agent {
    private int defectCounter = 0;
    private int coopCounter = 0;

    public SoftMajority() {
        super("SoftMJ");
    }

    @Override
    public void publishTariff(Observer ob) {
        if (this.rivalPrevPrevPrice > this.rivalPrevPrice) // other agent is defecting
            defectCounter++;
        else
            coopCounter++; // other agent is cooperating

        if (Observer.timeslot == 0) {
        } // Coop
        else if (coopCounter >= defectCounter) {
        } // Coop
        else // Defect
            defect(ob);

        tariffCheck(ob);
    }
}