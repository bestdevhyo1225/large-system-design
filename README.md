# 대규모 시스템 설계 기초

## 참고 서적

- **가상 면접 사례로 배우는 대규모 시스템 설계 기초**

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
