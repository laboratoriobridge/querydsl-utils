package br.ufsc.bridge.querydsl.selection;

public interface SelectionConverter<T, E> {

	E convert(T value);

}
