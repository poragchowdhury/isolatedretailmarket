package Agents;

import Observer.Observer;
import Tariff.TariffActions;

public class AlwaysSame extends Agent{
	public AlwaysSame() {
		// TODO Auto-generated constructor stub
		this.name = "AlzSame";
	}
	@Override
	public void publishTariff(Observer ob) {
		/* Tariff Check */
		//System.out.println(name+" : marketshare " + marketShare);
		tariffCheck(ob);
	}
}
