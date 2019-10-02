package Agents;

import java.util.Random;

import Configuration.Configuration;
import Configuration.DatabaseConnection;
import Observer.Observer;
import Tariff.TariffActions;

public class SMNE4 extends Agent{
	
	DQAgentL2 dqAgentL2;
	
	public SMNE4() {
		// TODO Auto-generated constructor stub
		this.name = "SMNE4";
		dqAgentL2 = new DQAgentL2();
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
		double prDQL1 = (2/17)*100;
		int prDQL1Int = (int) prDQL1;
		Random r = new Random();
		int coin = r.nextInt(100);
		if(coin < prDQL1Int) {
			dqAgentL2.publishTariff(ob);
		}
		else {
			softMajority(ob);
		}
	}
	
}
