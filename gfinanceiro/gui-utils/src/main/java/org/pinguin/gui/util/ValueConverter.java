package org.pinguin.gui.util;

public interface ValueConverter<T1, T2> {

    T1 convert1(T2 value);

    T2 convert2(T1 value);
}
