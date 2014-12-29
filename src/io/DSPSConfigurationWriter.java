package io;

import individualBasedModel.ModelType;
import individualBasedModel.PopulationType;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * class to write general DSPS configuration xmls
 * @author slycett
 * @created 29 Dec 2014
 * @version 29 Dec 2014
 *
 */
public class DSPSConfigurationWriter {

	private static Scanner 						keyboard = new Scanner(System.in);
	private static String						simType;
	private static ConfigurationXMLInterface 	writer;
	
	private static String chooseASetting(String question, List<String> choices, List<String> explains) {
		
		System.out.println(question+" :");
		for (int i = 0; i < choices.size(); i++) {
			System.out.println( "("+(i+1)+")\t"+choices.get(i)+"\t = "+explains.get(i) );
		}
		System.out.println("Please enter choice name, choice number or x to stop");
		String ans = keyboard.nextLine().trim();
		
		if (ans.equalsIgnoreCase("x")) {
			return null;
		} else if (choices.contains(ans)) {
			System.out.println("\t- "+ans+" chosen");
			return ans;
		} else {
			try {
				Integer choiceNumber = Integer.parseInt(ans);
				if ( (choiceNumber > 0) && (choiceNumber <= choices.size()) ) {
					choiceNumber = choiceNumber - 1;
					ans			 = choices.get(choiceNumber);
					System.out.println("\t- "+ans+" chosen");
					return ans;
				} else {
					System.out.print("Sorry cant accept "+ans+" will use ");
					ans = choices.get(0);
					System.out.println(ans+" instead");
					return ans;
				}
				
			} catch (NumberFormatException e) {
				System.out.println("Sorry cant accept "+ans);
				e.printStackTrace();
				return null;
			}
		}
		
	}
	
	private static void chooseSimulationType() {
		String question 		= "Please choose a simulation type";
		List<String> choices	= new ArrayList<String>();
		List<String> explains   = new ArrayList<String>();
		
		choices.add("ONE-DEME");
		choices.add("TWO-DEME");
		choices.add("SHAPED-NETWORK");
		choices.add("GENERAL-NETWORK");
		choices.add("STRUCTURED-POPULATION");
		
		explains.add("Simple 1 deme simulation");
		explains.add("Simple 2 deme simulation (e.g. 2 host types)");
		explains.add("Network (of 1 host per deme) of various specified shapes, e.g. LINE, STAR, RANDOM, FULL");
		explains.add("Network (of 1 host per deme) with edges input from edgeList input file");
		explains.add("Network containing many hosts per deme and arbitary edges, requires demeSizes and edgeList files");
		
		simType = chooseASetting(question, choices, explains);
		if (simType.equals("ONE-DEME")) {
			writer = new WriteNetworkShapeConfigurationXML();
		} else if (simType.equals("TWO-DEME")) {
			writer = new WriteNetworkShapeConfigurationXML();
		} else if (simType.equals("SHAPED-NETWORK")) {
			writer = new WriteNetworkShapeConfigurationXML();
		} else if (simType.equals("GENERAL-NETWORK")) {
			writer = new WriteNetworkConfigurationXML();
		} else if (simType.equals("STRUCTURED-POPULATION")) {
			writer = new WriteStructuredPopulationConfigurationXML();
		}
		
	}
	
	private static void setModelType() {
		
		System.out.println("Typical model type = SIR with infection parameters = 0.1, 0.05");
		
		String question 	 	= "Please enter model type";
		List<String> choices 	= new ArrayList<String>();
		List<String> explains 	= new ArrayList<String>();
		
		for (ModelType mt : ModelType.values()) {
			choices.add(mt.name());
			int numParams = mt.name().length()-1;
			explains.add("( model requires "+numParams+" infection parameters )");
		}
		
		String mt = chooseASetting(question, choices, explains);
		writer.setModelType(mt);
		
		int nn		 			 = mt.length();
		String[] infectionParams = new String[nn];
		infectionParams[0]		 = "InfectionParameters";
		for (int i = 1; i < nn; i++) {
			System.out.println("Enter infection parameter number "+i+":");
			String ip = keyboard.nextLine().trim();
			infectionParams[i] = ip;
		}
		writer.setInfectionParameters(infectionParams);
		
	}
	
	private static void setFileNames() {
		
		System.out.println("Please enter path (include //) for xml configuration file (e.g. test//):");
		String path = keyboard.nextLine().trim();
		
		System.out.println("Please enter rootname for xml configuration file (e.g. "+simType.replace("-", "_")+"):");
		String rootname = keyboard.nextLine().trim();
		
		System.out.println("Please enter path for simulation output files or x to use path ("+path+"):");
		String simpath = keyboard.nextLine().trim();
		if (simpath.equalsIgnoreCase("x")) {
			simpath = path;
		}
		
		System.out.println("Please enter name for simulation output files or x to use rootname ("+rootname+"):");
		String simname = keyboard.nextLine().trim();
		if (simname.equalsIgnoreCase("x")) {
			simname = rootname;
		}
		
		writer.setPath(path);
		writer.setRootname(rootname);
		writer.setSimpath(simpath);
		writer.setSimname(simname);
		
	}
	
