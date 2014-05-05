package main.java.uk.ac.ox.cs.krr.lopster;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.jnati.FileUtils;

/*This class is a data structure for stable models. The fields include the file location of the 
 * stable model and a set of atom java objects. The class has methods for parsing the stable model 
 * file produced by the DLV engine, for fact entailment and for extraction of subClass-superClass
 * subsumptions.
 *  */

public class StableModel {
	
	private String m_stableModelFilePath;
	private Set<Atom> m_facts;
	
	//the constructor assembles the set of atoms by parsing
	//the content of the file in the specified path
	public StableModel(String stableModelFilePath){
		this.m_stableModelFilePath=stableModelFilePath;
		this.m_facts=new LinkedHashSet<Atom>();
		
		try{
			String fileContent = FileUtils.readString(new FileReader(m_stableModelFilePath));
			//System.out.println(fileContent);
			ArrayList<String> stringFacts=retrieveStringFacts(fileContent);
			for (String stringFact:stringFacts){
				//System.out.println("The following string fact was retrieved: "+stringFact);
				Atom atom=new Atom(getPredicate(stringFact),getTerms(stringFact));
				//String predicate=atom.getPredicate();
				//if ((!predicate.equals("cleanReachable")) && (!(predicate.equals("closedLoopAtLeast3"))))
				if (atom.getTerms().size()==1)
					m_facts.add(atom);
				//System.out.println("The following atom was added:            "+atom.getTextRepresentation());				
			}			
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public String getStableModelFilePath(){
		return m_stableModelFilePath;
	}

	public Set<Atom> getFacts(){
		return m_facts;
	}
	
	//this method parses the file content and returns a list of 
	//strings, each of which corresponds to a fact
	public ArrayList<String> retrieveStringFacts(String fileContent){
		ArrayList<String> stringFacts=new ArrayList<String>();
		
		String delimiter = "(,\\s)|(\\{)|(\\})";
		Pattern delimiterPattern = Pattern.compile(delimiter);
		String[] sf = delimiterPattern.split(fileContent);
		for (String stringFact:sf){
			if ((!stringFact.isEmpty()) && (!Character.isWhitespace(stringFact.charAt(0)))){
				stringFacts.add(stringFact);
			}
		}
		return stringFacts;
	}
	
	//this method returns the predicate name from a string fact 
	public String getPredicate(String fact){
		String predicate="";
		
		try{
			String delimiter = "\\(";
			Pattern delimiterPattern = Pattern.compile(delimiter);
			String[] items = delimiterPattern.split(fact);
			if (items.length==0)
				predicate=fact;
			else
				predicate=items[0];
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return predicate;		
	}
	
	//this method parses a string fact and returns a set of strings 
	//that correspond to the terms of the fact  
	public ArrayList<String> getTerms(String fact){
		ArrayList<String> terms=new ArrayList<String>();
		
		try{
			Pattern pattern = Pattern.compile("(\\(.*\\))");
            Matcher matcher = pattern.matcher(fact);
            String stringTerms="";
            if (matcher.find()) {
            	stringTerms=matcher.group();
            	stringTerms=stringTerms.substring(1, stringTerms.length()-1);
                String delimiter = ",";
        		Pattern delimiterPattern = Pattern.compile(delimiter);
        		String[] arrayTerms = delimiterPattern.split(stringTerms);
        		if (arrayTerms.length >= 1)
        			for (String stringTerm:arrayTerms)
        				terms.add(stringTerm);
        		else
        			terms.add(stringTerms);
            }
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return terms;
	}
	
	//the method checks whether a fact is entailed by the current
	//stable model
	public boolean isEntailed(Atom fact){
		String factPredicate=fact.getPredicate();
		boolean found=false;
		
		for (Atom a:m_facts){
			found= found | (a.getPredicate().equals(factPredicate));
		}		
		if (found){
			for (Atom a:m_facts){
				found= (a.getTerms().size() == fact.getTerms().size());
				if (found){
					for (int i=0; i<a.getTerms().size(); i++){
						found = found & ((a.getTerms().get(i)).equals(fact.getTerms().get(i)));
					}
					if (found)
						return found;
				}				
			}
		}		
		return found;
	}
	
	//this method returns all the unary predicates that subsume
	//the predicate argument
	public Set<String> retrieveSuperClasses(String predicate){
		Set<String> superClasses=new LinkedHashSet<String>();
		
		for (Atom a:m_facts){
			if (a.getTerms().size()==1){
				if ((a.getTerms().get(0)).equals("c_"+predicate)){
					superClasses.add(a.getPredicate());				
				}
			}
		}		
		return superClasses;
	}
}
