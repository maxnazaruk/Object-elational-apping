package com.luxoft.querygenerator;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.StringJoiner;

public class QueryGenerator {

    public String getAll(Class<?> clazz) {
        StringBuilder stringBuilder = new StringBuilder("SELECT ");
        Table annotation = clazz.getAnnotation(Table.class);

        if (annotation == null) {
            throw new IllegalArgumentException("@Table is missing");
        }

        String tableName = annotation.name().isEmpty() ? clazz.getName() : annotation.name();

        StringJoiner stringJoiner = new StringJoiner(", ");

        for (Field declaredField : clazz.getDeclaredFields()) {
            Column columnAnnotation = declaredField.getAnnotation(Column.class);
            if (columnAnnotation != null) {
                String columnName = columnAnnotation.name().isEmpty() ? declaredField.getName() : columnAnnotation.name();
                stringJoiner.add(columnName);
            }
        }

        stringBuilder.append(stringJoiner);
        stringBuilder.append(" FROM ");
        stringBuilder.append(tableName);
        stringBuilder.append(";");

        return stringBuilder.toString();
    }

    public String insert(Class<?> clazz, Object object)  {
        StringBuilder stringBuilder = new StringBuilder("INSERT INTO ");
        Table annotation = clazz.getAnnotation(Table.class);

        if (annotation == null) {
            throw new IllegalArgumentException("Table is missing");
        }

        String tableName = annotation.name().isEmpty() ? clazz.getName() : annotation.name();

        stringBuilder.append(tableName);

        StringJoiner stringJoiner = new StringJoiner(", ", " (", ")");
        StringJoiner valuesJoiner = new StringJoiner(", ", " (", ")");

        for (Field declaredField : clazz.getDeclaredFields()) {
            Column columnAnnotation = declaredField.getAnnotation(Column.class);
            declaredField.setAccessible(true);
            if (columnAnnotation != null) {
                String columnName = columnAnnotation.name().isEmpty() ? declaredField.getName() : columnAnnotation.name();
                stringJoiner.add(columnName);

                for (Method declaredMethods : clazz.getDeclaredMethods()) {
                    if(declaredMethods.getName().startsWith("get") && declaredField.getName().toLowerCase().equals(declaredMethods.getName().substring(3).toLowerCase())){
                        String insterValue = null;
                        try {
                            insterValue = declaredMethods.invoke(object, null).toString();
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        }
                        if(declaredMethods.getReturnType() == String.class){
                            insterValue = "'" + insterValue + "'";
                        }
                        valuesJoiner.add(insterValue);
                    }
                }
            }
        }

        stringBuilder.append(stringJoiner + "\n");
        stringBuilder.append("VALUES");
        stringBuilder.append(valuesJoiner);
        stringBuilder.append(";");

        return stringBuilder.toString();
    }

    public String update(Class<?> clazz, Object value, String condition)  {
        StringBuilder stringBuilder = new StringBuilder("UPDATE ");
        Table annotation = clazz.getAnnotation(Table.class);

        if (annotation == null) {
            throw new IllegalArgumentException("Table is missing");
        }

        String tableName = annotation.name().isEmpty() ? clazz.getName() : annotation.name();

        stringBuilder.append(tableName + "\n");

        stringBuilder.append("SET ");

        StringJoiner stringJoiner = new StringJoiner(", ");

        for (Field declaredField : clazz.getDeclaredFields()) {
            Column columnAnnotation = declaredField.getAnnotation(Column.class);
            declaredField.setAccessible(true);
            if (columnAnnotation != null) {
                String columnName = columnAnnotation.name().isEmpty() ? declaredField.getName() : columnAnnotation.name();

                for (Method declaredMethods : clazz.getDeclaredMethods()) {
                    if(declaredMethods.getName().startsWith("get") && declaredField.getName().toLowerCase().equals(declaredMethods.getName().substring(3).toLowerCase())){
                        String insterValue = null;
                        try {
                            insterValue = declaredMethods.invoke(value, null).toString();
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        }
                        if(declaredMethods.getReturnType() == String.class){
                            insterValue = "'" + insterValue + "'";
                        }
                        stringJoiner.add(columnName + " = " + insterValue);
                    }
                }
            }
        }

        stringBuilder.append(stringJoiner).append("\n");
        stringBuilder.append("WHERE ").append(condition).append(";");

        return stringBuilder.toString();
    }

    public String getById(Class<?> clazz, String value) {
        StringBuilder stringBuilder = new StringBuilder(this.getAll(clazz).substring(0, this.getAll(clazz).length()-1) + "\n");
        stringBuilder.append("WHERE id = ").append(value).append(";");
        return stringBuilder.toString();
    }

    public String delete(Class<?> clazz, Object value) {
        StringBuilder stringBuilder = new StringBuilder("DELETE FROM ");
        Table annotation = clazz.getAnnotation(Table.class);

        if (annotation == null) {
            throw new IllegalArgumentException("Table is missing");
        }

        String tableName = annotation.name().isEmpty() ? clazz.getName() : annotation.name();

        stringBuilder.append(tableName).append(" WHERE ").append(value).append(";");
        return stringBuilder.toString();
    }
}
