package trees;

import individualBasedModel.Host;

import java.util.List;

/**
 * 
 * @author slycett2
 * @created 28 Oct 2015
 * 
 * A Dead node is a special type of sampled node (but extend from TransmissionNode)
 */
public class DeadNode extends TransmissionNode {

	static boolean includeTimeInSampledName = true;
	static String  delim = "|";							// good for BEAST 1
	
	public DeadNode(Host h) {
		super(h);
		name	  = new String( host.getNameWithDeme() + "_" + super.uid + "_dead");			// do like this because deme of host can change later on
	}
	
	/**
	 * adds the time stamp to the sampled name
	 */
	public void setNodeHeight(double nodeHeight) {
		this.nodeHeight = nodeHeight;
		if (includeTimeInSampledName) {
			name	  = name + delim + this.nodeHeight;
		}
	}
	
	public List<Node> getChildren() {
		return null;
	}
	
	public int getNumberOfChildren() {
		return 0;
	}
	
	public void setChildren(List<Node> children) {
		System.out.println("DeadNode.setChildren - WARNING - cannot have children");
	}
	
	public void addChild(TransmissionNode child) {
		System.out.println("DeadNode.addChild - WARNING - cannot have children");
	}
	
}
