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
package cc.kave.episodes.io;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import cc.kave.commons.model.events.completionevents.Context;
import cc.kave.commons.model.naming.Names;
import cc.kave.commons.model.naming.codeelements.IMethodName;
import cc.kave.commons.model.ssts.impl.SST;
import cc.kave.commons.model.ssts.impl.blocks.DoLoop;
import cc.kave.commons.model.ssts.impl.declarations.MethodDeclaration;
import cc.kave.commons.model.ssts.impl.expressions.assignable.InvocationExpression;
import cc.kave.commons.model.ssts.impl.statements.ContinueStatement;
import cc.kave.commons.model.ssts.impl.statements.ExpressionStatement;
import cc.kave.episodes.export.EventStreamGenerator;
import cc.kave.episodes.model.events.Event;
import cc.kave.episodes.model.events.Events;
import cc.recommenders.io.Directory;
import cc.recommenders.io.Logger;
import cc.recommenders.io.ReadingArchive;

import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class ReposFoldedParserTest {

	@Mock
	private Directory rootDirectory;

	private static final String REPO1 = "Github/usr1/repo1/ws.zip";
	private static final String REPO3 = "Github/usr1/repo3/ws.zip";
	private static final String REPO2 = "Github/usr1/repo2/ws.zip";

	private Map<String, Context> data;
	private Map<String, ReadingArchive> ras;

	private Context context;
	private Context context3;
	private Context context2;

	private ReposFoldedParser sut;

	@Before
	public void setup() throws IOException {
		Logger.reset();
		Logger.setCapturing(true);

		MockitoAnnotations.initMocks(this);

		data = Maps.newLinkedHashMap();
		ras = Maps.newLinkedHashMap();
		sut = new ReposFoldedParser(rootDirectory);

		SST sst = new SST();
		MethodDeclaration md = new MethodDeclaration();
		md.setName(Names.newMethod("[T,P] [T2,P].M()"));
		md.getBody().add(new ContinueStatement());
		sst.getMethods().add(md);

		MethodDeclaration md2 = new MethodDeclaration();
		md2.setName(Names.newMethod("[T,P] [T3,P].M2()"));

		InvocationExpression ie1 = new InvocationExpression();
		IMethodName methodName = Names
				.newMethod("[System.Void, mscore, 4.0.0.0] [T, P, 1.2.3.4].MI1()");
		ie1.setMethodName(methodName);
		InvocationExpression ie2 = new InvocationExpression();
		IMethodName methodName2 = Names
				.newMethod("[System.Void, mscore, 4.0.0.0] [T, P, 1.2.3.4].MI2()");
		ie2.setMethodName(methodName2);

		md2.getBody().add(wrap(ie1));
		md2.getBody().add(wrap(ie2));
		md2.getBody().add(wrap(ie2));
		sst.getMethods().add(md2);

		context = new Context();
		context.setSST(sst);
		data.put(REPO1, context);

		SST sst3 = new SST();
		MethodDeclaration md3 = new MethodDeclaration();
		md3.setName(Names.newMethod("[T,P] [T2,P].M()"));
		InvocationExpression ie5 = new InvocationExpression();
		IMethodName methodName5 = Names
				.newMethod("[System.Void,mscore, 4.0.0.0] [T, P, 1.2.3.4].MI1()");
		ie5.setMethodName(methodName5);
		md3.getBody().add(wrap(ie5));
		sst3.getMethods().add(md3);

		context3 = new Context();
		context3.setSST(sst3);
		data.put(REPO3, context3);

		SST sst2 = new SST();
		MethodDeclaration md4 = new MethodDeclaration();
		md4.setName(Names.newMethod("[T,P] [T2,P].M3()"));
		md4.getBody().add(new DoLoop());

		InvocationExpression ie3 = new InvocationExpression();
		IMethodName methodName3 = Names
				.newMethod("[System.Void, mscore, 4.0.0.0] [T, P, 1.2.3.4].MI3()");
		ie3.setMethodName(methodName3);

		InvocationExpression ie4 = new InvocationExpression();
		methodName3 = Names
				.newMethod("[System.Void, mscore, 4.0.0.0] [T, P, 1.2.3.4].MI3()");
		ie4.setMethodName(methodName3);

		md4.getBody().add(wrap(ie3));
		md4.getBody().add(wrap(ie4));
		md4.getBody().add(new ExpressionStatement());

		sst2.getMethods().add(md4);
		context2 = new Context();
		context2.setSST(sst2);

		data.put(REPO2, context2);

		when(rootDirectory.findFiles(anyPredicateOf(String.class))).thenAnswer(
				new Answer<Set<String>>() {
					@Override
					public Set<String> answer(InvocationOnMock invocation)
							throws Throwable {
						return data.keySet();
					}
				});
		when(rootDirectory.getReadingArchive(anyString())).then(
				new Answer<ReadingArchive>() {
					@Override
					public ReadingArchive answer(InvocationOnMock invocation)
							throws Throwable {
						String file = (String) invocation.getArguments()[0];
						ReadingArchive ra = mock(ReadingArchive.class);
						ras.put(file, ra);
						when(ra.hasNext()).thenReturn(true).thenReturn(false);
						when(ra.getNext(Context.class)).thenReturn(
								data.get(file));
						return ra;
					}
				});

		Logger.setPrinting(false);
	}

	@After
	public void teardown() {
		Logger.reset();
	}

	@Test
	public void contextTest() throws ZipException, IOException {
		sut.generateFoldedEvents(2);

		verify(rootDirectory).findFiles(anyPredicateOf(String.class));
		verify(rootDirectory).getReadingArchive(REPO1);
		verify(rootDirectory).getReadingArchive(REPO3);
		verify(rootDirectory).getReadingArchive(REPO2);

		verify(ras.get(REPO1), times(2)).hasNext();
		verify(ras.get(REPO1)).getNext(Context.class);

		verify(ras.get(REPO3), times(2)).hasNext();
		verify(ras.get(REPO3)).getNext(Context.class);

		verify(ras.get(REPO2), times(2)).hasNext();
		verify(ras.get(REPO2)).getNext(Context.class);
	}

	@Test
	public void oneFold() throws IOException {
		List<List<EventStreamGenerator>> actualEvents = sut
				.generateFoldedEvents(1);

		List<List<EventStreamGenerator>> expectedEvents = Lists.newLinkedList();

		List<EventStreamGenerator> stream = Lists.newLinkedList();
		EventStreamGenerator generator = new EventStreamGenerator();
		generator.add(context);
		stream.add(generator);

		generator = new EventStreamGenerator();
		generator.add(context3);
		stream.add(generator);

		generator = new EventStreamGenerator();
		generator.add(context2);
		stream.add(generator);

		expectedEvents.add(stream);

		assertStream(expectedEvents, actualEvents);
	}
	
	@Test
	public void twoFolds() throws IOException {
		List<List<EventStreamGenerator>> actualEvents = sut
				.generateFoldedEvents(2);

		List<List<EventStreamGenerator>> expectedEvents = Lists.newLinkedList();

		List<EventStreamGenerator> stream = Lists.newLinkedList();
		EventStreamGenerator generator = new EventStreamGenerator();
		generator.add(context);
		stream.add(generator);

		generator = new EventStreamGenerator();
		generator.add(context2);
		stream.add(generator);

		expectedEvents.add(stream);
		
		stream = Lists.newLinkedList();
		generator = new EventStreamGenerator();
		generator.add(context3);
		stream.add(generator);
		expectedEvents.add(stream);

		assertStream(expectedEvents, actualEvents);
	}

	private void assertStream(List<List<EventStreamGenerator>> expected,
			List<List<EventStreamGenerator>> actuals) {
		if (expected.isEmpty() && actuals.isEmpty()) {
			assertTrue(true);
		}
		assertTrue(expected.size() == actuals.size());

		Iterator<List<EventStreamGenerator>> itEFold = expected.iterator();
		Iterator<List<EventStreamGenerator>> itAFold = actuals.iterator();
		
		while(itEFold.hasNext()) {
			List<EventStreamGenerator> expectFold = itEFold.next();
			List<EventStreamGenerator> actualFold = itAFold.next();
			
			assertTrue(expectFold.size() == actualFold.size());

			Iterator<EventStreamGenerator> itE = expectFold.iterator();
			Iterator<EventStreamGenerator> itA = actualFold.iterator();
			
			while(itE.hasNext()) {
				EventStreamGenerator expect = itE.next();
				EventStreamGenerator actual = itA.next();
				
				List<Event> expEvents = expect.getEventStream();
				List<Event> actEvents = actual.getEventStream();
				
				assertTrue(expEvents.size() == actEvents.size());
				
				assertEquals(expect.getEventStream(), actual.getEventStream());
			}
		}
	}

	private <T> Predicate<T> anyPredicateOf(Class<T> clazz) {
		@SuppressWarnings("unchecked")
		Predicate<T> p = any(Predicate.class);
		return p;
	}

	private static ExpressionStatement wrap(InvocationExpression ie1) {
		ExpressionStatement expressionStatement = new ExpressionStatement();
		expressionStatement.setExpression(ie1);
		return expressionStatement;
	}
}