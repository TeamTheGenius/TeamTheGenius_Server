![image](https://github.com/TeamTheGenius/TeamTheGenius_Server/assets/95005061/9e508d5c-ff0d-4e82-95d0-3e2d1d6217c4)

</br>

## :raising_hand_man: 프로젝트 소개
🔥 GitGet은 챌린지 참여와 인증 활동을 통해 규칙적인 공부 습관을 도와주는 서비스입니다. <br> <br>
🙋🏻‍♂️Github 계정 연동을 통해 챌린지 활동을 인증할 수 있으며, 다른 참여자들의 인증 현황을 조회할 수 있습니다. <br> <br>
🎯 챌린지에 설정되어 있는 목표 달성 시 포인트가 주어지며, 이를  통해 아이템을 구매하고 사용할 수 있습니다. <br> <br> <br>


## :desktop_computer: 기술 스택
Framework - <img src="https://img.shields.io/badge/Spring Boot-6DB33F?style=flat&logo=Spring Boot&logoColor=white" /> <img src="https://img.shields.io/badge/Gradle-02303A?style=flat&logo=Gradle&logoColor=white" />

ORM - <img src="https://img.shields.io/badge/Spring Boot JPA-6DB33F?style=flat&logo=Spring Boot&logoColor=white" />

Authorization - <img src="https://img.shields.io/badge/Spring Security-6DB33F?style=flat&logo=Spring Security&logoColor=white" /> <img src="https://img.shields.io/badge/Json Web Tokens-000000?style=flat&logo=jsonwebtokens&logoColor=white" />

Test - <img src="https://img.shields.io/badge/Junit5-25A162?style=flat&logo=junit5&logoColor=white" /> <img src="https://img.shields.io/badge/Postman-FF6C37?style=flat&logo=postman&logoColor=white" />

Database - <img src="https://img.shields.io/badge/Mariadb-003545?style=flat&logo=mariadb&logoColor=white" /> <img src="https://img.shields.io/badge/Mongodb-47A248?style=flat&logo=mongodb&logoColor=white" />

DevOps - <img src="https://img.shields.io/badge/Amazon EC2-FF9900?style=flat&logo=amazonec2&logoColor=white" /> <img src="https://img.shields.io/badge/Amazon S3-569A31?style=flat&logo=amazons3&logoColor=white" /> <img src="https://img.shields.io/badge/Amazon Route53-8C4FFF?style=flat&logo=amazonroute53&logoColor=white" /> <img src="https://img.shields.io/badge/Docker-2496ED?style=flat&logo=docker&logoColor=white" /> <img src="https://img.shields.io/badge/Nginx-009639?style=flat&logo=nginx&logoColor=white" /> <img src="https://img.shields.io/badge/Github Actions-2088FF?style=flat&logo=githubactions&logoColor=white" /> 

Monitoring - <img src="https://img.shields.io/badge/Discord-5865F2?style=flat&logo=discord&logoColor=white" /> 

Other - <img src="https://img.shields.io/badge/Swagger-85EA2D?style=flat&logo=swagger&logoColor=white" /> 

<br> <br>

## 개발 환경
```
Java : 17
Spring Boot : 3.2.1
build : gradle
```

<br> <br>

## 다운로드 방법

```
git clone https://github.com/TeamTheGenius/TeamTheGenius_Server.git
```

<br> <br>

## 화면 설계서 
![image](https://github.com/TeamTheGenius/TeamTheGenius_Server/assets/95005061/52d8e894-14a7-40ad-b94c-3af1a03fedd9)

<br> <br>

## 주요 기능
### 로그인 / Github 연동
* 사용자는 회원가입을 통해 서비스를 이용할 수 있습니다.
* 챌린지에 참여하기 위해서는 Github Access Token 인증 과정을 **필수**로 진행해야 합니다.
* Pull Request 작업으로 사용자 Repository와 서비스가 연결되었는 지 확인이 필요합니다. 참여하고자 하는 브랜치에서 아무 작업을 진행하고, PR을 등록하여 등록 여부를 확인해주세요.

<br>

![image](https://github.com/TeamTheGenius/TeamTheGenius_Server/assets/95005061/019ad04b-b223-45f8-be21-a3a2bf63c0f2)

<br>

### 홈 화면
* 사용자는 참여하고자 하는 챌린지를 둘러볼 수 있습니다. 인기, 신규, 추천 카테고리를 이용 가능합니다.
* 검색을 통해 종료된 챌린지, 진행 중인 챌린지, 참여가 가능한 챌린지 목록을 확인할 수 있습니다.

<br>

![image](https://github.com/TeamTheGenius/TeamTheGenius_Server/assets/95005061/45a77dd6-9cf4-40b7-8935-4ca7a93dbec3)

<br>

### 챌린지 인증 현황 
* 참가자 인증 현황을 클릭하면 본인을 포함한 다른 참여자들의 인증 현황을 일주일 단위로 조회할 수 있습니다.
* 인증 내역을 확인하고 싶은 일자를 선택하면 그 날의 인증에 사용된 Github PR 목록 조회가 가능합니다.
* 조회한 Github PR 목록 중 구경하고 싶은 PR이 있다면, 해당 링크를 눌러 이동이 가능합니다.

<br>

![image](https://github.com/TeamTheGenius/TeamTheGenius_Server/assets/95005061/9e83841d-0709-4339-bd5e-9e8e29e731c4)

<br> <br>

## 데이터베이스 
![image](https://github.com/kimdozzi/Java/assets/95005061/b8930f51-22b4-4574-b5f3-58ffd9bbac01)

<br> <br>

## 아키텍처
```bash
.
├── main
│   ├── java
│   │   └── com
│   │       └── genius
│   │           └── gitget
│   │               ├── admin
│   │               │   ├── signout
│   │               │   └── topic
│   │               │       ├── controller
│   │               │       ├── domain
│   │               │       ├── dto
│   │               │       ├── repository
│   │               │       └── service
│   │               ├── challenge
│   │               │   ├── certification
│   │               │   │   ├── controller
│   │               │   │   ├── domain
│   │               │   │   ├── dto
│   │               │   │   │   └── github
│   │               │   │   ├── repository
│   │               │   │   ├── service
│   │               │   │   └── util
│   │               │   ├── instance
│   │               │   │   ├── controller
│   │               │   │   ├── domain
│   │               │   │   ├── dto
│   │               │   │   │   ├── crud
│   │               │   │   │   ├── detail
│   │               │   │   │   ├── home
│   │               │   │   │   └── search
│   │               │   │   ├── repository
│   │               │   │   └── service
│   │               │   ├── likes
│   │               │   │   ├── controller
│   │               │   │   ├── domain
│   │               │   │   ├── dto
│   │               │   │   ├── repository
│   │               │   │   └── service
│   │               │   ├── myChallenge
│   │               │   │   ├── controller
│   │               │   │   ├── dto
│   │               │   │   └── service
│   │               │   ├── participant
│   │               │   │   ├── domain
│   │               │   │   ├── repository
│   │               │   │   └── service
│   │               │   ├── report
│   │               │   │   ├── controller
│   │               │   │   ├── domain
│   │               │   │   ├── dto
│   │               │   │   ├── repository
│   │               │   │   └── service
│   │               │   └── user
│   │               │       ├── controller
│   │               │       ├── domain
│   │               │       ├── dto
│   │               │       ├── repository
│   │               │       └── service
│   │               ├── global
│   │               │   ├── file
│   │               │   │   ├── controller
│   │               │   │   ├── domain
│   │               │   │   ├── dto
│   │               │   │   ├── repository
│   │               │   │   └── service
│   │               │   ├── security
│   │               │   │   ├── config
│   │               │   │   ├── constants
│   │               │   │   ├── controller
│   │               │   │   ├── domain
│   │               │   │   ├── dto
│   │               │   │   ├── filter
│   │               │   │   ├── handler
│   │               │   │   ├── info
│   │               │   │   │   └── impl
│   │               │   │   ├── repository
│   │               │   │   └── service
│   │               │   └── util
│   │               │       ├── config
│   │               │       ├── domain
│   │               │       ├── exception
│   │               │       ├── formatter
│   │               │       └── response
│   │               │           └── dto
│   │               ├── profile
│   │               │   ├── controller
│   │               │   ├── dto
│   │               │   └── service
│   │               ├── schedule
│   │               │   ├── controller
│   │               │   └── service
│   │               └── store
│   │                   ├── item
│   │                   │   ├── controller
│   │                   │   ├── domain
│   │                   │   ├── dto
│   │                   │   ├── repository
│   │                   │   └── service
│   │                   └── payment
│   │                       ├── config
│   │                       ├── controller
│   │                       ├── domain
│   │                       ├── dto
│   │                       ├── repository
│   │                       └── service
│   └── resources
└── test
    ├── java
    │   └── com
    │       └── genius
    │           └── gitget
    │               ├── admin
    │               │   └── topic
    │               │       ├── controller
    │               │       ├── repository
    │               │       └── service
    │               ├── challenge
    │               │   ├── certification
    │               │   │   ├── controller
    │               │   │   ├── repository
    │               │   │   ├── service
    │               │   │   └── util
    │               │   ├── home
    │               │   │   ├── controller
    │               │   │   └── service
    │               │   ├── instance
    │               │   │   ├── controller
    │               │   │   ├── repository
    │               │   │   └── service
    │               │   ├── item
    │               │   │   └── service
    │               │   ├── likes
    │               │   │   ├── controller
    │               │   │   └── service
    │               │   ├── myChallenge
    │               │   │   └── service
    │               │   ├── participant
    │               │   │   └── service
    │               │   └── user
    │               │       ├── controller
    │               │       ├── domain
    │               │       ├── repository
    │               │       └── service
    │               ├── global
    │               │   ├── file
    │               │   │   ├── domain
    │               │   │   ├── repository
    │               │   │   └── service
    │               │   └── security
    │               │       ├── config
    │               │       ├── controller
    │               │       └── service
    │               ├── payment
    │               │   ├── controller
    │               │   └── service
    │               ├── profile
    │               │   ├── controller
    │               │   └── service
    │               └── util
    │                   └── file
    └── resources

```

<br> <br>

## 기여자
<table>
    <tr height="140px">
        <td align="center" width="130px">
            <a href="https://github.com/madirony"><img height="100px" width="100px" src="https://github.com/kimdozzi/Java-Algorithm/assets/95005061/e11cdedd-457a-43c4-9f9e-959f19c7b667"/></a>
            <br />
            <img src="https://img.shields.io/badge/SSung023-181717?style=flat&logo=github&logoColor=white" />
        </td>
        <td align="center" width="130px">
            <a href="https://github.com/kimdozzi"><img height="100px" width="100px" src="https://avatars.githubusercontent.com/u/95005061?v=4"/></a>
            <br />
            <img src="https://img.shields.io/badge/kimdozzi-181717?style=flat&logo=github&logoColor=white" />
        </td>
      <tr height="50px">
        <td align="center">
          <img src="http://mazassumnida.wtf/api/mini/generate_badge?boj=adrians023" />
            <br />
        </td>
        <td align="center">
            <img src="http://mazassumnida.wtf/api/mini/generate_badge?boj=kimdozzi" />
            <br />
        </td>
</table>

