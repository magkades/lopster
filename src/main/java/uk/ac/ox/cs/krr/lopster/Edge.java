package main.java.uk.ac.ox.cs.krr.lopster;

import java.util.Set;

/*This class is a data structure for labeled edges. The fields include an
 * integer that stores the number of the from node, another integer that 
 * stores the number of the to node and a set of strings that 
 * is the labeling of the edge.
*/

public class Edge {

	private Integer m_from;
	private Integer m_to;
	private Set<String> m_label;
	
	public Edge(Integer from,Integer to, Set<String> label){
		this.m_from=from;
		this.m_to=to;
		this.m_label=label;
	}
	
	public Integer getFromNode(){
		return m_from;
	}
	
	public Integer getToNode(){
		return m_to;
	}
	
	public Set<String> getLabel(){
		return m_label;
	}
}
