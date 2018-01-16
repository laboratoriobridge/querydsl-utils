package br.ufsc.bridge.querydsl.selection;

import com.querydsl.core.types.Expression;

public interface SelectExpression<T> {

	public Expression<T> createSelection();

	SelectionConverter<?, T> getConverter();

}