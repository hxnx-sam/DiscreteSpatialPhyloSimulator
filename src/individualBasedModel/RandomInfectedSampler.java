package individualBasedModel;

import io.Parameter;

import java.util.List;
import java.util.ArrayList;

import math.Distributions;

public class RandomInfectedSampler implements Sampler {
	
	protected double probabilityOfSamplingInfected 	= 0.5;
	
	// add a small amount to the current time for the sampling time
	// can be a fixed or random amount (according to gaussian distribution)
	protected double delay					  		= 1e-6;
	protected double stdev_delay					= 1e-6;
	protected boolean useRandomMeanDelay			= false;
	
	// sample every so often
	// applicable if doing population sampling
	protected boolean allowPopulationSampling		= true;
	protected double lastSample						= 0;
	protected double sampleEvery					= 5;
	
	protected boolean allowEventSampling			= true;
	
	public RandomInfectedSampler() {
		
	}
	
	public void setProbabilityOfSamplingInfected(double p) {
		this.probabilityOfSamplingInfected = p;
	}

	public void setDelay(double d) {
		this.delay = d;
	}
	
	/**
	 * only applies if useRandomMeanDelay = true
	 * @param s
	 */
	public void setStdev_delay(double s) {
		this.stdev_delay = s;
	}
	
	/**
	 * use this to switch between fixed delay sampling and random gaussian delay sampling
	 * @param d
	 */
	public void setUseRandomMeanDelay(boolean d) {
		this.useRandomMeanDelay = d;
	}
	
	/**
	 * only applies if allowPopulationSampling = true
	 * @param s
	 */
	public void setSampleEvery(double s) {
		this.sampleEvery = s;
	}
	
	public void setAllowPopulationSampling(boolean pp) {
		this.allowPopulationSampling = pp;
	}
	
	public void setAllowEventSampling(boolean pp) {
		this.allowEventSampling = pp;
	}
	
	///////////////////////////////////////////////////////////
	
	/**
	 * generates SAMPLING events from the current active hosts, with probability = probabilityOfSamplingInfected
	 * will generate one SAMPLING event per current active host at the current time
	 * this is also like deciding to sample everybody, but the sequencing only works X% of the time
	 */
	@Override
	public List<Event> generateSamplingEvents(Population thePopulation,
			double currentTime) {
		
		if ((allowPopulationSampling) && (currentTime >= (lastSample + sampleEvery)) ) {
		
			List<Event> events = new ArrayList<Event>();
		
			for (Host h : thePopulation.getInfectedHosts() ) {
				//if (h.state == InfectionState.INFECTED) {
					double x = Distributions.randomUniform();
					if (x < probabilityOfSamplingInfected) {
						Event e = new Event();
						e.setSamplingEvent(h, currentTime, currentTime + calculateDelay() );
						events.add(e);
					}
				//}
			}
		
			lastSample = currentTime;
			
			if (events.size() > 0) {
				return events;
			} else {
				return null;
			}
			
		} else {
			return null;
		}
	}

	/**
	 * generates a sampling event from an successful infection event, with probability = probabilityOfSamplingInfected
	 * generates just one event from the new infectee
	 * no event generated if infection event not successful, or not an infection event
	 */
	@Override
	public List<Event> generateSamplingEvents(Event event) {
		
		if (allowEventSampling) {
		
			List<Event> events = new ArrayList<Event>();
		
			if (event.success && (event.type == EventType.INFECTION )) {
				double x = Distributions.randomUniform();
				if (x < probabilityOfSamplingInfected) {
					Event e = new Event();
					e.setSamplingEvent(event.toHost, event.creationTime, event.actionTime + calculateDelay() );
					events.add(e);
				}
			}
		
			if (events.size() > 0) {
				return events;
			} else {
				return null;
			}
		
		} else {
			return null;
		}
	}
	
	/**
	 * adds a small amount to the current time to generate the actual time of sampling
	 * returns a small random amount or a fixed amount
	 * @return
	 */
	private double calculateDelay() {
		if (useRandomMeanDelay) {
			double x = (Distributions.randomGaussian()*stdev_delay) + delay;
			if (x > 0) {
				return x;
			} else {
				return 0;
			}
			
		} else {
			return delay;
		}
	}

