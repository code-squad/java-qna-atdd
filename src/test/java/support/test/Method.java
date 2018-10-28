package support.test;

public enum Method {

    METHOD("_method"),
    POST("post"),
    PUT("put"),
    DELETE("delete");

    private String type;

    Method(String type) {
        this.type = type;
    }

    public String getType() {
        return this.type;
    }

}
