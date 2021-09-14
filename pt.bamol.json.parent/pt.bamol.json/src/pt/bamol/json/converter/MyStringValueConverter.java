package pt.bamol.json.converter;

import org.eclipse.xtext.conversion.impl.STRINGValueConverter;
import org.eclipse.xtext.util.Strings;

import com.google.gson.Gson;

public class MyStringValueConverter extends STRINGValueConverter {
	
	
	private Gson gSon=null;
	
	public MyStringValueConverter() {
		super();
		
		gSon=new Gson();
	}
	
	@Override
	protected String toEscapedString(String value) {
		//return '"' + Strings.convertToJavaString(value, false) + '"';
		
		if ((value!=null) && (value.length()>=2) && (value.contains("'"))) {
			String result=gSon.toJson(value);
			return result;
		}
		
		return gSon.toJson(value);
	}

}
