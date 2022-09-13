package com.mycompany.common;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class Builder<T> {

    private final Supplier<T> instantiation;

    private List<Consumer<T>> instanceModifiers = new ArrayList<>();

    private Builder(Supplier<T> instantiation) {
        this.instantiation = instantiation;
    }

    public static <T> Builder<T> of(Supplier<T> instantiator) {
        return new Builder<T>(instantiator);
    }

    public <U> Builder<T> with(BiConsumer<T, U> consumer, U value) {
        Consumer<T> modifier = instance -> consumer.accept(instance, value);
        instanceModifiers.add(modifier);
        return this;
    }

    public T build() {
        T value = instantiation.get();
        instanceModifiers.forEach(modifier -> modifier.accept(value));
        instanceModifiers.clear();
        return value;
    }

    public T build(Consumer<T> persistable) {
        T value = build();
        persistable.accept(value);
        return value;
    }
}
