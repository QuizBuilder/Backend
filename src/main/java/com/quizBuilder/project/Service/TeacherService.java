package com.quizBuilder.project.Service;

import com.quizBuilder.project.Entity.Enum.Difficulty;
import com.quizBuilder.project.Entity.Enum.Role;
import com.quizBuilder.project.Entity.Enum.Topic;
import com.quizBuilder.project.Entity.Option;
import com.quizBuilder.project.Entity.Question;
import com.quizBuilder.project.Entity.Quiz;
import com.quizBuilder.project.Entity.User;
import com.quizBuilder.project.Model.Teacher.*;
import com.quizBuilder.project.Repository.QuestionRepository;
import com.quizBuilder.project.Repository.QuizRepository;
import com.quizBuilder.project.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service

public class TeacherService {
    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private JWTService jwtService;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QuizRepository quizRepository;

    public List<Long> generateRandomIds(Long noOfQuestions, Topic topic, Difficulty difficulty){

        List<Long> allIds = questionRepository.findAllIdsByTopicAndDifficulty(topic, difficulty);

        Collections.shuffle(allIds);
        List<Long> questionIds = new ArrayList<>();
        for(int i=0; i<noOfQuestions; i++){
            questionIds.add(allIds.get(i));
        }

        return questionIds;
    }

    public String generateQuizCode(){
        String code = UUID.randomUUID().toString().substring(0, 6);
        Quiz quiz = quizRepository.findByCode(code).orElse(null);
        if(quiz != null){
            throw new RuntimeException("Quiz code already exist");
        }
        return code;
    }

    public QuizGenerationResponse generateQuiz(String token, QuizGenerationRequest request) {

        if(!jwtService.validateToken(token)){
            throw new RuntimeException("Token is not valid");
        }

        User user = userRepository.findById(jwtService.extractUserIdFromToken(token)).orElse(null);

        if(user.getRole() != Role.TEACHER){
            throw new RuntimeException("Unauthorized user.");
        }

        if(request.getStartTime().isBefore(LocalDateTime.now())){
            throw new RuntimeException("Start Time is not correct");
        }

        List<Long> randomQueIds = generateRandomIds(request.getNoOfQuestions(), request.getTopic(), request.getDifficulty());

        List<QuestionResponse> randomGeneratedQues = new ArrayList<>();
        List<Question> quizQuestions = new ArrayList<>();

        for(Long randomId:randomQueIds){
            Question question = questionRepository.findById(randomId).orElse(null);
            quizQuestions.add(question);
            Option correctOpt = new Option();
            for(var opt:question.getOptions()){
                if(opt.isCorrect()){
                    correctOpt = opt;
                }
            }
            QuestionResponse questionResponse = QuestionResponse.builder()
                    .questionText(question.getText()).optAText(question.getOptions().get(0).getText())
                    .optBText(question.getOptions().get(1).getText()).optCText(question.getOptions().get(2).getText()).optDText(question.getOptions().get(3).getText())
                    .correctOptText(correctOpt.getText()).build();
            randomGeneratedQues.add(questionResponse);
        }

        String generatedCode = generateQuizCode();
        QuizGenerationResponse response = QuizGenerationResponse.builder()
                .code(generatedCode)
                .topic(request.getTopic())
                .difficulty(request.getDifficulty())
                .noOfQuestions(request.getNoOfQuestions())
                .questionList(randomGeneratedQues)
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .build();


        Quiz quiz = Quiz.builder()
                .code(generatedCode)
                .topic(request.getTopic())
                .difficulty(request.getDifficulty())
                .noOfQuestions(request.getNoOfQuestions())
                .questionList(quizQuestions)
                .user(user)
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .build();

        user.getQuizList().add(quiz);
        quizRepository.save(quiz);
        return response;

    }


    public List<GenQuizzesResponse> getGeneratedQuizzes(String token) {
        System.out.println(token);
        if(!jwtService.validateToken(token)){
            throw new RuntimeException("Token is not valid");
        }
        User user = userRepository.findById(jwtService.extractUserIdFromToken(token)).orElse(null);
        if(user.getRole() != Role.TEACHER){
            throw new RuntimeException("Unauthorized user.");
        }
        List<Quiz> generatedQuizzes = quizRepository.findByUser(user);
        if(generatedQuizzes.isEmpty()){
            throw new RuntimeException("No quizzes are generated for the user");
        }

        List<GenQuizzesResponse> genQuizzesResponses = new ArrayList<>();

        for (var quiz:generatedQuizzes){
            GenQuizzesResponse response = GenQuizzesResponse.builder()
                    .quizCode(quiz.getCode()).startTime(quiz.getStartTime())
                    .endTime(quiz.getEndTime()).topic(quiz.getTopic()).difficulty(quiz.getDifficulty()).build();
            genQuizzesResponses.add(response);
        }
        return genQuizzesResponses;
    }

    public GenQuizInfoResponse getQuizInfo(String token, String quizCode){
        if(!jwtService.validateToken(token)){
            throw new RuntimeException("Token is not valid");
        }
        User user = userRepository.findById(jwtService.extractUserIdFromToken(token)).orElse(null);
        if(user.getRole() != Role.TEACHER){
            throw new RuntimeException("Unauthorized user.");
        }
        System.out.println(quizCode);
        Quiz quiz = quizRepository.findByCode(quizCode).orElse(null);

        List<QuestionResponse> questionResponseList = new ArrayList<>();

        for(var question:quiz.getQuestionList()){
            Option correctOpt = new Option();
            for(var opt:question.getOptions()){
                if(opt.isCorrect()){
                    correctOpt = opt;
                }
            }
            QuestionResponse questionResponse = QuestionResponse.builder()
                    .questionText(question.getText()).optAText(question.getOptions().get(0).getText())
                    .optBText(question.getOptions().get(1).getText()).optCText(question.getOptions().get(2).getText()).optDText(question.getOptions().get(3).getText())
                    .correctOptText(correctOpt.getText()).build();
            questionResponseList.add(questionResponse);
        }


        GenQuizInfoResponse response = GenQuizInfoResponse.builder()
                .startTime(quiz.getStartTime())
                .endTime(quiz.getEndTime())
                .code(quiz.getCode())
                .questionList(questionResponseList)
                .topic(quiz.getTopic())
                .difficulty(quiz.getDifficulty())
                .noOfQuestions(quiz.getNoOfQuestions()).build();
        return response;
    }


}
