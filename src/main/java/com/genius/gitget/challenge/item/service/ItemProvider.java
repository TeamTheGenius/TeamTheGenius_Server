package com.genius.gitget.challenge.item.service;

import com.genius.gitget.challenge.item.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemProvider {
    private final ItemRepository itemRepository;
}
