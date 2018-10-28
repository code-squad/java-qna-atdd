package support.test;

import codesquad.domain.Question;
import codesquad.domain.QuestionRepository;
import codesquad.domain.User;
import codesquad.domain.UserRepository;

import java.util.List;

public class TestRepository {

    public static void deleteAllQuestions(QuestionRepository questionRepository) {
        List<Question> questions = questionRepository.findAll();
        for (Question question : questions) {
            questionRepository.delete(question.getId());
        }
    }

    public static Question createQuestion(QuestionRepository questionRepository, User loginUser) {
        return createQuestion(questionRepository, loginUser, "test_title", "test_contents");
    }

    public static Question createQuestion(QuestionRepository questionRepository, User loginUser, String title, String contents) {
        Question question = new Question(title, contents);
        question.writeBy(loginUser);
        return questionRepository.save(question);
    }

    public static User findByUserId(UserRepository userRepository, String userId) {
        return userRepository.findByUserId(userId).get();
    }

}
