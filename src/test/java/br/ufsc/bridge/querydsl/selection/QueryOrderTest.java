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
import br.ufsc.bridge.querydsl.test.entities.QBook;

import com.querydsl.core.Tuple;
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

		wrapper.mapSort("nome", author.name);

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

		wrapper.mapSort("nome", author.name);

		List<OrderSpec> orders = new ArrayList<>();

		orders.add(new OrderSpec(DirectionSpec.DESC, "nome"));

		List<AuthorDto> result = wrapper.fetch(new SortSpec(orders));

		Assertions.assertThat(result.size()).isEqualTo(2);
		Assertions.assertThat(result.get(0).getName()).isEqualTo("Stephen Hawking");
		Assertions.assertThat(result.get(1).getName()).isEqualTo("Friedrich Nietzsche");
	}

	@Test
	public void multipleOrderDesc() {
		QBook book = QBook.book;

		JPAQuery<Tuple> query = new JPAQuery<>(em);
		query.from(book);
		query.select(book.id, book.author.id);

		List<OrderSpec> orders = new ArrayList<>();
		orders.add(new OrderSpec(DirectionSpec.DESC, "sort"));

		List<Tuple> results = QueryWrapper.wrap(query)
				.mapSort("sort", book.author.name, book.title)
				.fetch(new SortSpec((orders)));

		Assertions.assertThat(results).hasSize(4);
		Assertions.assertThat(results.get(0).get(book.author.id)).isEqualTo(1);
		Assertions.assertThat(results.get(0).get(book.id)).isEqualTo(2);

		Assertions.assertThat(results.get(1).get(book.author.id)).isEqualTo(1);
		Assertions.assertThat(results.get(1).get(book.id)).isEqualTo(1);

		Assertions.assertThat(results.get(2).get(book.author.id)).isEqualTo(2);
		Assertions.assertThat(results.get(2).get(book.id)).isEqualTo(3);

		Assertions.assertThat(results.get(3).get(book.author.id)).isEqualTo(2);
		Assertions.assertThat(results.get(3).get(book.id)).isEqualTo(4);
	}

	@Test
	public void multipleOrderAsc() {
		QBook book = QBook.book;

		JPAQuery<Tuple> query = new JPAQuery<>(em);
		query.from(book);
		query.select(book.id, book.author.id);

		List<OrderSpec> orders = new ArrayList<>();
		orders.add(new OrderSpec(DirectionSpec.ASC, "sort"));

		List<Tuple> results = QueryWrapper.wrap(query)
				.mapSort("sort", book.author.name, book.title)
				.fetch(new SortSpec((orders)));

		Assertions.assertThat(results).hasSize(4);
		Assertions.assertThat(results.get(0).get(book.author.id)).isEqualTo(2);
		Assertions.assertThat(results.get(0).get(book.id)).isEqualTo(4);

		Assertions.assertThat(results.get(1).get(book.author.id)).isEqualTo(2);
		Assertions.assertThat(results.get(1).get(book.id)).isEqualTo(3);

		Assertions.assertThat(results.get(2).get(book.author.id)).isEqualTo(1);
		Assertions.assertThat(results.get(2).get(book.id)).isEqualTo(1);

		Assertions.assertThat(results.get(3).get(book.author.id)).isEqualTo(1);
		Assertions.assertThat(results.get(3).get(book.id)).isEqualTo(2);
	}
}
