package Agents;

import Observer.Observer;
import Tariff.TariffAction;

/*
 * Cooperates, until the opponent defects, and thereafter always defects. [FRI71]
 */

public class Grim extends Agent {
    public boolean booDefect = false;

    public Grim() {
        super("Grim");
    }

    @Override
    public TariffAction makeAction(Observer ob) {
        if (!booDefect) {
            // Rival Agent hasn't defected yet
            if (this.rivalPrevPrevPrice > this.rivalPrevPrice) { // other agent is defecting this round
                booDefect = true;
                return TariffAction.DEFECT;
            } else { // Agent hasn't defected: So cooperate
                return TariffAction.NOCHANGE;
            }
        } else // Always Defect
            return TariffAction.DEFECT;
    }
}
