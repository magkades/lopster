package main.java.uk.ac.ox.cs.krr.lopster;

import java.util.ArrayList;

/*This class is a data structure for first-order logic atoms. The fields include a 
 * string that stores the predicate and  an arraylist that stores a list of terms,
 * where terms are represented with strings.
*/
public class Atom {
		
	private String m_predicate;
	private ArrayList<String> m_terms;
	
	public Atom(String predicate,ArrayList<String> terms){
		m_predicate=predicate;
		m_terms=terms;
	}
	
	public String getPredicate(){
		return this.m_predicate;
	}
	
	public ArrayList<String> getTerms(){
		return this.m_terms;
	}
	
	//it converts an atom into a string according to the dlv syntax
	public String getTextRepresentation(){
		StringBuffer buffer=new StringBuffer();
		buffer.append(m_predicate);
		if (!m_terms.isEmpty()){
			buffer.append("(");
			for (String term:m_terms){
				buffer.append(term);
				buffer.append(",");
			}
			buffer.deleteCharAt(buffer.length()-1);
			buffer.append(")");
		}		
		return buffer.toString();
	}

}
