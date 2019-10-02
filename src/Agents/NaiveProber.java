package Agents;

import java.util.Random;

import Configuration.Configuration;
import Observer.Observer;
import Tariff.TariffActions;

/*
 * Naive Prober (NP): Like Tit for Tat, but occasionally defects with a small probability.
 * */
public class NaiveProber extends Agent{
	public NaiveProber() {
		// TODO Auto-generated constructor stub
		this.name = "NvPbr";
	}
	@Override
	public void publishTariff(Observer ob) {
		
		naiveProber(ob);
		
		/* Tariff Check */
		tariffCheck(ob);
	}
}
