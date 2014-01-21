package individualBasedModel;

import io.Parameter;

import java.util.List;

/**
 * factory class to generate demes with parameters
 * @author Samantha Lycett
 * @created 4 July 2013
 * @version 4 July 2013
 */
public class DemeFactory {

	public static Deme getDeme(List<Parameter> params) {
		
		Deme deme = null;
		
		if (params.get(0).getParentTag().equals("Deme")) {
			deme = new Deme();
			deme.setDemeParameters(params);
		}
		
		return deme;
	}
	
}
