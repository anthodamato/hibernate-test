package com.test.hibernate;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.Attribute.PersistentAttributeType;
import javax.persistence.metamodel.ManagedType;
import javax.persistence.metamodel.Type;
import javax.persistence.metamodel.Type.PersistenceType;

import org.junit.jupiter.api.Assertions;

/**
 * @author Antonio Damato <anto.damato@gmail.com>
 */
public class MetamodelUtils {
    public static void checkType(Type<?> type, Class<?> javaType, PersistenceType persistenceType) {
        Class<?> typeClass = type.getJavaType();
        Assertions.assertEquals(javaType, typeClass);
        PersistenceType pt = type.getPersistenceType();
        Assertions.assertEquals(pt, persistenceType);
    }

    public static List<String> getAttributeNames(ManagedType<?> managedType) {
        Set<?> attributes = managedType.getAttributes();
        return attributes.stream().map(a -> {
            Attribute<?, ?> attribute = (Attribute<?, ?>) a;
            return attribute;
        }).map(a -> a.getName()).collect(Collectors.toList());
    }

    public static void checkAttribute(
            Attribute<?, ?> attribute,
            String name,
            Class<?> javaType,
            PersistentAttributeType persistentAttributeType,
            boolean isAssociation,
            boolean isCollection) {
        Assertions.assertEquals(javaType, attribute.getJavaType());
        Assertions.assertEquals(persistentAttributeType, attribute.getPersistentAttributeType());
        Assertions.assertFalse(isAssociation);
        Assertions.assertFalse(isCollection);
        Assertions.assertEquals(name, attribute.getJavaMember().getName());
    }
}
