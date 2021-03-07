package com.bochkov.wicket.jpa.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.util.string.Strings;

import java.util.Optional;

public class JacksonModel<T> extends NonSerializableModel<T, String> {

    private ObjectMapper objMapper;

    private Class<T> clazz;

    public JacksonModel(Class<T> tClass) {
        this.objMapper = new ObjectMapper()
                .enable(SerializationFeature.INDENT_OUTPUT)
                .registerModule(new JavaTimeModule()).disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        this.clazz = tClass;
    }

    public JacksonModel(T object) {
        super(object);
        clazz = (Class<T>) object.getClass();
        this.objMapper = new ObjectMapper()
                .enable(SerializationFeature.INDENT_OUTPUT)
                .registerModule(new JavaTimeModule()).disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    public JacksonModel(ObjectMapper objMapper, Class<T> clazz) {
        this.objMapper = objMapper;
        this.clazz = clazz;
    }

    public JacksonModel(T object, ObjectMapper objMapper, Class<T> clazz) {
        super(object);
        this.objMapper = objMapper;
        this.clazz = clazz;
    }

    @Override
    public Optional<String> pack(T object) {
        return Optional.ofNullable(object).map(this::serializeObject);
    }

    @Override
    public Optional<T> unpack(String s) {
        return Optional.ofNullable(s).filter(str -> !Strings.isEmpty(str)).map(this::deserializeObject);
    }


    public String serializeObject(T target) {
        try {
            return objMapper.writeValueAsString(target);
        } catch (Exception e) {
            throw new WicketRuntimeException("An error occurred during object serialization.", e);
        }
    }

    public T deserializeObject(String source) {
        try {
            return objMapper.readValue(source, clazz);
        } catch (Exception e) {
            throw new WicketRuntimeException("An error occurred during object deserialization.", e);
        }
    }
}
