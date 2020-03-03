package Agents;

import java.util.Random;

import Configuration.Configuration;
import Observer.Observer;
import Tariff.TariffAction;

/**
 * Cooperates on the first move.
 * If a reward or temptation payoff is received
 * in the last round then repeats last choice,
 * otherwise chooses the opposite choice. [KRA89]
 */
public class Pavlov extends Agent {

	public Pavlov() {
		super("Pavlov", 8);
	}

	@Override
	public TariffAction makeAction(Observer ob) {
		
		if((ob.timeslot-1-Configuration.PUBLICATION_CYCLE >= 0)
				&& (this.rivalActHistory[ob.timeslot-1] == this.actHistory[ob.timeslot-1])) 
			// other agent is D
			pr = Math.min((pr + 0.1), 1); // DD state
		else
			pr = Math.max((pr - 0.2), 0); // CD or DC state
		
		if (previousAction == TariffAction.I1 || 
				previousAction == TariffAction.I2 || 
				previousAction == TariffAction.NC) { // C

			if (this.rivalActHistory[ob.timeslot-1] == TariffAction.I1.index || 
					this.rivalActHistory[ob.timeslot-1] == TariffAction.I2.index ||
					this.rivalActHistory[ob.timeslot-1] == TariffAction.NC.index)
				prI = Math.min((prI + 0.1), 1); // CC state: Increase
			else
				prI = Math.max((prI - 0.1), 0); // CC state: Nochage

		} else { // D
			prI = Math.max((prI - 0.1), 0);
		}
		
		
		int prInt = (int) pr * 100;
		Random r = new Random();
		int coin = r.nextInt(100);
		if (coin > prInt) // Take the stay action
			return TariffAction.valueOf(this.actHistory[ob.timeslot-1]);
		else
		{
			int prIInt = (int) prI * 100;
			coin = r.nextInt(100);
			if(coin > prIInt)
			{	
				// Take the shift action positive way
				if(this.actHistory[ob.timeslot-1] == TariffAction.NC.index)
					return TariffAction.I1;
				else 
					return TariffAction.NC;
			}
			else {
				// Take the shift action negative way
				if(this.actHistory[ob.timeslot-1] == TariffAction.NC.index)
					return TariffAction.D1;
				else 
					return TariffAction.NC;
			}
		}
		
		/* Tariff Check 
		if (previousAction == TariffAction.I1 || previousAction == TariffAction.I2 || previousAction == TariffAction.NC) { // C

			if((ob.timeslot-1-Configuration.PUBLICATION_CYCLE >= 0)
					&& (this.rivalTariffHistory[ob.timeslot-1] >= this.rivalTariffHistory[ob.timeslot-1-Configuration.PUBLICATION_CYCLE])) 
				// other agent is C
			{
				pr = Math.min((pr + 0.1), 1); // CC state

				if (previousAction == TariffAction.I1 || previousAction == TariffAction.I2)
					prI = Math.min((prI + 0.1), 1); // CC state: Increase
				else
					prI = Math.max((prI - 0.1), 0); // CC state: Nochage
			}

		} else if (previousAction == TariffAction.D1 || previousAction == TariffAction.D2) { // D
			if((ob.timeslot-1-Configuration.PUBLICATION_CYCLE >= 0)
					&& (this.rivalTariffHistory[ob.timeslot-1] < this.rivalTariffHistory[ob.timeslot-1-Configuration.PUBLICATION_CYCLE])) 
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
			//			int prIInt = (int) prI * 100;
			//			coin = r.nextInt(100);
			//			if(coin > prIInt)
			return TariffAction.NC;
			//			else
			//				return TariffAction.INCREASE;
		}
		*/
		
	}

	//    public TariffAction cooperate(Observer ob, double prI) {
	//        int prInt = (int) prI * 100;
	//        Random r = new Random();
	//        int coin = r.nextInt(100);
	//        if (coin > prInt) // No Change
	//            return TariffAction.NOCHANGE;
	//        else // Coop
	//            return TariffAction.INCREASE;
	//    }
}
