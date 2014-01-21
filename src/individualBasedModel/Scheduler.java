package individualBasedModel;

import java.util.*;
import io.Logger;
import trees.*;

/**
 * 
 * @author sam
 * @created 15 June 2013
 * @version 25 June 2013 - added transmission tree and sampler
 * @version 4 July 2013  - added method for tau leap
 * @version 24 July 2013 - only keep top event
 * @version 1 Sept 2013  - updated runEvents(Logger,Logger) to match other runEvents (24 July)
 * @version 26 Sept 2013 - population generates events
 * @vesrion 27 Sept 2013 - stopping criterion
 */
public class Scheduler {

	protected double 		time 			= 0;
	protected List<Event> 	events			= new ArrayList<Event>();
	protected Population	thePopulation 	= new Population();
	protected TransmissionTree tt			= new TransmissionTree();
	protected Sampler		theSampler		= new JustBeforeRecoverySampler();
	
	protected String		delim			= ",";
	
	protected int			maxIts			= -1;
	protected double		maxTime			= -1;
	protected boolean		stopWhenAllI	= false;
	protected boolean		stopWhenAllR	= true;
	
	public Scheduler() {
		
	}
	
	
	////////////////////////////////////////////////////////////
	
	/**
	 * @param maxIts the maxIts to set
	 */
	public void setMaxIts(int maxIts) {
		this.maxIts = maxIts;
	}


	/**
	 * @param maxTime the maxTime to set
	 */
	public void setMaxTime(double maxTime) {
		this.maxTime = maxTime;
	}


	/**
	 * @param stopWhenAllI the stopWhenAllI to set
	 */
	public void setStopWhenAllI(boolean stopWhenAllI) {
		this.stopWhenAllI = stopWhenAllI;
	}


	/**
	 * @param stopWhenAllR the stopWhenAllR to set
	 */
	public void setStopWhenAllR(boolean stopWhenAllR) {
		this.stopWhenAllR = stopWhenAllR;
	}


	////////////////////////////////////////////////////////////

	public void addEvent(Event e) {
		events.add(e);
		Collections.sort(events);
	}
	
	public void addEvent(List<Event> es) {
		events.addAll(es);
		Collections.sort(events);
	}
	
	public boolean hasEvents() {
		return (events.size() > 0);
	}
	
	/*
	protected Event doEvent() {
		
		// get first event from list
		Event e = events.remove(0);
		
		// perform the event
		thePopulation.performEvent(e);
		
		// update time to this event
		time	= e.getActionTime();
			
		return e;
		
	}
	*/
	
	////////////////////////////////////////////////////////////
	
	/**
	 * runs events in the list, and generates new events as appropriate
	 * @param eventLogger
	 * @param populationLogger
	 */
	// DEPCRECATED
	/*
	public void runEvents(Logger eventLogger, Logger populationLogger) {
		
		while ( events.size() > 0 ) {
			
			Event eventPerformed = doEvent();
			//System.out.println(eventPerformed);
			
			if (eventPerformed.success) {
				
				if (eventLogger != null) {
					eventLogger.recordEvent(eventPerformed);
				}
				
				if (tt != null) {
					// add sampling event to transmission tree
					tt.processEvent(eventPerformed);
				}
				
				Host h = null;
				
				if ( (eventPerformed.type == EventType.EXPOSURE) ||
					 (eventPerformed.type == EventType.INFECTION) ) {
					// add newly exposed host to active hosts population list
					h = eventPerformed.toHost;
					
					if ((h != null) && (!thePopulation.activeHosts.contains(h))) {
						thePopulation.activeHosts.add(h);
					}
					
				} else if ( eventPerformed.type == EventType.RECOVERY ) {
					// remove newly recovered host from active hosts population list					
					h = eventPerformed.toHost;
					
					if ((h != null) && (thePopulation.activeHosts.contains(h))) {
						thePopulation.activeHosts.remove(h);
					}
					
					// perge list of events from recovered hosts (which will be invalid now)
					List<Event> toRemove = new ArrayList<Event>();
					for (Event tempE : events) {
						if (tempE.fromHost.equals(h)) {
							toRemove.add(tempE);
						} else if (tempE.toHost.equals(h)) {
							toRemove.add(tempE);
						}
					}
					
					if (toRemove.size() > 0) {
						for (Event tempE : toRemove) {
							events.remove(tempE);
							//events.remove(toRemove);
						}
					}
					
				}
			
				// generate sampling events depending on sampler
				// note some samplers dont generate sampling events based on events performed
				List<Event> samplingEvents2 = theSampler.generateSamplingEvents(eventPerformed);
				if (samplingEvents2 != null) {
					events.addAll( samplingEvents2 );
				}
				
			}
			
			
			// generate new events
			// since only those active hosts may generate events, just go from active hosts list
			// rather than entire population
			List<Event> newEvents = new ArrayList<Event>();
			for (Host ah : thePopulation.activeHosts) {
				Event e2 = ah.generateNextEvent(time);
				if (e2 != null) {
					newEvents.add(e2);
				}
			}
			
			// SJL 24 July 2013
			// only keep most recent event
			if (newEvents.size() > 0) {
				Collections.sort(newEvents);
				events.add(newEvents.get(0));
			}
			
			// also generate sampling events depending on sampler
			// note some samplers dont generate population events all the time
			List<Event> samplingEvents = theSampler.generateSamplingEvents(thePopulation, time);
			if (samplingEvents != null) {
				events.addAll( samplingEvents );
			}
						
			//events.addAll(newEvents);
			
			Collections.sort(events);
			

			if (populationLogger == null) {
				System.out.println(time+"\tListI="+thePopulation.activeHosts.size()+"\tEvents="+events.size()+"\t"+thePopulation.info());
			} else {
				populationLogger.write( toOutput() );
			}
			
		}
		
	}
	*/
	
