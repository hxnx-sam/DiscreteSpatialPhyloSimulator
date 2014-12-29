package io;

import individualBasedModel.DemeType;
import individualBasedModel.JustBeforeRecoverySampler;
import individualBasedModel.ModelType;
import individualBasedModel.PopulationType;
import individualBasedModel.Sampler;

import java.util.ArrayList;
import java.util.List;

/**
 * class to write specific network shape configuration xmls, e.g. FULL, RANDOM, LINE, STAR etc
 * @author slycett
 * @version 29 Dec 2014
 *
 */
public class WriteNetworkShapeConfigurationXML implements ConfigurationXMLInterface {

	String path				= "test//";
	String rootname			= "example_network";
	
	String simpath			= "test//";
	String simname			= "example_network";
	long   seed  			= -1;
	int	   nreps 			=  1;
	double tauleap 			= 0;
	

	Sampler 		theSampler		= new JustBeforeRecoverySampler();
	
	PopulationType 	popType 		= PopulationType.RANDOM;
	double			probConnect		= 0.1;
	String			demeType		= DemeType.INFECTION_OVER_NETWORK.toString();
	String			dirType			= "FALSE";
	int				numberOfDemes	= 100;
	int				hostsInDeme		= 1;
	
	ModelType		modelType		= ModelType.SIR;
	String[]		infectionParams = {"InfectionParameters", "0.1", "0.05"};
	
	String			reportSummary	= "TRUE";
	
	Logger logFile;
	

	//////////////////////////////////////////////////////////////////////////////////
	
	public WriteNetworkShapeConfigurationXML() {
		
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
	
	public void setNumberOfDemes(int numDemes) {
		this.numberOfDemes = numDemes;
	}
	
	public void setHostsPerDeme(int hostsPerDeme) {
		this.hostsInDeme = hostsPerDeme;
	}

	public void setPopulationType(String pt) {
		popType = PopulationType.valueOf(pt);
	}
	
	//////////////////////////////////////////////////////////////////////////////////
	
	@Override
	public void writeConfigurationFile() {
		openFile();
		writeGeneral();
		writeSampler();
		//writeDemes();
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
		populationParams.add(new String[]{ "NumberOfDemes", ""+numberOfDemes });
		populationParams.add(new String[]{ "NetworkType", popType.toString() } );
		populationParams.add(new String[]{ "Directed", dirType});
		populationParams.add(new String[]{ "ProbabilityConnect", ""+probConnect} );
		populationParams.add(new String[]{ "ModelType", modelType.toString() } );
		populationParams.add( infectionParams );
		populationParams.add(new String[]{ "DemeType", demeType } );
		populationParams.add(new String[]{ "NumberOfHostsPerDeme", ""+hostsInDeme});
		populationParams.add(new String[]{ "ReportSummary", reportSummary});
		
		logFile.write("<PopulationStructure>");
		logFile.writeParametersXML(populationParams, 1);
		logFile.write("</PopulationStructure>");
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////
	// DEMES
	
	/*
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
		
		
		
		logFile.write("</Demes>");
	}
	*/
	
	////////////////////////////////////////////////////////////////////////////////////
	
	
	
	public static void random_test() {
		
		//String path 			 = "D://slycett//phylo_inference//networks//sims//";
		String path				 = "test//";
		String[] types		 	 = {"random1000_01","random1000_001","random1000_005","full1000_1"};
		PopulationType[] popType = {PopulationType.RANDOM, PopulationType.RANDOM, PopulationType.RANDOM, PopulationType.FULL};
		int[]  nDemes			 = {1000,1000,1000,1000};
		double probConnect[]	 = {0.1,0.01,0.05,1};
		int[] nreps				 = {100,100,100,100};
		
		for (int i = 0; i < types.length; i++) {

			WriteNetworkShapeConfigurationXML writer = new WriteNetworkShapeConfigurationXML();
			writer.nreps 		 = nreps[i];
			
			String rootname		 = types[i];
			String simpath		 = path;
			String simname		 = types[i];
		
			// paths for xml
			writer.setPath(path);
			writer.setRootname(rootname);
		
			// paths for simulations
			writer.setSimpath(simpath);
			writer.setSimname(simname);
		
			// set parameters
			writer.popType = popType[i];
			writer.numberOfDemes = nDemes[i];
			writer.probConnect = probConnect[i];
			
			writer.writeConfigurationFile();
		
		}
		
	}
	
	public static void main(String[] args) {
		System.out.println("** WriteNetworkConfigurationXML **");
		
		random_test();
		
		System.out.println("** END **");
	}
	
}
