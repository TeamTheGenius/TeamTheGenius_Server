![image](https://github.com/TeamTheGenius/TeamTheGenius_Server/assets/95005061/9e508d5c-ff0d-4e82-95d0-3e2d1d6217c4)

</br>

## :raising_hand_man: 프로젝트 소개
IT 종사자를 위한 공부 습관 형성 서비스입니다. 
(추가 소개글 또는 사진 필요)

## :desktop_computer: 기술 스택
Framework - <img src="https://img.shields.io/badge/Spring Boot-6DB33F?style=flat&logo=Spring Boot&logoColor=white" /> <img src="https://img.shields.io/badge/Gradle-02303A?style=flat&logo=Gradle&logoColor=white" />

ORM - <img src="https://img.shields.io/badge/Spring Boot JPA-6DB33F?style=flat&logo=Spring Boot&logoColor=white" />

Authorization - <img src="https://img.shields.io/badge/Spring Security-6DB33F?style=flat&logo=Spring Security&logoColor=white" /> <img src="https://img.shields.io/badge/Json Web Tokens-000000?style=flat&logo=jsonwebtokens&logoColor=white" />

Test - <img src="https://img.shields.io/badge/Junit5-25A162?style=flat&logo=junit5&logoColor=white" /> <img src="https://img.shields.io/badge/Postman-FF6C37?style=flat&logo=postman&logoColor=white" />

Database - <img src="https://img.shields.io/badge/Mariadb-003545?style=flat&logo=mariadb&logoColor=white" /> <img src="https://img.shields.io/badge/Mongodb-47A248?style=flat&logo=mongodb&logoColor=white" />

DevOps - <img src="https://img.shields.io/badge/Amazon EC2-FF9900?style=flat&logo=amazonec2&logoColor=white" /> <img src="https://img.shields.io/badge/Amazon S3-569A31?style=flat&logo=amazons3&logoColor=white" /> <img src="https://img.shields.io/badge/Amazon Route53-8C4FFF?style=flat&logo=amazonroute53&logoColor=white" /> <img src="https://img.shields.io/badge/Docker-2496ED?style=flat&logo=docker&logoColor=white" /> <img src="https://img.shields.io/badge/Nginx-009639?style=flat&logo=nginx&logoColor=white" /> <img src="https://img.shields.io/badge/Github Actions-2088FF?style=flat&logo=githubactions&logoColor=white" /> 

Monitoring - <img src="https://img.shields.io/badge/Discord-5865F2?style=flat&logo=discord&logoColor=white" /> 

Other - <img src="https://img.shields.io/badge/Swagger-85EA2D?style=flat&logo=swagger&logoColor=white" /> 


## 주요 기능



## 개발 환경
```
Java : 17
Spring Boot : 3.2.1
build : gradle
```

## 다운로드 방법

```
git clone https://github.com/TeamTheGenius/TeamTheGenius_Server.git
```

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

## 데이터베이스 
![image](https://github.com/kimdozzi/Java/assets/95005061/b8930f51-22b4-4574-b5f3-58ffd9bbac01)

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


## 업데이트 내역
...
