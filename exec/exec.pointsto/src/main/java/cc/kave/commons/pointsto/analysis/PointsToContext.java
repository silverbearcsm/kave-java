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
package cc.kave.commons.pointsto.analysis;

import cc.kave.commons.model.events.completionevents.Context;
import cc.kave.commons.model.ssts.impl.SST;
import cc.kave.commons.model.typeshapes.TypeShape;

public class PointsToContext extends Context {

	private PointerAnalysis pointerAnalysis;
	
	public PointsToContext() {
		this.setSST(new SST());
		this.setTypeShape(new TypeShape());
	}
	
	public PointsToContext(Context context, PointerAnalysis pointerAnalysis) {
		this.pointerAnalysis = pointerAnalysis;
		this.setSST(context.getSST());
		this.setTypeShape(context.getTypeShape());
	}
	
	public PointerAnalysis getPointerAnalysis() {
		return pointerAnalysis;
	}
}