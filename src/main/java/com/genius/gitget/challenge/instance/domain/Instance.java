package com.genius.gitget.challenge.instance.domain;


import com.genius.gitget.topic.domain.Topic;
import com.genius.gitget.challenge.certification.util.DateUtil;
import com.genius.gitget.challenge.instance.dto.crud.InstanceCreateRequest;
import com.genius.gitget.challenge.likes.domain.Likes;
import com.genius.gitget.challenge.participant.domain.Participant;
import com.genius.gitget.global.file.domain.FileHolder;
import com.genius.gitget.global.file.domain.Files;
import com.genius.gitget.global.util.exception.BusinessException;
import com.genius.gitget.global.util.exception.ErrorCode;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicInsert
@Table(name = "instance")
public class Instance implements FileHolder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "instance_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "files_id")
    private Files files;

    @OneToMany(mappedBy = "instance")
    private List<Likes> likesList = new ArrayList<>();

    @OneToMany(mappedBy = "instance")
    private List<Participant> participantList = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id")
    private Topic topic;

    private String title;

    private String description;

    private String tags;

    private int pointPerPerson;

    private int participantCount;

    private String notice;

    private String certificationMethod;

    @NotNull
    @Enumerated(EnumType.STRING)
    private Progress progress;

    @Column(name = "started_at")
    private LocalDateTime startedDate;

    @Column(name = "completed_at")
    private LocalDateTime completedDate;

    @Column(name = "instance_uuid")
    private String instanceUUID;

    @Builder
    public Instance(String title, String description, String tags, int pointPerPerson, Progress progress, String notice,
                    String certificationMethod,
                    LocalDateTime startedDate, LocalDateTime completedDate) {
        this.title = title;
        this.description = description;
        this.tags = tags;
        this.pointPerPerson = pointPerPerson;
        this.notice = notice;
        this.certificationMethod = certificationMethod;
        this.progress = progress;
        this.startedDate = startedDate;
        this.completedDate = completedDate;
    }

    public static Instance createByRequest(InstanceCreateRequest instanceCreateRequest) {
        return Instance.builder()
                .title(instanceCreateRequest.title())
                .tags(instanceCreateRequest.tags())
                .description(instanceCreateRequest.description())
                .pointPerPerson(instanceCreateRequest.pointPerPerson())
                .notice(instanceCreateRequest.notice())
                .startedDate(instanceCreateRequest.startedAt())
                .completedDate(instanceCreateRequest.completedAt())
                .certificationMethod(instanceCreateRequest.certificationMethod())
                .progress(Progress.PREACTIVITY)
                .build();
    }

    //== 연관관계 편의 메서드 ==//
    public void setTopic(Topic topic) {
        this.topic = topic;
        if (!topic.getInstanceList().contains(this)) {
            topic.getInstanceList().add(this);
        }
    }

    /*
     * 인스턴스 수정
     * */
    public void updateInstance(String description, String notice, int pointPerPerson, LocalDateTime startedDate,
                               LocalDateTime completedDate, String certificationMethod) {
        this.description = description;
        this.notice = notice;
        this.pointPerPerson = pointPerPerson;
        this.startedDate = startedDate;
        this.completedDate = completedDate;
        this.certificationMethod = certificationMethod;
    }

    //== 비지니스 로직 ==//

    /*
     * 참가자 수 정보 수정
     * */
    public void updateParticipantCount(int amount) {
        if (amount < 0 && this.participantCount + amount < 0) {
            return;
        }
        this.participantCount += amount;
    }

    /*
     * 진행 상황 수정
     *  */
    public void updateProgress(Progress progress) {
        this.progress = progress;
    }

    public int getLikesCount() {
        return this.likesList.size();
    }

    @Override
    public Optional<Files> getFiles() {
        return Optional.ofNullable(this.files);
    }

    @Override
    public void setFiles(Files files) {
        this.files = files;
    }

    /*
     * 챌린지 전체 인증 일자 조회
     * */
    public int getTotalAttempt() {
        return DateUtil.getAttemptCount(startedDate.toLocalDate(), completedDate.toLocalDate());
    }

    public boolean isActivatedInstance() {
        return this.progress == Progress.ACTIVITY;
    }

    /*
     * 인스턴스 고유 uuid 설정
     * */
    public void setInstanceUUID(String instanceUUID) {
        if (this.instanceUUID != null) {
            throw new BusinessException(ErrorCode.UUID_ALREADY_EXISTS);
        } else {
            this.instanceUUID = instanceUUID;
        }
    }

    public String getPrTemplate(LocalDate currentDate) {
        String today = currentDate.toString().replace("-", "");
        return "GITGET-" + instanceUUID + "-" + today;
    }
}
