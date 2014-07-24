package networks;

/**
 * Class to represent a line network (directed)
 * @author slycett
 * @version 23 July 2014
 */
public class LineNetwork extends BasicNetwork {

	
	public LineNetwork() {
		super();
		directed = true;
		modelType = NetworkModelType.LINE;
	}
	
	/**
	 * creates a network of n Basic Nodes in a line, this is a directed network
	 * @param numberOfNodes
	 */
	public LineNetwork(int numberOfNodes) {
		super(numberOfNodes);
		modelType = NetworkModelType.LINE;
		create();
		directed = true;
	}
	
	@Override
	public void create() {
		for (int i = 0; i < (nodes.size()-1); i++) {
			NetworkNode a = nodes.get(i);
			NetworkNode b = nodes.get(i+1);
			// a is the parent of b
			// a is connected to b
			createLink(a,b);
		}
	}
	
	@Override
	/**
	 * createLink between a and b - note that a line network is directed, so no link between b and a is created here.
	 */
	public void createLink(NetworkNode a, NetworkNode b) {
		
		if (nodes.contains(a) && nodes.contains(b)) {
			if (!a.hasNetworkNeighbour(b)) {
				a.addNetworkNode(b);
			}
		}	
	}
	
}
