package support.utils;

import codesquad.domain.Question;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

public class PagingUtils {
    public static final int DEFAULT_PAGE_QUESTION_COUNT = 15;
    public static final int DEFAULT_PAGE_COUNT = 5;

    private int totalPages;
    private int currentPage;

    public static PagingUtils of(Page<Question> currentPage) {
        return new PagingUtils(currentPage.getTotalPages(), currentPage.getNumber() + 1);
    }

    public PagingUtils(int totalPages, int currentPage) {
        this.totalPages = totalPages;
        this.currentPage = currentPage;
    }

    public List<Integer> getPages() {
        List<Integer> pages = new ArrayList<>();

        for (int i = getBunchFirst(); i <= getBunchLast(); i++) {
            pages.add(i);
        }

        return pages;
    }


    public boolean isNextBunch() {
        return getBunchNum(currentPage) < getBunchNum(totalPages);
    }

    public boolean isPreviousBunch() {
        return getBunchNum(currentPage) > 1;
    }

    private int getBunchNum(int pages) {
        return (int) Math.ceil(pages / (double)DEFAULT_PAGE_COUNT);
    }

    public int getBunchFirst() {
        return (getBunchNum(currentPage) - 1) * DEFAULT_PAGE_COUNT + 1;
    }

    private int getBunchLast() {
        return isNextBunch()? getBunchFirst() + (DEFAULT_PAGE_COUNT - 1) : totalPages;
    }

    public int getPreviousBunchLast() {
        return getBunchFirst() - 1;
    }


    public int getNextBunchFirst() {
        return getBunchLast() + 1;
    }
}
