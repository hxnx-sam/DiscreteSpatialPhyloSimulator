package individualBasedModel;

import io.Parameter;
//import networks.NetworkNode;


import java.io.File;
import java.util.*;

import math.Distributions;

/**
 * Class to represent a deme.  A deme contains one or more hosts.
 * Each host within the deme has the same parameters.
 * @author sam
 * @created 15 June 2013
 * @version 4 July 2013
 * @version 24 July 2013
 * @version 5  Sept 2013
 * @version 6  Sept 2013
 * @version 24 Sept 2013 - added Deme.generateEvent in order to generate events from each deme
 * @version 26 Sept 2013 - using hostStates
 * @version 27 Sept 2013 - include SI
 * @version 2  Oct  2013 - implements NetworkNode interface (networks package)
 * @version 3  Oct  2013 - actually changed mind about NetworkNode; use use networks package to generate connectivity patterns and integrate though population
 * @version 11 Oct  2013 - hack for network
 * @version 11 Nov  2013 - changed default for demeType to INFECTION_OVER_NETWORK - this ensures that the cumProbBetweenDemes is initialised properly
 * @version 3  June 2014 - birth and death (pop turnover)
 * @version 20 July 2014 - added ability to set multiple index cases as any host in any deme
 * @version 29 Dec  2014 - added "NeighbourLinkParameters" as synonym of MigrationParameters
 * @version 31 Dec  2014 - modification to hazards 
 * @version 23 Mar  2015 - problems with dummy hosts
 */
//public class Deme implements NetworkNode {
public class Deme {

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// class variables and methods
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	private static long demeCounter 	= -1;

	private static long nextDemeUID() {
		demeCounter++;
		return (demeCounter);
	}
	
	/**
	 * reset deme counter between multiple replicate runs of DiscreteSpatialPhyloSimulator if necessary
	 */
	static void resetDemeCounter() {
		demeCounter = -1;
	}
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// instance variables & methods
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	// instance variables
	protected String 			name 		= null;
	protected long 				uid;
	
	// parameters for between demes
	protected DemeType			demeType			= DemeType.INFECTION_OVER_NETWORK;	    //DemeType.MIGRATION_OF_INFECTEDS;		// migration
																							// NETWORK if allow direct cross deme infection
	protected List<Deme>		neighbours			= null;
	private	  String[]			neighbourDemeNames  = null;									// use if setting from parameters xml file
	protected double[]			migrationParameters = null;
	protected double[]			cumProbBetweenDemes = null;									// only not null if NETWORK
	protected double			bii					= 1;									// probability of infecting within self - is less than 1 if INFECTION_OVER_NETWORK
	protected double			totalMigration 		= 0;
	protected double			birthRate		    = 0;
	protected double			deathRate			= 0;
	
	
	// parameters for individuals within deme
	protected ModelType			modelType 	= ModelType.SIR;								// SIR or SEIR
	protected double[]			infectionParameters;
	
	protected int numberOfHosts				= 10;											// would normally have numberOfHosts = 1 if NETWORK
	protected List<Host> hosts;
	//protected Host dummyHost;
	
	protected int[] hostStates = new int[5];
	
	// not sure need this, 24 oct 2015
	//protected boolean onlyOnePerDeme 		= false;										// special case for network, numberOfHosts = 1 always

	//////////////////////////////////
	// constructors
	
	public Deme() {
		this.uid  = nextDemeUID();
		this.name = "" + uid;
		
		for (int i = 0; i < hostStates.length; i++) {
			hostStates[i] = 0;
		}
		
		// 23 mar 2015
		//dummyHost = new Host(this,"Dummy");
	}
	
	public Deme(String name) {
		this.name = name;
		this.uid  = nextDemeUID();
		
		for (int i = 0; i < hostStates.length; i++) {
			hostStates[i] = 0;
		}
		
		// 23 mar 2015
		//dummyHost = new Host(this,"Dummy");
	}
	
	//////////////////////////////////
	// setters
	
	// set methods for between demes
	
	public void setDemeType(DemeType demeType) {
		this.demeType = demeType;
	}
	
	public void setNeighbours(List<Deme> neighbours) {
		this.neighbours = neighbours;
	}
	
	void addNeighbour(Deme neighb) {
		if (this.neighbours == null) {
			this.neighbours = new ArrayList<Deme>();
		}
		this.neighbours.add(neighb);
	}
	
	/**
	 * if NETWORK then migrationParameters converted to cumulative probability of infection of neighbouring demes
	 * note if probabilities sum to 1 then never attempt to transmit to own deme, e.g. if numberOfHosts = 1
	 * @param migrationParameters
	 */
	public void setMigrationParameters(double[] migrationParameters) {
		this.migrationParameters = migrationParameters;
		
		totalMigration = 0;
		for (int i = 0; i < migrationParameters.length; i++) {
			totalMigration += migrationParameters[i];
		}
		
		if (demeType == DemeType.INFECTION_OVER_NETWORK) {
			// the migration parameters are actually pij for infection to other demes
			// they should sum to < 1 
			// the remainder 1 - sum(pij) = bii = probability of infecting within own deme
			
			cumProbBetweenDemes = new double[migrationParameters.length];
			
			cumProbBetweenDemes[0] = migrationParameters[0];
			for (int i = 1; i < migrationParameters.length; i++) {
				cumProbBetweenDemes[i] = cumProbBetweenDemes[i-1] + migrationParameters[i];
			}
			
			bii					= 1 - cumProbBetweenDemes[migrationParameters.length-1];
			//totalMigration 		= 0;				// this is a component of the migration hazard, but if INFECTION_OVER_NETWORK, then individuals dont actually move
		} else {
			// demeType = MIGRATION_OF_INFECTEDS
		
			/*
			totalMigration = 0;
			for (int i = 0; i < migrationParameters.length; i++) {
				totalMigration += migrationParameters[i];
			}
			*/
			
			// 26 Feb 2014 - think this is necessary after all because of getAnotherDeme
			// divided by total migration to make this relative preference between demes (not including self)
			
			// 1 Jan 2015 - dont think this is needed now at all
			/*
			cumProbBetweenDemes = new double[migrationParameters.length];
						
			cumProbBetweenDemes[0] = migrationParameters[0]/totalMigration;
			for (int i = 1; i < migrationParameters.length; i++) {
					cumProbBetweenDemes[i] = cumProbBetweenDemes[i-1] + migrationParameters[i]/totalMigration;
			}
			*/
			
			// 1 Jan 2015
			// when MIGRATION_OF_INFECTEDS, infections only occurr within demes
			bii = 1;
			
		}
	}
	
	
	public void setBirthRate(double birthRate) {
		this.birthRate = birthRate;
	}
	
