package main.java.uk.ac.ox.cs.krr.lopster;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.openscience.cdk.ChemFile;
import org.openscience.cdk.exception.CDKException;
import org.openscience.cdk.interfaces.IAtom;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IBond;
import org.openscience.cdk.interfaces.IChemModel;
import org.openscience.cdk.io.MDLV2000Reader;
import org.openscience.cdk.tools.manipulator.AtomContainerManipulator;

/*This class builds a set of description graph class objects based on the 
 * molfiles that are located in m_inputFilesPath. The size of the set is 
 * parameterisable.*/

public class DGGenerator {

	private String m_inputFilesPath;
	private Set<String> m_startConcepts=new LinkedHashSet<String>();
	
	public DGGenerator(String inputFilesPath){
		this.m_inputFilesPath=inputFilesPath;
	}	
	
	//this method produces a map of strings and  description graph 
	//objects by processing the molfiles that are located in m_inputFilesPath
	//the size of the map is specified by sizeOfDGSet
	public Map<String,DescriptionGraph> assembleDGMap(int sizeOfDGSet){
		Map<String,DescriptionGraph> descriptionGraphs=new HashMap<String,DescriptionGraph>();
		File molfilesFolder = new File(m_inputFilesPath);
		File[] list = molfilesFolder.listFiles();
		ArrayList<File> molFilesToConvert=new ArrayList<File>();
		
		//Filter mol files
		for (File file : list) {
			if ((file.getName()).endsWith("mol")) 
					molFilesToConvert.add(file);									
		}
		
		//Convert mol files into objects of the DescriptionGraph class
		//first discard all molecules that have an R atom
		//if there are not enough molfiles an index out of bounds exception
		//is thrown and the loop is interrupted
		try{
			int dgsIndex=0;
			int molfilesIndex=0;
			while(dgsIndex < sizeOfDGSet) {
				File molfile=molFilesToConvert.get(molfilesIndex);
				DescriptionGraph dg=buildDescriptionGraph(molfile);
				if (isRadicalFree(dg)){
					descriptionGraphs.put(dg.getStartConcept(), dg);
					dgsIndex++;
				}
				molfilesIndex++;
			}
			
		}
		catch (IndexOutOfBoundsException e){
			System.out.println("The number of available radical-free molfiles is smaller than the" +
					" number of molecules to be tested.");
			System.exit(1);
		}
		
		return descriptionGraphs;
	}
	
