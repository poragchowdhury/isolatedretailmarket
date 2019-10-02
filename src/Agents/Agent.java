package Agents;

import java.util.Random;

import Configuration.Configuration;
import Configuration.DatabaseConnection;
import Observer.Observer;
import Tariff.TariffActions;

public abstract class Agent {
	public String name;
	public double prevtariffPrice;
	public double tariffPrice;
	public double rivalPrevPrice;
	public double rivalPrevPrevPrice;
	public double revenue;
	public double prevrevenue;
	public double marketShare;
	public double prevmarketShare;
	public double cost;
	public double profit;
	public double prevprofit;
	public double tariffUtility;
	public int coopCounter;
	public int defectCounter;
	public boolean booDefect;
	public int agentId;
	public int punishCounter;
	public int myPrevActionId = 0; // 0: Defect, 1 : Coop
	public DatabaseConnection db;
	
	public Agent() {
		prevtariffPrice = Configuration.DEFAULT_TARIFF_PRICE;
		tariffPrice = Configuration.DEFAULT_TARIFF_PRICE;
		rivalPrevPrice = Configuration.DEFAULT_TARIFF_PRICE;
		rivalPrevPrevPrice = Configuration.DEFAULT_TARIFF_PRICE;
		booDefect = false;
	}
	
	public Agent(String rlagentName) {
		prevtariffPrice = Configuration.DEFAULT_TARIFF_PRICE;
		tariffPrice = Configuration.DEFAULT_TARIFF_PRICE;
		rivalPrevPrice = Configuration.DEFAULT_TARIFF_PRICE;
		rivalPrevPrevPrice = Configuration.DEFAULT_TARIFF_PRICE;
		booDefect = false;
		db = new DatabaseConnection(rlagentName);
	}

	
	public void reset() {
		prevtariffPrice = Configuration.DEFAULT_TARIFF_PRICE;
		tariffPrice = Configuration.DEFAULT_TARIFF_PRICE;
		rivalPrevPrice = Configuration.DEFAULT_TARIFF_PRICE;
		rivalPrevPrevPrice = Configuration.DEFAULT_TARIFF_PRICE;
		revenue = 0;
		prevrevenue = 0;
		marketShare = 0;
		prevmarketShare = 0;
		cost = 0;
		profit = 0;
		prevprofit = 0;
		tariffUtility = 0;
		coopCounter = 0;
		defectCounter = 0;
		booDefect = false;
		punishCounter = 0;
		myPrevActionId = 0; // 0: Defect, 1 : Coop
	}
	
	public boolean isOtherAgentCoop(double rivalCurPrice) {
		if(rivalPrevPrice >= this.rivalPrevPrevPrice)
			return true;
		else
			return false;
	}
	
	public void tariffCheck(Observer ob) {
		if(this.tariffPrice < ob.unitcost) {
			System.out.println(Observer.timeslot + " TS: Tariff price becoming unprofitable:NOTPUBLISHING Tariff: " + this.tariffPrice + " Cost " + ob.unitcost + " Prev Tariff " + this.prevtariffPrice);
			System.out.println(this.name + " agent is losing money. \nStopping simulation.");
			System.exit(0);
		}
	}
	
	public void defect(Observer ob) {
		double change = TariffActions.a[TariffActions.action.DEFECT.ordinal()];//(Configuration.ACT_CHANGE_PERC * this.tariffPrice)/100;
		double newTariff = this.tariffPrice + change;
		if(newTariff > ob.unitcost) {
			this.prevtariffPrice = this.tariffPrice;
			this.tariffPrice = newTariff;
			this.myPrevActionId = TariffActions.action.DEFECT.ordinal();
		}
//		else
//			System.out.println(Observer.timeslot + " TS: Tariff price becoming unprofitable:NOTPUBLISHING Tariff: " + this.tariffPrice + " Cost " + ob.unitcost + " Prev Tariff " + this.prevtariffPrice);
	}
	
	public void increase(Observer ob) {
		double change = TariffActions.a[TariffActions.action.INCREASE.ordinal()];//(Configuration.ACT_CHANGE_PERC * this.tariffPrice)/100;
		double newTariff = this.tariffPrice + change;
		if(newTariff < Configuration.MAX_TARIFF_PRICE) {
			this.prevtariffPrice = this.tariffPrice;
			this.tariffPrice = newTariff;
			this.myPrevActionId = TariffActions.action.INCREASE.ordinal();
		}
//		else
//			System.out.println(Observer.timeslot + " TS: Tariff price becoming unprofitable:NOTPUBLISHING Tariff: " + this.tariffPrice + " Cost " + ob.unitcost + " Prev Tariff " + this.prevtariffPrice);
	}
	
	
	public void publishTariffByActionId(Observer ob, int actionid) {
		double change = TariffActions.a[actionid];
		double newTariff = this.tariffPrice + change;
		if(newTariff > ob.unitcost) {
			this.prevtariffPrice = this.tariffPrice;
			this.tariffPrice = newTariff;
			this.myPrevActionId = actionid;
		}
//		else
//			System.out.println(Observer.timeslot + " TS: Tariff price becoming unprofitable:NOTPUBLISHING Tariff: " + this.tariffPrice + " Cost " + ob.unitcost + " Prev Tariff " + this.prevtariffPrice);
	}
	
