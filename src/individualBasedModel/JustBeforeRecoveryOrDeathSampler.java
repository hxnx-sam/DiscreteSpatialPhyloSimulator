package individualBasedModel;

import io.Parameter;

import java.util.ArrayList;
import java.util.List;

/**
 * class to take a sample of all infecteds just before they recover or die
 * @author Samantha Lycett
 * @created 28 Oct 2015
 * @version 28 Oct 2015 - the same as JustBeforeRecovery but added or die functionality
 */
public class JustBeforeRecoveryOrDeathSampler implements Sampler {

	protected double justBefore = 1e-16;

	public JustBeforeRecoveryOrDeathSampler() {
		
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
		
		if (eventPerformed.success) {
			if (eventPerformed.type == EventType.RECOVERY) {
				//System.out.println("Attempt to set sampling from "+eventPerformed.toOutput());
			
				samplingEvents 	= new ArrayList<Event>();
				Event e 		= new Event();
				
				e.setSamplingEvent(eventPerformed.toHost, eventPerformed.creationTime, eventPerformed.actionTime - justBefore);
				samplingEvents.add(e);
			
				//System.out.println("Sampling: "+e.toOutput());
			} else if (eventPerformed.type == EventType.DEATH) {
				// 28 oct 2015
				// need to check whether the host actually was in infected state first, otherwise no need to sample
				// (note could make this exposed or infected, but I think actually just infected here)
				
				samplingEvents 	= new ArrayList<Event>();
				Event e 		= new Event();
				
				Host h = eventPerformed.toHost;
				if ( h.getState().equals(InfectionState.INFECTED)  ) {
					e.setSamplingEvent(eventPerformed.toHost, eventPerformed.creationTime, eventPerformed.actionTime - justBefore);
					samplingEvents.add(e);
				}
				
			}
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
