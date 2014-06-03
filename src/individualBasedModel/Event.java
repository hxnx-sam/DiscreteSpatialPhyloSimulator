package individualBasedModel;

/**
 * 
 * @author sam
 * @created 15 June 2013
 * @version 17 June 2013
 * @version 26 Sept 2013
 * @version 5  Oct  2013
 * @version 3 June 2014 - added birth and death events
 */
public class Event implements Comparable<Event> {

	protected EventType type;
	protected Host		fromHost = null;
	protected Host		toHost   = null;
	protected Deme		fromDeme = null;
	protected Deme		toDeme   = null;
	protected double	creationTime;
	protected double	actionTime;
	protected boolean	success;
	
	protected String	delim = ",";
	
	// constructors
	public Event() {
		
	}
	
	////////////////////////////////////////////////
	// set methods
	
	/**
	 * Infection for SIR
	 * @param fromHost
	 * @param toHost
	 * @param creationTime
	 * @param actionTime
	 */
	public void setInfectionEvent(Host fromHost, Host toHost, double creationTime, double actionTime) {
		this.fromHost 		= fromHost;
		this.toHost   		= toHost;
		this.creationTime 	= creationTime;
		this.actionTime   	= actionTime;
		this.type			= EventType.INFECTION;
		

		this.fromDeme		= fromHost.myDeme;
		this.toDeme			= toHost.myDeme;
	}
	
	/**
	 * Exposure for SEIR
	 * @param fromHost
	 * @param toHost
	 * @param creationTime
	 * @param actionTime
	 */
	public void setExposureEvent(Host fromHost, Host toHost, double creationTime, double actionTime) {
		this.fromHost 		= fromHost;
		this.toHost   		= toHost;
		this.creationTime 	= creationTime;
		this.actionTime   	= actionTime;
		this.type			= EventType.EXPOSURE;
		

		this.fromDeme		= fromHost.myDeme;
		this.toDeme			= toHost.myDeme;
	}
	
	/** 
	 * Becoming Infectious after Exposure for SEIR
	 * @param theHost
	 * @param creationTime
	 * @param actionTime
	 */
	public void setBecomeInfectiousEvent(Host theHost, double creationTime, double actionTime) {
		this.fromHost 		= theHost;
		this.toHost   		= theHost;
		this.creationTime 	= creationTime;
		this.actionTime   	= actionTime;
		this.type			= EventType.INFECTION;
		
		this.fromDeme		= theHost.myDeme;
		this.toDeme			= theHost.myDeme;
	}
	
	/**
	 * Recovered after Infection for SIR or SEIR
	 * @param theHost
	 * @param creationTime
	 * @param actionTime
	 */
	public void setRecoveryEvent(Host theHost, double creationTime, double actionTime) {
		this.fromHost		= theHost;
		this.toHost			= theHost;
		this.creationTime 	= creationTime;
		this.actionTime   	= actionTime;
		this.type			= EventType.RECOVERY;
		
		this.fromDeme		= theHost.myDeme;
		this.toDeme			= theHost.myDeme;
	}
	
	/**
	 * Migration of one host from a deme to another deme
	 * @param theHost
	 * @param fromDeme
	 * @param toDeme
	 * @param creationTime
	 * @param actionTime
	 */
	public void setMigrationEvent(Host theHost, Deme fromDeme, Deme toDeme, double creationTime, double actionTime) {
		this.fromHost		= theHost;
		this.toHost			= theHost;
		this.fromDeme		= fromDeme;
		this.toDeme			= toDeme;
		this.creationTime 	= creationTime;
		this.actionTime   	= actionTime;
		this.type			= EventType.MIGRATION;
	}
	
	/**
	 * Sampling a host
	 * @param theHost
	 * @param creationTime
	 * @param actionTime
	 */
	public void setSamplingEvent(Host theHost, double creationTime, double actionTime) {
		this.fromHost		= theHost;
		this.toHost			= theHost;
		this.creationTime 	= creationTime;
		this.actionTime   	= actionTime;
		this.type			= EventType.SAMPLING;
		this.success		= true;
		
		this.fromDeme		= theHost.myDeme;
		this.toDeme			= theHost.myDeme;
	}

