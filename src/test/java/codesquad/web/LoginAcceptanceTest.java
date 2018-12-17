package codesquad.web;

import codesquad.domain.UserRepository;
import codesquad.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import support.test.AcceptanceTest;

import java.util.Arrays;

import static org.slf4j.LoggerFactory.getLogger;

public class LoginAcceptanceTest extends AcceptanceTest {

    private static final Logger logger = getLogger(LoginAcceptanceTest.class);

    private HtmlFormDataBuilder htmlFormDataBuilder;

    @Autowired
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Before
    public void setUp() {
        htmlFormDataBuilder = new HtmlFormDataBuilder();
    }

    @Test
    public void createForm() {
        /*
            getForEntity : 기본 Http Header를 사용하며 결과를 Http ResponseEntity로 반환 (요청을 하는 url, 리턴값타입)
             * 통합 자원 식별자(uniform resource identifier, URI) : 정보리소스의 고유한 위치
             * 통합 자원 지시자(uniform resource locator, URL) : 특정 서버의 리소스의 구체적인 위치, 리소스의 위치와 접근방법에 대한 방법 제공
              > uri가 url을 포함하는 상위 개념
        */
        ResponseEntity<String> response = template().getForEntity("/login", String.class);
        /*
            getStatusCode() : 응답에 대한 상태코드를 반환
        */
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        logger.debug("body : {}", response.getBody());
    }

    @Test
    public void create() throws Exception {
        /*
            Http Request
                요청 라인 : GET / HTTP/1.1
                    요청 메소드 : GET, POST, PUT, DELETE
                    요청 URL
                    HTTP 버전
                요청 헤더 : 키-값 방식
                    Accept : 클라이언트가 서버에 어떤 형식(MediaType)으로 달라는 요청을 할 수 있음
                    Cookie
                    Content-Type : 클라이언트가 request에 실어 보내는 데이타(body)의 형식(MediaType)
                    Content-Length : 메세지 바디 길이
                    If-Modified-Since : 특정 날짜 이후에 변경됐을 때만
                요청 바디(엔티티) : request일 때, 요청 메소드가 post, put 일 경우에 data가 전달
                * Content-type 과 Accept를 같이 보내는 이유는..? In Resuest
                    Response의 경우에는 Content-Type만 제공한다. 그 이유는 클라이언트가 Body 영역에 있는 데이터가 어떤 타입인지 알아야만 파싱이 가능하기 때문이다.
                    그러나, Resuest의 경우에는 Content-Type과 Accept 두개를 제공 가능하다. 그 이유는 Get일 경우에는 서버에 데이터를 전다할 때, 주소를 통해 전달하기 때문에
                    어떤 형태로 데이터를 보내는지 파악이 가능하다. (name=lee&age=29) 그러나, Post와 Put은 Body에 데이터르 전달하기 때문에 서버는 Body에 내용에
                    어떤 타입으로 보내는지를 알아야만 파싱이 가능하다.
                *
            Http Response
                응답 라인 : HTTP/1.1 200 OK
                    버전
                    상태 코드
                    상태 메세지
                응답 헤더
                    Content-Type : 바디 데이터의 타입
                    Content-Length : 바디 데이터 크기
                    Set-Cookie : 쿠키 설정
                    ETag : 엔티티 태그
                응답 바디 : HTML, JSON, Octet Stream 등이 있다.
        */
        htmlFormDataBuilder.addParameter("userId", "Doby");
        htmlFormDataBuilder.addParameter("name", "LEE_KI_HYUN");
        htmlFormDataBuilder.addParameter("password", "1234");
        htmlFormDataBuilder.addParameter("email", "lkhlkh09@gmail.com");

        /*
            질문! Request를 만드는데, 왜? Body와 Header만을 만들까? 응답라인을 만들지 않는건가? 그렇다면 어떻게 url을 찾아가는 것일까?
                , postForEntity 매소드를 통해 mehod, url을 넣을 때, 응답라인이 생성되는건가??
        */
        /*
            질문! Request.accept() == Response.content-Type 일치해야하는가?!
        */

        HttpEntity<MultiValueMap<String, Object>> request = htmlFormDataBuilder.build();
        ResponseEntity<String> response = template().postForEntity("/users", request, String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        softly.assertThat(response.getHeaders().getLocation().getPath()).isEqualTo("/users");
    }

    @Test
    public void login_success() throws Exception {
        htmlFormDataBuilder.addParameter("userId", "javajigi");
        htmlFormDataBuilder.addParameter("password", "test");

        HttpEntity<MultiValueMap<String, Object>> request = htmlFormDataBuilder.build();
        ResponseEntity<String> response = template().postForEntity("/login", request, String.class);

        /*
            Location : 서버의 응답이 컨텐츠가 다른 곳에 있다는 의미인 302 이므로 Location 헤더를 통해 가야할  URL 을 지정합니다.
                       301(MOVED_PERMANENTLY), 302(FOUND) 일 경우에만 볼 수 있음
             * 301 : 상태 응답 코드는 요청 된 리소스가 Location 헤더에 의해 지정된 URL로 명확하게 이동되었음
        */
        /* 질문! 301과 302의 차이 : 영구적으로 url 변경과 임시적인 url 변경의 차이! */
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        softly.assertThat(response.getHeaders().getLocation().getPath()).isEqualTo("/");
    }

    @Test
    public void login_failed_아이디() throws Exception {
        htmlFormDataBuilder.addParameter("userId", "errorId");
        htmlFormDataBuilder.addParameter("password", "test");
        HttpEntity<MultiValueMap<String, Object>> request = htmlFormDataBuilder.build();
        ResponseEntity<String> response = template().postForEntity("/login", request, String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    public void login_failed_패스워드() throws Exception {
        htmlFormDataBuilder.addParameter("userId", "javajigi");
        htmlFormDataBuilder.addParameter("password", "error");

        HttpEntity<MultiValueMap<String, Object>> request = htmlFormDataBuilder.build();
        ResponseEntity<String> response = template().postForEntity("/login", request, String.class);

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }
}
