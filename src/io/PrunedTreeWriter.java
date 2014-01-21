package io;

import trees.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class PrunedTreeWriter {

	String 	   rootname;
	String	   ext 			= ".nwk";
	
	TreePruner pruner;
	String	   fullExt		= "_full";
	String	   prunedExt	= "_pruned";
	String 	   bin_pruExt	= "_binaryPruned";
	
	boolean	   echo			= true;
	
	public PrunedTreeWriter(String rootname, TransmissionTree tt) {
		this.rootname = rootname;
		pruner 		  = new TreePruner(tt);
	}
	
	/**
	 * writes full unpruned tree to file, returns file name
	 */
	public String writeFullNewick() {
		
		try {
			String fname		   = rootname + fullExt + ext;
			BufferedWriter outFile = new BufferedWriter(new FileWriter(fname));
			String line 		   = pruner.getFullNewick();
			if (!line.endsWith(";")) {
				line = line + ";";
			}
			outFile.write(line);
			outFile.newLine();
			outFile.close();
			
			if (echo) {
				System.out.println("Full Tree to "+fname);
				System.out.println(line);
			}
			
			return fname;
			
		} catch (IOException e) {
			System.out.println(e.toString());
			
			return null;
		}
	}
	
	/**
	 * writes pruned tree to file, returns file name
	 */
	public String writePrunedNewick() {
		
		try {
			String fname		   = rootname + prunedExt + ext;
			BufferedWriter outFile = new BufferedWriter(new FileWriter(fname));
			String line 		   = pruner.getPrunedNewick();
			if (!line.endsWith(";")) {
				line = line + ";";
			}
			outFile.write(line);
			outFile.newLine();
			outFile.close();
			
			if (echo) {
				System.out.println("Pruned Tree to "+fname);
				System.out.println(line);
			}
			
			return fname;
		} catch (IOException e) {
			System.out.println(e.toString());
			
			return null;
		}
	}
	
	/**
	 * writes binary pruned tree to file, returns file name
	 */
	public String writeBinaryPrunedNewick() {
		
		try {
			String fname		   = rootname + bin_pruExt + ext;
			BufferedWriter outFile = new BufferedWriter(new FileWriter(fname));
			String line 		   = pruner.getBinaryPrunedNewick();
			if (!line.endsWith(";")) {
				line = line + ";";
			}
			outFile.write(line);
			outFile.newLine();
			outFile.close();
			
			if (echo) {
				System.out.println("Binary Pruned Tree to "+fname);
				System.out.println(line);
			}
			
			return fname;
		} catch (IOException e) {
			System.out.println(e.toString());
			
			return null;
		}
	}

	/**
	 * writes full, pruned and binary pruned trees, returns file names
	 */
	public List<String> writeNewickTrees() {
		
		List<String> fnames = new ArrayList<String>();
		
		fnames.add( writeFullNewick() );
		fnames.add( writePrunedNewick() );
		fnames.add( writeBinaryPrunedNewick() );
		
		return fnames;
	}
	
}
