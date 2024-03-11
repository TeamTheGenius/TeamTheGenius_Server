package com.genius.gitget.challenge.item.controller;

import static com.genius.gitget.global.util.exception.SuccessCode.SUCCESS;

import com.genius.gitget.challenge.item.domain.ItemCategory;
import com.genius.gitget.challenge.item.dto.ItemResponse;
import com.genius.gitget.challenge.item.service.ItemService;
import com.genius.gitget.global.security.domain.UserPrincipal;
import com.genius.gitget.global.util.response.dto.ListResponse;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ItemController {
    private final ItemService itemService;

    @GetMapping("/points")
    public ResponseEntity<ListResponse<ItemResponse>> getItemList(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam String category
    ) {
        List<ItemResponse> itemResponses = new ArrayList<>();
        ItemCategory itemCategory = ItemCategory.findCategory(category);
        if (itemCategory == ItemCategory.PROFILE_FRAME) {
            itemResponses = itemService.getProfileItemList(userPrincipal.getUser());
        }

        return ResponseEntity.ok().body(
                new ListResponse<>(SUCCESS.getStatus(), SUCCESS.getMessage(), itemResponses)
        );
    }
}
