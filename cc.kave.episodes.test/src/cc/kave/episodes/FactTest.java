/**
 * Copyright 2014 Technische Universität Darmstadt
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
package cc.kave.episodes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;

import cc.kave.episodes.Fact;
import cc.recommenders.exceptions.AssertionException;

public class FactTest {

	@Test
	public void defaultConstructor() {
		Fact sut = new Fact();
		assertEquals(null, sut.getRawFact());
	}

	@Test
	public void specialConstructor() {
		Fact sut = new Fact("f");
		assertEquals("f", sut.getRawFact());
	}

	@Test(expected = AssertionException.class)
	public void specialConstructorHandlesNull() {
		new Fact(null);
	}

	@Test
	public void equalityDefault() {
		Fact a = new Fact();
		Fact b = new Fact();
		assertEquals(a, b);
		assertEquals(a.hashCode(), b.hashCode());
	}

	@Test
	public void equalitySpecialCtor() {
		Fact a = new Fact("a");
		Fact b = new Fact("a");
		assertEquals(a, b);
		assertEquals(a.hashCode(), b.hashCode());
	}

	@Test
	public void equalityDifferentRawFact() {
		Fact a = new Fact("a");
		Fact b = new Fact("b");
		assertNotEquals(a, b);
		assertNotEquals(a.hashCode(), b.hashCode());
	}
}