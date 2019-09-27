package Agents;

import java.util.Random;

import Configuration.Configuration;
import Configuration.DatabaseConnection;
import Observer.Observer;
import Tariff.TariffActions;

public class SMNE2 extends RL{
	public SMNE2() {
		// TODO Auto-generated constructor stub
		super("rl1");
		this.name = "SMNE2";
	}

	@Override
	public void publishTariff(Observer ob) {
		try {
			strategySMNE2(ob);
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
		tariffCheck(ob); /* Tariff Check */
	}
	
	public void strategySMNE2(Observer ob) throws Exception {
		double prInc = (15/22)*100;
		int prIncInt = (int) prInc;
		Random r = new Random();
		int coin = r.nextInt(100);
		if(coin < prIncInt) {
			nochange();
		}
		else {
			publishTariffByActionId(ob, getNextActionId()); // play rl1
		}
	}
	
}
