package com.quizBuilder.project.Service;



import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.quizBuilder.project.Exception.BadRequestException;
import com.quizBuilder.project.Exception.ForbiddenException;
import com.quizBuilder.project.Exception.ResourceNotFoundException;
import com.quizBuilder.project.Exception.UnauthorizedException;
import com.quizBuilder.project.Model.AI.AIQuizRequest;
import com.quizBuilder.project.Model.AI.AIQuizResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Service
@RequiredArgsConstructor
public class AIIntegrationService {

    private final WebClient webClient;

    public AIQuizResponse generateQuiz(AIQuizRequest requestBody) {

        try {

            return webClient.post()
                    .uri("/generate-quiz")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(AIQuizResponse.class)
                    .block();

        } catch (WebClientResponseException e) {

            String responseBody = e.getResponseBodyAsString();
            int statusCode = e.getStatusCode().value();

            String extractedMessage = extractDetailMessage(responseBody);

            // Map status properly
            switch (statusCode) {
                case 400:
                case 422:
                    throw new BadRequestException(extractedMessage);
                case 401:
                    throw new UnauthorizedException(extractedMessage);
                case 403:
                    throw new ForbiddenException(extractedMessage);
                case 404:
                    throw new ResourceNotFoundException(extractedMessage);
                default:
                    throw new BadRequestException(extractedMessage);
            }

        } catch (Exception e) {
            throw new BadRequestException("Failed to connect to AI service");
        }
    }

    private String extractDetailMessage(String responseBody) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(responseBody);

            if (!root.has("detail")) {
                return responseBody;
            }

            JsonNode detailNode = root.get("detail");

            // Case 1: detail is simple string
            if (detailNode.isTextual()) {
                return detailNode.asText();
            }

            // Case 2: detail is object
            if (detailNode.isObject()) {
                if (detailNode.has("message")) {
                    return detailNode.get("message").asText();
                }
                return detailNode.toString();
            }

            // Case 3: detail is list (FastAPI validation errors)
            if (detailNode.isArray()) {
                return detailNode.toString();
            }

            return responseBody;

        } catch (Exception e) {
            return responseBody;
        }
    }

}


