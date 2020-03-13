package edu.utep.poragchowdhury.simulation;

public enum TariffAction {
    NC(0, 0), 
    D1(1, -0.01), 
    I1(2, 0.01), 
    D2(3, -0.02), 
    I2(4, 0.02);

    public int index;
    public double tariff;

    private TariffAction(int index, double tariff) {
        this.index = index;
        this.tariff = tariff;
    }

    public static TariffAction valueOf(int value) {
        if (value == NC.index)
            return TariffAction.NC;
        else if (value == D1.index)
            return TariffAction.D1;
        else if (value == I1.index)
            return TariffAction.I1;
        else if (value == D2.index)
            return TariffAction.D2;
        else
            return TariffAction.I2;
    }
}