	//////////////////////////////////////////////////////////////////////////////////////////////
	
	@Override
	public List<String[]> getSamplerParameterList() {
		List<String[]> params = new ArrayList<String[]>();

		String className = this.getClass().getName(); 
		
		params.add(new String[]{"SamplerType", className});
		params.add(new String[]{"probabilityOfSamplingInfected",""+probabilityOfSamplingInfected});
		params.add(new String[]{"delay",""+delay});
		params.add(new String[]{"stdev_delay",""+stdev_delay});
		params.add(new String[]{"useRandomMeanDelay",""+useRandomMeanDelay});
		params.add(new String[]{"allowPopulationSampling",""+allowPopulationSampling});
		
		if (allowPopulationSampling) {
			params.add(new String[]{"sampleEvery",""+sampleEvery});
		}
		
		params.add(new String[]{"allowEventSampling",""+allowEventSampling});
		
		return params;
		
	}
	
	/*
	@Override
	public void setSamplerParameters(List<String[]> params) {
		
		int i 				= 0;
		boolean found 		= false;
		String className 	= this.getClass().getName(); 
		
		while ( (i < params.size()) && (!found) ) {
			String[] pairs = params.get(i);
			
			if (pairs[0].equals("SamplerType")) {
				found = (pairs[1].equals(className));
			}
			
			i++;
		}
		
		if (found) {
			while (i < params.size()) {
				String[] pairs = params.get(i);
				
				if (pairs[0].equals("probabilityOfSamplingInfected")) {
					probabilityOfSamplingInfected = Double.parseDouble(pairs[1]);
				} else if (pairs[0].equals("delay")) {
					delay = Double.parseDouble(pairs[1]);
				} else if (pairs[0].equals("stdev_delay")) {
					stdev_delay = Double.parseDouble(pairs[1]);
				} else if (pairs[0].equals("useRandomMeanDelay")) {
					useRandomMeanDelay = Boolean.parseBoolean(pairs[1]);
				} else if (pairs[0].equals("allowPopulationSampling")) {
					allowPopulationSampling = Boolean.parseBoolean(pairs[1]);
				} else if (pairs[0].equals("sampleEvery")) {
					sampleEvery = Double.parseDouble(pairs[1]);
				} else if (pairs[0].equals("allowEventSampling")) {
					allowEventSampling = Boolean.parseBoolean(pairs[1]);
				}
				
				i++;
			}
		}
		
		
	}
	*/
	
	@Override
	public void setSamplerParameters(List<Parameter> params) {
		
		int i 				= 0;
		boolean found 		= false;
		String className 	= this.getClass().getName(); 
		
		while ( (i < params.size()) && (!found) ) {
			Parameter p = params.get(i);
			
			if ( (p.getParentTag().equals("Sampler")) && (p.getId().equals("SamplerType")) ) {
				found = (p.getValue().equals(className));
			}
			
			i++;
		}
		
		if (found) {
			while (i < params.size()) {
				Parameter p = params.get(i);
				
				if (p.getParentTag().equals("Sampler")) {
				
					if (p.getId().equals("probabilityOfSamplingInfected")) {
						probabilityOfSamplingInfected = Double.parseDouble(p.getValue());
					} else if (p.getId().equals("delay")) {
						delay = Double.parseDouble(p.getValue());
					} else if (p.getId().equals("stdev_delay")) {
						stdev_delay = Double.parseDouble(p.getValue());
					} else if (p.getId().equals("useRandomMeanDelay")) {
						useRandomMeanDelay = Boolean.parseBoolean(p.getValue());
					} else if (p.getId().equals("allowPopulationSampling")) {
						allowPopulationSampling = Boolean.parseBoolean(p.getValue());
					} else if (p.getId().equals("sampleEvery")) {
						sampleEvery = Double.parseDouble(p.getValue());
					} else if (p.getId().equals("allowEventSampling")) {
						allowEventSampling = Boolean.parseBoolean(p.getValue());
					}
				
				}
				
				i++;
			}
		}
		
		
	}
	
}
