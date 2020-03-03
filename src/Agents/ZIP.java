package Agents;

import Observer.Observer;
import Tariff.TariffAction;

/*
 * Cooperates, until the opponent defects, and thereafter always defects. [FRI71]
 */

public class ZIP extends Agent {

    public ZIP() {
        super("ZIP", 14);
    }

    @Override
    public TariffAction makeAction(Observer ob) {
        if (this.marketShare < 30) { // If market share is less than 30% then defect
        	if((tariffPrice + TariffAction.D1.tariff) > ob.unitcost_pred)
        		return TariffAction.D1;
        	else
        		return TariffAction.NC;
        } 
        else if (this.marketShare > 80) { // If market share is greater than 80% then defect
            return TariffAction.I1;
        }
        else { // Otherwise cooperate
            return TariffAction.NC;
        }
    }
}