package pt.bamol.atl.json2ecore;

import com.google.inject.Injector;

//import pt.bamol.bamolm.bamolM.BamolMPackage;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.URIHandlerImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.m2m.atl.emftvm.EmftvmFactory;
import org.eclipse.m2m.atl.emftvm.ExecEnv;
import org.eclipse.m2m.atl.emftvm.Metamodel;
import org.eclipse.m2m.atl.emftvm.Model;
import org.eclipse.m2m.atl.emftvm.impl.resource.EMFTVMResourceFactoryImpl;
import org.eclipse.m2m.atl.emftvm.util.DefaultModuleResolver;
import org.eclipse.m2m.atl.emftvm.util.ModuleResolver;
import org.eclipse.m2m.atl.emftvm.util.TimingData;
import org.eclipse.xtext.resource.XtextResourceSet;
import pt.bamol.json.JsonStandaloneSetup;
import pt.bamol.json.json.JsonPackage;
import pt.bamol.vmodel.VmodelPackage;

public class EcoreCustomization {

	public static void registerEcorePackages() {
		// For the xtext DSLs
		// do this only once per application
		Injector injector = new JsonStandaloneSetup().createInjectorAndDoEMFRegistration();

		// obtain a resourceset from the injector
		XtextResourceSet resSet = injector.getInstance(XtextResourceSet.class);

		// Associate the "uc" extension with the XMI resource format
		// Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("json", new
		// XMIResourceFactoryImpl());

		// Initialize the usecase package
		JsonPackage.eINSTANCE.eClass();

		// Initialize the EMFTVM package
		org.eclipse.m2m.atl.emftvm.EmftvmPackage.eINSTANCE.eClass();

		// Associate the "emftvm" extension with the EMFTVM resource format
		Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("emftvm", new EMFTVMResourceFactoryImpl());
		
		// For ecore?...
		Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("ecore", new XMIResourceFactoryImpl());
		EcorePackage.eINSTANCE.eClass();
		
		// For the vmodel
		VmodelPackage.eINSTANCE.eClass();
	}

	public static void main(String[] args) {

		String[] parms=null;
		
		if (args.length==0) {
			// Set default arguments
			parms = new String[] {
		            "/Users/alexandrebraganca/repositories/bamol-ls/json2ecore/pt.bamol.atl.json2ecore/instances/bamol-ecore.ecore",
		            "/Users/alexandrebraganca/repositories/bamol-ls/json2ecore/pt.bamol.atl.json2ecore/instances/bamol-ecore-customization-pre.ecore",
		            "/Users/alexandrebraganca/repositories/bamol-ls/json2ecore/pt.bamol.atl.json2ecore/instances/bamol-ecore-customization.ecore",
		            "/Users/alexandrebraganca/repositories/bamol-ls/json2ecore/pt.bamol.vmodel/model/vmodel.ecore"
		         //   <argument>platform:/resource/pt.bamol.vmodel/model/vmodel.ecore</argument> 					
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
		registerEcorePackages();

		ExecEnv env = EmftvmFactory.eINSTANCE.createExecEnv();
		ResourceSet rs = new ResourceSetImpl();

		// Load metamodels
//		Metamodel metaModel = EmftvmFactory.eINSTANCE.createMetamodel();
//		metaModel.setResource(rs.getResource(URI.createURI("http://bamol.pt/bamolM"), true));
//		env.registerMetaModel("ECORE", metaModel);
		Metamodel metaModel = EmftvmFactory.eINSTANCE.createMetamodel();
		metaModel.setResource(rs.getResource(URI.createURI("http://www.eclipse.org/emf/2002/Ecore"), true));
		env.registerMetaModel("ECORE", metaModel);	
		
		Metamodel metaModel2 = EmftvmFactory.eINSTANCE.createMetamodel();
		metaModel.setResource(rs.getResource(URI.createURI("http://www.bamol.pt/vmodel"), true));
		env.registerMetaModel("VMODELM", metaModel2);		

		// Load models
		Model inOutModel = EmftvmFactory.eINSTANCE.createModel();
		inOutModel.setResource(rs.getResource(URI.createURI(args[0], true), true));
		env.registerInOutModel("INPUT", inOutModel);

		Model vModel = EmftvmFactory.eINSTANCE.createModel();
		
		// platform:/resource/pt.bamol.vmodel/model/vmodel.ecore
		// vModel.setResource(rs.getResource(URI.createURI(args[2], true), true));
		// vModel.setResource(rs.getResource(URI.createPlatformResourceURI(args[2], true), true));
		vModel.setResource(rs.getResource(URI.createURI(args[3], true), true));
		env.registerInputModel("VMODEL", vModel);
		
		// Load the referenced resource
		// rs.getResource(URI.createURI(args[2], true), true);	

		// Load and run module
		ModuleResolver mr = new DefaultModuleResolver("transformations/", new ResourceSetImpl());

		TimingData td = new TimingData();
		env.loadModule(mr, "ecoreCustomization");
		td.finishLoading();
		env.run(td);
		td.finish();

		// Save models
		try {
			inOutModel.getResource().setURI(URI.createFileURI(args[1]));
			final Map<Object, Object> saveOptions = new HashMap<Object, Object>();
	        saveOptions.put(Resource.OPTION_SAVE_ONLY_IF_CHANGED, Resource.OPTION_SAVE_ONLY_IF_CHANGED_MEMORY_BUFFER);
	        saveOptions.put(Resource.OPTION_LINE_DELIMITER, Resource.OPTION_LINE_DELIMITER_UNSPECIFIED);
	        saveOptions.put(XMLResource.OPTION_USE_ENCODED_ATTRIBUTE_STYLE, Boolean.TRUE);
	        saveOptions.put(XMLResource.OPTION_LINE_WIDTH, 80);
	        saveOptions.put(XMLResource.OPTION_URI_HANDLER, new URIHandlerImpl.PlatformSchemeAware());
			inOutModel.getResource().save(saveOptions);
			
			// Corrigir erro do eproxy
			FileContentReplacer rep=new FileContentReplacer("ecore:EClass .+vmodel.ecore",
					"ecore:EClass platform:/resource/pt.bamol.vmodel/model/vmodel.ecore");
			rep.execute(args[1], args[2]);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}