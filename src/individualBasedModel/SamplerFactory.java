package individualBasedModel;

import io.Parameter;

import java.util.List;

/**
 * factory class for creating a sampling scheme from parameter list
 * @author Samantha Lycett
 * @created 4 July 2013
 * @version 4 July 2013
 */
public class SamplerFactory {
	
	private static boolean echo = true;

	public static Sampler getSampler(List<Parameter> params) {
		
		Sampler sampler = null;
		
		// which one to generate ?
		int 	i 		= 0;
		boolean found 	= false;
		while ( (i < params.size()) && (!found) ) {
			Parameter p = params.get(i);
			
			if (p.getId().equals("SamplerType")) {
				found			   = true;
				String samplerType = p.getValue();
				if ( samplerType.contains(RandomInfectedSampler.class.getName()) ) {
					sampler = new RandomInfectedSampler();
					
					if (echo) {
						System.out.println("Creating "+sampler.getClass().getName());
					}
					
				} else if ( samplerType.contains(JustBeforeRecoverySampler.class.getName()) ) {
					sampler = new JustBeforeRecoverySampler();
					
					if (echo) {
						System.out.println("Creating "+sampler.getClass().getName());
					}
					
				} else {
					sampler = new JustBeforeRecoverySampler();
					
					if (echo) {
						System.out.println("SamplerFactory.getSampler - WARNING didnt understand "+samplerType+" so went with JustBeforeRecoverySampler");
						System.out.println("Creating "+sampler.getClass().getName());
					}
					
				}
				sampler.setSamplerParameters(params);	
			}
		}
		
		if ((sampler == null) && (echo)) {
			System.out.println("SamplerFactor.getSampler - this didnt work returning null");
		}
		
		return sampler;
	}
	
}
