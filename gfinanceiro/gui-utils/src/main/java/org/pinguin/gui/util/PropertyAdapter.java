package org.pinguin.gui.util;

import java.lang.ref.WeakReference;

import javafx.beans.InvalidationListener;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

/**
 * @param <T> Needed type
 * @param <A> Another type
 */
public class PropertyAdapter<T, A> implements Property<T> {

    private final WeakReference<Property<A>> propRef;
    private final Property<T> property = new SimpleObjectProperty<T>();
    private ValueConverter<T, A> converter;

    public PropertyAdapter(final Property<A> property, final ValueConverter<T, A> converter) {
        this.converter = converter;
        this.propRef = new WeakReference<Property<A>>(property);

        bind();
    }

    @Override
    public Object getBean() {
        return property.getBean();
    }

    @Override
    public String getName() {
        return property.getName();
    }

    @Override
    public void addListener(final ChangeListener< ? super T> listener) {
        property.addListener(listener);
    }

    @Override
    public T getValue() {
        return property.getValue();
    }

    @Override
    public void removeListener(final ChangeListener< ? super T> listener) {
        property.removeListener(listener);
    }

    @Override
    public void addListener(final InvalidationListener listener) {
        property.addListener(listener);
    }

    @Override
    public void removeListener(final InvalidationListener listener) {
        property.removeListener(listener);
    }

    @Override
    public void setValue(final T value) {
        property.setValue(value);
    }

    protected ValueConverter<T, A> getConverter() {
        return converter;
    }

    protected void setConverter(final ValueConverter<T, A> converter) {
        this.converter = converter;
    }

    @Override
    public void bind(final ObservableValue< ? extends T> value) {
        property.bind(value);
    }

    @Override
    public void bindBidirectional(final Property<T> another) {
        property.bindBidirectional(another);
    }

    @Override
    public boolean isBound() {
        return property.isBound();
    }

    @Override
    public void unbind() {
        property.unbind();
    }

    @Override
    public void unbindBidirectional(final Property<T> another) {
        property.unbindBidirectional(another);
    }

    @Override
    public int hashCode() {
        return property.hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        return property.equals(obj);
    }

    private void bind() {
        final Ref<ChangeListener<T>> propChgLstnRef = new Ref<>();
        final Ref<ChangeListener<A>> refChgLstnRef = new Ref<>();
        Property<A> valuePropRef;

        final RefListener refChangeListener = new RefListener();
        refChangeListener.setPropChgLstnRef(propChgLstnRef);

        final PropChangeListener propChangeListener = new PropChangeListener();
        propChangeListener.setRefChgLstnRef(refChgLstnRef);

        propChgLstnRef.set(propChangeListener);
        refChgLstnRef.set(refChangeListener);

        valuePropRef = propRef.get();
        if (valuePropRef != null) {
            property.setValue(converter.convert1(valuePropRef.getValue()));
        }

        valuePropRef = propRef.get();
        if (valuePropRef != null) {
            valuePropRef.addListener(refChangeListener);
        }

        property.addListener(propChangeListener);
    }

    public class RefListener implements ChangeListener<A> {

        private Ref<ChangeListener<T>> propChgLstnRef;

        @Override
        public void changed(final ObservableValue< ? extends A> ref, final A oldValue, final A newValue) {
            property.removeListener(propChgLstnRef.get());
            property.setValue(converter.convert1(newValue));
            property.addListener(propChgLstnRef.get());
        }

        public void setPropChgLstnRef(final Ref<ChangeListener<T>> propChgLstnRef) {
            this.propChgLstnRef = propChgLstnRef;
        }

    }

    class PropChangeListener implements ChangeListener<T> {

        private Ref<ChangeListener<A>> refChgLstnRef;

        @Override
        public void changed(final ObservableValue< ? extends T> ref, final T oldValue, final T newValue) {

            final Property<A> valuePropRef = propRef.get();

            if (valuePropRef != null) {
                valuePropRef.removeListener(refChgLstnRef.get());
                valuePropRef.setValue(converter.convert2(newValue));
                valuePropRef.addListener(refChgLstnRef.get());
            }
        }

        public void setRefChgLstnRef(final Ref<ChangeListener<A>> refChgLstnRef) {
            this.refChgLstnRef = refChgLstnRef;
        }
    }

    private static class Ref<T> {

        private T ref;

        public T get() {
            return ref;
        }

        public void set(final T ref) {
            this.ref = ref;
        }
    }
}