	public void setDeathRate(double deathRate) {
		this.deathRate = deathRate;
	}

	// set methods for Hosts in this deme
		
	public void setModelType(ModelType modelType) {
		this.modelType = modelType;
	}
	
	
	
	public void setInfectionParameters(double[] infectionParameters) {
		if (modelType == ModelType.SI) {
			if (infectionParameters.length == 1) {
				this.infectionParameters = infectionParameters;
			} else {
				System.out.println("Deme.setInfectionParameters: WARNING cant set SI infection parameters");
			}
		} else if (modelType == ModelType.SIR) {
			if (infectionParameters.length == 2) {
				this.infectionParameters = infectionParameters;
			} else {
				System.out.println("Deme.setInfectionParameters: WARNING cant set SIR infection parameters");
			}
		} else if (modelType == ModelType.SEIR) {
			if (infectionParameters.length == 3) {
				this.infectionParameters = infectionParameters;
			} else {
				System.out.println("Deme.setInfectionParameters: WARNING cant set SEIR infection parameters");
			}
		} else {
			System.out.println("Deme.setInfectionParameters: WARNING unknown modelType");
			this.infectionParameters = infectionParameters;
		}
		
	}
	
	/**
	 * use to set multiple hosts per deme
	 * @param numberOfHosts
	 */
	public void setHosts(int numberOfHosts) {
		this.numberOfHosts = numberOfHosts;
		hosts			   = new ArrayList<Host>();
		for (int i = 0; i < numberOfHosts; i++) {
			hosts.add(new Host(this));
		}
		
		countHostStates();
	}
	
	/**
	 * use to set just one host per deme - useful if DemeType = NETWORK
	 */
	public void setHost() {
		this.numberOfHosts  = 1;
		hosts				= new ArrayList<Host>();
		hosts.add(new Host(this));
		
		countHostStates();
	}
	
	
	public void addHost(Host host) {
		if (hosts == null) {
			hosts = new ArrayList<Host>();
		}
		
		if (!hosts.contains(host)) {
			host.myDeme = this;
			hosts.add(host);
			
			countHostStates();
		}

		// 3 June 2014 - make sure that the number of hosts is up to date.
		numberOfHosts = hosts.size();
		
	}
	
	/**
	 * set the first host in this deme to be INFECTED
	 */
	public void setIndexCase() {
		if (hosts == null) {
			hosts = new ArrayList<Host>();
		}
		Host h = hosts.get(0);
		h.setState(InfectionState.INFECTED);
		countHostStates();
	}
	
	/**
	 * set any of the SUSCEPTIBLE hosts to be INFECTED 
	 */
	public boolean setAnyIndexCase() {
		boolean ok 	= false;
		Host h 		= getHost(InfectionState.SUSCEPTIBLE);
		
		if (h != null) {
			h.setState(InfectionState.INFECTED);
			System.out.println("Deme.setAnyIndexCase: deme="+name+" host="+h.getNameWithDeme()+" "+h.getState());
			ok 		= true;
		} else {
			System.out.println("Deme.setAnyIndexCase: WARNING cannot get SUSCEPTIBLE host for Deme="+name);
			ok		= false;
		}
		countHostStates();
		
		return ok;
		
	}
	
	public void removeHost(Host host) {
		if (hosts == null) {
			hosts = new ArrayList<Host>();
		}
		
		if (hosts.contains(host)) {
			//28 oct 2015
			//host.myDeme = null;		// actually want hosts to remember the last deme they are in until they are updated, e.g. for DEATH
			hosts.remove(host);
			
			countHostStates();
		}
		
		// 3 June 2014 - make sure that the number of hosts is up to date.
		numberOfHosts = hosts.size();
	}
	
	//////////////////////////////////
	// getters

	public String getName() {
		
		if (this.name == null) {
			this.name = ""+uid;
		}
		
		return this.name;
	}
	
	/**
	 * returns any host in the list from self
	 * @return
	 */
	protected Host getHost() {
		
		if (hosts.size() > 1) {
			int j = Distributions.randomInt(hosts.size());
			return ( hosts.get(j) );
			
		} else if (hosts.size() == 1) {
			return hosts.get(0);
			
		} else {
			return null;
			
		}
				
	}
	
	/**
	 * returns any host which has state = INFECTED
	 * @return
	 */
	protected Host getInfectedHost() {
		List<Host> infectedHosts = new ArrayList<Host>();
		for (Host h : hosts) {
			if (h.getState().equals(InfectionState.INFECTED)) {
				infectedHosts.add(h);
			}
		}
		
		if (infectedHosts.size() > 1) {
			int choice = Distributions.randomInt(infectedHosts.size());
			return infectedHosts.get(choice);
		} else if (infectedHosts.size() == 1) {
			return infectedHosts.get(0);
		} else {
			return null;
		}
	}
	
	/**
	 * returns any host which has state = EXPOSED
	 * @return
	 */
	protected Host getExposedHost() {
		List<Host> exposedHosts = new ArrayList<Host>();
		for (Host h : hosts) {
			if (h.getState().equals(InfectionState.EXPOSED)) {
				exposedHosts.add(h);
			}
		}
		
		if (exposedHosts.size() > 1) {
			int choice = Distributions.randomInt(exposedHosts.size());
			return exposedHosts.get(choice);
		
		} else if (exposedHosts.size() == 1) {
			return exposedHosts.get(0);
		} else {
			return null;
		}
	}
	
	/**
	 * returns all hosts which are infected - use this for sampling
	 * @return
	 */
	protected List<Host> getInfectedHosts() {
		List<Host> infectedHosts = new ArrayList<Host>();
		for (Host h : hosts) {
			if (h.getState().equals(InfectionState.INFECTED)) {
				infectedHosts.add(h);
			}
		}
		return infectedHosts;
	}
	
	
	/**
	 * returns any host which has state = IS (InfectionState)
	 * @param IS
	 * @return
	 */
	
	protected Host getHost(InfectionState IS) {
		List<Host> selectedHosts = new ArrayList<Host>();
		for (Host h : hosts) {
			if (h.getState().equals(IS)) {
				selectedHosts.add(h);
			}
		}
		
		// 23 march 2015
		if (selectedHosts.size() > 1) {
			int choice = Distributions.randomInt(selectedHosts.size());
			return selectedHosts.get(choice);
		} else if (selectedHosts.size() == 1) {
			return selectedHosts.get(0);
		} else {
			return null;
		}
	}
	
	
	
