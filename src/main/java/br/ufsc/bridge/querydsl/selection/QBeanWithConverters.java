package br.ufsc.bridge.querydsl.selection;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ImmutableMap;
import com.google.common.primitives.Primitives;
import com.querydsl.core.group.GroupExpression;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ExpressionException;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.FactoryExpression;
import com.querydsl.core.types.FactoryExpressionBase;
import com.querydsl.core.types.Operation;
import com.querydsl.core.types.Ops;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.Visitor;

/**
 * QBeanWithConverters is a JavaBean populating projection type
 *
 * <p>Example</p>
 *
 * <pre>
 * {@code
 * QEmployee employee = QEmployee.employee;
 * List<EmployeeInfo> result = query.from(employee)
 *      .where(employee.valid.eq(true))
 *      .list(new QBeanWithConverters<EmployeeInfo>(EmployeeInfo.class, employee.firstName, employee.lastName));
 * }
 * </pre>
 *
 * @param <T> bean type
 */
public class QBeanWithConverters<T> extends FactoryExpressionBase<T> {

    private static final long serialVersionUID = -8210214512730989778L;

    private static ImmutableMap<String,Expression<?>> createBindings(Expression<?>... args) {
        ImmutableMap.Builder<String, Expression<?>> rv = ImmutableMap.builder();
        for (Expression<?> expr : args) {
            if (expr instanceof Path<?>) {
                Path<?> path = (Path<?>)expr;
                rv.put(path.getMetadata().getName(), expr);
            } else if (expr instanceof Operation<?>) {
                Operation<?> operation = (Operation<?>)expr;
                if (operation.getOperator() == Ops.ALIAS && operation.getArg(1) instanceof Path<?>) {
                    Path<?> path = (Path<?>)operation.getArg(1);
                    if (isCompoundExpression(operation.getArg(0))) {
                        rv.put(path.getMetadata().getName(), operation.getArg(0));
                    } else {
                        rv.put(path.getMetadata().getName(), operation);
                    }
                } else {
                    throw new IllegalArgumentException("Unsupported expression " + expr);
                }

            } else {
                throw new IllegalArgumentException("Unsupported expression " + expr);
            }
        }
        return rv.build();
    }

    private static boolean isCompoundExpression(Expression<?> expr) {
        return expr instanceof FactoryExpression || expr instanceof GroupExpression;
    }

    private static Class<?> normalize(Class<?> cl) {
        return cl.isPrimitive() ? Primitives.wrap(cl) : cl;
    }

    private final ImmutableMap<String, Expression<?>> bindings;

    private final List<Method> setters;

    private transient List<SelectionConverter<?, ?>> converters;

	public QBeanWithConverters(Class<T> type, List<SelectionConverter<?, ?>> converters, Expression<?>... args) {
		super(type);
		this.converters = converters;
		this.bindings = ImmutableMap.copyOf(createBindings(args));
        this.setters = this.initMethods(this.bindings);
	}

    private List<Method> initMethods(Map<String, ? extends Expression<?>> args) {
        try {
            List<Method> methods = new ArrayList<Method>(args.size());
            BeanInfo beanInfo = Introspector.getBeanInfo(this.getType());
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            int i = 0;
            for (Map.Entry<String, ? extends Expression<?>> entry : args.entrySet()) {
                String property = entry.getKey();
                Expression<?> expr = entry.getValue();
                Method setter = null;
                for (PropertyDescriptor prop : propertyDescriptors) {
                    if (prop.getName().equals(property)) {
                        setter = prop.getWriteMethod();
                        if (!normalize(prop.getPropertyType()).isAssignableFrom(expr.getType()) && this.converters.get(i) == null) {
                            this.typeMismatch(prop.getPropertyType(), expr);
                        }
                        break;
                    }
                }
                if (setter == null) {
                    this.propertyNotFound(expr, property);
                }
                methods.add(setter);
                i++;
            }
            return methods;
        } catch (IntrospectionException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    protected void propertyNotFound(Expression<?> expr, String property) {
        // do nothing
    }

    protected void typeMismatch(Class<?> type, Expression<?> expr) {
        final String msg = expr.getType().getName() + " is not compatible with " + type.getName();
        throw new IllegalArgumentException(msg);
    }

    @Override
    public T newInstance(Object... a) {
        try {
            T rv = this.create(this.getType());
            for (int i = 0; i < a.length; i++) {
                Object value = a[i];
                if (value != null) {
                    Method setter = this.setters.get(i);
                    if (setter != null) {
                    	SelectionConverter selectionConverter = this.converters.get(i);
            			if (selectionConverter != null) {
            				setter.invoke(rv, selectionConverter.convert(value));
            			} else {
            				setter.invoke(rv, value);
            			}
					}
                }
            }
            return rv;
        } catch (InstantiationException e) {
            throw new ExpressionException(e.getMessage(), e);
        } catch (IllegalAccessException e) {
            throw new ExpressionException(e.getMessage(), e);
        } catch (InvocationTargetException e) {
            throw new ExpressionException(e.getMessage(), e);
        }
    }

    protected <T> T create(Class<T> type) throws IllegalAccessException, InstantiationException {
        return type.newInstance();
    }

    /**
     * Create an alias for the expression
     *
     * @return
     */
    public Expression<T> as(Path<T> alias) {
        return ExpressionUtils.operation(this.getType(),Ops.ALIAS, this, alias);
    }

    /**
     * Create an alias for the expression
     *
     * @return
     */
    public Expression<T> as(String alias) {
        return this.as(ExpressionUtils.path(this.getType(), alias));
    }

    @Override
    public <R,C> R accept(Visitor<R,C> v, C context) {
        return v.visit(this, context);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj instanceof QBeanWithConverters<?>) {
            QBeanWithConverters<?> c = (QBeanWithConverters<?>)obj;
            return this.getArgs().equals(c.getArgs()) && this.getType().equals(c.getType());
        } else {
            return false;
        }
    }

    @Override
    public List<Expression<?>> getArgs() {
        return this.bindings.values().asList();
    }

}