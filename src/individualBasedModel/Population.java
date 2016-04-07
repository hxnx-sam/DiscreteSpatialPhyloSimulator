package individualBasedModel;

import java.util.*;

import math.Distributions;
import io.*;

/**
 * Population class - this contains the demes
 * @author Samantha Lycett
 * @version 1 July 2013
 * @version 24 July 2013
 * @version 27 Sept 2013
 * @version 11 July 2014 - facility to add a number of identical demes (NumberOfDemes), useful for RANDOM network models
 * @version 20 July 2014 - added ability to set multiple index cases as any host in any deme
 * @version 22 July 2014
 * @version 24 Oct 2015
 */
public class Population {

	private	  int totalHosts 		 = -1;
	int 		  initI				 = 0;
	
	private   List<Deme> demes		 = new ArrayList<Deme>();
	//protected List<Host> activeHosts = new ArrayList<Host>();
	private String	 delim		 	 = ",";
	
	private PopulationType popType 	 = PopulationType.FULL;
	private double	 pedge		 	 = 1;						// probability of edge between demes
																// only applicable for popType=RANDOM
	private boolean	 directed	 	 = false;					// only applicable for popType=NETWORK at moment
	
	private List<Parameter> all_deme_params 	= new ArrayList<Parameter>();
																// parameters to apply to all demes
	
	private EventGenerator eventGenerator 	= new EventGenerator();
	
	boolean reportSummary			= false;
	
	public Population() {
		
	}
	
	public String info() {
		String line = "Population:";
		for (Deme d : demes ) {
			line = line + "-" + d.info();
		}
		return line;
	}
	
	///////////////////////////////////////////////////////////////////////////
	// methods for io
	
	public String totalPopulationState() {
		
		int[] ts = totalStates();
		String line = ""+ts[0];
		for (int i = 1; i < ts.length; i++) {
			line = line + delim + ts[i];
		}
		
		return line;
	}
	
	public String totalPopulationStateHeader() {
		String line = "S" + delim + "E" + delim + "I" + delim + "R" + delim + "M";
		return line;
	}
	
	
	/**
	 * returns the host states of each deme, in a format suitable for io
	 * @return
	 */
	public String populationState() {
		
	  if (reportSummary) {
		return totalPopulationState();
	  } else {
		
		String line = "";
		
		for (int i = 0; i < demes.size(); i++) {
			Deme d		  = demes.get(i);
			int[] hstates = d.hostStates();
			
			String tempLine = "" + hstates[0];
			for (int j = 1; j < hstates.length; j++) {
				tempLine = tempLine + delim + hstates[j];
			}
			
			if (i==0) {
				line = tempLine;
			} else {
				line = line + delim + tempLine;
			}
		}
		
		return line;
		}
	}
	
	/**
	 * returns the column headers for the host states in each deme, in a format suitable for io
	 * @return
	 */
	public String populationStateHeader() {
		
	  if (reportSummary) {
		return totalPopulationStateHeader();
	  } else {
		
		String line = "";
		
		for (int i = 0; i < demes.size(); i++) {
			Deme d			  	= demes.get(i);
			String[] hstates 	= d.hostStatesHeader();
			
			String tempLine = "" + hstates[0];
			for (int j = 1; j < hstates.length; j++) {
				tempLine = tempLine + delim + hstates[j];
			}
			
			if (i==0) {
				line = tempLine;
			} else {
				line = line + delim + tempLine;
			}
		}
		
		return line;
	  }
		
	}

	//////////////////////////////////////////////////////////////////////////////////
	// total population state information methods
	
	/**
	 * returns sum of all infected in each deme
	 */
	public int totalInfected() {
		int I = 0;
		for (Deme d : demes) {
			I += d.numberInfected();
		}
		return I;
	}
	
	/**
	 * returns sum of all exposed in each deme
	 * @return
	 */
	public int totalExposed() {
		int E = 0;
		for (Deme d : demes) {
			E += d.numberExposed();
		}
		return E;
	}
	
	/**
	 * returns sum of all exposed in each deme
	 * @return
	 */
	public int totalRecovered() {
		int R = 0;
		for (Deme d : demes) {
			R += d.numberRecovered();
		}
		return R;
	}
	
