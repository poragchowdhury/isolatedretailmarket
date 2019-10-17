package Agents;

import java.util.Random;

import Configuration.Configuration;
import Configuration.DatabaseConnection;
import Observer.Observer;
import Tariff.TariffActions;

public class SMNE2 extends Agent{
	
	DQAgentL1 dqAgentL1;
	
	public SMNE2() {
		// TODO Auto-generated constructor stub
		this.name = "SMNE2";
		dqAgentL1 = new DQAgentL1();
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
		double prDQL1 = (1/16)*100;
		int prDQL1Int = (int) prDQL1;
		Random r = new Random();
		int coin = r.nextInt(100);
		if(coin < prDQL1Int) {
			dqAgentL1.publishTariff(ob);
		}
		else {
			softMajority(ob);
		}
	}
	
}