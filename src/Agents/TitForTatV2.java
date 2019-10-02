package Agents;

import Configuration.Configuration;
import Observer.Observer;
import Tariff.TariffActions;

public class TitForTatV2 extends Agent{
	public TitForTatV2() {
		// TODO Auto-generated constructor stub
		this.name = "TFTV2";
	}
	
	
	@Override
	public void publishTariff(Observer ob) {

		tftv2(ob);
		/* Tariff Check */
		tariffCheck(ob);
	}
}