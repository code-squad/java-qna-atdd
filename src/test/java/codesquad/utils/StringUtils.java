package codesquad.utils;

public class StringUtils {

    public static Long getIdBySubLocation(String location) {
        return Long.valueOf(location.substring(location.length()-1));
    }
}
