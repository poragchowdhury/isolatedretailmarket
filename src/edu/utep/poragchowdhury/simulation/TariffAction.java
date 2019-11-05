package edu.utep.poragchowdhury.simulation;

public enum TariffAction {
    NOCHANGE(0, 0), DEFECT(1, -0.1), INCREASE(2, 0.1);

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
        else
            return TariffAction.INCREASE;
    }
}
