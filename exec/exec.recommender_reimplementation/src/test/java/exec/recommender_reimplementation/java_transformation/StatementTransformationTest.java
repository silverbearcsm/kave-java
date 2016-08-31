package exec.recommender_reimplementation.java_transformation;

import static cc.kave.commons.model.ssts.impl.SSTUtil.assign;
import static cc.kave.commons.model.ssts.impl.SSTUtil.declare;
import static cc.kave.commons.model.ssts.impl.SSTUtil.declareMethod;
import static cc.kave.commons.model.ssts.impl.SSTUtil.refExpr;
import static cc.kave.commons.model.ssts.impl.SSTUtil.returnStatement;

import org.junit.Test;

import com.google.common.collect.Lists;

import cc.kave.commons.model.naming.Names;
import cc.kave.commons.model.ssts.declarations.IMethodDeclaration;
import cc.kave.commons.model.ssts.impl.blocks.UnsafeBlock;
import cc.kave.commons.model.ssts.impl.declarations.PropertyDeclaration;
import cc.kave.commons.model.ssts.impl.expressions.assignable.InvocationExpression;
import cc.kave.commons.model.ssts.impl.references.UnknownReference;
import cc.kave.commons.model.ssts.impl.statements.EventSubscriptionStatement;
import cc.kave.commons.model.ssts.impl.statements.ExpressionStatement;
import cc.kave.commons.model.ssts.impl.statements.GotoStatement;
import cc.kave.commons.model.ssts.impl.statements.ReturnStatement;
import cc.kave.commons.model.ssts.impl.statements.UnknownStatement;

public class StatementTransformationTest extends JavaTransformationBaseTest {

	@Test
	public void transformsTypeParameterInVariableDeclaration() {
		assertNode(declare("variable", Names.newType("T")), declare("variable", Names.newType("p:object")));
	}

	@Test
	public void testEmptyReturnStatementInMethodDeclaration() {
		IMethodDeclaration methodDecl = declareMethod(method(type("s:System.Int32"), type("Class1, P1"), "m1"), true,
				new ReturnStatement());
		assertNode(methodDecl, methodDecl(method(type("s:System.Int32"), type("Class1, P1"), "m1"),
				returnStatement(constant("null"))));
	}

	@Test
	public void testReturnStatementWithUnknownReferenceInMethodDeclaration() {
		ReturnStatement returnStatement = new ReturnStatement();
		returnStatement.setExpression(refExpr(new UnknownReference()));
		IMethodDeclaration methodDecl = declareMethod(method(type("s:System.Int32"), type("Class1, P1"), "m1"), true, returnStatement);
		assertNode(methodDecl, methodDecl(method(type("s:System.Int32"), type("Class1, P1"), "m1"), returnStatement(constant("null"))));
	}

	@Test
	public void testEmptyReturnStatementInPropertyDeclaration() {
		PropertyDeclaration propertyDecl = new PropertyDeclaration();
		propertyDecl.setName(Names.newProperty("get [s:System.Int32,P1] [DeclaringType,P1].P()"));
		propertyDecl.setGet(Lists.newArrayList(new ReturnStatement()));
		assertPropertyDeclaration(propertyDecl,
				defaultSST(methodDecl(method(type("s:System.Int32"), type("DeclaringType"), "get$P"),
						returnStatement(constant("null")))));
	}

	@Test
	public void removesGotoStatement() {
		assertAroundMethodDeclaration(Lists.newArrayList(new GotoStatement()));
	}

	@Test
	public void removesUnsafeBlock() {
		assertAroundMethodDeclaration(Lists.newArrayList(new UnsafeBlock()));
	}

	@Test
	public void removesEventSubscriptionStatement() {
		assertAroundMethodDeclaration(Lists.newArrayList(new EventSubscriptionStatement()));
	}

	@Test
	public void removesUnknownStatement() {
		assertAroundMethodDeclaration(Lists.newArrayList(new UnknownStatement()));
	}

	@Test
	public void removesExpressionStatementWithUnknownInvocation() {
		ExpressionStatement expressionStatement = new ExpressionStatement();
		expressionStatement.setExpression(new InvocationExpression());
		assertAroundMethodDeclaration(Lists.newArrayList(expressionStatement));
	}

	@Test
	public void removesAssignmentWithUnknownInvocation() {
		assertAroundMethodDeclaration(Lists.newArrayList(assign(varRef("variable"), new InvocationExpression())));
	}
}