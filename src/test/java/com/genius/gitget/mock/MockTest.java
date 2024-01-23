package com.genius.gitget.mock;


import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.mockito.Mockito.*;

@SpringBootTest
public class MockTest {

    @Test
    public void Mock_Test() {
        List mockList = mock(List.class);
        when(mockList.get(anyInt())).thenReturn("first");
        System.out.println(mockList.get(999));
        verify(mockList).get(anyInt());
    }
}
