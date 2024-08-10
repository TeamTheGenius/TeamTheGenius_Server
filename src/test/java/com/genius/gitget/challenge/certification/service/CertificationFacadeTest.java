package com.genius.gitget.challenge.certification.service;

import com.genius.gitget.challenge.certification.facade.CertificationFacade;
import com.genius.gitget.challenge.certification.facade.GithubFacade;
import com.genius.gitget.challenge.certification.repository.CertificationRepository;
import com.genius.gitget.challenge.instance.repository.InstanceRepository;
import com.genius.gitget.challenge.participant.repository.ParticipantRepository;
import com.genius.gitget.challenge.user.repository.UserRepository;
import com.genius.gitget.store.item.repository.ItemRepository;
import com.genius.gitget.store.item.repository.OrdersRepository;
import java.time.LocalDate;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@SpringBootTest
@Transactional
@ActiveProfiles({"github"})
class CertificationFacadeTest {
    @Autowired
    private CertificationFacade certificationFacade;
    @Autowired
    private GithubFacade githubFacade;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private InstanceRepository instanceRepository;
    @Autowired
    private ParticipantRepository participantRepository;
    @Autowired
    private CertificationRepository certificationRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private OrdersRepository ordersRepository;

    @Value("${github.yeon-personalKey}")
    private String personalKey;

    @Value("${github.yeon-githubId}")
    private String githubId;

    @Value("${github.yeon-repository}")
    private String targetRepo;

    private LocalDate targetDate = LocalDate.of(2024, 2, 5);


    @Nested
    @DisplayName("한 주 간의 인증 내역 조회 시")
    class context_ {
        @Nested
        @DisplayName("본인의 정보를 조회할 때")
        class describe_ {
            @Test
            @DisplayName("챌린지의 시작일자가 월요일이 아니고 첫째주일 때, 시작일부터 현재 일자까지의 인증 내역을 반환해야 한다.")
            public void it_return_() {

            }

            @Test
            @DisplayName("저장된 인증 내역이 없을 때에도 더미 데이터를 포함하여 연속적인 데이터를 받을 수 있다.")
            public void it_return_continuous_data_saved_data_not_exist() {

            }
        }

        @Nested
        @DisplayName("다른 사람들의 한 주간 인증 내역 조회 시")
        class describe_inquiry_others {
            @Test
            @DisplayName("본인의 값을 제외하고 반환받아야 한다.")
            public void it_return_except_mine() {

            }
        }
    }

    @Nested
    @DisplayName("인증 내역 전체 조회 시")
    class context_inquiry_whole_certification {
        @Nested
        @DisplayName("인스턴스의 상태가 PREACTIVITY일 때")
        class describe_instance_is_preActivity {
            @Test
            @DisplayName("반환하는 데이터의 개수는 0개여야 한다.")
            public void it_return_nothing() {

            }
        }

        @Nested
        @DisplayName("인스턴스의 상태가 ACTIVITY일 떄")
        class describe_instance_is_ACTIVITY {
            @Test
            @DisplayName("반환하는 데이터의 개수는 시작일자부터 현재일자까지의 일차와 같아야 한다.")
            public void it_returns_data_size_current_attempt() {

            }
        }

        @Nested
        @DisplayName("인스턴스의 상태가 DONE일 때")
        class describe_instance_is_Done {
            @Test
            @DisplayName("반환하는 데이터의 개수는 인스턴스의 전체 일차와 같아야 한다.")
            public void it_returns_data_size_total_attempt() {
            }
        }
    }

    @Nested
    @DisplayName("인증 패스 시도 시")
    class context_try_pass_certification {
        @Nested
        @DisplayName("인스턴스의 인증 가능 조건 확인 시")
        class describe_validate_instance_certification_condition {
            @Test
            @DisplayName("인스턴스의 상태가 ACTIVITY이고, 진행일에 해당한다면 인증 패스 처리가 된다.")
            public void it_pass_certification_when_condition_valid() {

            }

            @Test
            @DisplayName("인스턴스의 상태가 ACTIVITY가 아니라면, NOT_ACTIVITY_INSTANCE 예외가 발생한다.")
            public void it_throws_NOT_ACTIVITY_INSTANCE_exception() {
            }

            @Test
            @DisplayName("현재 일자가 인스턴스 진행일 사이가 아니라면, NOT_CERTIFICATE_PERIOD 예외가 발생한다.")
            public void it_throws_NOT_CERTIFICATE_PERIOD_exception() {
            }
        }

