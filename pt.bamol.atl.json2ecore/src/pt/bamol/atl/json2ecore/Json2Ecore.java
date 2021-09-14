package pt.bamol.atl.json2ecore;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.Collections;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMLResourceFactoryImpl;
import org.eclipse.m2m.atl.emftvm.EmftvmFactory;
import org.eclipse.m2m.atl.emftvm.ExecEnv;
import org.eclipse.m2m.atl.emftvm.Metamodel;
import org.eclipse.m2m.atl.emftvm.Model;
import org.eclipse.m2m.atl.emftvm.impl.resource.EMFTVMResourceFactoryImpl;
import org.eclipse.m2m.atl.emftvm.util.DefaultModuleResolver;
import org.eclipse.m2m.atl.emftvm.util.ModuleResolver;
import org.eclipse.m2m.atl.emftvm.util.TimingData;
import org.eclipse.xtext.resource.XtextResourceSet;

import com.google.inject.Injector;

import pt.bamol.json.JsonStandaloneSetup;
import pt.bamol.json.json.JsonPackage;

public class Json2Ecore {
	
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
	}

	public static void Folder2JsonStart(String folder, String newFile) throws IOException {

		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(newFile), "utf-8"));

		writer.write("{");
		writer.newLine();

		Folder2Json(folder, writer, 0);

		writer.write("}");
		writer.newLine();

		// writer.write("something");

		writer.close();

	}
	
	public static void convertJson(String[] args) {
		try {
			Folder2JsonStart(args[0], args[1]);
					//"/Users/alexandrebraganca/disk/workspaces/bamol/bamol-ls/json2ecore/pt.bamol.atl.json2ecore/instances/bamol-json",
					//"/Users/alexandrebraganca/disk/workspaces/bamol/bamol-ls/json2ecore/pt.bamol.atl.json2ecore/instances/bamol-json.json");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * This will convert a folder structure with several json files in a unique json
	 * file
	 * 
	 * @throws IOException
	 */
	public static void Folder2Json(String folder, BufferedWriter writer, int level) throws IOException {
		// your directory
		File fStart = new File(folder);
		File[] matchingFiles = fStart.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				File temp = null;
				if (name.startsWith("."))
					return false;
				if (name.endsWith(".json"))
					return true;
				temp = new File(dir, name);
				if (temp.isDirectory())
					return true;
				return false;
				// return name.startsWith("temp") && name.endsWith("txt");
			}
		});

		// Tab
		String tab = "";
		for (int i = 0; i < level; ++i) {
			System.out.print(" ");
			tab = tab + " ";
		}

		// Any of the files is a folder?
		boolean hasSubFolder = false;
		if (level > 0) {
			for (File f : matchingFiles) {
				if (f.isDirectory())
					hasSubFolder = true;
			}

			if (hasSubFolder) {
				writer.write(tab + "{");
				writer.newLine();
			}
		}

		// Convert
		String separator = "";
		for (File f : matchingFiles) {

			writer.write(separator);
			writer.newLine();
			if (f.isDirectory()) {
				System.out.println("Diretorio:" + f.getName());

				writer.write(tab + "\"" + f.getName().toLowerCase() + "s" + "\" : [ ");
				writer.newLine();

				Folder2Json(f.getAbsolutePath(), writer, level + 1);

				writer.write(tab + " ] ");
				writer.newLine();
			} else {
				System.out.println("Ficheiro:" + f.getName());

				// Read contents of file...
				FileReader reader = new FileReader(f.getAbsoluteFile());
				int character;

				while ((character = reader.read()) != -1) {
					writer.write(character);
					// System.out.print((char) character);
				}
				reader.close();
			}
			separator = ",";
		}

		if (level > 0) {
			if (hasSubFolder) {
				writer.write(tab + "}");
				writer.newLine();
			}
		}
	}
	
	public static void main(String[] args) {

		String[] parms=null;
		
		if (args.length==0) {
			// Set default arguments
			parms = new String[] {
		            // "/Users/alexandrebraganca/repositories/bamol-ls/json2ecore/pt.bamol.atl.json2ecore/instances/bamol-json",
		            "/Users/alexandrebraganca/repositories/bamol-ls/json2ecore/pt.bamol.atl.json2ecore/instances/bamol-json-case-study",     
		            "/Users/alexandrebraganca/repositories/bamol-ls/json2ecore/pt.bamol.atl.json2ecore/instances/bamol-json.json",
		            "/Users/alexandrebraganca/repositories/bamol-ls/json2ecore/pt.bamol.atl.json2ecore/instances/bamol-ecore.ecore"
			};
		}
		else {
			parms=args.clone();
		}
		
        for (String s:parms) {
            System.out.println(s);
        }
        
		convertJson(parms);

		transform(parms);
	}
	
	public static void transform(String[] args) {
		registerEcorePackages();

		ExecEnv env = EmftvmFactory.eINSTANCE.createExecEnv();
		ResourceSet rs = new ResourceSetImpl();

		// Load metamodels
		Metamodel metaModel = EmftvmFactory.eINSTANCE.createMetamodel();
		metaModel.setResource(rs.getResource(URI.createURI("http://www.bamol.pt/json/Json"), true));
		env.registerMetaModel("JSON", metaModel);

		Metamodel metaModel2 = EmftvmFactory.eINSTANCE.createMetamodel();
		metaModel2.setResource(rs.getResource(URI.createURI("http://www.eclipse.org/emf/2002/Ecore"), true));
		env.registerMetaModel("ECORE", metaModel2);
		
		// Load models
		Model inModel = EmftvmFactory.eINSTANCE.createModel();
		inModel.setResource(rs.getResource(URI.createURI(args[1], true), true));
		env.registerInOutModel("IN", inModel);
		
		Model outModel = EmftvmFactory.eINSTANCE.createModel();
		outModel.setResource(rs.createResource(URI.createFileURI(args[2])));
		env.registerOutputModel("OUT", outModel);		

		// Load and run module
		ModuleResolver mr = new DefaultModuleResolver("transformations/", new ResourceSetImpl());

		TimingData td = new TimingData();
		env.loadModule(mr, "json2ecore");
		td.finishLoading();
		env.run(td);
		td.finish();

		// Save models
		try {
			outModel.getResource().save(Collections.emptyMap());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}	
}
