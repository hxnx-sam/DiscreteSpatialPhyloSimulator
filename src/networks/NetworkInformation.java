package networks;

/**
 * 
 * @author slycett
 * @version 23 July 2014
 */
public class NetworkInformation {

	String delim = "\t";
	String delim2= "\n";
	Network network;
	int numberOfEdges 		= -1;
	double averageDegree 	= -1;
	
	public NetworkInformation(Network network) {
		this.network = network;
	}
	
	/////////////////////////////////////////////////////////////////
	
	public int getNumberOfEdges() {
		if (numberOfEdges < 0) {
			numberOfEdges = 0;
			for (NetworkNode n : network.getNodes()) {
				numberOfEdges += n.numberOfNeighbours();
			}
			if (!network.isDirected()) {
				numberOfEdges = numberOfEdges/2;
			}
		}
		return numberOfEdges;
	}
	
	public double getAverageDegree() {
		if (averageDegree < 0) {
			averageDegree = 0;
			for (NetworkNode n : network.getNodes()) {
				averageDegree += (double)n.numberOfNeighbours();
				//System.out.println(n.getName()+" has "+n.numberOfNeighbours()+" neighbours");
			}
			averageDegree = averageDegree/(double)network.getNumberOfNodes();
		}
		return averageDegree;
	}
	
	/////////////////////////////////////////////////////////////////
	
	public String toString() {
		StringBuffer txt = new StringBuffer();
		
		txt.append("Type"+delim+network.getClass().getName()+delim2);
		txt.append("ModelType"+delim+network.getNetworkModelType()+delim2);
		txt.append("Directed"+delim+network.isDirected()+delim2);
		txt.append("NumberOfNodes"+delim+network.getNumberOfNodes()+delim2);
		txt.append("NumberOfEdges"+delim+getNumberOfEdges()+delim2);
		txt.append("AverageDegree"+delim+getAverageDegree()+delim2);
		
		return txt.toString();
	}
	
}
