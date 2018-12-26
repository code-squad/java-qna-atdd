package codesquad.domain;

import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class PageTest {

    private Paging paging;

    @Before
    public void setUp() {
        paging = new Paging(4L);
    }

    @Test
    public void startPageTest() {
        assertThat(paging.obtainStartPage()).isEqualTo(4);
    }

    @Test
    public void endPageTest() {
        assertThat(paging.obtainEndPage(15)).isEqualTo(5);

        assertThat(paging.obtainEndPage(40)).isEqualTo(6);
    }

    @Test
    public void totalPageTest() {
        assertThat(paging.obtainTotalPage(16)).isEqualTo(6);
    }
}
