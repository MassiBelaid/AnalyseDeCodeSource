package analyse;


import java.lang.reflect.ParameterizedType;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclaration;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

import com.google.common.reflect.Parameter;

public class MonVisiteur extends ASTVisitor{
	Set names = new HashSet();
	CompilationUnit cu;
	
	public MonVisiteur(CompilationUnit cu) {
		this.cu = cu;
	}
	 
	public boolean visit(VariableDeclarationFragment node) {
		SimpleName name = node.getName();
		this.names.add(name.getIdentifier());
		System.out.println("Declaration of '"+name+"' at line"+cu.getLineNumber(name.getStartPosition()));
		
		
		MethodDeclaration mdSet = cu.getAST().newMethodDeclaration();
		mdSet.setName( cu.getAST().newSimpleName( "set"+name ) );
		/*ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setSource(("this."+name+" = "+name+";").toCharArray());
        parser.setKind(ASTParser.K_STATEMENTS);
        Block block = (Block) parser.createAST(null);
        mdSet.setBody(block);
		System.out.println(mdSet);*/
		
		MethodDeclaration mdGet = cu.getAST().newMethodDeclaration();
		mdGet.setName( cu.getAST().newSimpleName( "get"+name ) );
		
		if(node.getParent() instanceof FieldDeclaration) {
			FieldDeclaration declaration = (FieldDeclaration)node.getParent();
			VariableDeclaration variable = cu.getAST().newSingleVariableDeclaration();
			
			if(declaration.getType().isSimpleType()) {
				String typeSimpleName = declaration.getType().toString();
				Type type = declaration.getType();
				mdGet.setReturnType2(cu.getAST().newSimpleType(cu.getAST().newName(typeSimpleName)));
				
				variable.setStructuralProperty(SingleVariableDeclaration.TYPE_PROPERTY,cu.getAST().newSimpleType(cu.getAST().newName(typeSimpleName)));
				variable.setName(cu.getAST().newSimpleName(node.getName().toString()));
				mdSet.parameters().add(variable);
			}else {
				mdGet.setReturnType2(cu.getAST().newPrimitiveType(PrimitiveType.INT));
				variable.setStructuralProperty(SingleVariableDeclaration.TYPE_PROPERTY,cu.getAST().newPrimitiveType(PrimitiveType.INT));
				variable.setName(cu.getAST().newSimpleName(node.getName().toString()));
				mdSet.parameters().add(variable);
			}
		}
		
		Block blockGet = cu.getAST().newBlock();
		ReturnStatement retourGET = cu.getAST().newReturnStatement();
		SimpleName simName = cu.getAST().newSimpleName(node.getName().toString());
		retourGET.setExpression(simName);
		blockGet.statements().add(retourGET);
		mdGet.setBody(blockGet);
		
		
		Block blockSet = cu.getAST().newBlock();
		Assignment as = cu.getAST().newAssignment();
		FieldAccess fAcc = cu.getAST().newFieldAccess();
		fAcc.setExpression(cu.getAST().newThisExpression());
		fAcc.setName(cu.getAST().newSimpleName(node.getName().toString()));
		as.setLeftHandSide(fAcc);
		as.setOperator(Assignment.Operator.ASSIGN);
		as.setRightHandSide(cu.getAST().newSimpleName(node.getName().toString()));
		blockSet.statements().add(cu.getAST().newExpressionStatement(as));
		mdSet.setBody(blockSet);
		
		TypeDeclaration typeDec = (TypeDeclaration)cu.types().get(0);
		typeDec.bodyDeclarations().add(mdGet);
		typeDec.bodyDeclarations().add(mdSet);
		
		
		
		return false; // do not continue to avoid usage info
	}

	public boolean visit(SimpleName node) {
		if (this.names.contains(node.getIdentifier())) {
			System.out.println("Usage of '" + node + "' at line " +	cu.getLineNumber(node.getStartPosition()));
		}
		return true;
	}
	
}
