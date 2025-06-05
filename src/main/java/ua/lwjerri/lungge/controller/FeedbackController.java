package ua.lwjerri.lungge.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import ua.lwjerri.lungge.dto.FeedbackRequest;
import ua.lwjerri.lungge.entity.Feedback;
import ua.lwjerri.lungge.service.FeedbackService;

@RestController
@RequestMapping("/api/feedbacks")
@Tag(name = "Feedback Management", description = "APIs for managing customer feedbacks")
@SecurityRequirement(name = "keycloak")
public class FeedbackController {

  @Autowired
  private FeedbackService feedbackService;

  @Operation(summary = "Submit feedback", description = "Submit a new feedback for a company")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Feedback successfully submitted"),
      @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content),
      @ApiResponse(responseCode = "404", description = "Company not found", content = @Content)
  })
  @PostMapping
  public ResponseEntity<Feedback> submitFeedback(@Valid @RequestBody FeedbackRequest feedbackRequest) {
    try {
      Feedback feedback = new Feedback(
          feedbackRequest.getFirstName(),
          feedbackRequest.getLastName(),
          feedbackRequest.getRating(),
          feedbackRequest.getComments(),
          feedbackRequest.getCompanyId());

      Feedback savedFeedback = feedbackService.saveFeedback(feedback);

      return new ResponseEntity<>(savedFeedback, HttpStatus.CREATED);
    } catch (IllegalArgumentException e) {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }

  @Operation(summary = "Get feedbacks by company ID", description = "Get all feedbacks for a specific company. Only company owners can access this endpoint.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully retrieved list of feedbacks"),
      @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token is missing or invalid", content = @Content),
      @ApiResponse(responseCode = "403", description = "Forbidden - User is not the owner of the company", content = @Content),
      @ApiResponse(responseCode = "404", description = "Company not found", content = @Content)
  })
  @GetMapping("/company/{companyId}")
  public ResponseEntity<List<Feedback>> getFeedbacksByCompanyId(@PathVariable Long companyId) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String userId;

    if (authentication.getPrincipal() instanceof Jwt) {
      userId = ((Jwt) authentication.getPrincipal()).getSubject();
    } else if (authentication.getPrincipal() instanceof OidcUser) {
      userId = ((OidcUser) authentication.getPrincipal()).getSubject();
    } else {
      return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    if (!feedbackService.isCompanyOwner(companyId, userId)) {
      return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }

    List<Feedback> feedbacks = feedbackService.findFeedbacksByCompanyId(companyId);

    return new ResponseEntity<>(feedbacks, HttpStatus.OK);
  }
}