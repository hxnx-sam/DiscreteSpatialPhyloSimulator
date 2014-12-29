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
 * specifically for a network.
 * It reads the edge list from edgeList.csv, assumes 1 host per deme, and infection parameters, and writes example_network_params.xml
 * @author Samantha Lycett
 * @version 29 Dec 2014
 *
 */
public class WriteNetworkConfigurationXML  implements ConfigurationXMLInterface {

	String path				= "test//";
	String rootname			= "example_network";
	
	String simpath			= "test//";
	String simname			= "example_network";
	long   seed  			= -1;
	int	   nreps 			=  1;
	double tauleap 			= 0;
	
	String	edgeListName	= "example_network_edgeList.csv";
	ReadEdgeList edgeList;
	String  dn				= "N";
	

	Sampler 		theSampler		= new JustBeforeRecoverySampler();
	PopulationType 	popType 		= PopulationType.NETWORK;
	ModelType		modelType		= ModelType.SIR;
	String[]		infectionParams = {"InfectionParameters", "1", "0.25"};
	
	String			reportSummary	= "TRUE";
	
	Logger logFile;
	

	//////////////////////////////////////////////////////////////////////////////////
	
	public WriteNetworkConfigurationXML() {
		
	}
	
	//////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * @param path the path to set
	 */
	@Override
	public void setPath(String path) {
		this.path = path;
	}

	/**
	 * @param rootname the rootname to set
	 */
	@Override
	public void setRootname(String rootname) {
		this.rootname = rootname;
	}

	/**
	 * @param simpath the simpath to set
	 */
	@Override
	public void setSimpath(String simpath) {
		this.simpath = simpath;
	}

	/**
	 * @param simname the simname to set
	 */
	@Override
	public void setSimname(String simname) {
		this.simname = simname;
	}

	@Override
	public void setSeed(long seed) {
		this.seed = seed;
	}
	
	@Override
	public void setNreps(int nreps) {
		this.nreps = nreps;
	}
	
	@Override
	public void setModelType(String mt) {
		this.modelType = ModelType.valueOf(mt);
	}

	@Override
	public void setInfectionParameters(String[] ip) {
		this.infectionParams = ip;
	}
	
	/**
	 * @param edgeListName the edgeListName to set
	 */
	public void setEdgeListName(String edgeListName) {
		this.edgeListName = edgeListName;
	}

	//////////////////////////////////////////////////////////////////////////////////
	
	public void readEdgeList() {
		System.out.println("Reading edge list file = "+path+edgeListName);
		edgeList = new ReadEdgeList( path + edgeListName );
	}
	
	@Override
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
		logFile.setEchoEvery(0);
		logFile.setPath(path);
		logFile.setName(rootname+"_params");
		logFile.setExt(".xml");
		logFile.openFile();
		logFile.write("<DSPS>");
		
		System.out.println("Writing xml parameters to "+logFile.path + logFile.name + logFile.ext);
	}
	
	void closeFile() {
		logFile.write("</DSPS>");
		logFile.closeFile();
		
		System.out.println("Finished writing parameters to "+logFile.path + logFile.name + logFile.ext);
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
		populationParams.add(new String[]{ "ReportSummary", reportSummary} );
		
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
		//migs[0]			= "MigrationParameters";
		migs[0]			= "NeighbourLinkParameters";
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
		
		int numDemes 		= edgeList.numberOfNodes();
		int	totalNeighbs 	= 0;
		for (int i = 0; i < numDemes; i++) {
			int demeUID 				= i;
			String demeName 			= edgeList.getNodeName(i);
			List<String> neighbourNames = edgeList.getNodeNeighbours(i);
			writeADeme(demeUID, demeName, neighbourNames);
			
			totalNeighbs += neighbourNames.size();
		}
		
		logFile.write("</Demes>");
		
		double avLinks = totalNeighbs/numDemes;
		
		System.out.println("Wrote "+numDemes+" demes with average links = "+avLinks+" to *_params.xml");
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
