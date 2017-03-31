package me.corriekay.pokegoutil.utils.helpers;

/**
 * FunctionalInterface for implementing a TriConsumer like BiConsumer.
 * @param <T> first argument type
 * @param <U> second argument type
 * @param <S> third argument type
 */
@FunctionalInterface
public interface TriConsumer<T, U, S> {
 
    /**
     * Applies this function to the given arguments.
     * @param first the first function argument
     * @param second the second function argument
     * @param third the third function argument
     */
    void accept(T first, U second, S third);
}
