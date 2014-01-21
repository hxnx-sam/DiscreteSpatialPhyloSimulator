package io;

import individualBasedModel.DemeType;
import individualBasedModel.JustBeforeRecoverySampler;
import individualBasedModel.ModelType;
import individualBasedModel.PopulationType;
import individualBasedModel.Sampler;

import java.util.ArrayList;
import java.util.List;

/**
 * class to create the input xml for DiscreteSpatialPhyloSimulator,
 * specifically for a network
 * @author Samantha Lycett
 *
 */
public class WriteNetworkConfigurationXML {

	String path				= "test//";
	String rootname			= "example_network";
	
	String simpath			= "test//";
	String simname			= "example_network";
	long   seed  			= -1;
	int	   nreps 			=  1;
	double tauleap 			= 0;
	
	String	edgeListName	= "edgeList.csv";
	ReadEdgeList edgeList;
	String  dn				= "N";
	

	Sampler 		theSampler		= new JustBeforeRecoverySampler();
	PopulationType 	popType 		= PopulationType.NETWORK;
	ModelType		modelType		= ModelType.SIR;
	String[]		infectionParams = {"InfectionParameters", "1", "0.5"};
	
	Logger logFile;
	

	//////////////////////////////////////////////////////////////////////////////////
	
	public WriteNetworkConfigurationXML() {
		
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

	//////////////////////////////////////////////////////////////////////////////////
	
	public void readEdgeList() {
		edgeList = new ReadEdgeList( path + edgeListName );
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
		populationParams.add(new String[]{ "NumberOfHostsPerDeme","1" } );
		populationParams.add(new String[]{ "ModelType", modelType.toString() } );
		populationParams.add( infectionParams );
		populationParams.add(new String[]{ "DemeType", DemeType.INFECTION_OVER_NETWORK.toString() } );
		
		logFile.write("<PopulationStructure>");
		logFile.writeParametersXML(populationParams, 1);
		logFile.write("</PopulationStructure>");
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////
	// DEMES
	
	private void writeADeme(int demeUID, String demeName, List<String> neighbourNames) {
		List<String[]> demeParams = new ArrayList<String[]>();
		demeParams.add(new String[]{ "DemeUID", ""+demeUID } );
		demeParams.add(new String[]{ "DemeName", demeName });
		
		// deme neighbours
		int numN    	= neighbourNames.size();
		String[] nn 	= new String[numN+1];
		nn[0]			= "Neighbours";
		
		// equal probability of infected each of the neighbours
		String[] migs 	= new String[numN+1];
		migs[0]			= "MigrationParameters";
		for (int i = 0; i < numN; i++) {
			nn[i+1] 	= neighbourNames.get(i);
			migs[i+1]	= ""+((double)1/(double)numN);
		}
		
		demeParams.add(nn);
		demeParams.add(migs);
		
		logFile.write("\t<Deme>");
		logFile.writeParametersXML(demeParams, 2);
		logFile.write("\t</Deme>");
		
	}
	
	void writeDemes() {
		logFile.write("<Demes>");
		
		int numDemes = edgeList.numberOfNodes();
		for (int i = 0; i < numDemes; i++) {
			int demeUID 				= i;
			String demeName 			= edgeList.getNodeName(i);
			List<String> neighbourNames = edgeList.getNodeNeighbours(i);
			writeADeme(demeUID, demeName, neighbourNames);
		}
		
		logFile.write("</Demes>");
	}
	
	////////////////////////////////////////////////////////////////////////////////////
	
	public static void test() {
		WriteNetworkConfigurationXML writer = new WriteNetworkConfigurationXML();
		writer.readEdgeList();
		writer.writeConfigurationFile();
	}
	
	public static void main(String[] args) {
		System.out.println("** WriteNetworkConfigurationXML **");
		
		test();
		
		System.out.println("** END **");
	}
	
}
