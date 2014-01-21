package trees;

import java.util.*;

import individualBasedModel.EventType;
import individualBasedModel.Host;
import individualBasedModel.Event;
//import individualBasedModel.EventType;

/**
 * class to represent the transmission tree - for outputing to newick
 * @author Samantha Lycett
 * @created 26 Nov 2012
 * @version 26 Nov 2012
 * @version 17 June 2013 - for DiscreteSpatialPhyloSimulator (not Demes, as in Nov 2012)
 */
public class TransmissionTree {

	List<Node> 			   nodes;
	List<TransmissionNode> mostRecentNodes;
	TransmissionNode	   rootNode;
	
	
	public TransmissionTree() {
		rootNode 		= new TransmissionNode();
		rootNode.setNodeHeight(0);
		
		nodes 			= new ArrayList<Node>();
		nodes.add(rootNode);
		
		mostRecentNodes = new ArrayList<TransmissionNode>();
		mostRecentNodes.add(rootNode);
	}
	
	
	/**
	 * makes a new transmission node but does not add into the tree
	 * @param tn_fromHost
	 * @param toHost
	 * @param time
	 * @return
	 */
	TransmissionNode makeNewTransmissionNode(TransmissionNode tn_fromHost, Host toHost, double time) {
		TransmissionNode tn_toHost = new TransmissionNode(toHost);
		tn_toHost.setNodeHeight(time);
		
		if (tn_fromHost != null) {
			tn_toHost.setParent(tn_fromHost);
			tn_fromHost.addChild(tn_toHost);
		} else {
			tn_toHost.setParent(rootNode);
			rootNode.addChild(tn_toHost);
		}
		
		return (tn_toHost);
	}
	
	/**
	 * makes a sampled transmission node (no children allowed), but does not add to tree
	 * @param tn_fromHost
	 * @param toHost
	 * @param time
	 * @return
	 */
	SampledNode makeNewSampledNode(TransmissionNode tn_fromHost, Host toHost, double time) {
		SampledNode sn_toHost = new SampledNode(toHost);
		sn_toHost.setNodeHeight(time);
		
		if (tn_fromHost != null) {
			sn_toHost.setParent(tn_fromHost);
			tn_fromHost.addChild(sn_toHost);
		} else {
			sn_toHost.setParent(rootNode);
			rootNode.addChild(sn_toHost);
		}
		
		return (sn_toHost);
	}
	
	/**
	 * retrieves an existing transmission node from the most recent nodes list
	 * @param fromHost
	 * @return
	 */
	TransmissionNode retrieveTransmissionNode(Host fromHost) {
		
		if (fromHost != null) {
		
			TransmissionNode tempNode = new TransmissionNode(fromHost);
			if (mostRecentNodes.contains(tempNode)) {
				int i = mostRecentNodes.indexOf(tempNode);
				return ( mostRecentNodes.get(i) );
			} else {
				return null;
			}
		
		} else {
			return rootNode;
		}
	}
	
	TransmissionNode getRootTransmissionNode() {
		return rootNode;
	}
	
	public void processEvent(Event e) {
		Host fromHost				= e.getFromHost();
		Host toHost					= e.getToHost();
		double time					= e.getActionTime();
		EventType etype				= e.getType();
		
		if (etype == EventType.SAMPLING) {
			
			if (fromHost.equals(toHost)) {
			
				// get the already existing transmission node from the fromHost
				TransmissionNode tn_parent_fromHost   = retrieveTransmissionNode(fromHost);
			
				// make a new internal transmission node from the fromHost
				TransmissionNode tn_internal_fromHost = makeNewTransmissionNode(tn_parent_fromHost, fromHost, time);
			
				// make a new sampled transmission node for the toHost (which should be the same as the fromHost anyway)
				SampledNode sn_toHost    		  	  = makeNewSampledNode(tn_parent_fromHost, toHost, time);
				
				// add new transmission nodes to transmission tree
				nodes.add(tn_internal_fromHost);
				nodes.add(sn_toHost);
			
				// update the most recent transmission node from the fromHost
				mostRecentNodes.remove(tn_parent_fromHost);
				mostRecentNodes.add(tn_internal_fromHost);
				
				// do not add the sampled node to the most recent nodes because you cannot attach to it
				
			} else {
				System.out.println("TransmissionTree.processEvent - WARNING - Sampling event is not correctly specified");
			}
			
		} else {
		
			// get the already existing transmission node from the fromHost
			TransmissionNode tn_parent_fromHost   = retrieveTransmissionNode(fromHost);
		
			// make a new internal transmission node from the fromHost
			TransmissionNode tn_internal_fromHost = makeNewTransmissionNode(tn_parent_fromHost, fromHost, time);
		
			// make a new transmission node for the toHost
			TransmissionNode tn_toHost    		  = makeNewTransmissionNode(tn_parent_fromHost, toHost, time);
		
			// add new transmission nodes to transmission tree
			nodes.add(tn_internal_fromHost);
			nodes.add(tn_toHost);
		
			// update the most recent transmission node from the fromHost
			mostRecentNodes.remove(tn_parent_fromHost);
			mostRecentNodes.add(tn_internal_fromHost);
			mostRecentNodes.add(tn_toHost);
		}
		
	}
	
