package main.java.uk.ac.ox.cs.krr.lopster;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/*This class creates  DLV program files in the location m_dlvFilesPath based on the description graphs from 
 *m_descriptionGraphs. Two kinds of programs are produced, (i) for checking acyclicity and (ii) 
 *for computing the stable model. The number of logic programs produced is specified by the number of molfiles
 *and the module size. 
 *m_descriptionGraphs maps start concepts to description graph objects
 *m_dlvProgram(forMSACheck)Prefix specified the prefix of the logic program names
 *m_startConceptsModules is an arraylist of modules each of which contains a set of start concepts
 *m_dlvPrograms(MSACheck)Names is an arraylist of the names of the logic programs produced
 * 
*/
public class DLVProgramManager {
	
	private int m_moduleSize;
	private String m_dlvFilesPath;
	private String m_classRulesPath;
	private int m_numberMolfiles;
	private Map<String,DescriptionGraph> m_descriptionGraphs=new HashMap<String, DescriptionGraph>();
	private String m_dlvProgramPrefix;
	private String m_dlvProgramForMSACheckPrefix;
	private ArrayList<Set<String>> m_startConceptsModules=new ArrayList<Set<String>>();
	private ArrayList<String> m_dlvProgramsNames=new ArrayList<String>();
	private Set<String> m_dlvProgramsMSACheckNames=new LinkedHashSet<String>();
	
	public DLVProgramManager(int moduleSize,String dlvFilesPath,String classRulesPath,int numberMolfiles,
			Map<String,DescriptionGraph> descriptionGraphs){
		this.m_moduleSize=moduleSize;
		this.m_dlvFilesPath=dlvFilesPath;
		this.m_classRulesPath=classRulesPath;
		this.m_numberMolfiles=numberMolfiles;
		this.m_descriptionGraphs=descriptionGraphs;
		setDLVProgramPrefix();
		setDLVProgramForMSACheckPrefix();
		m_startConceptsModules=splitIntoModules();
		/*for (int i=0; i<m_startConceptsModules.size(); i++){
			Set<String> ss=m_startConceptsModules.get(i);
			System.out.println("Module number "+i+" with size "+ss.size()+".");
			for (String s:ss){
				System.out.println("Contains start concept "+s+".");
			}
		}*/
	}
	
	public void setDLVProgramPrefix(){
		m_dlvProgramPrefix="jMolecules"+m_numberMolfiles;
	}
	
	public void setDLVProgramForMSACheckPrefix(){
		m_dlvProgramForMSACheckPrefix="jMoleculesMSACheck"+m_numberMolfiles;
	}
	
	//this method splits a set of description graphs into modules of 
	//size m_moduleSize and returns a set of string sets, where the string
	//sets correspond to the modules and the strings to start concepts
	public ArrayList<Set<String>> splitIntoModules(){
		ArrayList<Set<String>> startConceptsModules=new ArrayList<Set<String>>();
		//convert the map with description graphs into an arraylist of start concepts
		ArrayList<String> startConcepts=new ArrayList<String>(m_descriptionGraphs.keySet());
		
		//compute the number of molecules
		int numberOfModules=(m_descriptionGraphs.size())/m_moduleSize;
		
		if (((m_descriptionGraphs.size())%m_moduleSize)!=0)
			numberOfModules++;
		
		//compute in each run one module
		for (int currentModule=0; currentModule < numberOfModules; currentModule++){
			Set<String> module=new HashSet<String>();
			int indexWithinModule=0;
			
			//construct module as far as start concepts arraylist is not empty
			//and module has size smaller than m_moduleSize
			while((indexWithinModule<m_moduleSize) && (!startConcepts.isEmpty())){
				module.add(startConcepts.remove(0));
				indexWithinModule++;
			}			
			startConceptsModules.add(module);		
		}		
		return startConceptsModules;
	}
	
	public ArrayList<Set<String>> getStartConceptsModules(){
		return m_startConceptsModules;		
	}
	
	//this method creates the two DLV program files 
	public void createDLVPrograms(){
		int dgsEncodedSoFar=m_moduleSize;
		ArrayList<String> dlvPrograms=assembleManyDLVPrograms();
		
		for (int i=0; i<dlvPrograms.size(); i++){
			String dlvProgram=dlvPrograms.get(i);
			String dlvProgramName=m_dlvProgramPrefix+"_"+dgsEncodedSoFar;
			createDLVProgramFile(dlvProgram, m_dlvFilesPath+"/"+dlvProgramName);
			dgsEncodedSoFar+=m_moduleSize;
			m_dlvProgramsNames.add(dlvProgramName);
		}
		
		//reset counter for DLV programs to do MSA check
		dgsEncodedSoFar=m_moduleSize;
		for (String dlvProgram:assembleManyDLVProgramsMSACheck()){
			String dlvProgramMSACheckName=m_dlvProgramForMSACheckPrefix+"_"+dgsEncodedSoFar;
			createDLVProgramFile(dlvProgram,m_dlvFilesPath+"/"+dlvProgramMSACheckName);
			dgsEncodedSoFar+=m_moduleSize;
			m_dlvProgramsMSACheckNames.add(dlvProgramMSACheckName);
		}  
	}
	
