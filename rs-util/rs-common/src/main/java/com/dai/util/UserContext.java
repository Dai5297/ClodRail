package com.dai.util;

public class UserContext {

    private static final ThreadLocal<Long> tl = new ThreadLocal<>();

    public static void set(Long id) {
        tl.set(id);
    }

    public static Long get() {
        return tl.get();
    }

    public static void remove() {
        tl.remove();
    }
}
