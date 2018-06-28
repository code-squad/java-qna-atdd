package codesquad.domain;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Embeddable
public class DeleteHistories {

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<DeleteHistory> histories = new ArrayList<>();

    protected DeleteHistories addHistory(DeleteHistory history) {
        histories.add(history);
        return this;
    }

    public List<DeleteHistory> toList() {
        return histories;
    }

    public int size() {
        return histories.size();
    }

    @Override
    public String toString() {
        return "DeleteHistories{" +
                "histories=" + Arrays.toString(histories.toArray()) +
                '}';
    }
}