	/*
	protected Deme getAnotherDeme() {
		// TO DO - decide how to choose between self and contacts
		// System.out.println("Deme.getHost - NOT IMPLEMENTED FOR NETWORK YET");
		
		
		// migration parameters are probability of infection between demes
		// choose between demes
		// if max value of cumProbBetweenDemes is 1 then never choose own deme (e.g. numberOfHosts = 1)
		int choice = Distributions.weightedChoice(cumProbBetweenDemes);
		
		return (neighbours.get(choice));
	}
	*/
	
	
	/**
	 * returns any host apart from the input host within this deme,
	 * if there is only one (or zero) hosts in the deme then returns null
	 * 31 dec 2014
	 * @param notThisHost
	 * @return
	 */
	protected Host getAnotherHostFromThisDeme(Host notThisHost) {
		Host anotherHost = null;
		if (hosts.size() > 1) {
			int pos = hosts.indexOf(notThisHost);
			int j 	= Distributions.randomInt(hosts.size()-1);
			if (j >= pos) {
				j = j+1;
			}
			anotherHost = hosts.get(j);
		}
		return anotherHost;
	}
	
	/**
	 * returns any host apart from the input host
	 * if DemeType = NETWORK then this can be from a different deme
	 * @param notThisHost
	 * @return
	 */
	
	protected Host getHost(Host notThisHost) {
		
		Host anotherHost = null;
		if ( (demeType == DemeType.MIGRATION_OF_INFECTEDS) || (neighbours == null) || (neighbours.size() == 0) ) {
			
			anotherHost = getAnotherHostFromThisDeme(notThisHost);
			
		} else if (demeType == DemeType.INFECTION_OVER_NETWORK ) {
			
			// migration parameters are probability of infection between demes
			// choose between demes
			// if max value of cumProbBetweenDemes is 1 then never choose own deme (e.g. numberOfHosts = 1)
			
			int choice = -1;
			
			// 11 Oct 2013
			// THIS IS A HACK FOR NETWORK
			if (numberOfHosts == 1) {
				choice = Distributions.randomInt( neighbours.size() );
			} else {
				choice = Distributions.weightedChoice(cumProbBetweenDemes);
				
			}
			
			if (choice < neighbours.size()) {
				// if another deme
				anotherHost = neighbours.get(choice).getHost();
				
			} else {
				// if own deme
				anotherHost = getAnotherHostFromThisDeme(notThisHost);
				
			}
		}
		
		return anotherHost;
		
	}
	
	
	
	public List<Host> getHosts() {
		return hosts;
	}
	
	
	protected boolean containsHost(Host aHost) {
		return( hosts.contains(aHost) );
	}

	
	////////////////////////////////////////////////////
	// Set & Get Deme Parameters
	
	public List<String[]> getDemeParameterList() {
		List<String[]> params = new ArrayList<String[]>();
		params.add(new String[] {"DemeUID", ""+uid} );
		params.add(new String[] {"DemeName",getName()} );
		params.add(new String[]{"NumberOfHosts",""+hosts.size() } );
		params.add(new String[]{"ModelType",""+modelType} );
		
		if ((birthRate > 0) || (deathRate > 0)) {
			params.add(new String[]{"BirthRate",""+birthRate});
			params.add(new String[]{"DeathRate",""+deathRate});
		}
		
		String[] ip = new String[infectionParameters.length + 1];
		ip[0] = "InfectionParameters";
		for (int i = 0; i < infectionParameters.length; i++) {
			ip[i+1] = ""+infectionParameters[i];
		}
		params.add(ip);
		
		params.add(new String[]{"DemeType",""+demeType} );
		
		if (neighbours != null) {
			String[] np = new String[neighbours.size() + 1];
			np[0] = "Neighbours";
			for (int i = 0; i < neighbours.size(); i++) {
				np[i+1] = neighbours.get(i).getName();
			}
			params.add(np);
		}
		
		if (migrationParameters != null) {
			String[] mp = new String[migrationParameters.length + 1];
			mp[0] = "MigrationParameters";
			for (int i = 0; i < migrationParameters.length; i++) {
				mp[i+1] = ""+migrationParameters[i];
			}
			params.add(mp);
		}
		
		
		
		return params;
	}
	
	public void setDemeParameters(List<Parameter> params) {
		
		// specific to this deme
		if (params.get(0).getParentTag().equals("Deme")) {
			
			for (Parameter p : params) {
				if (p.getId().equals("DemeUID")) {
					uid = Integer.parseInt(p.getValue());
					
				} else if (p.getId().equals("DemeName")) {
					name = p.getValue();
					
				} else if (p.getId().equals("Neighbours")) {
					neighbourDemeNames = p.getValue().split(",");
					
				} else if ( p.getId().equals("MigrationParameters") || (p.getId().equals("NeighbourLinkParameters")) ) {
					String[] mpTxt = p.getValue().split(",");
					double[] mp	   = new double[mpTxt.length];
					for ( int i = 0; i < mpTxt.length; i++) {
						mp[i]		= Double.parseDouble(mpTxt[i]);
					}
					setMigrationParameters(mp);
				}
			}
			
		}
		
		// specific to this deme, or for when all demes are set the same
		if  ( params.get(0).getParentTag().equals("Deme") || params.get(0).getParentTag().equals("PopulationStructure") ) {
			
			for (Parameter p : params) {
				
				if (p.getId().equals("NumberOfHosts")) {
					setHosts( Integer.parseInt(p.getValue() ));
				
				} else if (p.getId().equals("NumberOfHostsPerDeme")) {
					setHosts( Integer.parseInt(p.getValue() ));	
					
				} else if (p.getId().equals("ModelType")) {
					setModelType( ModelType.valueOf( p.getValue() ) );
				
				} else if (p.getId().equals("InfectionParameters")) {
					String[] iptxt = p.getValue().split(",");
					double[] ip    = new double[iptxt.length];
					for ( int i = 0; i < iptxt.length; i++) {
						ip[i] = Double.parseDouble(iptxt[i]);
					}
					setInfectionParameters(ip);
				
				} else if (p.getId().equals("DemeType")) {
					setDemeType( DemeType.valueOf( p.getValue() ) );
				
				} else if (p.getId().equals("ProbabilityInfectionAnyOtherDeme") ||  p.getId().equals("ProbabilityMigrationAnyOtherDeme")) {
					// expect a single number as all neighbouring demes treated equally
					// divide this by number of neighbours
					double probBetween = Double.parseDouble(p.getValue());
					// 24 oct 2015
					if ( (neighbours == null) || (neighbours.size() < 1)) {
					//if ( (neighbours == null) || (neighbours.size() <= 1)) {
					//if (numNeighb < 1) {
						System.out.println("Deme.setDemeParameters - WARNING cant set probability infection because no neighbours to infect");
					} else {
						int    numNeighb   = neighbours.size();
						double p_per_neighb = probBetween/(double)numNeighb;
						double[] migrationParameters = new double[numNeighb];
						for (int i = 0; i < numNeighb; i++) {
							migrationParameters[i] = p_per_neighb;
						}
						this.setMigrationParameters(migrationParameters);
					}
				} else if (p.getId().equals("BirthRate")) {
					setBirthRate( Double.parseDouble( p.getValue() ) );
				} else if (p.getId().equals("DeathRate")) {
					setDeathRate( Double.parseDouble( p.getValue() ) );
				}
				
			}
			
		
								
				//} else {
				//	System.out.println("Deme.setDemeParamers - sorry dont understand "+p.getId()+" "+p.getValue());
				//}
			//}
			
		} else {
			System.out.println("Deme.setDemeParameters - WARNING attempted to set non-deme parameters for this deme, but didnt do anything");
		}
		
		
	}
	
