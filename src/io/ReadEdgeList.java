package io;

import java.io.*;
import java.util.*;

/**
 * 
 * @author Samantha Lycett
 * @version 31 Oct 2013 - included optional reading of edge weights
 */
public class ReadEdgeList {

	String fname;
	String 			delim 				= ",";
	String			prefix				= "Node";				// set to "" to remove
	boolean 		directed 			= false;
	List<String> 	names 				= new ArrayList<String>();
	List<List<String>> neighbours 		= new ArrayList<List<String>>();
	List<List<Double>> neighbourWeights	= new ArrayList<List<Double>>();
	boolean			doScaling			= false;
	double			scale				= 1.0;
	
	/**
	 * empty constructor
	 */
	public ReadEdgeList() {
		
	}
	
	/**
	 * constructor which opens file and reads contents, and closes file.
	 * Default is undirected network, comma separated, with no scaling
	 * @param fname
	 */
	public ReadEdgeList(String fname) {
		this.fname = fname;
		read();
	}
	
	////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * sets filename only, does not read contents
	 */
	public void setFname(String fname) {
		this.fname = fname;
	}

	public void setApplyScaleToWeights(double scale) {
		this.doScaling = true;
		this.scale     = scale;
	}
	
	////////////////////////////////////////////////////////////////////////////////////
	
	
	public void read() {
		try {
			Scanner inFile = new Scanner(new File(fname));
			while (inFile.hasNext()) {
				String line 	= inFile.nextLine().trim();
				String[] els 	= line.split(delim);
				
				String name1	= prefix + els[0];
				String name2	= prefix + els[1];
				
				Double w		= 1.0;
				if (els.length >= 3) {
					w = Double.parseDouble(els[2]);
				}
				if (doScaling) {
					w = w*scale;
				}
				
				
				if (names.contains(name1)) {
					int index 				= names.indexOf(name1);
					List<String> neigbs		= neighbours.get(index);
					neigbs.add(name2);
					
					if (els.length >= 3) {
						List<Double> edgeWeights = neighbourWeights.get(index);
						edgeWeights.add(w);
					}
					
				} else {
					names.add(name1);
					List<String> neigbs		= new ArrayList<String>();
					neigbs.add(name2);
					neighbours.add(neigbs);
					
					if (els.length >= 3) {
						List<Double> edgeWeights = new ArrayList<Double>();
						edgeWeights.add(w);
						neighbourWeights.add(edgeWeights);
					}
					
				}
				
				if (!directed) {
					// repeat other way around
					
					if (names.contains(name2)) {
						int index 				= names.indexOf(name2);
						List<String> neigbs		= neighbours.get(index);
						neigbs.add(name1);
						
						if (els.length >= 3) {
							List<Double> edgeWeights = neighbourWeights.get(index);
							edgeWeights.add(w);
						}
					} else {
						names.add(name2);
						List<String> neigbs		= new ArrayList<String>();
						neigbs.add(name1);
						neighbours.add(neigbs);
						
						if (els.length >= 3) {
							List<Double> edgeWeights = new ArrayList<Double>();
							edgeWeights.add(w);
							neighbourWeights.add(edgeWeights);
						}
						
					}
					
				} 
				
			}
			
			inFile.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	////////////////////////////////////////////////////////////////////////////////////
	
	public int numberOfNodes() {
		return names.size();
	}
	
	public String getNodeName(int i) {
		return names.get(i);
	}
	
	public List<String> getNodeNeighbours(int i) {
		return neighbours.get(i);
	}
	
	public List<Double> getNodeNeighbourEdgeWeights(int i) {
		return neighbourWeights.get(i);
	}
}
