package com.genius.gitget.store.item.controller;

import static com.genius.gitget.global.util.exception.SuccessCode.SUCCESS;

import com.genius.gitget.challenge.certification.util.DateUtil;
import com.genius.gitget.challenge.user.domain.User;
import com.genius.gitget.global.util.annotation.GitGetUser;
import com.genius.gitget.global.util.response.dto.CommonResponse;
import com.genius.gitget.global.util.response.dto.ListResponse;
import com.genius.gitget.global.util.response.dto.SingleResponse;
import com.genius.gitget.store.item.domain.ItemCategory;
import com.genius.gitget.store.item.dto.ItemResponse;
import com.genius.gitget.store.item.dto.OrderResponse;
import com.genius.gitget.store.item.dto.ProfileResponse;
import com.genius.gitget.store.item.facade.StoreFacade;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class StoreController {
    private final StoreFacade storeFacade;

    @GetMapping("/items")
    public ResponseEntity<ListResponse<ItemResponse>> getItemList(
            @GitGetUser User user,
            @RequestParam String category
    ) {
        ItemCategory itemCategory = ItemCategory.findCategory(category);
        List<ItemResponse> itemResponses = storeFacade.getItemsByCategory(user, itemCategory);

        return ResponseEntity.ok().body(
                new ListResponse<>(SUCCESS.getStatus(), SUCCESS.getMessage(), itemResponses)
        );
    }

    @PostMapping("/items/order/{identifier}")
    public ResponseEntity<SingleResponse<ItemResponse>> purchaseItem(
            @GitGetUser User user,
            @PathVariable int identifier
    ) {
        ItemResponse itemResponse = storeFacade.orderItem(user, identifier);

        return ResponseEntity.ok().body(
                new SingleResponse<>(SUCCESS.getStatus(), SUCCESS.getMessage(), itemResponse)
        );
    }

    @PostMapping("/items/use/{identifier}")
    public ResponseEntity<CommonResponse> useItem(
            @GitGetUser User user,
            @PathVariable int identifier,
            @RequestParam(required = false) Long instanceId
    ) {
        OrderResponse orderResponse = storeFacade.useItem(user, identifier,
                instanceId, DateUtil.convertToKST(LocalDateTime.now()));

        return ResponseEntity.ok().body(
                new SingleResponse<>(SUCCESS.getStatus(), SUCCESS.getMessage(), orderResponse)
        );
    }

    @PostMapping("/items/unuse")
    public ResponseEntity<ListResponse<ProfileResponse>> unmountItem(@GitGetUser User user) {
        List<ProfileResponse> profileResponses = storeFacade.unmountFrame(user);

        return ResponseEntity.ok().body(
                new ListResponse<>(SUCCESS.getStatus(), SUCCESS.getMessage(), profileResponses)
        );
    }
}
