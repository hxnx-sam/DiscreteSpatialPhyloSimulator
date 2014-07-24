package networks;

import java.util.*;

/**
 * 
 * @author slycett
 * @version 24 July 2014
 */
public class BasicNode implements NetworkNode {
	
	private static int uid = -1;
	
	private static int getNextID() {
		uid++;
		return uid;
	}
	
	static void resetNextID() {
		uid = -1;
	}
	
	//////////////////////////////////////////////////////////////////////////////
	
	List<NetworkNode> neighbours = new ArrayList<NetworkNode>();
	String name;
	
	public BasicNode() {
		name = ""+getNextID();
		//uid++;
	}

	@Override
	public void setNetworkNeighbours(List<NetworkNode> nn) {
		// TODO Auto-generated method stub
		neighbours = nn;
	}

	@Override
	public List<NetworkNode> getNetworkNeighbours() {
		return neighbours;
	}

	@Override
	public void addNetworkNode(NetworkNode nn) {
		neighbours.add(nn);
	}

	@Override
	public void removeNetworkNode(NetworkNode nn) {
		if (neighbours.contains(nn)) {
			neighbours.remove(nn);
		}
		
	}

	@Override
	public boolean hasNetworkNeighbour(NetworkNode b) {
		return (neighbours.contains(b));
	}

	@Override
	public int numberOfNeighbours() {
		if (neighbours != null) {
			return neighbours.size();
		} else {
			return 0;
		}
	}
	
	@Override
	public String getName() {
		return name;
	}

}
