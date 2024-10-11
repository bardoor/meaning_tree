package org.vstu.meaningtree.languages.configs;

import org.vstu.meaningtree.exceptions.MeaningTreeException;

import java.util.List;

public class ConfigParameter {
    public enum Scope {
        VIEWER, // only applicable to viewer
        PARSER, // only applicable to parser
        TRANSLATOR // applicable both parser and viewer
    }

    private String name;
    private Scope scope;
    private Object value;
    private Class<?> type;

    private static boolean isInAllowedTypes(Object value) {
        return List.of(Number.class,
                String.class, Boolean.class,
                Character.class).contains(value.getClass());
    }

    private ConfigParameter(String name, Object value, Scope scope) {
        if (!isInAllowedTypes(value)) {
            throw new MeaningTreeException("Unsupported for config value type");
        }
        this.name = name;
        this.scope = scope;
        this.value = value;
        this.type = value.getClass();
    }

    public ConfigParameter(String name, int value, Scope scope) {
        this(name, (long) value, scope);
    }

    public ConfigParameter(String name, float value, Scope scope) {
        this(name, (double) value, scope);
    }

    public ConfigParameter(String name, double value, Scope scope) {
        this(name, (Double) value, scope);
    }

    public ConfigParameter(String name, String value, Scope scope) {
        this(name, (Object) value, scope);
    }

    public ConfigParameter(String name, byte value, Scope scope) {
        this(name, (Byte) value, scope);
    }

    public ConfigParameter(String name, long value, Scope scope) {
        this(name, (Long) value, scope);
    }

    public ConfigParameter(String name, boolean value, Scope scope) {
        this(name, (Boolean) value, scope);
    }

    public ConfigParameter(String name, char value, Scope scope) {
        this(name, (Character) value, scope);
    }

    public Class<?> getType() {
        return type;
    }

    public long getNumber() {
        return (Long) value;
    }

    public String getStringValue() {
        return (String) value;
    }

    public double getDoubleValue() {
        return (Double) value;
    }

    public char getCharacterValue() {
        return (Character) value;
    }

    public boolean getBooleanValue() {
        return (Boolean) value;
    }

    public Object getRawValue() {
        return value;
    }

    public void setValue(Object newValue) {
        if (!newValue.getClass().equals(type)) {
            throw new MeaningTreeException("Invalid config value type. You cannot redefine type of value");
        }
        this.value = newValue;
    }

    public void setValue(int newValue) {
        setValue((long) newValue);
    }

    public void setValue(byte newValue) {
        setValue((Byte) newValue);
    }

    public void setValue(long newValue) {
        setValue((Long) newValue);
    }

    public void setValue(String newValue) {
        setValue((Object) newValue);
    }

    public void setValue(boolean newValue) {
        setValue((Boolean) newValue);
    }

    public void setValue(char newValue) {
        setValue((Character) newValue);
    }

    public void setValue(double newValue) {
        setValue((Double) newValue);
    }

    public void setValue(float newValue) {
        setValue((double) newValue);
    }

    public Scope getScope() {
        return scope;
    }

    public String getName() {
        return name;
    }

    public void inferValueFrom(String value) {
        if (value.equals("true") || value.equals("false")) {
            boolean typed = false;
            if (value.equals("true")) {
                typed = true;
            }
            setValue(typed);
            return;
        }

        try {
            setValue(Long.parseLong(value));
            return;
        } catch (NumberFormatException e) {}

        try {
            setValue(Double.parseDouble(value));
            return;
        } catch (NumberFormatException e) {}

        if (value.length() == 1) {
            setValue(value.charAt(0));
        } else {
            setValue(value);
        }
    }
}
