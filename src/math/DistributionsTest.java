package math;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class DistributionsTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		System.out.println("** Distributions Junit Test **");
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		System.out.println("** END **");
	}

	@Before
	public void setUp() throws Exception {
		//MersenneTwisterFast.initialiseWithSeed(12345);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testWeightedChoice() {
		System.out.println("- testWeightedChoice -");
		System.out.println("- with probabilities adding to 1 -");
		double[] probs 		= {0.1, 0.2, 0.3, 0.4};
		double[] cumProb	= new double[probs.length];
		
		cumProb[0]			= probs[0];
		System.out.println(0+"\tprob="+probs[0]+"\tcumProb="+cumProb[0]);
		
		for (int i = 1; i < probs.length; i++) {
			cumProb[i] = cumProb[i-1] + probs[i];
			
			System.out.println(i+"\tprob="+probs[i]+"\tcumProb="+cumProb[i]);
		}
		
		int nreps 			= 100;
		int[] vals			= new int[probs.length];
		for (int i = 0; i < probs.length; i++) {
			vals[i]			= 0;
		}
		
		for (int r = 0; r < nreps; r++) {
			int choice 	 = Distributions.weightedChoice(cumProb);
			vals[choice] = vals[choice]+1;
		}
		
		System.out.println("Number of reps = "+nreps);
		for (int i = 0; i < probs.length; i++) {
			System.out.println(i+"\ttimes chosen = "+vals[i]);
		}
		
		System.out.println("---------------");
		
		
		
	}
	
	@Test
	public void testWeightedChoice2() {
		System.out.println("- testWeightedChoice -");
		System.out.println("- with probabilities not adding to 1 (an extra class at the end) - ");
		
		double[] probs 		= {0.05, 0.05, 0.05, 0.05};
		double[] cumProb	= new double[probs.length];
		
		cumProb[0]			= probs[0];
		System.out.println(0+"\tprob="+probs[0]+"\tcumProb="+cumProb[0]);
		
		for (int i = 1; i < probs.length; i++) {
			cumProb[i] = cumProb[i-1] + probs[i];
			
			System.out.println(i+"\tprob="+probs[i]+"\tcumProb="+cumProb[i]);
		}
		
		int nreps 			= 100;
		int[] vals			= new int[probs.length+1];
		for (int i = 0; i < vals.length; i++) {
			vals[i]			= 0;
		}
		
		for (int r = 0; r < nreps; r++) {
			int choice 	 = Distributions.weightedChoice(cumProb);
			//System.out.println(choice);
			vals[choice] = vals[choice]+1;
		}
		
		System.out.println("Number of reps = "+nreps);
		for (int i = 0; i < vals.length; i++) {
			System.out.println(i+"\ttimes chosen = "+vals[i]);
		}
		
		System.out.println("---------------");
		
		
		
	}
	
	@Test
	public void testChooseWithWeights() {
		System.out.println("- testChooseWithWeights -");
		System.out.println("- with weight to any value - ");
		
		//double[] weights 	= {1, 2, 3, 4};	
		double[] weights 	= {1, 2, 3, 4};	
		double totalWeight  = 0;
		for (int i = 0; i < weights.length; i++) {
			System.out.println(i+"\tweight="+weights[i]);
			totalWeight += weights[i];
		}
		System.out.println("Total Weight = "+totalWeight);
		
		int nreps 			= 10000;
		int[] vals			= new int[weights.length];
		for (int i = 0; i < vals.length; i++) {
			vals[i]			= 0;
		}
		
		for (int r = 0; r < nreps; r++) {
			int choice 	 = Distributions.chooseWithWeights(weights, totalWeight);
			vals[choice] = vals[choice]+1;
		}
		
		System.out.println("Number of reps = "+nreps);
		for (int i = 0; i < vals.length; i++) {
			System.out.println(i+"\ttimes chosen = "+vals[i]);
		}
		
		System.out.println("---------------");
		
		
	}

}