	public void nochange() {
		this.myPrevActionId = TariffActions.action.NOCHANGE.ordinal();
	}
	
	public void defectOnRivalPrice(Observer ob) {
		double change = TariffActions.a[TariffActions.action.DEFECT.ordinal()];//(Configuration.ACT_CHANGE_PERC * this.tariffPrice)/100;
		double newTariff = this.rivalPrevPrice + change;
		if(newTariff > ob.unitcost) {
			this.prevtariffPrice = this.tariffPrice;
			this.tariffPrice = newTariff;
			this.myPrevActionId = TariffActions.action.DEFECT.ordinal();
		}
//		else
//			System.out.println(Observer.timeslot + " TS: Tariff price becoming unprofitable:NOTPUBLISHING Tariff: " + this.tariffPrice + " Cost " + ob.unitcost + " Prev Tariff " + this.prevtariffPrice);
	}
	
	public abstract void publishTariff(Observer ob);
	
	public void grim(Observer ob) {
		if(booDefect == false) {
			// Rival Agent hasn't defected yet
			if(this.rivalPrevPrevPrice > this.rivalPrevPrice ) 	{ 	// other agent is defecting this round
				booDefect = true;
				defect(ob);
			}
			else { } 												// Agent hasn't defected: So cooperate
		}
		else 														// Always Defect
			defect(ob);
	}
	
	public void random(Observer ob) {
		Random r = new Random();
		int coin = r.nextInt(2);
		if(coin == 0)				// Defect
			defect(ob);
		else {} 					// Coop 
	} 

	public void strategySMNE1(Observer ob) {
		double prDft = (39/68)*100;
		int prDftInt = (int) prDft;
		Random r = new Random();
		int coin = r.nextInt(100);
		if(coin < prDftInt)
			naiveProber(ob);
		else {
			increase(ob);
		}
		//increase(ob);
	}
	
	public void naiveProber(Observer ob) {
		double defectPr = 10;
		Random r = new Random(100);
		double coin = r.nextDouble();
		if(coin < defectPr) // defect
			defect(ob);
	}
	
	public void softMajority(Observer ob) {
		
		if(this.rivalPrevPrevPrice > this.rivalPrevPrice) 	// other agent is defecting
			defectCounter++;
		else coopCounter++;									// other agent is cooperating 
		
		if(Observer.timeslot == 0) {} 						// Coop
		else if(coopCounter >= defectCounter) {}			// Coop
		else 	 											// Defect
			defect(ob);
		
	}
	
	public void tf2t(Observer ob) {
		if(Observer.timeslot == 0) {}									// Start with Cooperation
		else if(defectCounter == 2) {									// Other agent has defected twice, so defect
			defectCounter = 0;
			defectOnRivalPrice(ob);
		}
		else if(this.rivalPrevPrevPrice > this.rivalPrevPrice )  		// other agent is defecting but not twice, so coop  
			defectCounter++;
		else {}															// Else Coop
	}
	
	public void tft(Observer ob) {
		if(Observer.timeslot == 0) {} 							// Coop in the first move
		else if(this.rivalPrevPrevPrice > this.rivalPrevPrice ) // other agent is defecting, so defect
			defect(ob);
		else {} 												// other agent is Cooping, So coop
		
	}
	
	public void ttft(Observer ob) {
		if(Observer.timeslot == 0) {}									// Start with Cooperation
		else if(punishCounter > 0) {									// Other agent has defected, so defect twice
			defectOnRivalPrice(ob);
			punishCounter--;
		}
		else if(this.rivalPrevPrevPrice > this.rivalPrevPrice ) { 		// other agent is defecting with clear history, so count and coop  
			defectOnRivalPrice(ob);
			punishCounter++;
		}
		else {}															// Else Coop

	}
	
	public void tftv2(Observer ob) {
		if(Observer.timeslot == 0) {} 							// Coop in the first move
		else if(this.rivalPrevPrevPrice > this.rivalPrevPrice ) // other agent is defecting, so defect
			defectOnRivalPrice(ob);
		else {} 												// other agent is Cooping, So coop
		
	}
	
	public void hardmajority(Observer ob) {
		if(this.rivalPrevPrevPrice > this.rivalPrevPrice) 	// other agent is defecting
			defectCounter++;
		else coopCounter++;									// other agent is cooperating 
		
		if(Observer.timeslot == 0)  						// Defect on first timeslot
			defect(ob);
		else if(defectCounter >= coopCounter) 				// defect
			defect(ob);
		else {} 											// Coop
		
	}
}
