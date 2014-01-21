package io;


import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class ParameterReaderTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		System.out.println("** ParameterReader JUnit Test **");
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		System.out.println("** END **");
	}

	/////////////////////////////////////////////////////////////////////////

	ParameterReader pr;
	
	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testReadFile() {
		String fname 	= "test//oneExampleDeme_params_log.xml";
		
		System.out.println("-- testReadFile --");
		System.out.println("Reading "+fname);
		pr 				= new ParameterReader(fname);
		
		List<List<Parameter>> params = pr.getParameters();
		System.out.println("Read "+params.size()+" sets of parameters");
		
		for (List<Parameter> ps : params) {
			System.out.println("Parameter set:");
			for (Parameter p : ps) {
				System.out.println("\t"+p);
			}
		}
		
	}
	
}
