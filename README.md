# 대규모 시스템 설계 기초

## 참고 서적

- [가상 면접 사례로 배우는 대규모 시스템 설계 기초](http://www.yes24.com/Product/Goods/102819435)

## URL 단축기 설계

### 요구 사항

- 긴 `URL` 을 단축해서 짧은 길이의 `URL` 을 사용자에게 제공해야 한다.
- 매일 `1억` 개의 단축기를 만들어야 한다.
- 단축 `URL` 에는 `숫자(0 ~ 9)`, `영문자(a ~ z, A ~ Z)` 까지만 사용할 수 있다.
- 시스템 단순화를 위해 갱신, 삭제는 할 수 없다.

### 개략적 추정

- `쓰기 연산` : 매일 `1억` 개의 단축 `URL` 생성
- `초당 쓰기 연산` : `1억 / 24시간 / 3600초 = 1,160회` 발생한다.
- `읽기 연산` : 읽기 연산과 쓰기 연산의 비율을 `10 : 1` 로 가정했을때, 초당 `11,600회` 발생한다.
- 10년간 `URL` 단축 서버 운영하게 된다면, `1억 x 356일 x 10년 = 3,650억` 개의 레코드를 보관해야 한다.
- 단축 전 `URL` 의 평균 길이를 `100` 이라 가정했을때, 10년 동안 필요한 저장 용량은 `3,650억 x 100바이트 = 36.5TB` 이다.

### 서버 실행 및 종료

> 권한 관련 명령어 실행

```shell
chmod +x ./shortened-url-server/start.sh
chmod +x ./shortened-url-server/stop.sh
```

> 서버 실행

- 도커 컨테이너 기반의 `URL 단축 서버` 가 실행된다.

```shell
./shortened-url-server/start.sh
```

> 서버 종료

- 도커 컨테이너의 `URL 단축 서버` 를 종료하고, `도커 컨테이너, 이미지, 볼륨, 이미지` 를 모두 삭제한다.

```shell
./shortened-url-server/stop.sh
```

### 부하 테스트 결과

- `Total Vusers`: 동시에 접속하는 가상 사용자의 수
- `TPS` : 초당 트랜잭션의 수(HTTP request가 성공할 때마다 트랜잭션 수 1씩 증가) , 초당 처리 수

> Cache 적용 전, 결과 (MySQL 사용)

| Total Vusers | TPS | Peak TPS | Excuted Tests | Successful Tests | Erros |
| :-: | :-: | :-: | :-: | :-: | :-: |
| | | | | | |

> Cache 적용 후, 결과 (MySQL, Redis 사용)

| Total Vusers | TPS | Peak TPS | Excuted Tests | Successful Tests | Erros |
| :-: | :-: | :-: | :-: | :-: | :-: |
| | | | | | |
