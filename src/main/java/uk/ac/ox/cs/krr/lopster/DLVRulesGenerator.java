package main.java.uk.ac.ox.cs.krr.lopster;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

/* This class generates (sets of) objects of the java class 
 * DLVRule for the representation of description graphs.
*/

public class DLVRulesGenerator {
	
	protected enum GraphAtomMode { FUNCTION_TERMS_ON, VARIABLES_ON, CONSTANTS_ON }
	
	//given a description graph it returns its translation into LP rules	
	public Set<DLVRule>  translateDGintoRules(DescriptionGraph descriptionGraph){
		Set<DLVRule> rules=new LinkedHashSet<DLVRule>();
		rules.addAll(produceLayoutRules(descriptionGraph,false));
		rules.add(produceStartConceptFact(descriptionGraph));
		return rules;
	}
	
	//it produces a rule of the form chebi_12345(X) -> g_chebi_12345(X,f1(X),f2(X),f3(X))
	public DLVRule produceStartRule(DescriptionGraph descriptionGraph){
		Atom headAtom=produceGraphAtom(descriptionGraph,GraphAtomMode.FUNCTION_TERMS_ON);
	    ArrayList<Atom> bodyAtoms=new ArrayList<Atom>();
	    bodyAtoms.add(produceStartAtom(descriptionGraph,"X"));
		return new DLVRule(headAtom,bodyAtoms);
	}
	
	//it produces a rule of the form chebi_12345(X) -> g_chebi_12345(X,c1,c2,c3)
	public DLVRule produceMSAStartRule(DescriptionGraph descriptionGraph){
		Atom headAtom=produceGraphAtom(descriptionGraph,GraphAtomMode.CONSTANTS_ON);
	    ArrayList<Atom> bodyAtoms=new ArrayList<Atom>();
	    bodyAtoms.add(produceStartAtom(descriptionGraph,"X"));
		return new DLVRule(headAtom,bodyAtoms);
	}

	//if flag is false, it produces a set of rules of the form chebi_12345(X) -> hasAtom(X,f1(X))
	//else if flag is true, it produces a set of rules of the form chebi_12345(X) -> hasAtom(X,c1)
	//for the labels of all nodes and all edges of the description graph
	public Set<DLVRule> produceLayoutRules(DescriptionGraph descriptionGraph,boolean forMSACheck){
		
		Set<DLVRule> rules=new LinkedHashSet<DLVRule>();
		ArrayList<Atom> bodyAtoms=new ArrayList<Atom>();
	    
		bodyAtoms.add(produceStartAtom(descriptionGraph.getStartConcept()));
	    
		//create all rules that give types to the nodes of the graph
	    for (Node node:descriptionGraph.getNodes()){
	    	//System.out.println("Producing layout rule for node "+node.getNumber().toString());
	    	Set<String> label=node.getLabel();
	    	//System.out.println("Size of the label is "+label.size());
	    	for (String unaryPredicate:label){
	    		ArrayList<String> headAtomTerms=new ArrayList<String>();
	    		String term;
	    		if (!forMSACheck){
	    			term=buildFunctionLayoutTerm(descriptionGraph.getStartConcept(), node.getNumber());
	    		}
	    		else {
	    			term=buildConstantLayoutTerm(descriptionGraph.getStartConcept(), node.getNumber());
	    		}
	    		headAtomTerms.add(term);
	       		Atom headAtom=new Atom(unaryPredicate,headAtomTerms);
	    		//System.out.println(new DLVRule(headAtom,bodyAtoms).getDLVSyntaxRule());
	    		rules.add(new DLVRule(headAtom,bodyAtoms));
	    	}
	    }	    
	    
	    //create all rules that give types to the edges of the graph
	    for (Edge edge:descriptionGraph.getEdges()){
	    	//System.out.println("Producing layout rule for edge "+edge.getFromNode().toString()
	    	//		+"->"+edge.getToNode().toString());
	    	Set<String> label=edge.getLabel();
	    	//System.out.println("Size of the label is "+label.size());
	    	for (String binaryPredicate:label){
	    		ArrayList<String> headAtomTerms=new ArrayList<String>();
	    		String fromTerm,toTerm;
	    		if (!forMSACheck){
	    			fromTerm=buildFunctionLayoutTerm(descriptionGraph.getStartConcept(), edge.getFromNode());
	    			toTerm=buildFunctionLayoutTerm(descriptionGraph.getStartConcept(), edge.getToNode());
	    		}
	    		else {
	    			fromTerm=buildConstantLayoutTerm(descriptionGraph.getStartConcept(), edge.getFromNode());
	    			toTerm=buildConstantLayoutTerm(descriptionGraph.getStartConcept(), edge.getToNode());
	    		}
	    		headAtomTerms.add(fromTerm);
	    		headAtomTerms.add(toTerm);
	    		Atom headAtom=new Atom(binaryPredicate,headAtomTerms);
	    		//System.out.println(new DLVRule(headAtom,bodyAtoms).getDLVSyntaxRule());
	    		rules.add(new DLVRule(headAtom,bodyAtoms));
	    	}
	    }	    
	   return rules;
	}

