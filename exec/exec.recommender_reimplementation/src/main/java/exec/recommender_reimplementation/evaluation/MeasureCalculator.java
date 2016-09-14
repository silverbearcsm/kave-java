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

import java.util.List;
import java.util.Set;

import cc.recommenders.datastructures.Tuple;
import cc.recommenders.names.ICoReMethodName;

public interface MeasureCalculator {

	public void addValue(ICoReMethodName expectedMethod, Set<Tuple<ICoReMethodName, Double>> proposals);

	public void addValue(String expectedRaychevMethod, List<String> proposals);

	public double getMean();

	public String getName();
}
