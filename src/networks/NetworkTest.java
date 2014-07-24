package networks;


import io.Parameter;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class NetworkTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		System.out.println("** NetworkTest JUNIT Test **");
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		System.out.println("** END **");
	}

	////////////////////////////////////////////////////////////////////////////
	
	public int numberOfNodes = 100;
	
	@Before
	public void setUp() throws Exception {
		BasicNode.resetNextID();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testStarNetwork() {
		StarNetwork network = new StarNetwork(numberOfNodes);
		//network.setBasicNodes(numberOfNodes);
		
		
		System.out.println("Network information");
		NetworkInformation ni = new NetworkInformation(network);
		System.out.println(ni);
		
		/*
		System.out.println();
		System.out.println("Network:");
		System.out.println(network);
		*/
	}
	
	@Test
	public void testRandomNetwork() {
		double plink 		  = 2.0/(double)numberOfNodes;
		RandomNetwork network = new RandomNetwork(numberOfNodes, plink);
		
		System.out.println("Network information");
		NetworkInformation ni = new NetworkInformation(network);
		System.out.println(ni);
	}
	
	@Test
	public void testRingNetwork() {
		RingNetwork network = new RingNetwork(numberOfNodes);
		
		System.out.println("Network information");
		NetworkInformation ni = new NetworkInformation(network);
		System.out.println(ni);
	}
	
	@Test
	public void testLineNetwork() {
		LineNetwork network = new LineNetwork(numberOfNodes);
		
		System.out.println("Network information");
		NetworkInformation ni = new NetworkInformation(network);
		System.out.println(ni);
		
	}
	
	@Test
	public void testSpatialNetwork() {
		SpatialModelNetwork network = new SpatialModelNetwork();
		
		System.out.println("Network information (empty)");
		NetworkInformation ni = new NetworkInformation(network);
		System.out.println(ni);
		
		String fname = "test//example_UK_cities.csv";
		String delim = ",";
		network.readNodeLocationsFromFile(fname, delim);
		network.create();
		
		System.out.println("Network information (created)");
		ni = new NetworkInformation(network);
		System.out.println(ni);
		
		System.out.println("First Node:");
		LocationNetworkNode locN = (LocationNetworkNode)network.getNodes().get(0);
		System.out.println(locN.neighboursLine());
		System.out.println(locN.distancesLine());
		System.out.println(locN.weightsLine());
		System.out.println();
		
		System.out.println("Now restrict to 100km");
		network.setParameter(new Parameter("distanceThreshold","100"));
		network.create();
		
		System.out.println("Network information (restricted)");
		ni = new NetworkInformation(network);
		System.out.println(ni);
		

		System.out.println("First Node (restricted):");
		locN = (LocationNetworkNode)network.getNodes().get(0);
		System.out.println(locN.neighboursLine());
		System.out.println(locN.distancesLine());
		System.out.println(locN.weightsLine());
		System.out.println();
		
		System.out.println("- End testSpatialNetwork - ");
		System.out.println();
	}
	
}
