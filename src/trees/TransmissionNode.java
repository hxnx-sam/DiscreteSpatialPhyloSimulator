package trees;

import java.util.*;
import individualBasedModel.Host;

/**
 * 
 * @author Samantha Lycett
 * @created 26 Nov 2012
 * @version 28 Nov 2012
 * @version 17 June 2013
 * @version 2  July 2013
 */
public class TransmissionNode implements Node, Cloneable {

	///////////////////////////////////
	// class variables and methods
	private static long nodeCounter 	= -1;

	private static long nextNodeUID() {
		nodeCounter++;
		return (nodeCounter);
	}
	
	///////////////////////////////////
	
	
	protected long 			uid;
	protected String 		name;						// access through getName because is set from host
	
	Host					host;
	TransmissionNode 		parent;
	private double			branchLength = -100;		// access through getBranchLength because is set from nodeHeight and parent
	double					nodeHeight   = 0;
	List<Node>			 	children;
	
	TransmissionNode() {
		uid 	  = nextNodeUID();
		name	  = "" + uid;
	}
	
	TransmissionNode(Host host) {
		this.host = host;
		uid 	  = nextNodeUID();
		name	  = new String( host.getNameWithDeme() + "_" + uid);			// do like this because deme of host can change later on
	}
	
	/**
	 * for use with clone()
	 * @param uid
	 */
	private TransmissionNode(long uid) {
		this.uid 	= uid;
	}
	
	////////////////////////////////////////////////////////////////////////////////////

	/*
	public long getId() {
		return id;
	}
	*/
	
	public String getName() {
		/*
		if (host != null) {
			return host.getNameWithDeme() + "_" + uid;
		} else {
			return ""+uid;
		}
		*/
		return name;
	}
	
	
	public Host getHost() {
		return host;
	}
	

	/**
	 * @return the parent
	 */
	public Node getParent() {
		return parent;
	}

	/**
	 * @return the branchLength
	 */
	
	public double getBranchLength() {
		if (branchLength <= -100) {
			if (parent != null) {
				branchLength  = nodeHeight - parent.nodeHeight;
			} else {
				// if no parent then should be root
				branchLength = 0;
			}
		}
		
		return branchLength;
		//return ( nodeHeight - parent.nodeHeight );
	}
	
	public void resetBranchLength() {
		if (parent != null) {
			branchLength  = nodeHeight - parent.nodeHeight;
		} else {
			// if no parent then should be root
			branchLength = 0;
		}
	}
	
	
	/**
	 * @return the children
	 */
	public List<Node> getChildren() {
		return children;
	}

	public double getNodeHeight() {
		return nodeHeight;
	}
	
	public int getNumberOfChildren() {
		if (children != null) {
			return children.size();
		} else {
			return 0;
		}
	}
	
	/**
	 * @param id the id to set
	 */
	/*
	public void setId(long id) {
		this.id = id;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	*/

	/**
	 * @param parent the parent to set
	 */
	public void setParent(TransmissionNode parent) {
		this.parent = parent;
	}

	public void setParent(Node parent) {
		this.parent = (TransmissionNode)parent;
	}
	
	/**
	 * @param branchLength the branchLength to set
	 */
	/*
	public void setBranchLength(double branchLength) {
		this.branchLength = branchLength;
	}
	*/

	public void setNodeHeight(double nodeHeight) {
		this.nodeHeight = nodeHeight;
	}
	
	/**
	 * @param children the children to set
	 */
	public void setChildren(List<Node> children) {
		this.children = children;
	}
	
	public void addChild(Node child) {
		if (children == null) {
			children = new ArrayList<Node>();
		}
		children.add(child);
	}
	
	public void removeUnsampledTipChildren() {
		
		// have to do it this way because equals and hash code are just on the host, not the full node name
		
		// use getNumberOfChildren because if SampledNode then this is 0 anyway
		if (getNumberOfChildren() > 0) {
		
			List<Node> toKeep = new ArrayList<Node>();
		
			for (Node child : children) {
			
				if (child.getNumberOfChildren() == 0) {
					if (child instanceof SampledNode) {
						// child is a sampled node so keep
						toKeep.add(child);
					}
				} else {
					// child is not a tip, so keep
					toKeep.add(child);
				}
			
			}
		
			// reset children as just those that want to keep
			children = toKeep;
		}
		
	}

	public void adoptSingleGrandChildren() {
		
		List<Node> myChildren = new ArrayList<Node>();
		
		if (getNumberOfChildren() >= 1) {
			for (Node child : children) {
				
				/*
				if (child instanceof SampledNode) {
					System.out.println(child.getName()+" is sampled");
				}
				*/
				
				if (child.getNumberOfChildren() == 1) {
					// if this child has only one child of its own then
					// add this single grand child as my own (and remove this child)
					Node grandChild = child.getChildren().get(0);
					//System.out.print(grandChild.getName()+"-"+grandChild.getParent().getName()+";"+grandChild.getBranchLength());
					
					
					grandChild.setParent(this);
					grandChild.resetBranchLength();
					
					myChildren.add( grandChild );
					
					//System.out.print("\t"+grandChild.getName()+"-"+grandChild.getParent().getName()+";"+grandChild.getBranchLength() );
					//
					//if (grandChild.getParent() == this) {
					//	System.out.println("\treset parent to this "+getName());
					//} else {
					//	System.out.println("\tparent not correct");
					//}
					
				} else {
					// this child remains my child
					myChildren.add( child );
				}
				
			}
			
			// reset children as these new ones
			children = myChildren;
		}
		
	}
	
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////

	// on host object
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((host == null) ? 0 : host.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TransmissionNode other = (TransmissionNode) obj;
		if (host == null) {
			if (other.host != null)
				return false;
		} else if (!host.equals(other.host))
			return false;
		return true;
	}
	
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////
	

	
	public String toString() {
		
		//String line = name + ":" + branchLength;
		String line = getName() + ":" + getBranchLength();
		
		if ( children != null ) {
			if ( children.size() > 0 ) {
				String cline = "(";
				for (int i = 0; i < children.size()-1; i++) {
					cline = cline + children.get(i).toString() + ",";
				}
				cline = cline + children.get(children.size()-1).toString()+")";
				line  = cline + line;
			}  
			
		}
		
		return line;
	}


	public TransmissionNode clone() {
		
		TransmissionNode tn_clone = new TransmissionNode(this.uid);
		tn_clone.host 			  = this.host;
		tn_clone.name 			  = this.name;
		tn_clone.parent 		  = this.parent;
		tn_clone.branchLength 	  = this.branchLength;
		tn_clone.nodeHeight 	  = this.nodeHeight;
		tn_clone.children		  = this.children;
		
		return tn_clone;
		
	}

	
}
