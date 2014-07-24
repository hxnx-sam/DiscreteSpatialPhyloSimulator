package networks;

import java.util.*;

public class LocationNetworkNode extends BasicNode implements NetworkNode {

	String			delim = ",";
	String			eol	  = "\n";
	
	double			citySize = 1;
	Location 		position;
	List<Double> 	distToNeighbours;
	List<Double>	weightsToNeighbours;
	
	public LocationNetworkNode(Location pos, double citySize) {
		super();
		this.position = pos;
		this.citySize = citySize;
	}
	
	/////////////////////////////////////////////////////////////////////////
	
	/**
	 * @return the citySize
	 */
	public double getCitySize() {
		return citySize;
	}


	/**
	 * @param citySize the citySize to set
	 */
	public void setCitySize(double citySize) {
		this.citySize = citySize;
	}


	/**
	 * @return the position
	 */
	public Location getPosition() {
		return position;
	}


	/**
	 * @return the distToNeighbours
	 */
	public List<Double> getDistToNeighbours() {
		return distToNeighbours;
	}

	/////////////////////////////////////////////////////////////////////////
	

	void calculateDistanceToNeighbours() {
		distToNeighbours = new ArrayList<Double>();
		
		for (NetworkNode nn : neighbours) {
			LocationNetworkNode locN = (LocationNetworkNode)nn;
			double dd				 = this.position.distanceFrom(locN.position);
			distToNeighbours.add( new Double(dd) );
		}
		
	}
	
	void removeIfFurtherThan(double thres) {
		if (distToNeighbours == null) {
			calculateDistanceToNeighbours();
		}
		
		List<NetworkNode> toRemove = new ArrayList<NetworkNode>();
		for (int i = 0; i < distToNeighbours.size(); i++) {
			if (distToNeighbours.get(i) > thres) {
				toRemove.add(neighbours.get(i));
			}
		}
		
		for (NetworkNode n : toRemove) {
			removeNetworkNode(n);
		}
		
	}
	
	/**
	 * calculate weights for gravity model, 
	 * note that distances between neighbours should have been calculated first. 
	 * weight = size1^a * size2^b / d^y
	 * @param a - power for this city size, typically = 1
	 * @param b - power for other city size, typically = 1
	 * @param y - power for distance, typically = 2
	 */
	void calculateGravityModelWeights(double a, double b, double y) {
		if (distToNeighbours == null) {
			calculateDistanceToNeighbours();
		}
		
		weightsToNeighbours = new ArrayList<Double>();
		for (int i = 0; i < neighbours.size(); i++) {
			LocationNetworkNode locN = (LocationNetworkNode)(neighbours.get(i));
			double s1 = citySize;
			double s2 = locN.citySize;
			double d  = distToNeighbours.get(i);
			
			s1 		  = a*Math.log(s1);
			s2 		  = b*Math.log(s2);
			d  		  = y*Math.log(d);
			
			double w  = s1 + s2 - d;
			w 		  = Math.exp(w);
			
			weightsToNeighbours.add(w);
		}
		
	}
	
	/**
	 * calculate weights for gravity model, 
	 * note that distances between neighbours should have been calculated first. 
	 * weight = size1 * size2 / d^2
	 */
	void calculateGravityModelWeights() {
		if (distToNeighbours == null) {
			calculateDistanceToNeighbours();
		}
		
		weightsToNeighbours = new ArrayList<Double>();
		for (int i = 0; i < neighbours.size(); i++) {
			LocationNetworkNode locN = (LocationNetworkNode)(neighbours.get(i));
			double s1 = citySize;
			double s2 = locN.citySize;
			double d  = distToNeighbours.get(i);
			double w  = s1*s2/(d*d);
			
			weightsToNeighbours.add(w);
		}
		
	}
	
	
	/**
	 * calculate weights for exponential spatial kernel,
	 * note that distances between neighbours should have been calculated first.
	 * weight = exp(-alpha * distance)
	 * @param alpha - exponential factor for distance
	 */
	void calculateExponentialKernelWeights(double alpha) {
		
		if (distToNeighbours == null) {
			calculateDistanceToNeighbours();
		}
		
		weightsToNeighbours = new ArrayList<Double>();
		for (int i = 0; i < neighbours.size(); i++) {
			double d  = distToNeighbours.get(i);
			double w  = Math.exp((-1)*alpha*d);
			weightsToNeighbours.add(w);
		}
		
	}
	
	/**
	 * calculate weights for gaussian spatial kernel,
	 * note that distances between neighbours should have been calculated first.
	 * weight = exp(-alpha * distance^2)
	 * @param alpha - factor for distance
	 */
	void calculateGaussianKernelWeights(double alpha) {
		if (distToNeighbours == null) {
			calculateDistanceToNeighbours();
		}
		
		weightsToNeighbours = new ArrayList<Double>();
		for (int i = 0; i < neighbours.size(); i++) {
			double d  = distToNeighbours.get(i);
			double w  = Math.exp((-1)*alpha*d*d);
			weightsToNeighbours.add(w);
		}
	}
	
