package Agents;

import Configuration.Configuration;
import Observer.Observer;
import Tariff.TariffActions;

public class HardMajority extends Agent{
	public HardMajority() {
		// TODO Auto-generated constructor stub
		this.name = "HardMj";
	}

	@Override
	public void publishTariff(Observer ob) {
		
		if(this.rivalPrevPrevPrice > this.rivalPrevPrice) 	// other agent is defecting
			defectCounter++;
		else coopCounter++;									// other agent is cooperating 
		
		if(Observer.timeslot == 0)  						// Defect on first timeslot
			defect(ob);
		else if(defectCounter >= coopCounter) 				// defect
			defect(ob);
		else {} 											// Coop
		
		/* Tariff Check */
		tariffCheck(ob);
	}
}