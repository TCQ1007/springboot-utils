package io.github.tcq1007.springbootutils.subtype;

import tools.jackson.databind.module.SimpleModule;

import java.util.ArrayList;
import java.util.List;

public class JacksonSubtypeRegister extends SimpleModule {
    public JacksonSubtypeRegister(Class<?> superType) {
        registerSubtypes(SubTypeUtil.getSubTypes(superType));
    }

    public JacksonSubtypeRegister(List<Class<?>> superTypes) {
        List<Class<?>> subTypes = new ArrayList<>();
        superTypes.forEach(superType -> {
            subTypes.addAll(SubTypeUtil.getSubTypes(superType));
        });
        registerSubtypes(subTypes.toArray(new Class[0]));
    }
}
