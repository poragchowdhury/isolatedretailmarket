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
		
		softMajority(ob);
		/* Tariff Check */
		tariffCheck(ob);
	}
}