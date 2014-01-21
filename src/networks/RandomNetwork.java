package networks;

import math.Distributions;
import io.Parameter;

public class RandomNetwork extends BasicNetwork {

	double plink = 0.1;
	
	public RandomNetwork() {
		super();
	}
	
	/**
	 * creates a random network of n BasicNodes, probability of connection = plink
	 * @param numberOfNodes
	 * @param plink
	 */
	public RandomNetwork(int numberOfNodes, double plink) {
		super(numberOfNodes);
		this.plink = plink;
		create();
	}
	
	public void setPlink(double plink) {
		this.plink = plink;
	}
	
	@Override
	public void setParameter(Parameter p) {
		if ( p.getId().equals("plink") ) {
			plink = Double.parseDouble( p.getValue() );
		} else {
			System.out.println("RandomNetwork.setParameter - sorry can only set the value of plink");
		}
	}
	
	@Override
	public void create() {
		for (int i = 0; i < (nodes.size()-1); i++) {
			NetworkNode a = nodes.get(i);
			for (int j = (i+1); j < nodes.size(); j++) {
				NetworkNode b = nodes.get(j);
				double x = Distributions.randomUniform();
				if ( x < plink) {
					createLink(a,b);
				}
			}
		}
	}
	
}
