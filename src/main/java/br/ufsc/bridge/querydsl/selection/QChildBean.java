package br.ufsc.bridge.querydsl.selection;
import java.util.List;

import com.querydsl.core.types.Expression;

public class QChildBean<T> extends QBeanWithConverters<T> {

	private static final long serialVersionUID = -3180340538904753892L;

	public QChildBean(Class<T> type, List<SelectionConverter<?, ?>> converters, Expression<?>... args) {
		super(type, converters, args);
	}

	@Override
	public T newInstance(Object... a) {
		boolean hasValues = false;
		for (int i = 0; i < a.length; i++) {
			if (a[i] != null) {
				hasValues = true;
				break;
			}
		}
		if (hasValues) {
			return super.newInstance(a);
		} else {
			return null;
		}

	}

}