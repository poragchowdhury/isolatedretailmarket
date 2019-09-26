package Agents;

import Configuration.Configuration;
import Observer.Observer;
import Tariff.TariffActions;

public class AlwaysDefect extends Agent{
	public AlwaysDefect() {
		// TODO Auto-generated constructor stub
		this.name = "AlD";
	}

	@Override
	public void publishTariff(Observer ob) {
		//System.out.println(name+" : marketshare " + marketShare);
		defect(ob);
		
		tariffCheck(ob); /* Tariff Check */
	}
}
