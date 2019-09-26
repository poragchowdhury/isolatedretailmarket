package Agents;

import java.util.Random;

import Configuration.Configuration;
import Observer.Observer;
import Tariff.TariffActions;

public class L4a_3b extends Agent{
	public L4a_3b() {
		// TODO Auto-generated constructor stub
		this.name = "L4a_3b";
	}

	@Override
	public void publishTariff(Observer ob) {
		double prDft = (2/19)*100;
		int prDftInt = (int) prDft;
		Random r = new Random();
		int coin = r.nextInt(100);
		if(coin < prDftInt)
			grim(ob);
		else
			random(ob);
		
		tariffCheck(ob); /* Tariff Check */
	}
	
	public void L3a(Observer ob) {
			double prDft = (5/6)*100;
			int prDftInt = (int) prDft;
			Random r = new Random();
			int coin = r.nextInt(100);
			if(coin < prDftInt)
				defect(ob);
			else
				random(ob);
	}
	
	public void random(Observer ob) {
		Random r = new Random();
		int coin = r.nextInt(2);
		if(coin == 0)				// Defect
			defect(ob);
		else {} 					// Coop 
	} 
}
