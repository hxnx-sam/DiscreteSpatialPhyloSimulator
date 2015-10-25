package trees;

//import java.util.*;

/**
 * class to prune a transmission tree to only the sampled nodes
 * @author Samantha Lycett
 * @created 1 July 2013
 * @version 1 July 2013
 * @version 2 July 2013
 * @version 26 Feb 2014
 */
public class TreePruner {

	boolean				pruned 				= false;
	TransmissionTree 	tt;
	String				fullNewick			= null;
	String				prunedNewick		= null;
	String				binaryPrunedNewick 	= null;
	
	public TreePruner(TransmissionTree tt) {
		this.tt = tt;
	}
	
	//////////////////////////////////////////////////////////////
	
	public void prune() {
		
		if (!pruned) {
			// record full newick of unpruned tree
			fullNewick 		= tt.toNewick();
		
			// remove the unsampled tips, note this changes the transmission tree forever
			tt.removeUnsampledTips();
			prunedNewick 	= tt.toNewick();
		
			// remove the one child nodes, note this changes the transmission tree forever
			tt.removeOneChildNodes();		// internals
			tt.removeOneChildNodes();		// connected to sampled
			binaryPrunedNewick = tt.toNewick();
			
			pruned 			= true;
		}
	}
	
	/**
	 * returns full unpruned newick string of the transmission tree
	 * @return
	 */
	public String getFullNewick() {
		if (fullNewick == null) {
			prune();
		}
		return fullNewick;
	}
	
	/**
	 * returns newick with sampled tips only, but note that there are 1 child nodes (OK for FigTree not OK for R-ape).
	 * @return
	 */
	public String getPrunedNewick() {
		if (prunedNewick == null) {
			prune();
		}
		return prunedNewick;
	}
	
	/**
	 * returns binary tree with sampled tips only.
	 * @return
	 */
	public String getBinaryPrunedNewick() {
		if (binaryPrunedNewick == null) {
			prune();
		}
		return binaryPrunedNewick;
	}
	
}
