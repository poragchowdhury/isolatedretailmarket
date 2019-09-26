package Agents;

import java.util.Random;

import Configuration.Configuration;
import Observer.Observer;
import Tariff.TariffActions;

public class L3 extends Agent{
	public L3() {
		// TODO Auto-generated constructor stub
		this.name = "L3";
	}

	@Override
	public void publishTariff(Observer ob) {
		strategyL3(ob);
		tariffCheck(ob); /* Tariff Check */
	}
}
