package individualBasedModel;

import io.Parameter;

import java.util.List;
import java.util.ArrayList;

/**
 * class to take a sample of all infecteds just before they recover
 * @author sam
 * @created 25 June 2013
 * @version 4 July 2013
 */
public class JustBeforeRecoverySampler implements Sampler {
	
	protected double justBefore = 1e-16;

	public JustBeforeRecoverySampler() {
		
	}
	
	public void setJustBefore(double d) {
		this.justBefore = d;
	}

	/** 
	 * never does population sampling
	 */
	@Override
	public List<Event> generateSamplingEvents(Population thePopulation,
			double actionTime) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Event> generateSamplingEvents(Event eventPerformed) {
		
		List<Event> samplingEvents = null;
		
		if (eventPerformed.success && (eventPerformed.type == EventType.RECOVERY)) {
			//System.out.println("Attempt to set sampling from "+eventPerformed.toOutput());
			
			samplingEvents 	= new ArrayList<Event>();
			Event e 		= new Event();
			/*
			e.fromHost		= eventPerformed.fromHost;
			e.toHost		= eventPerformed.toHost;
			e.fromDeme		= eventPerformed.fromDeme;
			e.toDeme		= eventPerformed.toDeme;
			e.creationTime  = eventPerformed.creationTime;
			e.actionTime    = eventPerformed.actionTime - justBefore;
			e.type		    = EventType.SAMPLING;
			*/
			e.setSamplingEvent(eventPerformed.toHost, eventPerformed.creationTime, eventPerformed.actionTime - justBefore);
			//e.success = true;
			samplingEvents.add(e);
			
			//System.out.println("Sampling: "+e.toOutput());
		}
		
		return samplingEvents;
	}
	
	@Override
	public List<String[]> getSamplerParameterList() {
		List<String[]> params = new ArrayList<String[]>();

		String className = this.getClass().getName(); 
		params.add(new String[]{"SamplerType", className});
		params.add(new String[]{"justBefore",""+justBefore});
		
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
				
				if (pairs[0].equals("justBefore")) {
					justBefore = Double.parseDouble(pairs[1]);
				}
				i++;
			}
		}
	}
	*/
	
	@Override
	public void setSamplerParameters(List<Parameter> params) {
		int i			    = 0;
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
				
				if (p.getId().equals("justBefore")) {
					justBefore = Double.parseDouble(p.getValue());
				}
				
				i++;
			}
		}
	}
	
}
