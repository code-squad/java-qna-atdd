package codesquad.domain;

import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;

public class Paging {
    private static final Logger log = getLogger(Paging.class);
    private static final int MAX_PAGE = 5;
    public static final int MAX_ITEM = 15;

    private List<Integer> pages;
    private int totalSize;
    private int currentPage;

    public Paging(int totalSize, int currentPage) {
        this.totalSize = totalSize;
        this.currentPage = currentPage;
    }

    public int getPrev() {
        int end = (int) (Math.ceil(currentPage / MAX_PAGE) + 1) * MAX_PAGE;
        int rangeStart = (end - MAX_PAGE) + 1;

        return rangeStart == 1 ? 1 : rangeStart - 1;
    }

    public int getNext() {
        int end = (int) (Math.ceil(currentPage / MAX_PAGE) + 1) * MAX_PAGE;

        int rangeEnd = end > totalSize ? totalSize : end;
        return rangeEnd == totalSize ? totalSize : rangeEnd + 1;
    }

    public List<Integer> getPages() {
        int end = (int) (Math.ceil(currentPage / MAX_PAGE) + 1) * MAX_PAGE;

        int rangeStart = (end - MAX_PAGE) + 1;
        int rangeEnd = end > totalSize ? totalSize : end;

        this.pages = new ArrayList<>();
        for (int i = rangeStart; i <= rangeEnd; i++) {
            pages.add(i);
        }
        return pages;
    }
}
