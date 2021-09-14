package pt.bamol.atl.configuration;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.m2m.atl.emftvm.impl.resource.EMFTVMResourceFactoryImpl;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.resource.XtextResourceSet;

import com.google.inject.Injector;

import pt.bamol.atl.configuration.util.Ref;
import pt.bamol.atl.configuration.util.RefArrayString;
import pt.bamol.atl.configuration.util.RefString;
import pt.bamol.bamolm2.BamolM2Package;
import pt.bamol.bamolm2.BamolM2StandaloneSetup;
import pt.bamol.json.JsonStandaloneSetup;
import pt.bamol.json.json.JsonFactory;
import pt.bamol.json.json.JsonPackage;
import pt.bamol.json.json.Member;
import pt.bamol.json.json.Null;
import pt.bamol.json.json.NumberVal;
import pt.bamol.json.json.StringVal;
import pt.bamol.json.json.Value;
import pt.bamol.json.json.Array;
import pt.bamol.json.json.BooleanVal;
import pt.bamol.json.json.Object;

public class Dsl2Json {

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
		EcorePackage.eINSTANCE.eClass();
	}

	public static void main(String[] args) {

		String[] parms = null;

		if (args.length == 0) {
			// Set default arguments
			parms = new String[] {
					// "/Users/alexandrebraganca/repositories/bamol-ls/json2ecore/pt.bamol.atl.configuration/instances/bamol-json",
					"/Users/alexandrebraganca/repositories/bamol-ls/json2ecore/pt.bamol.atl.configuration/instances/bamol1.bamolm2",
					"/Users/alexandrebraganca/repositories/bamol-ls/json2ecore/pt.bamol.atl.configuration/instances/bamol-json-out.json",
					"/Users/alexandrebraganca/repositories/bamol-ls/json2ecore/pt.bamol.atl.configuration/instances/bamol-json-case-study-out" };
		} else {
			parms = args.clone();
		}

		for (String s : parms) {
			System.out.println(s);
		}

		// transform(parms);
		transformGPL(parms);

		// convertJson(parms);

	}
	
	static Map<String, String> mapConcepts=null;
	
	private static void initConceptMap() {
		mapConcepts = new HashMap<String, String>();
		
		mapConcepts.put("datasources", "DataSource");
		mapConcepts.put("selectors", "Selector");
		mapConcepts.put("forms", "Form");
		mapConcepts.put("statemachines", "StateMachine");
		mapConcepts.put("enumerations", "Enumeration");
		mapConcepts.put("agents", "Agent");
		mapConcepts.put("documents", "Document");
		mapConcepts.put("series", "Serie");
		mapConcepts.put("dashboards", "Dashboard");
		mapConcepts.put("lists", "List");
		mapConcepts.put("genericentitys", "GenericEntity");
		mapConcepts.put("resources", "Resource");
		mapConcepts.put("querys", "Query");
		mapConcepts.put("applicationbehaviours", "ApplicationBehaviour");
	}

	public static void transformGPL(String[] args) {
		registerEcorePackages();

		ResourceSet rs = new ResourceSetImpl();

		// Load input model
		Resource rDsl = rs.getResource(URI.createURI(args[0], true), true);

		// Create output model
		Resource rJson = rs.createResource(URI.createFileURI(args[1]));

		EList<EObject> listDsl = rDsl.getContents();

		// now load the content.
		try {
			rDsl.load(Collections.EMPTY_MAP);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Get the root element
		EObject root = rDsl.getContents().get(0);
		// pt.bamol.json.json.Object myObject = (pt.bamol.json.json.Object) root;

		// refs=new ArrayList<Ref>();

		EObject eObject = transformObject("", 0, root);

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

		rJson.getContents().add(eObject);

		// now save the content.
		try {
			rJson.save(Collections.EMPTY_MAP);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Para converter para vários ficheiros...
//		XtextResource res = (XtextResource)object.eResource();
//		System.out.println(res.getSerializer().serialize(object));
		// Lets give this a try...
		initConceptMap();
		
		File baseDir = new File(args[2]);
		try {
			FileUtils.deleteDirectory(baseDir);

			// Creating the directory
			boolean bool = baseDir.mkdir();

			EList<Member> members1 = (EList<Member>) eObject.eGet(eObject.eClass().getEStructuralFeature("members"));

			for (Member m1 : members1) {

				if (m1.getKey().compareTo("models") == 0) {

					Array a2=(Array)m1.getValue();
					EList<Value> list2 = (EList<Value>)a2.getValues();
					
					Object o2=(Object)list2.get(0);
					
					EList<Member> members2 = (EList<Member>) o2.eGet(o2.eClass().getEStructuralFeature("members"));

					for (Member m2 : members2) {

						String concept=mapConcepts.get(m2.getKey());
						
						if (concept!=null) {

							// Lets try the first section/file...
							String dir = args[2] + "/Model/"+ concept;
							File fileDir = new File(dir);

							FileUtils.forceMkdir(fileDir);
							
							Array a3=(Array)m2.getValue();
							EList<Value> list3 = (EList<Value>)a3.getValues();
							
							for (Value v3: list3) {
								Object o3=(Object)v3;
								
								EList<Member> members3 = (EList<Member>) o3.eGet(o3.eClass().getEStructuralFeature("members"));
								
								for (Member m3: members3) {
									if (m3.getKey().compareTo("name") == 0) {
										
										StringVal strVal=(StringVal)m3.getValue();
										String jsonName=strVal.getStringValue();
										
										XtextResource res = (XtextResource)o3.eResource();
										String jsonFileName = args[2] + "/Model/"+ concept +"/"+ jsonName + ".json";
										
										
										FileUtils.writeStringToFile(new File(jsonFileName), res.getSerializer().serialize(o3));
									}
								}
							}
						}
					}
				}
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Experiment: output the identified references
//		for (Ref ref : refs) {
//			if (ref instanceof RefString) {
//				RefString refS=(RefString) ref;
//				System.out.println(" RefString: "+refS.getFieldName()+" EClass="+refS.geteClass());
//			}
//			else 
//			{
//				RefArrayString refAS=(RefArrayString) ref;
//				System.out.println(" RefArrayString: "+refAS.getFieldName()+" EClass="+refAS.geteClass());				
//			}
//		}
	}

	static EObject transformObject(String parent, int level, EObject obj) {

		EClass eClass = obj.eClass();
		if (eClass == null) {
			System.out.println(" Class not found=" + parent);
		}
		for (int i = 0; i < level; ++i)
			System.out.print('\t');
		System.out.println("Class=" + eClass.getName());

		// Create a new object in Json
		pt.bamol.json.json.Object jsonObj = JsonPackage.eINSTANCE.getJsonFactory().createObject();

		// Obtain the "members" collections
		EList<EObject> members = (EList<EObject>) jsonObj.eGet(jsonObj.eClass().getEStructuralFeature("members"));

		// Process all the members of the object/class
		for (EStructuralFeature m : eClass.getEStructuralFeatures()) {
			for (int i = 0; i < level; ++i)
				System.out.print('\t');

			String name = m.getName();
			System.out.println(" member=" + name);

			EClassifier type = m.getEType();

			if (m instanceof EReference) {
				EReference ref = (EReference) m;

				if (ref.isContainment()) {
					// Okay, lets handle the reference...
					if (m.getUpperBound() == 1) {
						// In Json, it is an object
						EObject object = (EObject) obj.eGet(m);

						JsonFactory jsonFact = (JsonFactory) JsonPackage.eINSTANCE.getEFactoryInstance();
						Member jsonMember = jsonFact.createMember();
						jsonMember.setKey(name);

						pt.bamol.json.json.Object valObj = (pt.bamol.json.json.Object) transformObject(name, level + 1,
								object);

						jsonMember.setValue(valObj);
						members.add((EObject) jsonMember);

					} else {
						// In Json, it is an array of objects
						EList<EObject> list = (EList<EObject>) obj.eGet(m);

						JsonFactory jsonFact = (JsonFactory) JsonPackage.eINSTANCE.getEFactoryInstance();
						Member jsonMember = jsonFact.createMember();
						jsonMember.setKey(name);

						if (name.compareTo("dataBehaviours") == 0) {
							String nome2 = name;
						}

						Array array = jsonFact.createArray();
						EList<EObject> arrayValues = (EList<EObject>) array
								.eGet(array.eClass().getEStructuralFeature("values"));

						for (EObject o : list) {
							EObject valObj = transformObject(name, level + 1, o);

							arrayValues.add(valObj);
						}
						jsonMember.setValue(array);
						members.add((EObject) jsonMember);
					}
				} else {
					// It is not a containment, we do not know how to deal with it... yet
				}

			} else if (m instanceof EAttribute) {
				// We do not handle it yet
				if (m.getUpperBound() == 1) {
					// It is a single value
					EClassifier eString = EcorePackage.eINSTANCE.getEString();
					EClassifier eBigDecimal = EcorePackage.eINSTANCE.getEBigDecimal();
					EClassifier eBoolean = EcorePackage.eINSTANCE.getEBoolean();
//					eString.getClass();
					if (type.getInstanceTypeName().compareTo(eString.getInstanceTypeName()) == 0) {
						String value = (String) obj.eGet(m);

						// There seems to be an issue in the strings coming from the DSL -> Where \'
						// replace with '
//						if ((value!=null) && (value.length()>=2) && (value.contains("'"))) {
//							value=value.replace("\\'", "\\");
//						}

						JsonFactory jsonFact = (JsonFactory) JsonPackage.eINSTANCE.getEFactoryInstance();
						Member jsonMember = jsonFact.createMember();

						if (value != null) {
							StringVal strVal = jsonFact.createStringVal();

							strVal.setStringValue(value);
							jsonMember.setKey(name);
							jsonMember.setValue(strVal);
						} else {
							Null nullVal = jsonFact.createNull();

							jsonMember.setKey(name);
							jsonMember.setValue(nullVal);
						}
						members.add((EObject) jsonMember);
					} else if (type.getInstanceTypeName().compareTo(eBigDecimal.getInstanceTypeName()) == 0) {
						BigDecimal value = (BigDecimal) obj.eGet(m);

						JsonFactory jsonFact = (JsonFactory) JsonPackage.eINSTANCE.getEFactoryInstance();
						Member jsonMember = jsonFact.createMember();

						if (value != null) {
							NumberVal numVal = jsonFact.createNumberVal();

							numVal.setNumberValue(value);
							jsonMember.setKey(name);
							jsonMember.setValue(numVal);
						} else {
							Null nullVal = jsonFact.createNull();

							jsonMember.setKey(name);
							jsonMember.setValue(nullVal);
						}
						members.add((EObject) jsonMember);
					} else if (type.getInstanceTypeName().compareTo(eBoolean.getInstanceTypeName()) == 0) {
						Boolean value = (Boolean) obj.eGet(m);

						JsonFactory jsonFact = (JsonFactory) JsonPackage.eINSTANCE.getEFactoryInstance();
						Member jsonMember = jsonFact.createMember();

						if (value != null) {
							BooleanVal boolVal = jsonFact.createBooleanVal();

							boolVal.setBooleanValue(value);
							jsonMember.setKey(name);
							jsonMember.setValue(boolVal);
						} else {
							Null nullVal = jsonFact.createNull();

							jsonMember.setKey(name);
							jsonMember.setValue(nullVal);
						}
						members.add((EObject) jsonMember);
					}
				} else {
					// Its an array of simple values
					EClassifier eString = EcorePackage.eINSTANCE.getEString();
					EClassifier eBigDecimal = EcorePackage.eINSTANCE.getEBigDecimal();
					EClassifier eBoolean = EcorePackage.eINSTANCE.getEBoolean();
//					eString.getClass();
					if (type.getInstanceTypeName().compareTo(eString.getInstanceTypeName()) == 0) {
						// String value = (String) obj.eGet(m);
						EList<String> list = (EList<String>) obj.eGet(m);

						// There seems to be an issue in the strings coming from the DSL -> Where \'
						// replace with '
//						if ((value!=null) && (value.length()>=2) && (value.contains("'"))) {
//							value=value.replace("\\'", "\\");
//						}

						JsonFactory jsonFact = (JsonFactory) JsonPackage.eINSTANCE.getEFactoryInstance();
						Member jsonMember = jsonFact.createMember();
						jsonMember.setKey(name);

						Array array = jsonFact.createArray();
						EList<EObject> arrayValues = (EList<EObject>) array
								.eGet(array.eClass().getEStructuralFeature("values"));

						for (String o : list) {
							StringVal strVal = jsonFact.createStringVal();

							strVal.setStringValue(o);
							arrayValues.add(strVal);
						}
						jsonMember.setValue(array);
						members.add((EObject) jsonMember);
					} else if (type.getInstanceTypeName().compareTo(eBigDecimal.getInstanceTypeName()) == 0) {
						EList<BigDecimal> list = (EList<BigDecimal>) obj.eGet(m);

						JsonFactory jsonFact = (JsonFactory) JsonPackage.eINSTANCE.getEFactoryInstance();
						Member jsonMember = jsonFact.createMember();
						jsonMember.setKey(name);

						Array array = jsonFact.createArray();
						EList<EObject> arrayValues = (EList<EObject>) array
								.eGet(array.eClass().getEStructuralFeature("values"));

						for (BigDecimal o : list) {
							NumberVal numVal = jsonFact.createNumberVal();

							numVal.setNumberValue(o);
							arrayValues.add(numVal);
						}
						jsonMember.setValue(array);
						members.add((EObject) jsonMember);
					} else if (type.getInstanceTypeName().compareTo(eBoolean.getInstanceTypeName()) == 0) {
						EList<Boolean> list = (EList<Boolean>) obj.eGet(m);

						JsonFactory jsonFact = (JsonFactory) JsonPackage.eINSTANCE.getEFactoryInstance();
						Member jsonMember = jsonFact.createMember();
						jsonMember.setKey(name);

						Array array = jsonFact.createArray();
						EList<EObject> arrayValues = (EList<EObject>) array
								.eGet(array.eClass().getEStructuralFeature("values"));

						for (Boolean o : list) {
							BooleanVal boolVal = jsonFact.createBooleanVal();

							boolVal.setBooleanValue(o);
							arrayValues.add(boolVal);
						}
						jsonMember.setValue(array);
						members.add((EObject) jsonMember);
					}

				}

			} else {
				// Error!!!!!
			}
		}

		return jsonObj;
	}

}