	public void setNeighbours(Population thePopulation) {
		
		if (neighbourDemeNames != null) {
			System.out.println("Deme.setNeighbours - setting neighbours from neighbour names");
			List<Deme> nd  		= new ArrayList<Deme>();
			List<Deme> allDemes = thePopulation.getDemes();
			for ( String ndn : neighbourDemeNames ) {
				Deme tempDeme	= new Deme( ndn );
				if ( allDemes.contains( tempDeme ) ) {
					nd.add( allDemes.get( allDemes.indexOf( tempDeme ) ) );
				}
			}
			neighbours = nd;
			
			// 11 Nov 2013
			System.out.print(name+" neighbours=");
			for (Deme dd : nd) {
				System.out.print("\t"+dd.name);
			}
			System.out.println();
			
		} else {
			//System.out.println("Deme.setNeighbours - WARNING cant set neighbouring demes from the the population because havent set neighbourDemeNames");
			if ( (neighbours != null) && (neighbours.size() > 0) ) {
				//System.out.println("Deme.setNeighbours - setting neighbour names from already set demes");
				neighbourDemeNames = new String[neighbours.size()];
				for (int i = 0; i < neighbours.size(); i++) {
					neighbourDemeNames[i] = neighbours.get(i).getName();
				}
			}
		}
	}
	
	/**
	 * output a string of the neighbours edge list useful for random networks
	 * @return
	 */
	public String getNeighbourEdgeList() {
		String delim = "\t";
		String eol	 = File.pathSeparator;
		StringBuffer txt = new StringBuffer();
		for (int i = 0; i < neighbours.size(); i++) {
			Deme n = neighbours.get(i);
			txt.append(this.name + delim + n.name + delim + migrationParameters[i] + eol);
		}
		
		return txt.toString();
	}
	
	
	////////////////////////////////////////////////////////////////////////////////////
	// EVENT GENERATORS 
	////////////////////////////////////////////////////////////////////////////////////
	
	// experimental 6 Sept 2013
	private void countHostStates() {
		
		int S = 0;
		int E = 0;
		int I = 0;
		int R = 0;
		int M = 0;
		for (Host h : hosts) {
			if (h.state == InfectionState.SUSCEPTIBLE) {
				S++;
			}else if (h.state == InfectionState.EXPOSED) {
				E++;
			} else if (h.state == InfectionState.INFECTED) {
				I++;
			} else if (h.state == InfectionState.RECOVERED) {
				R++;
			} else if (h.state == InfectionState.IMMUNE) {
				M++;
			}
		}
		
		hostStates[0] = S;
		hostStates[1] = E;
		hostStates[2] = I;
		hostStates[3] = R;
		hostStates[4] = M;
		
	}
	
	
	// INCREMENTORS
	protected void incrementS() {
		hostStates[0] = hostStates[0] + 1;
	}
	
	protected void incrementE() {
		hostStates[1] = hostStates[1] + 1;
	}
	
	protected void incrementI() {
		hostStates[2] = hostStates[2] + 1;
	}
	
	protected void incrementR() {
		hostStates[3] = hostStates[3] + 1;
	}
	
	protected void incrementM() {
		hostStates[4] = hostStates[4] + 1;
	}
	
	// DECREMENTORS
	protected void decrementS() {
		hostStates[0] = hostStates[0] - 1;
		
		if (hostStates[0] < 0) {
			hostStates[0] = 0;
		}
	}
	
	protected void decrementE() {
		hostStates[1] = hostStates[1] - 1;
		
		if (hostStates[1] < 0) {
			hostStates[1] = 0;
		}
	}
	
	protected void decrementI() {
		hostStates[2] = hostStates[2] - 1;
		
		if (hostStates[2] < 0) {
			hostStates[2] = 0;
		}
	}
	
	protected void decrementR() {
		hostStates[3] = hostStates[3] - 1;
		
		if (hostStates[3] < 0) {
			hostStates[3] = 0;
		}
	}
	
	protected void decrementM() {
		hostStates[4] = hostStates[4] - 1;
		
		if (hostStates[4] < 0) {
			hostStates[4] = 0;
		}
	}
	
	
	public int numberSusceptible() {
		return hostStates[0];
	}
	
	public int numberExposed() {
		return hostStates[1];
	}
	
	public int numberInfected() {
		return (hostStates[2]);
	}
	
	public int numberRecovered() {
		return hostStates[3];
	}
	
	public int numberImmune() {
		return hostStates[4];
	}
	
	/**
	 * counts the number of hosts in each state, then returns current state of hosts number of S, I, R or S, E, I, R as appropriate in this deme
	 * @return
	 */
	public int[] hostStates() {
		//countHostStates();
		
		int S = numberSusceptible();
		int E = numberExposed();
		int I = numberInfected();
		int R = numberRecovered();
		int M = numberImmune();
		
		if (modelType == ModelType.SI) {
			int[] hstates = {S, I};
			return hstates;
		} else if (modelType == ModelType.SIR) {
			int[] hstates = {S, I, R};
			return hstates;
		} else if (modelType == ModelType.SEIR) {
			int[] hstates = {S, E, I, R};
			return hstates;
		} else {
			int[] hstates = {S, E, I, R, M};
			return hstates;
		}
		
	}
	