	//this method examines the sizes that are in the specified file path
	//and prints out metrics about the molecules
	//e.g. CHEBI ID: chebi_12345 NO ATOMS: 10 NO BONDS: 10
	public void inspectSize(int sizeCurrentlyTested){
		File molfilesFolder = new File(m_inputFilesPath);
		File[] list = molfilesFolder.listFiles();
		ArrayList<File> molFilesToConvert=new ArrayList<File>();
		
		//Filter mol files
		for (File file : list) {
			if ((file.getName()).endsWith("mol")) 
					molFilesToConvert.add(file);									
		}
		
		//For each mol file retrieve chebi id, number of atoms
		//and number of bonds
		try{
			int molfilesIndex=0;
			double totalNoOfAtoms=0;
			double totalNoOfBonds=0;
			/*ArrayList<String> size1to20=new ArrayList<String>();
			ArrayList<String> size21to40=new ArrayList<String>();
			ArrayList<String> size41to60=new ArrayList<String>();
			ArrayList<String> size61to80=new ArrayList<String>();
			ArrayList<String> size81to100=new ArrayList<String>();
			ArrayList<String> size101to120=new ArrayList<String>();
			ArrayList<String> sizeabove120=new ArrayList<String>();*/
			while(molfilesIndex < sizeCurrentlyTested) {
				File molfile=molFilesToConvert.get(molfilesIndex);
				String molfileContent=retrieveContent(molfile);
				/*String fileName=molfile.getName();
				String moleculeName=retrieveChEBIID(molfile);*/
				MDLV2000Reader reader = new MDLV2000Reader(new StringReader(molfileContent)) ;
				ChemFile chemFile = (ChemFile) reader.read(new ChemFile());
				IChemModel model = chemFile.getChemSequence(0).getChemModel(0);
				IAtomContainer molContent = model.getMoleculeSet().getAtomContainer(0);
				//AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molContent);
				int numberOfAtoms=molContent.getAtomCount();
				totalNoOfAtoms+=numberOfAtoms;
				int numberOfBonds=molContent.getBondCount();
				totalNoOfBonds+=numberOfBonds;
				//System.out.println("CHEBI ID:"+moleculeName+" NO ATOMS:"+numberOfAtoms +" NO BONDS:"+numberOfBonds);
				/*String[] commandArguments={""};
				File workingDirectory = new File(m_inputFilesPath+"/");
				if (numberOfAtoms <= 20){
					//size1to20.add(retrieveChEBIID(fileName));
					String commandLine="cp "+fileName +" size1to20/";
					Runtime.getRuntime().exec(commandLine,commandArguments,workingDirectory);
				}					
				else if ((numberOfAtoms >= 21) && (numberOfAtoms <= 40)){
					String commandLine="cp "+fileName +" size21to40/";
					Runtime.getRuntime().exec(commandLine,commandArguments,workingDirectory);
				}
				else if ((numberOfAtoms >= 41) && (numberOfAtoms <= 60)){
					String commandLine="cp "+fileName +" size41to60/";
					Runtime.getRuntime().exec(commandLine,commandArguments,workingDirectory);
				}
				else if ((numberOfAtoms >= 61) && (numberOfAtoms <= 80)){
					String commandLine="cp "+fileName +" size61to80/";
					Runtime.getRuntime().exec(commandLine,commandArguments,workingDirectory);
				}
				else if ((numberOfAtoms >= 81) && (numberOfAtoms <= 100)){
					String commandLine="cp "+fileName +" size81to100/";
					Runtime.getRuntime().exec(commandLine,commandArguments,workingDirectory);
				}
				else if ((numberOfAtoms >= 101) && (numberOfAtoms <= 120)){
					String commandLine="cp "+fileName +" size101to120/";
					Runtime.getRuntime().exec(commandLine,commandArguments,workingDirectory);
				}
				else if (numberOfAtoms >= 120){
					String commandLine="cp "+fileName +" sizeabove120/";
					Runtime.getRuntime().exec(commandLine,commandArguments,workingDirectory);
				}*/
				molfilesIndex++;
			}
			double averageAtoms=totalNoOfAtoms/sizeCurrentlyTested;
			double averageBonds=totalNoOfBonds/sizeCurrentlyTested;
			System.out.println("Number of atoms on averate for size "+sizeCurrentlyTested+ " is "+averageAtoms);
			System.out.println("Number of bonds on averate for size "+sizeCurrentlyTested+ " is "+averageBonds);
			/*System.out.println("Number of atoms between 1  and 20 "+size1to20.size());
			for (String s:size1to20)
				System.out.println("Chebi id: "+s);
			System.out.println("Number of atoms between 21  and 40 "+size21to40.size());
			for (String s:size21to40)
				System.out.println("Chebi id: "+s);
			System.out.println("Number of atoms between 41  and 60 "+size41to60.size());
			for (String s:size41to60)
				System.out.println("Chebi id: "+s);
			System.out.println("Number of atoms between 61  and 80 "+size61to80.size());
			for (String s:size61to80)
				System.out.println("Chebi id: "+s);
			System.out.println("Number of atoms between 81  and 100 "+size81to100.size());
			for (String s:size81to100)
				System.out.println("Chebi id: "+s);
			System.out.println("Number of atoms between 101 and 120 "+size101to120.size());
			for (String s:size101to120)
				System.out.println("Chebi id: "+s);*/
		}
		catch (IndexOutOfBoundsException e){
			e.printStackTrace();
		}
		catch (NullPointerException npe) {
			npe.printStackTrace();
		} catch (CDKException e) {
			e.printStackTrace();
		}/*catch (IOException e) {
			e.printStackTrace();
		}*/
		
	}
	
	public Set<String> getStartConcepts(){
		return this.m_startConcepts;
	}
	
	//given a molfile it produces an object of the
	//description graph class
	public DescriptionGraph buildDescriptionGraph(File molfile){
		String molfileContent=retrieveContent(molfile);
		Set<Node> nodes=new LinkedHashSet<Node>();
		Set<Edge> edges=new LinkedHashSet<Edge>();
		String moleculeName="";
		try{
			//convert into lower case to fit DLV syntax
			moleculeName=retrieveChEBIID(molfile).toLowerCase();
			//System.out.println("Molecule name:"+moleculeName);
			m_startConcepts.add(moleculeName);
			MDLV2000Reader reader = new MDLV2000Reader(new StringReader(molfileContent)) ;
			ChemFile chemFile = (ChemFile) reader.read(new ChemFile());
			IChemModel model = chemFile.getChemSequence(0).getChemModel(0);
			IAtomContainer molContent = model.getMoleculeSet().getAtomContainer(0);
			AtomContainerManipulator.percieveAtomTypesAndConfigureAtoms(molContent);
			nodes=retrieveNodes(molContent,moleculeName);
			edges=retrieveEdges(molContent,moleculeName);		
		} catch (NullPointerException npe) {
			npe.printStackTrace();
		} catch (CDKException e) {
			e.printStackTrace();
		}	
		return new DescriptionGraph(nodes,edges,moleculeName);
	}
	
