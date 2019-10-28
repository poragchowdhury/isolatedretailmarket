package Agents;

import Observer.Observer;
import Tariff.TariffActions;

public class AlwaysSame extends Agent{
	public AlwaysSame() {
		// TODO Auto-generated constructor stub
		this.name = "AlzSame";
		this.myPrevActionId = TariffActions.action.NOCHANGE.ordinal();
	}
	@Override
	public void publishTariff(Observer ob) {
		/* Tariff Check */
		//System.out.println(name+" : marketshare " + marketShare);
		tariffCheck(ob);
	}
}
