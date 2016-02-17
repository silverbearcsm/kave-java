/**
 * Copyright (c) 2010, 2011 Darmstadt University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Ervina Cergani - initial API and implementation
 */
package cc.kave.episodes.mining.evaluation;

import static com.google.common.collect.Lists.newArrayList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Sets;

import cc.kave.episodes.model.PatternWithFreq;
import cc.kave.episodes.model.Query;
import cc.recommenders.datastructures.Tuple;

public class EpisodeRecommenderTest {
	
	private DecimalFormat df = new DecimalFormat("#.###");

	private Set<Tuple<PatternWithFreq, Double>> expectedProposals;
	private Set<Tuple<PatternWithFreq, Double>> actualProposals;
	private Map<Integer, List<PatternWithFreq>> learnedPatterns;
	private Map<Integer, List<PatternWithFreq>> emptyEpisodes;

	private EpisodeRecommender sut;

	@Before
	public void setup() {
		sut = new EpisodeRecommender();
		expectedProposals = Sets.newLinkedHashSet();
		actualProposals = Sets.newLinkedHashSet();
		emptyEpisodes = new HashMap<Integer, List<PatternWithFreq>>();

		learnedPatterns = new HashMap<Integer, List<PatternWithFreq>>();
		learnedPatterns.put(1, newArrayList(newPattern(3, "1"), newPattern(3, "2"), newPattern(3, "3")));
		learnedPatterns.put(2, newArrayList(newPattern(3, "4", "5", "4>5"), newPattern(2, "4", "6", "4>6")));
		learnedPatterns.put(3, newArrayList(newPattern(1, "6", "7", "8", "7>8"), newPattern(3, "10", "11", "12", "11>12")));
		learnedPatterns.put(4, newArrayList(newPattern(3, "10", "11", "12", "13")));
	}

	private PatternWithFreq newPattern(int freq, String...string) {
		PatternWithFreq pattern = new PatternWithFreq();
		pattern.setFrequency(freq);
		for (String s : string) {
			pattern.addFact(s);
		}
		return pattern;
	}

	@Test(expected=Exception.class)
	public void noLearnedEpisodes() throws Exception {
		sut.getProposals(newQuery(3, 1, "1"), emptyEpisodes, 5);
	}
	
	@Test(expected=Exception.class)
	public void noProposalsToShow() throws Exception {
		sut.getProposals(newQuery(3, 1, "1"), learnedPatterns, -1);
	}
	
	@Test
	public void queryBiggerThenEpisode() throws Exception {
		queryWith(4, "4", "5", "6", "9", "4>5", "4>6");
		
		addProposal(newPattern(1, "6", "7", "8", "7>8"), Double.valueOf(df.format(1.0 / 5.0)));
		
		assertProposals(actualProposals);
	}
	
	@Test
	public void sameProbabilityDifferentEventsNumber() throws Exception {
		queryWith(2, "10", "11");
		
		addProposal(newPattern(3, "10", "11", "12", "13"), Double.valueOf(df.format(2.0 / 3.0)));
		addProposal(newPattern(3, "10", "11", "12", "11>12"), Double.valueOf(df.format(2.0 / 3.0)));
		
		assertProposals(actualProposals);
	}
	
	@Test
	public void oneEventQuery() throws Exception {
		queryWith(1, "1");

		addProposal(newPattern(3, "1"), 1.0);

		assertProposals(actualProposals);
	}

	@Test
	public void twoEventQuery() throws Exception {

		// order relation is not counted in size
		queryWith(2, "5", "6", "5>6");

		addProposal(newPattern(3, "4", "5", "4>5"), Double.valueOf(df.format(1.0 / 3.0)));
		addProposal(newPattern(2, "4", "6", "4>6"), Double.valueOf(df.format(1.0 / 3.0)));
		addProposal(newPattern(1, "6", "7", "8", "7>8"), Double.valueOf(df.format(2.0 / 7.0)));

		assertProposals(actualProposals);
	}

	@Test
	public void threeNodeEpisode() throws Exception {

		queryWith(2, "7", "8");

		addProposal(newPattern(1, "6", "7", "8", "7>8"), Double.valueOf(df.format(2.0 / 3.0)));

		assertProposals(actualProposals);
	}

	@Test
	public void twoNodeQuery() throws Exception {

		queryWith(2, "1", "2", "1>2");

		assertProposals(actualProposals);
	}

	private Query newQuery(int frequency, int numberOfEvents, String... facts) {
		Query query = new Query();
		query.addStringsOfFacts(facts);
		return query;
	}

	private void queryWith(int numberOfEvents, String... facts) throws Exception {
		actualProposals = sut.getProposals(newQuery(1, numberOfEvents, facts), learnedPatterns, 3);
	}

	private void addProposal(PatternWithFreq e, double probability) {
		expectedProposals.add(Tuple.newTuple(e, probability));
	}

	private void assertProposals(Set<Tuple<PatternWithFreq, Double>> actualProposals) {
		if (expectedProposals.size() != actualProposals.size()) {
			System.out.println("expected\n");
			System.out.println(expectedProposals);
			System.out.println("\nbut was\n");
			System.out.println(actualProposals);
			fail();
		}
		Iterator<Tuple<PatternWithFreq, Double>> itE = expectedProposals.iterator();
		Iterator<Tuple<PatternWithFreq, Double>> itA = actualProposals.iterator();
		while (itE.hasNext()) {
			Tuple<PatternWithFreq, Double> expected = itE.next();
			Tuple<PatternWithFreq, Double> actual = itA.next();
			assertEquals(expected, actual);
		}
	}
}