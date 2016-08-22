/**
 * Copyright 2016 Technische Universität Darmstadt
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
package exec.recommender_reimplementation.raychev_analysis;

import static cc.kave.commons.model.ssts.impl.SSTUtil.assign;
import static cc.kave.commons.model.ssts.impl.SSTUtil.declareMethod;
import static cc.kave.commons.model.ssts.impl.SSTUtil.expr;
import static cc.kave.commons.model.ssts.impl.SSTUtil.variableReference;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Joiner;

import cc.kave.commons.model.names.IMethodName;
import cc.kave.commons.model.names.IParameterName;
import cc.kave.commons.model.names.ITypeName;
import cc.kave.commons.model.names.csharp.MethodName;
import cc.kave.commons.model.names.csharp.TypeName;
import cc.kave.commons.model.ssts.ISST;
import cc.kave.commons.model.ssts.declarations.IMethodDeclaration;
import cc.kave.commons.model.ssts.impl.SST;
import cc.kave.commons.model.ssts.impl.expressions.assignable.CompletionExpression;

public class RaychevQueryTransformerTest {
	protected RaychevQueryTransformer sut;

	@Before
	public void setUp() {
		sut = new RaychevQueryTransformer();
	}

	@Test
	public void containsCompletionExpressionTest() {
		IMethodDeclaration methodDecl = declareMethod(method(type("ReturnType"), type("T1"), "m1"), true,
				expr(new CompletionExpression()));
		Assert.assertTrue(sut.containsCompletionExpression(methodDecl));
	}

	@Test
	public void transformsIntoQueryTest() {
		SST sst = new SST();
		IMethodDeclaration methodDecl = declareMethod(method(type("ReturnType"), type("T1"), "m1"), true,
				assign(variableReference("foo"), new CompletionExpression()));
		sst.getMethods().add(methodDecl);
		sst.setEnclosingType(type("T1"));

		SST expected = new SST();
		IMethodDeclaration expectedMethodDecl = declareMethod(
				method(type("ReturnType"), type("com.example.fill.Query_T1"), "test"), true,
				expr(new CompletionExpression()));
		expected.getMethods().add(expectedMethodDecl);
		expected.getMethods().add(methodDecl);
		expected.setEnclosingType(type("com.example.fill.Query_T1"));

		ISST actual = sut.transfromIntoQuery(sst);

		Assert.assertEquals(expected, actual);
	}

	protected ITypeName type(String simpleName) {
		return TypeName.newTypeName(simpleName + ",P1");
	}

	protected static IMethodName method(ITypeName returnType, ITypeName declType, String simpleName,
			IParameterName... parameters) {
		String parameterStr = Joiner.on(", ")
				.join(Arrays.asList(parameters).stream().map(p -> p.getIdentifier()).toArray());
		String methodIdentifier = String.format("[%1$s] [%2$s].%3$s(%4$s)", returnType, declType, simpleName,
				parameterStr);
		return MethodName.newMethodName(methodIdentifier);
	}
}