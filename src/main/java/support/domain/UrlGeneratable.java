package support.domain;

public interface UrlGeneratable {
    String generateUrl();

    default String generateApiUrl() {
        return "/api" + generateUrl();
    }

    default String generateRedirectUrl() {
        return "redirect:" + generateUrl();
    }
}
