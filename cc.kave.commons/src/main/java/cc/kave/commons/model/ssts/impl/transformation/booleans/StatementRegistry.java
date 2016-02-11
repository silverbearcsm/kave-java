/**
 * Copyright 2016 Carina Oberle
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
package cc.kave.commons.model.ssts.impl.transformation.booleans;

import java.util.ArrayList;
import java.util.List;

import cc.kave.commons.model.ssts.IStatement;

public class StatementRegistry {
	private ArrayList<IStatement> statements;

	public StatementRegistry() {
		statements = new ArrayList<IStatement>();
	}
	
	public void add(IStatement s) {
		statements.add(s);
	}
	
	public void addAll(List<IStatement> statements) {
		this.statements.addAll(statements);
	}

	public List<IStatement> clearContent() {
		List<IStatement> content = new ArrayList<IStatement>();
		content.addAll(statements);
		statements.clear();
		return content;
	}

}