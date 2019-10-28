package Agents;

import java.util.Random;

import Configuration.Configuration;
import Observer.Observer;
import Tariff.TariffActions;

public class SMNE1 extends Agent{
	public SMNE1() {
		// TODO Auto-generated constructor stub
		this.name = "SMNE1";
	}

	@Override
	public void publishTariff(Observer ob) {
		//strategySMNE1(ob);
		nochange();
		//random(ob);
		tariffCheck(ob); /* Tariff Check */
	}
}
