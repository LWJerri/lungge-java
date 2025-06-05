package ua.lwjerri.lungge.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import ua.lwjerri.lungge.dto.FeedbackRequest;
import ua.lwjerri.lungge.entity.Feedback;
import ua.lwjerri.lungge.service.FeedbackService;

@WebMvcTest(FeedbackController.class)
public class FeedbackControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private FeedbackService feedbackService;

  @MockBean
  private SecurityContext securityContext;

  @MockBean
  private Authentication authentication;

  @MockBean
  private Jwt jwt;

  private Feedback testFeedback;
  private List<Feedback> testFeedbacks;
  private FeedbackRequest testFeedbackRequest;
  private final String TEST_USER_ID = "test-user-id";
  private final Long TEST_COMPANY_ID = 1L;

  @BeforeEach
  public void setup() {
    testFeedback = new Feedback("John", "Doe", 4, "Great service!", TEST_COMPANY_ID);
    testFeedback.setId(1L);

    Feedback feedback2 = new Feedback("Jane", "Smith", 5, "Excellent!", TEST_COMPANY_ID);
    feedback2.setId(2L);

    testFeedbacks = Arrays.asList(testFeedback, feedback2);

    testFeedbackRequest = new FeedbackRequest();
    testFeedbackRequest.setFirstName("John");
    testFeedbackRequest.setLastName("Doe");
    testFeedbackRequest.setRating(4);
    testFeedbackRequest.setComments("Great service!");
    testFeedbackRequest.setCompanyId(TEST_COMPANY_ID);

    SecurityContextHolder.setContext(securityContext);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(authentication.getPrincipal()).thenReturn(jwt);
    when(jwt.getSubject()).thenReturn(TEST_USER_ID);
  }

  @Test
  @WithMockUser
  public void testSubmitFeedback_Success() throws Exception {
    when(feedbackService.saveFeedback(any(Feedback.class))).thenReturn(testFeedback);

    mockMvc.perform(post("/api/feedbacks")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(testFeedbackRequest)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(testFeedback.getId()))
        .andExpect(jsonPath("$.firstName").value(testFeedback.getFirstName()))
        .andExpect(jsonPath("$.lastName").value(testFeedback.getLastName()))
        .andExpect(jsonPath("$.rating").value(testFeedback.getRating()))
        .andExpect(jsonPath("$.comments").value(testFeedback.getComments()))
        .andExpect(jsonPath("$.companyId").value(testFeedback.getCompanyId()));

    verify(feedbackService, times(1)).saveFeedback(any(Feedback.class));
  }

  @Test
  @WithMockUser
  public void testSubmitFeedback_CompanyNotFound() throws Exception {
    when(feedbackService.saveFeedback(any(Feedback.class)))
        .thenThrow(new IllegalArgumentException("Company not found"));

    mockMvc.perform(post("/api/feedbacks")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(testFeedbackRequest)))
        .andExpect(status().isNotFound());

    verify(feedbackService, times(1)).saveFeedback(any(Feedback.class));
  }

  @Test
  @WithMockUser
  public void testGetFeedbacksByCompanyId_Success() throws Exception {
    when(feedbackService.isCompanyOwner(TEST_COMPANY_ID, TEST_USER_ID)).thenReturn(true);
    when(feedbackService.findFeedbacksByCompanyId(TEST_COMPANY_ID)).thenReturn(testFeedbacks);

    mockMvc.perform(get("/api/feedbacks/company/" + TEST_COMPANY_ID)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value(testFeedback.getId()))
        .andExpect(jsonPath("$[0].firstName").value(testFeedback.getFirstName()))
        .andExpect(jsonPath("$[0].lastName").value(testFeedback.getLastName()))
        .andExpect(jsonPath("$[0].rating").value(testFeedback.getRating()))
        .andExpect(jsonPath("$[0].comments").value(testFeedback.getComments()))
        .andExpect(jsonPath("$[0].companyId").value(testFeedback.getCompanyId()))
        .andExpect(jsonPath("$[1].id").value(testFeedbacks.get(1).getId()));

    verify(feedbackService, times(1)).isCompanyOwner(TEST_COMPANY_ID, TEST_USER_ID);
    verify(feedbackService, times(1)).findFeedbacksByCompanyId(TEST_COMPANY_ID);
  }

  @Test
  @WithMockUser
  public void testGetFeedbacksByCompanyId_Forbidden() throws Exception {
    when(feedbackService.isCompanyOwner(TEST_COMPANY_ID, TEST_USER_ID)).thenReturn(false);

    mockMvc.perform(get("/api/feedbacks/company/" + TEST_COMPANY_ID)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isForbidden());

    verify(feedbackService, times(1)).isCompanyOwner(TEST_COMPANY_ID, TEST_USER_ID);
    verify(feedbackService, times(0)).findFeedbacksByCompanyId(TEST_COMPANY_ID);
  }
}