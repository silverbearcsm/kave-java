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
package cc.kave.episodes.model;

import static cc.recommenders.assertions.Asserts.assertTrue;

import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import com.google.common.collect.Sets;

import cc.kave.commons.model.episodes.Fact;

public class Episode {
	
	private Set<Fact> facts = Sets.newHashSet();
	private int frequency;
	
	public Set<Fact> getFacts() {
		return facts;
	}
	
	public int getFrequency() {
		return frequency;
	}
	
	public void setFrequency(int freq) {
		assertTrue(freq > 0, "Frequency should be a positive value!");
		this.frequency = freq;
	}
	
	public void addFact(Fact fact) {
		facts.add(fact);
	}

	public void addFact(String rawFact) {
		facts.add(new Fact(rawFact));
	}

	public void addStringsOfFacts(String... rawFacts) {
		for (String rawFact : rawFacts) {
			addFact(rawFact);
		}
	}

	public void addListOfFacts(List<Fact> facts) {
		for (Fact fact : facts) {
			addFact(fact);
		}
	}
	
	public boolean containsFact(Fact fact1) {
		if (facts.contains(fact1)) {
			return true;
		}
		return false;
	}

	public int getNumFacts() {
		return facts.size();
	}
	
	public int getNumEvents() {
		int numberEvents = 0;
		for (Fact fact : facts) {
			if (!fact.isRelation()) {
				numberEvents++;
			}
		}
		return numberEvents;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj);
	}
	
	public boolean equals(Episode ep) {
		if (this.frequency != ep.frequency) {
			return false;
		}
		if (this.facts.size() != ep.facts.size()) {
			return false;
		}
		for (Fact fact : ep.facts) {
			if (!this.facts.contains(fact)) {
				return false;
			}
		}
		return true;
	}
}