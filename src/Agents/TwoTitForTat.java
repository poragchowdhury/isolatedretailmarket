package Agents;

import Configuration.Configuration;
import Observer.Observer;
import Tariff.TariffActions;

public class TwoTitForTat extends Agent{
	public TwoTitForTat() {
		// TODO Auto-generated constructor stub
		this.name = "2TFT";
	}
	
	@Override
	public void publishTariff(Observer ob) {

		ttft(ob);
		
		/* Tariff Check */
		tariffCheck(ob);
	}
}