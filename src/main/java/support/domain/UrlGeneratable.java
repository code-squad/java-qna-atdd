package support.domain;

public interface UrlGeneratable {
    String generateUrl();

    default String generateRedirectUrl() {
        return "redirect:" + generateUrl();
    }
}
