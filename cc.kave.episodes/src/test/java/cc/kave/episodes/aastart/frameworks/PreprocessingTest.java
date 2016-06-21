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
package cc.kave.episodes.aastart.frameworks;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.ZipException;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.google.common.base.Predicate;
import com.google.common.collect.Lists;

import cc.kave.commons.model.episodes.Event;
import cc.kave.commons.model.episodes.Events;
import cc.kave.commons.model.events.completionevents.Context;
import cc.kave.commons.model.names.IMethodName;
import cc.kave.commons.model.names.csharp.MethodName;
import cc.kave.commons.model.ssts.impl.SST;
import cc.kave.commons.model.ssts.impl.blocks.DoLoop;
import cc.kave.commons.model.ssts.impl.declarations.MethodDeclaration;
import cc.kave.commons.model.ssts.impl.expressions.assignable.InvocationExpression;
import cc.kave.commons.model.ssts.impl.statements.ExpressionStatement;
import cc.kave.episodes.export.EventStreamIo;
import cc.kave.episodes.export.EventsFilter;
import cc.kave.episodes.model.EventStream;
import cc.recommenders.exceptions.AssertionException;
import cc.recommenders.io.Directory;
import cc.recommenders.io.Logger;

public class PreprocessingTest {

	@Rule
	public TemporaryFolder rootFolder = new TemporaryFolder();
	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Mock
	private Directory rootDirectory;
	@Mock
	private ReductionByRepos repos;

	private List<Event> events;
	private EventStream stream;

	private Preprocessing sut;

	@Before
	public void setup() throws IOException {
		Logger.reset();
		Logger.setCapturing(true);

		MockitoAnnotations.initMocks(this);

		events = Lists.newArrayList(ctx(1), inv(2), inv(3), ctx(4), inv(5), inv(2), ctx(1), inv(3));
		stream = new EventStream();
		stream.addEvent(ctx(1));
		stream.addEvent(inv(2));
		stream.addEvent(inv(3));
		stream.addEvent(unknown());
		stream.addEvent(inv(2));
		stream.addEvent(ctx(1));
		stream.addEvent(inv(3));
		
		sut = new Preprocessing(rootDirectory, rootFolder.getRoot(), repos);

		when(repos.select(any(Directory.class), anyInt())).thenReturn(events);
		when(EventsFilter.filterStream(any(List.class), anyInt())).thenReturn(stream);

		Logger.setPrinting(false);
	}

	@After
	public void teardown() {
		Logger.reset();
	}

	@Test
	public void contextTest() throws ZipException, IOException {
		sut.generate();;

//		verify(rootDirectory, times(2)).findFiles(anyPredicateOf(String.class));
	}

	@Test
	public void cannotBeInitializedWithNonExistingFolder() {
		thrown.expect(AssertionException.class);
		thrown.expectMessage("Contexts folder does not exist");
		sut = new Preprocessing(rootDirectory, new File("does not exist"), repos);
	}

	@Test
	public void cannotBeInitializedWithFile() throws IOException {
		File file = rootFolder.newFile("a");
		thrown.expect(AssertionException.class);
		thrown.expectMessage("Contexts is not a folder, but a file");
		sut = new Preprocessing(rootDirectory, file, repos);
	}

	@Test
	public void readTwoArchives() throws IOException {
		SST sst = new SST();
		MethodDeclaration md3 = new MethodDeclaration();
		md3.setName(MethodName.newMethodName("[T,P] [T2,P].M3()"));
		md3.getBody().add(new DoLoop());

		InvocationExpression ie3 = new InvocationExpression();
		IMethodName methodName = MethodName.newMethodName("[System.Void, mscore, 4.0.0.0] [T, P, 1.2.3.4].MI3()");
		ie3.setMethodName(methodName);

		md3.getBody().add(wrap(ie3));

		InvocationExpression ie4 = new InvocationExpression();
		methodName = MethodName.newMethodName("[System.Void, mscore, 4.0.0.0] [T, P, 1.2.3.4].MI3()");
		ie4.setMethodName(methodName);

		md3.getBody().add(wrap(ie4));

		md3.getBody().add(new ExpressionStatement());

		sst.getMethods().add(md3);
		Context context = new Context();
		context.setSST(sst);


		sut.generate();;

		verify(rootDirectory, times(2)).findFiles(anyPredicateOf(String.class));

		File streamFile = new File(getStreamPath());
		File mappingFile = new File(getMappingPath());

		String expectedStream = "2,0.500\n2,0.501\n3,1.002\n3,1.003\n";

		// ctx1 ctx2 inv1 inv2 inv2 ctx2 inv3 inv3
		String actualStream = FileUtils.readFileToString(streamFile);
		List<Event> actualMapping = EventStreamIo.readMapping(mappingFile.getAbsolutePath());
		
		assertEquals(expectedStream, actualStream);
		assertEquals(expectedMapping(), actualMapping);
		
		assertTrue(streamFile.exists());
		assertTrue(mappingFile.exists());
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

	private String getStreamPath() {
		File streamFile = new File(rootFolder.getRoot().getAbsolutePath() + "/eventStream.txt");
		return streamFile.getAbsolutePath();
	}

	private String getMappingPath() {
		File streamFile = new File(rootFolder.getRoot().getAbsolutePath() + "/eventMapping.txt");
		return streamFile.getAbsolutePath();
	}

	private List<Event> expectedMapping() {
		List<Event> events = new LinkedList<Event>();
		events.add(Events.newDummyEvent());
		events.add(Events.newUnknownEvent());
		
		String inv1 = "[System.Void, mscore, 4.0.0.0] [T, P, 1.2.3.4].MI2()";
		IMethodName methodInv1 = MethodName.newMethodName(inv1);
		Event e2 = Events.newInvocation(methodInv1);
		events.add(e2);
		
		String inv2 = "[System.Void, mscore, 4.0.0.0] [T, P, 1.2.3.4].MI3()";
		IMethodName methodInv2 = MethodName.newMethodName(inv2);
		Event e3 = Events.newInvocation(methodInv2);
		events.add(e3);
		
		return events;
	}
	
	private static Event inv(int i) {
		return Events.newInvocation(m(i));
	}

	private static Event ctx(int i) {
		return Events.newContext(m(i));
	}

	private static Event unknown() {
		return Events.newUnknownEvent();
	}

	private static IMethodName m(int i) {
		return MethodName.newMethodName("[T,P] [T,P].m" + i + "()");
	}
}