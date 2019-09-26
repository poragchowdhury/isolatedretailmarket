package Agents;

import java.util.Random;

import Configuration.Configuration;
import Observer.Observer;
import Tariff.TariffActions;

public class L5a extends Agent{
	public L5a() {
		// TODO Auto-generated constructor stub
		this.name = "L4";
	}

	@Override
	public void publishTariff(Observer ob) {
		double prDft = (3/4)*100;
		int prDftInt = (int) prDft;
		Random r = new Random();
		int coin = r.nextInt(100);
		if(coin < prDftInt)
			defect(ob);
		else
			random(ob);
		
		tariffCheck(ob); /* Tariff Check */
	}
	
	public void random(Observer ob) {
		Random r = new Random();
		int coin = r.nextInt(2);
		if(coin == 0)				// Defect
			defect(ob);
		else {} 					// Coop 
	} 
}
