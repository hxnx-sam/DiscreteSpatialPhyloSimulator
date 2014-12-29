package io;

import individualBasedModel.DemeType;
import individualBasedModel.JustBeforeRecoverySampler;
import individualBasedModel.ModelType;
import individualBasedModel.PopulationType;
import individualBasedModel.Sampler;

import java.util.ArrayList;
import java.util.List;

/**
 * class to read in Deme properties and connections and make XML
 * defaults set for H7N9 work (12 Nov 2013)
 * defaults set for Line & Star comparison work (2 May 2014)
 * @author Samantha Lycett
 * @version 12 Nov 2013
 * @version 2 May 2014
 */
public class WriteStructuredPopulationConfigurationXML {

	String path				= "test//";
	String rootname			= "example_network";
	
	String simpath			= "test//";
	String simname			= "example_network";
	
	int	   echoEvery		= 10000;
	
	long   seed  			= -1;
	int	   nreps 			=  1;
	double tauleap 			= 0;
	
	String 	demeSizesName	= "demeSizes.csv";
	String	edgeListName	= "edgeList.csv";
	
	ReadSimpleNamedParameters 	demeSizes;
	ReadEdgeList  				edgeList;
	//String  dn				= "N";
	

	Sampler 		theSampler		= new JustBeforeRecoverySampler();
	PopulationType 	popType 		= PopulationType.NETWORK;
	ModelType		modelType		= ModelType.SIR;
	//String[]		infectionParams = {"InfectionParameters", "1", "0.5"};
	String[]		infectionParams = {"InfectionParameters", "0.1", "0.05"};
	
	
	Logger logFile;
	

	//////////////////////////////////////////////////////////////////////////////////
	
	public WriteStructuredPopulationConfigurationXML() {
		
	}
	
	//////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * @param path the path to set
	 */
	public void setPath(String path) {
		this.path = path;
	}

	/**
	 * @param rootname the rootname to set
	 */
	public void setRootname(String rootname) {
		this.rootname = rootname;
	}

	/**
	 * @param simpath the simpath to set
	 */
	public void setSimpath(String simpath) {
		this.simpath = simpath;
	}

	/**
	 * @param simname the simname to set
	 */
	public void setSimname(String simname) {
		this.simname = simname;
	}

	/**
	 * @param edgeListName the edgeListName to set
	 */
	public void setEdgeListName(String edgeListName) {
		this.edgeListName = edgeListName;
	}
	
	public void setDemeSizesName(String name) {
		this.demeSizesName = name;
	}

	//////////////////////////////////////////////////////////////////////////////////
	
	public void readEdgeList() {
		edgeList 			= new ReadEdgeList( );
		edgeList.setFname(path + edgeListName);
		edgeList.directed 	= true;
		edgeList.delim 		= "\t";
		edgeList.prefix		= "";
		//edgeList.setApplyScaleToWeights(0.1);
		edgeList.read();
	}
	
	public void readDemeSizes() {
		demeSizes = new ReadSimpleNamedParameters( path + demeSizesName );
	}
	
	public void writeConfigurationFile() {
		openFile();
		writeGeneral();
		writeSampler();
		writeDemes();
		writePopulationStructure();
		closeFile();
	}
	
	//////////////////////////////////////////////////////////////////////////////////
	
	void openFile() {
		logFile = new Logger();
		logFile.setPath(path);
		logFile.setName(rootname+"_params");
		logFile.setExt(".xml");
		logFile.openFile();
		logFile.write("<DSPS>");
	}
	
	void closeFile() {
		logFile.write("</DSPS>");
		logFile.closeFile();
	}
	

	void writeGeneral() {
		
		List<String[]> simParams = new ArrayList<String[]>();
		if (seed > 0) {
			simParams.add(new String[]{"Seed",""+seed});
		}
		simParams.add(new String[]{"Path",simpath});
		simParams.add(new String[]{"Rootname",simname});
		simParams.add(new String[]{"Nreps",""+nreps});
		simParams.add(new String[]{"Tauleap",""+tauleap});
		simParams.add(new String[]{"EchoEvery",""+echoEvery});
		
		logFile.write("<General>");
		logFile.writeParametersXML(simParams,1);
		logFile.write("</General>");
	}
	
	void writeSampler() {
		
		List<String[]> samplingParams = theSampler.getSamplerParameterList();
		
		logFile.write("<Sampler>");
		logFile.writeParametersXML(samplingParams,1);
		logFile.write("</Sampler>");
	}
	
