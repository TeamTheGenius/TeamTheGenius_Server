package com.genius.gitget.store.item.domain;

import com.genius.gitget.global.util.exception.BusinessException;
import com.genius.gitget.global.util.exception.ErrorCode;
import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ItemCategory {
    PROFILE_FRAME("profile-frame", "프레임"),
    CERTIFICATION_PASSER("certification-passer", "인증 패스 아이템"),
    POINT_MULTIPLIER("point-multiplier", "포인트 2배 획득 아이템");

    private final String tag;
    private final String name;

    public static ItemCategory findCategory(String category) {
        String lowerCase = category.trim().toLowerCase();

        return Arrays.stream(ItemCategory.values())
                .filter(itemCategory -> itemCategory.tag.equals(lowerCase))
                .findFirst()
                .orElseThrow(() -> new BusinessException(ErrorCode.ITEM_CATEGORY_NOT_FOUND));
    }
}
