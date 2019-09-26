package Agents;

import java.util.Random;

import Configuration.Configuration;
import Observer.Observer;
import Tariff.TariffActions;

/*
 * Prober: Starts with D,C,C and 
 * then defects if the opponent has cooperated in the second and third move; 
 * otherwise, it plays TFT.
 * */
public class Prober extends Agent{
	
	public Prober() {
		// TODO Auto-generated constructor stub
		this.name = "Prober";
	}
	
	@Override
	public void publishTariff(Observer ob) {
		
		if(Observer.timeslot == 0) 										// Start with defection
			defect(ob);
		else if(Observer.timeslot == 1 || Observer.timeslot == 2) {} 	// Coop
		else if(coopCounter >= 2) 										// Other agent cooperated twice, so defect
			defect(ob);
		else if(this.rivalPrevPrevPrice > this.rivalPrevPrice ) { 		// other agent is defecting 
			coopCounter = 0;
			defect(ob);
		}
		else															// other agent is cooperating, So coop
			coopCounter++;
		
		/* Tariff Check */
		tariffCheck(ob);
	}
}
