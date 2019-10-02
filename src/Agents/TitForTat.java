package Agents;

import Configuration.Configuration;
import Observer.Observer;
import Tariff.TariffActions;

public class TitForTat extends Agent{
	public TitForTat() {
		// TODO Auto-generated constructor stub
		this.name = "TFT";
	}

	@Override
	public void publishTariff(Observer ob) {

		tft(ob);
		/* Tariff Check */
		tariffCheck(ob);
	}
}