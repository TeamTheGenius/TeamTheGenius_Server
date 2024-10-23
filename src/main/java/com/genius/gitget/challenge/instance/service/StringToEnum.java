package com.genius.gitget.challenge.instance.service;

import com.genius.gitget.challenge.instance.domain.Progress;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class StringToEnum implements Converter<String, Progress> {
    @Override
    public Progress convert(String source) {
        return Progress.valueOf(source.toUpperCase());
    }
}
