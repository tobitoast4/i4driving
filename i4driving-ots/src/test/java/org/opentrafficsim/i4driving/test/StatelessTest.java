package org.opentrafficsim.i4driving.test;

import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;

import org.junit.Test;
import org.opentrafficsim.i4driving.Stateless;

public class StatelessTest
{

    /**
     * Tests that classes with {@code @Stateless} are stateless.
     */
    @Test
    public void testStateless()
    {
        Collection<Class<?>> classes = ClassList.classList("org.opentrafficsim.i4driving", true);
        for (Class<?> clazz : classes)
        {
            if (clazz.isAnnotationPresent(Stateless.class))
            {
                for (Field field : clazz.getDeclaredFields())
                {
                    assertTrue(
                            "Field " + field.getName() + " is not final or static in stateless class " + clazz.getSimpleName(),
                            Modifier.isFinal(field.getModifiers()) || Modifier.isStatic(field.getModifiers()));
                }
            }
        }
    }

}
