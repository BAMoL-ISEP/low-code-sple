# Towards Supporting SPL Engineering in Low-Code Platforms using a DSL Approach

This repository contains all the resources and artifacts of the paper entitled **Towards Supporting SPL Engineering in Low-Code Platforms using a DSL Approach** submitted in the GPCE 2021 - 20th International Conference on Generative Programming: Concepts & Experiences by the authors Alexandre Bragança, Isabel Azevedo, Nuno Bettencourt, Carlos Morais, Diogo Teixeira, and David Caetano. 

**This corresponds to the case study presented and discussed in the paper.** The implementation is based on Eclipse, Xtext, Maven and ATL. 

The goal of this project is to explore supporting software product lines (SPL) in low code application platforms (LCAP) using domain-specific languages (DSL). 

The proposed solution is based on an integrated developmrnt environment (IDE) - that we call SPL IDE - that supports the development of the SPL in a way that is orthogonal to the LCAP. In fact, the only requirement is that the LCAP has some support for importing and exporting application models (e.g., using JSON files).
 
This project is a part/component of the BAMoL research project. *Project BAMoL Low-Code Platform and the consortium BAMoL – LCP, co-financed by Fundo Europeu de Desenvolvimento Regional (FEDER), Programa Operacional Competividade e Internacionalização and Portugal2020 (POCI-01-0247-FEDER-39661).*
 
Please refer to the paper **Towards Supporting SPL Engineering in Low-Code Platforms using a DSL Approach** for further details about the approach and solution.

*This repository contains only snapshot of the project.*

![](process-lcap-splide.png)

# Contacts

This project is developed by a team from the Institute of Engineering of Porto – Polytechnic of Porto (ISEP/IPP), Portugal.

For further information please contact:
 - Alexandre Braganca (ATB@isep.ipp.pt)
 - Isabel Azevedo (IFP@isep.ipp.pt)
 - Nuno Bettencourt (NMB@isep.ipp.pt)

# Requirements

  OpenJDK 1.8
  
  Maven

  Eclipse (tested with version 2021-03)    

## Notes

- In Maven, the **-o** option is used so that maven does not always fetch info from the repositories 
- We recommend that all the maven commands should be executed for the first time wothout the **-o** option, but after that, append the **-o** option to all maven commands so that the execution is faster 
  
# Projects and Usage

The repository contains several Eclipse/Maven projects. Projects from 1 to 6 are Eclipse plugins. Together they provide the functionality that *transforms* Eclipse into an SPL IDE.

Projects 1 to 6 can be build from a terminal or console using Maven. They also can be opened from Eclipse. 

Project 7 is a testing and demonstration project that should be used with an Eclipse instance that has all the plugins from projects 1 to 6 loaded and activated.

These artifacts are all based on the OMNIA low-code plataform (see: https://omnialowcode.com).  

In these artifacts when you see **bamol** we are refering to the name used for the produced DSL and also for this case study (and project).

**1) pt.bamol.vmodel**
	
 * this is the project that contains the **variability metamodel** used in this example (emf project)
 * it does not depend on any other project
 * to open in Eclipse use "File"->"Import..."->"Existing Projects into Workspace"
 * file pt.bamol.vmodel/model/vmodel.ecore contains the variability metamodel
 * to build:
 
	mvn clean install  

**2) pt.bamol.vmodel.vmodeldsl.parent**
	
 * this is the project that contains the **variability DSL** (xtext project). 
 * it depends on the vmodel project (for the variability metamodel)
 * to open in Eclipse use "File"->"Import..."->"Existing Maven Projects"
 * file pt.bamol.vmodel.vmodeldsl.parent/pt.bamol.vmodel.vmodeldsl/src/pt/bamol/vmodel/vmodeldsl/VModelDsl.xtext contains the grammar for the variability dsl 
 * to build (execute in the **parent**):
 
	mvn clean install  

**3) pt.bamol.json.parent**
	
 * this is the project that contains the **JSON DSL** (xtext project). 
 * it does not depend on any other project
 * to open in Eclipse use "File"->"Import..."->"Existing Maven Projects" 
 * file pt.bamol.json.parent/pt.bamol.json/src/pt/bamol/json/Json.xtext contains the grammar for the JSON dsl  
 * file pt.bamol.json.parent/pt.bamol.json/model/generated/Json.ecore contains the JSON metamodel generated from the JSON grammar  
 * to build (execute in the **parent**):
 
	mvn clean install  
	