	/**
	 * Birth of one host in a deme; the new host is already supplied (and should have deme set to the input deme)
	 * @param aHost
	 * @param theDeme
	 * @param creationTime
	 * @param actionTime
	 */
	public void setBirthEvent(Host aHost, Deme theDeme, double creationTime, double actionTime) {
		this.fromDeme	    = theDeme;
		this.toDeme		    = theDeme;
		this.fromHost		= aHost;
		this.toHost			= aHost;
		this.creationTime	= creationTime;
		this.actionTime		= actionTime;
		this.type			= EventType.BIRTH;
	}
	
	/**
	 * Death of one host in a deme
	 * @param aHost
	 * @param theDeme
	 * @param creationTime
	 * @param actionTime
	 */
	public void setDeathEvent(Host aHost, Deme theDeme, double creationTime, double actionTime) {
		this.fromDeme	    = theDeme;
		this.toDeme		    = theDeme;
		this.fromHost		= aHost;
		this.toHost			= aHost;
		this.creationTime	= creationTime;
		this.actionTime		= actionTime;
		this.type			= EventType.DEATH;
	}
	
	/**
	 * Set success of event in Scheduler, if the event actually happened
	 * @param success
	 */
	public void setSuccess(boolean success) {
		this.success = success;
	}
	
	// getters
	
	public EventType getType() {
		return type;
	}

	public Host getFromHost() {
		return fromHost;
	}

	public Host getToHost() {
		return toHost;
	}

	public Deme getFromDeme() {
		return fromDeme;
	}

	public Deme getToDeme() {
		return toDeme;
	}
	
	/**
	 * returns the Deme of the fromHost
	 * @return
	 */
	public Deme getResponsibleDeme() {
		return fromHost.myDeme;
	}
	
	/**
	 * returns the Deme of the toHost
	 * @return
	 */
	public Deme getRecipientDeme() {
		return toHost.myDeme;
	}

	public double getCreationTime() {
		return creationTime;
	}

	public double getActionTime() {
		return actionTime;
	}

	public boolean isSuccess() {
		return success;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(actionTime);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(creationTime);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		Event other = (Event) obj;
		if (Double.doubleToLongBits(actionTime) != Double
				.doubleToLongBits(other.actionTime))
			return false;
		if (Double.doubleToLongBits(creationTime) != Double
				.doubleToLongBits(other.creationTime))
			return false;
		if (type != other.type)
			return false;
		return true;
	}

	@Override
	/**
	 * compares the action time of this event to another
	 * use EventTest to test whether Collections.sort has the correct effect
	 */
	public int compareTo(Event eventB) {
		
		if (this.actionTime > eventB.actionTime) {
			return 1;
		} else if (this.actionTime < eventB.actionTime) {
			return -1;
		} else {
			// if action times are the same then put sampling below the others
			if ( (type == EventType.SAMPLING) && (eventB.type != EventType.SAMPLING) ) {
				return 1;
			} else if ( ( type != EventType.SAMPLING) && (eventB.type == EventType.SAMPLING) ) {
				return -1;
			}
			
			return 0;
		}
	}

	@Override
	public String toString() {
		return "Event [type=" + type + ", fromHost=" + fromHost + ", toHost="
				+ toHost + ", fromDeme=" + fromDeme + ", toDeme=" + toDeme
				+ ", creationTime=" + creationTime + ", actionTime="
				+ actionTime + ", success=" + success + "]";
	}

	/**
	 * returns details of event in io friendly form if the event was successful
	 * @return
	 */
	public String toOutput() {
	
		if (success) {
			/*
			String line = type + delim + creationTime + delim + actionTime + 
					fromDeme.getName() + "-" + fromHost.getName() + delim + 
					toDeme.getName() + "-" + toHost.getName();
			*/
			
			String line = type + delim + creationTime + delim + actionTime;
			
			if (fromDeme != null) {
				line = line + delim + fromDeme.getName() + "-";
			} else {
				line = line + delim;// + "unknown-";
			}
			
			if (fromHost != null) {
				line = line + fromHost.getName();
			} else {
				line = line + "unknown";
			}
			
			if (toDeme != null) {
				line = line + delim + toDeme.getName() + "-";
			} else {
				line = line + delim;// + "unknown-";
			}
			
			if (toHost != null) {
				line = line + toHost.getName();
			} else {
				line = line + "unknown";
			}
			
			return line;
		} else {
			return null;
		}
		
	}
	
	public String toOutputHeader() {
		String line = "EventType" + delim + "CreationTime" + delim + "ActionTime" + delim +
				"FromDeme-FromHost" + delim + "ToDeme-ToHost";
		return line;
	}
	
	
}
