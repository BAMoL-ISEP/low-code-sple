package pt.bamol.json.converter;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.eclipse.xtext.common.services.DefaultTerminalConverters;
import org.eclipse.xtext.conversion.ValueConverter;
import org.eclipse.xtext.conversion.IValueConverter;
import org.eclipse.xtext.conversion.ValueConverterException;
import org.eclipse.xtext.nodemodel.INode;

import com.google.inject.Singleton;


/**
 * Author: ATB
 * see: http://pettergraff.blogspot.com/2009/11/xtext-valueconverter.html
 * see: http://wiki.eclipse.org/Xtext/Documentation/ValueConverter
 * see: https://www.eclipse.org/forums/index.php/t/352143/
 */
public class MyValueConverter extends DefaultTerminalConverters {
	
	
	@ValueConverter(rule = "STRING")
	public IValueConverter<String> STRING() {
		return new MyStringValueConverter();
	}	

//	@ValueConverter(rule = "EDate")
//	public IValueConverter<java.util.Date> EDate() {
//		
//		return new IValueConverter<java.util.Date>() {
//
//			@Override
//			public java.util.Date toValue(String string, INode node)
//					throws ValueConverterException {
//				try {
//					String str2=string.substring(1, string.length()-1);
//					return new java.util.Date(new SimpleDateFormat("yyyy-MM-dd").parse(str2).getTime());
//				} catch (ParseException e) {
//					throw new ValueConverterException("could not parse date", node, e);
//				}
//			}
//
//			@Override
//			public String toString(java.util.Date value)
//					throws ValueConverterException {
//				return "'"+new SimpleDateFormat("yyyy-MM-dd").format(value)+"'";
//			}
//			
//		};
//		
//	}
	
}