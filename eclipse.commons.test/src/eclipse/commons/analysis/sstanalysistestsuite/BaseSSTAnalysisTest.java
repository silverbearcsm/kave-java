/**
 * Copyright 2015 Waldemar Graf
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *    http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eclipse.commons.analysis.sstanalysistestsuite;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TestName;

import cc.kave.commons.model.names.FieldName;
import cc.kave.commons.model.names.MethodName;
import cc.kave.commons.model.names.TypeName;
import cc.kave.commons.model.names.csharp.CsFieldName;
import cc.kave.commons.model.names.csharp.CsMethodName;
import cc.kave.commons.model.names.csharp.CsTypeName;
import cc.kave.commons.model.ssts.IStatement;
import cc.kave.commons.model.ssts.declarations.IMethodDeclaration;
import cc.kave.commons.model.ssts.expressions.IAssignableExpression;
import cc.kave.commons.model.ssts.expressions.ISimpleExpression;
import cc.kave.commons.model.ssts.expressions.simple.IConstantValueExpression;
import cc.kave.commons.model.ssts.impl.SST;
import cc.kave.commons.model.ssts.impl.declarations.FieldDeclaration;
import cc.kave.commons.model.ssts.impl.declarations.MethodDeclaration;
import cc.kave.commons.model.ssts.impl.expressions.assignable.CompletionExpression;
import cc.kave.commons.model.ssts.impl.expressions.assignable.InvocationExpression;
import cc.kave.commons.model.ssts.impl.expressions.simple.ConstantValueExpression;
import cc.kave.commons.model.ssts.impl.references.FieldReference;
import cc.kave.commons.model.ssts.impl.references.VariableReference;
import cc.kave.commons.model.ssts.impl.statements.Assignment;
import cc.kave.commons.model.ssts.impl.statements.ExpressionStatement;
import cc.kave.commons.model.ssts.impl.statements.VariableDeclaration;
import cc.kave.commons.model.ssts.references.IAssignableReference;
import cc.kave.commons.model.ssts.references.IVariableReference;
import eclipse.commons.test.PluginAstParser;

public abstract class BaseSSTAnalysisTest {

	protected SST context;
	protected static String packageName;
	private String projectName = "testproject";

	@Rule
	public TestName name = new TestName();

	public BaseSSTAnalysisTest() {
		packageName = getClass().getSimpleName();
	}

	protected MethodDeclaration newMethodDeclaration(String identifier) {
		MethodDeclaration decl = new MethodDeclaration();
		decl.setName(CsMethodName.newMethodName(identifier));

		return decl;
	}

	protected MethodDeclaration newDefaultMethodDeclaration() {
		MethodDeclaration decl = new MethodDeclaration();
		decl.setName(newDefaultMethodName());

		return decl;
	}

	protected MethodName newDefaultMethodName() {
		String qualifiedName = packageName.toLowerCase() + "." + capitalizeString(name.getMethodName());
		String identifier = "[%void, rt.jar, 1.8] [" + qualifiedName + ", ?].method()";
		return CsMethodName.newMethodName(identifier);
	}

	protected FieldDeclaration newFieldDeclaration(String identifier) {
		FieldDeclaration decl = new FieldDeclaration();
		decl.setName(CsFieldName.newFieldName(identifier));

		return decl;
	}

	protected IConstantValueExpression newConstantValue(String v) {
		ConstantValueExpression constExpr = new ConstantValueExpression();
		constExpr.setValue(v);
		return constExpr;
	}

	protected ExpressionStatement newEmptyCompletionExpression() {
		ExpressionStatement expressionStatement = new ExpressionStatement();
		expressionStatement.setExpression(new CompletionExpression());
		return expressionStatement;
	}

	protected VariableDeclaration newVariableDeclaration(String varName, TypeName type) {
		VariableDeclaration var = new VariableDeclaration();
		var.setType(type);
		var.setReference(newVariableReference(varName));

		return var;
	}

	protected static VariableReference newVariableReference(String id) {
		VariableReference varRef = new VariableReference();
		varRef.setIdentifier(id);

		return varRef;
	}

	protected static FieldReference newFieldReference(String name, TypeName type, String target) {
		FieldReference fieldRef = new FieldReference();
		fieldRef.setFieldName(CsFieldName.newFieldName(newMemberName(name, type)));
		fieldRef.setReference(newVariableReference(target));

		return fieldRef;
	}

	protected static FieldReference newFieldReference(FieldName name, IVariableReference declTypeRef) {
		FieldReference fieldRef = new FieldReference();
		fieldRef.setFieldName(name);
		fieldRef.setReference(declTypeRef);

		return fieldRef;
	}

	private static String newMemberName(String name, TypeName type) {
		return "[" + type.getIdentifier() + "] [" + packageName.toLowerCase() + "testproject]." + name;
	}

	protected static Assignment newAssignment(String id, IAssignableExpression expr) {
		Assignment assignment = new Assignment();
		assignment.setReference(newVariableReference(id));
		assignment.setExpression(expr);
		return assignment;
	}

	protected static Assignment newAssignment(IAssignableReference ref, IAssignableExpression expr) {
		Assignment assignment = new Assignment();
		assignment.setReference(ref);
		assignment.setExpression(expr);
		return assignment;
	}

	protected static ExpressionStatement newInvokeStatement(String id, MethodName methodName,
			ISimpleExpression... parameters) {
		ExpressionStatement stmt = new ExpressionStatement();
		assertThat("methodName is static", !methodName.isStatic());
		InvocationExpression invocation = new InvocationExpression();
		invocation.setReference(newVariableReference(id));
		invocation.setMethodName(methodName);
		invocation.setParameters(Arrays.asList(parameters));
		stmt.setExpression(invocation);
		return stmt;
	}

	protected static InvocationExpression newInvokeConstructor(MethodName methodName, ISimpleExpression... parameters) {
		assertThat("methodName is not a constructor", methodName.isConstructor());
		InvocationExpression invocation = new InvocationExpression();
		invocation.setMethodName(methodName);
		invocation.setParameters(Arrays.asList(parameters));
		return invocation;
	}

	protected IMethodDeclaration getFirstMethod() {
		return context.getMethods().iterator().next();
	}

	protected IStatement getFirstStatement() {
		return getFirstMethod().getBody().get(0);
	}

	protected void assertMethod(IStatement... stmt) {
		MethodDeclaration expected = newDefaultMethodDeclaration();
		expected.setBody(Arrays.asList(stmt));

		IMethodDeclaration actual = getFirstMethod();

		assertEquals("Different amount of statements", expected.getBody().size(), actual.getBody().size());
		assertEquals(expected, actual);
	}

	protected TypeName getDeclaringType() {
		return CsTypeName.newTypeName(packageName.toLowerCase() + "." + capitalizeString(name.getMethodName() + ", ?"));
	}

	protected <Decl> List<Decl> newList(Decl... item) {
		List<Decl> list = new ArrayList<Decl>();

		for (Decl decl : item) {
			list.add(decl);
		}

		return list;
	}

	protected <Decl> Set<Decl> newSet(Decl... item) {
		Set<Decl> set = new HashSet<Decl>();

		for (Decl decl : item) {
			set.add(decl);
		}

		return set;
	}

	/*
	 * Has to be called for every new test class. Testcases need to have the
	 * name of the tested compilationunit and the testclass must be named after
	 * the package of the compilationunits.
	 */
	@Before
	public void updateContext() {
		String cu = capitalizeString(name.getMethodName());
		String qualifiedName = packageName.toLowerCase() + ";" + cu + ".java";

		PluginAstParser parser = new PluginAstParser(projectName, qualifiedName);
		context = parser.getContext();
	}

	private String capitalizeString(String string) {
		return string.substring(0, 1).toUpperCase() + string.substring(1);
	}
}
