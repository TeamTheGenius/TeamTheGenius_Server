package com.genius.gitget.challenge.item.domain;

import com.genius.gitget.global.util.exception.BusinessException;
import com.genius.gitget.global.util.exception.ErrorCode;
import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ItemCategory {
    PROFILE_FRAME("profile-frame"),
    CERTIFICATION_PASSER("certification-passer"),
    POINT_MULTIPLIER("point-multiplier");

    private final String tag;

    public static ItemCategory findCategory(String category) {
        String lowerCase = category.trim().toLowerCase();

        return Arrays.stream(ItemCategory.values())
                .filter(itemCategory -> itemCategory.tag.equals(lowerCase))
                .findFirst()
                .orElseThrow(() -> new BusinessException(ErrorCode.ITEM_CATEGORY_NOT_FOUND));
    }
}