	/**
	 * counts the number of hosts in each state, then returns the number in each category of S, E, I, R, M
	 * @return
	 */
	public int[] getSEIRM() {
		countHostStates();
		
		int S = numberSusceptible();
		int E = numberExposed();
		int I = numberInfected();
		int R = numberRecovered();
		int M = numberImmune();
		
		int[] hstates = {S, E, I, R, M};
		return hstates;
	}
	
	/**
	 * returns header corresponding to hostStates, e.g. for writing to csv file
	 * @return
	 */
	public String[] hostStatesHeader() {
		if (modelType == ModelType.SI) {
			String[] hstates = {"S-"+uid, "I-"+uid};
			return hstates;
		} else if (modelType == ModelType.SIR) {
			String[] hstates = {"S-"+uid, "I-"+uid, "R-"+uid};
			return hstates;
		} else if (modelType == ModelType.SEIR) {
			String[] hstates = {"S-"+uid, "E-"+uid, "I-"+uid, "R-"+uid};
			return hstates;
		} else {
			String[] hstates = {"S-"+uid, "E-"+uid, "I-"+uid, "R-"+uid, "M-"+uid};
			return hstates;
		}
	}
	
	/*
	private boolean hasEvent() {
		// how many of my hosts are active ?
		return (( numberExposed() + numberInfected() ) > 0 );
	}
	*/
	
	private double generateExposedToInfectedHazard() {
		double h = 0;
		if (modelType == ModelType.SEIR) {
			h = infectionParameters[0]*(double)numberExposed();		// E -> I , dI ~ a*E, this is 1st rate - 30 Dec 2014
		}
		return h;
	}
	

	/**
	 * uses force of infection = b*S*I
	 * note this is not divided by N yet
	 * note you need to select toHost as susceptible in event generation
	 * @return
	 */
	private double generateInfectSelfHazard() {
		double h = 0;
		
		
		if (numberOfHosts > 1) {
		
			if ((modelType == ModelType.SIR) || (modelType == ModelType.SI))  {
				//h = infectionParameters[0]*(double)numberInfected();
			
				// 31 dec 2014	- b*S*I/N
				h = infectionParameters[0] * (double)numberInfected() * (double)numberSusceptible();// / (double)numberOfHosts;
			
			} else if (modelType == ModelType.SEIR) {
				//h = infectionParameters[1]*(double)numberInfected();	
			
				// dE ~ b*S*I, this is 2nd rate - 30 Dec 2014 
				// 31 dec 2014 - b*S*I		
				h = infectionParameters[1] * (double)numberInfected() * (double)numberSusceptible();// / (double)numberOfHosts;
			
			} else {
				System.out.println("Deme.generateInfectionHazard: WARNING unknown modelType");
			}
			
			// now x by bii
			// bii = 1 if MIGRATION_OF_INFECTEDS and will be less than 1 for INFECTION_OVER_NETWORK
			h = h * bii;
		
		}
		
		return h;
		
	}
	
	/**
	 * generates b * I(i) * S(j) hazard 
	 * note this is not divided by N yet
	 * note you need to select toHost as susceptible in event generation
	 * @param neighbourChoice
	 * @return
	 */
	private double generateInfectOtherHazard(int neighbourChoice) {
		
		double h = 0;
		
		// 1 jan 2015
		//if ( (demeType == DemeType.MIGRATION_OF_INFECTEDS) || (neighbours == null) || (neighbours.size() == 0) ) {
			// there are no neighbours, so cannot infect another deme
			// do nothing
		//	h = 0;
			
		//} else {
			
			double numberSusceptibleInNeighbour = (double)neighbours.get(neighbourChoice).numberSusceptible();
			
			if ((modelType == ModelType.SIR) || (modelType == ModelType.SI))  {
				h = infectionParameters[0] * (double)numberInfected() * numberSusceptibleInNeighbour;
				
			} else if (modelType == ModelType.SEIR) {
				h = infectionParameters[1] * (double)numberInfected() * numberSusceptibleInNeighbour;	
				// dE ~ b*S*I, this is 2nd rate - 30 Dec 2014 
				
			} else {
				System.out.println("Deme.generateInfectionHazard: WARNING unknown modelType");
			}
			
			// now x by pij, which is migrationParameters[j]
			h = h * migrationParameters[neighbourChoice];
			
		//}
		
		
		
		// original code, for when not specifying toHost state when choosing in generate events
		/*
		if ((modelType == ModelType.SIR) || (modelType == ModelType.SI))  {
			h = infectionParameters[0]*(double)numberInfected();
			
		} else if (modelType == ModelType.SEIR) {
			h = infectionParameters[1]*(double)numberInfected();	// dE ~ b*S*I, this is 2nd rate - 30 Dec 2014 
			
		} else {
			System.out.println("Deme.generateInfectionHazard: WARNING unknown modelType");
		}
		*/
		
		return h;
	}
	
	private double generateRecoveryHazard() {
		double h = 0;
		if (modelType == ModelType.SI) {
			// do nothing
		} else if (modelType == ModelType.SIR) {
			h = infectionParameters[1]*(double)numberInfected();			// dR ~ y*I
		} else if (modelType == ModelType.SEIR) {
			h = infectionParameters[2]*(double)numberInfected();			// dR ~ y*I
		} else {
			System.out.println("Deme.generateInfectionHazard: WARNING unknown modelType");
		}
		return h;
	}
	
	/** 
	 * TO DO - NOT SURE ABOUT THE SCALING BY N OR BY DENOMINATORN ??
	 * returns 0 if not MIGRATION_OF_INFECTIONS
	 * @return
	 */
	/*
	private double generateMigrationHazard() {
		double h = 0;
		if (demeType.equals(DemeType.MIGRATION_OF_INFECTEDS)) {
			//for (int i = 0; i < migrationParameters.length; i++) {
			//	h += migrationParameters[i];
			//}
			
			// this is migration of any host within the deme (not just the infecteds)
			h = (double)totalMigration * (double)hosts.size();				// migration to any deme (but this one) x hosts
			
			// migration of infecteds only
			//h = (double)totalMigration * (double)numberInfected();			// migration to any deme (but this one) x infecteds
		}
		return h;
	}
	*/
	
	private double generateBirthHazard() {
		double h = 0;
		
		h = birthRate * (double)numberOfHosts;
		// number of hosts is updated in addHost and removeHost, so this should be OK, but might be as well to check ...
		
		return h;
	}
	