	//it produces an atom of the form startConcept(X)
	public Atom produceStartAtom(String startConcept){
		ArrayList<String> bodyAtomTerms=new ArrayList<String>();
	    bodyAtomTerms.add("X");    
	    return new Atom(startConcept,bodyAtomTerms);
	}
	
	//it produces a term of the form f_startConcept_1(X)
	//unless nodeNumber is 0 in which case it produces X
	public String buildFunctionLayoutTerm(String startConcept,int nodeNumber){
		StringBuffer sb=new StringBuffer();
		if (nodeNumber!=0){
			sb.append("f_");
			sb.append(startConcept);
			sb.append("_");
			sb.append(nodeNumber);
			sb.append("(X)");
			String term=sb.toString();
			clearBuffer(sb);
			return term;
		}
		else {
			sb.append("X");
			String term=sb.toString();
			clearBuffer(sb);
			return term;
		}			
	}

	//it produces a term of the form c_startConcept_1
	//unless nodeNumber is 0 in which case it produces X
	public String buildConstantLayoutTerm(String startConcept,int nodeNumber){
		StringBuffer sb=new StringBuffer();
		if (nodeNumber!=0){
			sb.append("c_");
			sb.append(startConcept);
			sb.append("_");
			sb.append(nodeNumber);
			String term=sb.toString();
			clearBuffer(sb);
			return term;
		}
		else {
			sb.append("X");
			String term=sb.toString();
			clearBuffer(sb);
			return term;
		}			
	}
	
	
	//it produces an atom of the form chebi_12345(variable)
	public Atom produceStartAtom(DescriptionGraph descriptionGraph, String variable){
		ArrayList<String> bodyAtomTerms=new ArrayList<String>();
	    bodyAtomTerms.add(variable);    
	    return new Atom(descriptionGraph.getStartConcept(),bodyAtomTerms);
	    
	}
	
	//it produces an atom of the form g_chebi_12345(Y0,Y1,Y2) or
	//g_chebi_12345(X,f1(X),f2(X)) depending on the flag
	public Atom produceGraphAtom(DescriptionGraph descriptionGraph, GraphAtomMode graphAtomMode){
		StringBuffer sb=new StringBuffer();
	    sb.append("g_");
	    sb.append(descriptionGraph.getStartConcept());
	    String predicate=sb.toString();
	    clearBuffer(sb);
	    ArrayList<String> terms=new ArrayList<String>();
	    if ((GraphAtomMode.FUNCTION_TERMS_ON.equals(graphAtomMode)) || (GraphAtomMode.CONSTANTS_ON.equals(graphAtomMode))){
	    	terms.add("X");
	    }
	    else if (GraphAtomMode.VARIABLES_ON.equals(graphAtomMode)) {
	    	terms.add("Y0");
	    }
	        
	    for (int i=1; i<descriptionGraph.getNodes().size(); i++){
	    	if (GraphAtomMode.FUNCTION_TERMS_ON.equals(graphAtomMode)){
	    		sb.append("f_");
		    	sb.append(descriptionGraph.getStartConcept());
		    	sb.append("_");
		    	sb.append(i);
		    	sb.append("(X)");
	    	}
	    	else if (GraphAtomMode.VARIABLES_ON.equals(graphAtomMode)) {
	    		sb.append("Y");
		    	sb.append(i);
	    	}
	    	else if (GraphAtomMode.CONSTANTS_ON.equals(graphAtomMode)) {
	    		sb.append("c_");
		    	sb.append(descriptionGraph.getStartConcept());
		    	sb.append("_");
		    	sb.append(i);		    	
	    	}
	    	terms.add(sb.toString());
	    	clearBuffer(sb);
	    }
	    return new Atom(predicate,terms);
	}
	
	
	//it produces a fact of the form chebi_12345(c_chebi_12345)	
	public DLVRule produceStartConceptFact(DescriptionGraph descriptionGraph){
		StringBuffer sb=new StringBuffer();
		ArrayList<String> headAtomTerms=new ArrayList<String>();
		sb.append("c_");
		sb.append(descriptionGraph.getStartConcept());
		headAtomTerms.add(sb.toString());
		Atom headAtom=new Atom(descriptionGraph.getStartConcept(),headAtomTerms);
	    ArrayList<Atom> bodyAtoms=new ArrayList<Atom>();
		return new DLVRule(headAtom,bodyAtoms);
	}
	
