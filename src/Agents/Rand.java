package Agents;

import java.util.Random;

import Configuration.Configuration;
import Observer.Observer;
import Tariff.TariffActions;

public class Rand extends Agent{
	public Rand() {
		// TODO Auto-generated constructor stub
		this.name = "Rand";
	}
	@Override
	public void publishTariff(Observer ob) {
		random(ob);
		/* Tariff Check */
		tariffCheck(ob);
	}
}
