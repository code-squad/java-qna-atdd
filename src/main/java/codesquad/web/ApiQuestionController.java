package codesquad.web;

import codesquad.domain.Question;
import codesquad.domain.User;
import codesquad.security.LoginUser;
import codesquad.service.QnaService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;

import static org.slf4j.LoggerFactory.getLogger;

@RestController
@RequestMapping("/api/questions")
public class ApiQuestionController {

    private static final Logger logger = getLogger(ApiQuestionController.class);

    @Autowired
    private QnaService qnaService;

    @PostMapping()
    public ResponseEntity inquire(@LoginUser User loginUser, @Valid Question question, BindingResult result) {
        /* requestBody 붙이는 것의 차이? 붙이면 --> request의 바디를 객체롤 만드는건데... 처음에 보낼떄 직렬화를 통해 객체로 만들었는데.... */
        /* 또, 레스트컨트롤은 제이슨으로 변형하는건데 왜 제이슨으로 못 받을까? */
        logger.debug("Call Method!!!" + question);
        HttpHeaders httpHeaders = new HttpHeaders();
        if (result.hasErrors()) {
            logger.debug("유효한 데이터를 입력하지 않아서 예외 발생!");
            return new ResponseEntity(httpHeaders, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        logger.debug("Call Method!!!" + question);
        qnaService.create(loginUser, question);
        httpHeaders.setLocation(URI.create("/"));

        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();

        parameters.add("send_phone", "12341234"); // parseError원인은 Body가 null이라... 제이슨 형태로 변경하는 부분에서 오류가 발생했던 것!

        return new ResponseEntity(parameters, httpHeaders, HttpStatus.CREATED);
    }
}