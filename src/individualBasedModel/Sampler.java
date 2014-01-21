package individualBasedModel;

import io.Parameter;

import java.util.List;

/**
 * inferface for classes generating SAMPLING events
 * @author sam
 * @created 25 June 2013
 * @version 1 July 2013
 * @version 4 July 2013 - using Parameters
 */
public interface Sampler {

	/**
	 * generate sampling events from the population
	 * @param thePopulation
	 * @param actionTime
	 * @return
	 */
	List<Event> generateSamplingEvents(Population thePopulation, double actionTime);
	
	/**
	 * generated sampling events based on this event (e.g. infection, recovery)
	 * @param e
	 * @return
	 */
	List<Event> generateSamplingEvents(Event e);
	
	/**
	 * return parameter,value pairs for use with Logger etc
	 * @return
	 */
	List<String[]> getSamplerParameterList();
	
	/**
	 * set parameters from parameter,value pairs
	 * @param params
	 */
	//void setSamplerParameters( List<String[]> params);
	void setSamplerParameters( List<Parameter> params);
}
