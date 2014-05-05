package main.java.uk.ac.ox.cs.krr.lopster;

import java.util.Set;

/* This class is a data structure for descriptiong graphs. The fields include a set of labeled nodes, 
 * a set of labeled edges and a string where the name of the start concept is stored.
 * */

public class DescriptionGraph {

	private Set<Node> m_nodes;
	private Set<Edge> m_edges;
	private String m_startConcept;
	
	public DescriptionGraph(Set<Node> nodes,Set<Edge> edges,String startConcept){
		this.m_nodes=nodes;
		this.m_edges=edges;
		this.m_startConcept=startConcept;
	}
	
	public Set<Node> getNodes(){
		return this.m_nodes;
	}
	
	public Set<Edge> getEdges(){
		return this.m_edges;
	}
	
	public String getStartConcept(){
		return this.m_startConcept;
	}	
	
	//it returns a string which fully describes the description graph
	public String getTextRepresentation(){
		StringBuffer buffer=new StringBuffer();
        String CRLF=System.getProperty("line.separator");
        buffer.append('[');
        buffer.append(CRLF);
        buffer.append("Nodes");
        buffer.append(CRLF);
        buffer.append("--------------------");
        buffer.append(CRLF);
        for (Node node : m_nodes) {
            buffer.append("   ");
            buffer.append(node.getNumber());
            buffer.append(" : ");
            Set<String> label=node.getLabel();
	    	for (String unaryPredicate:label){
	    		buffer.append(unaryPredicate);
	    		buffer.append(",");
	    		}
	    	buffer.deleteCharAt(buffer.length()-1);
	    	buffer.append(".");
            buffer.append(CRLF);
        }
        buffer.append(CRLF);
        buffer.append("Edges");
        buffer.append(CRLF);
        buffer.append("--------------------");
        buffer.append(CRLF);
        for (Edge edge : m_edges) {
            buffer.append("  ");
            buffer.append(edge.getFromNode());
            buffer.append(" --> ");
            buffer.append(edge.getToNode());
            buffer.append(" : ");
            Set<String> label=edge.getLabel();
	    	for (String binaryPredicate:label){
	    		buffer.append(binaryPredicate);
	    		buffer.append(",");
	    	}
	    	buffer.deleteCharAt(buffer.length()-1);
	    	buffer.append(".");
            buffer.append(CRLF);
        }
        buffer.append(CRLF);
        buffer.append("Start Concept");
        buffer.append(CRLF);
        buffer.append("--------------------");
        buffer.append(CRLF);
        buffer.append(m_startConcept);
        buffer.append(']');
        
        return buffer.toString();
	}
	

}
