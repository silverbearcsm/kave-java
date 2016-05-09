/**
 * Copyright 2016 Simon Reuß
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
package cc.kave.commons.pointsto.analysis.inclusion;

import java.util.Arrays;
import java.util.List;

import cc.kave.commons.model.names.IDelegateTypeName;
import cc.kave.commons.model.names.IMemberName;
import cc.kave.commons.model.names.IParameterName;
import cc.kave.commons.model.names.ITypeName;
import cc.kave.commons.pointsto.analysis.inclusion.allocations.AllocationSite;
import cc.kave.commons.pointsto.analysis.inclusion.allocations.ArrayEntryAllocationSite;
import cc.kave.commons.pointsto.analysis.inclusion.allocations.OutParameterAllocationSite;
import cc.kave.commons.pointsto.analysis.inclusion.allocations.UndefinedMemberAllocationSite;
import cc.kave.commons.pointsto.analysis.inclusion.annotations.ContextAnnotation;
import cc.kave.commons.pointsto.analysis.inclusion.annotations.InclusionAnnotation;

public class Allocator {

	private final ConstraintResolver constraintResolver;
	private final SetVariableFactory variableFactory;

	public Allocator(ConstraintResolver constraintResolver, SetVariableFactory variableFactory) {
		this.constraintResolver = constraintResolver;
		this.variableFactory = variableFactory;
	}

	public void allocateOutParameter(IMemberName member, IParameterName parameter, SetVariable parameterVar) {
		ITypeName type = parameter.getValueType();
		if (type.isDelegateType()) {
			allocateDelegate((IDelegateTypeName) type, parameterVar);
		} else {
			AllocationSite allocationSite = new OutParameterAllocationSite(member, parameter);
			RefTerm paramObject = new RefTerm(allocationSite, variableFactory.createObjectVariable());
			constraintResolver.addConstraint(paramObject, parameterVar, InclusionAnnotation.EMPTY,
					ContextAnnotation.EMPTY);

			if (type.isArrayType()) {
				allocateArrayEntry(allocationSite, type, parameterVar);
			}
		}
	}

	public void allocateUndefinedReturnObject(IMemberName member, SetVariable returnVar, ITypeName type) {
		AllocationSite allocationSite = new UndefinedMemberAllocationSite(member, type);
		RefTerm returnObject = new RefTerm(allocationSite, variableFactory.createObjectVariable());
		constraintResolver.addConstraint(returnObject, returnVar, InclusionAnnotation.EMPTY, ContextAnnotation.EMPTY);

		if (type.isArrayType()) {
			allocateArrayEntry(allocationSite, type, returnVar);
		}
	}

	public void allocateDelegate(IDelegateTypeName delegateType, SetVariable dest) {
		LambdaTerm lambda = createDelegateTerm(delegateType);
		constraintResolver.addConstraint(lambda, dest, InclusionAnnotation.EMPTY, ContextAnnotation.EMPTY);
	}

	private LambdaTerm createDelegateTerm(IDelegateTypeName delegateType) {
		List<IParameterName> parameters = delegateType.getParameters();
		ITypeName returnType = delegateType.getReturnType();
		int numVars = 1 + parameters.size() + (returnType.isVoidType() ? 0 : 1);
		SetVariable[] vars = new SetVariable[numVars];
		Arrays.fill(vars, ConstructedTerm.BOTTOM);
		return LambdaTerm.newMethodLambda(Arrays.asList(vars), parameters, returnType);
	}

	public void allocateArrayEntry(AllocationSite arrayAllocationSite, ITypeName arrayType, SetVariable dest) {
		// provide one initialized array entry
		ITypeName baseType = arrayType.getArrayBaseType();
		ConstructedTerm arrayEntry;
		if (baseType.isDelegateType()) {
			arrayEntry = createDelegateTerm((IDelegateTypeName) baseType);
		} else {
			arrayEntry = new RefTerm(new ArrayEntryAllocationSite(arrayAllocationSite),
					variableFactory.createObjectVariable());
		}

		SetVariable temp = variableFactory.createProjectionVariable();
		Projection projection = new Projection(RefTerm.class, RefTerm.WRITE_INDEX, temp);

		// array ⊆ proj
		constraintResolver.addConstraint(dest, projection, InclusionAnnotation.EMPTY, ContextAnnotation.EMPTY);
		// src ⊆ temp
		constraintResolver.addConstraint(arrayEntry, temp, InclusionAnnotation.EMPTY, ContextAnnotation.EMPTY);
	}
}