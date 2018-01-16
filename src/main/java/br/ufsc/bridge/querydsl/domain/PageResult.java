package br.ufsc.bridge.querydsl.domain;

import java.util.Iterator;
import java.util.List;

public class PageResult<T> implements Iterable<T> {

	private final List<T> content;
	private final PageSpec paginator;
	private final long total;

	public PageResult(List<T> content, PageSpec paginator, long total) {
		super();
		this.content = content;
		this.paginator = paginator;
		this.total = total;
	}

	public List<T> getContent() {
		return this.content;
	}

	public PageSpec getPaginator() {
		return this.paginator;
	}

	public long getTotal() {
		return this.total;
	}

	@Override
	public Iterator<T> iterator() {
		return this.content.iterator();
	}

	/**
	 * Check if this page content is not empty.
	 *
	 * @return True if there is a content inside this page.
	 */
	public boolean hasContent() {
		return !this.content.isEmpty();
	}

	/**
	 * Calculate the total number of pages.
	 *
	 * @return The total number of pages.
	 */
	public long getTotalPages() {
		return this.paginator.getPageSize() == 0 ? 1 : (long) Math.ceil(this.total / (double) this.paginator.getPageSize());
	}

	/**
	 * Check if there is a previous {@link PageResult}.
	 *
	 * @return True if there is a previous {@link PageResult}.
	 */
	public boolean hasPrev() {
		return this.paginator.getPageNumber() > 0;
	}

	/**
	 * Check if there is a next {@link PageResult}.
	 *
	 * @return True if there is a next {@link PageResult}.
	 */
	public boolean hasNext() {
		return this.getPaginator().getPageNumber() + 1 < this.getTotalPages();
	}

	/**
	 * Check if this is the first page of the result set.
	 *
	 * @return True if this is the first page.
	 */
	public boolean isFirst() {
		return !this.hasPrev();
	}

	/**
	 * Check if this is the last page of the result set.
	 *
	 * @return True if this is the last page.
	 */
	public boolean isLast() {
		return !this.hasNext();
	}
}