	// TO DO
	/*
	 * Population.generateEvent

	 * 
	 */
	
	
	/**
	 * runs events in the list, and generates new events as appropriate
	 * runs all events in list within leap time
	 * @param eventLogger
	 * @param populationLogger
	 */
	// DEPRECATED
	/*
	public void runEvents(Logger eventLogger, Logger populationLogger, double leap) {
		
		while ( events.size() > 0 ) {
			
		  double stopTime = time + leap;
			
		  List<Event> eventsDone = new ArrayList<Event>();
			
			// do all the events upto the leap time
		  while ( (time < stopTime) && (events.size() > 0) ) {
				eventsDone.add( doEvent() );
		  }
		 
		  if (eventsDone.size() >= 10) {
			  System.out.println("Number of events in leap = "+eventsDone.size()+" at time = "+time);
		  }
		  
		  for (Event eventPerformed : eventsDone ) {
			
			if (eventPerformed.success) {
				
				if (eventLogger != null) {
					eventLogger.recordEvent(eventPerformed);
				}
				
				if (tt != null) {
					// add sampling event to transmission tree
					tt.processEvent(eventPerformed);
				}
				
				Host h = null;
				
				if ( (eventPerformed.type == EventType.EXPOSURE) ||
					 (eventPerformed.type == EventType.INFECTION) ) {
					// add newly exposed host to active hosts population list
					h = eventPerformed.toHost;
					
					if ((h != null) && (!thePopulation.activeHosts.contains(h))) {
						thePopulation.activeHosts.add(h);
					}
					
				} else if ( eventPerformed.type == EventType.RECOVERY ) {
					// remove newly recovered host from active hosts population list					
					h = eventPerformed.toHost;
					
					if ((h != null) && (thePopulation.activeHosts.contains(h))) {
						thePopulation.activeHosts.remove(h);
					}
					
					// perge list of events from recovered hosts (which will be invalid now)
					List<Event> toRemove = new ArrayList<Event>();
					for (Event tempE : events) {
						if (tempE.fromHost.equals(h)) {
							toRemove.add(tempE);
						} else if (tempE.toHost.equals(h)) {
							toRemove.add(tempE);
						}
					}
					
					if (toRemove.size() > 0) {
						for (Event tempE : toRemove) {
							events.remove(tempE);
							//events.remove(toRemove);
						}
					}
					
				}
			
				// generate sampling events depending on sampler
				// note some samplers dont generate sampling events based on events performed
				List<Event> samplingEvents2 = theSampler.generateSamplingEvents(eventPerformed);
				if (samplingEvents2 != null) {
					events.addAll( samplingEvents2 );
				}
				
			}
			
		  }
			
			// generate new events
			// since only those active hosts may generate events, just go from active hosts list
			// rather than entire population
			List<Event> newEvents = new ArrayList<Event>();
			for (Host ah : thePopulation.activeHosts) {
				Event e2 = ah.generateNextEvent(time);
				if (e2 != null) {
					newEvents.add(e2);
				}
			}
			
			// SJL 24 July 2013
			// only keep most recent event
			if (newEvents.size() > 0) {
				Collections.sort(newEvents);
				events.add(newEvents.get(0));
			}
			
		
			events.add(thePopulation.generateEvent( time ));
			
			// also generate sampling events depending on sampler
			// note some samplers dont generate population events all the time
			List<Event> samplingEvents = theSampler.generateSamplingEvents(thePopulation, time);
			if (samplingEvents != null) {
				events.addAll( samplingEvents );
			}
			
			
			
			//events.addAll(newEvents);
			
			Collections.sort(events);
			

			if (populationLogger == null) {
				System.out.println(time+"\tListI="+thePopulation.activeHosts.size()+"\tEvents="+events.size()+"\t"+thePopulation.info());
			} else {
				populationLogger.write( toOutput() );
			}
			
		}
		
	}
	*/
	
