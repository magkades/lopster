package main.java.uk.ac.ox.cs.krr.lopster;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Set;

/*This class handles operations that involve computation of the stable model for a dlv program.*/

public class ModelComputer {
	
	private String m_dlvFilesPath;
	private String m_dlvEnginePath;
	private long m_modelComputationTime;
	private long m_msaCheckTime;
	
	public ModelComputer(String dlvFilesPath,String dlvEnginePath){
		this.m_dlvFilesPath=dlvFilesPath;
		this.m_dlvEnginePath=dlvEnginePath;
	}

	//this method returns true only if all the programs 
	//represented by dlvProgramsForMSACheckNames are MSA
	public boolean areMSA(Set<String> dlvProgramsForMSACheckNames){
		boolean areMSA=true;
		
		for (String program:dlvProgramsForMSACheckNames)
			areMSA=areMSA & isMSA(program);
				
		return areMSA;
	}
	
	//this method tells whether the logic program with the name 
	//dlvProgramForMSACheckName is model-summarising acyclic 
	public boolean isMSA(String dlvProgramForMSACheckName){
		
		long start=System.currentTimeMillis();
		StableModel stableModel=computeDLVModel(dlvProgramForMSACheckName);
		Atom cycle=new Atom("cycle",new ArrayList<String>());
		long end=System.currentTimeMillis();
		m_msaCheckTime=end-start;
		return !stableModel.isEntailed(cycle);
	}

	//this method returns the time taken for the most recent
	//model computation
	public double getModelComputationTimeInSeconds(){
		return ((double)m_modelComputationTime)/1000;
	}
	
	//this method returns the time taken for the most recent
	//msa check
	public double getMSACheckTimeInSeconds(){
		return ((double)m_msaCheckTime)/1000;
	}
	
	//this method computes the dlv models of the dlv programs
	//whose names are stored in dlvProgramsNames and returns 
	//an arraylist that contains the stable models in the same order
	public ArrayList<StableModel> computeDLVModels(ArrayList<String> dlvProgramsNames){
		ArrayList<StableModel> stableModels=new ArrayList<StableModel>();
		
		for (int i=0; i<dlvProgramsNames.size(); i++)
			stableModels.add(computeDLVModel(dlvProgramsNames.get(i)));
					
		return stableModels;
	}
	
	//this method invokes the DLV engine that computes the stable model
	//of the specified DLV program and returns an object of the class 
	//StableModel that is constructed using the generated stable model
	public StableModel computeDLVModel(String dlvProgramName){
		String outputString;
		String computedModelFileName=m_dlvFilesPath+"/"+dlvProgramName+"Model";
		String CRLF=System.getProperty("line.separator");
		
		try {
			String commandLine="./"+m_dlvEnginePath +" -silent "+dlvProgramName;
			String[] commandArguments={""};
			File workingDirectory = new File(m_dlvFilesPath+"/");
			long start=System.currentTimeMillis();
			Process process = Runtime.getRuntime().exec(commandLine,commandArguments,workingDirectory);
			
			BufferedReader bufferedReader= new BufferedReader(new InputStreamReader(process.getInputStream()));
			//System.out.println("Creating file "+computedModelFileName+"...");
			File model = new File(computedModelFileName);
			model.createNewFile();
			FileWriter fileWriter = new FileWriter(model);
			BufferedWriter modelBufferedWriter = new BufferedWriter(fileWriter);
			
			while ((outputString = bufferedReader.readLine()) != null) {
				modelBufferedWriter.append(outputString);
				modelBufferedWriter.append(CRLF);
			}		
			
			modelBufferedWriter.close();
			long end=System.currentTimeMillis();
			m_modelComputationTime=end-start;			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new StableModel(computedModelFileName);
	}
}
