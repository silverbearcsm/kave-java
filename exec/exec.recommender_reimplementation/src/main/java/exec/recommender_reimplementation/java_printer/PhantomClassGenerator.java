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

import static exec.recommender_reimplementation.java_printer.PhantomClassGeneratorUtil.addMethodDeclarationToSST;
import static exec.recommender_reimplementation.java_printer.PhantomClassGeneratorUtil.getOrCreateSST;

import java.util.Map;
import java.util.Set;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import cc.kave.commons.model.events.completionevents.Context;
import cc.kave.commons.model.naming.Names;
import cc.kave.commons.model.naming.codeelements.IMethodName;
import cc.kave.commons.model.naming.types.ITypeName;
import cc.kave.commons.model.ssts.ISST;
import cc.kave.commons.model.ssts.impl.SST;
import cc.kave.commons.model.typeshapes.IMethodHierarchy;
import cc.kave.commons.model.typeshapes.ITypeShape;
import exec.recommender_reimplementation.java_transformation.JavaTransformationVisitor;

public class PhantomClassGenerator {

	public Set<ISST> convert(Set<Context> contexts) {
		Map<ITypeName, SST> phantomClasses = Maps.newHashMap();
		addSystemObjectSST(phantomClasses);
		addSystemBooleanSST(phantomClasses);

		for (Context context : contexts) {
			addSuperMethods(context, phantomClasses);
			ISST sst = context.getSST();
			JavaTransformationVisitor javaTransformationVisitor = new JavaTransformationVisitor(sst);
			sst = javaTransformationVisitor.transform(sst);
			sst.accept(new PhantomClassVisitor(), phantomClasses);
		}

		return Sets.newHashSet(phantomClasses.values());
	}

	private void addSystemObjectSST(Map<ITypeName, SST> phantomClasses) {
		SST objectSST = new SST();
		objectSST.setEnclosingType(Names.newType("p:object"));
		phantomClasses.put(Names.newType("p:object"), objectSST);
	}

	private void addSystemBooleanSST(Map<ITypeName, SST> phantomClasses) {
		SST objectSST = new SST();
		objectSST.setEnclosingType(Names.newType("p:bool"));
		phantomClasses.put(Names.newType("p:bool"), objectSST);
	}

	public void addSuperMethods(Context context, Map<ITypeName, SST> phantomClasses) {
		ITypeShape typeShape = context.getTypeShape();
		for (IMethodHierarchy methodHierarchy : typeShape.getMethodHierarchies()) {
			IMethodName firstMethod = methodHierarchy.getFirst();
			IMethodName superMethod = methodHierarchy.getSuper();
			if (firstMethod != null) {
				SST sst = getOrCreateSST(firstMethod.getDeclaringType(), phantomClasses);
				addMethodDeclarationToSST(firstMethod, sst);
			}
			if (superMethod != null) {
				SST sst = getOrCreateSST(superMethod.getDeclaringType(), phantomClasses);
				addMethodDeclarationToSST(superMethod, sst);
			}
		}
	}

}