	public ArrayList<String> getDLVProgramsNames(){
		return m_dlvProgramsNames;
	}
	
	public Set<String> getDLVProgramsMSACheckNames(){
		return m_dlvProgramsMSACheckNames;
	}
	
	//this method returns an arraylist of DLV program strings
	//each DLV program is generated by a set of start concepts
	//found in m_startConceptsModules
	public ArrayList<String> assembleManyDLVPrograms(){
		ArrayList<String> dlvPrograms= new ArrayList<String>();
		
		/*for (Set<String> startConceptsModule:m_startConceptsModules){
			String dlvProgram=assembleOneDLVProgram(startConceptsModule);
			dlvPrograms.add(dlvProgram);
		}*/
		
		for (int i=0; i<m_startConceptsModules.size(); i++){
			Set<String> startConceptsModule=m_startConceptsModules.get(i);
			String dlvProgram=assembleOneDLVProgram(startConceptsModule);
			dlvPrograms.add(dlvProgram);
		}
		
		return dlvPrograms;
		
	}
	
	//same as assembleManyDLVPrograms()
	//here the programs are used to check acyclicity
	public Set<String> assembleManyDLVProgramsMSACheck(){
		Set<String> dlvPrograms= new LinkedHashSet<String>();
		
		for (Set<String> startConceptsModule:m_startConceptsModules){
			String dlvProgram=assembleOneDLVProgramMSACheck(startConceptsModule);
			dlvPrograms.add(dlvProgram);
		}
		
		return dlvPrograms;
		
	}
	
	//this method returns a DLV program that encodes the description graphs
	//whose start concepts are in the start concepts module
	public String assembleOneDLVProgram(Set<String> startConceptsModule){
		StringBuffer sb=new StringBuffer();
		DLVRulesGenerator dlvRulesGenerator = new DLVRulesGenerator();
		Set<DLVRule> dlvRules=new LinkedHashSet<DLVRule>();
		String CRLF=System.getProperty("line.separator");  
		
		for (String startConcept:startConceptsModule){
			dlvRules.addAll(dlvRulesGenerator.translateDGintoRules(m_descriptionGraphs.get(startConcept)));
		}
		
		for (DLVRule rule:dlvRules){
			sb.append(rule.getDLVSyntaxRule());
			sb.append(CRLF);
		}		

		sb.append(transcriptChemicalClassesRules());
		
		return sb.toString();
	}
	
	//same as assembleOneDLVProgram(..)
	//here the DLV program is used for checking acyclicity
	public String assembleOneDLVProgramMSACheck(Set<String> startConceptsModule){
		StringBuffer sb=new StringBuffer();
		DLVRulesGenerator dlvRulesGenerator = new DLVRulesGenerator();
		Set<DLVRule> dlvRules=new LinkedHashSet<DLVRule>();
		String CRLF=System.getProperty("line.separator");  
		
		for (String startConcept:startConceptsModule){
			dlvRules.addAll(dlvRulesGenerator.translateDGintoMSARules(m_descriptionGraphs.get(startConcept)));
		}
		
		dlvRules.addAll(dlvRulesGenerator.produceDescendantTransClosureRules());
		
		for (DLVRule rule:dlvRules){
			sb.append(rule.getDLVSyntaxRule());
			sb.append(CRLF);
		}		
		
		return sb.toString();
	}
	
	
	//this method creates a DLV program with given content and file name
	public void createDLVProgramFile(String content, String fileName){
		
		try{		
			//System.out.println("Creating file "+fileName+"...");
			File dlvProgram = new File(fileName);
			dlvProgram.createNewFile();
			FileWriter fStream = new FileWriter(dlvProgram);
			BufferedWriter outputLogicProgram = new BufferedWriter(fStream);
			outputLogicProgram.append(content);
			outputLogicProgram.close();
			//System.out.println("File "+fileName+" created.");
		}
		catch(Exception e){
			e.printStackTrace();
		}	
	}
	
	//this method returns a string with all the DLV rules
	//that encode chemical classes
	public String transcriptChemicalClassesRules(){
		File classRulesFile = new File(m_classRulesPath);
		return retrieveContent(classRulesFile);
	}
	
	//it returns the content of a file in the form of a string
	public String retrieveContent(File rulesfile){
		StringBuffer content=new StringBuffer();
        String CRLF=System.getProperty("line.separator");
        try {
			BufferedReader input=new BufferedReader(new FileReader(rulesfile)) ;
			String line="";
			while((line = input.readLine())!=null){
				content.append(line);
				content.append(CRLF);			
			}
		}
        catch (FileNotFoundException e) {
			e.printStackTrace();
		} 
        catch (IOException e) {
			e.printStackTrace();
		}
		return content.toString();
	}
}