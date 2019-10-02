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
		
		hardmajority(ob);
		/* Tariff Check */
		tariffCheck(ob);
	}
}