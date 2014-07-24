package networks;

/**
 * Class to represent locations, using lat/long, northing/easting, or x/y, with an optional name field.  
 * provides methods to calculate distances between location also.
 * (This code is also used in BroadwickExamples)
 * 
 * @author slycett
 * @version 10 June 2014
 * @version 24 July 2014
 */
public class Location {
    
    // class variables
    static final double         rad         = 2*Math.PI/360;
    static final double         earthRadius = 6371;
    public static final String  delim = ",";
    
    // class methods
    public static LocationType getLocationType(String a, String b) {
    	
    	if ( ( a.toUpperCase().startsWith("LAT") ) && (b.toUpperCase().startsWith("LON"))  ) {
    		return LocationType.LATLONG;
    	} else if ( a.toUpperCase().startsWith("NORTH") && b.toUpperCase().startsWith("EAST")   ) {
    		return LocationType.NORTHINGEASTING;
    	} else {
    		return LocationType.XY;
    	}
    	
    }
    
    /////////////////////////////////////////////////////////////
    // instance variables and methods
    
    String          name;
    LocationType    locType;
    double          x;
    double          y;
    double          northing;
    double          easting;
    double          lat;
    double          lon;
    
    public Location(double x, double y, LocationType locType) {
        this.locType = locType;
        setCoordinates(x, y, locType);
    }
    
    public Location(String name, double x, double y, LocationType locType) {
        this.name    = name;
        this.locType = locType;
        setCoordinates(x, y, locType);
    }
    
    //////////////////////////////////////////////////////////////////

    
    void setNorthingEasting(double x, double y) {
        setNorthing(x);
        setEasting(y);
    }
    
    void setLatLon(double x, double y) {
        setLat(x);
        setLon(y);
    }
    
    void setXY(double x, double y) {
        setX(x);
        setY(y);
    }
    
    private void setCoordinates(double x, double y, LocationType locType) {
        
        switch (locType) {
            case NORTHINGEASTING: setNorthingEasting(x,y); 
                break;
            case LATLONG: setLatLon(x,y);
                break;
            case XY: setXY(x,y);
                break;
        }
    }
    
    //////////////////////////////////////////////////////////////////
    
     
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }

    public LocationType getLocType() {
        return locType;
    }

    public void setLocType(LocationType locType) {
        this.locType = locType;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getNorthing() {
        return northing;
    }

    public void setNorthing(double northing) {
        this.northing = northing;
    }

    public double getEasting() {
        return easting;
    }

    public void setEasting(double easting) {
        this.easting = easting;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }
    
    ///////////////////////////////////////////////////////////////////
    
    private double XYDistance(Location b) {
        double xx = (this.x - b.x) * (this.x - b.x);
        double yy = (this.y - b.y) * (this.y - b.y);
        double rr = Math.sqrt(xx + yy);
        return rr;
    }
    
    private double NEDistance(Location b) {
        double xx = (this.northing - b.northing) * (this.northing - b.northing);
        double yy = (this.easting - b.easting) * (this.easting - b.easting);
        double rr = Math.sqrt(xx + yy);
        return rr;
    }
    
    /**
     * LatLonDistance - this is based on spherical law of cosines
     * 
     * @param b
     * @return 
     */
    private double LatLonDistance(Location b) {
        /*  # Spherical Law of Cosines for Lat-Lon to distances
            # http://www.movable-type.co.uk/scripts/latlong.html
            # S. J. Lycett
            # 24 April 2013

            # Spherical law of cosines: 	d = acos( sin(f1).sin(f2) + cos(f1).cos(f2).cos(??) ).R
            # where 	f is latitude, ? is longitude, R is earthís radius (mean radius = 6,371km)
            # note that angles need to be in radians to pass to trig functions!
            # Excel: 	=ACOS(SIN(lat1)*SIN(lat2)+COS(lat1)*COS(lat2)*COS(lon2-lon1))*6371

            # R = 6371 // km
            distance_from_lat_lon <- function( lat1=lat1, lon1=lon1, lat2=lat2, lon2=lon2, R=6371 ) {

            rad  <- 2*pi/360
            phi1 <- lat1*rad
            phi2 <- lat2*rad
            dlam <- (lon2-lon1)*rad

            ss   <- sin(phi1)*sin(phi2)
            cc   <- cos(phi1)*cos(phi2)*cos(dlam)
            d    <- R*acos(ss + cc)

            return( d )
        }

        # check via websites
        #d <- distance_from_lat_lon(50,0,60,0)	# 1111.949 OK
        #d <- distance_from_lat_lon(0,10,0,20)	# 1111.949 OK
        #d <- distance_from_lat_lon(50,10,50,20)	# 714.2143 OK
        */
        
        double phi1= this.lat*rad;
        double phi2= b.lat*rad;
        double dlam= (b.lon - this.lon)*rad;
        
        double ss  = Math.sin(phi1)*Math.sin(phi2);
        double cc  = Math.cos(phi1)*Math.cos(phi2)*Math.cos(dlam);
        double dd  = earthRadius*Math.acos( ss + cc);
        
        return dd;
    }
    
    
    public double distanceFrom(Location b) {
        
        if (b.locType != this.locType) {
            System.out.println("Location.distanceFrom: sorry type conversion not implemented yet");
            return -1;
        } else {
            double rr = -1;
            switch (locType) {
                case NORTHINGEASTING: rr = NEDistance(b);
                    break;
                case LATLONG: rr = LatLonDistance(b);
                    break;
                case XY: rr = XYDistance(b);
                    break;
            }
            return rr;
        }
        
    }
    
    ///////////////////////////////////////////////////////////////////
    
    @Override
    public String toString() {
        String txt = "";
        switch (locType) {
            case LATLONG: 
                txt = this.name + delim + this.lat + delim + this.lon;
                break;
            case NORTHINGEASTING: 
                txt = this.name + delim + this.northing + delim + this.easting;
                break;
            case XY: 
                txt = this.name + delim + this.x + delim + this.y;
                break;
            }
        return txt;
    }
    
    public String toHeader() {
        String txt = "";
        switch (locType) {
            case LATLONG:
                txt = "Location" + delim + "Latitude" + delim + "Longitude";
                break;
            case NORTHINGEASTING:
                txt = "Location" + delim + "Northing" + delim + "Easting";
                break;
            case XY:
                txt = "Location" + delim + "X" + delim + "Y";
                break;
        }
        return txt;
    }

	

    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    /*
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + Objects.hashCode(this.name);
        return hash;
    }
    

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Location other = (Location) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        return true;
    }
    */
    
    /* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Location))
			return false;
		Location other = (Location) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
    
    
}