	private static void setSeed() {
		System.out.println("Please enter seed value or -1 to leave unset:");
		String ans = keyboard.nextLine().trim();
		long seed  = -1;
		try {
			seed = Long.parseLong(ans);	
		} catch (NumberFormatException e) {
			System.out.println("Seed not accepted, so using "+seed);
		}
		if (seed > 0) {
			writer.setSeed(seed);
		}
	}
	
	private static void setNreps() {
		System.out.println("Please enter number of reps for simulations:");
		String ans = keyboard.nextLine().trim();
		int nreps  = 1;
		try {
			nreps = Integer.parseInt(ans);	
		} catch (NumberFormatException e) {
			System.out.println("Nreps not accepted, so using "+nreps);
		}
		if (nreps >= 0) {
			writer.setNreps(nreps);
		} else {
			writer.setNreps(1);
		}
	}
	
	
	//////////////////////////////////////////////////////////////////////////////////////
	
	private static void setup() {
		
		chooseSimulationType();
		
		
		if (writer instanceof WriteNetworkShapeConfigurationXML) {
			WriteNetworkShapeConfigurationXML netWriter = (WriteNetworkShapeConfigurationXML)writer;
			
			int numDemes 	 = 1;
			int hostsPerDeme = 1;
			
			if (simType.equals("ONE-DEME")) {
				numDemes = 1;
				
				System.out.println("Please enter the number of hosts per deme");
				String ans = keyboard.nextLine().trim();
				try {
					hostsPerDeme = Integer.parseInt(ans);
				} catch (NumberFormatException e) {
					hostsPerDeme = 1000;
					System.out.println("Error, but setting number of hosts per deme = "+hostsPerDeme);
				}
				
				netWriter.setPopulationType("FULL");
				netWriter.reportSummary = "FALSE";
				
			} else if (simType.equals("TWO-DEME")) {
				numDemes = 2;
				
				System.out.println("Please enter the number of hosts per deme");
				String ans = keyboard.nextLine().trim();
				try {
					hostsPerDeme = Integer.parseInt(ans);
				} catch (NumberFormatException e) {
					hostsPerDeme = 500;
					System.out.println("Error, but setting number of hosts per deme = "+hostsPerDeme);
				}
				
				netWriter.setPopulationType("FULL");
				netWriter.reportSummary = "FALSE";
				
			} else if (simType.equals("SHAPED-NETWORK")) {
				System.out.println("Please enter the number of demes");
				String ans = keyboard.nextLine().trim();
				try {
					numDemes = Integer.parseInt(ans);
				} catch (NumberFormatException e) {
					numDemes = 100;
					System.out.println("Error, but setting number of demes = "+numDemes);
				}
				hostsPerDeme = 1;
				
				String question 	 	= "Please enter network shape type";
				List<String> choices 	= new ArrayList<String>();
				List<String> explains 	= new ArrayList<String>();
				for (PopulationType pt : PopulationType.values()) {
					if (pt != PopulationType.NETWORK) {
						choices.add(pt.name());
						explains.add("( "+pt.name().toLowerCase()+" network )");
					}
				}
				String popType = chooseASetting(question, choices, explains);
				netWriter.setPopulationType(popType);
				
			} else {
				System.out.print("Sorry "+simType+" is not supported");
				numDemes 	 = 100;
				hostsPerDeme = 1000;
				System.out.println(" but setting number of demes = "+numDemes+" and hosts per deme = "+hostsPerDeme);
			}
			
			netWriter.setNumberOfDemes(numDemes);
			netWriter.setHostsPerDeme(hostsPerDeme);
			
		} else if (writer instanceof WriteNetworkConfigurationXML ) {
			
			System.out.println("Please enter edge list file name:");
			String edgeListName = keyboard.nextLine().trim();
			((WriteNetworkConfigurationXML)writer).edgeListName = edgeListName;
			
		} else if (writer instanceof WriteStructuredPopulationConfigurationXML) {
			

			System.out.println("Please enter edge list file name:");
			String edgeListName = keyboard.nextLine().trim();
			((WriteStructuredPopulationConfigurationXML)writer).edgeListName = edgeListName;
			
			System.out.println("Please enter deme sizes file name:");
			String demeSizesName = keyboard.nextLine().trim();
			((WriteStructuredPopulationConfigurationXML)writer).demeSizesName = demeSizesName;
			
			
		} else {
			
			System.out.println("Sorry "+writer.getClass()+" not supported yet");
			
		}
		
		setModelType();
		setFileNames();
		setSeed();
		setNreps();
		
	}
	
	public static void run() {
		setup();
		writer.writeConfigurationFile();
	}
	
	public static void main (String[] args) {
		System.out.println("** DSPSConfigurationWriter **");
		
		run();
		
		System.out.println("** END **");
	}
	
}
