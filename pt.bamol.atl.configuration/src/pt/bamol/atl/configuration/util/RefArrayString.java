package pt.bamol.atl.configuration.util;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;

// Metaclasse: eClass
// Nome do Campo: name
// object: eObject
// valores a "converter": values ("stringValue")
public class RefArrayString extends Ref {

	public EClass geteClass() {
		return eClass;
	}

	public String getFieldName() {
		return fieldName;
	}

	public EObject geteObject() {
		return eObject;
	}

	public EList<pt.bamol.json.json.Value> getValues() {
		return values;
	}

	private EClass eClass;
	private String fieldName;
	private EObject eObject;
	private EList<pt.bamol.json.json.Value> values;

	public RefArrayString(EClass eClass, String fieldName, EObject eObject,
			EList<pt.bamol.json.json.Value> values) {
		this.eClass=eClass;
		this.eObject=eObject;
		this.fieldName=fieldName;
		this.values=values;
	}
}