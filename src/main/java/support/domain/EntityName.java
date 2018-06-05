package support.domain;

public enum EntityName {
    QUESTION("question", "questions"), USER("user", "users"), ANSWER("answer", "answers");

    private String single;
    private String multiple;

    EntityName(String single, String multiple) {
        this.single = single;
        this.multiple = multiple;
    }

    public static String getModelName(EntityName entity) {
        return entity.single;
    }

    public static String getModelNameOfMulti(EntityName entity) {
        return entity.multiple;
    }
}
