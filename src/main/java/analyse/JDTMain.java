package analyse;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.internal.compiler.CompilationResult;

public class JDTMain {

	public static void main(String[] args) {
        
        ASTParser parser = ASTParser.newParser(AST.JLS3); 
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
//		parser.setSource("public class A { int i = 9;  \\n int j; \\n ArrayList<Integer> al = new ArrayList<Integer>();j=1000; }".toCharArray()); // set source
		parser.setSource(ClassAsString.classAsString.toCharArray()); // set source
		parser.setResolveBindings(true); // we need bindings later on
		CompilationUnit cu = (CompilationUnit) parser.createAST(null /* IProgressMonitor */); // parse
		
		
		MonVisiteur visiteur = new MonVisiteur(cu);
		cu.accept(visiteur);
		
		
		
		/*cu.accept(new ASTVisitor() {
			 
			Set names = new HashSet();
 
			public boolean visit(VariableDeclarationFragment node) {
				SimpleName name = node.getName();
				this.names.add(name.getIdentifier());
				System.out.println("Declaration of '"+name+"' at line"+cu.getLineNumber(name.getStartPosition()));
				
				MethodDeclaration md = cu.getAST().newMethodDeclaration();
				md.setName( cu.getAST().newSimpleName( "get"+name ) );
				//md.setReturnType2();
				md.setBody( cu.getAST().newBlock());
				
				return false; // do not continue to avoid usage info
			}
 
			public boolean visit(SimpleName node) {
				if (this.names.contains(node.getIdentifier())) {
				System.out.println("Usage of '" + node + "' at line " +	cu.getLineNumber(node.getStartPosition()));
				}
				return true;
			}
			
 
		});*/
	}
}