	/**
	 * returns the total number of hosts in all demes
	 * this is not expected to change, so is calculated once then re-used
	 * @return
	 */
	public int totalHosts() {
		if ( totalHosts <= 0) {
			totalHosts = 0;
			for (Deme d : demes) {
				totalHosts += d.numberOfHosts;
			}
		}
		return totalHosts;
	}
	
	/**
	 * returns sum of all in each class
	 * @return
	 */
	public int[] totalStates() {
		int[] hs = demes.get(0).getSEIRM();
		
		for (int i = 1; i < demes.size(); i++) {
			int[] hs2 = demes.get(i).getSEIRM();
			
			for (int j = 0; j < hs.length; j++) {
				hs[j] = hs[j] + hs2[j];
			}
			
		}
		
		return hs;
	}
	
	/////////////////////////////////////////////////////////////////////
	// access methods for demes
	
	public void setDemes(List<Deme> demes) {
		this.demes = demes;
	}

	public void addDeme(Deme deme) {
		if (this.demes == null) {
			this.demes = new ArrayList<Deme>();
		}
		if (!this.demes.contains(deme)) {
			this.demes.add(deme);
		} else {
			System.out.println("Population.addDeme - sorry cant add Deme "+deme.getName()+" because already in list");
		}
	}
	
	/**
	 * adds one infected host into the first deme
	 */
	public void setIndexCaseFirstDeme() {
		Deme d = demes.get(0);
		d.setIndexCase();
	}
	
	public void setIndexCaseAnyDeme() {
		if (demes.size() > 1) {
			int choice = Distributions.randomInt(demes.size());
			Deme d = demes.get(choice);
			d.setIndexCase();
		} else {
			setIndexCaseFirstDeme();
		}
	}
	
	/**
	 * sets initI index cases, these can be any host in any deme
	 */
	public void setIndexCasesAnyDemes() {
		
		for (int i = 0; i < initI; i++) {
			
			if (demes.size() > 1) {
				boolean again = true;
				int count 	  = 0;
				
				while (again) {
					int choice 	= Distributions.randomInt(demes.size());
					Deme d 		= demes.get(choice);
					boolean ok 	= d.setAnyIndexCase();
					
					again		= ( !ok ) && ( count < (2*totalHosts()) );
					count++;
				}
				
			} else {
				
				Deme d = demes.get(0);
				d.setAnyIndexCase();
			}
		}
		
	}
	
	
	public List<Deme> getDemes() {
		return demes;
	}
	
	
	public List<Host> getInfectedHosts() {
		
		List<Host> activeHosts = new ArrayList<Host>();
		for (Deme d : demes) {
			activeHosts.addAll(d.getInfectedHosts());
		}
		
		return activeHosts;
	}
	
	/**
	 * returns the model type of the first deme (and assumes that all the demes have the same model type)
	 * @return
	 */
	public ModelType getDemesModelType() {
		return ( demes.get(0).modelType );
	}
	
	/**
	 * returns the deme type of the first deme (and assumes that all the demes have the same deme type)
	 * @return
	 */
	public DemeType getDemeType() {
		return ( demes.get(0).demeType );
	}
	
	////////////////////////////////////////////////////////////////////
	// POPULATION STRUCTURE
	// methods for linking demes together
	
	/**
	 * set one way link between deme1 and deme2 ( deme1 -> deme2 )
	 * @param demeName1
	 * @param demeName2
	 */
	private void setDirectedLink(String demeName1, String demeName2) {
		int i = demes.indexOf(demeName1);
		int j = demes.indexOf(demeName2);
		Deme deme1 = demes.get(i);
		Deme deme2 = demes.get(j);
		deme1.addNeighbour(deme2);
	}
	
	/**
	 * set two way link between deme1 and deme2 ( deme1 <-> deme2 )
	 * @param demeName1
	 * @param demeName2
	 */
	private void setLink(String demeName1, String demeName2) {
		int i = demes.indexOf(demeName1);
		int j = demes.indexOf(demeName2);
		Deme deme1 = demes.get(i);
		Deme deme2 = demes.get(j);
		deme1.addNeighbour(deme2);
		deme2.addNeighbour(deme1);
	}
	
