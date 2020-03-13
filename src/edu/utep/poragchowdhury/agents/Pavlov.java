package edu.utep.poragchowdhury.agents;

import java.util.Random;

import edu.utep.poragchowdhury.agents.base.Agent;
import edu.utep.poragchowdhury.agents.base.AgentID;
import edu.utep.poragchowdhury.core.Configuration;
import edu.utep.poragchowdhury.simulation.Observer;
import edu.utep.poragchowdhury.simulation.TariffAction;

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
        super("Pavlov", AgentID.Pavlov);
    }

    @Override
    public TariffAction makeAction(Observer ob) {
        /* Tariff Check */
        if (previousAction == TariffAction.I1 || previousAction == TariffAction.I2 || previousAction == TariffAction.NC) { // C

            if ((ob.timeslot - 1 - Configuration.PUBLICATION_CYCLE >= 0)
                    && (this.rivalTariffHistory[ob.timeslot - 1] >= this.rivalTariffHistory[ob.timeslot - 1 - Configuration.PUBLICATION_CYCLE]))
            // other agent is C
            {
                pr = Math.min((pr + 0.1), 1); // CC state

                if (previousAction == TariffAction.I1 || previousAction == TariffAction.I2)
                    prI = Math.min((prI + 0.1), 1); // CC state: Increase
                else
                    prI = Math.max((prI - 0.1), 0); // CC state: Nochange
            }

        } else if (previousAction == TariffAction.D1 || previousAction == TariffAction.D2) { // D
            if ((ob.timeslot - 1 - Configuration.PUBLICATION_CYCLE >= 0)
                    && (this.rivalTariffHistory[ob.timeslot - 1] < this.rivalTariffHistory[ob.timeslot - 1 - Configuration.PUBLICATION_CYCLE]))
                // other agent is D
                pr = Math.min((pr + 0.1), 1); // DD state
        } else {
            pr = Math.max((pr - 0.2), 0); // CD or DC state
        }

        int prInt = (int) pr * 100;
        Random r = new Random();
        int coin = r.nextInt(100);
        if (coin > prInt) // Defect
            return TariffAction.D1;
        else // Coop
        {
            // int prIInt = (int) prI * 100;
            // coin = r.nextInt(100);
            // if(coin > prIInt)
            return TariffAction.NC;
            // else
            // return TariffAction.INCREASE;
        }
    }
}
