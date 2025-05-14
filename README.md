# Member Service

무물보 서비스에서 회원 정보를 관리하는 서비스입니다.

## Docker 이미지 정보

- 이미지: `mumulbo/mmb-member-service`
- 태그: `latest`, `dev`

## 실행 방법

### 회원 서비스와 DB를 컨테이너로 함께 띄울 경우

``` bash
docker-compose up -d
```

### 회원 서비스만 컨테이너로 띄울 경우

``` bash
docker run -d \
  --name mmb-member-service \
  -p 8082:8082 \
  --env-file .env \
  --network external-net \
  mmb-member-service:dev
```

## 환경변수

아래 환경변수를 직접 수정해서 사용할 수 있습니다.  
따로 설정하지 않을 경우 `application.yml` 파일이나 `.env` 파일에 설정된 기본값으로 실행됩니다.

* `APPLICATION_PORT`: 애플리케이션 실행 시 사용되는 포트
* `APPLICATION_NAME`: 애플리케이션 이름
* `DB_USERNAME`: DB 사용자
* `DB_PASSWORD`: DB 비밀번호
* `DB_HOST`: DB 주소
* `CONTAINER_DB_PORT`: DB 실행 시 사용되는 포트
* `DB_NAME`: Database 이름
* `JPA_DDL_AUTO`: 애플리케이션 실행 시 Table 생성 옵션
* `JPA_FORMAT_SQL`: JPA 쿼리가 출력될 때 포맷팅 여부
* `JPA_SHOW_SQL`: JPA 쿼리 출력 여부
* `LOG_ROOT_LEVEL`: 기본 로그 레벨
* `LOG_JPA_LEVEL`: JPA 로그 레벨

## 로그/볼륨 설정 (선택)

TBD

## 헬스 체크

TBD

## 참고

* [소스 코드 저장소](https://github.com/A-OverFlow/mmb-member-service)
* [회원 서비스 Api 문서](https://github.com/A-OverFlow/mmb-docs/blob/main/0_%ED%94%84%EB%A1%9C%EC%A0%9D%ED%8A%B8_%EA%B4%80%EB%A6%AC/1_%EA%B0%9C%EB%B0%9C/API_Docs/MEMBER_REST_API_Docs.md)
