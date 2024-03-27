package com.genius.gitget.challenge.user.service;

import static com.genius.gitget.global.util.exception.ErrorCode.ALREADY_REGISTERED;
import static com.genius.gitget.global.util.exception.ErrorCode.GITHUB_TOKEN_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.genius.gitget.challenge.user.domain.Role;
import com.genius.gitget.challenge.user.domain.User;
import com.genius.gitget.challenge.user.dto.SignupRequest;
import com.genius.gitget.challenge.user.repository.UserRepository;
import com.genius.gitget.global.file.domain.FileType;
import com.genius.gitget.global.file.domain.Files;
import com.genius.gitget.global.security.constants.ProviderInfo;
import com.genius.gitget.global.security.dto.AuthResponse;
import com.genius.gitget.global.util.exception.BusinessException;
import com.genius.gitget.global.util.exception.ErrorCode;
import com.genius.gitget.store.item.domain.EquipStatus;
import com.genius.gitget.store.item.domain.Item;
import com.genius.gitget.store.item.domain.ItemCategory;
import com.genius.gitget.store.item.domain.Orders;
import com.genius.gitget.store.item.repository.ItemRepository;
import com.genius.gitget.store.item.repository.OrdersRepository;
import com.genius.gitget.util.file.FileTestUtil;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.EnumSource.Mode;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@SpringBootTest
@Transactional
@Slf4j
class UserServiceTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private OrdersRepository ordersRepository;
    @Autowired
    private UserService userService;

    @Test
    @DisplayName("특정 사용자를 가입한 이후, 사용자를 찾았을 때 가입했을 때 입력한 정보와 일치해야 한다.")
    public void should_matchValues_when_signupUser() {
        //given
        String identifier = "identifier";
        saveUnsignedUser(identifier, Role.NOT_REGISTERED);
        SignupRequest signupRequest = SignupRequest.builder()
                .identifier(identifier)
                .nickname("nickname")
                .information("information")
                .interest(List.of("관심사1", "관심사2"))
                .build();
        MultipartFile multipartFile = FileTestUtil.getMultipartFile("profile");

        //when
        User user = userService.findUserByIdentifier(identifier);

        Long signupUserId = userService.signup(signupRequest, multipartFile);
        User foundUser = userService.findUserById(signupUserId);

        //then
        assertThat(user.getIdentifier()).isEqualTo(foundUser.getIdentifier());
        assertThat(user.getNickname()).isEqualTo(foundUser.getNickname());
        assertThat(user.getProviderInfo()).isEqualTo(foundUser.getProviderInfo());
        assertThat(user.getInformation()).isEqualTo(foundUser.getInformation());
        assertThat(user.getTags()).isEqualTo(foundUser.getTags());
        assertThat(user.getRole()).isEqualTo(Role.USER);

        Files files = user.getFiles().get();
        assertThat(files.getFileType()).isEqualTo(FileType.PROFILE);
        assertThat(files.getOriginalFilename()).contains(multipartFile.getOriginalFilename());
    }

    @Test
    @DisplayName("어드민 깃허브 계정에 해당하는 사용자가 가입을 요청하는 경우, ROLE이 자동으로 ADMIN으로 설정된다.")
    public void should_setRoleAdmin_when_identifierMatchesWithAdmin() {
        //given
        String identifier = "SSung023";
        saveUnsignedUser(identifier, Role.NOT_REGISTERED);
        SignupRequest signupRequest = SignupRequest.builder()
                .identifier(identifier)
                .nickname("nickname")
                .information("information")
                .interest(List.of("관심사1", "관심사2"))
                .build();
        MultipartFile multipartFile = FileTestUtil.getMultipartFile("profile");

        //when
        Long signupUserId = userService.signup(signupRequest, multipartFile);
        User signupUser = userService.findUserById(signupUserId);

        //then
        assertThat(signupUser.getRole()).isEqualTo(Role.ADMIN);
    }

    @Test
    @DisplayName("사용자가 한 차례 회원가입을 진행한 후, 한 번 더 회원가입을 요청하면 예외가 발생해야 한다.")
    public void should_throwException_when_requestRegisterAgain() {
        //given
        String identifier = "identifier";
        saveUnsignedUser(identifier, Role.NOT_REGISTERED);
        SignupRequest signupRequest = SignupRequest.builder()
                .identifier(identifier)
                .nickname("nickname")
                .information("information")
                .interest(List.of("관심사1", "관심사2"))
                .build();
        MultipartFile multipartFile = FileTestUtil.getMultipartFile("profile");

        //when
        User user = userService.findUserByIdentifier(identifier);
        Long signupUserId = userService.signup(signupRequest, multipartFile);

        //then
        assertThatThrownBy(() -> userService.signup(signupRequest, multipartFile))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ErrorCode.ALREADY_REGISTERED.getMessage());
    }

    @Test
    @DisplayName("저장되어 있는 사용자를 PK를 통해 찾을 수 있다.")
    public void should_returnUser_when_passPK() {
        //given
        User user = getSavedUser();

        //when
        User foundUser = userService.findUserById(user.getId());

        //then
        assertThat(user.getId()).isEqualTo(foundUser.getId());
        assertThat(user.getIdentifier()).isEqualTo(foundUser.getIdentifier());
        assertThat(user.getProviderInfo()).isEqualTo(foundUser.getProviderInfo());
        assertThat(user.getNickname()).isEqualTo(foundUser.getNickname());
        assertThat(user.getRole()).isEqualTo(foundUser.getRole());
        assertThat(user.getInformation()).isEqualTo(foundUser.getInformation());
        assertThat(user.getTags()).isEqualTo(foundUser.getTags());
    }

    @Test
    @DisplayName("저장되어 있는 사용자를 identifier를 통해 찾을 수 있다.")
    public void should_returnUser_when_passIdentifier() {
        //given
        User user = getSavedUser();

        //when
        User foundUser = userService.findUserByIdentifier(user.getIdentifier());

        //then
        assertThat(user.getId()).isEqualTo(foundUser.getId());
        assertThat(user.getIdentifier()).isEqualTo(foundUser.getIdentifier());
        assertThat(user.getProviderInfo()).isEqualTo(foundUser.getProviderInfo());
        assertThat(user.getNickname()).isEqualTo(foundUser.getNickname());
        assertThat(user.getRole()).isEqualTo(foundUser.getRole());
        assertThat(user.getInformation()).isEqualTo(foundUser.getInformation());
        assertThat(user.getTags()).isEqualTo(foundUser.getTags());
    }

    @Test
    @DisplayName("이미 등록되어 있는 닉네임인 경우 예외가 발생한다.")
    public void should_throwException_when_nicknameIsDuplicated() {
        //given
        User user = getSavedUser();

        //when & then
        assertThatThrownBy(() -> userService.isNicknameDuplicate(user.getNickname()))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ErrorCode.DUPLICATED_NICKNAME.getMessage());
    }

    @ParameterizedTest
    @DisplayName("User 엔티티로부터 깃허브 토큰을 불러올 때 길이가 0이거나, 공백으로 이루어져 있다면 예외가 발생한다.")
    @ValueSource(strings = {"", "  "})
    public void should_throwException_when_githubTokenInvalid(String githubToken) {
        //given
        User user = getSavedUser();

        //when
        user.updateGithubPersonalToken(githubToken);

        //then
        assertThatThrownBy(() -> userService.getGithubToken(user))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(GITHUB_TOKEN_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("User 엔티티로부터 깃허브 토큰을 불러올 때 null 이라면 예외가 발생한다.")
    public void should_throwException_when_githubTokenNull() {
        //given
        User user = getSavedUser();

        //when
        user.updateGithubPersonalToken(null);

        //then
        assertThatThrownBy(() -> userService.getGithubToken(user))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(GITHUB_TOKEN_NOT_FOUND.getMessage());
    }

    @ParameterizedTest
    @DisplayName("Role이 NOT_REGISTERED가 아닌 경우에는 이미 등록이 되어 있다는 예외가 발생한다.")
    @EnumSource(mode = Mode.INCLUDE, names = {"USER", "ADMIN"})
    public void should_throwException_when_roleIsNotNOT_REGISTERED(Role role) {
        assertThatThrownBy(
                () -> userService.isAlreadyRegistered(saveUnsignedUser("identifier", role)))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ALREADY_REGISTERED.getMessage());
    }

    @Test
    @DisplayName("사용자가 프로필 프레임을 장착하고 있지 않을 때, 사용자의 ROLE과 프레임의 PK는 null로 받는다.")
    public void should_getUserInfo_when_notEquipFrame() {
        //given
        User user = getSavedUser();

        //when
        AuthResponse authResponse = userService.getUserInfo(user.getIdentifier());

        //then
        assertThat(authResponse.role()).isEqualTo(Role.USER);
        assertThat(authResponse.frameId()).isEqualTo(null);
    }

    @Test
    @DisplayName("사용자가 프로필 프레임을 장착하고 있을 때, 사용자의 ROLE과 사용 중인 프레임의 PK를 받을 수 있다.")
    public void should_getUserInfo_when_equipFrame() {
        //given
        User user = getSavedUser();
        Item item = getSavedItem(ItemCategory.PROFILE_FRAME);
        Orders orders = getSavedOrders(user, item);
        orders.updateEquipStatus(EquipStatus.IN_USE);

        //when
        AuthResponse authResponse = userService.getUserInfo(user.getIdentifier());

        //then
        assertThat(authResponse.role()).isEqualTo(Role.USER);
        assertThat(authResponse.frameId()).isEqualTo(item.getId());
    }


    private User saveUnsignedUser(String identifier, Role role) {
        return userRepository.save(User.builder()
                .role(role)
                .providerInfo(ProviderInfo.NAVER)
                .identifier(identifier)
                .build());
    }

    private User getSavedUser() {
        return userRepository.save(User.builder()
                .identifier("identifier")
                .role(Role.USER)
                .information("information")
                .tags("interest1,interest2")
                .nickname("nickname")
                .providerInfo(ProviderInfo.GITHUB)
                .build());
    }

    private Item getSavedItem(ItemCategory itemCategory) {
        return itemRepository.save(
                Item.builder()
                        .itemCategory(itemCategory)
                        .build()
        );
    }

    private Orders getSavedOrders(User user, Item item) {
        Orders orders = Orders.createDefault(1, item.getItemCategory());
        orders.setUser(user);
        orders.setItem(item);
        return ordersRepository.save(orders);
    }
}