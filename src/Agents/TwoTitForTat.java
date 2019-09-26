package Agents;

import Configuration.Configuration;
import Observer.Observer;
import Tariff.TariffActions;

public class TwoTitForTat extends Agent{
	public TwoTitForTat() {
		// TODO Auto-generated constructor stub
		this.name = "2TFT";
	}
	
	@Override
	public void publishTariff(Observer ob) {

		if(Observer.timeslot == 0) {}									// Start with Cooperation
		else if(punishCounter > 0) {									// Other agent has defected, so defect twice
			defectOnRivalPrice(ob);
			punishCounter--;
		}
		else if(this.rivalPrevPrevPrice > this.rivalPrevPrice ) { 		// other agent is defecting with clear history, so count and coop  
			defectOnRivalPrice(ob);
			punishCounter++;
		}
		else {}															// Else Coop

		
		/* Tariff Check */
		tariffCheck(ob);
	}
}