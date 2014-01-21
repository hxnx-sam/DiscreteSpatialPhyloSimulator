package individualBasedModel;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.*;

public class EventTest {

	static Deme deme1 = new Deme();
	static Host host1 = new Host(deme1);
	static Host host2 = new Host(deme1);
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		System.out.println("** Event JUnit Test **");
		host1.state = InfectionState.INFECTED;
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		System.out.println("** End Event JUnit Test **");
	}

	/////////////////////////////////////////////////////////
	
	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testSetInfectionEvent() {
		
		System.out.println("- testSetInfectionEvent -");
		System.out.println("test creating a simple infection event (no action)");
		Event e = new Event();
		e.setInfectionEvent(host1, host2, 0, 1);
		System.out.println("toString():");
		System.out.println(e.toString());
		
		System.out.println("Pretend that it was actioned - set success to true");
		e.success = true;
		System.out.println("toOutput():");
		System.out.println(e.toOutput());
		
	}
	
	@Test
	public void testOrderingOfEvents() {
		System.out.println("- testOrderingOfEvents -");
		System.out.println("test to make sure that the events come in the correct order when sorted");
		
		System.out.println("Initialise events in reverse order");
		int numEvents = 10;
		List<Event> events = new ArrayList<Event>();
		for (int i = (numEvents-1); i > 0; i--) {
			Event e = new Event();
			e.setInfectionEvent(host1, host2, 0, i);
			System.out.println(e.toString());
			events.add(e);
		}
		
		System.out.println("Sort events");
		Collections.sort(events);
		System.out.println("Events sorted according to action time");
		System.out.println("List of events has size = "+events.size());
		for (int i = 0; i < events.size(); i++) {
			Event e = events.get(i);
			System.out.println(e.toString());
		}
		
		
	}
	
	@Test
	public void testSamplingOrder() {
		System.out.println("- test order of SAMPLING events -");
		
		List<Event> events = new ArrayList<Event>();
		
		Event e1 = new Event();
		e1.setBecomeInfectiousEvent(host1, 0, 1);
		Event e2 = new Event();
		e2.setSamplingEvent(host1, 0, 1);
		Event e3 = new Event();
		e3.setInfectionEvent(host2, host1, 0, 1);
		
		events.add(e1);
		events.add(e2);
		events.add(e3);
		
		System.out.println("Events in original order");
		System.out.println("List of events has size = "+events.size());
		for (int i = 0; i < events.size(); i++) {
			Event e = events.get(i);
			System.out.println(e.toString());
		}
		
		System.out.println("Sort events");
		Collections.sort(events);
		System.out.println("Events sorted according to action time");
		System.out.println("List of events has size = "+events.size());
		for (int i = 0; i < events.size(); i++) {
			Event e = events.get(i);
			System.out.println(e.toString());
		}
		
		
	}

}
