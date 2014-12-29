package io;

public interface ConfigurationXMLInterface {

	public void setPath(String path);
	public void setRootname(String rootname);
	public void setSimpath(String simPath);
	public void setSimname(String simName);
	
	public void setSeed(long seed);
	public void setNreps(int nreps);
	public void setModelType(String modelType);
	public void setInfectionParameters(String[] ip);
	
	public void writeConfigurationFile();
}
