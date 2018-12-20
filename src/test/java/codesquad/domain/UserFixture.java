package codesquad.domain;

import org.springframework.beans.factory.annotation.Autowired;

public class UserFixture {

    public static final User TEST_USER = new User(1000, "defaultUser", "test", "마구니를쫓는자", "lkh@gmail.com");

    public static final User JAVAJIGI_USER = new User(1, "javajigi", "test", "자바지기", "javajigi@slipp.net");

    public static final User SANJIGI_USER = new User(2, "sanjigi", "test", "산지기", "sanjigi@slipp.net");
}
