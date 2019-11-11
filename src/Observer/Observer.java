package Observer;

import java.util.ArrayList;
import java.util.Arrays;
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
    }

    public void updateAgentUnitCost() {
        for(Agent a : agentPool)
        	a.randomWalkUnitCost(timeslot);
    }

    public void clear() {
        this.agentPool.clear();
        publication_cycle_count = 0;
        this.fcc = new FactoredConsumptionCustomer(this);
    }

    public void allsampleclear() {
        Arrays.fill(agentPayoffs[0], 0);
        Arrays.fill(agentPayoffs[1], 0);
        
        Arrays.fill(agentBestResponse[0], 0);
        Arrays.fill(agentBestResponse[1], 0);
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
