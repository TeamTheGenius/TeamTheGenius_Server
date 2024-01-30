package com.genius.gitget.challenge.instance.domain;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToEnum implements Converter<String, Progress> {
    @Override
    public Progress convert(String source) {
        return Progress.valueOf(source.toUpperCase());
    }
}
