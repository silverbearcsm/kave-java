/**
 * Copyright 2016 Technische Universität Darmstadt
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cc.kave.episodes.postprocessor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import cc.kave.commons.model.naming.Names;
import cc.kave.commons.model.naming.codeelements.IMethodName;
import cc.kave.commons.model.naming.types.ITypeName;
import cc.kave.episodes.model.Episode;
import cc.kave.episodes.model.events.Events;
import cc.kave.episodes.model.events.Fact;
import cc.kave.episodes.postprocessor.EnclosingMethods;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class EnclosingMethodsTest {

	private EnclosingMethods sut0;
	private EnclosingMethods sut1;

	@Before
	public void setup() {
		sut0 = new EnclosingMethods(false);
		sut1 = new EnclosingMethods(true);
	}

	@Test
	public void defaultValues() {
		assertTrue(sut0.getOccurrences() == 0);
		assertEquals(Sets.newLinkedHashSet(), sut0.getMethodNames(5));
	}

	@Test
	public void valuesCanBeSet() throws Exception {
		Episode episode = new Episode();
		episode.addStringsOfFacts("1", "2");

		List<Fact> method = Lists.newArrayList(new Fact(1), new Fact(2));

		sut0.addMethod(episode, method, Events.newContext(m(3, 1)));
		sut1.addMethod(episode, method, Events.newContext(m(3, 1)));

		Set<IMethodName> expected = Sets.newHashSet(Events.newContext(m(3, 1))
				.getMethod());

		assertTrue(sut0.getOccurrences() == 1);
		assertTrue(sut1.getOccurrences() == 1);
		assertEquals(expected, sut0.getMethodNames(5));
	}

	@Test
	public void multipleOccurrence() throws Exception {
		Episode episode = new Episode();
		episode.addStringsOfFacts("2", "3");

		List<Fact> method = Lists.newArrayList(new Fact(1), new Fact(2),
				new Fact(2), new Fact(3), new Fact(3), new Fact(2));

		sut0.addMethod(episode, method, Events.newContext(m(3, 1)));
		sut1.addMethod(episode, method, Events.newContext(m(3, 1)));

		Set<IMethodName> expected = Sets.newHashSet(Events.newContext(m(3, 1))
				.getMethod());

		assertTrue(sut0.getOccurrences() == 2);
		assertTrue(sut1.getOccurrences() == 2);
		assertEquals(expected, sut0.getMethodNames(5));
	}

	@Test
	public void multipleMethods() throws Exception {
		Episode episode = new Episode();
		episode.addStringsOfFacts("2", "3");

		List<Fact> method1 = Lists.newArrayList(new Fact(1), new Fact(2),
				new Fact(2), new Fact(3), new Fact(3), new Fact(2));
		List<Fact> method2 = Lists.newArrayList(new Fact(4), new Fact(5),
				new Fact(3), new Fact(3), new Fact(2));

		sut0.addMethod(episode, method1, Events.newContext(m(3, 1)));
		sut0.addMethod(episode, method2, Events.newContext(m(3, 2)));

		sut1.addMethod(episode, method1, Events.newContext(m(3, 1)));
		sut1.addMethod(episode, method2, Events.newContext(m(3, 2)));

		Set<IMethodName> expected = Sets.newHashSet(Events.newContext(m(3, 1))
				.getMethod());

		assertTrue(sut0.getOccurrences() == 3);
		assertTrue(sut1.getOccurrences() == 3);
		assertEquals(expected, sut0.getMethodNames(1));
	}

	@Test
	public void dublicateMethods() throws Exception {
		Episode episode = new Episode();
		episode.addStringsOfFacts("2", "3");

		List<Fact> method1 = Lists.newArrayList(new Fact(1), new Fact(2),
				new Fact(2), new Fact(3), new Fact(3), new Fact(2));
		List<Fact> method2 = Lists.newArrayList(new Fact(1), new Fact(5),
				new Fact(3), new Fact(3), new Fact(2));

		sut0.addMethod(episode, method1, Events.newContext(m(3, 1)));
		sut0.addMethod(episode, method2, Events.newContext(m(3, 1)));

		Set<IMethodName> expected = Sets.newHashSet(Events.newContext(m(3, 1))
				.getMethod());

		assertTrue(sut0.getOccurrences() == 1);
		assertEquals(expected, sut0.getMethodNames(5));
	}

	@Test
	public void orderWithoutRelation() throws Exception {
		Episode episode = new Episode();
		episode.addStringsOfFacts("2", "3");

		List<Fact> method1 = Lists.newArrayList(new Fact(1), new Fact(2),
				new Fact(2), new Fact(3), new Fact(3), new Fact(2));
		List<Fact> method2 = Lists.newArrayList(new Fact(4), new Fact(5),
				new Fact(3), new Fact(3), new Fact(2));

		sut1.addMethod(episode, method1, Events.newContext(m(3, 1)));
		sut1.addMethod(episode, method2, Events.newContext(m(3, 2)));

		Set<IMethodName> expected = Sets.newHashSet(Events.newContext(m(3, 1))
				.getMethod(), Events.newContext(m(3, 2)).getMethod());

		assertTrue(sut1.getOccurrences() == 3);
		assertEquals(expected, sut1.getMethodNames(5));
	}

	@Test
	public void orderWithRelation() throws Exception {
		Episode episode = new Episode();
		episode.addStringsOfFacts("2", "3", "2>3");

		List<Fact> method1 = Lists.newArrayList(new Fact(1), new Fact(2),
				new Fact(2), new Fact(3), new Fact(3), new Fact(2));
		List<Fact> method2 = Lists.newArrayList(new Fact(4), new Fact(5),
				new Fact(3), new Fact(3), new Fact(2));

		sut1.addMethod(episode, method1, Events.newContext(m(3, 1)));
		sut1.addMethod(episode, method2, Events.newContext(m(3, 2)));

		Set<IMethodName> expected = Sets.newHashSet(Events.newContext(m(3, 1))
				.getMethod());

		assertTrue(sut1.getOccurrences() == 2);
		assertEquals(expected, sut1.getMethodNames(5));
	}

	@Test
	public void orderWithPartialRelation() throws Exception {
		Episode episode = new Episode();
		episode.addStringsOfFacts("2", "3", "4", "2>3");

		List<Fact> method1 = Lists.newArrayList(new Fact(1), new Fact(2),
				new Fact(4), new Fact(2), new Fact(3), new Fact(3),
				new Fact(2), new Fact(4), new Fact(3));
		List<Fact> method2 = Lists.newArrayList(new Fact(5), new Fact(4),
				new Fact(2), new Fact(3), new Fact(2), new Fact(3));

		sut1.addMethod(episode, method1, Events.newContext(m(3, 1)));
		sut1.addMethod(episode, method2, Events.newContext(m(3, 2)));

		Set<IMethodName> expected = Sets.newHashSet(Events.newContext(m(3, 1))
				.getMethod(), Events.newContext(m(3, 2)).getMethod());

		assertTrue(sut1.getOccurrences() == 3);
		assertEquals(expected, sut1.getMethodNames(5));
	}

	@Test
	public void unknownMethods() throws Exception {
		Episode episode = new Episode();
		episode.addStringsOfFacts("2", "3", "4", "2>3");

		List<Fact> method1 = Lists.newArrayList(new Fact(1), new Fact(2),
				new Fact(4), new Fact(2), new Fact(3), new Fact(3),
				new Fact(2), new Fact(4), new Fact(3));
		List<Fact> method2 = Lists.newArrayList(new Fact(5), new Fact(4),
				new Fact(2), new Fact(3), new Fact(2), new Fact(3));

		sut1.addMethod(episode, method1, Events.newContext(m(0, 0)));
		sut1.addMethod(episode, method2, Events.newContext(m(3, 2)));
		sut1.addMethod(episode, method2, Events.newContext(m(2, 0)));

		Set<IMethodName> expected = Sets.newHashSet(Events.newContext(m(0, 0))
				.getMethod(), Events.newContext(m(3, 2)).getMethod());

		assertTrue(sut1.getOccurrences() == 4);
		assertEquals(expected, sut1.getMethodNames(5));
	}

	@Test
	public void equalityDefault() {
		EnclosingMethods a = new EnclosingMethods(false);
		EnclosingMethods b = new EnclosingMethods(false);

		assertTrue(a.equals(b));
		assertTrue(a.getOccurrences() == b.getOccurrences());
		assertEquals(a.getMethodNames(5), b.getMethodNames(5));
	}

	@Test
	public void equalityReallySame() throws Exception {
		Episode episode = new Episode();
		episode.addStringsOfFacts("1", "2");

		List<Fact> method = Lists.newArrayList(new Fact(1), new Fact(2));

		EnclosingMethods a = new EnclosingMethods(false);
		a.addMethod(episode, method, Events.newContext(m(1, 2)));

		EnclosingMethods b = new EnclosingMethods(false);
		b.addMethod(episode, method, Events.newContext(m(1, 2)));

		assertTrue(a.equals(b));
		assertTrue(a.getOccurrences() == b.getOccurrences());
		assertEquals(a.getMethodNames(5), b.getMethodNames(5));
	}

	@Test
	public void differentMethods() throws Exception {
		Episode episode = new Episode();
		episode.addStringsOfFacts("2", "3");

		List<Fact> method1 = Lists.newArrayList(new Fact(1), new Fact(2),
				new Fact(3));
		List<Fact> method2 = Lists.newArrayList(new Fact(4), new Fact(2),
				new Fact(3));

		EnclosingMethods a = new EnclosingMethods(false);
		a.addMethod(episode, method1, Events.newContext(m(3, 1)));

		EnclosingMethods b = new EnclosingMethods(false);
		b.addMethod(episode, method2, Events.newContext(m(3, 2)));

		assertTrue(a.getOccurrences() == b.getOccurrences());
		assertFalse(a.equals(b));
		assertNotEquals(a.getMethodNames(5), b.getMethodNames(5));
	}

	@Test
	public void differentOcurrences() throws Exception {
		Episode episode = new Episode();
		episode.addStringsOfFacts("2", "3");

		List<Fact> method1 = Lists.newArrayList(new Fact(1), new Fact(2),
				new Fact(2), new Fact(3), new Fact(3));
		List<Fact> method2 = Lists.newArrayList(new Fact(1), new Fact(2),
				new Fact(3));

		EnclosingMethods a = new EnclosingMethods(false);
		a.addMethod(episode, method1, Events.newContext(m(1, 2)));

		EnclosingMethods b = new EnclosingMethods(false);
		b.addMethod(episode, method2, Events.newContext(m(1, 2)));

		assertTrue(a.getOccurrences() != b.getOccurrences());
		assertFalse(a.equals(b));
		assertEquals(a.getMethodNames(5), b.getMethodNames(5));
	}

	private IMethodName m(int typeNum, int methodNum) {
		if ((typeNum == 0) || (methodNum == 0)) {
			return Names.getUnknownMethod();
		} else {
			return Names.newMethod(String.format("[R,P] [%s].m%d()",
					t(typeNum), methodNum));
		}
	}

	private ITypeName t(int typeNum) {
		return Names.newType(String.format("T%d,P", typeNum));
	}
}
