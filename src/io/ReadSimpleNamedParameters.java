package io;

import java.io.*;
import java.util.*;

/**
 * class to read in a list of parameters and their values
 * e.g.:
 * Deme1	100
 * Deme2	200
 * values are read as strings, so can be numbers or discrete traits.
 * default delimiter is tab.
 * 
 * @author Samantha Lycett
 * @version 31 Oct 2013
 */
public class ReadSimpleNamedParameters {

	String 			fname;
	List<String> 	parameterNames;
	List<String> 	parameterValues;
	
	String delim 	= "\t";
	boolean header 	= true;
	String headerLine;

	/**
	 * all in one constructor, opens the file with input filename (fname), reads the contents, closes the file
	 * @param fname
	 */
	public ReadSimpleNamedParameters(String fname) {
		this.fname = fname;
		readFile();
	}
	
	/**
	 * opens the file, reads the contents, closes the file
	 */
	public void readFile() {
		parameterNames  = new ArrayList<String>();
		parameterValues = new ArrayList<String>();
		
		try {
			BufferedReader inFile = new BufferedReader(new FileReader(fname));
			
			if (header) {
				headerLine = inFile.readLine();
			}
			
			boolean again = true;
			while (again) {
				String line 	= inFile.readLine();
				
				if ( (line != null) && (line.length() > 1) ) {
				
					String[] els 	= line.split(delim);
					if (els.length >= 2) {
						parameterNames.add(els[0]);
						parameterValues.add(els[1]);
					} else {
						System.out.println("Sorry cant process "+line);
					}
				
				} else {
					again = false;
				}
			}
			
			inFile.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	///////////////////////////////////////////////////////////////////////////////////////
	
	public List<String> getParameterNames() {
		return parameterNames;
	}
	
	public List<String> getParameterValues() {
		return parameterValues;
	}
	
	public String getHeaderLine() {
		return headerLine;
	}
	
	public int size() {
		return parameterNames.size();
	}
	
	public String getParameterName(int index) {
		return parameterNames.get(index);
	}
	
	public String getParameterValue(int index) {
		return parameterValues.get(index);
	}
	
	public String getParameterValue(String parameterName) {
		int index = parameterNames.indexOf(parameterName);
		return getParameterValue(index);
	}
	
	public Double getParameterValue_as_Double(String parameterName) {
		int index = parameterNames.indexOf(parameterName);
		if (index >= 0 ) {
			return ( Double.parseDouble( parameterValues.get(index)) );	
		} else {
			System.out.println("Sorry cant find parameter "+parameterName);
			return null;
		}
	}
	
	public Integer getParameterValue_as_Integer(String parameterName) {
		int index = parameterNames.indexOf(parameterName);
		if (index >= 0) {
			return ( Integer.parseInt( parameterValues.get(index)) );
		} else {
			System.out.println("Sorry cant find parameter "+parameterName);
			return null;
		}
	}
}
