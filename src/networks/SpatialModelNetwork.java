package networks;

import io.Parameter;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;


/**
 * 
 * @author slycett
 * @created 23 July 2014
 * @version 24 July 2014
 */
public class SpatialModelNetwork implements Network {

	List<NetworkNode> 	nodes;
	
	NetworkModelType  	modelType = NetworkModelType.GRAVITY_MODEL;
	List<Parameter> 	params;
	
	
	public SpatialModelNetwork() {
		nodes  = new ArrayList<NetworkNode>();
		params = new ArrayList<Parameter>();
	}
	
	public SpatialModelNetwork(NetworkModelType netType) {
		modelType 	= netType;
		nodes  		= new ArrayList<NetworkNode>();
		params 		= new ArrayList<Parameter>();
	}
	
	//////////////////////////////////////////////////////////////////////////////
	
	@Override
	public NetworkModelType getNetworkModelType() {
		return modelType;
	}
	
	/* (non-Javadoc)
	 * @see networks.Network#setParameter(io.Parameter)
	 */
	@Override
	public void setParameter(Parameter p) {
		if (params == null) {
			params = new ArrayList<Parameter>();
		}
		params.add(p);		
	}

	/* (non-Javadoc)
	 * @see networks.Network#getParameter(io.Parameter)
	 */
	@Override
	public Parameter getParameter(Parameter p) {
		
		int index = params.indexOf(p);
		if (index >= 0) {
			return params.get( index );
		} else {
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see networks.Network#getParameters()
	 */
	@Override
	public List<Parameter> getParameters() {
		return params;
	}

	/* (non-Javadoc)
	 * @see networks.Network#setNodes(java.util.List)
	 */
	@Override
	public void setNodes(List<NetworkNode> nn) {
		this.nodes = nn;
	}

	
	public void create(NetworkModelType netType) {
		this.modelType = netType;
		create();
	}
	
	/** 
	 * create the network from the nodes, using the parameters and a model type
	 */
	@Override
	public void create() {
		 
		// add links to all nodes, and also sets physical distance and gravity model weight
		for (int i = 0; i < nodes.size(); i++) {
			LocationNetworkNode locN = (LocationNetworkNode)(nodes.get(i));
			locN.setNetworkNeighbours(nodes);
			
			//System.out.println(locN.name+" has "+locN.numberOfNeighbours()+" neighbours");
		}
		
		
		int temp = params.indexOf(new Parameter("distanceThreshold"));
		if ( temp >= 0)  {
			double thres = Double.parseDouble(params.get(temp).getValue());
			for (NetworkNode n : nodes) {
				LocationNetworkNode locN = (LocationNetworkNode)n;
				locN.removeIfFurtherThan(thres);
			}
		}
		
		
		if (modelType.equals(NetworkModelType.GRAVITY_MODEL)) {
			
			// no parameters required
			for (int i = 0; i < nodes.size(); i++) {
				LocationNetworkNode locN = (LocationNetworkNode)(nodes.get(i));
				locN.calculateGravityModelWeights();
			}
			
		} else if (modelType.equals(NetworkModelType.GENERALGRAVITY_MODEL)) {
			
			double alpha 	= 1;
			double beta 	= 1;
			double gamma 	= 2;
			
			int index	 = params.indexOf( new Parameter("alpha") );
			if (index >= 0) alpha = Double.parseDouble( params.get(index).getValue() );
			 
			index		 = params.indexOf( new Parameter("beta") );
			if (index >= 0) beta  = Double.parseDouble( params.get(index).getValue() );
			
			index		 = params.indexOf( new Parameter("gamma") );
			if (index >= 0) gamma = Double.parseDouble( params.get(index).getValue() );
			
			for (NetworkNode n : nodes) {
				LocationNetworkNode locN = (LocationNetworkNode)n;
				locN.calculateGravityModelWeights(alpha, beta, gamma);
			}
			
		} else if (modelType.equals(NetworkModelType.EXPONENTIAL_KERNEL)) {
			
			double alpha 	= 1;
			
			int index	 = params.indexOf( new Parameter("alpha") );
			if (index >= 0) alpha = Double.parseDouble( params.get(index).getValue() );
			
			for (NetworkNode n : nodes) {
				LocationNetworkNode locN = (LocationNetworkNode)n;
				locN.calculateExponentialKernelWeights(alpha);
			}
			
			
		} else if (modelType.equals(NetworkModelType.GAUSSIAN_KERNEL)) {
			
			double alpha 	= 1;
			
			int index	 = params.indexOf( new Parameter("alpha") );
			if (index >= 0) alpha = Double.parseDouble( params.get(index).getValue() );
			
			for (NetworkNode n : nodes) {
				LocationNetworkNode locN = (LocationNetworkNode)n;
				locN.calculateGaussianKernelWeights(alpha);
			}
			
			
		} else if (modelType.equals(NetworkModelType.POWERLAW_KERNEL)) {
			
			double alpha 	= 1;
			double beta		= 1;
			
			int index	 = params.indexOf( new Parameter("alpha") );
			if (index >= 0) alpha = Double.parseDouble( params.get(index).getValue() );
			
			index	 = params.indexOf( new Parameter("beta") );
			if (index >= 0) beta = Double.parseDouble( params.get(index).getValue() );
			
			for (NetworkNode n : nodes) {
				LocationNetworkNode locN = (LocationNetworkNode)n;
				locN.calculatePowerLawKernelWeights(alpha, beta);
			}
			
		} else {
			System.out.println("SpatialModelNetwork.create - no weights set for "+modelType);
		}
		
		temp = params.indexOf(new Parameter("scaleWeights"));
		if (temp > 0) {
			double a= Double.parseDouble(params.get(temp).getValue());
			
			for (NetworkNode n : nodes) {
				LocationNetworkNode locN = (LocationNetworkNode)n;
				locN.scaleWeights(a);
			}
			
		}
		
		temp = params.indexOf(new Parameter("normaliseWeights"));
		if (temp > 0) {
			
			boolean norm = Boolean.parseBoolean( params.get(temp).getValue() );
			
			if (norm) {
				for (NetworkNode n : nodes) {
					LocationNetworkNode locN = (LocationNetworkNode)n;
					locN.normaliseWeights();
				}
			}
			
		}
		
		
	}

	
	@Override
	public List<NetworkNode> getNodes() {
		return nodes;
	}

	/* (non-Javadoc)
	 * @see networks.Network#getNumberOfNodes()
	 */
	@Override
	public int getNumberOfNodes() {
		if (nodes == null) {
			return 0;
		} else {
			return nodes.size();
		}
	}

	@Override
	public boolean isDirected() {
		return false;
	}

	/**
	 * add link a to b, and also b to a because this is an undirected model
	 */
	@Override
	public void createLink(NetworkNode a, NetworkNode b) {
		a.addNetworkNode(b);
		b.addNetworkNode(a);
	}

	/**
	 * remove link a to b, and also b to a because this is an undirected model
	 */
	@Override
	public void removeLink(NetworkNode a, NetworkNode b) {
		a.removeNetworkNode(b);
		b.removeNetworkNode(a);
	}

	/**
	 * returns true if a has a link to b (not checked b to a).
	 */
	@Override
	public boolean isLink(NetworkNode a, NetworkNode b) {
		return  a.hasNetworkNeighbour(b);
	}

	/////////////////////////////////////////////////////////////////////////////////////////
	
	public void readNodeLocationsFromFile(String fname, String delim) {
		
		try {
			Scanner inFile = new Scanner(new File(fname));
			
			String header 	  = inFile.nextLine();
			String[] hels	  = header.split(delim);
			
			int col1		  = 1;
			int col2		  = 2;
			if (hels[col1].toUpperCase().startsWith("LON")) {
				col1 = 2;
				col2 = 1;
			} else if (hels[col1].toUpperCase().startsWith("EAST")) {
				col1 = 2;
				col2 = 1;
			} else if (hels[col1].toUpperCase().startsWith("Y")) {
				col1 = 2;
				col2 = 1;
			}
			
			LocationType locType = Location.getLocationType(hels[col1], hels[col2]);
			System.out.println("SpatialModelNetwork - location type = "+locType+" detected");
			
			nodes = new ArrayList<NetworkNode>();
			
			
			String locName	  = "";
			double pos1		  = 0;
			double pos2		  = 0;
			double citySize	  = 1;
			
			while (inFile.hasNext()) {
				String line = inFile.nextLine();
				String[] els= line.split(delim);
				
				if (els.length >= 3) {
					
					locName    = els[0];
					pos1	   = Double.parseDouble(els[col1]);
					pos2	   = Double.parseDouble(els[col2]);
					
					if (els.length > 3) {
						citySize   = Double.parseDouble(els[3]);
					} else {
						citySize   = 1;
					}
					
					Location loc 			 = new Location(locName, pos1, pos2, locType);
					LocationNetworkNode locN = new LocationNetworkNode(loc, citySize);
					
					System.out.println(locN.toString());
					
					nodes.add(locN);
				}
				
				
				
			}
			
			inFile.close();
			
			System.out.println("Read "+nodes.size()+" location nodes");
			
			System.out.println("-- end of file --");
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

}