	void writePopulationStructure() {

		List<String[]> populationParams = new ArrayList<String[]>();
		populationParams.add(new String[]{ "NetworkType", popType.toString() } );
		populationParams.add(new String[]{ "Directed", "TRUE"});
		populationParams.add(new String[]{ "ModelType", modelType.toString() } );
		populationParams.add( infectionParams );
		populationParams.add(new String[]{ "DemeType", DemeType.INFECTION_OVER_NETWORK.toString() } );
		
		logFile.write("<PopulationStructure>");
		logFile.writeParametersXML(populationParams, 1);
		logFile.write("</PopulationStructure>");
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////
	// DEMES
	
	private void writeADeme(int demeUID, String demeName, int hostsInDeme, List<String> neighbourNames, List<Double> neighbourWeights) {
		List<String[]> demeParams = new ArrayList<String[]>();
		demeParams.add(new String[]{ "DemeUID", ""+demeUID } );
		demeParams.add(new String[]{ "DemeName", demeName });
		demeParams.add(new String[]{ "NumberOfHosts", ""+hostsInDeme});
		
		// deme neighbours
		int numN    	= neighbourNames.size();
		
		if (numN > 0) {
		
			String[] nn 	= new String[numN+1];
			nn[0]			= "Neighbours";
		
			String[] migs 	= new String[numN+1];
			migs[0]			= "MigrationParameters";
			for (int i = 0; i < numN; i++) {
				nn[i+1] 	= neighbourNames.get(i);
				migs[i+1]	= ""+neighbourWeights.get(i);
			}
		
			demeParams.add(nn);
			demeParams.add(migs);
		
		}
		
		logFile.write("\t<Deme>");
		logFile.writeParametersXML(demeParams, 2);
		logFile.write("\t</Deme>");
		
	}
	
	void writeDemes() {
		logFile.write("<Demes>");
		
		int numDemes = edgeList.numberOfNodes();
		for (int i = 0; i < numDemes; i++) {
			int demeUID 					= i;
			String demeName 				= edgeList.getNodeName(i);
			List<String> neighbourNames 	= edgeList.getNodeNeighbours(i);
			List<Double> neighbourWeights 	= edgeList.getNodeNeighbourEdgeWeights(i);
			int hostsInDeme					= demeSizes.getParameterValue_as_Integer(demeName);
			writeADeme(demeUID, demeName, hostsInDeme, neighbourNames, neighbourWeights);
		}
		
		logFile.write("</Demes>");
	}
	
	////////////////////////////////////////////////////////////////////////////////////
	
	public static void test_H7N9() {
		WriteStructuredPopulationConfigurationXML writer = new WriteStructuredPopulationConfigurationXML();
		
		String path 		 = "D://slycett//H7N9//for_epidemics//DSPS_simulations//";
		//String path			 = "//Users//sam//Documents//data//H7N9//DSPS_simulations//";
		String demeSizesName = "joint_common_NS_locations_demeSizes.txt";
		String edgeListName  = "joint_refine_run34_deme_edgeList.txt";
		String rootname		 = "joint_refine_run34";
		String simpath		 = path + "joint_refine_run34//";
		String simname		 = "joint_refine_run34";
		
		// paths for input deme sizes and edges
		writer.setDemeSizesName(demeSizesName);
		writer.setEdgeListName(edgeListName);
		
		// paths for xml
		writer.setPath(path);
		writer.setRootname(rootname);
		
		// paths for simulations
		writer.setSimpath(simpath);
		writer.setSimname(simname);
		
		writer.readEdgeList();
		writer.readDemeSizes();
		writer.writeConfigurationFile();
		
	}
	
	public static void line_and_star_test() {
		
		String path 		 = "D://slycett//phylo_inference//line_and_star//";
		String demeSizesName = "demeSizes6.txt";
		String[] types		 = {"star6","line6"};
		
		
		for (String tt : types) {

			WriteStructuredPopulationConfigurationXML writer = new WriteStructuredPopulationConfigurationXML();
			writer.nreps 		 = 50;
			
			String edgeListName  = tt+"_edgeList.txt";
			String rootname		 = tt;
			String simpath		 = path + tt + "//";
			String simname		 = tt;
		
			// paths for input deme sizes and edges
			writer.setDemeSizesName(demeSizesName);
			writer.setEdgeListName(edgeListName);
		
			// paths for xml
			writer.setPath(path);
			writer.setRootname(rootname);
		
			// paths for simulations
			writer.setSimpath(simpath);
			writer.setSimname(simname);
		
			writer.readEdgeList();
			writer.readDemeSizes();
			writer.writeConfigurationFile();
		
		}
		
	}
	
	public static void ctsFarmCounty() {
		
		String path 			= "D://slycett//phylo_inference//ctsFarmCounty//";
		String rootname			= "scotgrav";//"scotland";//"farmCounty";
		String demeSizesName 	= rootname + "_demeSizes.txt";
		String edgeListName		= rootname + "_edgeList.txt";
		//String simpath			= path + rootname + "mov//";
		String simpath			= path + rootname + "//";
		String simname			= rootname;
		
		WriteStructuredPopulationConfigurationXML writer = new WriteStructuredPopulationConfigurationXML();
		writer.nreps 		 = 50;
	
		// paths for input deme sizes and edges
		writer.setDemeSizesName(demeSizesName);
		writer.setEdgeListName(edgeListName);
	
		// paths for xml
		writer.setPath(path);
		writer.setRootname(rootname);
	
		// paths for simulations
		writer.setSimpath(simpath);
		writer.setSimname(simname);
	
		writer.readEdgeList();
		writer.readDemeSizes();
		writer.writeConfigurationFile();
		
	}
	
	public static void main(String[] args) {
		System.out.println("** WriteStructuredPopulationConfigurationXML **");
		
		//test_H7N9();
		//line_and_star_test();
		ctsFarmCounty();
		
		System.out.println("** END **");
	}
	
	
}
