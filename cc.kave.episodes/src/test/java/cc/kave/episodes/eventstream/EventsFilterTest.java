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
package cc.kave.episodes.eventstream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import cc.kave.commons.model.naming.Names;
import cc.kave.commons.model.naming.codeelements.IMethodName;
import cc.kave.episodes.model.EventStream;
import cc.kave.episodes.model.events.Event;
import cc.kave.episodes.model.events.Events;

import com.google.common.collect.Lists;

public class EventsFilterTest {

	private static final int FREQUENCY = 2;

	private List<Event> events1;
	private List<Event> events2;

	private EventStream expStream1;
	private EventStream expStream2;

	@Before
	public void setup() {
		events1 = Lists.newArrayList(firstCtx(1), enclCtx(0), inv(2), inv(3), 
									firstCtx(0), superCtx(2), enclCtx(7), inv(5), inv(0), inv(2), 
									firstCtx(1), enclCtx(6), inv(2), inv(3), 
									firstCtx(1), enclCtx(0), inv(2), inv(3),
									firstCtx(0), enclCtx(8), inv(2),
									firstCtx(1), enclCtx(6), inv(2), inv(3), 
									firstCtx(3), superCtx(4), enclCtx(0), inv(3));
		
		expStream1 = new EventStream();
		expStream1.addEvent(firstCtx(1));	// 1
		expStream1.addEvent(enclCtx(0));  	
		expStream1.addEvent(inv(2)); 		// 2
		expStream1.addEvent(inv(3)); 		// 3
		expStream1.addEvent(firstCtx(0));
		expStream1.addEvent(enclCtx(7)); 	
		expStream1.addEvent(inv(2)); 		// 2
		expStream1.addEvent(firstCtx(1));	// 1
		expStream1.addEvent(enclCtx(6));
		expStream1.addEvent(inv(2));		// 2
		expStream1.addEvent(inv(3));		// 3
		expStream1.addEvent(firstCtx(1));	// 1	
		expStream1.addEvent(enclCtx(0));
		expStream1.addEvent(inv(2));		// 2
		expStream1.addEvent(inv(3));		// 3
		expStream1.addEvent(firstCtx(0));
		expStream1.addEvent(enclCtx(8));
		expStream1.addEvent(inv(2));		// 2
		expStream1.addEvent(firstCtx(1));
		expStream1.addEvent(enclCtx(6));
		expStream1.addEvent(inv(2));
		expStream1.addEvent(inv(3));
//		expStream1.addEvent(firstCtx(3)); 	// 4
		expStream1.increaseTimeout();
		expStream1.addEvent(enclCtx(0)); 
		expStream1.addEvent(inv(3)); 		// 3
		
		events2 = Lists.newArrayList(firstCtx(1), enclCtx(0), inv(30),
				firstCtx(0), superCtx(2), enclCtx(7), inv(20), inv(5), inv(10), 
				firstCtx(1), enclCtx(6), inv(2), inv(3), 
				firstCtx(0), enclCtx(8), inv(10), inv(30),
				firstCtx(1), enclCtx(9), inv(3), inv(2), 
				firstCtx(3), superCtx(4), enclCtx(0), inv(20));
		
		expStream2 = new EventStream();
		expStream2.addEvent(firstCtx(1));	// 1	
		expStream2.addEvent(enclCtx(0));  	
		expStream2.addEvent(firstCtx(0));
		expStream2.addEvent(enclCtx(7)); 
		expStream2.addEvent(inv(20));
		expStream2.addEvent(firstCtx(1));	// 1	
		expStream2.addEvent(enclCtx(6));	
		expStream2.addEvent(inv(2));		// 2
		expStream2.addEvent(inv(3));		// 3
		expStream2.addEvent(firstCtx(0));
		expStream2.addEvent(enclCtx(8));	
		expStream2.addEvent(firstCtx(1));	// 1
		expStream2.addEvent(enclCtx(9));	
		expStream2.addEvent(inv(3));		// 3
		expStream2.addEvent(inv(2));		// 2
		expStream2.increaseTimeout();
		expStream2.addEvent(enclCtx(0));
		expStream2.addEvent(inv(20));
	}
	
	@Test
	public void emptyStream() {
		List<Event> events = Lists.newLinkedList();
		
		EventStream expected = new EventStream();
		
		EventStream actuals = EventsFilter.filterStream(events, FREQUENCY);
		
		assertTrue(expected.equals(actuals));
	}

	@Test
	public void filterStream1() {
		EventStream actuals = EventsFilter.filterStream(events1, FREQUENCY);

		assertEquals(expStream1.getStreamData(), actuals.getStreamData());
		assertEquals(expStream1.getStreamText(), actuals.getStreamText());
		assertEquals(expStream1.getMapping(), actuals.getMapping());
		assertTrue(expStream1.equals(actuals));
	}
	
	@Test
	public void filterStream2() {
		EventStream actuals = EventsFilter.filterStream(events2, FREQUENCY);

		assertEquals(expStream2.getStreamData(), actuals.getStreamData());
		assertEquals(expStream2.getStreamText(), actuals.getStreamText());
		assertTrue(actuals.getStreamData().size() == 5);
		assertTrue(expStream2.equals(actuals));
	}

	private static Event inv(int i) {
		return Events.newInvocation(m(i));
	}

	private static Event firstCtx(int i) {
		return Events.newFirstContext(m(i));
	}

	private static Event superCtx(int i) {
		return Events.newSuperContext(m(i));
	}

	private static Event enclCtx(int i) {
		return Events.newContext(m(i));
	}

	private static IMethodName m(int i) {
		if (i == 0) {
			return Names.getUnknownMethod();
		} else if (i == 10) {
			return Names.newMethod("[T,P] [T,P].m()");
		} else if (i == 20) {
			return Names.newMethod("[mscorlib,P, 4.0.0.0] [mscorlib,P, 4.0.0.0].m()");
		} else if (i == 30) {
			return Names.newMethod("[mscorlib,P] [mscorlib,P].m()");
		} else {
			return Names.newMethod("[T,P, 1.2.3.4] [T,P, 1.2.3.4].m" + i + "()");
		}
	}
}