	private double generateDeathHazard() {
		double h = 0;
		
		h = deathRate * (double)numberOfHosts;
		// number of hosts is updated in addHost and removeHost, so this should be OK, but might be as well to check ...
		
		return h;
	}
	
	Hazard generateHazards() {
		Hazard h = new Hazard(this);

		h.setExposedToInfectedHazard( generateExposedToInfectedHazard() );
		
		// 11 Jan 2015
		if ((neighbours == null) || (neighbours.size() == 0)) {
			// if no neighbours then only calculate infect self hazard and normalised by number of hosts
			
			// calculate infect self hazard (bii*S*I)
			// 1 Jan 2015
			double infectSelfHazard = generateInfectSelfHazard();
			
			infectSelfHazard 	= infectSelfHazard/(double)numberOfHosts;
			h.setInfectSelfHazard(infectSelfHazard);
			
		} else if (demeType == DemeType.INFECTION_OVER_NETWORK) {
			// there are some neighbours to infect as well as self
			
			// calculate infect self hazard (bii*S*I)
			// 1 Jan 2015
			double infectSelfHazard = generateInfectSelfHazard();
			
			double denominatorN 	 = bii * numberOfHosts;
			double infectOtherHazard = 0;
			for (int j = 0; j < neighbours.size(); j++) {
					double pijN 		= migrationParameters[j] * (double)neighbours.get(j).numberOfHosts;
					denominatorN 		+= pijN;
					infectOtherHazard 	+= generateInfectOtherHazard(j);
			}
						
			// normalise self hazard by denominatorN
			infectSelfHazard		= infectSelfHazard / denominatorN;
			h.setInfectSelfHazard( infectSelfHazard );
						
			// normalise other hazard by denominatorN
			infectOtherHazard		= infectOtherHazard / denominatorN;
			h.setInfectOtherHazard( infectOtherHazard );
			
		} else if (demeType == DemeType.MIGRATION_OF_INFECTEDS) {
			// calculate infection of self deme and migration between demes
			
			// calculate infect self hazard
			// 1 Jan 2015
			double infectSelfHazard = generateInfectSelfHazard();
			
			// normalised infect self by number in own deme
			infectSelfHazard		= infectSelfHazard / (double)numberOfHosts;
			h.setInfectSelfHazard( infectSelfHazard );
						
			// calculate migration between demes, this can be of an individual in any state
			// this is migration from me
			// (p241 Keeling & Rohani)
			h.setMigrationHazard( (double)totalMigration * (double)numberOfHosts );
		
		}
		
		h.setRecoveryHazard( generateRecoveryHazard() );
		
		h.setBirthHazard( generateBirthHazard() );
		h.setDeathHazard( generateDeathHazard() );
		
		h.getTotalHazard();										// calculates total hazard
		return h;
	}
	
	Event generateEvent(Hazard h, double currentTime, double actionTime) {
		
		/*
		double[] cumRateEvents = new double[4];
		cumRateEvents[0] = h.getExposedToInfectedHazard();					// this could be 0
		cumRateEvents[1] = h.getInfectOtherHazard() + cumRateEvents[0];			
		cumRateEvents[2] = h.getMigrationHazard() + cumRateEvents[1];		// this could be 0
		cumRateEvents[3] = h.getRecoveryHazard() + cumRateEvents[2];		// this could be 0
		*/
		
	  double totalWeights = h.getTotalHazard();
	  if (totalWeights > 0) {
		
		//						0								1							2						3						4					5						6
		double[] weights 	= {h.getExposedToInfectedHazard(), h.getInfectSelfHazard(), h.getInfectOtherHazard(), h.getMigrationHazard(), h.getRecoveryHazard(), h.getBirthHazard(), h.getDeathHazard()};
		
		int eventChoice  	= Distributions.chooseWithWeights(weights, totalWeights);
		
		Event e			 = new Event();
		
		if (eventChoice == 0) {
			// EXPOSED TO INFECTED
			Host aHost = getExposedHost();
			
			if (aHost != null) {
				e.setBecomeInfectiousEvent(aHost, currentTime, actionTime);
			} else {
				e = null;
			}
			
		} else if (eventChoice == 1) {
			// INFECTION with self (own deme)
			// also, only specify the dummy host at this point, if the event is actually performed then choose a susceptible
			
			Host aHost = getInfectedHost();
			
			// 23 mar 2015
			//Host bHost = dummyHost;
			Host bHost = getHost(InfectionState.SUSCEPTIBLE);
			
			/*
			int infectedHostChoice = -1;
			if (numberInfected() == 1) {
				infectedHostChoice = 0;
			} else if (numberInfected() > 1) {
				infectedHostChoice 		= Distributions.randomInt( numberInfected() );
			}
			
			int susceptibleHostChoice = -1;
			if (numberSusceptible() == 1) {
				susceptibleHostChoice = 0;
			} else if (numberSusceptible() > 1) {
				susceptibleHostChoice 	= Distributions.randomInt( numberSusceptible() );
			}
			
			if ( (infectedHostChoice >= 0) && (susceptibleHostChoice >=0)) {
				
				int infectedCount 		= -1;
				int susceptibleCount 	= -1;
				int count				= 0;
				
				Host aHost = null;
				Host bHost = null;
				
				while ( (count < hosts.size() ) && ( ( aHost == null) || ( bHost==null) )  ) {
					Host tempH = hosts.get(count);
					count++;
					
					if (tempH.getState().equals(InfectionState.SUSCEPTIBLE)) {
						susceptibleCount++;
						if (susceptibleCount == susceptibleHostChoice) {
							bHost = tempH;
						}
					}
					
					if (tempH.getState().equals(InfectionState.INFECTED)) {
						infectedCount++;
						if (infectedCount == infectedHostChoice) {
							aHost = tempH;
						}
					}
					
				}
				*/
				
				if ((aHost != null) && (bHost != null)) {
					if (modelType.equals(ModelType.SEIR)) {
						e.setExposureEvent(aHost, bHost, currentTime, actionTime);				
					} else {
						e.setInfectionEvent(aHost, bHost, currentTime, actionTime);
					}
				} else {
					e = null;
				}
				
			//} else {
			//	e = null;
			//}
			
			
		} else if (eventChoice == 2) {
			// CROSS DEME INFECTION
			// cross deme infection only, within own deme is covered above
			
			Host aHost 			= getInfectedHost();
			
			if (aHost != null) {
			
				// also, only specify the dummy host at this point, if the event is actually performed then choose a susceptible
				// int neighbourChoice = Distributions.chooseWithWeights(migrationParameters, totalMigration );
				// Host bHost			= neighbours.get(neighbourChoice).dummyHost;
			
				
				//Host bHost = getHost(aHost);
				//Host bHost = getAnotherDeme().getHost();		// 31 dec 2014
				
				// supposed to find susceptible host here, but what if there isnt one ? esp if numberOfHosts = 1
				// problem for numberOfHosts = 1 because dont know which of neighbs are sus here and should only be choosing from these
				int neighbourChoice = Distributions.chooseWithWeights(migrationParameters, totalMigration );
				Host bHost			= neighbours.get(neighbourChoice).getHost(InfectionState.SUSCEPTIBLE);
			
				if (bHost != null) {
				
					if (modelType.equals(ModelType.SEIR)) {
						e.setExposureEvent(aHost, bHost, currentTime, actionTime);				
					} else {
						e.setInfectionEvent(aHost, bHost, currentTime, actionTime);
					}
					
				} else {
					e = null;
					//System.out.println("Deme.generateEvent couldnot select susceptible host in neighbour choice deme");
				}
			} else {
				e = null;
			}
				
		} else if (eventChoice == 3) {
			// MIGRATION of INFECTEDS (this should never be choosen if INFECTION_OVER_NETWORK)
			
			// this is migration of any host
			Host aHost  			= getHost();
			int neighbourChoice		= Distributions.chooseWithWeights(migrationParameters, totalMigration);
			Deme toDeme				= neighbours.get(neighbourChoice);
			
			// this is migration of infecteds only
			//Host aHost  = getInfectedHost();
			//Deme toDeme = getAnotherDeme();
			
			if ((aHost != null) && (toDeme != null) ) {
				e.setMigrationEvent(aHost, this, toDeme, currentTime, actionTime);
			} else {
				e = null;
			}
			
		} else if (eventChoice == 4) {
			// RECOVERY
			if ( modelType.equals(ModelType.SI) ) {
				e = null;
				System.out.println("Deme.generateEvent - event choice = "+eventChoice+" but this is invalild for "+modelType);
					
			} else {
			
				Host aHost = getInfectedHost();
			
				if (aHost != null) {
					e.setRecoveryEvent(aHost, currentTime, actionTime);
			
				} else {
					e = null;
				}
			
			}
			
		} else if (eventChoice == 5) {
			// BIRTH
			Host aHost 		= new Host(this);
			aHost.setState(InfectionState.SUSCEPTIBLE);
			
			e.setBirthEvent(aHost, this, currentTime, actionTime);
			
		} else if (eventChoice == 6) {
			// DEATH
			// death can be of any host
			Host aHost		= getHost();
			e.setDeathEvent(aHost, this, currentTime, actionTime);
			
		} else {
		
		
			System.out.println("Deme.generateEvent - event choice = "+eventChoice+" but this is invalild");
			e = null;
		}
		
		return e;
		
	  } else {
			return null;
	  }
		
	}
	
	
	
