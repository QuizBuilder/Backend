package com.quizBuilder.project.Service;

import com.quizBuilder.project.Entity.Enum.Difficulty;
import com.quizBuilder.project.Entity.Enum.Role;
import com.quizBuilder.project.Entity.Enum.Topic;
import com.quizBuilder.project.Entity.Option;
import com.quizBuilder.project.Entity.Question;
import com.quizBuilder.project.Entity.Quiz;
import com.quizBuilder.project.Entity.User;
import com.quizBuilder.project.Exception.BadRequestException;
import com.quizBuilder.project.Exception.ForbiddenException;
import com.quizBuilder.project.Exception.ResourceNotFoundException;
import com.quizBuilder.project.Exception.UnauthorizedException;
import com.quizBuilder.project.Model.AI.AIQuizRequest;
import com.quizBuilder.project.Model.AI.AIQuizResponse;
import com.quizBuilder.project.Model.Teacher.*;
import com.quizBuilder.project.Repository.QuestionRepository;
import com.quizBuilder.project.Repository.QuizRepository;
import com.quizBuilder.project.Repository.UserRepository;
import jakarta.transaction.Transactional;
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
    private AIIntegrationService aiIntegrationService;
    @Autowired
    private QuizRepository quizRepository;

    public String generateQuizCode() {
        String code = UUID.randomUUID().toString().substring(0, 6);
        Quiz quiz = quizRepository.findByCode(code).orElse(null);
        if (quiz != null) {
            throw new BadRequestException("Quiz code already exists");
        }
        return code;
    }

    @Transactional
    public QuizGenerationResponse generateAIQuiz(String token, QuizGenerationRequest request) {

        if (!jwtService.validateToken(token)) {
            throw new UnauthorizedException("Token is not valid");
        }

        User user = userRepository
                .findById(jwtService.extractUserIdFromToken(token))
                .orElseThrow(() -> new UnauthorizedException("Unauthorized user"));

        if (user.getRole() != Role.TEACHER) {
            throw new ForbiddenException("Access denied.");
        }

        if (request.getStartTime().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Start Time is not correct");
        }
        if (request.getEndTime().isBefore(request.getStartTime())) {
            throw new BadRequestException("End Time is not correct");
        }

        AIQuizRequest aiRequest = AIQuizRequest.builder()
                .topic(request.getTopic().name())
                .difficulty(request.getDifficulty().name())
                .noOfQuestions(request.getNoOfQuestions())
                .additionalInstruction(request.getAdditionalInstruction())
                .build();

        AIQuizResponse aiResponse = aiIntegrationService.generateQuiz(aiRequest);

        if (aiResponse == null || aiResponse.getQuestions() == null || aiResponse.getQuestions().isEmpty()) {
            throw new BadRequestException("AI failed to generate questions");
        }

        List<Question> quizQuestions = new ArrayList<>();
        List<QuestionResponse> responseQuestions = new ArrayList<>();

        for (var aiQuestion : aiResponse.getQuestions()) {

            Question question = new Question();
            question.setText(aiQuestion.getQuestion());

            List<Option> optionList = new ArrayList<>();

            for (int i = 0; i < aiQuestion.getOptions().size(); i++) {

                Option option = new Option();
                option.setText(aiQuestion.getOptions().get(i));
                option.setCorrect(i == aiQuestion.getCorrectIndex());
                option.setQuestion(question);

                optionList.add(option);
            }

            question.setOptions(optionList);
            quizQuestions.add(question);

            Option correctOpt = optionList.get(aiQuestion.getCorrectIndex());

            QuestionResponse questionResponse = QuestionResponse.builder()
                    .questionText(question.getText())
                    .optAText(optionList.get(0).getText())
                    .optBText(optionList.get(1).getText())
                    .optCText(optionList.get(2).getText())
                    .optDText(optionList.get(3).getText())
                    .correctOptText(correctOpt.getText())
                    .build();

            responseQuestions.add(questionResponse);
        }

        String generatedCode = generateQuizCode();

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

        return QuizGenerationResponse.builder()
                .code(generatedCode)
                .topic(request.getTopic())
                .difficulty(request.getDifficulty())
                .noOfQuestions(request.getNoOfQuestions())
                .questionList(responseQuestions)
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .build();
    }

    public List<GenQuizzesResponse> getGeneratedQuizzes(String token) {

        if (!jwtService.validateToken(token)) {
            throw new UnauthorizedException("Token is not valid");
        }

        User user = userRepository
                .findById(jwtService.extractUserIdFromToken(token))
                .orElseThrow(() -> new UnauthorizedException("Unauthorized user"));

        if (user.getRole() != Role.TEACHER) {
            throw new ForbiddenException("Access denied.");
        }

        List<Quiz> generatedQuizzes = quizRepository.findByUser(user);

        if (generatedQuizzes.isEmpty()) {
            throw new ResourceNotFoundException("No quizzes are generated for the user");
        }

        List<GenQuizzesResponse> genQuizzesResponses = new ArrayList<>();

        for (var quiz : generatedQuizzes) {
            GenQuizzesResponse response = GenQuizzesResponse.builder()
                    .quizCode(quiz.getCode())
                    .startTime(quiz.getStartTime())
                    .endTime(quiz.getEndTime())
                    .topic(quiz.getTopic())
                    .difficulty(quiz.getDifficulty())
                    .build();
            genQuizzesResponses.add(response);
        }

        return genQuizzesResponses;
    }

    public GenQuizInfoResponse getQuizInfo(String token, String quizCode) {

        if (!jwtService.validateToken(token)) {
            throw new UnauthorizedException("Token is not valid");
        }

        User user = userRepository
                .findById(jwtService.extractUserIdFromToken(token))
                .orElseThrow(() -> new UnauthorizedException("Unauthorized user"));

        if (user.getRole() != Role.TEACHER) {
            throw new ForbiddenException("Access denied.");
        }

        Quiz quiz = quizRepository.findByCode(quizCode)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found"));

        List<QuestionResponse> questionResponseList = new ArrayList<>();

        for (var question : quiz.getQuestionList()) {

            Option correctOpt = question.getOptions()
                    .stream()
                    .filter(Option::isCorrect)
                    .findFirst()
                    .orElseThrow(() -> new BadRequestException("Question has no correct option"));

            QuestionResponse questionResponse = QuestionResponse.builder()
                    .questionText(question.getText())
                    .optAText(question.getOptions().get(0).getText())
                    .optBText(question.getOptions().get(1).getText())
                    .optCText(question.getOptions().get(2).getText())
                    .optDText(question.getOptions().get(3).getText())
                    .correctOptText(correctOpt.getText())
                    .build();

            questionResponseList.add(questionResponse);
        }

        return GenQuizInfoResponse.builder()
                .startTime(quiz.getStartTime())
                .endTime(quiz.getEndTime())
                .code(quiz.getCode())
                .questionList(questionResponseList)
                .topic(quiz.getTopic())
                .difficulty(quiz.getDifficulty())
                .noOfQuestions(quiz.getNoOfQuestions())
                .build();
    }
}

