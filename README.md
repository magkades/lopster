LoPStER
=======

A **Lo**gic **P**rogramming for **St**ructured **E**ntities **R**easoner.  

The current prototype software classifies molecular descriptions extracted from the [ChEBI ontology](http://www.ebi.ac.uk/chebi/) using the [DLV deductive database system](http://www.dlvsystem.com/dlvsystem/index.php/Home). 

ORGANISATION OF THE FOLDER
---------------------------

* A folder `LoPStER/src/main/resources/molfiles` which contains the descriptions of 500 molecular entities in molfile format as extracted from ChEBI.
* A folder `LoPStER/src/main/resources/chemrules` which contains two files with LP rules encoding chemical classes: (i) chemRulesNoCyclic with rules describing 51 chemical classes and (ii) chemRulesNoCyclic with rules describing 48 of the said chemical classes above, excluding the cyclicity-related classes.
* A folder `LoPStER/src/main/resources/output/taxonomy` where an excel file  with the superclasses of the abovementioned molecules will be stored after running LoPStER.
* A folder `LoPStER/src/main/java/uk/ac/ox/cs/krr/lobster` that contains the java code of the prototype
* A folder `LoPStER/src/main/resources/dlv` where the DLV engine should be placed
* A folder `LoPStER/libraries` where the cdk and poi .jar files should be placed.

HOW TO REPRODUCE THE EXPERIMENTS THROUGH ECLIPSE
------------------------------------------------

1. Create a java project by importing the folder `LoPStER/`.
2. Add the cdk .jar files to the library of the said project:
	(i) Download the cdk and poi .jar files from http://sourceforge.net/projects/cdk/files/cdk/ and http://poi.apache.org/download.html.
	(ii) Save the files under "LoPStER/libraries".
	(iii) Right click on the name of the java project -> Properties -> Java Build Path -> Add external JARS... and select the two .jar files from `LoPStER/libraries`.
2.Add the dlv file:
	(i) Download the DLV engine that suits your machine from http://www.dlvsystem.com/dlvsystem/index.php/DLV#Download.
	(ii) Place it under the folder `LoPStER/src/main/resources/dlv`.
	(iii)Either change the name of the DLV engine file to `dlvNoLimit` or change the name of the variable `m_dlvEnginePath` to the name of the DLV engine file. (for mac computers the .bin extension of the DLV engine file needs to be preserved)
	(iv)run the command `chmod a+rx dlv_engine_file_name` to make the DLV engine file executable.
3.Right click on Tester.java -> Run Java application.
4.An excel file under the folder `LoPStER/src/main/resources/output/taxonomy` is created with the subclass/superclass pairs.

For queries, please contact `magkades@gmail.com`.



