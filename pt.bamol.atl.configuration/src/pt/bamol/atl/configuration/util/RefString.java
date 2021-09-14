package pt.bamol.atl.configuration.util;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;

// Metaclasse: eClass
// Nome do Campo: name
// object: eObject
// valor a "converter": val ("stringValue")
public class RefString extends Ref {
	public EClass geteClass() {
		return eClass;
	}

	public String getFieldName() {
		return fieldName;
	}

	public EObject geteObject() {
		return eObject;
	}

	public String getValue() {
		return value;
	}

	private EClass eClass;
	private String fieldName;
	private EObject eObject;
	private String value;

	public RefString(EClass eClass, String fieldName, EObject eObject,
			String value) {
		this.eClass=eClass;
		this.eObject=eObject;
		this.fieldName=fieldName;
		this.value=value;
	}
}
