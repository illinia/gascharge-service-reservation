# gascharge-service-reservation

예약 서비스 관련 모듈

* charge, reservation, user 서비스, dto, util 클래스
* 특정 메서드들은 레디스 캐싱 어노테이션 사용
* 서비스 단위테스트 존재

*common-common, module-redis, domain-reservation 의존, 독립적으로 실행 불가능*

로컬, 원격 메이븐 레포지토리
```groovy
implementation 'com.gascharge.taemin:gascharge-service-reservation:0.0.1-SNAPSHOT'
```

멀티 모듈 프로젝트
```groovy
// settings.gradle
includeProject("reservation", "service")
```
```groovy
// build.gradle
implementation project(":gascharge-service-reservation")
```

YAML 파일 설정은 https://github.com/illinia/gascharge-module-yml 참조