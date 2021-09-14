package pt.bamol.atl.configuration;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.emf.ecore.xmi.impl.XMLResourceFactoryImpl;
import org.eclipse.m2m.atl.emftvm.EmftvmFactory;
import org.eclipse.m2m.atl.emftvm.ExecEnv;
import org.eclipse.m2m.atl.emftvm.Metamodel;
import org.eclipse.m2m.atl.emftvm.Model;
import org.eclipse.m2m.atl.emftvm.impl.resource.EMFTVMResourceFactoryImpl;
import org.eclipse.m2m.atl.emftvm.util.ClassModuleResolver;
import org.eclipse.m2m.atl.emftvm.util.DefaultModuleResolver;
import org.eclipse.m2m.atl.emftvm.util.ModuleResolver;
import org.eclipse.m2m.atl.emftvm.util.TimingData;
import org.eclipse.xtext.nodemodel.impl.BasicNodeTreeIterator;
import org.eclipse.xtext.resource.XtextResourceSet;

import com.google.inject.Injector;

import pt.bamol.atl.configuration.util.Ref;
import pt.bamol.bamolm2.BamolM2Package;
import pt.bamol.bamolm2.BamolM2StandaloneSetup;
import pt.bamol.json.JsonStandaloneSetup;
import pt.bamol.json.json.JsonPackage;
import pt.bamol.atl.configuration.util.*;

public class Json2Dsl {

