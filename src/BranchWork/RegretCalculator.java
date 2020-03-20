package BranchWork;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class RegretCalculator {
    
    private static Logger log = Logger.getLogger("retailmarketmanager");
    
    public void setupLogging() throws IOException {
		// %1 = Date, %2 = Source, %3 = Logger, %4 = Level, %5 = Message, &6 = Thrown
		// %1$tF = Date -> Y-m-d
		// %1$tT = Date -> 24 hour format
		// %4$s = Log Type (Info, ...)
		// %2$s = Class and Method Call
		// %5$s%6$s = Message
		// {%1$tT} %2$s %5$s%6$s  
		System.setProperty("java.util.logging.SimpleFormatter.format", "%5$s%6$s" + "\n");
		FileHandler fh = new FileHandler("experiment.log", true);
		SimpleFormatter formatter = new SimpleFormatter();

		fh.setFormatter(formatter);
		log.addHandler(fh);
	}

    
    public static void main(String[] args) throws FileNotFoundException, IOException{
    	RegretCalculator rc = new RegretCalculator();
    	int NUM_CANDIDATE_STRATEGY = 16; 
    	rc.setupLogging();
    	try(BufferedReader br = new BufferedReader(new FileReader("game.csv"))) {
		    
		    String line = br.readLine();
		    String[] arrStrategies = line.split(",");
		    int n = arrStrategies.length;
		    System.out.println(n + " rows");
		    double [][] payoffs = new double[n][NUM_CANDIDATE_STRATEGY];
		    double [][] regretTable = new double[n+1][NUM_CANDIDATE_STRATEGY+1]; // for row player
			
		    line = br.readLine();
		    int row = 0;
		    
		    // load the values from the file
		    while (line != null) {
		    	String [] values = line.split(",");
		    	for(int col = 0, item = 0; col < n; col++) {
		    		payoffs[row][col] = Double.parseDouble(values[++item]);
		    		regretTable[row][col] = payoffs[row][col];
		    	}
		    	line = br.readLine();
		    	row++;
		    }
		    
		    // Created the regret table! 
		    for(int c = 0; c < NUM_CANDIDATE_STRATEGY; c++) {
		    	double maxPayoff = payoffs[0][c];
		    	for(int r = 0; r < n; r++) {
		    		if(payoffs[r][c] > maxPayoff)
		    			maxPayoff = payoffs[r][c];
		    	}
		    	regretTable[n][c] = maxPayoff;
		    	
		    	// Compute the total regret for each strategies
		    	// got the maxPayoff now del from every value from the regrettable
		    	for(int r = 0; r < n; r++)
		    		regretTable[r][c] = maxPayoff - regretTable[r][c];

		    }
		    
		    for(int r = 0; r < n; r++) {
		    	int totalRegret = 0;
		    	for(int c = 0; c < NUM_CANDIDATE_STRATEGY; c++) {
		    		totalRegret += regretTable[r][c];
		    	}
		    	regretTable[r][NUM_CANDIDATE_STRATEGY] = totalRegret;
		    	log.info(String.format("%s, %.3f,", arrStrategies[r],regretTable[r][NUM_CANDIDATE_STRATEGY]));
		    }
		    
		    System.out.println();
		}
    }
}
