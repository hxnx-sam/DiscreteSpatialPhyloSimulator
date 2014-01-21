package individualBasedModel;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * 
 * @author sam
 * @created 15 June 2013
 * @version 16 June 2013
 * @vesrion 27 Sept 2013
 */
public class HostTest {

	static Deme 	deme1;
	static Host 	theHost;
	static Event 	anEvent;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		System.out.println("** Host JUnit Test **");
		setUpDeme1();
		theHost = deme1.getHost();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		System.out.println("** END **");
	}
	
	static void setUpDeme1() {
		System.out.println("- make one deme of 100 SIR -");
		deme1 = new Deme("Deme1");
		deme1.setDemeType(DemeType.MIGRATION_OF_INFECTEDS);
		deme1.setModelType(ModelType.SIR);
		deme1.setHosts(100);
		
		double[] infectionParameters = { 0.1, 0.05 };
		deme1.setInfectionParameters(infectionParameters);
		
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testSetState() {
		System.out.println("- testSetState -");
		System.out.println("Initial state\t= "+theHost.state);
		theHost.setState(InfectionState.INFECTED);
		System.out.println("New state\t= "+theHost.state);
		System.out.println();
	}
	
	// Event generated at level of demes
	/*
	@Test
	public void testHasEvent() {
		System.out.println(" - testHasEvent -");
		if (theHost.hasEvent()) {
			System.out.println("PASS");
		} else {
			fail("theHost should be INFECTED and be able to generate events");
		}
		System.out.println();
		
	}

	@Test
	public void testGenerateNextEvent() {
		System.out.println("- testGenerateNextEvent -");
		
		Event e = theHost.generateNextEvent(0);
		while (e.getType() != EventType.INFECTION ) {
			e = theHost.generateNextEvent(0);
		}
		
		System.out.println("Event generated: "+e.toString());
		System.out.println();
		
		anEvent = e;
		
	}
	
	

	

	@Test
	public void testPerformEvent() {
		
		System.out.println("- testPerformEvent -");
		Host anotherHost = anEvent.getToHost();
		anotherHost.performEvent(anEvent);
		
		System.out.println("Event attempted: "+anEvent.toString());
		System.out.println();
		
		
		//fail("Not yet implemented");
	}
	 */
}
