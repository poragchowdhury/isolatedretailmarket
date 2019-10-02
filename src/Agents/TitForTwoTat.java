package Agents;

import Configuration.Configuration;
import Observer.Observer;
import Tariff.TariffActions;

public class TitForTwoTat extends Agent{
	public TitForTwoTat() {
		// TODO Auto-generated constructor stub
		this.name = "TF2T";
	}
	
	
	@Override
	public void publishTariff(Observer ob) {

		tf2t(ob);
		
		/* Tariff Check */
		tariffCheck(ob);
	}
}