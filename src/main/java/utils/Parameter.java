package utils;

import java.util.Objects;

/**
 * Class that matches key-value query parameters with "="
 */
public class Parameter {
    private final String name;
    private final String value;

    public Parameter(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Parameter parameter1 = (Parameter) o;
        return Objects.equals(name, parameter1.name) &&
                Objects.equals(value, parameter1.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, value);
    }

    @Override
    public String toString() {
        return "Parameter{" +
                "parameter='" + name + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}