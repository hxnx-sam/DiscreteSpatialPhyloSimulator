package io;

import java.io.*;
import java.util.*;
import java.text.*;

import individualBasedModel.DiscreteSpatialPhyloSimulator;
import individualBasedModel.Event;
import individualBasedModel.EventType;

/**
 * basic string logging class
 * @author sam
 * @created 17 June 2013
 */
public class Logger {

	protected String path 	= "test//";
	protected String name 	= "temp";
	protected String ext  	= ".csv";
	protected String xmlName = "DSPS";
	
	protected BufferedWriter outFile;
	protected SimpleDateFormat df		= new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	
	protected boolean echoToScreen		= true;
	protected int	  echoEvery			= 100;
	protected int	  lineNo			= 0;
	
	protected Date logFileCreated		= new Date();
	
	protected String paramDelim			= "=";
	protected String paramDelim2		= ",";
	
	protected List<EventType> recordTypes	= new ArrayList<EventType>();
	
	/**
	 * blank constructor (does nothing else)
	 */
	public Logger() {
		
	}
	
	/**
	 * creates the logger, opens the log file and adds a file creation time stamp
	 * @param path
	 * @param name
	 * @param ext
	 */
	public Logger(String path, String name, String ext) {
		this.path = path;
		this.name = name;
		this.ext  = ext;
		openFile();
		addFileCreationTimeStamp();
	}
	
	public Logger(String path, String name, String ext, EventType recordEventType) {
		this.path = path;
		this.name = name;
		this.ext  = ext;
		setRecordType(recordEventType);
		openFile();
		addFileCreationTimeStamp();
		write("Record eventType"+paramDelim+recordEventType);
		write( (new Event()).toOutputHeader() );
	}
	
	////////////////////////////////////////////////
	// getters and setters

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getExt() {
		return ext;
	}

	public void setExt(String ext) {
		this.ext = ext;
	}
	
	public void setRecordType(EventType e) {
		this.recordTypes.add(e);
	}
	
	public void setRecordType(List<EventType> es) {
		this.recordTypes.addAll(es);
	}
	
	/**
	 * use echoEvery = 0 to turn off echo to screen
	 * @param echoEvery
	 */
	public void setEchoEvery(int echoEvery) {
		if (echoEvery >= 1) {
			echoToScreen 	= true;
			this.echoEvery 	= echoEvery;
		} else {
			echoToScreen = false;
		}
	}
	
	//////////////////////////////////////////////////////
	
	public void openFile() {
		String fname = path + name + ext;
		
		try {
			outFile 		= new BufferedWriter(new FileWriter(fname));
			logFileCreated 	= new Date();
			
			if (echoToScreen) {
				System.out.println("Log file open = "+fname+" at "+df.format(logFileCreated) );
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void closeFile() {
		try {
			outFile.close();
			
			if (echoToScreen) {
				System.out.println("Log file closed");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void closeFileWithStamp() {
		
		
		String line = "Log file closed on : "+df.format(new Date());
		if (ext.toLowerCase().contains("xml")) {
			write("</"+xmlName+">");
			
			line = "<!-- "+line+" -->";
			write(line);
		} else {
			write(line);
		}
		
		closeFile();
	}
	
	public void write(String line) {
		try {
			outFile.write(line);
			outFile.newLine();
			
			if (echoToScreen) {
				if ( (lineNo % echoEvery) == 0) {
					System.out.println("Log\t"+lineNo+"\t"+line);
				}
			}
			lineNo++;
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void write(List<String> lines) {
		try {
			for (String l : lines) {
				outFile.write(l);
				outFile.newLine();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void writeParameters(List<String[]> params) {
		try {
			for (String[] pp : params) {
				if (pp.length >= 2) {
					String line = pp[0] + paramDelim + pp[1];
					for (int i = 2; i < pp.length; i++) {
						line = line + paramDelim2 + pp[i];
					}
					outFile.write(line);
					outFile.newLine();
				} else {
					outFile.write(pp[0]);
					outFile.newLine();
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void writeParametersXML(List<String[]> params, int indent) {
		
		String indentTabs = "";
		for (int i = 0; i < indent; i++) {
			indentTabs = indentTabs + "\t";
		}
		
		try {
			for (String[] pp : params) {
				if (pp.length >= 2) {
					
					String line = indentTabs + "<parameter id=\"" + pp[0] + "\" value=\""+pp[1];
					for (int i = 2; i < pp.length; i++) {
						line = line + paramDelim2 + pp[i];
					}
					line = line + "\"/>";
					outFile.write(line);
					outFile.newLine();
					
				} else {
					String line = indentTabs + "<parameter id=\""+pp[0]+"\"/>";
					outFile.write(line);
					outFile.newLine();
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void recordEvent(Event e) {
		if (recordTypes.contains(e.getType())) {
			write(e.toOutput());
		}
	}
	
	public void addFileCreationTimeStamp() {
			
		String line = "Log file created on : "+df.format(logFileCreated);
		if (ext.toLowerCase().contains("xml")) {
			String xmlStartLine = "<?xml version=\"1.0\" standalone=\"yes\"?>";
			write(xmlStartLine);
			write("");
			
			String appLine = "<!-- File Created by "+DiscreteSpatialPhyloSimulator.version+" -->";
			write(appLine);
			
			line = "<!-- "+line+" -->";
			write(line);
			write("");
			
			write("<"+xmlName+">");
		} else {
			write(line);
		}
	}
	
}
