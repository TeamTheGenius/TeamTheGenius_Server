package com.genius.gitget.challenge.certification.service;

import static com.genius.gitget.challenge.certification.domain.CertificateStatus.CERTIFICATED;
import static com.genius.gitget.challenge.certification.domain.CertificateStatus.NOT_YET;
import static com.genius.gitget.challenge.certification.domain.CertificateStatus.PASSED;
import static com.genius.gitget.challenge.instance.domain.Progress.ACTIVITY;
import static com.genius.gitget.challenge.instance.domain.Progress.DONE;
import static com.genius.gitget.challenge.participant.domain.JoinResult.SUCCESS;
import static com.genius.gitget.challenge.user.domain.Role.USER;
import static com.genius.gitget.global.util.exception.ErrorCode.ALREADY_PASSED_CERTIFICATION;
import static com.genius.gitget.global.util.exception.ErrorCode.CAN_NOT_USE_PASS_ITEM;
import static com.genius.gitget.global.util.exception.ErrorCode.NOT_ACTIVITY_INSTANCE;
import static com.genius.gitget.global.util.exception.ErrorCode.NOT_CERTIFICATE_PERIOD;
import static com.genius.gitget.store.item.domain.ItemCategory.CERTIFICATION_PASSER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.genius.gitget.challenge.certification.domain.CertificateStatus;
import com.genius.gitget.challenge.certification.domain.Certification;
import com.genius.gitget.challenge.certification.dto.CertificationInformation;
import com.genius.gitget.challenge.certification.dto.CertificationRequest;
import com.genius.gitget.challenge.certification.dto.CertificationResponse;
import com.genius.gitget.challenge.certification.dto.TotalResponse;
import com.genius.gitget.challenge.certification.dto.WeekResponse;
import com.genius.gitget.challenge.certification.facade.CertificationFacade;
import com.genius.gitget.challenge.certification.facade.GithubFacade;
import com.genius.gitget.challenge.certification.repository.CertificationRepository;
import com.genius.gitget.challenge.instance.domain.Instance;
import com.genius.gitget.challenge.instance.domain.Progress;
import com.genius.gitget.challenge.instance.repository.InstanceRepository;
import com.genius.gitget.challenge.participant.domain.Participant;
import com.genius.gitget.challenge.participant.repository.ParticipantRepository;
import com.genius.gitget.challenge.user.domain.User;
import com.genius.gitget.challenge.user.repository.UserRepository;
import com.genius.gitget.global.util.exception.BusinessException;
import com.genius.gitget.store.item.domain.Item;
import com.genius.gitget.store.item.domain.Orders;
import com.genius.gitget.store.item.repository.ItemRepository;
import com.genius.gitget.store.item.repository.OrdersRepository;
import com.genius.gitget.util.certification.CertificationFactory;
import com.genius.gitget.util.instance.InstanceFactory;
import com.genius.gitget.util.participant.ParticipantFactory;
import com.genius.gitget.util.store.StoreFactory;
import com.genius.gitget.util.user.UserFactory;
import java.time.LocalDate;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.EnumSource.Mode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
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
    private String githubToken;

    @Value("${github.yeon-githubId}")
    private String githubId;

    @Value("${github.yeon-repository}")
    private String targetRepo;

    private LocalDate currentDate;
    private User user;
    private Instance instance;
    private Participant participant;

    @BeforeEach
    void setup() {
        user = userRepository.save(UserFactory.createByInfo(githubId, USER));
        githubFacade.registerGithubPersonalToken(user, githubToken);
    }

    @Nested
    @DisplayName("한 주 간의 인증 내역 조회 시")
    class context_inquiry_week_certifications {
        @Nested
        @DisplayName("인스턴스가 아직 시작하지 않았고, 본인의 정보 조회 시")
        class describe_instance_preActivity_inquiry_mine {
            @BeforeEach
            void setup() {
                currentDate = LocalDate.now();
                instance = instanceRepository.save(InstanceFactory.createPreActivity(10));
                participant = participantRepository.save(ParticipantFactory.createPreActivity(user, instance));
            }

            @Test
            @DisplayName("반환한 데이터의 개수가 0개여야 한다.")
            public void it_return_nothing() {
                WeekResponse weekResponses = certificationFacade.getMyWeekCertifications(participant.getId(),
                        currentDate);

                assertThat(weekResponses.certifications().size()).isEqualTo(0);
            }
        }

        @Nested
        @DisplayName("인스턴스가 진행 중이고, 본인의 정보를 조회할 때")
        class describe_instance_activity_inquiry_mine {
            @BeforeEach
            void setup() {
                currentDate = LocalDate.of(2024, 8, 13);
            }

            @Test
            @DisplayName("챌린지의 시작일자가 월요일이 아니고 첫째주일 때, 시작일부터 현재 일자까지의 인증 내역을 반환해야 한다.")
            public void it_return_current_certifications() {
                int passedDays = 3;

                instance = instanceRepository.save(InstanceFactory.createByInfo(currentDate, ACTIVITY));
                participant = participantRepository.save(ParticipantFactory.createProcessing(user, instance));

                WeekResponse weekResponses = certificationFacade.getMyWeekCertifications(participant.getId(),
                        currentDate.plusDays(passedDays));

                assertThat(weekResponses.certifications().size()).isEqualTo(passedDays + 1);
            }

            @Test
            @DisplayName("저장된 인증 내역이 없을 때에도 더미 데이터를 포함하여 연속적인 데이터를 받을 수 있다.")
            public void it_return_continuous_data_saved_data_not_exist() {
                int passedDays = 3;

                instance = instanceRepository.save(InstanceFactory.createByInfo(currentDate, ACTIVITY));
                participant = participantRepository.save(ParticipantFactory.createProcessing(user, instance));

                WeekResponse weekResponses = certificationFacade.getMyWeekCertifications(participant.getId(),
                        currentDate.plusDays(passedDays));

                assertThat(weekResponses.certifications().size()).isEqualTo(passedDays + 1);
            }
        }

        @Nested
        @DisplayName("다른 사람들의 한 주간 인증 내역 조회 시")
        class describe_inquiry_others {
            User other;

            @BeforeEach
            void setup() {
                other = userRepository.save(UserFactory.createByInfo("identifier2", USER));
                instance = instanceRepository.save(InstanceFactory.createActivity(10));
                participantRepository.save(ParticipantFactory.createProcessing(user, instance));
                participantRepository.save(ParticipantFactory.createProcessing(other, instance));
            }


            @Test
            @DisplayName("본인의 값을 제외하고 반환받아야 한다.")
            public void it_return_except_mine() {
                currentDate = LocalDate.now();

                Slice<WeekResponse> weekResponses = certificationFacade.getOthersWeekCertifications(
                        user.getId(), instance.getId(), currentDate,
                        PageRequest.of(0, 10));

                assertThat(weekResponses.getContent().size()).isEqualTo(1);
            }
        }
    }

    @Nested
    @DisplayName("인증 내역 전체 조회 시")
    class context_inquiry_whole_certification {
        @Nested
        @DisplayName("인스턴스의 상태가 PREACTIVITY일 때")
        class describe_instance_is_preActivity {
            @BeforeEach
            void setup() {
                currentDate = LocalDate.now();
                instance = instanceRepository.save(InstanceFactory.createPreActivity(10));
                participant = participantRepository.save(ParticipantFactory.createPreActivity(user, instance));
            }

            @Test
            @DisplayName("반환하는 데이터의 개수는 0개여야 한다.")
            public void it_return_nothing() {
                TotalResponse totalResponse = certificationFacade.getTotalCertification(participant.getId(),
                        currentDate);

                assertThat(totalResponse.certifications().size()).isEqualTo(0);
            }
        }

        @Nested
        @DisplayName("인스턴스의 상태가 ACTIVITY일 떄")
        class describe_instance_is_ACTIVITY {
            @BeforeEach
            void setup() {
                LocalDate startedDate = LocalDate.of(2024, 8, 5);
                currentDate = LocalDate.of(2024, 8, 13);
                instance = instanceRepository.save(InstanceFactory.createByInfo(startedDate, ACTIVITY));
                participant = participantRepository.save(ParticipantFactory.createProcessing(user, instance));
            }

            @Test
            @DisplayName("반환하는 데이터의 개수는 시작일자부터 현재일자까지의 일차와 같아야 한다.")
            public void it_returns_data_size_current_attempt() {
                TotalResponse totalResponse = certificationFacade.getTotalCertification(participant.getId(),
                        currentDate);

                assertThat(totalResponse.certifications().size()).isEqualTo(9);
            }
        }

        @Nested
        @DisplayName("인스턴스의 상태가 DONE일 때")
        class describe_instance_is_Done {
            @BeforeEach
            void setup() {
                LocalDate startedDate = LocalDate.of(2024, 8, 5);
                currentDate = LocalDate.of(2024, 8, 20);
                instance = instanceRepository.save(InstanceFactory.createByInfo(startedDate, DONE));
                participant = participantRepository.save(
                        ParticipantFactory.createByJoinResult(user, instance, SUCCESS));
            }

            @Test
            @DisplayName("반환하는 데이터의 개수는 인스턴스의 전체 일차와 같아야 한다.")
            public void it_returns_data_size_total_attempt() {
                TotalResponse totalResponse = certificationFacade.getTotalCertification(participant.getId(),
                        currentDate);

                int totalAttempt = instance.getTotalAttempt();

                assertThat(totalResponse.totalAttempts()).isEqualTo(totalAttempt);
                assertThat(totalResponse.certifications().size()).isEqualTo(totalAttempt);
            }
        }
    }

    @Nested
    @DisplayName("인증 갱신 시도 시")
    class context_try_update_certification {
        LocalDate startedDate;

        @Nested
        @DisplayName("인스턴스의 인증 가능 조건 확인 시")
        class describe_validate_certification_instance_condition {
            @BeforeEach
            void setup() {
                currentDate = LocalDate.of(2024, 2, 5);
                startedDate = currentDate.minusDays(5);

                instance = instanceRepository.save(InstanceFactory.createByInfo(startedDate, ACTIVITY));
                participant = participantRepository.save(ParticipantFactory.createProcessing(user, instance));
                instance.setInstanceUUID("instanceUUID");
                participant.updateRepository(targetRepo);
            }

            @Test
            @DisplayName("인스턴스의 상태가 ACTIVITY이고, 인스턴스 진행일 사이라면 예외가 발생하지 않는다.")
            public void it_not_throw_exception_when_condition_valid() {
                instance = instanceRepository.save(InstanceFactory.createByInfo(currentDate, ACTIVITY));
                participant = participantRepository.save(ParticipantFactory.createProcessing(user, instance));
                participant.updateRepository(targetRepo);

                assertThatNoException().isThrownBy(() -> {
                    certificationFacade.updateCertification(user,
                            CertificationRequest.of(instance.getId(), currentDate));
                });
            }

            @ParameterizedTest
            @DisplayName("인스턴스의 상태가 ACTIVITY가 아니라면, NOT_ACTIVITY_INSTANCE 예외가 발생한다.")
            @EnumSource(mode = Mode.INCLUDE, names = {"PREACTIVITY", "DONE"})
            public void it_throws_NOT_ACTIVITY_INSTANCE_exception(Progress progress) {
                instance = instanceRepository.save(InstanceFactory.createByInfo(startedDate, progress));
                participant = participantRepository.save(ParticipantFactory.createProcessing(user, instance));
                participant.updateRepository(targetRepo);

                assertThatThrownBy(() -> certificationFacade.updateCertification(user,
                        CertificationRequest.of(instance.getId(), currentDate)))
                        .isInstanceOf(BusinessException.class)
                        .hasMessageContaining(NOT_ACTIVITY_INSTANCE.getMessage());
            }

            @Test
            @DisplayName("현재 일자가 인스턴스 진행 일 사이가 아니라면 NOT_CERTIFICATE_PERIOD 예외가 발생한다.")
            public void it_throws_NOT_CERTIFICATE_PERIOD_exception() {
                currentDate = startedDate.minusDays(1);

                instance = instanceRepository.save(InstanceFactory.createByInfo(startedDate, ACTIVITY));
                participant = participantRepository.save(ParticipantFactory.createProcessing(user, instance));
                participant.updateRepository(targetRepo);

                assertThatThrownBy(() -> certificationFacade.updateCertification(user,
                        CertificationRequest.of(instance.getId(), currentDate)))
                        .isInstanceOf(BusinessException.class)
                        .hasMessageContaining(NOT_CERTIFICATE_PERIOD.getMessage());
            }
        }

        @Nested
        @DisplayName("인증에 사용할 PR 확인 시")
        class describe_check_pr {
            CertificationRequest certificationRequest;

            @Test
            @DisplayName("PR의 body가 null이거나 empty하다면 인증 결과가 NOT_YET으로 유지된다.")
            public void it_does_not_contain_PR_body_empty() {
                currentDate = LocalDate.of(2024, 2, 25);
                startedDate = currentDate.minusDays(10);

                instance = instanceRepository.save(InstanceFactory.createByInfo(startedDate, ACTIVITY));
                participant = participantRepository.save(ParticipantFactory.createProcessing(user, instance));
                instance.setInstanceUUID("instanceUUID");
                participant.updateRepository(targetRepo);

                certificationRequest = CertificationRequest.of(instance.getId(), currentDate);
                CertificationResponse certificationResponse = certificationFacade.updateCertification(user,
                        certificationRequest);

                assertThat(certificationResponse.certificateStatus()).isEqualTo(NOT_YET);
            }

            @Test
            @DisplayName("PR의 body에 PR Template가 없다면 인증 결과가 NOT_YET 으로 유지된다.")
            public void it_does_not_contain_PR_template_not_exist() {
                currentDate = LocalDate.of(2024, 3, 12);
                startedDate = currentDate.minusDays(10);

                instance = instanceRepository.save(InstanceFactory.createByInfo(startedDate, ACTIVITY));
                participant = participantRepository.save(ParticipantFactory.createProcessing(user, instance));
                instance.setInstanceUUID("instanceUUID");
                participant.updateRepository(targetRepo);

                certificationRequest = CertificationRequest.of(instance.getId(), currentDate);
                CertificationResponse certificationResponse = certificationFacade.updateCertification(user,
                        certificationRequest);

                assertThat(certificationResponse.certificateStatus()).isEqualTo(NOT_YET);
            }

            @Test
            @DisplayName("PR 인증 조건에 부합한다면 인증 결과를 CERTIFICATED 로 갱신한다.")
            public void it_returns_pr_link_when_pr_valid() {
                currentDate = LocalDate.of(2024, 8, 11);
                startedDate = currentDate.minusDays(10);

                instance = instanceRepository.save(InstanceFactory.createByInfo(startedDate, ACTIVITY));
                participant = participantRepository.save(ParticipantFactory.createProcessing(user, instance));
                instance.setInstanceUUID("instanceUUID");
                participant.updateRepository(targetRepo);

                certificationRequest = CertificationRequest.of(instance.getId(), currentDate);
                CertificationResponse certificationResponse = certificationFacade.updateCertification(user,
                        certificationRequest);

                assertThat(certificationResponse.certificateStatus()).isEqualTo(CertificateStatus.CERTIFICATED);
            }
        }

        @Nested
        @DisplayName("인증 객체 확인 시")
        class describe_check_certification_object {
            Certification certification;

            @BeforeEach
            void setup() {
                currentDate = LocalDate.of(2024, 8, 11);
                startedDate = currentDate.minusDays(10);

                instance = instanceRepository.save(InstanceFactory.createByInfo(startedDate, ACTIVITY));
                participant = participantRepository.save(ParticipantFactory.createProcessing(user, instance));
                instance.setInstanceUUID("instanceUUID");
                participant.updateRepository(targetRepo);
            }

            @Test
            @DisplayName("인증 객체의 상태가 PASSED라면 ALREADY_PASSED_CERTIFICATION 예외가 발생한다.")
            public void it_throws_ALREADY_PASSED_CERTIFICATION_exception() {
                certification = certificationRepository.save(
                        CertificationFactory.createPassed(participant, currentDate));

                assertThatThrownBy(() -> certificationFacade.updateCertification(user,
                        CertificationRequest.of(instance.getId(), currentDate)))
                        .isInstanceOf(BusinessException.class)
                        .hasMessageContaining(ALREADY_PASSED_CERTIFICATION.getMessage());
            }

            @Test
            @DisplayName("인증 상태가 NOT_YET이라면 상태가 CERTIFICATED로 갱신된다.")
            public void it_update_to_CERTIFICATED() {
                certification = certificationRepository.save(
                        CertificationFactory.createNotYet(participant, currentDate)
                );

                CertificationResponse certificationResponse = certificationFacade.updateCertification(user,
                        CertificationRequest.of(instance.getId(), currentDate));

                assertThat(certificationResponse.certificateStatus()).isEqualTo(CertificateStatus.CERTIFICATED);
                assertThat(certificationResponse.certificatedAt()).isEqualTo(currentDate);
            }

            @Test
            @DisplayName("인증 상태가 CERTIFICATED라면 certificationLinks의 내용이 갱신된다.")
            public void it_update_certificationLinks() {
                certification = certificationRepository.save(
                        CertificationFactory.createCertificated(participant, currentDate)
                );

                CertificationResponse certificationResponse = certificationFacade.updateCertification(user,
                        CertificationRequest.of(instance.getId(), currentDate));

                assertThat(certificationResponse.certificateStatus()).isEqualTo(CertificateStatus.CERTIFICATED);
                assertThat(certificationResponse.prLinks()).isNotEmpty();
                assertThat(certificationResponse.prCount()).isNotZero();
            }
        }
    }

    @Nested
    @DisplayName("인증 패스 시도 시")
    class context_try_pass_certification {
        LocalDate startedDate;
        Item item;
        Orders orders;
        Certification certification;
        CertificationRequest certificationRequest;

        @BeforeEach
        void setup() {
            currentDate = LocalDate.of(2024, 2, 5);
            startedDate = currentDate.minusDays(5);

            instance = instanceRepository.save(InstanceFactory.createByInfo(startedDate, ACTIVITY));
            participant = participantRepository.save(ParticipantFactory.createProcessing(user, instance));
            instance.setInstanceUUID("instanceUUID");
            participant.updateRepository(targetRepo);

            item = itemRepository.save(StoreFactory.createItem(CERTIFICATION_PASSER));
            orders = ordersRepository.save(StoreFactory.createOrders(user, item, CERTIFICATION_PASSER, 3));

            certificationRequest = CertificationRequest.of(instance.getId(), currentDate);
        }

        @Nested
        @DisplayName("인스턴스의 인증 가능 조건 확인 시")
        class describe_validate_instance_certification_condition {
            @Test
            @DisplayName("인스턴스의 상태가 ACTIVITY이고, 진행일에 해당한다면 인증 패스 처리가 된다.")
            public void it_pass_certification_when_condition_valid() {
                instance = instanceRepository.save(InstanceFactory.createByInfo(currentDate, ACTIVITY));
                participant = participantRepository.save(ParticipantFactory.createProcessing(user, instance));
                participant.updateRepository(targetRepo);

                assertThatNoException().isThrownBy(() -> {
                    certificationFacade.passCertification(user.getId(), certificationRequest);
                });
            }

            @ParameterizedTest
            @DisplayName("인스턴스의 상태가 ACTIVITY가 아니라면, NOT_ACTIVITY_INSTANCE 예외가 발생한다.")
            @EnumSource(mode = Mode.INCLUDE, names = {"PREACTIVITY", "DONE"})
            public void it_throws_NOT_ACTIVITY_INSTANCE_exception(Progress progress) {
                instance = instanceRepository.save(InstanceFactory.createByInfo(startedDate, progress));
                participant = participantRepository.save(ParticipantFactory.createProcessing(user, instance));
                participant.updateRepository(targetRepo);

                assertThatThrownBy(() -> certificationFacade.passCertification(user.getId(),
                        CertificationRequest.of(instance.getId(), currentDate)))
                        .isInstanceOf(BusinessException.class)
                        .hasMessageContaining(NOT_ACTIVITY_INSTANCE.getMessage());
            }

            @Test
            @DisplayName("현재 일자가 인스턴스 진행일 사이가 아니라면, NOT_CERTIFICATE_PERIOD 예외가 발생한다.")
            public void it_throws_NOT_CERTIFICATE_PERIOD_exception() {
                currentDate = startedDate.minusDays(1);

                instance = instanceRepository.save(InstanceFactory.createByInfo(startedDate, ACTIVITY));
                participant = participantRepository.save(ParticipantFactory.createProcessing(user, instance));
                participant.updateRepository(targetRepo);

                assertThatThrownBy(() -> certificationFacade.passCertification(user.getId(),
                        CertificationRequest.of(instance.getId(), currentDate)))
                        .isInstanceOf(BusinessException.class)
                        .hasMessageContaining(NOT_CERTIFICATE_PERIOD.getMessage());
            }
        }

        @Nested
        @DisplayName("인증 객체의 인증 가능 조건 확인 시")
        class describe_validate_certification_condition {
            @ParameterizedTest
            @DisplayName("인증의 상태가 NOT_YET이 아니라면 CAN_NOT_USE_PASS_ITEM 예외가 발생한다.")
            @EnumSource(mode = Mode.INCLUDE, names = {"CERTIFICATED", "PASSED"})
            public void it_throws_exception_when_certificateStatus_not_NOT_YET(CertificateStatus status) {
                certification = certificationRepository.save(
                        CertificationFactory.create(status, currentDate, participant));

                assertThatThrownBy(() -> certificationFacade.passCertification(user.getId(), certificationRequest))
                        .isInstanceOf(BusinessException.class)
                        .hasMessageContaining(CAN_NOT_USE_PASS_ITEM.getMessage());
            }

            @Test
            @DisplayName("인증의 상태가 NOT_YET이라면 예외가 발생하지 않는다.")
            public void it_not_throws_exception_when_certificateStatus_is_NOT_YET() {
                certification = certificationRepository.save(
                        CertificationFactory.create(NOT_YET, currentDate, participant)
                );

                assertThatNoException().isThrownBy(() -> {
                    certificationFacade.passCertification(user.getId(), certificationRequest);
                });
            }
        }

        @Nested
        @DisplayName("인증 객체의 존재 여부 확인 시")
        class describe_check_certification_exist {
            @Test
            @DisplayName("인증 객체가 존재하지 않았다면 인증 객체를 새로 저장한다.")
            public void it_save_new_object_when_not_exist() {
                Optional<Certification> beforePassed = certificationRepository.findByDate(currentDate,
                        participant.getId());

                certificationFacade.passCertification(user.getId(), certificationRequest);
                Optional<Certification> afterPassed = certificationRepository.findByDate(currentDate,
                        participant.getId());

                assertThat(beforePassed).isNotPresent();
                assertThat(afterPassed).isPresent();
            }

            @Test
            @DisplayName("인증 객체가 존재했다면 PASSED로 상태를 업데이트한다.")
            public void it_update_certificateStatus_to_PASSED() {
                certification = certificationRepository.save(
                        CertificationFactory.createNotYet(participant, currentDate)
                );

                certificationFacade.passCertification(user.getId(), certificationRequest);
                Optional<Certification> afterPassed = certificationRepository.findByDate(currentDate,
                        participant.getId());

                assertThat(afterPassed).isPresent();
                assertThat(afterPassed.get().getCertificationStatus()).isEqualTo(PASSED);
            }
        }
    }

    @Nested
    @DisplayName("인증 관련 정보 조회 시")
    class context_inquiry_certification_information {
        LocalDate startedDate;

        @Nested
        @DisplayName("인스턴스의 상태가 모두 PREACTIVITY라면")
        class describe_instance_is_all_preActivity {
            @BeforeEach
            void setup() {
                currentDate = LocalDate.now();
                instance = instanceRepository.save(InstanceFactory.createPreActivity(10));
                participant = participantRepository.save(ParticipantFactory.createPreActivity(user, instance));
            }

            @Test
            @DisplayName("성공/실패의 값은 모두 0이고, 남은일자는 전체 회차여야 한다.")
            public void it_returns_correct_values() {
                CertificationInformation information = certificationFacade.getCertificationInformation(instance,
                        participant, currentDate);

                assertThat(information.totalAttempt()).isEqualTo(instance.getTotalAttempt());
                assertThat(information.currentAttempt()).isZero();
                assertThat(information.successCount()).isZero();
                assertThat(information.failureCount()).isZero();
                assertThat(information.remainCount()).isEqualTo(instance.getTotalAttempt());
            }
        }

        @Nested
        @DisplayName("인스턴스의 상태가 모두 ACTIVITY라면")
        class describe_instance_is_all_activity {
            @BeforeEach
            void setup() {
                startedDate = LocalDate.of(2024, 8, 10);
                currentDate = LocalDate.of(2024, 8, 15);
                instance = instanceRepository.save(InstanceFactory.createByInfo(startedDate, ACTIVITY));
                participant = participantRepository.save(ParticipantFactory.createProcessing(user, instance));

                certificationRepository.save(CertificationFactory.create(NOT_YET, startedDate, participant));
                certificationRepository.save(
                        CertificationFactory.create(CERTIFICATED, startedDate.plusDays(1), participant));
                certificationRepository.save(
                        CertificationFactory.create(CERTIFICATED, startedDate.plusDays(2), participant));
                certificationRepository.save(
                        CertificationFactory.create(PASSED, startedDate.plusDays(3), participant));
                certificationRepository.save(
                        CertificationFactory.create(NOT_YET, startedDate.plusDays(4), participant));
            }

            @Test
            @DisplayName("성공/실패/남을 일자가 올바르게 나와야 한다.")
            public void it_returns_correct_values() {
                CertificationInformation information = certificationFacade.getCertificationInformation(instance,
                        participant, currentDate);

                assertThat(information.totalAttempt()).isEqualTo(instance.getTotalAttempt());
                assertThat(information.successCount()).isEqualTo(3);
                assertThat(information.failureCount()).isEqualTo(3);
                assertThat(information.currentAttempt()).isEqualTo(6);
                assertThat(information.remainCount()).isEqualTo(instance.getTotalAttempt() - 6);
            }
        }

        @Nested
        @DisplayName("인스턴스의 상태가 모두 DONE이라면")
        class describe_instance_is_all_DONE {
            @BeforeEach
            void setup() {
                startedDate = LocalDate.of(2024, 8, 10);
                currentDate = LocalDate.of(2024, 8, 15);
                instance = instanceRepository.save(InstanceFactory.createByInfo(startedDate, DONE));
                participant = participantRepository.save(ParticipantFactory.createProcessing(user, instance));

                certificationRepository.save(CertificationFactory.create(NOT_YET, startedDate, participant));
                certificationRepository.save(
                        CertificationFactory.create(CERTIFICATED, startedDate.plusDays(1), participant));
                certificationRepository.save(
                        CertificationFactory.create(CERTIFICATED, startedDate.plusDays(2), participant));
                certificationRepository.save(
                        CertificationFactory.create(PASSED, startedDate.plusDays(3), participant));
                certificationRepository.save(
                        CertificationFactory.create(NOT_YET, startedDate.plusDays(4), participant));
            }

            @Test
            @DisplayName("성공/실패/남은일자가 올바르게 나와야 한다.")
            public void it_returns_correct_values() {
                CertificationInformation information = certificationFacade.getCertificationInformation(instance,
                        participant, currentDate);

                int totalAttempt = instance.getTotalAttempt();

                assertThat(information.totalAttempt()).isEqualTo(instance.getTotalAttempt());
                assertThat(information.successCount()).isEqualTo(3);
                assertThat(information.failureCount()).isEqualTo(totalAttempt - 3);
                assertThat(information.currentAttempt()).isEqualTo(totalAttempt);
                assertThat(information.remainCount()).isEqualTo(0);
            }
        }
    }
}