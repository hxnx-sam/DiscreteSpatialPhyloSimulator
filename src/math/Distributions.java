package math;

/**
 * adapter class for MersenneTwisterFast random number generation
 * @author  Samantha Lycett
 * @created 23 Nov 2012
 * @version 17 June 2013 - add initialise from seed methods in here
 */
public class Distributions {
	
	public static long initialise() {
		return MersenneTwisterFast.getSeed();
	}
	
	public static void initialiseWithSeed(int seed) {
		MersenneTwisterFast.initialiseWithSeed(seed);
	}

	/*
	public static double randomExponential(double mean) {
		double u = MersenneTwisterFast.getDouble();
		double x = (Math.log(1-u))*mean;
		return x;
	}
	*/
	
	public static double randomGaussian() {
		return MersenneTwisterFast.getGaussian();
	}
	
	public static double randomUniform() {
		return MersenneTwisterFast.getDouble();
	}
	
	public static int randomInt() {
		return MersenneTwisterFast.getInt();
	}
	
	public static int randomInt(int upper) {
		return MersenneTwisterFast.getInt(upper);
	}
	
	/**
	 * chooses index according to weights; if the final weight is not 1 then index longer than array can be returned
	 * @param cumProb
	 * @return
	 */
	public static int weightedChoice(double[] cumProb) {
		double x   = MersenneTwisterFast.getDouble();
		int choice = 0;
		if (x > cumProb[cumProb.length-1]) {
			choice = cumProb.length;
		} else {
			while ( (choice < cumProb.length) && (x > cumProb[choice])  ) {
				choice++;
			}
		}
		
		return choice;
	}
	
	/**
	 * chooses index according to weights; use for when the weights are not normalised to 1 (i.e. when last weight != 1)
	 * @param cumProb
	 * @return
	 */
	/*
	public static int unNormalisedWeightedChoice(double[] cumW) {
		double x   = MersenneTwisterFast.getDouble()*cumW[cumW.length-1] ;
		int choice = 0;
		if (x > cumW[cumW.length-1]) {
			choice = cumW.length;
		} else {
			while ( (choice < cumW.length) && (x > cumW[choice])  ) {
				choice++;
			}
		}
		
		return choice;
	}
	*/
	
	/**
	 * chooses index according to weights, weights are not cumulative or normalised
	 * @param weights
	 * @param totalWeights
	 * @return
	 */
	public static int chooseWithWeights(double[] weights, double totalWeights) {
		double x   = MersenneTwisterFast.getDouble() * totalWeights;
		double ctr = 0;
		int choice = -1;
		//int i      = 0;
		
		//while ( (i < weights.length) && (choice==-1) ) {
		for (int i = 0; i < weights.length; i++) {
			double ctr2 = ctr + weights[i];
			if ( (ctr < x) && (x < ctr2) ) {
				choice = i;
			}
			ctr = ctr2;
			//i++;
		}
		
		return choice;
	}
}