	public static void registerEcorePackages() {
		// For the xtext DSLs
		// do this only once per application
		Injector injector = new JsonStandaloneSetup().createInjectorAndDoEMFRegistration();

		Injector injector2 = new BamolM2StandaloneSetup().createInjectorAndDoEMFRegistration();

		// obtain a resourceset from the injector
		XtextResourceSet resSet = injector.getInstance(XtextResourceSet.class);

		// Associate the "uc" extension with the XMI resource format
		// Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("json", new
		// XMIResourceFactoryImpl());

		// Initialize the package
		JsonPackage.eINSTANCE.eClass();

		BamolM2Package.eINSTANCE.eClass();

		// Initialize the EMFTVM package
		org.eclipse.m2m.atl.emftvm.EmftvmPackage.eINSTANCE.eClass();

		// Associate the "emftvm" extension with the EMFTVM resource format
		Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("emftvm", new EMFTVMResourceFactoryImpl());

		// For ecore?...
		// Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("ecore",
		// new XMIResourceFactoryImpl());
		// EcorePackage.eINSTANCE.eClass();
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
			// "/Users/alexandrebraganca/disk/workspaces/bamol/bamol-ls/json2ecore/pt.bamol.atl.json2ecore/instances/bamol-json",
			// "/Users/alexandrebraganca/disk/workspaces/bamol/bamol-ls/json2ecore/pt.bamol.atl.json2ecore/instances/bamol-json.json");
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

		String[] parms = null;

		if (args.length == 0) {
			// Set default arguments
			parms = new String[] {
					//"/Users/alexandrebraganca/repositories/bamol-ls/json2ecore/pt.bamol.atl.configuration/instances/bamol-json",
					"/Users/alexandrebraganca/repositories/bamol-ls/json2ecore/pt.bamol.atl.configuration/instances/bamol-json-case-study",
					
					"/Users/alexandrebraganca/repositories/bamol-ls/json2ecore/pt.bamol.atl.configuration/instances/bamol-json.json",
					"/Users/alexandrebraganca/repositories/bamol-ls/json2ecore/pt.bamol.atl.configuration/instances/bamol1.bamolm2" };
		} else {
			parms = args.clone();
		}

		for (String s : parms) {
			System.out.println(s);
		}

		convertJson(parms);

		// transform(parms);
		transformGPL(parms);

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
		metaModel2.setResource(rs.getResource(URI.createURI("http://bamol.pt/bamolM2"), true));
		env.registerMetaModel("BAMOL", metaModel2);

		// Load models
		Model inModel = EmftvmFactory.eINSTANCE.createModel();
		inModel.setResource(rs.getResource(URI.createURI(args[1], true), true));
		env.registerInOutModel("IN", inModel);

		Model outModel = EmftvmFactory.eINSTANCE.createModel();
		outModel.setResource(rs.createResource(URI.createFileURI(args[2])));
		env.registerOutputModel("OUT", outModel);

		// Load and run module
		// ModuleResolver mr = new DefaultModuleResolver("transformations/", new
		// ResourceSetImpl());
		ModuleResolver mr = new ClassModuleResolver(Json2Dsl.class);

		TimingData td = new TimingData();
		env.loadModule(mr, "json2dsl");
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

	public static void transformGPL(String[] args) {
		registerEcorePackages();

		ResourceSet rs = new ResourceSetImpl();

		// Load models
		Resource rJson = rs.getResource(URI.createURI(args[1], true), true);

		Resource rDsl = rs.createResource(URI.createFileURI(args[2]));

		EList<EObject> listJson = rJson.getContents();

		// now load the content.
		try {
			rJson.load(Collections.EMPTY_MAP);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Get the root element
		EObject root = rJson.getContents().get(0);
		pt.bamol.json.json.Object myObject = (pt.bamol.json.json.Object) root;
		
		refs=new ArrayList<Ref>();

		EObject eObject = transformObject("", 0, myObject);

		// -------------
		// Test
//		EClass eClass1=getEClass("Root");
//		EObject eObject=eClass1.getEPackage().getEFactoryInstance().create(eClass1);
//		EClass eClass2=getEClass("Models");
//		EObject eObject2=eClass2.getEPackage().getEFactoryInstance().create(eClass2);
//		EList<EObject> list=(EList<EObject>)eObject.eGet(eClass1.getEStructuralFeature("models"));
//		list.add(eObject2);
		// UNNECSSARY: eObject.eSet(eClass1.getEStructuralFeature("models"), list);
		// -------------

		rDsl.getContents().add(eObject);

		// now save the content.
		try {
			rDsl.save(Collections.EMPTY_MAP);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// Experiment: output the identified references
		for (Ref ref : refs) {
			if (ref instanceof RefString) {
				RefString refS=(RefString) ref;
				System.out.println(" RefString: "+refS.getFieldName()+" EClass="+refS.geteClass());
			}
			else 
			{
				RefArrayString refAS=(RefArrayString) ref;
				System.out.println(" RefArrayString: "+refAS.getFieldName()+" EClass="+refAS.geteClass());				
			}
		}
		
	}

	static EClass getEClass(String className) {
		// Para ir buscar as classes desta package:
		EList<EClassifier> lClasses = BamolM2Package.eINSTANCE.getEClassifiers();
		for (EClassifier eClass : lClasses) {
			// System.out.println(" Class = " + eClass.getName());
			if (className.compareTo(eClass.getName()) == 0)
				return (EClass) eClass;
		}

		return null;
	}
	
	static private ArrayList<Ref> refs=null;

	static EObject transformObject(String parent, int level, pt.bamol.json.json.Object obj) {

		EClass eClass = null;
		if (level == 0) {
			eClass = getEClass("Root");
		} else {
			eClass = getEClass(parent);
		}
		if (eClass == null) {
			System.out.println(" Class not found=" + parent);
		}
		for (int i = 0; i < level; ++i)
			System.out.print('\t');
		System.out.println("Class=" + eClass.getName());

		// Create a new object of the Class
		EObject eObject = eClass.getEPackage().getEFactoryInstance().create(eClass);

		// if (level>1) return eObject;

		// Process all the members of the object/class
		for (pt.bamol.json.json.Member m : obj.getMembers()) {
			for (int i = 0; i < level; ++i)
				System.out.print('\t');
			String name = m.getKey();
			System.out.println(" member=" + name);

			pt.bamol.json.json.Value val = m.getValue();
			if (val != null) {
				if (val.eClass().equals(JsonPackage.eINSTANCE.getObject())) {
					// for (int i=0; i<level; ++i) System.out.print(' ');
					// System.out.println("-> Object");

					// Lets find the Class for this object...
					String className = parent + (name.substring(0, 1).toUpperCase() + name.substring(1, name.length()));

					EObject memberObject = transformObject(className, level+1, (pt.bamol.json.json.Object) val);
					//EList<EObject> list = (EList<EObject>) eObject.eGet(eClass.getEStructuralFeature(name));
					eObject.eSet(eClass.getEStructuralFeature(name), memberObject);
					
					//list.add(memberObject);
					// eObject.eSet(eClass.getEStructuralFeature(name), list);
				} else if (val.eClass().equals(JsonPackage.eINSTANCE.getArray())) {
					// for (int i=0; i<level; ++i) System.out.print(' ');
					// System.out.println("-> Array");

					pt.bamol.json.json.Array array = (pt.bamol.json.json.Array) val;
					EList<pt.bamol.json.json.Value> values = array.getValues();

					if (values != null && !values.isEmpty()) {

						if (values.get(0).eClass().equals(JsonPackage.eINSTANCE.getObject())) {
							// An array of objects

							// Lets find the Class for this object...
							String className = parent
									+ (name.substring(0, 1).toUpperCase() + name.substring(1, name.length()));
							
							if (className.compareTo("ModelsFormsElementsElements")==0)
							{
								System.out.println("  ->>> Class name before transformObject = "+className);
							}

							EList<EObject> list = (EList<EObject>) eObject.eGet(eClass.getEStructuralFeature(name));
							for (pt.bamol.json.json.Value valObj : values) {
								EObject memberObject = transformObject(className, level+1,
										(pt.bamol.json.json.Object) valObj);
								list.add(memberObject);
							}
						} else if (values.get(0).eClass().equals(JsonPackage.eINSTANCE.getStringVal())) {
							// An array of strings

							// Atencao que podem ser referencias para objectos!
							// Lets check the type of this field in the metamodel
							Boolean isReference = false;
							EStructuralFeature feature = eClass.getEStructuralFeature(name);
							if (feature instanceof EReference) {
								EReference ref = (EReference) feature;
								if (!ref.isContainment())
									isReference = true;
							}
							// ------

							if (!isReference) {
								EList<String> list = (EList<String>) eObject.eGet(eClass.getEStructuralFeature(name));
								for (pt.bamol.json.json.Value valObj : values) {

									String valor = (String) valObj
											.eGet(valObj.eClass().getEStructuralFeature("stringValue"));
									list.add(valor);
								}
							} else {
								// Metaclasse: eClass
								// Nome do Campo: name
								// object: eObject
								// valores a "converter": values ("stringValue")
								refs.add(new RefArrayString(eClass, name, eObject, values));
							}
						}
					}
				} else if (val.eClass().equals(JsonPackage.eINSTANCE.getStringVal())) {
					// Atencao que as strings podem ser referencias, como no caso das Entity!!!!
					// Lets check the type of this field in the metamodel
					Boolean isReference = false;
					EStructuralFeature feature = eClass.getEStructuralFeature(name);
					if (feature instanceof EReference) {
						EReference ref = (EReference) feature;
						if (!ref.isContainment())
							isReference = true;
					}

					String valor = (String) val.eGet(val.eClass().getEStructuralFeature("stringValue"));
					if (!isReference) {
						// Get the value
						// EString valor=(EString) val;
						// if (val.eClass().isInstance(String.class)) {
						// String valor=EcoreUtil.convertToString((EDataType)val.eClass(), val);
						
						eObject.eSet(eClass.getEStructuralFeature(name), valor);
						// }
					} else {
						// Metaclasse: eClass
						// Nome do Campo: name
						// object: eObject
						// valor a "converter": val ("stringValue")
						refs.add(new RefString(eClass, name, eObject, valor));
					}
				} else if (val.eClass().equals(JsonPackage.eINSTANCE.getBooleanVal())) {
					Boolean valor = (Boolean) val.eGet(val.eClass().getEStructuralFeature("booleanValue"));
					eObject.eSet(eClass.getEStructuralFeature(name), valor);
				} else if (val.eClass().equals(JsonPackage.eINSTANCE.getNumberVal())) {
					BigDecimal valor = (BigDecimal) val.eGet(val.eClass().getEStructuralFeature("numberValue"));
					eObject.eSet(eClass.getEStructuralFeature(name), valor);
				}
			}
		}

		return eObject;
	}

}
