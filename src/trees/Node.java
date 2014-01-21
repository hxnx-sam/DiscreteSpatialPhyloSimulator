package trees;

import individualBasedModel.Host;
import java.util.List;

public interface Node {

	public	double 		getBranchLength();
	public  void		resetBranchLength();
	public	Host		getHost();
	public	String		getName();
	public	int			getNumberOfChildren();
	
	public	Node		getParent();
	public	void		setParent(Node parent);
	
	public	List<Node> 	getChildren();
	public	void		setChildren(List<Node> children);
	public  void		addChild(Node child);
	
	public	double		getNodeHeight();
	public	void 		setNodeHeight(double nodeHeight);
	
}
