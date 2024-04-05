## PROJECT
## LINK
**2024.01 - 2024.02**

![z2thw0r.png](https://i.imgur.com/z2thw0r.png)

점수제 해커톤 운영 플랫폼 (https://link.mgbg.kr)


프로젝트 소개
```
경력 인증을 기반으로 한 점수제 시스템을 통해 공정한 환경에서 경쟁
자동화분석을 통해 성능 개선을 위한 구체적인 가이드라인을 제공
가이드라인에 따라 점수 산정
레드마인을 통해 프로젝트 관리를 지원
사용자 경험을 중시해 반응형 디자인을 적용
```

**Backend**

```Java 17```, ```Spring Boot 3```, ```Spring Security 6```, ```Spring Data JPA```, ```JPA Hibernate```, ```QueryDSL```, ```MySQL```, ```Redis```, ```RabbitMQ```, ```Python 3```

**Frontend**

```TypeScript```, ```Vue 3```, ```Vuex```

**Infra**

```Docker```,```AWS EC2```, ```AWS S3,``` ```Nginx```, ```Jenkins```, ```WebRTC OpenVidu```

**Analysis**

```SonarQube```, ```Lighthouse```, ```Redmine```

**Tools**

```Git```, ```Jira```, ```Postman```


![](https://i.imgur.com/PwhzkMG.png)

**팀원 구하기**

기술 스택, 티어, 기타 정보들로 사용자를  필터링하여 나에게 맞는 팀원을 구할 수 있도록 합니다.

![](https://i.imgur.com/jlIiSDd.png)

**화상면접**

WebRTC기술을 활용하여 면접예약한 사용자와 화상면접을 진행할 수 있습니다.

![](https://i.imgur.com/nRJ9vvp.png)

**프로젝트 링크**

깃허브 링크 및 배포 URL등을 입력하여 관리할 수 있습니다.

**프로젝트 테스트**

프론트엔드와 백엔드 테스트를 진행하고 결과를 보여주고 개선에 대한 가이드라인을 제시합니다.

![](https://i.imgur.com/SgZkPle.png)

**깃허브 분석**

레포지토리를 분석하여 현재 팀원들의 깃허브 정보를 분석하여 제공합니다.


![](https://i.imgur.com/Vy3tOkE.png)

**프로젝트 관리**
1. 노션을 사용하여 기능 명세서 및 기타 문서 관리
2. Jira를 사용함에 따라 스크럼 방법론을 도입 프로젝트의 유연성과 반응성 향상 -> Redmine 기능 추가와 같은 새로운 요구사항이 발생했을 때 즉각적으로 대응

![](https://i.imgur.com/TCgOUrR.png)

**기술선정 이유**
1. **SpringBoot** 가용성과 방대한 커뮤니티로 백엔드 프레임워크로 선정
2. **Vue.js** HTML, CSS, JavaScript의 명확한 분리 구조로 개발 용이
3. **Python** 백엔드 서버와 분석 작업의 분리, 데이터의 정제 용이
4. **RabbitMq** 분석 작업의 비동기 처리. 시스템 간 결합도를 낮추고 리소스 사용을 최적화
5. **TypeScript** 타입 체크를 활용해 초기 단계에서 버그를 예방, 인터페이스로 더미 데이터를 설정하여 프론트엔드와 백엔드 병행 개발 이를 도입하여 5주간 40페이지 가량의 페이지 개발완료

![](https://i.imgur.com/njQiMx0.png)


**무중단 배포 도입 배경**

프로젝트를 진행하면서 백엔드 서버의 업데이트로 인해 다운타임이 발생

-> Jenkins, Ngnix, Docker를 활용하여 서비스의 무중단 배포를 구현

프로젝트의 규모가 작고 개발 단계이며 6주라는 한정된 시간

-> 가장 간단하여 빠르게 도입할 수 있는 블루-그린 배포 선택

![](https://i.imgur.com/QRYkerB.png)

**분석 시스템 메모리 오버플로**

문제 상황 자동 분석 시스템에서, 동시 접속자 수가 증가할 때 메모리 사용량이 급격히 증가하는 현상을 발견, 최악의 경우 블루 스크린(BSOD)으로 다운되는 현상까지 발생  -> 가설 : “동기처리로 인하여 분석작업이 시스템의 리소스를 과도하게 사용한다”

문제 진단 -> 가설을 검증하기 위해 JMeter를 사용하여 부하 테스트 점진적 진행 

테스트 결과

`# 초당 60회의 요청이 발생했을 때 메모리 사용량이 증가하여 시스템의 한계치에 도달`

`# 초당 80회에서 블루스크린이 발생함`

![](https://i.imgur.com/RTqIWWK.png)

**개선 결과**

RabbitMQ의 비동기 처리 도입하여 시스템이 고부하 상황에서도 안정적인 메모리를 관리 가능

`# 동시접속 100회 BSOD 발생 -> 동시접속 1000건 안정적 처리`

# 커밋 컨벤션


|             이모지              |            태그             |                 설명                  |
|:----------------------------:|:-------------------------:|:-----------------------------------:|
|          :sparkles:          |           Feat:           |           새로운 기능을 추가하는 경우           |
|            :zap:             |          Update:          |             코드를 변경한 경우              |
|            :fire:            |          Remove:          |           코드나 파일을 삭제한 경우            |
|            :bug:             |           Fix:            |              버그를 고친 경우              |
|            :memo:            |           Docs:           |             문서를 수정한 경우              |
|       :speech_balloon:       |         Comment:          |           필요한 주석 추가 및 변경            |
| :twisted_rightwards_arrows:  |          Merge:           |               브랜치 합병                |
|          :lipstick:          |          Design:          |              UI 디자인 변경              |
|      :white_check_mark:      |           Test:           |          테스트 추가, 테스트 리팩토링           |
|          :recycle:           |         Refactor:         |                리팩토링                 |
|           :wrench:           |          Chore:           |        빌드 업무 설정, 패키지 매니저 설정         |

# 팀


### 팀장
- 주 윤: [jooyun-1](https://github.com/jooyun-1)

### 팀원
- 김지민: [jjmsrc](https://github.com/jjmsrc)
- 유제훈: [JehunYoo](https://github.com/JehunYoo)
- 임종율: [Jong-Youl](https://github.com/Jong-Youl)
- 마성진: [MaSeongJin](https://github.com/MaSeongJin)
- 조민균: [jmg9776](https://github.com/jmg9776)
