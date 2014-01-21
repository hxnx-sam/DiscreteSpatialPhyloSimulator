package individualBasedModel;


import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class DemeTest {


	static Deme deme1 = new Deme();
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		System.out.println("** Deme JUnit Test **");
		
		System.out.println("- make one deme of 100 SIR -");
		deme1.setDemeType(DemeType.MIGRATION_OF_INFECTEDS);
		deme1.setModelType(ModelType.SIR);
		deme1.setHosts(100);
		
		double[] infectionParameters = { 1, 0.5 };
		deme1.setInfectionParameters(infectionParameters);
		
		System.out.println("Set Index Case");
		deme1.setIndexCase();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		System.out.println("** END **");
	}
	

	/////////////////////////////////////////////////////////////////////
	
	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void testCountStates() {
		System.out.println("-- testCountStates --");
		System.out.println( deme1.info() );
	}

	@Test
	public void testGenerateEvents() {
		System.out.println("-- testGenerateEvents --");
		
		System.out.println("");
		
		int maxReps = 10;
		System.out.println("Generating "+maxReps+" events from "+deme1.name);
		
		for (int i =0; i < maxReps; i++) {
		
			System.out.print( deme1.info()+"\t" );
			Hazard h = deme1.generateHazards();
			System.out.print("Hazard: "+h.getExposedToInfectedHazard()+" "+h.getInfectOtherHazard()+
				" "+h.getMigrationHazard()+" "+h.getRecoveryHazard()+" "+h.getTotalHazard()+"\t");
			Event  e = deme1.generateEvent(h, 0, 0);
			System.out.println( e.getType() );
		
		}
		
		System.out.println("");
		System.out.println("Generating "+maxReps+" events via Event generator");
		EventGenerator eventGenerator = new EventGenerator();
		
		for (int i =0; i < maxReps; i++) {
		
			System.out.print( deme1.info()+"\t" );
			Hazard h = deme1.generateHazards();
			System.out.print("Hazard: "+h.getExposedToInfectedHazard()+" "+h.getInfectOtherHazard()+
				" "+h.getMigrationHazard()+" "+h.getRecoveryHazard()+" "+h.getTotalHazard()+"\t");
			
			Event  e = eventGenerator.generateEvent(h, 0);
			e.setSuccess(true);
			System.out.println( e.getType()+"\t"+e.toOutput() );
		
		}
		
		for (int II = 1; II <= 10; II++) {
			
		
		maxReps = 1000;
		System.out.println("");
		System.out.println("Generating "+maxReps+" events via Event generator");
		int numInfectionEvents = 0;
		int numRecoveryEvents  = 0;
		int numExposureEvents  = 0;
		int numMigrationEvents = 0;
		
		double avInfectionTime = 0;
		double avRecoveryTime  = 0;
		double avExposureTime  = 0;
		double avMigrationTime  = 0;
		

		Hazard h = deme1.generateHazards();
		System.out.print( deme1.info()+"\t" );
		System.out.println("Hazard: "+h.getExposedToInfectedHazard()+" "+h.getInfectOtherHazard()+
			" "+h.getMigrationHazard()+" "+h.getRecoveryHazard()+" "+h.getTotalHazard()+"\t");
		
		for (int i =0; i < maxReps; i++) {
			Event  e = eventGenerator.generateEvent(h, 0);
			if (e.getType() == EventType.INFECTION) {
				numInfectionEvents++;
				avInfectionTime += e.getActionTime();
				
			} else if (e.getType() == EventType.RECOVERY) {
				numRecoveryEvents++;
				avRecoveryTime += e.getActionTime();
				
			} else if (e.getType() == EventType.EXPOSURE) {
				numExposureEvents++;
				avExposureTime += e.getActionTime();
				
			} else if (e.getType() == EventType.MIGRATION ) {
				numMigrationEvents++;
				avMigrationTime += e.getActionTime();
			}
		}
		
		avInfectionTime = avInfectionTime/numInfectionEvents;
		avRecoveryTime  = avRecoveryTime/numRecoveryEvents;
		avExposureTime  = avExposureTime/numExposureEvents;
		avMigrationTime = avMigrationTime/numMigrationEvents;

		System.out.println("Number of Exposure events =\t"+numExposureEvents+"\t av time =\t"+avExposureTime);
		System.out.println("Number of Infection events =\t"+numInfectionEvents+"\t av time =\t"+avInfectionTime);
		System.out.println("Number of Migration events =\t"+numMigrationEvents+"\t av time =\t"+avMigrationTime);
		System.out.println("Number of Recovery  events =\t"+numRecoveryEvents+"\t av time =\t"+avRecoveryTime);
		

		Event fakeEvent = new Event();
		fakeEvent.setInfectionEvent(deme1.getHosts().get(0), deme1.getHosts().get(II), 0, 1);
		deme1.performEvent(fakeEvent);
		
	}
		
	}
}
