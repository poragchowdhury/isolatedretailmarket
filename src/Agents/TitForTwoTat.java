package Agents;

import Configuration.Configuration;
import Observer.Observer;
import Tariff.TariffActions;

public class TitForTwoTat extends Agent{
	public TitForTwoTat() {
		// TODO Auto-generated constructor stub
		this.name = "TF2T";
	}
	
	
	@Override
	public void publishTariff(Observer ob) {

		if(Observer.timeslot == 0) {}									// Start with Cooperation
		else if(defectCounter == 2) {									// Other agent has defected twice, so defect
			defectCounter = 0;
			defectOnRivalPrice(ob);
		}
		else if(this.rivalPrevPrevPrice > this.rivalPrevPrice )  		// other agent is defecting but not twice, so coop  
			defectCounter++;
		else {}															// Else Coop

		
		/* Tariff Check */
		tariffCheck(ob);
	}
}