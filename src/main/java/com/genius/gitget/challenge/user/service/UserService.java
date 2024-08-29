package com.genius.gitget.challenge.user.service;

import static com.genius.gitget.global.util.exception.ErrorCode.*;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.genius.gitget.challenge.certification.util.EncryptUtil;
import com.genius.gitget.challenge.user.domain.Role;
import com.genius.gitget.challenge.user.domain.User;
import com.genius.gitget.challenge.user.dto.SignupRequest;
import com.genius.gitget.challenge.user.dto.UserProfileInfo;
import com.genius.gitget.challenge.user.repository.UserRepository;
import com.genius.gitget.global.file.dto.FileResponse;
import com.genius.gitget.global.file.service.FilesManager;
import com.genius.gitget.global.security.dto.AuthResponse;
import com.genius.gitget.global.util.exception.BusinessException;
import com.genius.gitget.global.util.exception.ErrorCode;
import com.genius.gitget.signout.Signout;
import com.genius.gitget.signout.SignoutRepository;
import com.genius.gitget.store.item.domain.Item;
import com.genius.gitget.store.item.service.OrdersService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {
	private final UserRepository userRepository;
	private final OrdersService ordersService;
	private final FilesManager filesManager;
	private final EncryptUtil encryptUtil;
	private final SignoutRepository signoutRepository;

	@Value("${admin.githubId}")
	private List<String> adminIds;

	public User findUserById(Long id) {
		return userRepository.findById(id)
			.orElseThrow(() -> new BusinessException(MEMBER_NOT_FOUND));
	}

	public User findUserByIdentifier(String identifier) {
		return userRepository.findByIdentifier(identifier)
			.orElseThrow(() -> new BusinessException(MEMBER_NOT_FOUND));
	}

	@Transactional
	public Long save(User user) {
		return userRepository.saveAndFlush(user).getId();
	}

	public void delete(Long userId, String identifier, String reason) {
		userRepository.deleteById(userId);
		signoutRepository.save(
			Signout.builder()
				.identifier(identifier)
				.reason(reason)
				.build());
	}

	// 포인트 조회
	public Long getUserPoint(Long userId) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

		return user.getPoint();
	}

	@Transactional
	public Long signup(SignupRequest requestUser) {
		User user = findUserByIdentifier(requestUser.identifier());
		isAlreadyRegistered(user);

		String interest = String.join(",", requestUser.interest());
		user.updateUser(requestUser.nickname(),
			requestUser.information(),
			interest);
		updateRole(user);

		return user.getId();
	}

	private void updateRole(User user) {
		if (adminIds.contains(user.getIdentifier())) {
			user.updateRole(Role.ADMIN);
			return;
		}
		user.updateRole(Role.USER);
	}

	public void isNicknameDuplicate(String nickname) {
		if (userRepository.findByNickname(nickname).isPresent()) {
			throw new BusinessException(DUPLICATED_NICKNAME);
		}
	}

	public String getGithubToken(User user) {
		String githubToken = user.getGithubToken();
		if (githubToken == null || githubToken.isEmpty() || githubToken.isBlank()) {
			throw new BusinessException(GITHUB_TOKEN_NOT_FOUND);
		}
		return encryptUtil.decrypt(githubToken);
	}

	public void isAlreadyRegistered(User user) {
		if (user.getRole() != Role.NOT_REGISTERED) {
			throw new BusinessException(ALREADY_REGISTERED);
		}
	}

	public AuthResponse getUserAuthInfo(String identifier) {
		User user = userRepository.findByIdentifier(identifier)
			.orElseThrow(() -> new BusinessException(MEMBER_NOT_FOUND));
		Item usingFrame = ordersService.getUsingFrameItem(user.getId());
		return new AuthResponse(user.getRole(), usingFrame.getId());
	}

	public UserProfileInfo getUserProfileInfo(User user) {
		Long frameId = ordersService.getUsingFrameItem(user.getId()).getId();
		FileResponse fileResponse = filesManager.convertToFileResponse(user.getFiles());

		return UserProfileInfo.createByEntity(user, frameId, fileResponse);
	}
}