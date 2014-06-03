package individualBasedModel;

import io.*;
import math.Distributions;
import java.util.*;

/**
 * Main class
 * @author sam
 * @created 19 June 2013
 * @version 25 June 2013
 * @version 1 July 2013
 * @version 24 July 2013 - connections between demes in progress
 * @version 6  Sept 2013
 * @version 27 Sept 2013
 * @version 3  Oct  2013 - SI and SIR are working
 * @version 25 Feb  2014
 * @version 3 June 2014 - added birth & death (SJL)
 * 
 */

public class DiscreteSpatialPhyloSimulator {
	
	//////////////////////////////////////////////////////////////////////////////////
	// class variables
	
	public final static String 				  version 		= "DiscreteSpatialPhyloSimulator - 3 June 2014";
	protected 	 static List<List<Parameter>> params;		// from configuration XML
	
	protected static String		path 	 					= "test//";
	protected static String 	rootname 					= "test";
	protected static int		nreps	 					= 1;
	protected static int		repCounter		 			= 0;
	protected static long		seed;
	protected static double		tauleap						= 0;
	protected static String		stopWhen					= "default";

	/////////////////////////////////////////////////////////////////////////////////
	// instance variables
	
	protected Scheduler theScheduler;
	
	protected Logger	populationLogger;
	protected Logger	eventLogger;
	protected Logger	migrationLogger = null;
	protected Logger	logFile;
	protected int		rep;
	
	
	////////////////////////////////////////////////////////////////////////
	// constructors
	
	public DiscreteSpatialPhyloSimulator() {
		//seed = Distributions.initialise();
		rep	   		 = getNextRep();
		theScheduler = new Scheduler();
	}

	/*
	public DiscreteSpatialPhyloSimulator(int seed) {
		Distributions.initialiseWithSeed(seed);
		this.seed = (long)seed;
		
		theScheduler = new Scheduler();
	}
	*/
		
	///////////////////////////////////////////////////////////////////
	// logging methods
	
	/**
	 * initialise loggers - must be done after the rest of scheduler set up
	 */
	private void initialiseLoggers() {
		
		System.out.println("* Initialising loggers *");
		
		/*
		 * Population log
		 */
		populationLogger = new Logger(path, rootname + "_" +rep + "_popLog", ".csv");
		populationLogger.write(theScheduler.toOutputHeader());
		populationLogger.setEchoEvery(100);
		
		/*
		 * event log
		 */
		if (theScheduler.thePopulation.getDemesModelType() == ModelType.SIR) {
			eventLogger		 = new Logger(path, rootname + "_" +rep + "_infectionEventLog", ".csv", EventType.INFECTION);
		} else if (theScheduler.thePopulation.getDemesModelType() == ModelType.SEIR) {
			eventLogger		 = new Logger(path, rootname + "_" +rep + "_exposureEventLog", ".csv", EventType.EXPOSURE);	
		} else {
			// records everything
			eventLogger		 = new Logger(path, rootname + "_" +rep + "_eventLog", ".csv");
		}
		eventLogger.setEchoEvery(0);
		
		/*
		 * migration log (not always present)
		 */
		if (theScheduler.thePopulation.getDemeType() == DemeType.MIGRATION_OF_INFECTEDS) {
			migrationLogger  = new Logger(path, rootname + "_" + rep + "_migrationEventLog", ".csv", EventType.MIGRATION);
		}
		
		//System.out.println("** WARNING NOT IMPLEMENTED MIGRATION LOGGING YET **");
		
	}
	
	private void writeParametersLog() {
		/*
		 * Parameters log 
		 */
		logFile			 = new Logger(path, rootname + "_" +rep+ "_params_log", ".xml");
		logFile.setEchoEvery(0);
		
		logFile.write("<General>");
		List<String[]> simParams = new ArrayList<String[]>();
		simParams.add(new String[]{"Seed",""+seed});
		simParams.add(new String[]{"Path",path});
		simParams.add(new String[]{"Rootname",rootname});
		simParams.add(new String[]{"Nreps",""+nreps});
		simParams.add(new String[]{"Rep",""+rep});
		simParams.add(new String[]{"Tauleap",""+tauleap});
		simParams.add(new String[]{"StopWhen",stopWhen});
		logFile.writeParametersXML(simParams,1);
		logFile.write("</General>");
		
		logFile.write("<Demes>");
		for (Deme d : theScheduler.thePopulation.getDemes() ) {
			logFile.write("\t<Deme>");
			List<String[]> params = d.getDemeParameterList();
			logFile.writeParametersXML(params,2);
			logFile.write("\t</Deme>");
		}
		logFile.write("</Demes>");
		
		logFile.write("<Sampler>");
		List<String[]> samplingParams = theScheduler.theSampler.getSamplerParameterList();
		logFile.writeParametersXML(samplingParams,1);
		logFile.write("</Sampler>");
		//logFile.closeFile();
	}
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////
	// set up and run methods
	
