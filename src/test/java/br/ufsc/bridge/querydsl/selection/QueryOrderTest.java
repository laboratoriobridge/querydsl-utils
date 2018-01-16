package br.ufsc.bridge.querydsl.selection;

import java.util.ArrayList;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import br.ufsc.bridge.querydsl.QueryWrapper;
import br.ufsc.bridge.querydsl.domain.SortSpec;
import br.ufsc.bridge.querydsl.domain.SortSpec.DirectionSpec;
import br.ufsc.bridge.querydsl.domain.SortSpec.OrderSpec;
import br.ufsc.bridge.querydsl.test.JPAHibernateTest;
import br.ufsc.bridge.querydsl.test.dtos.AuthorDto;
import br.ufsc.bridge.querydsl.test.dtos.MAuthorDto;
import br.ufsc.bridge.querydsl.test.entities.QAuthor;

import com.querydsl.jpa.impl.JPAQuery;

public class QueryOrderTest extends JPAHibernateTest {


	@Test
	public void orderAsc() {
		QAuthor author = QAuthor.author;
		MAuthorDto meta = MAuthorDto.meta;

		Select<AuthorDto> select = new Select<>(AuthorDto.class);
		select.as(author.id, meta.id);
		select.as(author.name, meta.name);

		JPAQuery<AuthorDto> query = new JPAQuery<>(em);
		query.from(author);
		query.select(select);

		QueryWrapper<AuthorDto, JPAQuery<AuthorDto>> wrapper = QueryWrapper.wrap(query);

		wrapper.addSort("nome", author.name);

		List<OrderSpec> orders = new ArrayList<>();

		orders.add(new OrderSpec(DirectionSpec.ASC, "nome"));

		List<AuthorDto> result = wrapper.fetch(new SortSpec(orders));

		Assertions.assertThat(result.size()).isEqualTo(2);
		Assertions.assertThat(result.get(0).getName()).isEqualTo("Friedrich Nietzsche");
		Assertions.assertThat(result.get(1).getName()).isEqualTo("Stephen Hawking");
	}

	@Test
	public void orderDesc() {
		QAuthor author = QAuthor.author;
		MAuthorDto meta = MAuthorDto.meta;

		Select<AuthorDto> select = new Select<>(AuthorDto.class);
		select.as(author.id, meta.id);
		select.as(author.name, meta.name);

		JPAQuery<AuthorDto> query = new JPAQuery<>(em);
		query.from(author);
		query.select(select);

		QueryWrapper<AuthorDto, JPAQuery<AuthorDto>> wrapper = QueryWrapper.wrap(query);

		wrapper.addSort("nome", author.name);

		List<OrderSpec> orders = new ArrayList<>();

		orders.add(new OrderSpec(DirectionSpec.DESC, "nome"));

		List<AuthorDto> result = wrapper.fetch(new SortSpec(orders));

		Assertions.assertThat(result.size()).isEqualTo(2);
		Assertions.assertThat(result.get(0).getName()).isEqualTo("Stephen Hawking");
		Assertions.assertThat(result.get(1).getName()).isEqualTo("Friedrich Nietzsche");

	}
}
