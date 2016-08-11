/**
 * Copyright 2016 Technische Universität Darmstadt
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package cc.kave.commons.model.naming.impl.v0.idecomponents;

import cc.kave.commons.model.naming.idecomponents.IDocumentName;
import cc.kave.commons.model.naming.impl.v0.BaseName;

public class DocumentName extends BaseName implements IDocumentName {

	public DocumentName(String identifier) {
		super(identifier);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getLanguage() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getFileName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isUnknown() {
		// TODO Auto-generated method stub
		return false;
	}
}