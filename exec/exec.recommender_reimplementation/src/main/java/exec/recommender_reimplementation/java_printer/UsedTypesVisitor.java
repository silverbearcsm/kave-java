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

import static exec.recommender_reimplementation.java_printer.PhantomClassGeneratorUtil.isJavaValueType;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.Sets;

import cc.kave.commons.model.names.IAssemblyName;
import cc.kave.commons.model.names.IMethodName;
import cc.kave.commons.model.names.IParameterName;
import cc.kave.commons.model.names.ITypeName;
import cc.kave.commons.model.ssts.ISST;
import cc.kave.commons.model.ssts.blocks.ICatchBlock;
import cc.kave.commons.model.ssts.blocks.ITryBlock;
import cc.kave.commons.model.ssts.expressions.assignable.IInvocationExpression;
import cc.kave.commons.model.ssts.impl.visitor.AbstractTraversingNodeVisitor;
import cc.kave.commons.model.ssts.references.IEventReference;
import cc.kave.commons.model.ssts.references.IFieldReference;
import cc.kave.commons.model.ssts.references.IMethodReference;
import cc.kave.commons.model.ssts.references.IPropertyReference;
import cc.kave.commons.model.ssts.references.IVariableReference;
import cc.kave.commons.model.ssts.statements.IVariableDeclaration;

public class UsedTypesVisitor extends AbstractTraversingNodeVisitor<Void, Void> {
	private ITypeName className;

	private Set<ITypeName> usedTypes;
	
	public UsedTypesVisitor() {
		usedTypes = Sets.newHashSet();
	}
	
	@Override
	public Void visit(ISST sst, Void context) {
		className = sst.getEnclosingType();
		return super.visit(sst, context);
	}

	@Override
	public Void visit(IVariableDeclaration stmt, Void context) {
		ITypeName type = stmt.getType();
		if (!isJavaValueType(type) && !type.equals(className)) {
			usedTypes.add(type);		
		}
		return super.visit(stmt, context);
	}

	@Override
	public Void visit(IInvocationExpression invocation, Void context) {
		handleMethod(invocation.getMethodName(), invocation.getReference());
		return super.visit(invocation, context);
	}

	@Override
	public Void visit(IEventReference eventRef, Void context) {
		ITypeName type = eventRef.getEventName().getDeclaringType();
		String identifier = eventRef.getReference().getIdentifier();
		if (isReferenceToOutsideClass(type, identifier)) {
			usedTypes.add(type);
		}
		usedTypes.add(eventRef.getEventName().getValueType());
		return super.visit(eventRef, context);
	}
	
	@Override
	public Void visit(IFieldReference fieldRef, Void context) {
		ITypeName type = fieldRef.getFieldName().getDeclaringType();
		String identifier = fieldRef.getReference().getIdentifier();
		if (isReferenceToOutsideClass(type, identifier)) {
			usedTypes.add(type);
		}
		usedTypes.add(fieldRef.getFieldName().getValueType());
		return super.visit(fieldRef, context);
	}
	
	@Override
	public Void visit(IMethodReference methodRef, Void context) {
		handleMethod(methodRef.getMethodName(), methodRef.getReference());
		return super.visit(methodRef, context);
	}

	@Override
	public Void visit(IPropertyReference propertyRef, Void context) {
		ITypeName type = propertyRef.getPropertyName().getDeclaringType();
		String identifier = propertyRef.getReference().getIdentifier();
		if (isReferenceToOutsideClass(type, identifier)) {
			usedTypes.add(type);
		}
		usedTypes.add(propertyRef.getPropertyName().getValueType());
		return super.visit(propertyRef, context);
	}

	@Override
	public Void visit(ITryBlock block, Void context) {
		for (ICatchBlock catchBlock : block.getCatchBlocks()) {
			usedTypes.add(catchBlock.getParameter().getValueType());
		}
		return super.visit(block, context);
	}
	
	private void addMethodParameters(List<IParameterName> parameters) {
		for (IParameterName parameterName : parameters) {
			usedTypes.add(parameterName.getValueType());
		}
	}

	private void handleMethod(IMethodName methodName, IVariableReference varRef) {
		ITypeName type = methodName.getDeclaringType();
		String identifier = varRef.getIdentifier();
		if (isReferenceToOutsideClass(type, identifier)) {
			usedTypes.add(type);
		}
		ITypeName returnType = methodName.getReturnType();
		if(!returnType.isVoidType()) {
			usedTypes.add(returnType);
		}
		addMethodParameters(methodName.getParameters());
	}

	private boolean isReferenceToOutsideClass(ITypeName type, String identifier) {
		return !identifier.equals("this") && !type.equals(className);
	}

	public Set<ITypeName> getUsedTypes() {
		return usedTypes;
	}
	
	public Set<IAssemblyName> getAssemblies() {
		return usedTypes.stream().map(ITypeName::getAssembly).collect(Collectors.toSet());
	}

}