	///////////////////////////////////////////////////////////////////////////////
	
	/**
	 * removes unsampled tips from the tree (permanently) - note this leaves 1 child nodes (call from TreePruner)
	 */
	protected void removeUnsampledTips() {
		
		List<Node> toProcess 	  = new ArrayList<Node>();
		toProcess.add(rootNode);
		
		while (toProcess.size() > 0) {
			Node tn = toProcess.remove(0);
				
			if (tn.getNumberOfChildren() > 0) {
				
				((TransmissionNode)tn).removeUnsampledTipChildren();
				toProcess.addAll(tn.getChildren());
				
			} else {
				// this is a tip
				if (tn instanceof SampledNode) {
					// this is a sampled node
					//System.out.println("Keeping "+tn.getName() );
					
				} else {
					// this is an unsampled tip
					System.out.println("TransmissionTree.removeUnsampledTips - WARNING Should have already removed "+tn.getName());
				}
			}
		}
		
	}
	
	/**
	 * removes one child nodes from the tree (permantently) - use this after removeUnsampledTips (call from TreePruner)
	 */
	protected void removeOneChildNodes() {
		List<Node> toProcess = new ArrayList<Node>();
		toProcess.add(rootNode);
		
		while (toProcess.size() > 0) {
			Node tn = toProcess.remove(0);
			
			if (tn.getNumberOfChildren() > 0) {
				
				((TransmissionNode)tn).adoptSingleGrandChildren();
				toProcess.addAll(tn.getChildren());
				
			}
			
			/*
			if (tn.getNumberOfChildren() == 1) {
				TransmissionNode par = (TransmissionNode)tn.getParent();
				Node child			 = tn.getChildren().get(0);
				// connect child directly to parent bypassing this current node
				child.setParent(par);
				par.addChild(child);
				child.resetBranchLength();
			} else if (tn.getNumberOfChildren() >= 2) {
				toProcess.addAll(tn.getChildren());
			}
			*/
			
		}
		
	}
	
	public boolean isRoot(TransmissionNode tn) {
		return (tn.getParent() == null);
	}
	
	public boolean isTip(TransmissionNode tn) {
		return ( (tn.getChildren() == null) || (tn.getChildren().size() == 0)); 
	}
	
	///////////////////////////////////////////////////////////////////////////////
	
	public String toNewick() {
		
		StringBuilder buffer = new StringBuilder();
		toNewick(rootNode, buffer);
        buffer.append(";");
		return buffer.toString();
		
	}
	
	// see jebl.evolultion.trees.Utils
	private void toNewick(TransmissionNode tn, StringBuilder buffer) {
		
		/*
		if (tn.getParent() != null) {
			System.out.println("parent = "+tn.getParent().getName()+"\tchild = "+tn.getName()+"\tbranchLength = "+tn.getBranchLength()+"\tchild height = "+tn.getNodeHeight());
		}
		*/
		
		if (isTip(tn)) {
			String name = tn.getName();
			buffer.append(name);
			buffer.append(':');
			buffer.append(tn.getBranchLength());
			/*
			try {
				buffer.append(tn.getBranchLength());
			} catch (java.lang.StackOverflowError e) {
				System.out.println("-- Problem building string --");
				System.out.println(name);
				System.out.println(tn.getBranchLength());
				System.out.println(buffer.capacity());
				System.out.println(buffer.toString());
				e.printStackTrace();
			}
			*/
			
		} else {
			buffer.append('(');
			//List<TransmissionNode> children = tn.getChildren();
			List<Node> children = tn.getChildren();
			final int last = children.size() - 1;
			for (int i = 0; i < children.size(); i++) {
				toNewick((TransmissionNode)children.get(i), buffer);
				buffer.append(i == last ? ')' : ',');
			}

			String name = tn.getName();
			buffer.append(name);
			buffer.append(':');
			buffer.append(tn.getBranchLength());
			
			/*
			TransmissionNode parent = tn.getParent();
			// Don't write root length. This is ignored elsewhere and the nexus importer fails
			// when it is present.
			if (parent != null ) {
				buffer.append(":").append(tn.getBranchLength());
			}
			*/
			
		}
	}
	
	//////////////////////////////////////////////////////////////////////////////////////////////
	// info methods for testing
	
	String treeInfo() {
		StringBuffer txt = new StringBuffer();
		txt.append("Tree has "+nodes.size()+" nodes and "+mostRecentNodes.size()+" most-recent-nodes\n");
		txt.append("Transmission nodes are:\n");
		for (Node n : nodes) {
			txt.append("\t"+n.toString()+"\n");
		}
		txt.append("Most-recent-nodes are:\n");
		for (TransmissionNode n : mostRecentNodes) {
			txt.append("\t"+n.toString()+"\n");
		}
		
		return (txt.toString());
	}
	
	
}
