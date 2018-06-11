package codesquad.web;

import codesquad.CannotDeleteException;
import codesquad.domain.Question;
import codesquad.domain.User;
import codesquad.dto.QuestionDto;
import codesquad.security.LoginUser;
import codesquad.service.QnaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

import static support.domain.EntityName.QUESTION;
import static support.domain.EntityName.getModelName;
import static support.domain.ViewPath.*;

@Controller
@RequestMapping("/questions")
public class QuestionController {
    private static final Logger log = LoggerFactory.getLogger(QuestionController.class);

    @Resource
    private QnaService qnaService;

    @Autowired
    public QuestionController(QnaService qnaService) {
        this.qnaService = qnaService;
    }

    @PostMapping
    public String create(@LoginUser User loginUser, @Valid QuestionDto questionDto) {
        return qnaService.create(loginUser, questionDto).generateRedirectUrl();
    }

    @GetMapping("/{id}")
    public String show(@PathVariable Long id, Model model) {
        Question question = qnaService.findQuestionById(id);
        model.addAttribute(getModelName(QUESTION), question);
        return getViewPath(QNA_SHOW);
    }

    @GetMapping("/{id}/form")
    public String edit(@LoginUser User loginUser, @PathVariable Long id, Model model) {
        model.addAttribute(getModelName(QUESTION), qnaService.findQuestionById(loginUser, id));
        return getViewPath(QNA_EDIT);
    }

    @PutMapping("/{id}")
    public String update(@LoginUser User loginUser, @PathVariable Long id, QuestionDto updateQuestionDto) {
        QuestionDto updatedQuestionDto = qnaService.updateQuestion(loginUser, id, updateQuestionDto);
        return updatedQuestionDto.toQuestion().generateRedirectUrl();
    }

    @DeleteMapping("/{id}")
    public String delete(@LoginUser User loginUser, @PathVariable Long id) throws CannotDeleteException {
        qnaService.deleteQuestion(loginUser, id);
        return "redirect:/";
    }
}
