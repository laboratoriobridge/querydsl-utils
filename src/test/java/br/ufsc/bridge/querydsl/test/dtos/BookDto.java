package br.ufsc.bridge.querydsl.test.dtos;

import br.ufsc.bridge.metafy.Metafy;

@Metafy
public class BookDto {

	private Integer id;

	private String title;

	private AuthorDto author;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public AuthorDto getAuthor() {
		return author;
	}

	public void setAuthor(AuthorDto author) {
		this.author = author;
	}

}
