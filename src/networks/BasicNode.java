package networks;

import java.util.*;

public class BasicNode implements NetworkNode {
	
	static int uid = 0;
	
	List<NetworkNode> neighbours = new ArrayList<NetworkNode>();
	String name;
	
	public BasicNode() {
		name = ""+uid;
		uid++;
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
