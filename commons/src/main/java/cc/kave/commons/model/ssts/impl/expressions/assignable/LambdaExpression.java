package cc.kave.commons.model.ssts.impl.expressions.assignable;

import java.util.ArrayList;
import java.util.List;

import cc.kave.commons.model.names.LambdaName;
import cc.kave.commons.model.names.csharp.CsLambdaName;
import cc.kave.commons.model.ssts.IStatement;
import cc.kave.commons.model.ssts.expressions.assignable.ILambdaExpression;
import cc.kave.commons.model.ssts.visitor.ISSTNodeVisitor;

public class LambdaExpression implements ILambdaExpression {

	private List<IStatement> body;
	private LambdaName name;

	public LambdaExpression() {
		this.body = new ArrayList<>();
		this.name = CsLambdaName.UNKNOWN_NAME;
	}

	@Override
	public List<IStatement> getBody() {
		return this.body;
	}

	public void setBody(List<IStatement> body) {
		this.body = body;
	}

	public void setName(LambdaName name) {
		this.name = name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((body == null) ? 0 : body.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof LambdaExpression))
			return false;
		LambdaExpression other = (LambdaExpression) obj;
		if (body == null) {
			if (other.body != null)
				return false;
		} else if (!body.equals(other.body))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public <TContext, TReturn> TReturn accept(ISSTNodeVisitor<TContext, TReturn> visitor, TContext context) {
		return visitor.visit(this, context);
	}

	@Override
	public LambdaName getName() {
		return this.name;
	}

}