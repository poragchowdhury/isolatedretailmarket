package Agents;

import java.util.Random;

import Configuration.Configuration;
import Observer.Observer;
import Tariff.TariffActions;

public class L4 extends Agent{
	public L4() {
		// TODO Auto-generated constructor stub
		this.name = "L4";
	}

	@Override
	public void publishTariff(Observer ob) {
		strategyL4(ob);
		tariffCheck(ob); /* Tariff Check */
	}
}
