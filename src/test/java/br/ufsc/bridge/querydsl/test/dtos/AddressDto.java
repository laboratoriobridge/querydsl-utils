package br.ufsc.bridge.querydsl.test.dtos;

import br.ufsc.bridge.metafy.Metafy;

@Metafy
public class AddressDto {

	private String country;

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

}
