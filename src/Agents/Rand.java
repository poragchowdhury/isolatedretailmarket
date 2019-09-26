package Agents;

import java.util.Random;

import Configuration.Configuration;
import Observer.Observer;
import Tariff.TariffActions;

public class Rand extends Agent{
	public Rand() {
		// TODO Auto-generated constructor stub
		this.name = "Rand";
	}
	@Override
	public void publishTariff(Observer ob) {

		Random r = new Random();
		int coin = r.nextInt(2);
		if(coin == 0)				// Defect
			defect(ob);
		else {} 					// Coop 
		
		
		/* Tariff Check */
		tariffCheck(ob);
	}
}
