package pt.bamol.bamolm2.ui.highlighting;

import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.util.CancelIndicator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.Keyword;
import org.eclipse.xtext.ide.editor.syntaxcoloring.DefaultSemanticHighlightingCalculator;
import org.eclipse.xtext.ide.editor.syntaxcoloring.IHighlightedPositionAcceptor;
import org.eclipse.xtext.impl.KeywordImpl;

import pt.bamol.bamolm2.ModelsCommitmentsAttributes;
import pt.bamol.bamolm2.Root;
import pt.bamol.vmodel.Feature;

public class MySemanticHighlightingCalculator extends DefaultSemanticHighlightingCalculator {
	
	@Override
	public void provideHighlightingFor(XtextResource resource,
		    IHighlightedPositionAcceptor acceptor,
			CancelIndicator cancelIndicator) {
		  if (resource == null || resource.getParseResult() == null)
		    return;
		  
		  super.provideHighlightingFor(resource, acceptor, cancelIndicator);
		    
		  INode root = resource.getParseResult().getRootNode();
		  
		  for (INode node : root.getAsTreeIterable()) {
			EObject obj=node.getGrammarElement();
			
			if (obj instanceof KeywordImpl) {
				Keyword key=(Keyword) obj;
				
				if (key.getValue().compareTo("inc")==0) {
				      acceptor.addPosition(node.getOffset(), node.getLength(), 
						        MySemanticHighlightingConfiguration.KEYWORD_INC_ID);
						    					
				}
			}
			
//			if (node.hasDirectSemanticElement()) {
//				if (node.getSemanticElement() instanceof Feature) {
//				      acceptor.addPosition(node.getOffset(), node.getLength(), 
//						        MySemanticHighlightingConfiguration.KEYWORD_INC_ID);
//						    }
//			}
			
//		    if (node.getGrammarElement() instanceof ModelsCommitmentsAttributes) {
//		      acceptor.addPosition(node.getOffset(), node.getLength(), 
//		        MySemanticHighlightingConfiguration.KEYWORD_INC_ID);
//		    }
		  }
		}

}
