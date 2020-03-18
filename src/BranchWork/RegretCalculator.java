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
    	rc.setupLogging();
    	try(BufferedReader br = new BufferedReader(new FileReader("game.csv"))) {
		    
		    String line = br.readLine();
		    String[] arrStrategies = line.split(",,");
		    int n = arrStrategies.length - 1;
		    System.out.println(n + " number of strategies");
		    double [][] regretTable = new double[n+1][n]; // for column player
			
		    double [][][] payoffs = new double[n][n][2]; 
		    line = br.readLine();
		    int row = 0;
		    while (line != null) {
		    	String [] values = line.split(",");
		    	double maxPayoff = Double.parseDouble(values[1]);
		    	for(int col = 0, item = 0; col < n; col++) {
		    		payoffs[row][col][0] = Double.parseDouble(values[++item]);
		    		payoffs[row][col][1] = Double.parseDouble(values[++item]);
		    		regretTable[row][col] = payoffs[row][col][1];
		    		if(payoffs[row][col][1] > maxPayoff)
						maxPayoff = payoffs[row][col][1];
		    	}
		    	
				// got the maxPayoff now del from every value from the regrettable
				for(int col = 0; col < n; col++)
					regretTable[row][col] = maxPayoff - regretTable[row][col];

		    	line = br.readLine();
		    	row++;
		    }
		    
		    // Created the regret table! Now compute the total regret for each strategies

		    for(int c = 0; c < n; c++) {
		    	int totalRegret = 0;
		    	for(int r = 0; r < n; r++) {
		    		totalRegret += regretTable[r][c];
		    	}
		    	regretTable[n][c] = totalRegret;
		    	log.info(String.format("%s, %.3f,", arrStrategies[c+1],regretTable[n][c]));
		    }
		    
		    
		    
		    System.out.println();
		}
    }
}
