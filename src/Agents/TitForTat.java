package Agents;

import Observer.Observer;
import Tariff.TariffAction;

public class TitForTat extends Agent {
    public boolean isV2 = false;
    public int numTitsPerTat = 1;
    public int numTatsPerTit = 1;

    private int defectCounter = 0;
    private int punishCounter = 0;

    public TitForTat(int numTitsPerTat, int numTatsPerTit) {
        super("temp");
        String tempName = numTitsPerTat + "TF" + numTatsPerTit + "T";
        if (isV2)
            tempName += "V2";

        this.name = tempName;
        this.numTitsPerTat = numTitsPerTat;
        this.numTatsPerTit = numTatsPerTit;
    }

    @Override
    public TariffAction makeAction(Observer ob) {
        // Coop in the first move
        if (ob.timeslot == 0) {
            return TariffAction.NOCHANGE;

        } else if (punishCounter > 0) {
            punishCounter--;
            return TariffAction.DEFECT;

        } else if (defectCounter == numTatsPerTit) {
            defectCounter = 0;
            punishCounter = numTitsPerTat;
            return TariffAction.DEFECT;
        }
        // other agent is defecting, so defect
        else if (this.rivalPrevPrevPrice > this.rivalPrevPrice) {
            defectCounter++;
            return TariffAction.DEFECT;
        }
        // other agent is increasing price
        else if (this.rivalPrevPrevPrice < this.rivalPrevPrice) {
            return TariffAction.INCREASE;
        }
        return TariffAction.NOCHANGE;
    }
}