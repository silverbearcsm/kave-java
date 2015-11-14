package commons.model.ssts.transformation;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import cc.kave.commons.model.ssts.ISST;
import cc.kave.commons.model.ssts.IStatement;
import cc.kave.commons.model.ssts.declarations.IFieldDeclaration;
import cc.kave.commons.model.ssts.impl.declarations.MethodDeclaration;
import cc.kave.commons.model.ssts.impl.statements.Assignment;
import cc.kave.commons.model.ssts.impl.transformation.ConstantCollectorVisitor;
import cc.kave.commons.model.ssts.references.IFieldReference;
import commons.model.ssts.impl.visitor.inlining.InliningBaseTest;

public class ConstantCollectorTest extends InliningBaseTest {
	private ConstantCollectorVisitor collector;
	private MethodDeclaration method;
	private List<IStatement> body;
	private Set<IFieldDeclaration> fields;
	private String fieldA;
	private String fieldB;
	private String fieldC;
	private IFieldReference refA;
	private IFieldReference refB;
	private IFieldReference refC;

	@Before
	public void setup() {
		collector = new ConstantCollectorVisitor();
		method = new MethodDeclaration();
		body = new ArrayList<IStatement>();
		fields = new HashSet<IFieldDeclaration>();
		fieldA = "[T1,P1,1] [System.Int32,P2,1].fA";
		fieldB = "[T1,P1,1] [System.Int32,P2,1].fB";
		fieldC = "[T1,P1,1] [Some.RefType,P2,1].fC";
		refA = refField(fieldA);
		refB = refField(fieldB);
		refC = refField(fieldC);
	}

	@Test
	public void testSimpleTypeAssigned() {
		fields = declareFields(fieldA);
		Assignment assignment = new Assignment();
		IStatement stmt = returnStatement(refExpr(refA), false);
		assignment.setReference(refA);
		body.add(assignment);
		body.add(stmt);
		method.setBody(body);

		ISST sst = buildSST(fields, method);
		Set<IFieldDeclaration> constants = sst.accept(collector, new HashSet<IFieldDeclaration>());

		assertTrue(constants.isEmpty());
	}

	@Test
	public void testRefTypeAssigned() {
		fields = declareFields(fieldC);
		Assignment assignment = new Assignment();
		IStatement stmt = returnStatement(refExpr(refC), false);
		assignment.setReference(refC);
		body.add(assignment);
		body.add(stmt);
		method.setBody(body);

		ISST sst = buildSST(fields, method);
		Set<IFieldDeclaration> constants = sst.accept(collector, new HashSet<IFieldDeclaration>());

		assertTrue(constants.isEmpty());
	}

	@Test
	public void testSimpleTypeNotAssigned() {
		fields = declareFields(fieldA);
		IStatement stmt = returnStatement(refExpr(refA), false);
		body.add(stmt);
		method.setBody(body);

		ISST sst = buildSST(fields, method);
		Set<IFieldDeclaration> constants = sst.accept(collector, new HashSet<IFieldDeclaration>());

		assertTrue(constants.size() == 1);
		assertTrue(constants.containsAll(fields));
	}

	@Test
	public void testRefTypeNotAssigned() {
		fields = declareFields(fieldC);
		IStatement stmt = returnStatement(refExpr(refC), false);
		body.add(stmt);
		method.setBody(body);
		
		ISST sst = buildSST(fields, method);
		Set<IFieldDeclaration> constants = sst.accept(collector, new HashSet<IFieldDeclaration>());

		assertTrue(constants.isEmpty());
	}

	@Test
	public void testNoneAssigned() {
		fields = declareFields(fieldA, fieldB, fieldC);
		IStatement stmt = returnStatement(refExpr(refA), false);
		body.add(stmt);
		method.setBody(body);

		ISST sst = buildSST(fields, method);
		Set<IFieldDeclaration> constants = sst.accept(collector, new HashSet<IFieldDeclaration>());

		assertTrue(constants.size() == 2);
		assertTrue(constants.containsAll(declareFields(fieldA, fieldB)));
	}

	@Test
	public void testOneAssigned() {
		Set<IFieldDeclaration> fields = declareFields(fieldA, fieldB);
		Assignment assignment = new Assignment();
		IStatement stmt = returnStatement(refExpr(refA), false);
		assignment.setReference(refA);
		body.add(assignment);
		body.add(stmt);
		method.setBody(body);

		ISST sst = buildSST(fields, method);
		Set<IFieldDeclaration> constants = sst.accept(collector, new HashSet<IFieldDeclaration>());

		assertTrue(constants.size() == 1);
		assertTrue(constants.containsAll(declareFields(fieldB)));
	}

	@Test
	public void testTwoAssigned() {
		Set<IFieldDeclaration> fields = declareFields(fieldA, fieldB);
		IStatement stmt = returnStatement(refExpr(refA), false);
		Assignment assignmentA = new Assignment();
		Assignment assignmentB = new Assignment();
		assignmentA.setReference(refA);
		assignmentB.setReference(refB);
		body.add(assignmentA);
		body.add(assignmentB);
		body.add(stmt);
		method.setBody(body);

		ISST sst = buildSST(fields, method);
		Set<IFieldDeclaration> constants = sst.accept(collector, new HashSet<IFieldDeclaration>());

		assertTrue(constants.isEmpty());
	}
}