package io;

import java.io.*;

public class NewickWriter {
	
	BufferedWriter outFile;
	String rootname			= "test//temp";
	String ext 				= ".nwk";
	
	/**
	 * basic constructor
	 */
	public NewickWriter() {
		
	}

	/**
	 * all in one constructor, open file, writes tree string, closes file
	 * @param rootname
	 * @param treeString
	 */
	public NewickWriter(String rootname, String treeString) {
		this.rootname = rootname;
		openFile();
		writeTreeString(treeString);
		closeFile();
	}
	
	
	////////////////////////////////////////////////////////////
	
	public void openFile() {
		try {
			outFile = new BufferedWriter(new FileWriter(rootname + ext));
		} catch (IOException e) {
			System.out.println(e.toString());
		}
	}
	
	/**
	 * write the tree string to an open file, add ; to the end if not already done, and add write final new line
	 * @param treeString
	 */
	public void writeTreeString(String treeString) {
		if (!treeString.endsWith(";")) {
			treeString = treeString + ";";
		}
		try {
			outFile.write(treeString);
			outFile.newLine();
		} catch (IOException e) {
			System.out.println(e.toString());
		}
	}
	
	/**
	 * just write the input string to the open file, no new lines or anything
	 * useful if you think there might be string length problems
	 * @param anyString
	 */
	public void write(String anyString) {
		try {
			outFile.write(anyString);
		} catch (IOException e) {
			System.out.println(e.toString());
		}
	}
	
	public void closeFile() {
		try {
			outFile.close();
		} catch (IOException e) {
			System.out.println(e.toString());
		}
	}
	
}
