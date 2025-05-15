# 🛡️ Spring Boot 기반 JWT 인증/인가 시스템 & AWS 배포

- Spring Boot를 활용하여 JWT 기반 인증/인가 시스템을 구현하고, AWS EC2에 배포한 백엔드 프로젝트입니다.  
- Swagger를 통한 API 문서화와 테스트 코드 작성을 포함하여 실제 운영 환경에 적용 가능한 구조로 구성하였습니다.

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

- [http://43.203.192.5:8080/docs](http://43.203.192.5:8080/docs)
- Swagger를 통해 API를 테스트하고 문서를 확인할 수 있습니다.

---


## 📇 데이터베이스 접속 (H2 Database)

- [http://43.203.192.5:8080/h2-console](http://43.203.192.5:8080/h2-console)
```
JDBC URL : jdbc:h2:mem:testdb
User Name : sa
Password : <없음>
```
- h2 데이터베이스를 통해 저장된 사용자의 데이터를 확인할 수 있습니다.

---

## 📌 주요 API 예시

### 🧑‍💻 회원가입


```
POST 43.203.192.5:8080/signup
Content-Type: application/json
{
"username": "JIN HO",
"password": "12341234",
"nickname": "Mentos"
}
```

### 🔐 로그인
```
POST 43.203.192.5:8080/login
Content-Type: application/json
{
"username": "JIN HO",
"password": "12341234"
}
```

### 🧑‍💼 관리자 권한 부여
```
PATCH 43.203.192.5:8080/admin/users/{userId}/roles
Headers : 
Authorization : Bearer {token}
```

---

## 🚀 배포 환경

- 인프라: AWS EC2 (Ubuntu)

- 배포 절차는 다음을 참고해주세요.
```bash
# 1. EC2 접속 (실제 pem 키 파일 경로 및 EC2 주소로 대체 필요)
ssh -i "<private_key_path>.pem" <username>@<ec2_public_ip_or_dns>

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
git clone git@github.com:<github_username>/SpringSecurity_JWT.git
cd SpringSecurity_JWT

# 또는 최신 커밋을 받을 때
git pull origin main

# 5. 환경 변수 설정 (.env 파일 생성)
cd src/main/resources
nano .env

# 6. 빌드
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
│   │   └── resources/                           # application.yml, .env
│   │
│   └── test/
│       └── java/com/sparta/springsecurity_jwt/
│           └── SpringSecurityJwtApplicationTests.java  # 테스트 클래스
│
└── build.gradle                                 # Gradle 빌드 스크립트
```
---
## 🐛 트러블슈팅: EC2 환경에서 Gradle 테스트 중 OOM 발생

### 🔍 문제 현상

- AWS EC2 (t2.micro) 환경에서 `./gradlew build` 명령 실행 시, 다음과 같은 오류 발생:


### ⚠️ 원인 분석

- t2.micro 인스턴스는 기본적으로 **1GB RAM**만 제공
- Gradle 빌드 시 실행되는 JUnit 테스트가 **JVM의 Heap 메모리를 초과**
- EC2 인스턴스는 **Swap 영역이 기본적으로 비활성화**되어 있어, 메모리 부족 시 시스템이 바로 실패함


### ✅ 해결 방법

#### 🔧 Swap 공간 수동 생성 (임시 메모리 확장)

```bash
# 1. 빈 파일 생성 (128MB * 16 = 2GB)
sudo dd if=/dev/zero of=/swapfile bs=128M count=16

# 2. 스왑 파일에 대한 읽기 및 쓰기 권한을 업데이트
sudo chmod 600 /swapfile
 
# 3. Linux Swap 영역으로 초기화
sudo mkswap /swapfile

# 4. Swap 활성화
sudo swapon /swapfile

# 5. 정상 활성화 확인
swapon -s

# 6. /etc/fstab 파일을 편집해서 부팅 시 스왑 파일을 활성화
sudo vi /etc/fstab

# 파일 끝에 다음 줄을 새로 추가하고 저장
-> /swapfile swap swap defaults 0 0 
```
---
## 💡 기타 참고사항

- `Authorization` 헤더는 Bearer 타입으로 토큰을 전달해야 합니다.
- EC2에서 프로젝트 pull 후 .env 파일 설정이 필요합니다. .env 파일은 .gitignore에 추가되어 있어 git을 통해 공유되지 않습니다.
- 프로젝트 업데이트 및 .env 파일 유지하기 :
  ```bash
  cd ~/.ssh/SpringSecurity_JWT
  git pull origin main
- 인스턴스가 중지되는 경우 IP주소가 변경될 수 있는 점 참고 바랍니다.