	//detects whether the graph contains a node labeled with r
	public boolean isRadicalFree(DescriptionGraph dg){
		Set<Node> nodes=dg.getNodes();
		
		for (Node n:nodes){
			Set<String> labels=n.getLabel();
			for (String label:labels)
				if (label.equals("r"))
					return false;
		}		
		return true;
	}
	
	//it returns the content of a molfile (in particular) in the form of a string
	public String retrieveContent(File molfile){
		StringBuffer content=new StringBuffer();
        String CRLF=System.getProperty("line.separator");
        try {
			BufferedReader input=new BufferedReader(new FileReader(molfile)) ;
			String line="";
			while(!(line.endsWith("END"))){
				line=input.readLine();
				content.append(line);
				if (!(line.endsWith("END"))){
					content.append(CRLF);
				}
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
	
	//it returns a set of objects in the class Node
	public Set<Node> retrieveNodes(IAtomContainer molContent,String moleculeName){
		Set<Node> nodes=new LinkedHashSet<Node>();
		try{
			Set<String> labelZeroNode=new LinkedHashSet<String>();
			//labelZeroNode.add(moleculeName);
			labelZeroNode.add("molecule");
			nodes.add(new Node(new Integer(0),labelZeroNode));
			int numberOfAtoms=molContent.getAtomCount();
			for (int i=0; i<numberOfAtoms; i++){
				int nodeIndex=i+1;
				Set<String> label=new LinkedHashSet<String>();
				label.add(molContent.getAtom(i).getSymbol().toLowerCase());
				Integer dcharge=molContent.getAtom(i).getFormalCharge();
				if (dcharge!=null){
					int charge=molContent.getAtom(i).getFormalCharge().intValue();
					switch (charge) {
					case 1:  label.add("plus1");
	                     	break;
					case 2:  label.add("plus2");
	                     	break;
					case 3:  label.add("plus3");
	                     	break;
					case -1:  label.add("minus1");
	                     	break;
					case -2:  label.add("minus2");
	                     	break;
					case -3:  label.add("minus3");
	                     	break;
					default: 
							break;
					}
				}
				nodes.add(new Node(new Integer(nodeIndex),label));
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}		
		return nodes;		
	} 
	
	//it returns a set of objects in the class Edge
	public Set<Edge> retrieveEdges(IAtomContainer molContent,String moleculeName){
		Set<Edge> edges=new LinkedHashSet<Edge>();
		try {
			//keep the indices of the atoms from molContent
			Map<IAtom,Integer> atomIndexMap = new HashMap<IAtom, Integer>();
			int numberOfAtoms=molContent.getAtomCount();
			for (int i=0; i<numberOfAtoms; i++){
				atomIndexMap.put(molContent.getAtom(i), new Integer(i+1));
			}
			Integer rootNode=new Integer(0);
			//first add the hasAtom edges
			Set<String> labelHasAtom=new LinkedHashSet<String>();
			labelHasAtom.add("hasAtom");
			for (int i=0; i<numberOfAtoms; i++){
				int nodeIndex=i+1;
				Edge edge=new Edge(rootNode,new Integer(nodeIndex),labelHasAtom);
				edges.add(edge);
			}
			//then add the bond edges
			int numberOfBonds=molContent.getBondCount();
			for (int i=0; i<numberOfBonds; i++){
				IBond bond=molContent.getBond(i);
				Integer fromNode=atomIndexMap.get(bond.getAtom(0));
				Integer toNode=atomIndexMap.get(bond.getAtom(1));
				String bondOrder=bond.getOrder().toString().toLowerCase();
				Set<String> label=new LinkedHashSet<String>();
				label.add(bondOrder);
				Edge edgeForward=new Edge(fromNode,toNode,label);
				Edge edgeBackward=new Edge(toNode,fromNode,label);
				edges.add(edgeForward);
				edges.add(edgeBackward);
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}	
		return edges;
		
	}
	
	//it returns the chebi ID, e.g. ChEBI_12345
	public String retrieveChEBIID(File molfile){
		return molfile.getName().replaceFirst("\\.mol", "");
	}
}
