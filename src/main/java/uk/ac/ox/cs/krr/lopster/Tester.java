package main.java.uk.ac.ox.cs.krr.lopster;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

/*This is the  class of the prototype that  calls the main method. The functionality of the system can be summed up
 * as following: (i) it reads a number of molfiles whose location is specified by the variable m_inputFilesPath
 * (ii) it constructs one description graph java object for each file
 * (iii) each description graph java object is converted into a set of DLV rules
 * (iv) the DLV rules for all description graphs and the DLV rules that encode chemical classes are 
 * stored in a logic program whose location is specified by the variable m_dlvFilesPath
 * (v) the logic program is checked for MSA
 * (v) if it is MSA, then the DLV engine is invoked in order to compute the stable model of the program 
 * (vi)a set of subsumptions is extracted from the stable model and stored in an excel file along with the time
 *  measures
 *  Note that the above sequence of actions is repeated for different numbers of molecules 
 *  m_batchSize, 2*m_batchSize,..., n*m_batchSize where n=m_numberMolfiles/m_batchSize */

public class Tester { 

	public static String m_inputFilesPath="main/resources/input/molfiles";
	public static String m_classRulesPath="main/resources/input/chemRulesNoCyclic";
	public static String m_dlvFilesPath="dlv";
	public static String m_dlvEnginePath="dlvNoLimit";
	public static String m_outputFilesPath="mail/resources/output/taxonomy";
	public static int m_numberMolecules=500;
	public static int m_batchSize=50;
	public static int m_excelNumberOfColumns=(m_numberMolecules/m_batchSize)*3;
	public static int m_moduleSize=50;
	public static ClassificationMode classificationMode = ClassificationMode.APART;
	
	protected enum ClassificationMode { BULK, APART }
	  
	public static void main(String[] args) {
		
		//check whether the step for molecules increase during evaluation is 
		//greater than overall number of molecules
		if (m_batchSize > m_numberMolecules){
			System.out.println("Evaluation cannot proceed as batch size is greater than number of molfiles.");
			System.exit(0);
		}
		
		//check whether the module size is greater than the step for molecules
		//increase during evaluation
		if ((m_moduleSize > m_batchSize) && (ClassificationMode.APART.equals(classificationMode))){
			System.out.println("Evaluation cannot proceed as module size is greater than batch size.");
			System.exit(0);
		}
		 
		//this is the index of column for the results excel file
		int columnIndex=0;		
		ResultsManager resultsManager = new ResultsManager(m_outputFilesPath,m_excelNumberOfColumns);
		for (int sizeCurrentlyTested=m_batchSize; sizeCurrentlyTested<=m_numberMolecules; sizeCurrentlyTested+=m_batchSize){
			
			//if the classification mode is bulk set module size as the size currently tested
			//so that only one module is generated
			if (ClassificationMode.BULK.equals(classificationMode))
				m_moduleSize=sizeCurrentlyTested;
			
			//generate the description graphs from molfiles
  			long beforeDGGeneration=System.currentTimeMillis();
			DGGenerator dgGenerator = new DGGenerator(m_inputFilesPath);
			Map<String,DescriptionGraph> descriptionGraphs=dgGenerator.assembleDGMap(sizeCurrentlyTested);
			
			//generate the DLV programs
			DLVProgramManager dlvProgramManager = new DLVProgramManager(m_moduleSize,m_dlvFilesPath,m_classRulesPath,
					sizeCurrentlyTested,descriptionGraphs);
			dlvProgramManager.createDLVPrograms();
			long afterDLVProgramsCreation=System.currentTimeMillis();
			double creationTime = ((double)(afterDLVProgramsCreation-beforeDGGeneration))/1000;
			
			//check for model summarising acyclicity
			ModelComputer modelComputer = new ModelComputer(m_dlvFilesPath,m_dlvEnginePath);
			boolean areMSA=modelComputer.areMSA(dlvProgramManager.getDLVProgramsMSACheckNames());
			long afterMSACheck=System.currentTimeMillis();
			double msaTime=((double)(afterMSACheck-afterDLVProgramsCreation))/1000;
			
			if (areMSA){
				//compute the stable models
				ArrayList<StableModel> stableModels=modelComputer.computeDLVModels(dlvProgramManager.getDLVProgramsNames());
				ArrayList<Set<String>> startConceptsModules=dlvProgramManager.getStartConceptsModules();
				//extract the taxonomy from the stable models
				long beforeTaxonomy=System.currentTimeMillis();
				Map<String,Set<String>> taxonomy=resultsManager.classifyApartMode(stableModels,startConceptsModules);
				long afterTaxonomy=System.currentTimeMillis();
				//measure timings of different tasks
				double dlvTime=((double)(beforeTaxonomy-afterMSACheck))/1000;
				double taxonomyTime=((double)(afterTaxonomy-beforeTaxonomy))/1000;
				double totalTime=((double)(afterTaxonomy-beforeDGGeneration))/1000;
				//write the results in an excel file
				resultsManager.transcriptResultsInExcel(sizeCurrentlyTested,creationTime,msaTime,dlvTime,taxonomyTime,
						totalTime,columnIndex,taxonomy);
				columnIndex=columnIndex+3;
				resultsManager.flushToExcelFile(m_excelNumberOfColumns);				
			}
			else 
				System.out.println("Generation of stable model might not terminate.");
			System.out.println("Classification of ontology with size "+sizeCurrentlyTested+" completed.");
		}
		
	}

}