        @Nested
        @DisplayName("인증 객체의 인증 가능 조건 확인 시")
        class describe_validate_certification_condition {
            @Test
            @DisplayName("인증의 상태가 NOT_YET이 아니라면 CAN_NOT_USE_PASS_ITEM 예외가 발생한다.")
            public void it_throws_exception_when_certificateStatus_not_NOT_YET() {

            }

            @Test
            @DisplayName("인증의 상태가 NOT_YET이라면 예외가 발생하지 않는다.")
            public void it_not_throws_exception_when_certificateStatus_is_NOT_YET() {
            }
        }

        @Nested
        @DisplayName("인증 객체의 존재 여부 확인 시")
        class describe_check_certification_exist {
            @Test
            @DisplayName("인증 객체가 존재하지 않았다면 인증 객체를 새로 저장한다.")
            public void it_save_new_object_when_not_exist() {

            }

            @Test
            @DisplayName("인증 객체가 존재했다면 PASSED로 상태를 업데이트한다.")
            public void it_update_certificateStatus_to_PASSED() {

            }
        }

        @Nested
        @DisplayName("인증이 가능한 조건이라면")
        class describe_pass_condition_valid {
            @Test
            @DisplayName("인증 객체의 상태가 PASSED로 업데이트된다.")
            public void it_update_certificateStatus_to_PASSED() {
            }
        }
    }

    @Nested
    @DisplayName("인증 갱신 시도 시")
    class context_try_update_certification {
        @Nested
        @DisplayName("인스턴스의 인증 가능 조건 확인 시")
        class describe_validate_certification_instance_condition {
            @Test
            @DisplayName("인스턴스의 상태가 ACTIVITY이고, 인스턴스 진행일 사이라면 예외가 발생하지 않는다.")
            public void it_not_throw_exception_when_condition_valid() {

            }

            @Test
            @DisplayName("인스턴스의 상태가 ACTIVITY가 아니라면, NOT_ACTIVITY_INSTANCE 예외가 발생한다.")
            public void it_throws_NOT_ACTIVITY_INSTANCE_exception() {

            }

            @Test
            @DisplayName("현재 일자가 인스턴스 진행 일 사이가 아니라면 NOT_CERTIFICATE_PERIOD 예외가 발생한다.")
            public void it_throws_NOT_CERTIFICATE_PERIOD_exception() {
            }
        }

        @Nested
        @DisplayName("인증에 사용할 PR 확인 시")
        class describe_check_pr {
            @Test
            @DisplayName("PR의 내용에 아무것도 없다면 결과에 포함되지 않는다.")
            public void it_does_not_contain_PR_body_empty() {

            }

            @Test
            @DisplayName("PR에 인스턴스의 PR Template가 없다면 결과에 포함되지 않는다.")
            public void it_does_not_contain_PR_template_not_exist() {

            }

            @Test
            @DisplayName("PR 인증 조건에 부합한다면 PR 주소를 반환한다.")
            public void it_returns_pr_link_when_pr_valid() {

            }

            @Test
            @DisplayName("PR 필터 결과의 사이즈가 0이라면 예외가 발생해야 한다.")
            public void it_throws_exception_when_filtered_size_is_zero() {

            }
        }

        @Nested
        @DisplayName("인증 객체 확인 시")
        class describe_check_certification_object {
            @Test
            @DisplayName("인증 객체의 상태가 PASSED라면 ALREADY_PASSED_CERTIFICATION 예외가 발생한다.")
            public void it_throws_ALREADY_PASSED_CERTIFICATION_exception() {

            }

            @Test
            @DisplayName("인증 상태가 NOT_YET이라면 상태가 CERTIFICATED로 갱신된다.")
            public void it_update_to_CERTIFICATED() {

            }

            @Test
            @DisplayName("인증 상태가 CERTIFICATED라면 certificationLinks의 내용이 갱신된다.")
            public void it_update_certificationLinks() {

            }
        }
    }

    @Nested
    @DisplayName("인증 관련 정보 조회 시")
    class context_inquiry_certification_information {
        @Nested
        @DisplayName("인스턴스의 상태가 모두 PREACTIVITY라면")
        class describe_instance_is_all_preActivity {
            @Test
            @DisplayName("성공/실패의 값은 모두 0이고, 남은일자는 전체 회차여야 한다.")
            public void it_returns_correct_values() {

            }
        }
    }
}