**4) pt.bamol.atl.json2ecore**
	
 * this is a project that contains **model transformations** (ATL project and also EMF project, since it produces ecore metamodels)
 * this project is used to generate (deduce) metamodels from models. It uses json model as input and produces ecore metamodel as ouput
 * it depends on the projects: **json**; **vmodel**
 * to open in Eclipse use "File"->"Import..."->"Existing Maven Projects"  
 * to build:
 
	mvn clean install  
	
 * after building, execute the transformations
 * to execute the transformation (pt.bamol.atl.json2ecore/transformations/json2ecore.atl) that takes as input the json files of OMNIA and **produces a first metamodel of OMNIA** (pt.bamol.atl.json2ecore/instances/bamol-ecore.ecore):
 
	mvn exec:java@json2ecore
	
 * the resulting metamodel has the following identification:

	name <- 'bamolM',
	nsURI <- 'http://bamol.pt/bamolM',
	nsPrefix <- 'bamolM'
	
 * the process is able to produce a metamodel, but it is incomplete
 * a **new transformation** can be used to provide the missing information (pt.bamol.atl.json2ecore/transformations/ecoreCustomization.atl). For instance, this will also add references to the variability metamodel so that the elements of the generated Bamol metamodel can be annotated with variability information. This will **produce the final metamodel of OMNIA** (pt.bamol.atl.json2ecore/instances/bamol-ecore-customization.ecore):
 * to execute this new transformation use:
 
	mvn exec:java@ecoreCustomization
	
 * the resulting metamodel has the following identification:

	name <- 'bamolM2',
	nsURI <- 'http://bamol.pt/bamolM2',
	nsPrefix <- 'bamolM2'
 	 	
 * **NOTE:** this transformation produces an intermediate metamodel that is 'invalid'. This is not a problem, since this metamodel is only temporary, to support the process
 * the default resulting metamodel name is **bamol-ecore-customization.ecore**.
 * It is necessary to open this project in Eclipse to generate a genmodel and then generate the code for this final metamodel (at the moment this activity is not automated). After that the following command should be executed again:

	mvn clean install  

 
**5) pt.bamol.bamolm2.parent** 	

 * this is a project that contains a **DSL based in the previous bamol metamodel** (xtext project). This is the generated DSL for the LCAP (in this case OMNIA).
 * this project will depend on the projects: **json2ecore**; **vmodel**
 * to open in Eclipse use "File"->"Import..."->"Existing Maven Projects"  
 * this project can be generated by using the Eclipse wizard Xtext  "Xtext Project From Existing Ecore Models" and selecting the previous generated genmodel (bamol-ecore-customization.genmodel). Select the element **Root** as entry rule.
 * normally this project already exists and, therefore, is simpler to use the Xtext wizard to generate a new *dummy* project based on the previous metamodel and after that copy the contents of the xtext file into the existing file **pt.bamol.bamolm2.parent/pt.bamol.bamolm2/src/pt/bamol/banolm2/BamolM2.xtext**. You may them delete the *dummy* project.
 * **Note:** If the grammar contains the following rule:
 	
		EBigDecimal returns ecore::EBigDecimal:
			INT? '.' INT;
	
	replace it with:

		EBigDecimal returns ecore::EBigDecimal:
			'-'? (INT? '.')? INT (('E'|'e') '-'? INT)?;

 * You may also need to review some aspects of the generated grammar. For instance, the rules for the variability annotations (i.e., 'inc') should be the first in their groups. For example:

		Root returns Root:
		{Root}
		'Root'
		'{'
			('inc' '(' inc+=[vmodel::Feature|EString] ( "," inc+=[vmodel::Feature|EString])* ')' )?
			('models' '{' models+=Models ( "," models+=Models)* '}' )?
		'}';

 	instead of 

		Root returns Root:
		{Root}
		'Root'
		'{'
			('models' '{' models+=Models ( "," models+=Models)* '}' )?
			('inc' '(' inc+=[vmodel::Feature|EString] ( "," inc+=[vmodel::Feature|EString])* ')' )?
		'}';

	We stronly suggest to apply this change to all the rules that include variability annotations.

 * in Eclipse, right click the xtext file and select "Run As"->"Generate Xtext Artifacts".
 * after that, execute in a terminal, in the **parent** folder:	
 
	mvn clean install  
	
**6) pt.bamol.atl.configuration** 

 * this is a project that contains a **model transformation** (ATL project) that generates instances of the Bamol DSL that are in accordance with configuration models
 * it depends on the projects: **bamolm2**; **vmodeldsl**; **vmodel**; **json2ecore**
 * to open in Eclipse use "File"->"Import..."->"Existing Maven Projects" 
 * to build:
 
	mvn clean install  
	
 * after building, you may execute a simple transformation just for testing purposes:
 
	mvn exec:java@config
	
 * this transformation takes as input: an instance of the LCAP dsl file (that can have variabillity annotations); the name of a file to be used to contain the resulting LCAP dsl with solved variability; an instance of the variability dsl that is referenced by the previous bamol dsl; an instance of the variability dsl that functions as a configuration model (which contains only the selected features)

 * To import/export between the LCAP dsl and the OMNIA LCAP platform two processess are available: **dsl2json** and **json2dsl**. These can be executed using Maven (see details imn the pom file).
 
**7) Test project**

 * This project can be used to test the overwall approach
 
 * You should open all the others projects in Eclipse and spawn a second instance of Eclipse were you use this project to test the approach

# Overview of the Process 

![](process1.jpg)	
