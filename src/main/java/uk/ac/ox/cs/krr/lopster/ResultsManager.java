package main.java.uk.ac.ox.cs.krr.lopster;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.poi.hssf.usermodel.HSSFRow;

/*This class processes the stable model produced by DLV for a given logic program in order to
 * extract the subsumption relations from it and transcript them along with the timings in an
 * excel file. All results  are stored in a single excel file.*/

public class ResultsManager {
	
	private String m_excelFileName;
	private ExcelWriter m_excelWriter;
	private int m_excelNumberOfColumns;
	private HSSFRow rowNumberOfDgs;
	private HSSFRow rowCreationTime;
	private HSSFRow rowMSATime;
	private HSSFRow rowDLVTime;
	private HSSFRow rowTaxonomyTime;
	private HSSFRow rowTotalTime;
	private HSSFRow rowSubSuperClass;
	private Map<Integer,HSSFRow> rowMap;
	
	
	public ResultsManager(String excelFileName, int excelNumberOfColumns){
		this.m_excelFileName=excelFileName;
		this.m_excelNumberOfColumns=excelNumberOfColumns;
		this.m_excelWriter=new ExcelWriter();
		this.rowNumberOfDgs = m_excelWriter.newRow(0,m_excelNumberOfColumns);
		this.rowCreationTime = m_excelWriter.newRow(1,m_excelNumberOfColumns);
		this.rowMSATime= m_excelWriter.newRow(2,m_excelNumberOfColumns);
		this.rowDLVTime =  m_excelWriter.newRow(3,m_excelNumberOfColumns);
		this.rowTaxonomyTime =  m_excelWriter.newRow(4,m_excelNumberOfColumns);
		this.rowTotalTime = m_excelWriter.newRow(5,m_excelNumberOfColumns);
		this.rowSubSuperClass = m_excelWriter.newRow(7,m_excelNumberOfColumns);
		this.rowMap = new HashMap<Integer, HSSFRow>();
	}
	
	//this method builds a hierarchy based on a stable model and on a set of start concepts
	//that form the subclasses of the hierarchy
	public Map<String,Set<String>> classifyBulkMode(StableModel stableModel,Set<String> startConcepts){
		Map<String,Set<String>> taxonomy=new HashMap<String, Set<String>>();
		
		for (String startConcept:startConcepts)
			taxonomy.put(startConcept, stableModel.retrieveSuperClasses(startConcept));
		
		return taxonomy;
	}
	
	//this method builds a hierarchy based on the arraylist of stable models and start 
	//concepts set, where the stable models contain the superclasses of start concepts
	//according to the order of the arraylist
	public Map<String,Set<String>> classifyApartMode(ArrayList<StableModel> stableModels,
			ArrayList<Set<String>> startConceptsModules){
		Map<String,Set<String>> taxonomy=new HashMap<String, Set<String>>();
		int numModules=startConceptsModules.size();
		
		for (int i=0; i<numModules; i++){
			Set<String> startConcepts=startConceptsModules.get(i);
			StableModel stableModel=stableModels.get(i);
			for (String startConcept:startConcepts){
				taxonomy.put(startConcept, stableModel.retrieveSuperClasses(startConcept));
			}
		}		
		return taxonomy;
	}
	
	//this method writes time measurements in the excel file
	//the columnIndex depends on the number of molecules currently tested
	//and is increased by 3 every time that the number increases
	public void transcriptTimingsInExcel(int numberOfDgs,double creationTime,double msaTime,double dlvTime,
			double taxonomyTime,double totalTime,int columnIndex){
		m_excelWriter.writeCell(rowNumberOfDgs, columnIndex, "Molecules:");
		m_excelWriter.writeCell(rowNumberOfDgs, columnIndex+1, numberOfDgs);
		
		m_excelWriter.writeCell(rowCreationTime, columnIndex, "DLV Programs creation time:");
		m_excelWriter.writeCell(rowCreationTime, columnIndex+1, creationTime);
		
		m_excelWriter.writeCell(rowMSATime, columnIndex, "MSA Check time:");
		m_excelWriter.writeCell(rowMSATime, columnIndex+1, msaTime);
		
		m_excelWriter.writeCell(rowDLVTime, columnIndex, "DLV time:");
		m_excelWriter.writeCell(rowDLVTime, columnIndex+1, dlvTime);
		
		m_excelWriter.writeCell(rowTaxonomyTime, columnIndex, "Taxonomy time:");
		m_excelWriter.writeCell(rowTaxonomyTime, columnIndex+1, taxonomyTime);
		
		m_excelWriter.writeCell(rowTotalTime, columnIndex, "Total time:");
		m_excelWriter.writeCell(rowTotalTime, columnIndex+1, totalTime);		
	}
	
	//this method writes the taxonomy in the excel file
	//there are two columns one for subclasses and another for superclasses
	//the columnIndex depends on the number of molecules currently tested
	//and is increased by 3 every time that the number increases
	public void transcriptTaxonomyInExcel(int columnIndex,Map<String,Set<String>> taxonomy){
		m_excelWriter.writeCell(rowSubSuperClass, columnIndex, "Subclass");
		m_excelWriter.writeCell(rowSubSuperClass, columnIndex+1, "Superclass");
		
		int rowIndex=8;
		for (String subClass:taxonomy.keySet())
			for (String superClass:taxonomy.get(subClass)){
				Integer keyRowIndex=new Integer(rowIndex);
				if (rowMap.containsKey(keyRowIndex)){
					HSSFRow row = rowMap.get(keyRowIndex);
					m_excelWriter.writeCell(row, columnIndex, subClass);
					m_excelWriter.writeCell(row, columnIndex+1, superClass);
				}
				else{
					HSSFRow row = m_excelWriter.newRow(rowIndex,m_excelNumberOfColumns);
					rowMap.put(keyRowIndex, row);
					m_excelWriter.writeCell(row, columnIndex, subClass);
					m_excelWriter.writeCell(row, columnIndex+1, superClass);
				}				
				rowIndex++;
			}
	}
	
	//this method transcripts all results (timings and subsumptions) in the excel file	
	public void transcriptResultsInExcel(int numberOfDgs,double creationTime,double msaTime,double dlvTime,double taxonomyTime,
			double totalTime,int columnIndex,Map<String,Set<String>> taxonomy){
		transcriptTimingsInExcel(numberOfDgs,creationTime, msaTime,dlvTime,taxonomyTime,totalTime,columnIndex);
		transcriptTaxonomyInExcel(columnIndex,taxonomy);		
	}
	
	//this method flushes all data that m_excelWriter contains int the excel file
	//and closes it
	public void flushToExcelFile(int excelNumberOfColumns){
		m_excelWriter.adjustAllColumns(excelNumberOfColumns);
		m_excelWriter.writeToExcelFile(m_excelFileName);
	}

}