	//given a description graph it returns its translation into datalog rules for MSA check
	public Set<DLVRule>  translateDGintoMSARules(DescriptionGraph descriptionGraph){
		Set<DLVRule> rules=new LinkedHashSet<DLVRule>();
		
		rules.addAll(produceLayoutRules(descriptionGraph,true));
		rules.addAll(produceSuccessionRules(descriptionGraph));
		rules.addAll(produceTagRules(descriptionGraph));
		rules.addAll(produceCycleDetectionRules(descriptionGraph));
		rules.add(produceStartConceptFact(descriptionGraph));
		
		return rules;
	}
	
	//given a description graph it returns all the succession rules of the form
	//chebi_12345(X) -> successor(X,f1(X))
	public Set<DLVRule>  produceSuccessionRules (DescriptionGraph descriptionGraph){
		Set<DLVRule> rules=new LinkedHashSet<DLVRule>();
		StringBuffer sb=new StringBuffer();
		
		ArrayList<Atom> bodyAtoms=new ArrayList<Atom>();
	    bodyAtoms.add(produceGraphAtom(descriptionGraph, GraphAtomMode.VARIABLES_ON));
		
		for (int i=1; i<descriptionGraph.getNodes().size(); i++){
	    	ArrayList<String> headAtomTerms=new ArrayList<String>();
	        sb.append("Y0");
	        headAtomTerms.add(sb.toString());
	        clearBuffer(sb);
	        sb.append('Y');
	        sb.append(i);
	        headAtomTerms.add(sb.toString());
	        clearBuffer(sb);
	        Atom headAtom=new Atom("successor",headAtomTerms);
	    	rules.add(new DLVRule(headAtom,bodyAtoms));
	    	
	    }	
		
		return rules;
	}
	
