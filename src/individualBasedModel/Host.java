package individualBasedModel;

import math.Distributions;

/**
 * Class to represent an individual host.  All hosts belong to a deme.
 * The demes have the infection parameters and model types
 * @author sam
 * @created 15 June 2013
 * @version 16 June 2013
 * @version 17 June 2013
 * @version 4 July 2013
 * @version 27 Sept 2013 - event generation and action at level of Demes - Host class is simplified now.
 */

public class Host {
	
	///////////////////////////////////
	// class variables and methods
	private static long hostCounter 	= -1;
	
	private static long nextHostUID() {
		hostCounter++;
		return (hostCounter);
	}
	
	/**
	 * reset the host counter between multiple replicate runs of DiscreteSpatialPhyloSimulator
	 */
	static void resetHostCounter() {
		hostCounter = -1;
	}
	
	///////////////////////////////////
	// instance variables & methods

	// instance variables
	protected String 			name = null;
	protected long	 		 	uid;
	protected InfectionState 	state 		= InfectionState.SUSCEPTIBLE;
	
	/*
	protected ModelType			modelType 	= ModelType.SIR;
	protected double[]			infectionParameters;
	*/
	
	protected Deme				myDeme;
	
	// constructors
	public Host(Deme myDeme) {
		this.uid 	= nextHostUID();
		this.name   = ""+uid;
		this.myDeme = myDeme;
	}
	
	public Host(Deme myDeme, String name) {
		this.uid  	= nextHostUID();
		this.name 	= name;
		this.myDeme = myDeme;
	}
	
	//////////////////////////////////
	// setters
	
	public void setState(InfectionState state) {
		this.state = state;
	}
	
	///////////////////////////////////
	// getters
	
	public InfectionState getState() {
		return state;
	}
	
	public String getName() {
		if (this.name == null) {
			this.name = ""+uid;
		}
		return this.name;
	}
	
	public String getNameWithDeme() {
		return (getName() + "_" + myDeme.getName());
	}
	
	public long getUid() {
		return uid;
	}
	
	//////////////////////////////////////////////////////
	// EVENT GENERATORS
	//////////////////////////////////////////////////////
	
	/**
	 * returns true if the host is potentially able to generate an event
	 * (i.e. not susceptible or recovered etc)
	 * @return
	 */
	/*
	public boolean hasEvent() {
		if ( (state == InfectionState.EXPOSED) || (state == InfectionState.INFECTED) ) {
			return true;
		} else {
			return false;
		}
	}
	*/
	
	/**
	 * generateNextEvent - the host generates a new event if it is able, else returns null
	 * @param currentTime
	 * @return
	 */
	/*
	public Event generateNextEvent(double currentTime) {
		
		Event e = null;
			
		if (hasEvent()) {
		
			if ( state == InfectionState.EXPOSED ) {
				// generate event for transition to INFECTED (SEIR)
				e		 			= generateEventExposed_to_Infectious(currentTime);
			
			} else if ( state == InfectionState.INFECTED ) {
				// could either infect another individual or do recovery event
				e					= generateEventFromInfected(currentTime);
			}
		}
		// else no event to generate so return null
		
		return e;
	}
*/
	
	////////////////////////////////////////////////////////////////////
	// private methods for event generation

	/////////////////////////////////////////////////////////////////
	// EXPOSED -> INFECTED of self
	
	/*
	private Event generateEventExposed_to_Infectious(double currentTime) {
		// this uses infection parameter 1
		// 0 = Sus -> Exp
		// 1 = Exp -> Inf
		// 2 = Inf -> Rec
		
		if (myDeme.modelType != ModelType.SEIR) {
			System.out.println("Host.calculateTime_Exposed_to_Infectious: WARNING not SEIR !!");
		}
		
		double rate_become_infectious = myDeme.infectionParameters[1];
		double time_to_event 		  = Distributions.randomExponential(-rate_become_infectious);
		
		Event e			 			= new Event();
		double actionTime			= currentTime + time_to_event;
		e.setBecomeInfectiousEvent(this, currentTime, actionTime);
		
		return (e);
	}
	*/
	
	/////////////////////////////////////////////////////////////////
	// INFECTED -> INFECT another or RECOVER
	
