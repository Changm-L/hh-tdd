# 동시성 제어 방식에 대한 분석

---

## 1. `synchronized` (선택하지 않음)

### 단점 / 제약

- 타임아웃/백오프 불가
- 인터럽트 불가
- 확장성 문제, 다중환경에서 보호되지 않지만 현 어플리케이션에선 고려하지 않음

## 2. `DB Lock[낙관적 | 비관적]` (선택하지 않음: 실제 DB 미사용)

1. 낙관적 Lock(Optimistic Lock)
    - JPA 사용 시 `@Version`을 작성하면 JPA가 WHERE version = ?를 붙여 충돌 감지
    - 충돌 시 백오프 필요
2. 비관적 Lock(Pessimistic Lock)
    - JPA 사용 시 `@Lock(LockModeType.PESSIMISTIC_WRITE)`등과 같이 사용
    - `Transactional`과 함께 사용해야 함
    - Lock이 걸려있는 공유 자원 접근 시 충돌, 대기, 타임아웃

## 3. `ReentrantLock` (선택)

- JVM 내부에서 Lock 진행
- timeout, interrupt 등 상세 구현 가능
- 분산 환경에서 사용되지 않지만 현 시스템은 분산 환경을 고려하지 않았으므로 적절하다 생각함
    