
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.ISourceManipulation;
import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.text.edits.UndoEdit;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AnonymousClassDeclaration;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.BodyDeclaration;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.IVariableBinding;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.StructuralPropertyDescriptor;
import org.eclipse.jdt.core.dom.SuperFieldAccess;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;

import com.sun.jdi.PrimitiveType;
import com.sun.tools.corba.se.idl.constExpr.Expression;
 
public class AstVisitor {
	String filepath = new String();
	String source_path = new String();
	
	HashMap<IVariableBinding, String> oldVarVsNewVar = new HashMap();
	// TODO Currently using this index to generate name of new vars 
	// Change it to unique string generation code
	static int iVarCnt = 0;
	HashMap<String, Set<String>> expression_map;
	String current_key = null;
	boolean monitor_current_key = false;
	Set<String> rvars = new HashSet();
	AST ast = null;
	boolean modify = false;
	
	
	AstVisitor(HashMap<String, Set<String>> expression_map_, boolean modify_) {
		expression_map = expression_map_;
		modify = modify_;
		String currentDir = System.getProperty("user.dir");
		source_path = currentDir + "\\src";
		filepath = source_path + "\\test.java";
	}
	
	String get_output_filepath() {
		return filepath;
	}
	
	private FieldDeclaration createNewField(AST ast, String name, int modifiers) {
		VariableDeclarationFragment fragment= ast.newVariableDeclarationFragment();
		fragment.setName(ast.newSimpleName(name));
		FieldDeclaration declaration= ast.newFieldDeclaration(fragment);
		declaration.modifiers().addAll(ast.newModifiers(modifiers));
		return declaration;
	}
	
	public ASTNode getOuterClass(ASTNode node) {
        String str = "no";
	    do {
	        node= node.getParent();
	    } while (node != null && node.getNodeType() != ASTNode.TYPE_DECLARATION);

	     return node;
	}
	
	public ASTNode getOuterMethod(ASTNode node) {
        String str = "no";
	    do {
	        node= node.getParent();
	    } while (node != null && node.getNodeType() != ASTNode.METHOD_DECLARATION);

	     return node;
	}
	
	private String readFileToString(String filePath) throws IOException {
		StringBuilder fileData = new StringBuilder(1000);
		BufferedReader reader = new BufferedReader(new FileReader(filePath));
 
		char[] buf = new char[10];
		int numRead = 0;
		while ((numRead = reader.read(buf)) != -1) {
			//System.out.println(numRead);
			String readData = String.valueOf(buf, 0, numRead);
			fileData.append(readData);
			buf = new char[1024];
		}
 
		reader.close();
 
		return  fileData.toString();	
	}
 
	public void run() {
		ASTParser parser = ASTParser.newParser(AST.JLS8);
		try {
			parser.setKind(ASTParser.K_COMPILATION_UNIT);
			parser.setResolveBindings(true);	
			Map options = JavaCore.getOptions();
			parser.setCompilerOptions(options); 
			String unitName = "test.java";
			parser.setUnitName(unitName);
			String[] sources = { source_path }; 
			String[] classpath = {};	 
		    parser.setEnvironment(classpath, sources, new String[] { "UTF-8"}, true);	
			parser.setSource(readFileToString(filepath).toCharArray());			
			 
	    } catch(IOException ex) {
			
		}
		
		
		final CompilationUnit cu = (CompilationUnit) parser.createAST(null); 
		if(modify) {
			cu.recordModifications();
			ast = cu.getAST();
		}
		
		cu.accept(new ASTVisitor() { 
			Set names = new HashSet(); 			
			public boolean visit(Assignment node) {
				if(!modify) {
					monitor_current_key = true;				
				}
				return true;
			}
			
			public void endVisit(Assignment node) {		
				if(!modify) {
					if( expression_map.containsKey(current_key)) {
						Set<String> rvars_orig = expression_map.get(current_key);
						Iterator<String> it = rvars_orig.iterator();
						while (it.hasNext()) {
							String var = (String)it.next();
							if(!rvars.contains(var)) {
								it.remove();
							}
						}					
					}
					monitor_current_key = false;
					current_key = null;
					rvars.clear();
				}
				return; 
			}
			
			
			public void endVisit(VariableDeclarationStatement node) {	
				List fragments = node.fragments();
				Iterator iter = fragments.iterator();
				while(iter.hasNext()) { 
					VariableDeclarationFragment fragment = (VariableDeclarationFragment) iter.next();
					SimpleName name = fragment.getName();	
					if(!modify) {									
						this.names.add(name.getIdentifier());
					}
					Type type = node.getType();	
					// IVariablebinding for checking if its a field and use node.getType to check
					// if it is a primitive field
				 	IVariableBinding binding = fragment.resolveBinding();
				 	if(binding != null && !binding.isField() && node.getType().isPrimitiveType() && modify) {
				 		MethodDeclaration method = (MethodDeclaration) getOuterMethod(fragment);
				 		TypeDeclaration parent_class = (TypeDeclaration) getOuterClass(method);
				 		String new_name = name.getFullyQualifiedName() + "__" + iVarCnt;	
				 		iVarCnt++;
				 		BodyDeclaration declaration= createNewField(ast, new_name, Modifier.STATIC);
				 		oldVarVsNewVar.put(binding,  new_name);
					 	parent_class.bodyDeclarations().add(0, declaration);		
					 	Assignment initializer_exp = ast.newAssignment();
					 	//initializer_exp.setOperator(InfixExpression.Operator.EQUALS);
					 	Name left = ast.newSimpleName(new_name);
					 	Name right = ast.newSimpleName(name.toString());
					 	initializer_exp.setLeftHandSide(left);
					 	initializer_exp.setRightHandSide(right);
					 	ExpressionStatement expr = ast.newExpressionStatement(initializer_exp);
					    List stmts = method.getBody().statements();
					    int curr_index = 0;
					    Iterator iter1 = stmts.iterator();
					    // Finding index where to insert the initializer expression
					    while(iter1.hasNext()) {
					    	Object statement = iter1.next();
					    	if(statement.equals(node)) {
					    		stmts.add(++curr_index, expr);
					    	}
					    	curr_index++;
					    }
					 	
				 	}
				 	
				}
				return; 
			}
			
		
 
			public boolean visit(SimpleName node) {
				String var = node.toString() + "." + cu.getLineNumber(node.getStartPosition());
				if(oldVarVsNewVar.containsKey(node.resolveBinding()) && modify) {				
					Name new_name = ast.newName(oldVarVsNewVar.get(node.resolveBinding()));
					node.setIdentifier(new_name.toString());	
					return true;
			    }
				if(monitor_current_key && current_key == null) {					
					current_key = var;
				} else if(monitor_current_key) {
					rvars.add(var);
				}
				return true;
			}
 
		});
		if(modify) {		
			try {		
				Document document = new Document(readFileToString(filepath).toString());
				TextEdit edits = cu.rewrite(document, null);
				edits.apply(document);
				PrintWriter writer = new PrintWriter(filepath, "UTF-8");
				writer.print(document.get());
				writer.close();
			} catch(IOException ex){
			} catch(MalformedTreeException ex) {
			} catch(BadLocationException ex) {
			}
		}

	}
}