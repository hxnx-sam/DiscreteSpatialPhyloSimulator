package individualBasedModel;

//import math.Distributions;

/**
 * Class to represent an individual host.  All hosts belong to a deme.
 * The demes have the infection parameters and model types
 * @author sam
 * @created 15 June 2013
 * @version 16 June 2013
 * @version 17 June 2013
 * @version 4  July 2013
 * @version 27 Sept 2013 - event generation and action at level of Demes - Host class is simplified now.
 * @version 1  Jan  2015 - clean up
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
	
	protected Deme				myDeme;
	
	// constructors
	public Host(Deme myDeme) {
		this.uid 	= nextHostUID();
		this.name   = "H"+String.format("%07d", uid);	//""+uid;
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
