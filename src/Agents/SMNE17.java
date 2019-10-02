package Agents;

import java.util.Random;

import Configuration.Configuration;
import Configuration.DatabaseConnection;
import Observer.Observer;
import Tariff.TariffActions;

public class SMNE17 extends Agent{
	DQAgentL8_2 dqagentL8_2;
	DQAgentL15 dqagentL15;
	DQAgentL16 dqagentL16;
	public SMNE17() {
		// TODO Auto-generated constructor stub
		this.name = "SMNE17";
		dqagentL8_2 = new DQAgentL8_2();
		dqagentL15 = new DQAgentL15();
		dqagentL16 = new DQAgentL16();
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
		
		Random r = new Random();
		int coin = r.nextInt(3);
		if(coin == 0)
			dqagentL8_2.publishTariff(ob);
		else if(coin == 1)
			dqagentL15.publishTariff(ob);
		else
			dqagentL16.publishTariff(ob);
	}
	
}
