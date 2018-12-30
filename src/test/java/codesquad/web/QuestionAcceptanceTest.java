package codesquad.web;

import codesquad.domain.QuestionRepository;
import codesquad.domain.User;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import support.test.AcceptanceTest;
import support.test.HtmlFormDataBuilder;

public class QuestionAcceptanceTest extends AcceptanceTest {
    private static final Logger log = LoggerFactory.getLogger(UserAcceptanceTest.class);

    @Autowired
    private QuestionRepository questionRepository;

    @Test
    public void 질문하기_버튼_클릭_로그인O() throws Exception {
        User loginUser = defaultUser();
        ResponseEntity<String> response = basicAuthTemplate(loginUser)  //로그인 체크
                .getForEntity(String.format("/questions/form"), String.class);    //getForEntity get 매핑, PostForEntity post 매핑
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        log.debug("body : {}", response.getBody());
    }

    @Test
    public void 질문하기_버튼_클릭_로그인X() throws Exception {
        ResponseEntity<String> response = template().getForEntity(String.format("/questions/form"), //template 뭐임??
                String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void 질문_내용_작성() throws Exception {
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
                .addParameter("title", "질문있어요")
                .addParameter("contents", "질문이 뭔지 까먹었어요")
                .build();
        User loginUser = defaultUser();
        ResponseEntity<String> response = basicAuthTemplate(loginUser)
                .postForEntity("/questions", request, String.class);     //request에 데이터를 담으면 postForEntity로 얘가 판단해주네

        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        softly.assertThat(response.getHeaders().getLocation().getPath()).startsWith("/");
        softly.assertThat(questionRepository.findByTitle("질문있어요")).isNotEmpty();
        softly.assertThat(questionRepository.findByTitle("질문_있어요")).isEmpty();
    }

    @Test
    public void 질문수정_버튼_클릭_로그인O() throws Exception {   //로그인 된 경우, 수정버튼 클릭
        User loginUser = defaultUser();
        ResponseEntity<String> response = basicAuthTemplate(loginUser)
                .getForEntity(String.format("/questions/%d/form", 1), String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);   //정상처리 : 200, redirect는 status code == 300대
    }

    @Test
    public void 질문수정_버튼_클릭_로그인X() throws Exception {    //로그인 안된 경우, 수정버튼 클릭
        ResponseEntity<String> response = template().getForEntity(String.format("/questions/%d/form", 1),
                String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED); //httpStatus는 어떻게 설정함?? 컨트롤러에서 예외 발생시킬때 말임
    }

    @Test
    public void 질문수정_버튼_클릭_다른유저() throws Exception {   //다른 로그인 유저일 시, 수정버튼 클릭
        User otherUser = defaultUser();
        ResponseEntity<String> response = basicAuthTemplate(otherUser)
                .getForEntity(String.format("/questions/%d/form", 3), String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);   //redirect는 status code == 300대
        softly.assertThat(response.getHeaders().getLocation().getPath().startsWith("/questions/%d/form", 3));    //결과 url
    }

    @Test
    public void 질문수정_작성_로그인O() throws Exception {     //질문 수정
        User loginUser = defaultUser();
        ResponseEntity<String> response = update(basicAuthTemplate(loginUser));
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        softly.assertThat(response.getHeaders().getLocation().getPath().startsWith("/questions/%d", 1));
    }

    @Test
    public void 질문수정_작성_로그인X() throws Exception {
        ResponseEntity<String> response = update(template());
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    private ResponseEntity<String> update(TestRestTemplate template) throws Exception {
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
                .put()
                .addParameter("title", "질문 수정입니다.")
                .addParameter("contents", "질문 또 까먹었습니다.")
                .build();
        return template.postForEntity(String.format("/questions/%d", 1), request, String.class);
    }

    @Test
    public void 질문삭제_버튼_클릭_로그인O() {
        User loginUser = defaultUser();
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
                .delete()
                .build();
        ResponseEntity<String> response = basicAuthTemplate(loginUser).postForEntity(String.format("/questions/%d", 1), request, String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);
        softly.assertThat(response.getHeaders().getLocation().getPath()).startsWith("/");
    }

    @Test
    public void 질문삭제_버튼_클릭_로그인X() {
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
                .delete()
                .build();
        ResponseEntity<String> response = template().postForEntity(String.format("/questions/%d", 1), request, String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    public void 질문삭제_버튼_클릭_다른유저() {
        User otherUser = defaultUser();
        HttpEntity<MultiValueMap<String, Object>> request = HtmlFormDataBuilder.urlEncodedForm()
                .delete()
                .build();
        ResponseEntity<String> response = basicAuthTemplate(otherUser).postForEntity(String.format("/questions/%d", 2), request, String.class);
        softly.assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }
}