package cc.kave.commons.model.ssts.impl.declarations;

import cc.kave.commons.model.names.DelegateTypeName;
import cc.kave.commons.model.names.TypeName;
import cc.kave.commons.model.names.csharp.CsDelegateTypeName;
import cc.kave.commons.model.ssts.declarations.IDelegateDeclaration;
import cc.kave.commons.model.ssts.visitor.ISSTNodeVisitor;

public class DelegateDeclaration implements IDelegateDeclaration {

	private DelegateTypeName name;

	public DelegateDeclaration() {
		this.name = CsDelegateTypeName.UNKNOWN_NAME;
	}

	@Override
	public TypeName getName() {
		return this.name;
	}

	public void setName(DelegateTypeName name) {
		this.name = name;
	}

	private boolean equals(DelegateDeclaration other) {
		return this.name.equals(other.getName());
	}

	public boolean equals(Object obj) {
		return obj instanceof DelegateDeclaration ? this.equals((DelegateDeclaration) obj) : false;
	}

	public int hashCode() {
		return 23 + this.name.hashCode();
	}

	@Override
	public <TContext, TReturn> TReturn accept(ISSTNodeVisitor<TContext, TReturn> visitor, TContext context) {
		return visitor.visit(this, context);
	}

}
