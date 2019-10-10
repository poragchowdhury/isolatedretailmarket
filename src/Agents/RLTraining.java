package Agents;

import Configuration.Configuration;

public class RLTraining extends RL {
    public double maxppts = (Configuration.MAX_TARIFF_PRICE * Configuration.POPULATION) / Configuration.PPTS_DISCRTZD;
    public int mynextaction = 0;

    public RLTraining() {
        super(Configuration.DB_NAME_TRAINING);
    }
}
