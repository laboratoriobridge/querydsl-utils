package br.ufsc.bridge.querydsl.domain;

public class PageSpec {

	private int pageNumber;
	private int pageSize;
	private SortSpec sortSpec;

	public PageSpec(int pageNumber, int pageSize, SortSpec sortSpec) {
		super();

		if (pageNumber < 0) {
			throw new IllegalArgumentException("Page index must not be less than zero!");
		}

		if (pageSize < 1) {
			throw new IllegalArgumentException("Page size must not be less than one!");
		}

		this.pageNumber = pageNumber;
		this.pageSize = pageSize;
		this.sortSpec = sortSpec;
	}

	public int getPageNumber() {
		return this.pageNumber;
	}

	public int getPageSize() {
		return this.pageSize;
	}

	public SortSpec getSortSpec() {
		return this.sortSpec;
	}

	public int getOffset() {
		return this.pageNumber * this.pageSize;
	}

	/**
	 * Create a new {@link PageSpec} that represents the previous page relative to this one.
	 *
	 * @return The previous {@link PageSpec} or null if there is not a previous page.
	 */
	public PageSpec prev() {
		return this.pageNumber == 0 ? null : new PageSpec(this.pageNumber - 1, this.pageSize, this.sortSpec);
	}

	/**
	 * Create a new {@link PageSpec} that represents the next page relative to this one.
	 *
	 * @return The next {@link PageSpec}.
	 */
	public PageSpec next() {
		return new PageSpec(this.pageNumber + 1, this.pageSize, this.sortSpec);
	}
}
