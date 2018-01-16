package br.ufsc.bridge.querydsl.domain;

import java.util.List;

public class SortSpec {

	private final List<OrderSpec> orders;

	public SortSpec(List<OrderSpec> orders) {
		super();
		this.orders = orders;
	}

	public List<OrderSpec> getOrders() {
		return this.orders;
	}

	public static enum DirectionSpec {
		ASC,
		DESC;
	}

	public static class OrderSpec {

		private final DirectionSpec directionSpec;
		private final String property;

		public OrderSpec(DirectionSpec directionSpec, String property) {
			super();
			this.directionSpec = directionSpec;
			this.property = property;
		}

		public DirectionSpec getDirectionSpec() {
			return this.directionSpec;
		}

		public String getProperty() {
			return this.property;
		}
	}

}
