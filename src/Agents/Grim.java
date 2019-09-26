package Agents;

import Configuration.Configuration;
import Observer.Observer;
import Tariff.TariffActions;

/*
 * Cooperates, until the opponent defects, and thereafter always defects. [FRI71]
 * */

public class Grim extends Agent{
	
	public Grim() {
		// TODO Auto-generated constructor stub
		this.name = "Grim";
	}
	@Override
	public void publishTariff(Observer ob) {
		grim(ob);
		/* Tariff Check */
		tariffCheck(ob);
	}
}
