package com.genius.gitget.global.web;

import com.genius.gitget.challenge.instance.domain.Progress;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class StringToEnum implements Converter<String, Progress> {
    // TODO progress, file 처리 필요
    @Override
    public Progress convert(String source) {
        String typeName = source.getClass().getName();
        log.info(typeName);
        return Progress.valueOf(source.toUpperCase());
    }
}
