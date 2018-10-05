package br.ufsc.bridge.querydsl;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import br.ufsc.bridge.querydsl.domain.SortSpec;
import br.ufsc.bridge.querydsl.domain.SortSpec.DirectionSpec;
import br.ufsc.bridge.querydsl.domain.SortSpec.OrderSpec;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;

public class SortMap {

	private Map<String, Expression<?>[]> sortMap = new HashMap<>();

	public void add(String sort, Expression<?>... sortExpressions) {
		this.sortMap.put(sort, sortExpressions);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<OrderSpecifier<?>> parseSortSpec(SortSpec sortSpec) {
		List<OrderSpecifier<?>> orders = new LinkedList<>();
		for (OrderSpec orderSpec : sortSpec.getOrders()) {
			if (!this.sortMap.containsKey(orderSpec.getProperty())) {
				throw new RuntimeException(String.format("Sort option (%s) not mapped for this query", orderSpec.getProperty()));
			}

			Expression<?>[] sortExpressions = this.sortMap.get(orderSpec.getProperty());
			for (Expression<?> exp : sortExpressions) {
				orders.add(new OrderSpecifier(DirectionSpec.ASC.equals(orderSpec.getDirectionSpec()) ? Order.ASC : Order.DESC, exp));
			}
		}
		return orders;
	}

}
