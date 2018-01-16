package br.ufsc.bridge.querydsl;

import java.util.Collections;
import java.util.List;

import br.ufsc.bridge.querydsl.domain.PageResult;
import br.ufsc.bridge.querydsl.domain.PageSpec;
import br.ufsc.bridge.querydsl.domain.SortSpec;

import com.querydsl.core.Fetchable;
import com.querydsl.core.Query;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.OrderSpecifier;

public class QueryWrapper<R, Q extends Query<?> & Fetchable<R>> {

	private Q query;
	private SortMap sortMap;

	private QueryWrapper(Q query) {
		this.query = query;
		this.sortMap = new SortMap();
	}

	public static <R, Q extends Query<?> & Fetchable<R>> QueryWrapper<R, Q> wrap(Q query) {
		return new QueryWrapper<>(query);
	}

	public void addSort(String sort, Expression<?> queryPath) {
		this.sortMap.add(sort, queryPath);
	}

	public List<R> fetch(SortSpec sortSpec) {
		this.applySortSpec(sortSpec);
		return this.query.fetch();
	}

	public PageResult<R> fetchPage(PageSpec pageSpec) {
		return this.fetchPage(this.query, pageSpec);
	}

	public PageResult<R> fetchPage(Q countQuery, PageSpec pageSpec) {
		Long total = countQuery.fetchCount();
		List<R> content = Collections.emptyList();

		if (total.equals(0L)) {
			return new PageResult<>(content, pageSpec, total);
		}

		if (pageSpec == null || total > pageSpec.getOffset()) {
			this.applyPageSpec(pageSpec);

			content = this.query.fetch();
		}

		return new PageResult<>(content, pageSpec, total);
	}

	private void applyPageSpec(PageSpec pageSpec) {
		if (pageSpec != null) {
			this.query.offset(pageSpec.getOffset());
			this.query.limit(pageSpec.getPageSize());

			this.applySortSpec(pageSpec.getSortSpec());
		}
	}

	private void applySortSpec(SortSpec sortSpec) {
		if (sortSpec != null && this.sortMap != null) {
			List<OrderSpecifier<?>> orderSpecifiers = this.sortMap.parseSortSpec(sortSpec);
			this.query.orderBy(orderSpecifiers.toArray(new OrderSpecifier[orderSpecifiers.size()]));
		}
	}

}
