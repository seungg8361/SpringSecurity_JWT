# Spring Boot 기반 JWT 인증/인가 및 AWS 배포 과제
### 요구사항
1️⃣ **Spring Boot**를 이용하여 JWT 인증/인가 로직과 API를 구현한다.

2️⃣ **Junit** 기반의 테스트 코드를 작성한다.

3️⃣ **Swagger** 로 API를 문서화 한다.

4️⃣ 애플리케이션을 **AWS EC2**에 배포하고, 실제 환경에서 실행되도록 구성한다.
<br><br>

### 🛠️ 기술 스택
* Java 17
* Gradle
* Spring Boot 3.4.5
* H2 InMemory DB
* JPA, Hibernate
* Swagger
* Spring Security & JWT

<br><br>

회원가입
```
POST 0.0.0.0:8080/signup
{
"username": "JIN HO",
"password": "12341234",
"nickname": "Mentos"
}
```

로그인
```
POST 0.0.0.0:8080/login
{
"username": "JIN HO",
"password": "12341234"
}
```

관리자 권한 부여 API
```
PATCH 0.0.0.0:8080/admin/users/{userId}/roles
Header : Authorization : Bearer {token}
```

<br><br>

### Swagger UI 주소
```
http://localhost:8080/swagger-ui/index.html
```