package networks;

public class RingNetwork extends BasicNetwork {

	public RingNetwork() {
		super();
		modelType = NetworkModelType.RING;
	}
	
	/**
	 * creates a network of n Basic Nodes in a ring pattern
	 * @param numberOfNodes
	 */
	public RingNetwork(int numberOfNodes) {
		super(numberOfNodes);
		modelType = NetworkModelType.RING;
		create();
	}
	
	@Override
	public void create() {
		for (int i = 0; i < (nodes.size()-1); i++) {
			NetworkNode a = nodes.get(i);
			NetworkNode b = nodes.get(i+1);
			createLink(a,b);
		}
		NetworkNode a = nodes.get(nodes.size()-1);
		NetworkNode b = nodes.get(0);
		createLink(a,b);
	}
	
}
