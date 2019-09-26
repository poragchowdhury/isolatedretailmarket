package Agents;

import java.util.Random;

import com.mysql.cj.exceptions.ClosedOnExpiredPasswordException;

import Configuration.Configuration;
import Configuration.DatabaseConnection;
import Observer.Observer;
import Tariff.TariffActions;

public class RL1Fixed extends Agent{
	public double maxppts = (Configuration.MAX_TARIFF_PRICE * Configuration.POPULATION) / Configuration.PPTS_DISCRTZD;
	public int mynextaction = 0;
	public RL1Fixed() {
		this.name = "rl1";
		db = new DatabaseConnection(this.name);
	}
	
	public void getNextActionId() throws Exception{
		// Get the discretized value of state features to form the position and form String s_prime
		double maxqval = -1.0;
		double pr = 1;///((double)TariffActions.a.length);
		
		for(int action = 0; action < TariffActions.a.length; action++) {
			double ex_qval = 0;
			for(long ppts = 0; ppts < maxppts; ppts++){
				String tableName = "ppts"+ ppts + "a" + action;
				double qval = Configuration.db.getQValue(tableName);
				ex_qval += qval * pr;
			}
			if(ex_qval > maxqval) {
				maxqval = ex_qval;
				mynextaction = action;
			}
		}
	}
	
	public void updateQvalue(Observer ob) throws Exception{
		double spa_maxqval = -1.0;
		
		long cur_ppts = (long)(prevprofit / (double)Observer.timeslot-1);
		if(prevprofit == 0)
			cur_ppts = 0;
		else
			cur_ppts = (long) (cur_ppts / Configuration.PPTS_DISCRTZD);
		
		String sa_tableName = "ppts"+cur_ppts+"a"+myPrevActionId;
		//System.out.println("current state-action : " + sa_tableName);
		double sa_qval = Configuration.db.getQValue(sa_tableName);
		
		double pr = 1/((double)TariffActions.a.length);
		
		for(int actionid = 0; actionid < TariffActions.a.length; actionid++) {
			double ex_qval = 0;
			for(long ppts = 0; ppts <= maxppts; ppts++){
				String spa_tableName = "ppts" + ppts + "a" + actionid;
				double spa_qval = Configuration.db.getQValue(spa_tableName);
				ex_qval += spa_qval * pr;
			}
			if(ex_qval > spa_maxqval) {
				spa_maxqval = ex_qval;
				//mynextaction = actionid;
			}
		}
		// update Q(s,a)
		double reward = profit-prevprofit;
		//sa_qval = ((1-Configuration.LEARNING_RATE) * sa_qval) + (Configuration.LEARNING_RATE * (reward + Configuration.DISCOUNT_FACTOR * spa_maxqval));
		sa_qval = (Configuration.LEARNING_RATE * (reward + Configuration.DISCOUNT_FACTOR * spa_maxqval));
		Configuration.db.insertQValue(sa_tableName, sa_qval);
		
		Random r = new Random();
		mynextaction = r.nextInt(TariffActions.a.length);
		 
	}
	
	@Override
	public void publishTariff(Observer ob) {
		try {
			//System.out.println("Starting RL logic");
			if(Configuration.RL_TRAINING)
				updateQvalue(ob);
			else
				getNextActionId();
			
			publishTariffByActionId(ob, mynextaction);
			
			//System.out.println("RL logic: action :"+ TariffActions.action.values()[mynextaction].toString() + " TariffPrice " + tariffPrice);
		}
		catch(Exception ex) {
			ex.printStackTrace();
			System.exit(0);
		}
		/* Step 1. What is my next action to take from s'? 
		 * 
		 * My current state is s' 
		 * 
		 * 
		 * 
		 * 
		 * 
		 * Query the database to 
		 * 
		 * 
		 * */
		
		// 2. I have reward R(s, a, s') = Revenue
		// 3. I know what are my values in Q(s', a) is database: find the max Q to find the best action to s''
		// 4. update Q(s, a) = (1-alpha)*Q(s, a) + alpha*(Profit + gamma*(max(Q(s',a))));
		
		// You already know which action you have taken to come down here to state s' i.e. String a_prev
		
		// Get the discretized value
		
		// query the database to get q values for each action 
		
		// get the action with the maximum q value
		
		// update q value for current action taken 
		
		// Take the best action from the current state S' 
	}
}
