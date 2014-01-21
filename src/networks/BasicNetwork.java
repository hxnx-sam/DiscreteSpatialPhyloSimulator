package networks;

import io.Parameter;

import java.util.*;

/**
 * class to allow the creation of a Basic network. No parameters.
 * @author Samantha Lycett
 *
 */
public class BasicNetwork implements Network {
	
	String delim			= ",";
	String delim2			= "\n";
	int numberOfNodes 		= 0;
	List<NetworkNode> nodes;
	boolean directed		= false;
	
	public BasicNetwork() {
		
	}
	
	public BasicNetwork(int numberOfNodes) {
		setBasicNodes(numberOfNodes);
	}
	
	public void setBasicNodes(int numberOfNodes) {
		this.numberOfNodes = numberOfNodes;
		nodes = new ArrayList<NetworkNode>();
		for (int i = 0; i < numberOfNodes; i++) {
			nodes.add( new BasicNode() );
		}
	}
	
	@Override
	public void setNodes(List<NetworkNode> nn) {
		this.nodes = nn;
	}

	@Override
	public int getNumberOfNodes() {
		return numberOfNodes;
	}
	
	@Override
	public boolean isDirected() {
		return directed;
	}
	
	@Override
	public void setParameter(Parameter p) {
		// TODO Auto-generated method stub
	}

	@Override
	public Parameter getParameter(Parameter p) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Parameter> getParameters() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void create() {
		// TODO Auto-generated method stub
	}

	@Override
	public List<NetworkNode> getNodes() {
		return nodes;
	}

	
	@Override
	/**
	 * creates an undirected link between a and b, i.e. a has a link to b AND b has a link to a.
	 */
	public void createLink(NetworkNode a, NetworkNode b) {
		
		if (nodes.contains(a) && nodes.contains(b)) {
		
			if (!a.hasNetworkNeighbour(b)) {
				a.addNetworkNode(b);
			}
			if (!b.hasNetworkNeighbour(a)) {
				b.addNetworkNode(a);
			}
		
		}
		
	}

	@Override
	public void removeLink(NetworkNode a, NetworkNode b) {
		
		if (nodes.contains(a) && nodes.contains(b)) {
		
			if (a.hasNetworkNeighbour(b)) {
				a.removeNetworkNode(b);
			}
			if (b.hasNetworkNeighbour(a)) {
				b.removeNetworkNode(a);
			}
		
		}
		
	}

	@Override
	public boolean isLink(NetworkNode a, NetworkNode b) {
		if (nodes.contains(a) && nodes.contains(b)) {
			return (a.hasNetworkNeighbour(b) && b.hasNetworkNeighbour(a));
		} else {
			return false;
		}
	}

	public String toString() {
		StringBuffer txt = new StringBuffer();
		for (NetworkNode n : nodes) {
			for (NetworkNode b : n.getNetworkNeighbours()) {
				txt.append(n.getName() + delim + b.getName() + delim2);
			}
		}
		
		return txt.toString();
	}

}