	/**
	 * all demes connected to all demes (two connectivity)
	 */
	private void setFullConnectivity() {
		// 24 Oct 2015
		System.out.println("Set FULL connectivity between demes");
		pedge = 1;
		for (int i = 0; i < (demes.size()-1); i++) {
			for (int j = (i+1); j < demes.size(); j++) {
				/*
				Deme deme1 = demes.get(i);
				Deme deme2 = demes.get(j);
				deme1.addNeighbour(deme2);
				deme2.addNeighbour(deme1);
				*/
				demes.get(i).addNeighbour(demes.get(j));
				demes.get(j).addNeighbour(demes.get(i));
				
				System.out.println("Deme "+i+" connected to Deme "+j+" and vice versa");
				System.out.print(demes.get(i).printNeighbours());
				System.out.print(demes.get(j).printNeighbours());
			}
		}
	}
	
	/**
	 * two way connections, 0 - 1 - 2 - 3 - 4 etc
	 */
	private void setLineConnectivity() {
		pedge = 1;
		for (int i = 0; i < (demes.size()-1); i++) {
			int j 	   = i+1;
			Deme deme1 = demes.get(i);
			Deme deme2 = demes.get(j);
			deme1.addNeighbour(deme2);
			deme2.addNeighbour(deme1);
		}
	}
	
	/**
	 * add demes connected to the first one (two way connectivity), 0 - 1, 0 - 2, 0 - 3 etc
	 */
	private void setStarConnectivity() {
		pedge = 1;
		Deme deme0 = demes.get(0);
		for (int i = 1; i < demes.size(); i++) {
			Deme demei = demes.get(i);
			deme0.addNeighbour(demei);
			demei.addNeighbour(deme0);
		}
	}
	
	/**
	 * connect pairs of demes with probability pedge (two way connectivity)
	 */
	private void setRandomConnectivity() {		
		for (int i = 0; i < (demes.size()-1); i++) {
			for (int j = (i+1); j < demes.size(); j++) {
				double x = Distributions.randomUniform();
				if (x <= pedge) {
					Deme deme1 = demes.get(i);
					Deme deme2 = demes.get(j);
					deme1.addNeighbour(deme2);
					deme2.addNeighbour(deme1);
				}
			}
		}
	}
	
	/**
	 * sets the network structure if one of the named types
	 */
	private void setNetworkStructure() {
		if (popType.equals(PopulationType.FULL)) {
			setFullConnectivity();
		} else if (popType.equals(PopulationType.LINE)) {
			setLineConnectivity();
		} else if (popType.equals(PopulationType.STAR)) {
			setStarConnectivity();
		} else if (popType.equals(PopulationType.RANDOM)) {
			setRandomConnectivity();
		}
	}
	
	/**
	 * sets network structure according to popType and pedge
	 * called from setPopulationStructure(List<Parameter> ps)
	 */
	private void setNetworkStructure(List<String[]> demePairs) {
		if (popType.equals(PopulationType.FULL)) {
			setFullConnectivity();
		} else if (popType.equals(PopulationType.LINE)) {
			setLineConnectivity();
		} else if (popType.equals(PopulationType.STAR)) {
			setStarConnectivity();
		} else if (popType.equals(PopulationType.RANDOM)) {
			setRandomConnectivity();
		} else if (popType.equals(PopulationType.NETWORK)) {
			for (String[] demeNames : demePairs) {
				if (directed) {
					setDirectedLink(demeNames[0], demeNames[1]);
				} else {
					setLink(demeNames[0], demeNames[1]);
				}
			}
		} else {
			System.out.println("Population.setNetworkStructure - sorry dont understand type "+popType);
		}
	}
	
