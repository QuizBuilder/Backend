package com.quizBuilder.project.Service;

import com.quizBuilder.project.Entity.*;
import com.quizBuilder.project.Entity.Enum.Role;
import com.quizBuilder.project.Model.Student.AnswerRequest;
import com.quizBuilder.project.Model.Student.QuestionResponse;
import com.quizBuilder.project.Model.Student.QuizEndResponse;
import com.quizBuilder.project.Model.Student.StudentQuizHistoryResponse;
import com.quizBuilder.project.Repository.QuestionRepository;
import com.quizBuilder.project.Repository.QuizRepository;
import com.quizBuilder.project.Repository.QuizSubmissionRepository;
import com.quizBuilder.project.Repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final JWTService jwtService;
    private final QuizRepository quizRepository;
    private final QuestionRepository questionRepository;
    private final QuizSubmissionRepository quizSubmissionRepository;
    private final UserRepository userRepository;

    @Transactional
    public List<QuestionResponse> getQuiz(String token, String code){
        if(!jwtService.validateToken(token)){
            throw new RuntimeException("Token is not valid");
        }
        User user = userRepository.findById(jwtService.extractUserIdFromToken(token)).orElse(null);
        if(user.getRole() != Role.STUDENT){
            throw new RuntimeException("Unauthorized user.");
        }

        List<QuestionResponse> questionResponses = new ArrayList<>();

        Quiz quiz = quizRepository.findByCode(code).orElse(null);
        if(quiz == null){
            throw new RuntimeException("Quiz code entered is invalid");
        }
        if(quiz.getEndTime().isBefore(LocalDateTime.now())){
            throw new RuntimeException("You cant enter quiz after end-time");
        }
        if(quiz.getStartTime().isAfter(LocalDateTime.now())){
            throw new RuntimeException("Quiz is not yet started");
        }


        List<Question> questions = quiz.getQuestionList();

        for(Question question:questions){
            QuestionResponse elem = QuestionResponse.builder()
                    .questionId(question.getId())
                    .optAId(question.getOptions().get(0).getId())
                    .optBId(question.getOptions().get(1).getId())
                    .optCId(question.getOptions().get(2).getId())
                    .optDId(question.getOptions().get(3).getId())
                    .questionText(question.getText()).optAText(question.getOptions().get(0).getText())
                    .optAText(question.getOptions().get(0).getText())
                    .optBText(question.getOptions().get(1).getText())
                    .optCText(question.getOptions().get(2).getText())
                    .optDText(question.getOptions().get(3).getText()).build();

            questionResponses.add(elem);
        }


        return questionResponses;

    }


    public Long calculateScore(List<AnswerRequest> answerRequests){
        Long score = 0L;
        for(var answerRequest: answerRequests){
            Long questionId = answerRequest.getQuestionId();
            Long optionId = answerRequest.getSelectedOptionId();
            System.out.println(questionId);
            System.out.println(optionId);

            Question question = questionRepository.findById(questionId).orElse(null);

            List<Option> options = question.getOptions();
            for(var option:options){
                System.out.println(option.getId());
                if(option.getId().equals(optionId)){

                    if(option.isCorrect()){

                        score++;
                    }
                }
            }
        }
        return score;
    }

    @Transactional
    public QuizEndResponse submitQuiz(String token, List<AnswerRequest> answerRequests, String quizCode) {
        if(!jwtService.validateToken(token)){
            throw new RuntimeException("Token is not valid");
        }
        User user = userRepository.findById(jwtService.extractUserIdFromToken(token)).orElse(null);
        if(user.getRole() != Role.STUDENT){
            throw new RuntimeException("Unauthorized user.");
        }




        Quiz quiz = quizRepository.findByCode(quizCode).orElse(null);
        boolean isAttempted = quizSubmissionRepository.findByQuizAndUser(quiz, user)!=null;
        if(isAttempted){
            throw new RuntimeException("You cannot submit the quiz again");
        }

        LocalDateTime quizEndTime = quiz.getEndTime().plusMinutes(5);

        if(quizEndTime.isBefore(LocalDateTime.now())){
            throw new RuntimeException("You cannot submit the quiz after the deadline");
        }

        List<User> userList = quiz.getUserList();
        userList.add(user);
        quiz.setUserList(userList);

        QuizSubmission quizSubmission = new QuizSubmission();


        Long score = calculateScore(answerRequests);
        quizSubmission.setScore(score);
        quizSubmission.setUser(user);
        quizSubmission.setQuiz(quiz);


        quizSubmissionRepository.save(quizSubmission);

        QuizEndResponse quizEndResponse = new QuizEndResponse();
        quizEndResponse.setScore(score);



        return quizEndResponse;


    }

    public List<StudentQuizHistoryResponse> getAttemptedQuizInfo(String token) {
        if(!jwtService.validateToken(token)){
            throw new RuntimeException("Token is not valid");
        }
        User user = userRepository.findById(jwtService.extractUserIdFromToken(token)).orElse(null);
        if(user.getRole() != Role.STUDENT){
            throw new RuntimeException("Unauthorized user.");
        }

        List<Quiz> quizList = user.getQuizzesAttempted();
        System.out.println("quilist");
        System.out.println(quizList);

        if(quizList.isEmpty()){
            throw new RuntimeException("No quizzes are attempted");
        }

        List<StudentQuizHistoryResponse> data = new ArrayList<>();
        for(var quiz:quizList){
            System.out.println("quiSublist");
            System.out.println(user.getId());
            System.out.println(quiz.getCode());
            QuizSubmission quizSubmission = quizSubmissionRepository.findByQuizAndUser(quiz, user);
            StudentQuizHistoryResponse response = StudentQuizHistoryResponse.builder()
                    .quizCode(quiz.getCode())
                    .topic(quiz.getTopic())
                    .difficulty(quiz.getDifficulty())
                    .score(quizSubmission.getScore())
                    .rank(quizSubmission.getQuizRank())
                    .build();

            data.add(response);
        }
        return data;
    }
}
