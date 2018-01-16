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

	private Map<String, Expression<?>> sortMap = new HashMap<>();

	public void add(String sort, Expression<?> queryPath) {
		this.sortMap.put(sort, queryPath);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<OrderSpecifier<?>> parseSortSpec(SortSpec sortSpec) {
		List<OrderSpecifier<?>> orders = new LinkedList<>();
		for (OrderSpec orderSpec : sortSpec.getOrders()) {
			if (!this.sortMap.containsKey(orderSpec.getProperty())) {
				throw new RuntimeException(String.format("Opção de sort (%s) não mapeada para esta consulta", orderSpec.getProperty()));
			}
			orders.add(new OrderSpecifier(DirectionSpec.ASC.equals(orderSpec.getDirectionSpec()) ? Order.ASC : Order.DESC, this.sortMap.get(orderSpec.getProperty())));
		}
		return orders;
	}

}
