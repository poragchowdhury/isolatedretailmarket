package Agents;

import Configuration.Configuration;
import Observer.Observer;
import Tariff.TariffActions;

public class AlwaysIncrease extends Agent{
	public AlwaysIncrease() {
		// TODO Auto-generated constructor stub
		this.name = "AlzIncz";
	}

	@Override
	public void publishTariff(Observer ob) {
		//System.out.println(name+" : marketshare " + marketShare);
		increase(ob);
		
		tariffCheck(ob); /* Tariff Check */
	}
}
