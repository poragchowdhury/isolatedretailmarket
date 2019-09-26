package Agents;

import Configuration.Configuration;
import Observer.Observer;
import Tariff.TariffActions;

public class TitForTat extends Agent{
	public TitForTat() {
		// TODO Auto-generated constructor stub
		this.name = "TFT";
	}

	@Override
	public void publishTariff(Observer ob) {

		if(Observer.timeslot == 0) {} 							// Coop in the first move
		else if(this.rivalPrevPrevPrice > this.rivalPrevPrice ) // other agent is defecting, so defect
			defect(ob);
		else {} 												// other agent is Cooping, So coop
		
		/* Tariff Check */
		tariffCheck(ob);
	}
}