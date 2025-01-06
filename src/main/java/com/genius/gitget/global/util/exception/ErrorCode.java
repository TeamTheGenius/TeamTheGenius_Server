package com.genius.gitget.global.util.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    UUID_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "이미 존재하는 uuid입니다."),

    MEMBER_NOT_UPDATED(HttpStatus.BAD_REQUEST, "유저 정보가 업데이트되지 않았습니다."),

    LIKES_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 좋아요 목록을 찾을 수 없습니다."),

    FAILED_POINT_PAYMENT(HttpStatus.BAD_REQUEST, "결제 조건이 충족하지 않습니다."),
    INVALID_PAYMENT_AMOUNT(HttpStatus.BAD_REQUEST, "최초 결제 요청 금액과 일치하지 않습니다."),
    FAILED_FINAL_PAYMENT(HttpStatus.BAD_REQUEST, "최종 결제가 승인되지 않았습니다."),
    INVALID_ORDERID(HttpStatus.NOT_FOUND, "해당 주문번호가 존재하지 않습니다."),

    TOPIC_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 토픽을 찾을 수 없습니다."),
    TOPIC_HAVE_INSTANCE(HttpStatus.BAD_REQUEST, "해당 토픽은 인스턴스를 가지고 있으므로 삭제할 수 없습니다."),
    TOPIC_IMAGE_NOT_FOUND(HttpStatus.BAD_REQUEST, "토픽 이미지가 존재하지 않습니다. 토픽 이미지를 설정해주세요."),

    INVALID_INSTANCE_DATE(HttpStatus.BAD_REQUEST, "인스턴스 생성/종료 일자는 현재 일자 이후여야 합니다."),
    INSTANCE_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 인스턴스를 찾을 수 없습니다."),
    PARTICIPANT_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 참여 정보를 찾을 수 없습니다."),

    ALREADY_PASSED_CERTIFICATION(HttpStatus.BAD_REQUEST, "패스한 인증에 대해서는 인증 갱신할 수 없습니다."),
    CERTIFICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 인증 정보를 찾을 수 없습니다."),

    ALREADY_REGISTERED(HttpStatus.BAD_REQUEST, "이미 회원가입이 완료된 사용자입니다."),
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "회원 정보를 찾을 수 없습니다."),
    DUPLICATED_NICKNAME(HttpStatus.BAD_REQUEST, "이미 존재하는 닉네임입니다."),
    NOT_AUTHENTICATED_USER(HttpStatus.BAD_REQUEST, "인증 가능한 사용자가 아닙니다."),

    INVALID_EXPIRED_JWT(HttpStatus.BAD_REQUEST, "이미 만료된 JWT 입니다."),
    INVALID_MALFORMED_JWT(HttpStatus.BAD_REQUEST, "JWT의 구조가 유효하지 않습니다."),
    INVALID_CLAIM_JWT(HttpStatus.BAD_REQUEST, "JWT의 Claim이 유효하지 않습니다."),
    UNSUPPORTED_JWT(HttpStatus.BAD_REQUEST, "지원하지 않는 JWT 형식입니다."),
    INVALID_JWT(HttpStatus.BAD_REQUEST, "JWT가 유효하지 않습니다."),
    INVALID_PROGRESS(HttpStatus.BAD_REQUEST, "존재하지 않는 정보입니다."),

    JWT_NOT_FOUND_IN_DB(HttpStatus.NOT_FOUND, "DB에 JWT 정보가 존재하지 않습니다."),
    JWT_NOT_FOUND_IN_HEADER(HttpStatus.NOT_FOUND, "Header에 JWT 정보가 존재하지 않습니다."),
    JWT_NOT_FOUND_IN_COOKIE(HttpStatus.NOT_FOUND, "Cookie에 JWT 정보가 존재하지 않습니다."),
    REFRESH_TOKEN_NOT_MATCH(HttpStatus.BAD_REQUEST, "DB에 저장되어 있는 Refresh token과 일치하지 않습니다."),

    MULTIPART_FILE_NOT_EXIST(HttpStatus.BAD_REQUEST, "MultipartFile이 전달되지 않았습니다."),
    FILE_NOT_EXIST(HttpStatus.BAD_REQUEST, "해당 파일(이미지)이 존재하지 않습니다."),
    INVALID_FILE_NAME(HttpStatus.BAD_REQUEST, "전달받은 파일(이미지)의 이름이 null이거나 빈 문자열입니다."),
    NOT_SUPPORTED_EXTENSION(HttpStatus.BAD_REQUEST, "지원하지 않는 확장자입니다."),
    NOT_SUPPORTED_IMAGE_TYPE(HttpStatus.BAD_REQUEST, "지원하지 않는 이미지 타입입니다."),
    FILE_NOT_DELETED(HttpStatus.BAD_REQUEST, "파일(이미지)이 정상적으로 삭제되지 않았습니다."),
    FILE_NOT_SAVED(HttpStatus.BAD_REQUEST, "파일(이미지)가 정상적으로 저장되지 않았습니다."),
    FILE_NOT_COPIED(HttpStatus.BAD_REQUEST, "파일(이미지)가 정상적으로 복사되지 않았습니다."),
    IMAGE_NOT_ENCODED(HttpStatus.BAD_REQUEST, "이미지를 인코딩하는 과정에서 오류가 발생했습니다."),
    FILE_MAX_SIZE_EXCEED(HttpStatus.BAD_REQUEST, "파일(이미지)의 크기가 최대 용량을 초과했습니다."),

    GITHUB_CONNECTION_FAILED(HttpStatus.BAD_REQUEST, "Github 연결이 실패했습니다."),
    GITHUB_TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "저장된 Github Token을 찾을 수 없습니다."),
    GITHUB_ID_INCORRECT(HttpStatus.BAD_REQUEST, "소셜로그인에 사용한 Github 계정과 일치하지 않습니다."),
    GITHUB_REPOSITORY_INCORRECT(HttpStatus.BAD_REQUEST, "해당 레포지토리와 연결이 되지 않습니다."),
    GITHUB_PR_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 레포지토리에 PR이 존재하지 않습니다."),

    CAN_NOT_JOIN_INSTANCE(HttpStatus.BAD_REQUEST, "해당 인스턴스에 참여할 수 없습니다."),
    INVALID_JOIN_DATE(HttpStatus.BAD_REQUEST, "인스턴스 시작 당일에는 신규 참여할 수 없습니다."),
    CAN_NOT_QUIT_INSTANCE(HttpStatus.BAD_REQUEST, "해당 인스턴스의 참여를 취소할 수 없습니다."),

    PR_TEMPLATE_NOT_FOUND(HttpStatus.NOT_FOUND, "인증에 사용하는 PR의 내용에 PR Template가 포함되어야 합니다."),
    NOT_ACTIVITY_INSTANCE(HttpStatus.BAD_REQUEST, "진행 중인 챌린지에 대해서만 인증이 가능합니다."),
    NOT_CERTIFICATE_PERIOD(HttpStatus.BAD_REQUEST, "챌린지 인증은 챌린지 진행 기간 내에만 가능합니다. 챌린지 진행 기간인지 확인해주세요."),

    NOT_ENOUGH_POINT(HttpStatus.BAD_REQUEST, "사용자의 보유 포인트가 충분하지 않습니다."),
    ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, "아이템 정보를 찾을 수 없습니다."),
    ORDERS_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자의 아이템 보유 정보를 찾을 수 없습니다."),
    HAS_NO_ITEM(HttpStatus.NOT_FOUND, "해당 아이템을 보유하고 있지 않습니다."),
    CAN_NOT_USE_PASS_ITEM(HttpStatus.BAD_REQUEST, "인증 패스 아이템을 사용할 수 없는 조건입니다."),
    ITEM_CATEGORY_NOT_FOUND(HttpStatus.BAD_REQUEST, "해당 카테고리에 맞는 아이템을 찾을 수 없습니다."),

    ALREADY_PURCHASED(HttpStatus.BAD_REQUEST, "프로필 프레임은 재구매가 불가능 합니다."),
    INVALID_EQUIP_CONDITION(HttpStatus.BAD_REQUEST, "프로필 프레임을 장착할 수 있는 상태가 아닙니다."),
    IN_USE_FRAME_NOT_FOUND(HttpStatus.NOT_FOUND, "사용 중인 프로필 프레임을 찾을 수 없습니다"),
    TOO_MANY_USING_FRAME(HttpStatus.BAD_REQUEST, "사용 중인 프로필 프레임이 너무 많습니다. 장착 해제를 먼저 실행해주세요."),

    CAN_NOT_GET_REWARDS(HttpStatus.BAD_REQUEST, "챌린지 보상을 받을 수 있는 조건이 아닙니다."),
    ALREADY_REWARDED(HttpStatus.BAD_REQUEST, "해당 챌린지 보상은 이미 지급되었습니다.");


    private final HttpStatus status;
    private final String message;
}
