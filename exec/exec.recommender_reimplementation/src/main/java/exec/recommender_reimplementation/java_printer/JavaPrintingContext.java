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
package exec.recommender_reimplementation.java_printer;

import static exec.recommender_reimplementation.java_printer.PhantomClassGeneratorUtil.transformNestedType;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

import cc.kave.commons.model.naming.codeelements.IParameterName;
import cc.kave.commons.model.naming.types.ITypeName;
import cc.kave.commons.utils.sstprinter.SSTPrintingContext;

public class JavaPrintingContext extends SSTPrintingContext {
	
	static final Map<String,String> C_SHARP_TO_JAVA_KEYWORD_MAPPING =
			ImmutableMap.<String,String>builder()
			.put("lock","synchronized")
			.put("using", "try")
			.put("in", ":")
			.put("foreach", "for")
			.put("is", "instanceof")
			.build();
	

	@Override
	public SSTPrintingContext cursorPosition() {
		// ignore cursor position
		return this;
	}
	
	@Override
	public SSTPrintingContext keyword(String keyword) {
		if(C_SHARP_TO_JAVA_KEYWORD_MAPPING.containsKey(keyword))
			return text(C_SHARP_TO_JAVA_KEYWORD_MAPPING.get(keyword));
		return text(keyword);
	}
	
	@Override
	public SSTPrintingContext typeNameOnly(ITypeName typeName) {
		if (typeName != null) {
			if (typeName.isVoidType()) {
				return text("void");
			}
			if (typeName.isUnknown())
				return text("Object");
			if (typeName.isPredefined()) {
				typeName = typeName.asPredefinedTypeName().getFullType();
			}
			return text(typeName.getName());
		}
		return this;
	}
	
	@Override
	public SSTPrintingContext type(ITypeName typeName) {
		if (typeName.isNestedType()) {
			typeName = transformNestedType(typeName);
		}
		typeNameOnly(typeName);

		// ignores TypeParameters
		return this;
	}

	@Override
	public SSTPrintingContext unknownMarker() {
		return super.unknownMarker();
	}
	
	@Override
	public SSTPrintingContext parameterList(List<IParameterName> list) {
		text("(");

		for (Iterator<IParameterName> iterator = list.iterator(); iterator.hasNext();) {
			IParameterName parameter = iterator.next();
			if (parameter.isPassedByReference() && parameter.getValueType().isValueType()) {
				// ignores ref 
			}

			if (parameter.isOutput()) {
				// ignores out
			}

			if (parameter.isOptional()) {
				// could use varargs here but isn't fully compatible
			}

			if (parameter.isParameterArray()) {
				type(parameter.getValueType())
				.text("...")
				.space().text(parameter.getName());
			}
			else {
				type(parameter.getValueType()).space().text(parameter.getName());				
			}

			if (iterator.hasNext()) {
				text(",").space();
			}
		}

		text(")");

		return this;
	}
}

	