	/**
	 * THIS IS OLD; it assumes that the responsible deme is the from deme
	 * Deme performs event, and also updates the host state count
	 * @param e
	 */
	public void performEvent_old(Event e) {
		
		e.setSuccess(false);			// event success defaults to false
		//Event newEvent = null;
		
		Host toHost 		 = e.getToHost();		// this could be in this deme, or another deme
		Deme toDeme			 = e.getToDeme();		// may or may not be this deme
		
		
		// 11 Jan 2015 this is in Population
		//if ( toHost.equals(toDeme.dummyHost) ) {
		//	// this means that the host has not been set so far, and it should be a susceptible host
		//	toHost = toDeme.getHost(InfectionState.SUSCEPTIBLE);
		//}
		
		InfectionState state = toHost.getState();
		
		if ( e.getFromDeme().equals(this)  ) {	
			
			if ( e.getType() == EventType.EXPOSURE ) {
				
				if ((state == InfectionState.SUSCEPTIBLE ) && (modelType == ModelType.SEIR) ) {
					// event possible
					toHost.setState(InfectionState.EXPOSED);
					e.setSuccess(true);
					
					// S -> E
					decrementS();
					toDeme.incrementE();
					
				} 
				
			} else if ( e.getType() == EventType.INFECTION )  {
				
				if ((state == InfectionState.EXPOSED) && (modelType == ModelType.SEIR)) {
					
					// event possible
					toHost.setState(InfectionState.INFECTED);
					e.setSuccess(true);
					
					// E -> I
					decrementE();
					toDeme.incrementI();
					
				} else {
					// SIR, SI
					if ( state == InfectionState.SUSCEPTIBLE )  {
						// event possible
						toHost.setState(InfectionState.INFECTED);
						e.setSuccess(true);
					
						// S -> I
						decrementS();
						toDeme.incrementI();
					}
				}
				
			} else if ( e.getType() == EventType.RECOVERY ) {
				if (state == InfectionState.INFECTED) {
					toHost.setState( InfectionState.RECOVERED );
					e.setSuccess(true);
					
					// I -> R
					decrementI();
					toDeme.incrementR();
				}
				
			} else if ( e.getType() == EventType.MIGRATION ) {
					// event possible
					// change demes
					
					// remove from this
					removeHost(toHost);
			
					toHost.myDeme = toDeme;
					
					// add to toDeme
					toDeme.addHost(toHost);
					
					e.setSuccess(true);
				//} else {
				//	System.out.println("Deme.performEvent: WARNING just tried to action inappropriate MIGRATION event (wrong Deme)");
				//}
				
			} else if ( e.getType() == EventType.BIRTH ) {
				

				// add the new host to this deme
				if (!hosts.contains(toHost)) {
					addHost(toHost);
					e.setSuccess(true);
				} else {
					e.setSuccess(false);
				}
				
				
			} else if ( e.getType() == EventType.DEATH ) {
				
				// remove the host from this deme
				if (hosts.contains(toHost)) {
					removeHost(toHost);
					e.setSuccess(true);
				} else {
					e.setSuccess(false);
				}
				
				
			} else {
				System.out.println("Deme.performEvent: WARNING cant do event "+e.getType());
			}
			
			
		} else {
			System.out.println("Deme.performEvent: WARNING just tried to action inappropriate event (wrong Deme)");
			
		}
		
		//return newEvent;
		
	}
	
	
	/**
	 * Deme performs event, and also updates the host state count
	 * @param e
	 */
	public void performEvent(Event e) {
		
		e.setSuccess(false);			// event success defaults to false
		//Event newEvent = null;
		
		Deme fromDeme		 = e.getFromDeme();		// might not be from this deme
		//Host fromHost		 = e.getFromHost();		// might not be from this deme
		
		Host toHost 		 = e.getToHost();		// in this deme
		Deme toDeme			 = e.getToDeme();		// in this deme
		
		
		InfectionState state = toHost.getState();
		
		if ( e.getToDeme().equals(this)  ) {	
			
			if ( e.getType() == EventType.EXPOSURE ) {
				
				if ((state == InfectionState.SUSCEPTIBLE ) && (modelType == ModelType.SEIR) ) {
					// event possible
					toHost.setState(InfectionState.EXPOSED);
					e.setSuccess(true);
					
					// S -> E
					decrementS();
					incrementE();
					
				} 
				
			} else if ( e.getType() == EventType.INFECTION )  {
				
				if ((state == InfectionState.EXPOSED) && (modelType == ModelType.SEIR)) {
					
					// event possible
					toHost.setState(InfectionState.INFECTED);
					e.setSuccess(true);
					
					// E -> I
					decrementE();
					incrementI();
					
				} else {
					// SIR, SI
					if ( state == InfectionState.SUSCEPTIBLE )  {
						// event possible
						toHost.setState(InfectionState.INFECTED);
						e.setSuccess(true);
					
						// S -> I
						decrementS();
						incrementI();
					}
				}
				
			} else if ( e.getType() == EventType.RECOVERY ) {
				if (state == InfectionState.INFECTED) {
					toHost.setState( InfectionState.RECOVERED );
					e.setSuccess(true);
					
					// I -> R
					decrementI();
					incrementR();
				}
				
			} else if ( e.getType() == EventType.MIGRATION ) {
					// event possible
					// change demes
					
					// remove from fromDeme
					fromDeme.removeHost(toHost);
			
					toHost.myDeme = toDeme;
					
					// add to toDeme
					toDeme.addHost(toHost);
					
					e.setSuccess(true);
				//} else {
				//	System.out.println("Deme.performEvent: WARNING just tried to action inappropriate MIGRATION event (wrong Deme)");
				//}
				
			} else if ( e.getType() == EventType.BIRTH ) {
				

				// add the new host to this deme
				if (!hosts.contains(toHost)) {
					addHost(toHost);
					e.setSuccess(true);
				} else {
					e.setSuccess(false);
				}
				
				
			} else if ( e.getType() == EventType.DEATH ) {
				
				// remove the host from this deme
				if (hosts.contains(toHost)) {
					removeHost(toHost);
					e.setSuccess(true);
				} else {
					e.setSuccess(false);
				}
				
				
			} else {
				System.out.println("Deme.performEvent: WARNING cant do event "+e.getType());
			}
			
			
		} else {
			System.out.println("Deme.performEvent: WARNING just tried to action inappropriate event (wrong Deme)");
			
		}
		
		//return newEvent;
		
	}
	
	
	// end experimental
	