	/**
	 * calculate weights for power law spatial kernel,
	 * note that distances between neighbours should have been calculated first.
	 * weight = (1 + distance/beta)^(-alpha)
	 * @param alpha - power law power
	 * @param beta - scaling factor for distance
	 */
	void calculatePowerLawKernelWeights(double alpha, double beta) {
		
		if (distToNeighbours == null) {
			calculateDistanceToNeighbours();
		}
		
		weightsToNeighbours = new ArrayList<Double>();
		for (int i = 0; i < neighbours.size(); i++) {
			double d  = distToNeighbours.get(i);
			double w  = Math.exp( (-1) * alpha * Math.log(1 + d/beta) );
			weightsToNeighbours.add(w);
		}
		
	}
	
	void scaleWeights(double a) {
		for (Double w : weightsToNeighbours) {
			w = w*a;
		}
	}
	
	void normaliseWeights() {
		double total = 0;
		for (Double w : weightsToNeighbours) {
			total += w;
		}
		
		for (Double w : weightsToNeighbours) {
			w = w/total;
		}
	}
	
	/////////////////////////////////////////////////////////////

	/**
	 * sets the neighbours for this node, removes reference to self if present, and calculates the distance to the neighbours
	 */
	@Override
	public void setNetworkNeighbours(List<NetworkNode> nn) {
		
		neighbours = new ArrayList<NetworkNode>();
		for (int i = 0; i < nn.size(); i++) {
			if ( !this.equals(nn.get(i)) ) {
				neighbours.add(nn.get(i));
			}
		}
		
		/*
		neighbours = nn;
		
		if (neighbours.contains(this)) {
			neighbours.remove(this);
		}
		*/
		
		calculateDistanceToNeighbours();
	}
	
	
	@Override
	public void addNetworkNode(NetworkNode nn) {
		neighbours.add(nn);
		
		if (distToNeighbours != null) {
			LocationNetworkNode locN = (LocationNetworkNode)nn;
			double dd				 = this.position.distanceFrom(locN.position);
			distToNeighbours.add(new Double(dd));
		}
		
	}

	@Override
	public void removeNetworkNode(NetworkNode nn) {
		
		int i = neighbours.indexOf(nn);
		if (i >= 0) {
			neighbours.remove(i);
			
			if (distToNeighbours != null) {
				distToNeighbours.remove(i);
			}
		}
		
		/*
		if (neighbours.contains(nn)) {
			neighbours.remove(nn);
		}
		*/
		
	}
	
	////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * returns a list of the neighbours all on one line
	 * @return
	 */
	public String neighboursLine() {
		StringBuffer txt = new StringBuffer();
		
		txt.append(neighbours.get(0).getName());
		
		for (int i = 1; i < neighbours.size(); i++) {
			txt.append(delim);
			txt.append(neighbours.get(i).getName());
		}
		
		return txt.toString();
	}
	
	/**
	 * returns a list of the weights for the links to the neigbhours all on one line
	 * @return
	 */
	public String weightsLine() {
		StringBuffer txt = new StringBuffer();
		
		txt.append(weightsToNeighbours.get(0).doubleValue());
		
		for (int i = 1; i < weightsToNeighbours.size(); i++) {
			txt.append(delim);
			txt.append(weightsToNeighbours.get(i).doubleValue());
		}
		
		return txt.toString();
		
	}
	
	/**
	 * returns a list of the distances between self and neighbours all one line
	 * @return
	 */
	public String distancesLine() {
	
		StringBuffer txt = new StringBuffer();
		
		txt.append(distToNeighbours.get(0).doubleValue());
		
		for (int i = 1; i < distToNeighbours.size(); i++) {
			txt.append(delim);
			txt.append(distToNeighbours.get(i).doubleValue());
		}
		
		return txt.toString();
	
	}
	
	public String edgeList() {
		StringBuffer txt = new StringBuffer();
		
		for (int i = 0; i < neighbours.size(); i++) {
			txt.append(this.name);
			txt.append(delim);
			txt.append( neighbours.get(i).getName() );
			txt.append(delim);
			txt.append( distToNeighbours.get(i).doubleValue() );
			txt.append(delim);
			txt.append( weightsToNeighbours.get(i).doubleValue() );
			txt.append(eol);
		}
		
		return txt.toString();
	}
	
	public String toString() {
		
		return (name +" "+ position.toString() +" "+ citySize);
		
	}
	
	///////////////////////////////////////////////////////////////////////////////////
	
}
