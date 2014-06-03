package individualBasedModel;

/**
 * class to store hazards for demes - needs other parameters adding
 * @author Samantha Lycett
 * @created 24 Sept 2013
 * @version 27 Sept 2013
 * @version 3 June 2014 - include birth and death
 */
public class Hazard {

	Deme   myDeme;
	double exposedToInfectedHazard 	= 0;
	double infectOtherHazard 		= 0;
	double recoveryHazard			= 0;
	double migrationHazard			= 0;
	double birthHazard				= 0;
	double deathHazard				= 0;
	double totalHazard				= -1;
	
	// add other hazards later
	
	public Hazard(Deme myDeme) {
		this.myDeme = myDeme;
	}


	////////////////////////////////////////////////////////////////////////
	// Getters
	
	/**
	 * @return the myDeme
	 */
	public Deme getMyDeme() {
		return myDeme;
	}

	/**
	 * @return the exposedToInfectedHazard
	 */
	public double getExposedToInfectedHazard() {
		return exposedToInfectedHazard;
	}

	/**
	 * @return the infectOtherHazard
	 */
	public double getInfectOtherHazard() {
		return infectOtherHazard;
	}

	/**
	 * @return the recoveryHazard
	 */
	public double getRecoveryHazard() {
		return recoveryHazard;
	}

	public double getMigrationHazard() {
		return migrationHazard;
	}
	
	public double getBirthHazard() {
		return birthHazard;
	}
	
	public double getDeathHazard() {
		return deathHazard;
	}
	
	////////////////////////////////////////////////////////////////////////
	// Setters
	
	/**
	 * @param myDeme the myDeme to set
	 */
	public void setMyDeme(Deme myDeme) {
		this.myDeme = myDeme;
	}

	/**
	 * @param exposedToInfectedHazard the exposedToInfectedHazard to set
	 */
	public void setExposedToInfectedHazard(double exposedToInfectedHazard) {
		this.exposedToInfectedHazard = exposedToInfectedHazard;
	}

	/**
	 * @param infectOtherHazard the infectOtherHazard to set
	 */
	public void setInfectOtherHazard(double infectOtherHazard) {
		this.infectOtherHazard = infectOtherHazard;
	}

	/**
	 * @param recoveryHazard the recoveryHazard to set
	 */
	public void setRecoveryHazard(double recoveryHazard) {
		this.recoveryHazard = recoveryHazard;
	}
	
	public void setMigrationHazard(double migrationHazard) {
		this.migrationHazard = migrationHazard;
	}
	
	public void setBirthHazard(double birthHazard) {
		this.birthHazard = birthHazard;
	}
	
	public void setDeathHazard(double deathHazard) {
		this.deathHazard = deathHazard;
	}
	
	/////////////////////////////////////////////////////////////////////////
	
	public double getTotalHazard() {
		if (totalHazard == -1) {
			totalHazard = exposedToInfectedHazard + infectOtherHazard + recoveryHazard + migrationHazard + birthHazard + deathHazard;
		}
		return totalHazard;
	}
	
	//////////////////////////////////////////////////////////////////////////
	
}