	/*
	private Event generateEventFromInfected(double currentTime) {
		
		Event e = new Event();
		
		double rate_infect			= Double.MAX_VALUE/2;
		double rate_recover			= Double.MAX_VALUE/2;
		
		if (myDeme.modelType==ModelType.SIR) {
			rate_infect				= myDeme.infectionParameters[0];
			rate_recover			= myDeme.infectionParameters[1];
		} else if (myDeme.modelType == ModelType.SEIR) {
			rate_infect				= myDeme.infectionParameters[0];
			rate_recover			= myDeme.infectionParameters[2];
		} else {
			System.out.println("Host.generatedEventFromInfected: WARNING unknown modelType");
		}
		
		double rate_any			= rate_infect + rate_recover;
		double time_to_event	= Distributions.randomExponential(-rate_any);
		double actionTime 		= currentTime + time_to_event;
		
		// weighted choice between two events
		double x				= Distributions.randomUniform()*rate_any;
		boolean choose_infect	= x < rate_infect;
		
		if (choose_infect) {
			// generate infection event
			Host toHost = chooseHostToInfect();
			if (toHost == null) {
				e = null;
			} else {
				e.setInfectionEvent(this, toHost, currentTime, actionTime);
			}
			
		} else {
			// generate recovery event
			e.setRecoveryEvent(this, currentTime, actionTime);
		}
		
		return e;
	}
	*/
	
	//////////////////////////////////////////////////
	// if INFECTED and have decided to INFECT another
	
	/*
	private Host chooseHostToInfect() {
		Host h = myDeme.getHost(this);
		return h;
	}
	*/
	
	// end of EVENT GENERATION methods
	/////////////////////////////////////////////////////////////////////////
		
	/////////////////////////////////////////////////////////////////////////
	// EVENT ACTION methods
	/////////////////////////////////////////////////////////////////////////
	
	/**
	 * performs input event e if applicable to this host and is possible
	 * also sets success field in input event
	 * @param e
	 * @return
	 */
	/*
	public void performEvent(Event e) {
		
		e.setSuccess(false);			// event success defaults to false
		//Event newEvent = null;
		
		if (e.getToHost().equals(this)) {
			
			if ( e.getType() == EventType.EXPOSURE ) {
				if ((state == InfectionState.SUSCEPTIBLE ) && (myDeme.modelType == ModelType.SEIR) ) {
					// event possible
					state = InfectionState.EXPOSED;
					e.setSuccess(true);
					
					// S -> E
					myDeme.decrementS();
					myDeme.incrementE();
					
				} 
				
			} else if ( e.getType() == EventType.INFECTION )  {
				if ((state == InfectionState.EXPOSED) && (myDeme.modelType == ModelType.SEIR)) {
					// event possible
					state = InfectionState.INFECTED;
					e.setSuccess(true);
					
					// E -> I
					myDeme.decrementE();
					myDeme.incrementI();
					
				} else if ((state == InfectionState.SUSCEPTIBLE) && (myDeme.modelType == ModelType.SIR)) {
					// event possible
					state = InfectionState.INFECTED;
					e.setSuccess(true);
					
					// S -> I
					myDeme.decrementS();
					myDeme.incrementI();
					
				}
				
			} else if ( e.getType() == EventType.RECOVERY ) {
				if (state == InfectionState.INFECTED) {
					state = InfectionState.RECOVERED;
					e.setSuccess(true);
					
					// I -> R
					myDeme.decrementI();
					myDeme.incrementR();
				}
				
			} else if ( e.getType() == EventType.MIGRATION ) {
				Deme fromDeme = e.getFromDeme();
				Deme toDeme   = e.getToDeme();
				
				if (fromDeme.equals(myDeme)) {
					// event possible
					// change demes
					
					// remove from fromDeme
					// fromDeme.remove(this);
					
					// add to toDeme
					// toDeme.add(this);
					
					// add to toDeme
					myDeme = toDeme;
					e.setSuccess(true);
				}
				
			} else {
				System.out.println("Host.performEvent: WARNING cant do event "+e.getType());
			}
			
			
		} else {
			System.out.println("Host.performEvent: WARNING just tried to action inappropriate event (wrong host)");
		}
		
		//return newEvent;
		
	}
	*/
	
	// end of EVENT ACTION methods
	/////////////////////////////////////////////////////////////////////////

	/////////////////////////////////////////////////////////////////////////
	// comparison methods and toString

	@Override
	public String toString() {
		return "Host [name=" + name + ", uid=" + uid + ", state=" + state
				+ ", myDeme=" + myDeme + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (uid ^ (uid >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Host other = (Host) obj;
		if (uid != other.uid)
			return false;
		return true;
	}
	
	
	
}
