package Agents;

import Observer.Observer;

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
    public void publishTariff(Observer ob) {
        // Coop in the first move
        if (ob.timeslot == 0) {
            nochange();

        } else if (punishCounter > 0) {
            defectOnRivalPrice(ob);
            punishCounter--;
        } else if (defectCounter == numTatsPerTit) {
            defectCounter = 0;
            punishCounter = numTitsPerTat;
            defectOnRivalPrice(ob);
        }
        // other agent is defecting, so defect
        else if (this.rivalPrevPrevPrice > this.rivalPrevPrice) {
            if (isV2)
                defectOnRivalPrice(ob);
            else
                defect(ob);
            defectCounter++;
        }
    }
}