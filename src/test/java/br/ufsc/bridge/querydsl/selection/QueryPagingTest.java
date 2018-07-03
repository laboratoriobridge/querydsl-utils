package br.ufsc.bridge.querydsl.selection;

import java.util.ArrayList;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import br.ufsc.bridge.querydsl.QueryWrapper;
import br.ufsc.bridge.querydsl.domain.PageResult;
import br.ufsc.bridge.querydsl.domain.PageSpec;
import br.ufsc.bridge.querydsl.domain.SortSpec;
import br.ufsc.bridge.querydsl.domain.SortSpec.DirectionSpec;
import br.ufsc.bridge.querydsl.domain.SortSpec.OrderSpec;
import br.ufsc.bridge.querydsl.test.JPAHibernateTest;
import br.ufsc.bridge.querydsl.test.dtos.AuthorDto;
import br.ufsc.bridge.querydsl.test.dtos.MAuthorDto;
import br.ufsc.bridge.querydsl.test.entities.QAuthor;

import com.querydsl.jpa.impl.JPAQuery;

public class QueryPagingTest extends JPAHibernateTest {

	@Test
	public void fetchPage() {
		QAuthor author = QAuthor.author;
		MAuthorDto meta = MAuthorDto.meta;

		Select<AuthorDto> select = new Select<>(AuthorDto.class);
		select.as(author.id, meta.id);
		select.as(author.name, meta.name);

		JPAQuery<AuthorDto> query = new JPAQuery<>(em);
		query.from(author);
		query.select(select);

		QueryWrapper<AuthorDto, JPAQuery<AuthorDto>> wrapper = QueryWrapper.wrap(query);

		wrapper.mapSort("nome", author.name);

		List<OrderSpec> orders = new ArrayList<>();

		orders.add(new OrderSpec(DirectionSpec.ASC, "nome"));

		PageSpec pageSpec = new PageSpec(0, 1, new SortSpec(orders));

		PageResult<AuthorDto> result = wrapper.fetchPage(pageSpec);

		Assertions.assertThat(result.getTotalPages()).isEqualTo(2);
		Assertions.assertThat(result.getTotal()).isEqualTo(2);
		Assertions.assertThat(result.getContent().size()).isEqualTo(1);
		Assertions.assertThat(result.getContent().get(0).getName()).isEqualTo("Friedrich Nietzsche");

		pageSpec = new PageSpec(1, 1, new SortSpec(orders));

		result = wrapper.fetchPage(pageSpec);

		Assertions.assertThat(result.getTotalPages()).isEqualTo(2);
		Assertions.assertThat(result.getTotal()).isEqualTo(2);
		Assertions.assertThat(result.getContent().size()).isEqualTo(1);
		Assertions.assertThat(result.getContent().get(0).getName()).isEqualTo("Stephen Hawking");
	}

}
