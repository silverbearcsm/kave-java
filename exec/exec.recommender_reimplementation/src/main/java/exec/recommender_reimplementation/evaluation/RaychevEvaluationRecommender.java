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
package exec.recommender_reimplementation.evaluation;

import static exec.recommender_reimplementation.raychev_analysis.RaychevEvaluation.getRaychevMethodName;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import cc.kave.commons.model.events.completionevents.Context;
import cc.kave.commons.model.naming.codeelements.IMethodName;
import cc.recommenders.datastructures.Tuple;
import cc.recommenders.names.ICoReMethodName;
import exec.recommender_reimplementation.raychev_analysis.RaychevRecommender;
import exec.recommender_reimplementation.util.QueryUtil;

public class RaychevEvaluationRecommender extends EvaluationRecommender {

	private RaychevRecommender raychevRecommender;

	private static final String RAYCHEV_ANALYSIS_SET = "superputty";

	private List<String> proposalsOfLastQuery;

	private IMethodName expectedMethodOfLastQuery;

	@Override
	public String getName() {
		return "Raychev";
	}

	@Override
	public void analysis(List<Context> contextList) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void initalizeRecommender() {
		raychevRecommender = new RaychevRecommender();
	}

	@Override
	public Set<Tuple<ICoReMethodName, Double>> handleQuery(QueryContext query) {
		try {
			raychevRecommender.executeRecommender(query.getQueryName(), RAYCHEV_ANALYSIS_SET, false);
			proposalsOfLastQuery = raychevRecommender.getProposals();
			expectedMethodOfLastQuery = QueryUtil.getExpectedMethodName(query.getCompletionEvent());
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void calculateMeasures(Set<Tuple<ICoReMethodName, Double>> proposals, ICoReMethodName expectedMethod) {
		for (MeasureCalculator measure : measures) {
			measure.addValue(getRaychevMethodName(expectedMethodOfLastQuery), proposalsOfLastQuery);
		}
	}

	@Override
	public boolean supportsAnalysis() {
		return false;
	}
}