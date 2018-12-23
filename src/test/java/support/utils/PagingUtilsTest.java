package support.utils;

import org.junit.Test;
import support.test.BaseTest;

public class PagingUtilsTest extends BaseTest {

    @Test
    public void 뒤에_페이지_유무_같은뭉치일때() {
        PagingUtils pagingUtils = new PagingUtils(5, 5);
        softly.assertThat(pagingUtils.isNextBunch()).isEqualTo(false);
    }

    @Test
    public void 뒤에_페이지_유무_같은뭉치일때2() {
        PagingUtils pagingUtils = new PagingUtils(5, 2);
        softly.assertThat(pagingUtils.isNextBunch()).isEqualTo(false);
    }

    @Test
    public void 뒤에_페이지_유무_뒤에있을때() {
        PagingUtils pagingUtils = new PagingUtils(6, 2);
        softly.assertThat(pagingUtils.isNextBunch()).isEqualTo(true);
    }

    @Test
    public void 앞에_페이지_유무_첫뭉치일때() {
        PagingUtils pagingUtils = new PagingUtils(6, 2);
        softly.assertThat(pagingUtils.isPreviousBunch()).isEqualTo(false);
    }

    @Test
    public void 앞에_페이지_유무_첫뭉치아닐때() {
        PagingUtils pagingUtils = new PagingUtils(22, 8);
        softly.assertThat(pagingUtils.isPreviousBunch()).isEqualTo(true);
    }

    @Test
    public void offset_구하기() {
        PagingUtils pagingUtils = new PagingUtils(6, 2);
        softly.assertThat(pagingUtils.getBunchFirst()).isEqualTo(1);
    }

    @Test
    public void offset_구하기2() {
        PagingUtils pagingUtils = new PagingUtils(6, 5);
        softly.assertThat(pagingUtils.getBunchFirst()).isEqualTo(1);
    }

    @Test
    public void offset_구하기3() {
        PagingUtils pagingUtils = new PagingUtils(8, 6);
        softly.assertThat(pagingUtils.getBunchFirst()).isEqualTo(6);
    }

    @Test
    public void 앞으로_이동() {
        PagingUtils pagingUtils = new PagingUtils(9, 8);
        softly.assertThat(pagingUtils.getPreviousBunchLast()).isEqualTo(5);
    }

    @Test
    public void 뒤로_이동() {
        PagingUtils pagingUtils = new PagingUtils(9, 3);
        softly.assertThat(pagingUtils.getNextBunchFirst()).isEqualTo(6);
    }
}