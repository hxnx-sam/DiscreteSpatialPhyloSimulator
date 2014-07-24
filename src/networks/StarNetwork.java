package networks;

/**
 * 
 * @author Samantha Lycett
 *
 */
public class StarNetwork extends BasicNetwork {

	NetworkNode centralNode;
	
	public StarNetwork() {
		super();
		modelType = NetworkModelType.STAR;
	}
	
	/**
	 * creates a network of n BasicNodes in a star pattern
	 * @param numberOfNodes
	 */
	public StarNetwork(int numberOfNodes) {
		super(numberOfNodes);
		modelType = NetworkModelType.STAR;
		create();
	}
	
	public void setCentralNode(NetworkNode centralNode) {
		this.centralNode = centralNode;
		if (!nodes.contains(centralNode)) {
			nodes.add(centralNode);
		}
	}
	
	public NetworkNode getCentralNode() {
		return centralNode;
	}
	
	public void create() {
		if (centralNode == null) {
			centralNode = nodes.get(0);
		}
		for (int i = 0; i < nodes.size(); i++) {
			NetworkNode b = nodes.get(i);
			createLink(centralNode, b);
			//System.out.println(centralNode.getName()+","+b.getName());
		}
	}
	
}
