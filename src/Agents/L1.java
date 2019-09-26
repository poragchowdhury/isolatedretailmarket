package Agents;

import java.util.Random;

import Configuration.Configuration;
import Observer.Observer;
import Tariff.TariffActions;

public class L1 extends Agent{
	public L1() {
		// TODO Auto-generated constructor stub
		this.name = "L1";
	}

	@Override
	public void publishTariff(Observer ob) {
		strategyL1(ob);
		tariffCheck(ob); /* Tariff Check */
	}
}
