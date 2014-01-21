package trees;


import individualBasedModel.Host;

import java.util.List;

/**
 * 
 * @author Samantha Lycett
 * @version 24 Oct 2013 - added include time in sampled name (defaults to true)
 *
 */
public class SampledNode extends TransmissionNode {

	static boolean includeTimeInSampledName = true;
	static String  delim = "|";							// good for BEAST 1
	
	public SampledNode(Host h) {
		super(h);
		name	  = new String( host.getNameWithDeme() + "_" + super.uid + "_sampled");			// do like this because deme of host can change later on
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
		System.out.println("SampledNode.setChildren - WARNING - cannot have children");
	}
	
	public void addChild(TransmissionNode child) {
		System.out.println("SampledNode.addChild - WARNING - cannot have children");
	}
	
	
	
}
