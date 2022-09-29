/*
 * Copyright (c) 2005, Sam Pullara. All Rights Reserved.
 * You may modify and redistribute as long as this attribution remains.
 */

package com.hankcs.hanlp.model.perceptron.cli;

import java.io.PrintStream;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class Args
{

    private static void processField(Object target, Field field, List<String> arguments)
    {
        Argument argument = field.getAnnotation(Argument.class);
        if (argument != null)
        {
            boolean set = false;
            for (Iterator<String> i = arguments.iterator(); i.hasNext(); )
            {
                String arg = i.next();
                String prefix = argument.prefix();
                String delimiter = argument.delimiter();
                if (arg.startsWith(prefix))
                {
                    Object value;
                    String name = getName(argument, field);
                    String alias = getAlias(argument);
                    arg = arg.substring(prefix.length());
                    Class<?> type = field.getType();
                    if (arg.equals(name) || (alias != null && arg.equals(alias)))
                    {
                        i.remove();
                        value = consumeArgumentValue(name, type, argument, i);
                        if (!set)
                        {
                            setField(type, field, target, value, delimiter);
                        }
                        else
                        {
                            addArgument(type, field, target, value, delimiter);
                        }
                        set = true;
                    }
                    if (set && !type.isArray()) break;
                }
            }
            if (!set && argument.required())
            {
                String name = getName(argument, field);
                throw new IllegalArgumentException("缺少必需参数: " + argument.prefix() + name);
            }
        }
    }

    private static void addArgument(Class type, Field field, Object target, Object value, String delimiter)
    {
        try
        {
            Object[] os = (Object[]) field.get(target);
            Object[] vs = (Object[]) getValue(type, value, delimiter);
            Object[] s = (Object[]) Array.newInstance(type.getComponentType(), os.length + vs.length);
            System.arraycopy(os, 0, s, 0, os.length);
            System.arraycopy(vs, 0, s, os.length, vs.length);
            field.set(target, s);
        }
        catch (IllegalAccessException iae)
        {
            throw new IllegalArgumentException("Could not set field " + field, iae);
        }
        catch (NoSuchMethodException e)
        {
            throw new IllegalArgumentException("Could not find constructor in class " + type.getName() + " that takes a string", e);
        }
    }


    private static void fieldUsage(PrintStream errStream, Object target, Field field)
    {
        Argument argument = field.getAnnotation(Argument.class);
        if (argument != null)
        {
            String name = getName(argument, field);
            String alias = getAlias(argument);
            String prefix = argument.prefix();
            String delimiter = argument.delimiter();
            String description = argument.description();
            makeAccessible(field);
            try
            {
                Object defaultValue = field.get(target);
                Class<?> type = field.getType();
                propertyUsage(errStream, prefix, name, alias, type, delimiter, description, defaultValue);
            }
            catch (IllegalAccessException e)
            {
                throw new IllegalArgumentException("Could not use thie field " + field + " as an argument field", e);
            }
        }
    }

    private static void propertyUsage(PrintStream errStream, String prefix, String name, String alias, Class<?> type, String delimiter, String description, Object defaultValue)
    {
        StringBuilder sb = new StringBuilder("  ");
        sb.append(prefix);
        sb.append(name);
        if (alias != null)
        {
            sb.append(" (");
            sb.append(prefix);
            sb.append(alias);
            sb.append(")");
        }
        if (type == Boolean.TYPE || type == Boolean.class)
        {
            sb.append("\t[flag]\t");
            sb.append(description);
        }
        else
        {
            sb.append("\t[");
            if (type.isArray())
            {
                String typeName = getTypeName(type.getComponentType());
                sb.append(typeName);
                sb.append("[");
                sb.append(delimiter);
                sb.append("]");
            }
            else
            {
                String typeName = getTypeName(type);
                sb.append(typeName);
            }
            sb.append("]\t");
            sb.append(description);
            if (defaultValue != null)
            {
                sb.append(" (");
                if (type.isArray())
                {
                    List<Object> list = new ArrayList<Object>();
                    int len = Array.getLength(defaultValue);
                    for (int i = 0; i < len; i++)
                    {
                        list.add(Array.get(defaultValue, i));
                    }
                    sb.append(list);
                }
                else
                {
                    sb.append(defaultValue);
                }
                sb.append(")");
            }

        }
        errStream.println(sb);
    }

    private static String getTypeName(Class<?> type)
    {
        String typeName = type.getName();
        int beginIndex = typeName.lastIndexOf(".");
        typeName = typeName.substring(beginIndex + 1);
        return typeName;
    }

    private static Object consumeArgumentValue(String name, Class<?> type, Argument argument, Iterator<String> i)
    {
        Object value;
        if (type == Boolean.TYPE || type == Boolean.class)
        {
            value = true;
        }
        else
        {
            if (i.hasNext())
            {
                value = i.next();
                i.remove();
            }
            else
            {
                throw new IllegalArgumentException("非flag参数必须指定值: " + argument.prefix() + name);
            }
        }
        return value;
    }

    static String getAlias(Argument argument)
    {
        String alias = argument.alias();
        if (alias.equals(""))
        {
            alias = null;
        }
        return alias;
    }

    static String getName(Argument argument, Field field)
    {
        String name = argument.value();
        if (name.equals(""))
        {
            name = field.getName();
        }
        return name;
    }

    static void setField(Class<?> type, Field field, Object target, Object value, String delimiter)
    {
        makeAccessible(field);
        try
        {
            value = getValue(type, value, delimiter);
            field.set(target, value);
        }
        catch (IllegalAccessException iae)
        {
            throw new IllegalArgumentException("Could not set field " + field, iae);
        }
        catch (NoSuchMethodException e)
        {
            throw new IllegalArgumentException("Could not find constructor in class " + type.getName() + " that takes a string", e);
        }
    }

    private static Object getValue(Class<?> type, Object value, String delimiter) throws NoSuchMethodException
    {
        if (type != String.class && type != Boolean.class && type != Boolean.TYPE)
        {
            String string = (String) value;
            if (type.isArray())
            {
                String[] strings = string.split(delimiter);
                type = type.getComponentType();
                if (type == String.class)
                {
                    value = strings;
                }
                else
                {
                    Object[] array = (Object[]) Array.newInstance(type, strings.length);
                    for (int i = 0; i < array.length; i++)
                    {
                        array[i] = createValue(type, strings[i]);
                    }
                    value = array;
                }
            }
            else
            {
                value = createValue(type, string);
            }
        }
        return value;
    }

    private static Object createValue(Class<?> type, String valueAsString) throws NoSuchMethodException
    {
        for (ValueCreator valueCreator : valueCreators)
        {
            Object createdValue = valueCreator.createValue(type, valueAsString);
            if (createdValue != null)
            {
                return createdValue;
            }
        }
        throw new IllegalArgumentException(String.format("cannot instanciate any %s object using %s value", type.toString(), valueAsString));
    }

    private static void makeAccessible(AccessibleObject ao)
    {
        if (ao instanceof Member)
        {
            Member member = (Member) ao;
            if (!Modifier.isPublic(member.getModifiers()))
            {
                ao.setAccessible(true);
            }
        }
    }

    public static interface ValueCreator
    {
        /**
         * Creates a value object of the given type using the given string value representation;
         *
         * @param type  the type to create an instance of
         * @param value the string represented value of the object to create
         * @return null if the object could not be created, the value otherwise
         */
        public Object createValue(Class<?> type, String value);
    }

    /**
     * Creates a {@link ValueCreator} object able to create object assignable from given type,
     * using a static one arg method which name is the the given one taking a String object as parameter
     *
     * @param compatibleType the base assignable for which this object will try to invoke the given method
     * @param methodName     the name of the one arg method taking a String as parameter that will be used to built a new value
     * @return null if the object could not be created, the value otherwise
     */
    public static ValueCreator byStaticMethodInvocation(final Class<?> compatibleType, final String methodName)
    {
        return new ValueCreator()
        {
            public Object createValue(Class<?> type, String value)
            {
                Object v = null;
                if (compatibleType.isAssignableFrom(type))
                {
                    try
                    {
                        Method m = type.getMethod(methodName, String.class);
                        return m.invoke(null, value);
                    }
                    catch (NoSuchMethodException e)
                    {
                        // ignore
                    }
                    catch (Exception e)
                    {
                        throw new IllegalArgumentException(String.format("could not invoke %s#%s to create an obejct from %s", type.toString(), methodName, value));
                    }
                }
                return v;
            }
        };
    }

    /**
     * {@link ValueCreator} building object using a one arg constructor taking a {@link String} object as parameter
     */
    public static final ValueCreator FROM_STRING_CONSTRUCTOR = new ValueCreator()
    {
        public Object createValue(Class<?> type, String value)
        {
            Object v = null;
            try
            {
                Constructor<?> init = type.getDeclaredConstructor(String.class);
                v = init.newInstance(value);
            }
            catch (NoSuchMethodException e)
            {
                // ignore
            }
            catch (Exception e)
            {
                throw new IllegalArgumentException("Failed to convertPKUtoCWS " + value + " to type " + type.getName(), e);
            }
            return v;
        }
    };

    public static final ValueCreator ENUM_CREATOR = new ValueCreator()
    {
        @SuppressWarnings({"unchecked", "rawtypes"})
        public Object createValue(Class type, String value)
        {
            if (Enum.class.isAssignableFrom(type))
            {
                return Enum.valueOf(type, value);
            }
            return null;
        }
    };

    private static final List<ValueCreator> DEFAULT_VALUE_CREATORS = Arrays.asList(Args.FROM_STRING_CONSTRUCTOR, Args.ENUM_CREATOR);
    private static List<ValueCreator> valueCreators = new ArrayList<ValueCreator>(DEFAULT_VALUE_CREATORS);

    /**
     * Allows external extension of the valiue creators.
     *
     * @param vc another value creator to take into account for trying to create values
     */
    public static void registerValueCreator(ValueCreator vc)
    {
        valueCreators.add(vc);
    }

    /**
     * Cleanup of registered ValueCreators (mainly for tests)
     */
    public static void resetValueCreators()
    {
        valueCreators.clear();
        valueCreators.addAll(DEFAULT_VALUE_CREATORS);
    }
}
