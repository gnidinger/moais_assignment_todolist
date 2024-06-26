# 과제: TODO List 서비스_김태영

---
### Swagger Document: http://13.124.251.51:8080/swagger-ui/index.html#
### H2 Database: http://13.124.251.51:8080/h2
### Server Address: http://13.124.251.51:8080/
---

### 기술 스택: Java + Spring Boot + H2
![제목 없는 다이어그램](https://github.com/gnidinger/moais_assignment_todolist/assets/13742045/e8fe3bd8-9b8d-4aec-a514-0234d7f82a50)


### 패키지 구성
![스크린샷 2024-06-01 오후 2 23 04](https://github.com/gnidinger/moais_assignment_todolist/assets/13742045/b6ac9cae-4e91-49f8-92f9-e01a28ee7cbd)


### 기본 기능
#### 일반
- API는 Rest 방식을 이용해서 설계했습니다.

#### 회원 관련
- 유저는 서비스에 가입 할 수 있습니다.
- 서비스에 가입 한 유저를 회원(AmUser)이라고 합니다.
- 회원은 닉네임을 가집니다.
- 회원은 서비스에 로그인 및 탈퇴 할 수 있습니다.
- 스프링 시큐리티와 JWT를 이용해 보안이 고려되어 있습니다.

#### TODO List 관련
- 회원은 TODO List를 작성할 수 있습니다.
- 회원은 작성한 TODO List를 아래와 같이 조회할 수 있습니다.
  - 가장 최근의 TODO 1개
  - 전체 목록
- 회원은 작성한 TODO List의 상태를 아래와 같이 변경할 수 있습니다.
  - TODO (할 일)
  - IN PROGRESS (진행 중)
  - DONE (완료)
  - PENDING (대기)
    - 진행 중 상태에서만 대기 상태로 변경될 수 있습니다.
    - 대기 상태에서는 어떤 상태로든 변경될 수 있습니다.

### 추가 기능
- 주요 진행에 따른 시스템 로깅이 되어있습니다.
- Todolist 전체 조회시 생성일 기준 내림차순으로 페이지네이션이 구현되어 있습니다.
- Todolist의 상태 변경과 별개로 항목의 제목과 설명을 업데이트 하는 기능이 구현되어 있습니다.
- Todolist 삭제 로직이 구현되어 있습니다.
- 회원 탈퇴시 관련 Todolist가 삭제되도록 구현되어 있습니다.
- 매일 자정에 <상태가 'DONE'으로 바뀐 Todolist 중 2주가 지난 객체>를 자동 삭제하는 기능이 구현되어 있습니다.
- 컨트롤러, 서비스, 레포지토리 클래스의 모든 메서드에 대한 테스트코드가 작성되어 있습니다.
- Swagger를 이용한 문서화 코드가 작성되어 있습니다.

### 확장 가능한 코드&아키텍처
![제목 없는 다이어그램-페이지-3](https://github.com/gnidinger/moais_assignment_todolist/assets/13742045/012da46b-2425-4e21-9a86-904ee58f89f2)

- 비관적 락(Pessimistic Lock)의 경우 @Lock 애너테이션 사용
- 낙관적 락(Optimistic Lock)의 경우 @Version 애너테이션 사용

