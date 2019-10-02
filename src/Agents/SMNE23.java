package Agents;

import java.util.Random;

import Configuration.Configuration;
import Configuration.DatabaseConnection;
import Observer.Observer;
import Tariff.TariffActions;

public class SMNE23 extends Agent{
	DQAgentL15 dqagentL15;

	public SMNE23() {
		// TODO Auto-generated constructor stub
		this.name = "SMNE23";
		dqagentL15 = new DQAgentL15();

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
		double prDQL1 = (9/16)*100;
		int prDQL1Int = (int) prDQL1;
		Random r = new Random();
		int coin = r.nextInt(100);
		if(coin < prDQL1Int) {
			tftv2(ob);
		}
		else {
			dqagentL15.publishTariff(ob);
		}
	}
	
}
