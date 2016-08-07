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

import static org.junit.Assert.*;

import java.util.Set;

import org.hamcrest.Matchers;
import org.junit.Test;

import com.google.common.collect.Sets;

import cc.kave.commons.model.names.ITypeName;
import exec.recommender_reimplementation.java_printer.javaPrinterTestSuite.JavaPrintingVisitorBaseTest;

public class JavaPrintingUtilsTest extends JavaPrintingVisitorBaseTest {

	@Test
	public void appendImportListTest() {
		Set<ITypeName> classesList = Sets.newHashSet(
				type("Class1"), type("Assembly.Class2"), type("Assembly.Assembly2.Class3"));
		
		String[] expecteds = {
				"import Class1;",
				"import Assembly.Class2;",
				"import Assembly.Assembly2.Class3;"
		};
		
		String[] actuals = JavaPrintingUtils.appendImportListToString(classesList, new StringBuilder()).toString().split("\n");
		
		assertThat(actuals, Matchers.arrayContainingInAnyOrder(expecteds));		
	}

}
