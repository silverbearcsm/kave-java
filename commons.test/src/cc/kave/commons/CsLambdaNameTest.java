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

package cc.kave.commons;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import cc.kave.commons.model.names.csharp.CsLambdaName;
import cc.kave.commons.model.names.csharp.CsParameterName;
import cc.kave.commons.model.names.csharp.CsTypeName;

public class CsLambdaNameTest {

	@Test
	public void returnType() {
		CsLambdaName name = CsLambdaName.newLambdaName("[System.String, mscorlib, 4.0.0.0] ()");

		assertEquals(CsTypeName.newTypeName("System.String, mscorlib, 4.0.0.0"), name.getReturnType());
	}

	@Test
	public void withoutParameters() {
		CsLambdaName name = CsLambdaName.newLambdaName("[System.String, mscorlib, 4.0.0.0] ()");

		assertFalse(name.hasParameters());
		assertTrue(name.getParameters().isEmpty());
		assertEquals("()", name.getSignature());
	}

	@Test
	public void withParameters() {
		CsLambdaName name = CsLambdaName.newLambdaName("[System.String, mscorlib, 4.0.0.0] ([C, A] p1, [C, B] p2)");

		assertTrue(name.hasParameters());
		assertEquals(new Object[] { CsParameterName.newParameterName("[C, A] p1"),
				CsParameterName.newParameterName("[C, B] p2") }, name.getParameters().toArray());
		assertEquals("([C, A] p1, [C, B] p2)", name.getSignature());
	}
}