	public void setPopulationStructure(List<Parameter> ps) {
		//System.out.println("** Population.setPopulationStructure - NOT IMPLEMENTED YET **");
		List<String[]> demePairs = null;
		
		for (Parameter p : ps) {
			if (p.getId().equals("NumberOfDemes")) {
				
				// SJL 11 July 2014
				// adds a number of blank demes - these will be parameterised by all deme parameters
				// WARNING setting this will cancel out anything set above in the XML to do with <Demes>
				int nDemes = Integer.parseInt( p.getValue() );
				this.demes = new ArrayList<Deme>();
				for (int i = 0; i < nDemes; i++) {
					demes.add( new Deme() );
				}
				
			} else if (p.getId().equals("NetworkType")) {
				popType = PopulationType.valueOf( p.getValue() );
				//setNetworkStructure();
				
			} else if (p.getId().equals("ProbabilityConnect")) {
				pedge   = Double.parseDouble( p.getValue() );
				
			} else if (p.getId().equals("Link")) {
				String[] demeNames = p.getValue().split(",");
				if (demePairs == null) {
					demePairs = new ArrayList<String[]>();
				}
				demePairs.add(demeNames);
				
			} else if (p.getId().equals("Directed")) {
				directed = Boolean.parseBoolean(p.getValue().toLowerCase());
				
			} else if (p.getId().equals("DemeType")) {
				all_deme_params.add(p);
				
			} else if (p.getId().equals("NumberOfHostsPerDeme")) {
				all_deme_params.add(p);
				
			} else if (p.getId().equals("ModelType")) {
				all_deme_params.add(p);
				
			} else if (p.getId().equals("InfectionParameters")) {
				all_deme_params.add(p);
				
			} else if (p.getId().equals("BirthRate")) {
				all_deme_params.add(p);
				
			} else if (p.getId().equals("DeathRate")) {
				all_deme_params.add(p);
				
			} else if (p.getId().equals("ProbabilityInfectionAnyOtherDeme")) {
				all_deme_params.add(p);
				
			} else if (p.getId().equals("ProbabilityMigrationAnyOtherDeme")) {
				all_deme_params.add(p);
				
			} else if (p.getId().equals("ProbabilityInfectionScaledByDemeSize")) {
				System.out.println("Population.setPopulationStructure - Not implemented yet "+p.getId()+" "+p.getValue());
				
			} else if (p.getId().equals("ReportSummary")) {
				reportSummary = Boolean.parseBoolean( p.getValue().toLowerCase() );
				
			} else if (p.getId().equals("InitI")) {
				initI = Integer.parseInt( p.getValue() );
				
			} else {
			
				System.out.println("Population.setPopulationStructure - sorry dont understand "+p);
			}
		}
		
		if (demePairs != null) {
			setNetworkStructure(demePairs);
		} else {
			setNetworkStructure();
		}
	}
	
	/**
	 * sets population structure from deme neighbours if applicable and 
	 * also sets the deme parameters which are the same for all demes
	 */
	public void setPopulationStructure() {
		for (Deme deme : demes) {
			deme.setNeighbours(this);
			
			if (all_deme_params.size() > 0) {
				deme.setDemeParameters(all_deme_params);
			}
		}
	}
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////
	// 24 Sept 2013
	// Demes are generating own hazards
	// Population gets list of all hazards
	
	private List<Hazard> allHazards() {
		List<Hazard> h = new ArrayList<Hazard>();
		
		for (Deme d : demes) {
			Hazard demeHazard = d.generateHazards();
			
			// 24 oct 2015
			if (demeHazard.totalHazard > 0) {
				//System.out.println("Population.allHazards Hazard: "+demeHazard.getMyDeme()+" "+demeHazard.getTotalHazard());
				h.add(demeHazard);
			}
		}
		
		if (h.size() == 0) {
			System.out.println("Population.allHazards no hazards in list");
		}
		
		return h;
	}
	
	/**
	 * Generate an event from the Population
	 * @param currentTime
	 * @return
	 */
	public Event generateEvent(double currentTime) {
		Event e					= eventGenerator.generateEvent(allHazards(), currentTime);
		
		/*
		if (e != null) {
			System.out.println("Population.generateEvent: "+e.toString());
		} else {
			System.out.println("Population.generateEvent: no event");
		}
		*/
		
		
		return e;
	}
	
	/**
	 * perform an Event - will farm out to Demes
	 * @param e
	 */
	protected void performEvent(Event e) {
		
		// try to do this event
		if (e == null) {
			System.out.println("NULL EVENT");
			
		} else if (e.getType() == EventType.SAMPLING) {
			// set success = true to allow the event to be processed in runEvents
			e.success = true;
			
		//} else if (e.getType() == EventType.MIGRATION) {
		//	System.out.println("MIGRATION not implemented yet");
				
		} else {
				
			//Host actor 		= e.getToHost();
			//actor.performEvent(e);
			
			// 11 Jan 2015
			/*
			Host toHost			= e.getToHost();
			if ( toHost.equals(e.getToDeme().dummyHost) ) {
				// this means that the host has not been set so far, and it should be a susceptible host
				toHost = e.getToDeme().getHost(InfectionState.SUSCEPTIBLE);
			}
			*/
			
			//Deme responsibleDeme = e.getResponsibleDeme();
			Deme responsibleDeme = e.getToDeme();
			responsibleDeme.performEvent(e);
			
		}
		
		//return e;
		
	}
	
}
