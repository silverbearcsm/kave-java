/**
 * Copyright 2015 Simon Reuß
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package cc.kave.commons.pointsto.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Set;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

import cc.kave.commons.model.events.completionevents.Context;
import cc.kave.commons.model.names.MethodName;
import cc.kave.commons.pointsto.analysis.PointerAnalysis;
import cc.kave.commons.pointsto.analysis.SimplePointerAnalysisFactory;
import cc.kave.commons.pointsto.analysis.TypeBasedAnalysis;
import cc.kave.commons.pointsto.dummies.DummyCallsite;
import cc.kave.commons.pointsto.dummies.DummyDefinitionSite;
import cc.kave.commons.pointsto.dummies.DummyUsage;
import cc.kave.commons.pointsto.extraction.PointsToUsageExtractor;
import cc.recommenders.usages.CallSiteKind;
import cc.recommenders.usages.DefinitionSiteKind;

public class UsageExtractionTest {

	@Test
	public void testPaperTest() {
		TestSSTBuilder builder = new TestSSTBuilder();
		SimplePointerAnalysisFactory<TypeBasedAnalysis> paFactory = new SimplePointerAnalysisFactory<>(
				TypeBasedAnalysis.class);
		PointsToUsageExtractor usageExtractor = new PointsToUsageExtractor();
		List<Context> contexts = builder.createPaperTest();

		for (Context context : contexts) {
			String typeName = context.getTypeShape().getTypeHierarchy().getElement().getName();
			if (typeName.equals("A")) {
				List<DummyUsage> usages = usageExtractor.extract(paFactory.create().compute(context));

				assertEquals(3, usages.size()); // S(A), B, C
				for (DummyUsage usage : usages) {
					String usageTypeName = usage.getType().getName();

					assertEquals("entry1", usage.getMethodContext().getName());
					assertEquals("S", usage.getMethodContext().getDeclaringType().getName());
					assertEquals("S", usage.getClassContext().getName());

					if (usageTypeName.equals("S")) {
						assertEquals(DefinitionSiteKind.THIS, usage.getDefinitionSite().getKind());

						Set<DummyCallsite> callsites = usage.getAllCallsites();
						assertEquals(1, callsites.size());
						DummyCallsite callsite = callsites.iterator().next();
						assertEquals(CallSiteKind.RECEIVER, callsite.getKind());
						assertEquals("fromS", callsite.getMethod().getName());
						assertEquals("S", callsite.getMethod().getDeclaringType().getName());
					} else if (usageTypeName.equals("B")) {
						assertEquals(DefinitionSiteKind.FIELD, usage.getDefinitionSite().getKind());

						Set<DummyCallsite> callsites = usage.getAllCallsites();
						assertEquals(3, callsites.size());
						for (DummyCallsite callsite : callsites) {
							String methodName = callsite.getMethod().getName();
							if (methodName.equals("m1") || methodName.equals("m2")) {
								assertEquals(CallSiteKind.RECEIVER, callsite.getKind());
								assertEquals("B", callsite.getMethod().getDeclaringType().getName());
							} else if (methodName.equals("entry2")) {
								assertEquals(CallSiteKind.PARAMETER, callsite.getKind());
								assertEquals("C", callsite.getMethod().getDeclaringType().getName());
								assertEquals(0, callsite.getArgIndex());
							} else {
								Assert.fail();
							}
						}
					} else if (usageTypeName.equals("C")) {
						assertEquals(DefinitionSiteKind.RETURN, usage.getDefinitionSite().getKind());
						assertEquals("fromS", usage.getDefinitionSite().getMethod().getName());

						Set<DummyCallsite> callsites = usage.getAllCallsites();
						assertEquals(1, callsites.size());
						DummyCallsite callsite = callsites.iterator().next();
						assertEquals(CallSiteKind.RECEIVER, callsite.getKind());
						assertEquals("entry2", callsite.getMethod().getName());
					} else {
						Assert.fail();
					}
				}
			} else if (typeName.equals("C")) {
				List<DummyUsage> usages = usageExtractor.extract(paFactory.create().compute(context));
				
				assertEquals(3, usages.size()); // B, C, D
				for (DummyUsage usage : usages) {
					String usageTypeName = usage.getType().getName();
					
					assertEquals("C", usage.getClassContext().getName());
					
					if (usageTypeName.equals("B")) {
						assertEquals("entry2", usage.getMethodContext().getName());
						assertEquals("C", usage.getMethodContext().getDeclaringType().getName());
						assertEquals(DefinitionSiteKind.PARAM, usage.getDefinitionSite().getKind());
						assertEquals(0, usage.getDefinitionSite().getArgIndex());

						Set<DummyCallsite> callsites = usage.getAllCallsites();
						assertEquals(1, callsites.size());
						DummyCallsite callsite = callsites.iterator().next();
						assertEquals(CallSiteKind.RECEIVER, callsite.getKind());
						assertEquals("m3", callsite.getMethod().getName());
					} else if (usageTypeName.equals("C")) {
						assertEquals("entry2", usage.getMethodContext().getName());
						assertEquals(DefinitionSiteKind.THIS, usage.getDefinitionSite().getKind());

						Set<DummyCallsite> callsites = usage.getAllCallsites();
						assertEquals(1, callsites.size());
						DummyCallsite callsite = callsites.iterator().next();
						assertEquals(CallSiteKind.RECEIVER, callsite.getKind());
						assertEquals("entry3", callsite.getMethod().getName());
					} else if (usageTypeName.equals("D")) {
						assertEquals("entry3", usage.getMethodContext().getName());
						assertEquals(DefinitionSiteKind.NEW, usage.getDefinitionSite().getKind());

						Set<DummyCallsite> callsites = usage.getAllCallsites();
						assertEquals(2, callsites.size());
						for (DummyCallsite callsite : callsites) {
							assertEquals(CallSiteKind.RECEIVER, callsite.getKind());
							assertThat(callsite.getMethod().getName(), Matchers.isOneOf("m4", "m5"));
						}
					} else {
						Assert.fail();
					}
				}
			} else {
				Assert.fail();
			}
		}
	}

	@Test
	public void testStreamTestTypeBased() {
		TestSSTBuilder builder = new TestSSTBuilder();
		PointerAnalysis pointerAnalysis = new TypeBasedAnalysis();
		PointsToUsageExtractor usageExtractor = new PointsToUsageExtractor();
		Context context = builder.createStreamTest();

		List<DummyUsage> usages = usageExtractor.extract(pointerAnalysis.compute(context));
		for (DummyUsage usage : usages) {
			String usageTypeName = usage.getType().getName();
			String methodContextName = usage.getMethodContext().getName();
			assertEquals("CopyTo", methodContextName);

			if (usageTypeName.equals("String")) {
				assertEquals(DefinitionSiteKind.PARAM, usage.getDefinitionSite().getKind());
				assertEquals(0, usage.getDefinitionSite().getArgIndex());

				Set<DummyCallsite> callsites = usage.getAllCallsites();
				assertEquals(1, callsites.size());
				DummyCallsite callsite = callsites.iterator().next();
				assertEquals(CallSiteKind.PARAMETER, callsite.getKind());
				assertEquals(0, callsite.getArgIndex());
				MethodName method = callsite.getMethod();
				assertTrue(method.isConstructor());
				assertEquals("FileStream", method.getDeclaringType().getName());

			} else if (usageTypeName.equals("FileStream")) {
				DummyDefinitionSite definitionSite = usage.getDefinitionSite();
				assertEquals(DefinitionSiteKind.NEW, definitionSite.getKind());
				assertTrue(definitionSite.getMethod().isConstructor());
				assertEquals("FileStream", definitionSite.getMethod().getDeclaringType().getName());

				Set<DummyCallsite> callsites = usage.getAllCallsites();
				assertEquals(3, callsites.size()); // Read, Write, Close
				for (DummyCallsite callsite : callsites) {
					assertEquals(CallSiteKind.RECEIVER, callsite.getKind());
					assertThat(callsite.getMethod().getName(), Matchers.isOneOf("Read", "Write", "Close"));
				}

			} else {
				assertThat(usageTypeName, Matchers.isOneOf("Byte[]", "Int32"));
			}
		}
	}
}
