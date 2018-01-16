package br.ufsc.bridge.querydsl.domain;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

import br.ufsc.bridge.querydsl.domain.PageSpec;

public class PageSpecTest {

	private PageSpec spec1;
	private PageSpec spec2;
	private PageSpec spec3;

	@Before
	public void setUp() {
		this.spec1 = new PageSpec(0, 10, null);
		this.spec2 = new PageSpec(1, 5, null);
		this.spec3 = new PageSpec(2, 20, null);
	}

	@Test
	public void testOffset() {
		Assertions.assertThat(this.spec1.getOffset()).isEqualTo(0);
		Assertions.assertThat(this.spec2.getOffset()).isEqualTo(5);
		Assertions.assertThat(this.spec3.getOffset()).isEqualTo(40);
	}

	@Test
	public void testPrev() {
		Assertions.assertThat(this.spec1.prev()).isNull();

		Assertions.assertThat(this.spec2.prev().getPageNumber()).isEqualTo(0);
		Assertions.assertThat(this.spec2.prev().getPageSize()).isEqualTo(this.spec2.getPageSize());
		Assertions.assertThat(this.spec2.prev().getSortSpec()).isEqualTo(this.spec2.getSortSpec());
	}

	@Test
	public void testNext() {
		Assertions.assertThat(this.spec3.next().getPageNumber()).isEqualTo(3);
		Assertions.assertThat(this.spec3.next().getPageSize()).isEqualTo(this.spec3.getPageSize());
		Assertions.assertThat(this.spec3.next().getSortSpec()).isEqualTo(this.spec3.getSortSpec());
	}
}
