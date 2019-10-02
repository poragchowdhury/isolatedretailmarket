package Agents;

import java.util.Random;

import Configuration.Configuration;
import Configuration.DatabaseConnection;
import Observer.Observer;
import Tariff.TariffActions;

public class SMNE6 extends Agent{
	
	DQAgentL1 dqAgentL1;
	
	public SMNE6() {
		// TODO Auto-generated constructor stub
		this.name = "SMNE6";
		dqAgentL1 = new DQAgentL1();
	}

	@Override
	public void publishTariff(Observer ob) {
		try {
			strategySMNE6(ob);
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
		tariffCheck(ob); /* Tariff Check */
	}
	
	public void strategySMNE6(Observer ob) throws Exception {
		double prDQL1 = (2/27)*100;
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
