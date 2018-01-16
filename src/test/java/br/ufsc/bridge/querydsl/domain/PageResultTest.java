package br.ufsc.bridge.querydsl.domain;

import java.util.ArrayList;
import java.util.Arrays;

import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;

import br.ufsc.bridge.querydsl.domain.PageResult;
import br.ufsc.bridge.querydsl.domain.PageSpec;

public class PageResultTest {

	private PageResult<Integer> page1;
	private PageResult<Integer> page2;
	private PageResult<Integer> page3;

	@Before
	public void setUp() {
		this.page1 = new PageResult<>(Arrays.asList(1, 2, 3), new PageSpec(0, 3, null), 8);
		this.page2 = new PageResult<>(Arrays.asList(4, 5, 6), new PageSpec(1, 3, null), 8);
		this.page3 = new PageResult<>(Arrays.asList(7, 8), new PageSpec(2, 3, null), 8);
	}

	@Test
	public void testIterable() {
		PageResult<Integer> page = new PageResult<>(Arrays.asList(1, 2, 3), new PageSpec(0, 3, null), 3);

		int i = 1;
		for (Integer number : page) {
			Assertions.assertThat(number).isEqualTo(i++);
		}
	}

	@Test
	public void testHasContent() {
		PageResult<Integer> empty = new PageResult<>(new ArrayList<Integer>(), new PageSpec(0, 3, null), 9);

		Assertions.assertThat(empty.hasContent()).isFalse();
		Assertions.assertThat(this.page1.hasContent()).isTrue();
	}

	@Test
	public void testGetTotalPages() {
		Assertions.assertThat(this.page1.getTotalPages()).isEqualTo(3);
		Assertions.assertThat(this.page3.getTotalPages()).isEqualTo(3);
	}

	@Test
	public void testHasPrev() {
		Assertions.assertThat(this.page1.hasPrev()).isFalse();
		Assertions.assertThat(this.page2.hasPrev()).isTrue();
		Assertions.assertThat(this.page3.hasPrev()).isTrue();
	}

	@Test
	public void testHasNext() {
		Assertions.assertThat(this.page1.hasNext()).isTrue();
		Assertions.assertThat(this.page2.hasNext()).isTrue();
		Assertions.assertThat(this.page3.hasNext()).isFalse();
	}

	@Test
	public void testIsFirst() {
		Assertions.assertThat(this.page1.isFirst()).isTrue();
		Assertions.assertThat(this.page2.isFirst()).isFalse();
		Assertions.assertThat(this.page3.isFirst()).isFalse();
	}

	@Test
	public void testIsLast() {
		Assertions.assertThat(this.page1.isLast()).isFalse();
		Assertions.assertThat(this.page2.isLast()).isFalse();
		Assertions.assertThat(this.page3.isLast()).isTrue();
	}
}
