package com.gtc.tradinggateway.config.converters;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
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
    protected void writeInternal(Object value, HttpOutputMessage outputMessage) {
        pojoSerialize(mapper, value, outputMessage);
    }

    @SneakyThrows
    public static String pojoSerialize(ObjectMapper mapper, Object value) {
        WrappedHttpOutputMessage outputMessage = new WrappedHttpOutputMessage();
        pojoSerialize(mapper, value, outputMessage);
        return outputMessage.content();
    }

    @SneakyThrows
    private static void pojoSerialize(ObjectMapper mapper, Object value, HttpOutputMessage outputMessage) {
        Map<String, String> asMap = mapper.convertValue(value, new TypeReference<Map<String, String>>() {});
        MultiValueMap<String, String> mvMap = new LinkedMultiValueMap<>();
        asMap.forEach(mvMap::add);
        formHttpMessageConverter.write(mvMap, MediaType.APPLICATION_FORM_URLENCODED, outputMessage);
    }

    private static class WrappedHttpOutputMessage implements HttpOutputMessage {

        private ByteArrayOutputStream stream = new ByteArrayOutputStream();
        private HttpHeaders headers = new HttpHeaders();

        @Override
        public OutputStream getBody() {
            return stream;
        }

        @Override
        public HttpHeaders getHeaders() {
            return headers;
        }

        private String content() {
            return new String(stream.toByteArray(), StandardCharsets.UTF_8);
        }
    }
}