	/**
	 * set up an example deme for a simple test
	 */
	void setUpExampleDeme() {
		
		
		System.out.println("* Set up example deme *");
		System.out.println("- make one deme of 100 SIR -");
		Deme deme1 = new Deme();
		deme1.setDemeType(DemeType.MIGRATION_OF_INFECTEDS);
		deme1.setModelType(ModelType.SIR);
		deme1.setHosts(100);
		
		double[] infectionParameters = { 0.1, 0.05 };
		deme1.setInfectionParameters(infectionParameters);
		
		theScheduler.setThePopulation(new Population());
		theScheduler.thePopulation.addDeme(deme1);
		
		System.out.println("- infect the first host -");
		theScheduler.thePopulation.setIndexCaseFirstDeme();
		
		System.out.println("- generate the first event and add to Scheduler -");
		
		// make sure it is an infection event for this test
		Event e = theScheduler.thePopulation.generateEvent(theScheduler.time);
		while (e.type != EventType.INFECTION) {
			e = theScheduler.thePopulation.generateEvent(theScheduler.time);
		}
		
		
		theScheduler.addEvent(e);
		//theScheduler.thePopulation.activeHosts.add(h);
		
		System.out.println("First event in Scheduler:");
		System.out.println(theScheduler.events.get(0).toString());
		
		rootname = "oneExampleDeme";

		initialiseLoggers();
		writeParametersLog();
		
		populationLogger.write(theScheduler.toOutput());
		
	}
	
	
	/**
	 * method to initialise simulation from parameters read from xml file
	 * note that this should be called for each separate simulation
	 */
	public void initialise() {
		
		Population thePopulation	 = new Population();
		
		if (!stopWhen.equals("default")) {
			String els[] = stopWhen.split(",");
			for (String cond : els) {
				if (cond.equals("NoI")) {
					theScheduler.setStopWhenNoI(true);
				} else if (cond.equals("AllI")) {
					theScheduler.setStopWhenAllI(true);
				} else if (cond.equals("AllR")) {
					theScheduler.setStopWhenAllR(true);
				} else if (cond.startsWith("maxIts")) {
					int maxits = Integer.parseInt( cond.split("=")[1] );
					theScheduler.setMaxIts(maxits);
				} else if (cond.startsWith("maxTime")) {
					double mt = Double.parseDouble( cond.split("=")[1] );
					theScheduler.setMaxTime(mt);
				} else {
					System.out.println("Sorry cant understand stopWhen="+cond);
				}
			}
		}
		
		// create set of demes and sampler
		for (List<Parameter> ps : params) {
			Parameter firstP = ps.get(0);
			
			if (firstP.getParentTag().equals("Deme")) {
				thePopulation.addDeme( DemeFactory.getDeme(ps) );
				
			} else if (firstP.getParentTag().equals("Sampler")) {
				theScheduler.theSampler = SamplerFactory.getSampler(ps);
				
			}
		}
		
		System.out.println("- connecting demes together in a network -");
		
		
		// link demes together to fully specify population
		// get parameters from xml file
		for (List<Parameter> ps : params) {
			Parameter firstP = ps.get(0);
			
			if (firstP.getParentTag().equals("PopulationStructure")) {
				thePopulation.setPopulationStructure(ps);
			}
		}
		
		// finish off setting the population
		// this might include setting neighbours from demes
		thePopulation.setPopulationStructure();
		
		// add population to scheduler
		theScheduler.setThePopulation(thePopulation);
		
		System.out.println("** Replicate "+rep+" of "+nreps+" **");
		System.out.println("- infect the first host in the first deme -");
		//theScheduler.thePopulation.setIndexCaseAnyDeme();
		theScheduler.thePopulation.setIndexCaseFirstDeme();
		
		System.out.println("- generate the first infection event and add to Scheduler -");
		
		// make sure it is an infection event for this test
		Event e = theScheduler.thePopulation.generateEvent(theScheduler.time);
		while (e.type != EventType.INFECTION) {
			e = theScheduler.thePopulation.generateEvent(theScheduler.time);;
		}
		
		theScheduler.addEvent(e);
		//theScheduler.thePopulation.activeHosts.add(h);
		
		System.out.println("First event in Scheduler:");
		System.out.println(theScheduler.events.get(0).toString());
		
		initialiseLoggers();
		writeParametersLog();
		
		populationLogger.write(theScheduler.toOutput());
		
	}
	
	public void run() {
		System.out.println("- run events -");
		if (tauleap <= 0) {
			theScheduler.runEvents(eventLogger, populationLogger, migrationLogger, 0);
		} else {
			theScheduler.runEvents(eventLogger, populationLogger, migrationLogger, tauleap);
		}
		System.out.println("- end state -");
		System.out.println(theScheduler.toOutput());
	}
	
