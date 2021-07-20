package br.ufsc.bridge.querydsl.selection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.ufsc.bridge.metafy.MetaField;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.FactoryExpression;
import com.querydsl.core.types.FactoryExpressionBase;
import com.querydsl.core.types.Visitor;
import com.querydsl.core.types.dsl.SimpleExpression;

public class Select<T> implements SelectExpression<T>, FactoryExpression<T> {

	/**
	 *
	 */
	private static final long serialVersionUID = 1286449954664399314L;
	private String alias;
	private Class<T> resultType;
	private Map<MetaField<?>, SelectExpression<?>> selectionMap;

	public Select(Class<T> resultType) {
		this.resultType = resultType;
		this.selectionMap = new HashMap<>();
	}

	public Select(Class<T> resultType, String alias) {
		this(resultType);
		this.alias = alias;
	}

	public <E> void as(SimpleExpression<E> path, MetaField<E> metaField) {
		this.select(path, metaField, null);
	}

	public <E, C> void as(SimpleExpression<E> path, MetaField<C> metaField, SelectionConverter<E, C> converter) {
		this.select(path, metaField, converter);
	}

	/**
	 * This method is no longer needed.
	 * Pass the Select object directly to the query.
	 *
	 * @return
	 */
	@Deprecated
	@Override
	public Expression<T> createSelection() {
		List<Expression<?>> properties = new ArrayList<>(this.selectionMap.size());
		List<SelectionConverter<?, ?>> converters = new ArrayList<>(this.selectionMap.size());
		for (SelectExpression<?> expression : this.selectionMap.values()) {
			properties.add(expression.createSelection());
			converters.add(expression.getConverter());
		}

		Expression<?>[] array = properties.toArray(new Expression<?>[properties.size()]);

		if (this.alias != null) {
			return new QChildBean<>(this.resultType, converters, array).as(this.alias);
		} else {
			return new QBeanWithConverters<>(this.resultType, converters, array);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private MetaField<?> select(SimpleExpression<?> expression, MetaField<?> path, SelectionConverter<?, ?> converter) {
		/*
		 * Verifica se é path composto e ignora o path raiz.
		 * Ex: profissional.cbo.nome, o parent do "nome" é o "cbo", porém o parent("profissional") do "cbo" é ignorado por ser a raiz do select
		 */
		if (path.getParent() != null && !path.getParent().getType().equals(this.resultType)) {
			// Cria selection para os níveis acima
			MetaField<?> startingField = this.select(null, path.getParent(), null);

			if (expression != null) {
				// Caso uma expressão relacionada for informada, fazer o bind no selection criado para o parent
				((Select) this.selectionMap.get(startingField)).select(expression, this.truncatePath(path, startingField), converter);
			}

			return startingField;
		} else {
			if (!this.selectionMap.containsKey(path)) {
				if (expression != null) {
					// Caso uma expressão relacionada for informada, fazer um bind simples
					this.selectionMap.put(path, new SimpleSelectExpression(expression, path, converter));
				} else {
					// Caso uma expressão não for informada, criar uma selecion para os paths filhos
					this.selectionMap.put(path, new Select<>(path.getType(), path.getAlias()));
				}
			}
			return path;
		}
	}

	/*
	 * Extrai parte de um path composto.
	 * Ex: pathToTruncate = profissional.cbo.nome, startingPath = profissional.cbo, resultado = nome
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private MetaField<?> truncatePath(MetaField<?> pathToTruncate, MetaField<?> startingPath) {
		if (pathToTruncate != startingPath) {
			MetaField<?> returnPath = this.truncatePath(pathToTruncate.getParent(), startingPath);
			if (returnPath == null) {
				returnPath = new MetaField(pathToTruncate.getType(), pathToTruncate.getAlias());
			} else {
				returnPath = new MetaField(returnPath, pathToTruncate.getType(), pathToTruncate.getAlias());
			}
			return returnPath;
		}
		return null;
	}

	public class SimpleSelectExpression implements SelectExpression<Object> {

		private SimpleExpression<?> expression;
		private MetaField<?> constant;
		private SelectionConverter<?, ?> converter;

		protected SimpleSelectExpression(SimpleExpression<?> expression, MetaField<?> constant, SelectionConverter<?, ?> converter) {
			super();
			this.expression = expression;
			this.constant = constant;
			this.converter = converter;
		}

		@Override
		@SuppressWarnings("unchecked")
		public Expression<Object> createSelection() {
			return (Expression<Object>) this.expression.as(this.constant.getAlias());
		}

		@Override
		@SuppressWarnings({ "rawtypes", "unchecked" })
		public SelectionConverter getConverter() {
			return this.converter;
		}

	}

	private FactoryExpressionBase<T> qbean;

	@Override
	public <R, C> R accept(Visitor<R, C> v, C context) {
		return v.visit(this, context);
	}

	@Override
	public Class<? extends T> getType() {
		return this.resultType;
	}

	@Override
	public List<Expression<?>> getArgs() {
		if(qbean == null) {
			this.qbean = (FactoryExpressionBase<T>) this.createSelection();
		}
		return this.qbean.getArgs();
	}

	@Override
	public T newInstance(Object... args) {
		return this.qbean.newInstance(args);
	}

	@Override
	public SelectionConverter<?, T> getConverter() {
		return null;
	}

}
