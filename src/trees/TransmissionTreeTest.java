package trees;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import individualBasedModel.*;

/**
 * 
 * @author  Samantha Lycett
 * @created 19 June 2013
 */
public class TransmissionTreeTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		System.out.println("** TransmissionTree JUnit Test **");
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		System.out.println("** END **");
	}

	///////////////////////////////////////////////////////////////////////////////////////////
	
	static TransmissionTree tt = new TransmissionTree();
	static Deme	deme1		   = new Deme("Deme1");
	static Host host1 		   = new Host(deme1,"Host1");
	static Host host2		   = new Host(deme1,"Host2");
	
	///////////////////////////////////////////////////////////////////////////////////////////
	
	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testMakeNewTransmissionNode() {
		
		System.out.println("- testMakeNewTransmissionNode -");
		System.out.println("TransmissionNodes made but not added into the tree");
		
		System.out.println("Get root");
		System.out.println(tt.treeInfo());
		TransmissionNode rootNode = tt.getRootTransmissionNode();
		
		System.out.println("Make tn1");
		TransmissionNode tn1 = tt.makeNewTransmissionNode(rootNode, host1, 1);
		//tt.nodes.add(tn1);
		System.out.println(tt.treeInfo());
		
		System.out.println("Make tn2");
		TransmissionNode tn2 = tt.makeNewTransmissionNode(rootNode, host2, 2);
		//tt.nodes.add(tn2);
		System.out.println(tt.treeInfo());
		
		System.out.println("Add transmission nodes to tree");
		// add new transmission nodes to transmission tree
		tt.nodes.add(tn1);
		tt.nodes.add(tn2);
		
		System.out.println("Update the most recent transmission nodes");
		// update the most recent transmission node from the fromHost
		tt.mostRecentNodes.remove(rootNode);
		tt.mostRecentNodes.add(tn1);
		tt.mostRecentNodes.add(tn2);
		
		System.out.println(tt.treeInfo());
		//fail("Not yet implemented");
	}

	@Test
	public void testRetrieveTransmissionNode() {
		
		System.out.println("- testRetrieveTransmissionNode -");
		
		System.out.println("Retrieve transmission for host1 from most recent transission nodes list");
		TransmissionNode tn1 = tt.retrieveTransmissionNode(host1);
		System.out.println(tn1.toString());
		
		//fail("Not yet implemented");
	}

	@Test
	public void testProcessEvent() {
		
		System.out.println("- testProcessEvent -");
		
		Host host3 = new Host(deme1, "Host3");
		
		System.out.println("Create event");
		Event e = new Event();
		e.setInfectionEvent(host1, host3, 2, 3.5);
		System.out.println(e.toString());
		
		System.out.println("Process event in tree");
		tt.processEvent(e);
		System.out.println(tt.treeInfo());
		
		//fail("Not yet implemented");
	}

	@Test
	public void testIsRoot() {
		
		System.out.println("- testIsRoot -");
		
		System.out.println("test the root node - should be a root");
		TransmissionNode rootNode = tt.rootNode;
		System.out.println( rootNode.toString() );
		assertTrue( tt.isRoot( rootNode ) );
		assertTrue( tt.isRoot( tt.getRootTransmissionNode() ) );
		assertTrue( tt.isRoot( (TransmissionNode)tt.nodes.get(0) ) );
		
		System.out.println("test nodes corresponding to hosts 1 & 2 - should not be a root");
		assertTrue( !tt.isRoot(tt.retrieveTransmissionNode(host1)) );
		assertTrue( !tt.isRoot(tt.retrieveTransmissionNode(host2)) );
		
	}

	@Test
	public void testIsTip() {
		
		System.out.println("- testIsTip -");
		
		System.out.println("test the most recent nodes (should all be tips)");
		for (TransmissionNode tn : tt.mostRecentNodes ) {
			System.out.println(tn);
			assertTrue( tt.isTip(tn) );
		}
		
		System.out.println("test the root node - should not be a tip");
		TransmissionNode rn = tt.getRootTransmissionNode();
		assertTrue( !tt.isTip(rn) );
		
	}

	@Test
	public void testToNewick() {
		
		System.out.println("- testToNewick -");
		String nwk = tt.toNewick();
		System.out.println(nwk);
		
	}

	@Test
	public void testSamplingEvent() {
		System.out.println("- testSamplingEvent -");
		
		
		System.out.println("Create event");
		Event e = new Event();
		e.setSamplingEvent(host1, 0, 4);
		System.out.println(e.toString());
		
		System.out.println("Process event in tree");
		tt.processEvent(e);
		System.out.println(tt.treeInfo());
	}
	
}
