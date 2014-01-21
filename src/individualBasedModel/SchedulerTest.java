package individualBasedModel;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class SchedulerTest {

	static Scheduler 	theScheduler;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		System.out.println("** Scheduler JUnit Test **");
		theScheduler = new Scheduler();
		//setUpDeme1();
		setUpDeme1_SI();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		System.out.println("** END **");
	}

	static void setUpDeme1() {
		System.out.println("- make one deme of 100 SIR -");
		Deme deme1 = new Deme();
		deme1.setDemeType(DemeType.MIGRATION_OF_INFECTEDS);
		deme1.setModelType(ModelType.SIR);
		deme1.setHosts(100);
		
		double[] infectionParameters = { 0.1, 0.05 };
		deme1.setInfectionParameters(infectionParameters);
		
		theScheduler.setThePopulation(new Population());
		theScheduler.getThePopulation().getDemes().add(deme1);
		
	}
	
	static void setUpDeme1_SI() {
		System.out.println("- make one deme of 100 SI -");
		Deme deme1 = new Deme();
		deme1.setDemeType(DemeType.INFECTION_OVER_NETWORK);
		deme1.setModelType(ModelType.SI);
		deme1.setHosts(1000);
		
		double[] infectionParameters = { 0.001 };
		deme1.setInfectionParameters(infectionParameters);
		
		theScheduler.setThePopulation(new Population());
		theScheduler.getThePopulation().getDemes().add(deme1);
		
		//theScheduler.setStopWhenAllI(true);
	}
	
	///////////////////////////////////////////////////
	
	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void testAddEvent() {
		System.out.println("- infect the first host -");
		//Host h = theScheduler.getThePopulation().getDemes().get(0).hosts.get(0);
		//h.setState(InfectionState.INFECTED);
		theScheduler.thePopulation.setIndexCaseFirstDeme();
		
		System.out.println("- generate the first event and add to Scheduler -");
		
		// make sure it is an infection event for this test
		Event e = theScheduler.thePopulation.generateEvent(theScheduler.time);
		while (e.type != EventType.INFECTION) {
			e = theScheduler.thePopulation.generateEvent(theScheduler.time);
		}
		
		theScheduler.addEvent(e);
		//theScheduler.getThePopulation().activeHosts.add(h);
		
		System.out.println("First event in Scheduler:");
		System.out.println(theScheduler.events.get(0).toString());
	}

	@Test
	public void testRunEvents() {
		System.out.println();
		System.out.println("- TestRunEvents - ");
		
		System.out.println("There are "+theScheduler.events.size()+" events in the Scheduler");
		System.out.println("These are:");
		for (Event e : theScheduler.events) {
			System.out.println(e.toString());
		}
		
		System.out.println("- run events -");
		theScheduler.runEvents(null, null, 0);
	}

}
