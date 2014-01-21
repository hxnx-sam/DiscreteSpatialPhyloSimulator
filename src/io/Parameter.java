package io;

import org.w3c.dom.Element;

/**
 * 
 * @author Samantha Lycett
 * @created 3 July 2013
 * @version 4 July 2013
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


		
}

