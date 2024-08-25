package com.genius.gitget.page;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.genius.gitget.global.page.CustomPageImpl;

public class CustomPageImplTest {

	private final ObjectMapper objectMapper = new ObjectMapper();

	@Test
	public void testCustomPageImplSerialization() throws JsonProcessingException {

		// 데이터 목록과 페이지 정보를 설정
		List<String> data = List.of("item1", "item2", "item3");
		PageRequest pageRequest = PageRequest.of(0, 10);

		// CustomPageImpl 객체 생성
		CustomPageImpl<String> customPage = new CustomPageImpl<>(data, pageRequest, 3L);

		// CustomPageImpl 객체를 JSON으로 직렬화
		String json = objectMapper.writeValueAsString(customPage);

		System.out.println(json);

		// JSON 문자열을 다시 CustomPageImpl 객체로 역직렬화
		CustomPageImpl deserializedPage = objectMapper.readValue(json, CustomPageImpl.class);

		assertNotNull(deserializedPage);
		assertEquals(customPage.getContent(), deserializedPage.getContent());
		assertEquals(customPage.getTotalElements(), deserializedPage.getTotalElements());
		assertEquals(customPage.getPageable().getPageNumber(), deserializedPage.getPageable().getPageNumber());
	}
}

