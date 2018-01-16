package br.ufsc.bridge.querydsl.test.dtos;

import java.util.List;

import br.ufsc.bridge.metafy.Metafy;

@Metafy
public class AuthorDto {

	private Integer id;

	private String name;

	private AddressDto address;

	private List<BookDto> books;

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<BookDto> getBooks() {
		return this.books;
	}

	public void setBooks(List<BookDto> books) {
		this.books = books;
	}

	public AddressDto getAddress() {
		return this.address;
	}

	public void setAddress(AddressDto address) {
		this.address = address;
	}
}
