package networks;

import java.util.List;

public interface NetworkNode {

	public void 				setNetworkNeighbours(List<NetworkNode> nn);
	public List<NetworkNode> 	getNetworkNeighbours();
	
	public void 				addNetworkNode(NetworkNode nn);
	public void 				removeNetworkNode(NetworkNode nn);
	
	public boolean				hasNetworkNeighbour(NetworkNode b);
	
	public int					numberOfNeighbours();
	public String				getName();
	
}
