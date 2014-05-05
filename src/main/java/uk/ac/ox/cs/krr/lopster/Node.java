package main.java.uk.ac.ox.cs.krr.lopster;

import java.util.Set;

/*This class is a data structure for labeled nodes. The fields include an
 * integer that stores the number of the node and a set of strings that 
 * is the labeling of the node.
*/

public class Node {

	private Integer m_number;
	private Set<String> m_label;
	
	public Node(Integer number, Set<String> label){
		this.m_number=number;
		this.m_label=label;
	}
	
	public Integer getNumber(){
		return m_number;
	}
	
	public Set<String> getLabel(){
		return m_label;
	}
}
