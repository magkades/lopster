package main.java.uk.ac.ox.cs.krr.lopster;

import java.util.ArrayList;

/* This class is a data structure for dlv rules. The fields include the head atom of the 
 * rule and a list of the body atoms. There is a method for getting a string representation
 * of the rule.
*/
public class DLVRule {

	private Atom m_headAtom;
    private ArrayList<Atom> m_bodyAtoms;
    
    public DLVRule(Atom headAtom,ArrayList<Atom> bodyAtoms){
		m_headAtom=headAtom;
		m_bodyAtoms=bodyAtoms;
	}
	
	public Atom getHeadAtom(){
		return this.m_headAtom;
	}
	
	public ArrayList<Atom> getBodyAtoms(){
		return this.m_bodyAtoms;
	}
	
	//it converts an DLV rule into a string
	public String getDLVSyntaxRule(){
		StringBuffer buffer=new StringBuffer();
        buffer.append(m_headAtom.getTextRepresentation());
        if (!m_bodyAtoms.isEmpty()){
        	buffer.append(" :- ");        
            for (Atom bodyAtom:m_bodyAtoms) {
            	buffer.append(bodyAtom.getTextRepresentation());
            	buffer.append(",");
			}
			buffer.deleteCharAt(buffer.length()-1);
        }        
        buffer.append(".");
        return buffer.toString();
	}

}
