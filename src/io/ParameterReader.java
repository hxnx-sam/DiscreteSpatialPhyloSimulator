package io;

import java.util.*;
import javax.xml.parsers.*;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * based on example from http://totheriver.com/learn/xml/xmltutorial.html
 * @author Samantha Lycett
 * @created 3 July 2013
 * @version 4 July 2013
 * @version 24 July 2013
 */
public class ParameterReader {

	String 				fname;
	Document 			parametersXML;
	
	List<List<Parameter>>	params = new ArrayList<List<Parameter>>();
	
	
	public ParameterReader(String fname) {
		this.fname = fname;
		readFile();
		parseDocument();
	}
	

	/////////////////////////////////////////////////////////////////////////////////
	
	public List<List<Parameter>> getParameters() {
		return params;
	}
	
	/////////////////////////////////////////////////////////////////////////////////
	
	private void readFile() {
		
		DocumentBuilderFactory doc_factory = DocumentBuilderFactory.newInstance();
		
		try {
			
			// Using factory get an instance of document builder
			DocumentBuilder doc = doc_factory.newDocumentBuilder();
			
			// parse using builder to get DOM representation of the XML file
			parametersXML = doc.parse(fname);
			

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void parseDocument(){
		// get the root elememt
		Element docEl = parametersXML.getDocumentElement();
		
		//System.out.println(docEl.getNodeName());
		if (!docEl.getNodeName().equals("DSPS")) {
			System.out.println("ParameterReader.parseDocument - WARNING document type "+docEl.getNodeName()+" is not expected, but carrying on regardless");
		}
		
		String[] parentTags = {"General","Deme","Sampler", "PopulationStructure"};
		
		for (String parentTag : parentTags) {
			NodeList nl = docEl.getElementsByTagName(parentTag);
			
			
			for (int i = 0; i < nl.getLength(); i++) {
				
				List<Parameter> tagParams = new ArrayList<Parameter>();
				Element 	el = (Element)nl.item(i);
				NodeList 	pl = el.getElementsByTagName("parameter");
				for (int j = 0; j < pl.getLength(); j++) {
					Parameter param = new Parameter(parentTag, i, (Element)pl.item(j));
					tagParams.add(param);
					//System.out.println(param);
				}
				params.add(tagParams);
				
			}
			
		}
		
	}
	
	
	
	
}