	/**
	 * runs events in the list, and generates new events as appropriate
	 * runs all events in list within leap time
	 * @param eventLogger
	 * @param populationLogger
	 */
	public void runEvents(Logger eventLogger, Logger populationLogger, double leap) {
		
		// temporary fix
		// make sure stopping condition is set for SI
		if ( thePopulation.getDemesModelType().equals(ModelType.SI) ) {
			setStopWhenAllI(true);
		}
		
		boolean goOn = true;
		int numIts   = 0;
		
		while ( goOn ) {
		  numIts++;
			
		  //double stopTime = time + leap;
	
		  // do all the events upto the leap time
		  //List<Event> eventsDone = new ArrayList<Event>();			
		  
		  // temp remove leap
		 // while ( (time <= stopTime) && (events.size() > 0) ) {
			  
			    //Event e = doEvent();
			    //System.out.println("Just done "+e.toString());
			    
			    // get first event from list
				Event e = events.remove(0);
				
				// perform the event
				thePopulation.performEvent(e);
				
				// update time to this event
				time	= e.getActionTime();
				
				// add to done list (whether successful or not)
				//eventsDone.add( e );
				  
				
		  //}
		 
		  //if (eventsDone.size() >= 10) {
		//	  System.out.println("Number of events in leap = "+eventsDone.size()+" at time = "+time);
		  //}
		  
		  // EVENT SAMPLING
		  //for (Event eventPerformed : eventsDone ) {
			Event eventPerformed = e;
				
			if (eventPerformed.success) {
				
				if (eventLogger != null) {
					eventLogger.recordEvent(eventPerformed);
				} //else {
				//	System.out.println("Success for: "+eventPerformed.toString());
				//}
				
				if (tt != null) {
					// add event to transmission tree
					tt.processEvent(eventPerformed);
				}
				
				// generate sampling events depending on sampler
				// note some samplers dont generate sampling events based on events performed
				List<Event> samplingEvents2 = theSampler.generateSamplingEvents(eventPerformed);
				if (samplingEvents2 != null) {
					events.addAll( samplingEvents2 );
				}
				
			}
			
			if (eventPerformed.getType() != EventType.SAMPLING) {
				// GENERATE NEW EVENT
			  	Event newEvent = thePopulation.generateEvent( time );
			  	if (newEvent != null) {
			  		events.add( newEvent );
			  	}
			}
			
		  //}
			
		  	
			
			// also generate sampling events depending on sampler
			// note some samplers dont generate population events all the time
			List<Event> samplingEvents = theSampler.generateSamplingEvents(thePopulation, time);
			if (samplingEvents != null) {
				events.addAll( samplingEvents );
			}
			
			if (events.size() > 1) {
				Collections.sort(events);
			}
			

			if (populationLogger == null) {
				System.out.println(time+"\tListI="+thePopulation.totalInfected()+"\tEvents="+events.size()+"\t"+thePopulation.info());
			} else {
				populationLogger.write( toOutput() );
			}
			
			// do next iteration ?
			goOn = (events.size() > 0);
			if (maxTime > 0) {
				goOn = (time < maxTime);
			}
			if (maxIts > 0) {
				goOn = goOn && (numIts < maxIts);
			}
			if (stopWhenAllI) {
				goOn = goOn && (thePopulation.totalInfected() < thePopulation.totalHosts());
			}
			if (stopWhenAllR) {
				goOn = goOn && (thePopulation.totalRecovered() < thePopulation.totalHosts());
			}
			
		}
		
	}
	
	////////////////////////////////////////////////////////////
	
	String schedulerState() {
		String line = time + delim + thePopulation.totalInfected() + delim + events.size();
		return line;
	}
	
	String schedulerStateHeader() {
		 String line = "Time" + delim + "ActiveHosts" + delim + "Events";
		 return line;
	}
	
	public String toOutput() {
		return ( schedulerState() + delim + thePopulation.populationState());
	}
	
	public String toOutputHeader() {
		return ( schedulerStateHeader() + delim + thePopulation.populationStateHeader() );
	}
	
	public String toTransmissionTreeNewick() {
		return (tt.toNewick());
	}

	//////////////////////////////////////////////////////////////////////////////
	// methods to access the population - primarily for testing

	public void setThePopulation(Population thePopulation) {
		this.thePopulation = thePopulation;
	}

	
	public Population getThePopulation() {
		return thePopulation;
	}
	
	public double getTime() {
		return time;
	}
	
	public List<Event> getEvents() {
		return events;
	}
	
	public TransmissionTree getTransmissionTree() {
		return tt;
	}
}
