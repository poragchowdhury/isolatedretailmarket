package edu.utep.poragchowdhury.simulation;

public enum TariffAction {
    NOCHANGE(0, 0), 
    DEFECT(1, -0.01), 
    INCREASE(2, 0.01), 
    DEFECT2(3, -0.02), 
    INCREASE2(4, 0.02);

    public int index;
    public double tariff;

    private TariffAction(int index, double tariff) {
        this.index = index;
        this.tariff = tariff;
    }

    public static TariffAction valueOf(int value) {
        if (value == NOCHANGE.index)
            return TariffAction.NOCHANGE;
        else if (value == DEFECT.index)
            return TariffAction.DEFECT;
        else if (value == INCREASE.index)
            return TariffAction.INCREASE;
        else if (value == DEFECT2.index)
            return TariffAction.DEFECT2;
        else
            return TariffAction.INCREASE2;
    }
}
