package RetailMarketManager;

public class Payoffs {
	public double value1;
	public double value2;

	public Payoffs(double val1, double val2) {
		value1 = val1;
		value2 = val2;
	}

	@Override
	public String toString() {
		return String.format("[%.3f, %.3f]", value1, value2);
	}
}
