package Agents;

import Configuration.Configuration;
import Observer.Observer;
import Tariff.TariffActions;

public class SoftMajority extends Agent{
	public SoftMajority() {
		// TODO Auto-generated constructor stub
		this.name = "SoftMj";
	}

	@Override
	public void publishTariff(Observer ob) {
		
		if(this.rivalPrevPrevPrice > this.rivalPrevPrice) 	// other agent is defecting
			defectCounter++;
		else coopCounter++;									// other agent is cooperating 
		
		if(Observer.timeslot == 0) {} 						// Coop
		else if(coopCounter >= defectCounter) {}			// Coop
		else 	 											// Defect
			defect(ob);
		
		/* Tariff Check */
		tariffCheck(ob);
	}
}