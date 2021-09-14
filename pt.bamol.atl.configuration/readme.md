# Requirements

  OpenJDK 1.8
  
## Notes

- In Maven, the **-o** option is used so that maven does not always fetch info from the repositories  

# Attention

  When building inside Code or Gitpod be aware that sometimes these IDEs only work with java versions from 11! They can try to automatically build the sources!

# To build

	cd pt.bamol.atl.configuration
	mvn clean install

# To execute the transformation bamolConfig

This will transform a bamol dsl file that models a product line into a model of a single product by removing the variability according to a variability configuration dsl file (vmodel)

	cd pt.bamol.atl.bamolconfig
	mvn exec:java@config -o

# To only compile the ATL file

	cd pt.bamol.atl.bamolconfig
	mvn validate -o

