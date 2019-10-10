package Agents;

import Observer.Observer;

/*
 * Cooperates, until the opponent defects, and thereafter always defects. [FRI71]
 */

public class Grim extends Agent {
    public boolean booDefect = false;

    public Grim() {
        super("Grim");
    }

    @Override
    public void publishTariff(Observer ob) {
        if (!booDefect) {
            // Rival Agent hasn't defected yet
            if (this.rivalPrevPrevPrice > this.rivalPrevPrice) { // other agent is defecting this round
                booDefect = true;
                defect(ob);
            } else {
            } // Agent hasn't defected: So cooperate
        } else // Always Defect
            defect(ob);
        /* Tariff Check */
        tariffCheck(ob);
    }
}
