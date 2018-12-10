package support.test;

import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Rule;

public class BaseTest {
    /*
        @Rule 은 테스트 클래스에 포함된 테스트 메소드의 유연한 재정의 또는 추가를 가능하게 한다.
        테스터는 자신이 작성한 확정된 룰을 재사용할 수 았다.
    */
    @Rule
    public JUnitSoftAssertions softly = new JUnitSoftAssertions();
}
