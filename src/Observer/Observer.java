package Observer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.logging.Logger;

import Agents.Agent;
import Configuration.CaseStudy;
import Configuration.Configuration;
import Customers.FactoredConsumptionCustomer;
import Tariff.TariffAction;

/**
 * Contains the observable information for one game
 */
public class Observer {
	
	// Random walk cost variables
	public double c_max = 0.15;
	public double c_min = 0.05;
	public double tr_min = 0.95;
	public double tr_max = 1 / 0.95;
	public double unitcost = 0.05;
	public double unitcost_pred = 0.06;
	public double trend = 1;
	// Initial profit, cost, marketshare, unitcost
	public double init_unitcost = 0.05;
	public double init_mkshare = 50.0;
	public double init_cost = 0.05 * Configuration.POPULATION / 2 * 8;
	public double init_revenue = Configuration.DEFAULT_TARIFF_PRICE * Configuration.POPULATION / 2 * 8;

    public int publication_cycle_count = 0; // The amount of publication cycles that this game has had
    public int timeslot = 0; // The current timeslot of the game
    public double[] payoffs;
    public int payoffcount = 0;
    public ArrayList<Agent> agentPool;
    public FactoredConsumptionCustomer fcc;
    public Configuration config;
    public double money[];
    public double cost[];
    public double custSubs[];
    public double utility[];

    public int SEED = 0;
    
    private static Logger log = Logger.getLogger("retailmarketmanager");
    /*
     * Error bound and Average Calculation
     */
    public double agentPayoffs[][];
    public double agentBestResponse[][];
    public double agentWins [];

    public Observer() {
        // TODO Auto-generated constructor stub
        config = new Configuration();
        agentPool = new ArrayList<Agent>();
        payoffs = new double[8];
        custSubs = new double[2];
        money = new double[2];
        cost = new double[2];
        utility = new double[2];
        fcc = new FactoredConsumptionCustomer(this);
        agentPayoffs = new double[2][Configuration.TEST_ROUNDS];
        agentBestResponse = new double[2][Configuration.TEST_ROUNDS];
        agentWins = new double[2];
        
        this.unitcost = Configuration.INITUNITCOST;
		this.c_max = Configuration.MAX_UNIT_COST;
		this.c_min = Configuration.MIN_UNIT_COST;
    }

	public void randomWalkUnitCost(int ts) {
		double new_unitcost = Math.min(this.c_max, Math.max(this.c_min, this.trend * this.unitcost));
		double new_trend = Math.max(this.tr_min, Math.min(this.tr_max, this.trend + getRandomValInRange(0.01)));
		if ((this.trend * this.unitcost) < this.c_min || (this.trend * this.unitcost) > this.c_max)
			this.trend = 1;
		else
			this.trend = new_trend;
		this.unitcost = new_unitcost;
		
		double error = getRandomValInRange(0.01);
		this.unitcost_pred = this.unitcost + error;
	}

	/*
	 * Generation random values between -max to +max
	 */
	public double getRandomValInRange(double max) {
		int divisor = 100;
		while (max % 1 != 0) {
			max *= 10;
			divisor *= 10;
		}
		int maxbound = (int) max * 100;
		Random r = new Random();
		int randInt = r.nextInt(maxbound + 1);
		double val = (double) randInt / divisor;
		int coin = r.nextInt(2);
		if (coin == 0)
			val *= -1;
		return val;
	}
    
    public void updateAgentUnitCost() {
    	randomWalkUnitCost(timeslot);
        for(Agent a : agentPool)
        	a.costHistory[timeslot] = this.unitcost;
    }

    public void clear() {
        this.agentPool.clear();
        publication_cycle_count = 0;
        unitcost = 0.05;
        this.fcc = new FactoredConsumptionCustomer(this);
    }

    public void allsampleclear() {
        Arrays.fill(agentPayoffs[0], 0);
        Arrays.fill(agentPayoffs[1], 0);
        
        Arrays.fill(agentBestResponse[0], 0);
        Arrays.fill(agentBestResponse[1], 0);
        
        Arrays.fill(agentWins, 0);
    }

    public double[] calcAvg(CaseStudy cs, double [][] resultArray) {
        double avgagent1 = 0;
        double avgagent2 = 0;
        
        
        for (int i = 0; i < resultArray[0].length; i++) {
            avgagent1 += resultArray[0][i];
            avgagent2 += resultArray[1][i];
        }
        avgagent1 /= resultArray[0].length;
        avgagent2 /= resultArray[1].length;

        double stdagent1 = 0.0;
        double stdagent2 = 0.0;
        for (int i = 0; i < resultArray[0].length; i++) {
            double absdiff1 = Math.abs(avgagent1 - resultArray[0][i]);
            double absdiff2 = Math.abs(avgagent2 - resultArray[1][i]);
            double sqr1 = absdiff1 * absdiff1;
            double sqr2 = absdiff2 * absdiff2;
            stdagent1 += sqr1;
            stdagent2 += sqr2;
        }

        stdagent1 /= resultArray[0].length;
        stdagent2 /= resultArray[1].length;

        stdagent1 = Math.sqrt(stdagent1);
        stdagent2 = Math.sqrt(stdagent2);

        double error1 = 1.96 * (stdagent1 / Math.sqrt(Configuration.TEST_ROUNDS));
        double error2 = 1.96 * (stdagent2 / Math.sqrt(Configuration.TEST_ROUNDS));

        return new double[] { avgagent1, avgagent2, error1, error2 };
    }

    
    public void printAgentPath() {
    	for(Agent a : agentPool) {
    		log.info(a.getAllHistoryActions());
    	}
    }
    
}
