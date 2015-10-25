package trees;

//import static org.junit.Assert.*;
import individualBasedModel.Deme;
import individualBasedModel.DemeType;
import individualBasedModel.Event;
import individualBasedModel.EventType;
//import individualBasedModel.Host;
//import individualBasedModel.InfectionState;
import individualBasedModel.ModelType;
import individualBasedModel.Population;
import individualBasedModel.Scheduler;
import io.NewickWriter;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * 
 * @author Samantha Lycett
 * @created 1 July 2013
 * @version 2 July 2013
 * @version 27 Sept 2013
 */
public class TreePrunerTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		System.out.println("** JUnit Test Tree Pruner **");
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		System.out.println("** END **");
	}

	
	///////////////////////////////////////////////////////////////////////////////////////////
	
	static Scheduler		theScheduler = new Scheduler();
	static TransmissionTree tt;
	static TreePruner		pruner;
	
	///////////////////////////////////////////////////////////////////////////////////////////
	
	public void setUpExampleDeme() {
			
		System.out.println("* Set up example deme *");
		System.out.println("- make one deme of 100 SIR -");
		Deme			deme1		 = new Deme("Deme1");
		deme1.setDemeType(DemeType.MIGRATION_OF_INFECTEDS);
		deme1.setModelType(ModelType.SIR);
		deme1.setHosts(100);
		
		double[] infectionParameters = { 0.1, 0.05 };
		deme1.setInfectionParameters(infectionParameters);
		
		theScheduler.setThePopulation(new Population());
		theScheduler.getThePopulation().getDemes().add(deme1);
		
		System.out.println("- infect the first host -");
		//Host h = theScheduler.getThePopulation().getDemes().get(0).getHosts().get(0);
		//h.setState(InfectionState.INFECTED);
		theScheduler.getThePopulation().setIndexCaseFirstDeme();
		
		System.out.println("- generate the first event and add to Scheduler -");
		
		// make sure it is an infection event for this test
		Event e = theScheduler.getThePopulation().generateEvent(theScheduler.getTime() );
		while (e.getType() != EventType.INFECTION) {
			e = theScheduler.getThePopulation().generateEvent(theScheduler.getTime() );
		}
		
		
		theScheduler.addEvent(e);
		//theScheduler.getThePopulation().getActiveHosts().add(h);
		
		System.out.println("First event in Scheduler:");
		System.out.println(theScheduler.getEvents().get(0).toString());
		
		System.out.println("Run events in Scheduler");
		theScheduler.runEvents(null, null, null, 0);
		
		tt = theScheduler.getTransmissionTree();
	}
	
	
	@Before
	public void setUp() throws Exception {
		setUpExampleDeme();
		pruner = new TreePruner(tt);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testPrune() {
		
		System.out.println("-- testPrune --");
		
		System.out.println("Prune the tree");
		pruner.prune();
		
		System.out.println("Write original tree to file");
		new NewickWriter( "test//treePrunerTest_originalTree", pruner.getFullNewick());
				
		System.out.println("Write pruned tree to file");
		new NewickWriter("test//treePrunerTest_prunedTree", pruner.getPrunedNewick());
		
		System.out.println("Write binary pruned tree to file");
		new NewickWriter("test//treePrunerTest_binaryPrunedTree", pruner.getBinaryPrunedNewick());
		
		//fail("Not yet working");
	}

}
