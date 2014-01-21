package networks;

import io.Parameter;
import java.util.List;

/**
 * 
 * @author  Samantha Lycett
 * @created 2 Oct 2013
 */
public interface Network {

	//public void				setNumberOfNodes(int n);
	public void 	 			setParameter(Parameter p);
	public Parameter 			getParameter(Parameter p);
	public List<Parameter> 		getParameters();
	
	public void					setNodes(List<NetworkNode> nn);
	public void 			 	create();
	public List<NetworkNode> 	getNodes();
	public int					getNumberOfNodes();
	public boolean				isDirected();
	
	public void					createLink(NetworkNode a, NetworkNode b);
	public void					removeLink(NetworkNode a, NetworkNode b);
	public boolean				isLink(NetworkNode a, NetworkNode b);
	
}