	////////////////////////////////////////////////////////////////////////////////////
	// INFO METHODS
	////////////////////////////////////////////////////////////////////////////////////
	
	
	@Override
	public String toString() {
		if (name == null) {
			name = ""+uid;
		}
		return name;
		//return "Deme [name=" + name + ", uid=" + uid + "]";
	}
	
	/**
	 * recounts the number of hosts in each state and returns string
	 * @return
	 */
	public String info() {
		
		countHostStates();
		
		String line = "Deme:"+name;
		
		int S = numberSusceptible();
		int E = numberExposed();
		int I = numberInfected();
		int R = numberRecovered();
		int M = numberImmune();
		
		if (modelType == ModelType.SIR) {
			line = line + "\tS="+S+"\tI="+I+"\tR="+R;
		} else if (modelType == ModelType.SEIR) {
			line = line + "\tS="+S+"\tE"+E+"\tI="+I+"\tR="+R;	
		} else {
			line = line + "\tS="+S+"\tE"+E+"\tI="+I+"\tR="+R+"\tM="+M;
		}
		
		return line;
	}
	
	// 24 oct 2015
	String printNeighbours() {
		StringBuffer txt = new StringBuffer();
		for (Deme neighb : neighbours) {
			txt.append(this.name+","+neighb.name+"\n");
		}
		return txt.toString();
	}
	
	
	////////////////////////////////////////////////////////////////////////////////////
	// implementation of NetworkNode interface
	
	/*
	public void 				setNetworkNeighbours(List<NetworkNode> nn) {
		for (NetworkNode n : nn) {
			if (n instanceof Deme) {
				addNeighbour((Deme)n);
			} else {
				System.out.println("Deme.setNetworkNeighbours - cant add "+n.toString());
			}
		}
	}
	
	
	public List<NetworkNode> 	getNetworkNeighbours() {
		List<NetworkNode> nn = new ArrayList<NetworkNode>();
		for (NetworkNode n : neighbours) {
			nn.add(n);
		}
		return nn;
	}
	
	public void 				addNetworkNode(NetworkNode n) {
		if (n instanceof Deme) {
			addNeighbour((Deme)n);
		} else {
			System.out.println("Deme.addNetworkNode - cant add "+n.toString());
		}
	}
	
	public void 				removeNetworkNode(NetworkNode n) {
		if (n instanceof Deme) {
			Deme d = (Deme)n;
			if (neighbours.contains(d)) {
				neighbours.remove(d);
			} else {
				System.out.println("Deme.removeNetworkNode - cant remove "+d.toString()+" not in neighbours");
			}
		} else {
			System.out.println("Deme.removeNetworkNode - cant remove "+n.toString());
		}
	}
	
	public boolean 				hasNetworkNeighbour(NetworkNode b) {
		if (b instanceof Deme) {
			Deme d = (Deme)b;
			return (neighbours.contains(d));
		} else {
			return false;
		}
		
	}
	
	public int					numberOfNeighbours() {
		return neighbours.size();
	}
	
	*/
	
	///////////////////////////////////////////////////////////////////////////////////
	// hashCode and equals on deme name only
	///////////////////////////////////////////////////////////////////////////////////

	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (obj == null) {
			return false;
		} else if (obj instanceof Deme) {
			Deme other = (Deme) obj;
			if (name == null) {
				if (other.name == null) {
					return true;
				} else {
					return false;
				}
			} else {
				return (name.equals(other.name));
			}
		} else if (obj instanceof String) {
			String other = (String)obj;
			if (name != null) {
				return ( name.equals(other) );
			} else {
				return false;
			}
		} else {
			return false;
		}
			
			
	}
	
}
