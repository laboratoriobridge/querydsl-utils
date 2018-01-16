package br.ufsc.bridge.querydsl.selection;

import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import br.ufsc.bridge.querydsl.test.JPAHibernateTest;
import br.ufsc.bridge.querydsl.test.dtos.AuthorDto;
import br.ufsc.bridge.querydsl.test.dtos.MAuthorDto;
import br.ufsc.bridge.querydsl.test.entities.Author;
import br.ufsc.bridge.querydsl.test.entities.Book;
import br.ufsc.bridge.querydsl.test.entities.QAuthor;

import com.querydsl.jpa.impl.JPAQuery;

public class SelectTest extends JPAHibernateTest {

	@Test
	public void testEntitiesAssociation() {
		Book book1 = em.find(Book.class, 1);
		Assertions.assertThat(book1).isNotNull();
		Assertions.assertThat(book1.getAuthor()).isNotNull();

		Author author1 = em.find(Author.class, 1);
		Assertions.assertThat(author1).isNotNull();
		Assertions.assertThat(author1.getBooks()).isNotEmpty();
	}

	@Test
	public void testSimpleMapping() {
		QAuthor author = QAuthor.author;
		MAuthorDto meta = MAuthorDto.meta;

		Select<AuthorDto> select = new Select<>(AuthorDto.class);
		select.as(author.id, meta.id);
		select.as(author.name, meta.name);

		JPAQuery<AuthorDto> query = new JPAQuery<>(em);
		query.from(author);

		List<AuthorDto> authors = query.select(select).fetch();
		Assertions.assertThat(authors).isNotEmpty();

		AuthorDto hawking = authors.get(0);
		Assertions.assertThat(hawking.getId()).isEqualTo(1);
		Assertions.assertThat(hawking.getName()).isEqualTo("Stephen Hawking");

		AuthorDto nietzsche = authors.get(1);
		Assertions.assertThat(nietzsche.getId()).isEqualTo(2);
		Assertions.assertThat(nietzsche.getName()).isEqualTo("Friedrich Nietzsche");
	}

	@Test
	public void testMappingSubDto() {
		QAuthor author = QAuthor.author;
		MAuthorDto meta = MAuthorDto.meta;

		Select<AuthorDto> select = new Select<>(AuthorDto.class);
		select.as(author.id, meta.id);
		select.as(author.name, meta.name);
		select.as(author.country, meta.address().country);

		JPAQuery<AuthorDto> query = new JPAQuery<>(em);
		query.from(author);

		List<AuthorDto> authors = query.select(select).fetch();
		Assertions.assertThat(authors).isNotEmpty();

		AuthorDto hawking = authors.get(0);
		Assertions.assertThat(hawking.getId()).isEqualTo(1);
		Assertions.assertThat(hawking.getName()).isEqualTo("Stephen Hawking");
		Assertions.assertThat(hawking.getAddress().getCountry()).isEqualTo("United Kingdom");

		AuthorDto nietzsche = authors.get(1);
		Assertions.assertThat(nietzsche.getId()).isEqualTo(2);
		Assertions.assertThat(nietzsche.getName()).isEqualTo("Friedrich Nietzsche");
		Assertions.assertThat(nietzsche.getAddress().getCountry()).isEqualTo("Prussia");
	}
}
