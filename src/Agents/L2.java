package Agents;

import java.util.Random;

import Configuration.Configuration;
import Observer.Observer;
import Tariff.TariffActions;

public class L2 extends Agent{
	public L2() {
		// TODO Auto-generated constructor stub
		this.name = "L2";
	}

	@Override
	public void publishTariff(Observer ob) {
		strategyL2(ob);
		tariffCheck(ob); /* Tariff Check */
	}
}
