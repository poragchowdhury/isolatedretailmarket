package Agents;

import java.util.Random;

import Observer.Observer;
import Tariff.TariffAction;

/**
 * Prober: Starts with D,C,C and
 * then defects if the opponent has cooperated in the second and third move;
 * otherwise, it plays TFT.
 */
public class Prober extends Agent {
    public Prober() {
        super("Pbr", 10);
    }

    
    /*
     * Includes D2 action
     */ 
    @Override
    public TariffAction makeAction(Observer ob) {
    	Random rand = new Random();
    	int noise = rand.nextInt(2);
        if (ob.timeslot == 1) // Start with defection
            return TariffAction.D1;
        else if (ob.timeslot == (7+noise) || ob.timeslot == (13+noise)) // Coop
            return TariffAction.NC;
        else if (coopCounter >= 2) // Other agent cooperated twice, so defect
            return TariffAction.D1;
        else if (rivalActHistory[ob.timeslot-1] == TariffAction.D1.index ||
        		rivalActHistory[ob.timeslot-1] == TariffAction.D2.index) { // other agent is defecting
            coopCounter = 0;
            return TariffAction.D1;
        } else {// other agent is cooperating, So coop
            coopCounter++;
            return TariffAction.NC;
        }
    }
}
