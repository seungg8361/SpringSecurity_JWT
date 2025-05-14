# 🛡️ Spring Boot 기반 JWT 인증/인가 시스템 & AWS 배포

Spring Boot를 활용하여 JWT 기반 인증/인가 시스템을 구현하고, AWS EC2에 배포한 백엔드 프로젝트입니다.  
Swagger를 통한 API 문서화와 테스트 코드 작성을 포함하여 실제 운영 환경에 적용 가능한 구조로 구성하였습니다.

---

## ✅ 요구사항

1. **Spring Boot** 기반 JWT 인증/인가 API 구현
2. **JUnit** 기반의 테스트 코드 작성
3. **Swagger**를 통한 API 문서화
4. **AWS EC2**에 애플리케이션을 배포하고 서비스 실행

---

## 🛠️ 기술 스택

| 분류         | 기술                                 |
|--------------|--------------------------------------|
| 언어          | Java 17                             |
| 프레임워크     | Spring Boot 3.4.5                   |
| 빌드 도구      | Gradle                              |
| DB           | H2 (In-Memory)                       |
| ORM          | JPA, Hibernate                       |
| 보안         | Spring Security, JWT                |
| API 문서화     | Swagger                             |
| 테스트        | JUnit                               |
| 인프라        | AWS EC2 (Ubuntu)                    |

---

## 📄 API 문서 (Swagger)

- [http://43.201.70.222:8080/docs](http://43.201.70.222:8080/docs)
- Swagger를 통해 API를 테스트하고 문서를 확인할 수 있습니다.

---


## 📇 데이터베이스 접속

- [http://43.201.70.222:8080/h2-console](http://43.201.70.222:8080/h2-console)
- h2 데이터베이스를 통해 저장된 사용자의 데이터를 확인할 수 있습니다.

---

## 📌 주요 API 예시

### 🧑‍💻 회원가입


```
POST 43.201.70.222:8080/signup
Content-Type: application/json
{
"username": "JIN HO",
"password": "12341234",
"nickname": "Mentos"
}
```

### 🔐 로그인
```
POST 43.201.70.222:8080/login
Content-Type: application/json
{
"username": "JIN HO",
"password": "12341234"
}
```

### 🛡️ 관리자 권한 부여
```
PATCH 43.201.70.222:8080/admin/users/{userId}/roles
Headers : 
Authorization : Bearer {token}
```

---

## 🚀 배포 환경

- 인프라: AWS EC2 (Ubuntu)
- 애플리케이션 포트: `8080`
- 배포 절차는 다음을 참고해주세요.
```bash
# 1. EC2 접속 (실제 pem 키 파일 경로 및 EC2 주소로 대체 필요)
ssh -i "<your_private_key_path>.pem" <your_username>@<your_ec2_public_ip_or_dns>

# 2. Git 설치 (Ubuntu 환경)
sudo apt update
sudo apt install git

# 설치 확인
git --version

# 3. JDK 17 설치 (Ubuntu 환경)
sudo apt update
sudo apt install openjdk-17-jdk

# 설치 확인
java -version

# 4. 프로젝트 ssh 클론 (최초 배포 시) 또는 Pull (업데이트 시)
git clone git@github.com:<your_github_username>/SpringSecurity_JWT.git
cd SpringSecurity_JWT

# 또는 최신 커밋을 받을 때
git pull origin main

# 5. 빌드
./gradlew build

# 6. 애플리케이션 실행
java -jar build/libs/SpringSecurity_JWT-0.0.1-SNAPSHOT.jar
```

---
## 🧪 테스트

- **JUnit5** 기반 테스트 코드 작성
- 인증/인가 비즈니스 로직 단위 테스트 포함

---
## 📁 프로젝트 구조
```
SpringSecurity_JWT/
├── src/
│   ├── main/
│   │   ├── java/com/sparta/springsecurity_jwt/
│   │   │   ├── common/                          # 설정 및 예외
│   │   │   ├── controller/                      # API 요청 처리
│   │   │   ├── domain/                          # 엔티티
│   │   │   ├── dto/                             # 요청/응답 DTO
│   │   │   ├── filter/                          # JWT 필터 처리
│   │   │   ├── repository/                      # JPA Repository
│   │   │   ├── service/                         # 비즈니스 로직
│   │   │   ├── util/                            # JWT 유틸리티
│   │   │   └── SpringSecurityJwtApplication.java 
│   │   └── resources/                           # application.yml
│   │
│   └── test/
│       └── java/com/sparta/springsecurity_jwt/
│           └── SpringSecurityJwtApplicationTests.java  # 테스트 클래스
│
└── build.gradle                                 # Gradle 빌드 스크립트
```
---

## 💡 기타 참고사항

- `Authorization` 헤더는 Bearer 타입으로 토큰을 전달해야 합니다.
- EC2에서 프로젝트 pull 실수 시, 루트 디렉토리로 이동하여 다시 `git pull origin main` 실행하면 최신 상태로 갱신됩니다.
- 인스턴스가 중지되는 경우 IP주소가 변경될 수 있는 점 참고 바랍니다.
  ```bash
  cd ~/.ssh/SpringSecurity_JWT
  git pull origin main