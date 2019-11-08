package Customers;

import java.util.Arrays;
import java.util.Collections;
import java.util.Random;

import Configuration.Configuration;
import Observer.Observer;

public class FactoredConsumptionCustomer {
    // algorithm parameters - needed for numerical stablity
    public Double[] usage = {4.0,3.0,2.0,1.0,1.0,1.0,1.0,1.0,4.0,5.0,7.0,3.0,2.0,4.0,3.0,2.0,1.0,1.0,4.0,5.0,6.0,7.0,5.0,4.0}; //new double[24];// =
    public double lambdaMax = 50.0;
    public double maxLinearUtility = 7.0;
    public Double maxUsage = 1.0;
    public int population = Configuration.POPULATION;
    public int custId[] = new int[population];
    public int[] inertiaPC = new int[population];
    public double[] custMem = new double[population];
    public int lamda = Integer.MAX_VALUE; // Perfectly rational
    public Observer ob;

    public FactoredConsumptionCustomer(Observer ob) {
        custId = new int[population];
        this.ob = ob;

        for (int id = 0; id < population; id++) {
            Random r = new Random();
            custId[id] = r.nextInt(2);
            ob.custSubs[custId[id]]++;
            inertiaPC[id] = 0;
            custMem[id] = Configuration.DEFAULT_TARIFF_PRICE;
        }
        this.maxUsage = Collections.max(Arrays.asList(usage)); 
//        Arrays.fill(usage, 1);
        // printCustomers();
    }

    public void printCustomers() {
        System.out.print("Customer Broker Subscription [ ");
        for (int i = 0; i < population; i++) {
            System.out.print(custId[i] + " ");
        }
        System.out.println("]");
    }

    public boolean getPrToEvaluate(int n) {
        double pw = Math.pow(2, -n);
        // System.out.println("2 ^ " + n + "=" + pw);
        double Ia = (1.00 - pw) * Configuration.INERTIA;
        // System.out.println("Ia " + Ia);
        double pr = (1 - Ia);

        Random r = new Random();
        pr *= 100;
        int toss = r.nextInt(100);
        if (toss <= pr)
            return true;
        return false;
    }

    public double getUtilityPerc(double tariffPrice) {
        return (Configuration.DEFAULT_TARIFF_PRICE - tariffPrice) / Configuration.DEFAULT_TARIFF_PRICE;
    }

    public void evaluateTariffs() {

        /* Code for rationality */
        // enabling perfect rational agent
        // find the maximum utility
        double p0 = 0.5;
        if (Configuration.RATIONALITY > 1 - 1e-6) {

            double u0 = getUtilityPerc(ob.agentPool.get(0).tariffPrice);
            double u1 = getUtilityPerc(ob.agentPool.get(1).tariffPrice);
            ob.utility[0] = u0;
            ob.utility[1] = u1;

            if (u0 > u1)
                p0 = 1;
            else if (u0 < u1)
                p0 = 0;

        } else {

            // Calculated tariff utitlity
            double u0 = getUtilityPerc(ob.agentPool.get(0).tariffPrice);
            double u1 = getUtilityPerc(ob.agentPool.get(1).tariffPrice);

            u0 = constrainUtility(getTariffUtility(u0));
            u1 = constrainUtility(getTariffUtility(u1));

            double ut = u0 + u1;

            p0 = u0 / ut;

            ob.utility[0] = p0;
            ob.utility[1] = 1 - p0;

        }
        // System.out.println("TariffProb0 " + p0);

        double pr0 = p0 * population;
        for (int id = 0; id < population; id++) {
            // Code for : will the customer evaluate the tariff?
            if (getPrToEvaluate(inertiaPC[id])) {
                // assign the customer to a tariff
                chooseTariff(id, pr0);
            }
        }
    }

    public void chooseTariff(int id, double pr0) {
        // change the inertia momentum
        if (custMem[id] != ob.agentPool.get(custId[id]).tariffPrice)
            inertiaPC[id] = 0;
        else
            inertiaPC[id]++;

        Random r = new Random();
        int coin = r.nextInt(population);
        if (coin >= pr0) {
            // subscribe to broker 1
            custId[id] = 1;
        } else {
            custId[id] = 0;
        }

        // update agent memory
        custMem[id] = ob.agentPool.get(custId[id]).tariffPrice;
    }

    public double getTariffUtility(double utilityPerc) {
        // we have the utility
        // double ui = (Configuration.DEFAULT_TARIFF_PRICE-tariffPrice)/Configuration.DEFAULT_TARIFF_PRICE;
        double lambda = Math.pow(lambdaMax, Configuration.RATIONALITY) - 1.0;
        double u = Math.exp(utilityPerc * lambda);
        // System.out.print("utility " + u + " ");
        return u;
    }

    // Ensures numeric stability by constraining range of utility values.
    private double constrainUtility(double utility) {
        if (utility > maxLinearUtility) {
            double compressed = Math.log10(utility - maxLinearUtility);
            return Math.min(maxLinearUtility + compressed, maxLinearUtility * 2);
        } else if (utility < -maxLinearUtility) {
            return -maxLinearUtility; // these will never be chosen anyway
        } else
            return utility;
    }
}