	//given a description graph it returns all the tag rules of the form
	//chebi_12345(X) -> p_chebi_12345_1(f1(X))
	public Set<DLVRule>  produceTagRules (DescriptionGraph descriptionGraph){
		Set<DLVRule> rules=new LinkedHashSet<DLVRule>();
		StringBuffer sb=new StringBuffer();
		
		ArrayList<Atom> bodyAtoms=new ArrayList<Atom>();
	    bodyAtoms.add(produceGraphAtom(descriptionGraph, GraphAtomMode.VARIABLES_ON));
		
		for (int i=1; i<descriptionGraph.getNodes().size(); i++){
	    	ArrayList<String> headAtomTerms=new ArrayList<String>();
	        sb.append('Y');
	        sb.append(i);
	        headAtomTerms.add(sb.toString());
	        clearBuffer(sb);
	        sb.append("p_");
	    	sb.append(descriptionGraph.getStartConcept());
	    	sb.append("_");
	    	sb.append(i);
	    	String tag=sb.toString();
	    	clearBuffer(sb);
	        Atom headAtom=new Atom(tag,headAtomTerms);
	        rules.add(new DLVRule(headAtom,bodyAtoms));	    	
	    }			
		return rules;
	}
	
	
	//given a description graph it returns all the cycle
	//detection rules of the form
	//p_chebi_12345_1(X),descendant(X,Y),p_chebi_12345_1(Y)-> cycle
	public Set<DLVRule>  produceCycleDetectionRules (DescriptionGraph descriptionGraph){
		Set<DLVRule> rules=new LinkedHashSet<DLVRule>();
		ArrayList<String> cycleAtomTerms=new ArrayList<String>();
		Atom cycleAtom=new Atom("cycle",cycleAtomTerms);
		
		for (int i=1; i<descriptionGraph.getNodes().size(); i++){
    		ArrayList<Atom> bodyAtomsCycleRule=new ArrayList<Atom>();
    		String tag="p_"+descriptionGraph.getStartConcept()+"_"+i;
    		
    		ArrayList<String> bodyAtomLeftCycleRuleTerms=new ArrayList<String>();
    		bodyAtomLeftCycleRuleTerms.add("X");
    		Atom leftBodyAtom=new Atom(tag,bodyAtomLeftCycleRuleTerms);
    		bodyAtomsCycleRule.add(leftBodyAtom);
    		
    		ArrayList<String> bodyAtomMidCycleRuleTerms=new ArrayList<String>();
    		bodyAtomMidCycleRuleTerms.add("X");
    		bodyAtomMidCycleRuleTerms.add("Y");
    	    Atom headAtomPropRule=new Atom("descendant",bodyAtomMidCycleRuleTerms);    		
    		bodyAtomsCycleRule.add(headAtomPropRule);
    		
    		ArrayList<String> bodyAtomRightCycleRuleTerms=new ArrayList<String>();
    		bodyAtomRightCycleRuleTerms.add("Y");
    		Atom rightBodyAtom=new Atom(tag,bodyAtomRightCycleRuleTerms);
    		bodyAtomsCycleRule.add(rightBodyAtom);    
    		
    		rules.add(new DLVRule(cycleAtom,bodyAtomsCycleRule));
    	}			
		return rules;
	}
	
	//for a set of description graphs it returns for each description graph
	//rules of the form  p_chebi_12345_1(X),descendant(X,Y),p_chebi_12345_1(Y)-> cycle
	//and the propagation and transitive closure rules for cycle detection
	public Set<DLVRule>  produceDescendantTransClosureRules (){
		Set<DLVRule> rules=new LinkedHashSet<DLVRule>();
		
		ArrayList<Atom> bodyAtomsPropRule=new ArrayList<Atom>();
		ArrayList<String> bodyAtomPropRuleTerms=new ArrayList<String>();
		bodyAtomPropRuleTerms.add("X");
		bodyAtomPropRuleTerms.add("Y");
	    bodyAtomsPropRule.add(new Atom("successor",bodyAtomPropRuleTerms));
	    
	    ArrayList<String> headAtomPropRuleTerms=new ArrayList<String>();
		headAtomPropRuleTerms.add("X");
		headAtomPropRuleTerms.add("Y");
	    Atom headAtomPropRule=new Atom("descendant",headAtomPropRuleTerms);
	    
	    rules.add(new DLVRule(headAtomPropRule,bodyAtomsPropRule));
	    
	    ArrayList<Atom> bodyAtomsTransRule=new ArrayList<Atom>();
		ArrayList<String> bodyAtomLeftTransRuleTerms=new ArrayList<String>();
		bodyAtomLeftTransRuleTerms.add("X");
		bodyAtomLeftTransRuleTerms.add("Y");
	    bodyAtomsTransRule.add(new Atom("descendant",bodyAtomLeftTransRuleTerms));
	    ArrayList<String> bodyAtomRightTransRuleTerms=new ArrayList<String>();
		bodyAtomRightTransRuleTerms.add("Y");
		bodyAtomRightTransRuleTerms.add("Z");
	    bodyAtomsTransRule.add(new Atom("successor",bodyAtomRightTransRuleTerms));
	    
	    
	    ArrayList<String> headAtomTransRuleTerms=new ArrayList<String>();
		headAtomTransRuleTerms.add("X");
		headAtomTransRuleTerms.add("Z");
	    Atom headAtomTransRule=new Atom("descendant",headAtomTransRuleTerms);
	    
	    rules.add(new DLVRule(headAtomTransRule,bodyAtomsTransRule));
	    
	    return rules;
	}
	
	//this methods clears the content of a string buffer
	public StringBuffer clearBuffer(StringBuffer sb){
		return sb.delete(0,sb.length());
	}

}
