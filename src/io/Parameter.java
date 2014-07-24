package io;

import org.w3c.dom.Element;

/**
 * 
 * @author Samantha Lycett
 * @created 3 July 2013
 * @version 4 July 2013
 * @version 23 July 2014 - added hash code and equals based on id value only, and constructor with id only
 * @version 24 July 2014 - added constructor for id and value only
 */
public class Parameter {
	
		String id;
		String value;
		String parentTag;
		int	   parentNumber;
		
		public Parameter(String parentTag, int parentNumber, Element el) {
			this.id 			= el.getAttribute("id");
			this.value 			= el.getAttribute("value");
			this.parentTag 		= parentTag;
			this.parentNumber 	= parentNumber;
		}
		
		/**
		 * use this constructor to make a dummy object which is used in searches ( e.g. ArrayList . contains etc)
		 * @param id
		 */
		public Parameter(String id) {
			this.id = id;
		}
		
		/**
		 * use this constructor to make simple parameters (e.g. when not configuring from XML).
		 * @param id
		 * @param value
		 */
		public Parameter(String id, String value) {
			this.parentTag    = "";
			this.parentNumber = 0;
			this.id 	= id;
			this.value 	= value;
		}
		
		//////////////////////////////////////////////////////////////////////////////////
		
		/**
		 * @return the id
		 */
		public String getId() {
			return id;
		}

		/**
		 * @return the value
		 */
		public String getValue() {
			return value;
		}

		/**
		 * @return the parentTag
		 */
		public String getParentTag() {
			return parentTag;
		}

		/**
		 * @return the parentNumber
		 */
		public int getParentNumber() {
			return parentNumber;
		}
		
		
		//////////////////////////////////////////////////////////////////////////////////
		
		
		public String[] getNameValuePair() {
			String[] pair = new String[2];
			pair[0] = id;
			pair[1] = value;
			return pair;
		}
		
		//////////////////////////////////////////////////////////////////////////////////
		
		public String toString() {
			return (parentTag + "-" + parentNumber + "\t" + id + "\t" + value);
		}

		

		//////////////////////////////////////////////////////////////////////////////////
		// hash code and equals based on id only
		
		/* (non-Javadoc)
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((id == null) ? 0 : id.hashCode());
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
			if (!(obj instanceof Parameter))
				return false;
			Parameter other = (Parameter) obj;
			if (id == null) {
				if (other.id != null)
					return false;
			} else if (!id.equals(other.id))
				return false;
			return true;
		}


		
		
		
}

