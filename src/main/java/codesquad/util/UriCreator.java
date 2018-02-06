package codesquad.util;

import support.domain.UrlGeneratable;

import java.net.URI;

public class UriCreator {

    public static URI createUri (UrlGeneratable urlGeneratable) {
        return URI.create("/api/"+urlGeneratable.generateUrl());
    }
}
