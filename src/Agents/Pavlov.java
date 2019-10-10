package Agents;

import java.util.Random;

import Observer.Observer;
import Tariff.TariffActions;

/**
 * Cooperates on the first move.
 * If a reward or temptation payoff is received
 * in the last round then repeats last choice,
 * otherwise chooses the opposite choice. [KRA89]
 */
public class Pavlov extends Agent {
    public double pr = 1; // probability to increase+nochange
    public double prI = 1;

    public Pavlov() {
        // TODO Auto-generated constructor stub
        super("Pavlov");
        this.myPrevActionId = TariffActions.action.NOCHANGE.ordinal();
    }

    @Override
    public void publishTariff(Observer ob) {
        /* Tariff Check */
        if (myPrevActionId == TariffActions.action.INCREASE.ordinal() || myPrevActionId == TariffActions.action.NOCHANGE.ordinal()) { // C
            if (this.rivalPrevPrevPrice == this.rivalPrevPrice) // other agent is C
            {
                pr = Math.min((pr + 0.1), 1); // CC state

                if (myPrevActionId == TariffActions.action.INCREASE.ordinal())
                    prI = Math.min((prI + 0.1), 1); // CC state: Increase
                else
                    prI = Math.max((prI - 0.1), 0); // CC state: Nochage
            }

        } else if (myPrevActionId == TariffActions.action.DEFECT.ordinal()) { // D
            if (this.rivalPrevPrevPrice > this.rivalPrevPrice) // other agent is D
                pr = Math.min((pr + 0.1), 1); // DD state
        } else {
            pr = Math.max((pr - 0.2), 0); // CD or DC state
        }

        int prInt = (int) pr * 100;
        Random r = new Random();
        int coin = r.nextInt(100);
        if (coin > prInt) // Defect
            defect(ob);
        else // Coop
            cooperate(ob, prI);

        tariffCheck(ob);
    }

    public void cooperate(Observer ob, double prI) {
        int prInt = (int) prI * 100;
        Random r = new Random();
        int coin = r.nextInt(100);
        if (coin > prInt) // Nochange
            nochange();
        else // Coop
            increase(ob);
    }
}
