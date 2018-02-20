package com.gtc.tradinggateway.config.converters;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.IOException;
import java.util.Map;

/**
 * Smart converter to convert {@link MediaType#APPLICATION_FORM_URLENCODED} to POJO. Adds necessary header.
 */
public class FormHttpMessageToPojoConverter extends AbstractHttpMessageConverter<Object> {

    private static final FormHttpMessageConverter formHttpMessageConverter = new FormHttpMessageConverter();

    private final ObjectMapper mapper;

    public FormHttpMessageToPojoConverter(ObjectMapper mapper) {
        super(formHttpMessageConverter.getSupportedMediaTypes().toArray(new MediaType[0]));
        this.mapper = mapper;
    }

    @Override
    protected boolean supports(Class<?> clazz) {
        return true;
    }

    @Override
    protected Object readInternal(Class<?> clazz, HttpInputMessage inputMessage) throws IOException {
        Map<String, String> vals = formHttpMessageConverter.read(null, inputMessage).toSingleValueMap();

        return mapper.convertValue(vals, clazz);
    }

    @Override
    protected void writeInternal(Object value, HttpOutputMessage outputMessage) throws IOException {
        Map<String, String> asMap = mapper.convertValue(value, new TypeReference<Map<String, String>>() {});
        MultiValueMap<String, String> mvMap = new LinkedMultiValueMap<>();
        asMap.forEach(mvMap::add);
        formHttpMessageConverter.write(mvMap, MediaType.APPLICATION_FORM_URLENCODED, outputMessage);
    }
}
