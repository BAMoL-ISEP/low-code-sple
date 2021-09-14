package pt.bamol.atl.configuration;

import java.io.IOException;
import java.util.Collections;
import java.util.NoSuchElementException;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.m2m.atl.emftvm.EmftvmFactory;
import org.eclipse.m2m.atl.emftvm.ExecEnv;
import org.eclipse.m2m.atl.emftvm.Metamodel;
import org.eclipse.m2m.atl.emftvm.Model;
import org.eclipse.m2m.atl.emftvm.impl.resource.EMFTVMResourceFactoryImpl;
import org.eclipse.m2m.atl.emftvm.util.ClassModuleResolver;
import org.eclipse.m2m.atl.emftvm.util.DefaultModuleResolver;
import org.eclipse.m2m.atl.emftvm.util.ModuleResolver;
import org.eclipse.m2m.atl.emftvm.util.TimingData;
import org.eclipse.xtext.resource.XtextResourceSet;

import com.google.inject.Injector;

import pt.bamol.bamolm2.BamolM2Package;
import pt.bamol.bamolm2.BamolM2StandaloneSetup;
import pt.bamol.vmodel.Feature;
import pt.bamol.vmodel.vmodeldsl.VModelDslStandaloneSetup;

public class BamolConfig {
	
	private static ResourceSet rsConfig = null;
	private static Resource rConfig = null;

	public static void registerEcorePackages() {
		// For the xtext DSLs
		// do this only once per application
		Injector injector = new BamolM2StandaloneSetup().createInjectorAndDoEMFRegistration();

		Injector injector2 = new VModelDslStandaloneSetup().createInjectorAndDoEMFRegistration();

		// obtain a resourceset from the injector
		XtextResourceSet resSet = injector.getInstance(XtextResourceSet.class);

		// Associate the "uc" extension with the XMI resource format
		// Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("json", new
		// XMIResourceFactoryImpl());

		// Initialize the usecase package
		//JsonPackage.eINSTANCE.eClass();

		// Initialize the EMFTVM package
		org.eclipse.m2m.atl.emftvm.EmftvmPackage.eINSTANCE.eClass();

		// Associate the "emftvm" extension with the EMFTVM resource format
		Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("emftvm", new EMFTVMResourceFactoryImpl());
		
		// For ecore?...
		//Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("ecore", new XMIResourceFactoryImpl());
		//EcorePackage.eINSTANCE.eClass();
		
		// For the ecore-bamol
		BamolM2Package.eINSTANCE.eClass();
	}

	public static void main(String[] args) {
		
		String[] parms=null;
		
		if (args.length==0) {
			// Set default arguments
			parms = new String[] {
			 "/Users/alexandrebraganca/repositories/bamol-ls/json2ecore/pt.bamol.atl.configuration/instances/test1.bamolm2",
			 "/Users/alexandrebraganca/repositories/bamol-ls/json2ecore/pt.bamol.atl.configuration/instances/test1-out.bamolm2",
			 "/Users/alexandrebraganca/repositories/bamol-ls/json2ecore/pt.bamol.atl.configuration/instances/vmodeldsl1.vmodeldsl",
			 "/Users/alexandrebraganca/repositories/bamol-ls/json2ecore/pt.bamol.atl.configuration/instances/configuration1.vmodeldsl"
			};
		}
		else {
			parms=args.clone();
		}
		
        for (String s:parms) {
            System.out.println(s);
        }
        
		transform(parms);
	}

	public static void transform(String[] args) {
		try {
		registerEcorePackages();
		
		// Store the configuration model
		rsConfig = new ResourceSetImpl();
		rConfig= rsConfig.getResource(URI.createURI(args[3], true), true);
		
		rConfig.load(Collections.EMPTY_MAP);

		ExecEnv env = EmftvmFactory.eINSTANCE.createExecEnv();
		ResourceSet rs = new ResourceSetImpl();

		// Load metamodels
//		Metamodel metaModel = EmftvmFactory.eINSTANCE.createMetamodel();
//		metaModel.setResource(rs.getResource(URI.createURI("http://bamol.pt/bamolM"), true));
//		env.registerMetaModel("ECORE", metaModel);
		Metamodel metaModel = EmftvmFactory.eINSTANCE.createMetamodel();
		//metaModel.setResource(rs.getResource(URI.createURI("http://www.bamol.pt/bamolm2/BamolM2"), true));
		metaModel.setResource(rs.getResource(URI.createURI("http://bamol.pt/bamolM2"), true));
		env.registerMetaModel("BAMOL", metaModel);		

//		Metamodel metaModel2 = EmftvmFactory.eINSTANCE.createMetamodel();
//		metaModel.setResource(rs.getResource(URI.createURI("http://www.bamol.pt/vmodel"), true));
//		env.registerMetaModel("VMODEL", metaModel);
		
		// Load models
		Model inOutModel = EmftvmFactory.eINSTANCE.createModel();
		inOutModel.setResource(rs.getResource(URI.createURI(args[0], true), true));
		env.registerInOutModel("IN", inOutModel);

		// Load the referenced resource
		rs.getResource(URI.createURI(args[2], true), true);
//		Model inModel = EmftvmFactory.eINSTANCE.createModel();
//		inModel.setResource(rs.getResource(URI.createURI(args[2], true), true));
//		env.registerInOutModel("CONFIG", inOutModel);
		
		// Load and run module
		// ModuleResolver mr = new DefaultModuleResolver("transformations/", new ResourceSetImpl());
		// Do NOT USE: ModuleResolver mr = new DefaultModuleResolver("platform:/plugin/pt.bamol.atl.configuration/transformations/", new ResourceSetImpl());
		ModuleResolver mr = new ClassModuleResolver(BamolConfig.class);
		
		TimingData td = new TimingData();
		env.loadModule(mr, "bamolConfig");
		td.finishLoading();
		env.run(td);
		td.finish();

		// Save models

			inOutModel.getResource().setURI(URI.createFileURI(args[1]));
			inOutModel.getResource().save(Collections.emptyMap());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static boolean verify(Feature feat) {
		// Verify if the Feature is included in the Configuration model
		pt.bamol.vmodel.Model model = (pt.bamol.vmodel.Model)rConfig.getContents().get(0);
		
		TreeIterator<EObject> tree=model.eAllContents();
		EObject obj=null;
		if (tree.hasNext()) obj=tree.next();
		while (obj!=null) {
			// See if we find the Feature in the configuration Model
			if (obj.eClass().getName().compareTo(feat.eClass().getName())==0) {
				return true;
			}
			
			obj=null;
			if (tree.hasNext()) obj=tree.next();
		}
		// if (feat.getName().compareTo("sale2")==0) return true;
		return false;
	}
}