	public void finish() {
		if (populationLogger != null) {
			populationLogger.closeFile();
		}
		
		if (eventLogger != null) {
			eventLogger.closeFile();
		}
		
		if (migrationLogger != null) {
			migrationLogger.closeFile();
		}
		
		logFile.closeFileWithStamp();
		
		System.out.println("* Write transmission trees to file *");
		PrunedTreeWriter treesOut = new PrunedTreeWriter(path + rootname + "_" +rep, theScheduler.tt);
		treesOut.writeNewickTrees();
		
	}
	
	////////////////////////////////////////////////////////////////
	// class methods
	
	private int getNextRep() {
		repCounter++;
		return repCounter;
	}
	
	public static void readParametersFromXML(String xmlName) {
		ParameterReader pr 			 = new ParameterReader(xmlName);
		params 						 = pr.getParameters();
		
		// set global parameters
		for (List<Parameter> ps : params) {
			if (ps.get(0).getParentTag().equals("General")) {
				for (Parameter p : ps) {
					if (p.getId().equals("Seed")) {				
						seed = Long.parseLong(p.getValue());
						Distributions.initialiseWithSeed((int)seed);
					} else if (p.getId().equals("Rootname")) {
						rootname = p.getValue();
					} else if (p.getId().equals("Path")) {
						path = p.getValue();
					} else if (p.getId().equals("Nreps")) {
						nreps = Integer.parseInt(p.getValue());
					} else if (p.getId().equals("Tauleap")) {
						tauleap = Double.parseDouble(p.getValue());
					} else if (p.getId().equals("StopWhen")) {
						stopWhen = p.getValue();
					} else {
						System.out.println("DiscreteSpatialPhyoSimulator.readParametersFromXML - sorry couldnt understand "+p.getId()+" "+p.getValue());
					}
				}
			}
		}
	}
	
	/**
	 * example for testing
	 */
	static void doExample() {
		
		long t1 = System.currentTimeMillis();
		
		seed = 283397407;
		Distributions.initialiseWithSeed((int)seed);
		
		DiscreteSpatialPhyloSimulator dsps = new DiscreteSpatialPhyloSimulator();
		dsps.setUpExampleDeme();
		dsps.run();
		dsps.finish();
		
		long t2 = System.currentTimeMillis();
		System.out.println("* This example took = "+(t2-t1)+" milli seconds *");
		
		System.out.println("* End Example *");
	}

	
	static void exampleFromXML() {
		//String xmlName = "test//deme1000_tau0_params.xml";
		String xmlName = "test//example_structure_params.xml";
		runFromXML(xmlName);
	}
	
	static void validation() {
		repCounter = 0;
		runFromXML("test//simpleSI_params.xml");
		repCounter = 0;
		runFromXML("test//simpleSIR_params.xml");
	}
	
	static void runFromXML(String xmlName) {
		readParametersFromXML(xmlName);
		
		for (int i = 0; i < nreps; i++) {

			long t1 = System.currentTimeMillis();
			
			DiscreteSpatialPhyloSimulator dsps = new DiscreteSpatialPhyloSimulator();
			dsps.initialise();
			dsps.run();
			dsps.finish();
			
			Host.resetHostCounter();
			Deme.resetDemeCounter();
			
			long t2 = System.currentTimeMillis();
			System.out.println("* This replicate took = "+(t2-t1)+" milli seconds *");
			
		}
		
	}
	
	/**
	 * method to run DSPS from xml file (enter filename)
	 * @param args
	 */
	public static void run(String[] args) {
		
		if ( (args == null) || (args.length == 0) ) {
			Scanner keyboard = new Scanner(System.in);
			boolean again 	 = true;
			while (again) {
				System.out.println("Please enter configuration parameter file xml, include // path separators and .xml extension:");
				System.out.println("e.g. test//simpleSIR_params.xml:");
				String xmlName = keyboard.nextLine().trim();
				if (xmlName.equals("validation")) {
					validation();
					again = false;
				} else if (!xmlName.equals("x")) {
					runFromXML(xmlName);
				
					System.out.println("Again ? (y/n)");
					String ans = keyboard.nextLine().trim().toLowerCase();
					if (ans.startsWith("y")) {
						again = true;
					} else {
						again = false;
					}
				} else {
					again = false;
				}
			}
			keyboard.close();
		} else {
			if (args[0].equals("validation")) {
				validation();
			} else {
				for (String xmlName : args) {
					runFromXML(xmlName);
				}
			}
		}
		
	}
	
	public static void main(String[] args) {
		System.out.println("** DiscreteSpatialPhyloSimulator **");
		
		//doExample();
		//exampleFromXML();
		run(args);
		
		System.out.println("** END **");
	}
	
	
}
