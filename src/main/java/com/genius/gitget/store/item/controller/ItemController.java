package com.genius.gitget.store.item.controller;

import static com.genius.gitget.global.util.exception.SuccessCode.SUCCESS;

import com.genius.gitget.challenge.certification.util.DateUtil;
import com.genius.gitget.global.security.domain.UserPrincipal;
import com.genius.gitget.global.util.response.dto.CommonResponse;
import com.genius.gitget.global.util.response.dto.ListResponse;
import com.genius.gitget.global.util.response.dto.SingleResponse;
import com.genius.gitget.store.item.domain.Item;
import com.genius.gitget.store.item.domain.ItemCategory;
import com.genius.gitget.store.item.dto.ItemResponse;
import com.genius.gitget.store.item.dto.ItemUseResponse;
import com.genius.gitget.store.item.dto.ProfileResponse;
import com.genius.gitget.store.item.service.ItemService;
import com.genius.gitget.store.item.service.StoreFacade;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ItemController {
    private final StoreFacade storeFacade;
    private final ItemService itemService;

    @GetMapping("/items")
    public ResponseEntity<ListResponse<ItemResponse>> getItemList(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @RequestParam String category
    ) {
        List<ItemResponse> itemResponses;
        ItemCategory itemCategory = ItemCategory.findCategory(category);
        itemResponses = storeFacade.getItemsByCategory(userPrincipal.getUser(), itemCategory);

        return ResponseEntity.ok().body(
                new ListResponse<>(SUCCESS.getStatus(), SUCCESS.getMessage(), itemResponses)
        );
    }

    @PostMapping("/items/order/{identifier}")
    public ResponseEntity<SingleResponse<ItemResponse>> purchaseItem(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable int identifier
    ) {
        Item item = itemService.findByIdentifier(identifier);
        ItemResponse itemResponse = storeFacade.orderItem(userPrincipal.getUser(), item.getId());

        return ResponseEntity.ok().body(
                new SingleResponse<>(SUCCESS.getStatus(), SUCCESS.getMessage(), itemResponse)
        );
    }

    @PostMapping("/items/use/{identifier}")
    public ResponseEntity<CommonResponse> useItem(
            @AuthenticationPrincipal UserPrincipal userPrincipal,
            @PathVariable int identifier,
            @RequestParam(required = false) Long instanceId
    ) {
        Item item = itemService.findByIdentifier(identifier);
        ItemUseResponse itemUseResponse = storeFacade.useItem(userPrincipal.getUser(), item.getId(),
                instanceId, DateUtil.convertToKST(LocalDateTime.now()));

        return ResponseEntity.ok().body(
                new SingleResponse<>(SUCCESS.getStatus(), SUCCESS.getMessage(), itemUseResponse)
        );
    }

    @PostMapping("/items/unuse")
    public ResponseEntity<ListResponse<ProfileResponse>> unmountItem(
            @AuthenticationPrincipal UserPrincipal userPrincipal
    ) {
        List<ProfileResponse> profileResponses = storeFacade.unmountFrame(userPrincipal.getUser());

        return ResponseEntity.ok().body(
                new ListResponse<>(SUCCESS.getStatus(), SUCCESS.getMessage(), profileResponses)
        );
    }
}
