package com.github.compiler;


public interface Compiler {

    Class<?> compile(String code, ClassLoader classLoader);

}

