package pt.bamol.bamolm2.ui.highlighting;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.xtext.ui.editor.syntaxcoloring.IHighlightingConfigurationAcceptor;
import org.eclipse.xtext.ui.editor.syntaxcoloring.DefaultHighlightingConfiguration;
import org.eclipse.xtext.ui.editor.syntaxcoloring.IHighlightingConfiguration;
import org.eclipse.xtext.ui.editor.utils.TextStyle;

public class MySemanticHighlightingConfiguration extends DefaultHighlightingConfiguration {

	
	public static final String KEYWORD_INC_ID = "inc";
	
	public TextStyle keywordIncTextStyle() {
		TextStyle textStyle = new TextStyle();
		textStyle.setColor(new RGB(0, 0, 0));
		textStyle.setBackgroundColor(new RGB(0, 255, 0));
		textStyle.setStyle(SWT.BOLD);
		return textStyle;
		}
	
	@Override
	public void configure(IHighlightingConfigurationAcceptor acceptor) {
		// TODO Auto-generated method stub
		
		super.configure(acceptor);

		acceptor.acceptDefaultHighlighting(KEYWORD_INC_ID, "inc", keywordIncTextStyle());

	}

}
