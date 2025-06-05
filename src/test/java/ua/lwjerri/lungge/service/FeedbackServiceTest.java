package ua.lwjerri.lungge.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import ua.lwjerri.lungge.entity.Company;
import ua.lwjerri.lungge.entity.Feedback;
import ua.lwjerri.lungge.repository.CompanyRepository;
import ua.lwjerri.lungge.repository.FeedbackRepository;

@ExtendWith(MockitoExtension.class)
public class FeedbackServiceTest {

  @Mock
  private FeedbackRepository feedbackRepository;

  @Mock
  private CompanyRepository companyRepository;

  @InjectMocks
  private FeedbackService feedbackService;

  private Feedback testFeedback;
  private List<Feedback> testFeedbacks;
  private Company testCompany;
  private final String TEST_USER_ID = "test-user-id";
  private final Long TEST_COMPANY_ID = 1L;

  @BeforeEach
  public void setup() {
    testCompany = new Company("Test Company", "Test Description", "Test Address", TEST_USER_ID);
    testCompany.setId(TEST_COMPANY_ID);

    testFeedback = new Feedback("John", "Doe", 4, "Great service!", TEST_COMPANY_ID);
    testFeedback.setId(1L);

    Feedback feedback2 = new Feedback("Jane", "Smith", 5, "Excellent!", TEST_COMPANY_ID);
    feedback2.setId(2L);

    testFeedbacks = Arrays.asList(testFeedback, feedback2);
  }

  @Test
  public void testSaveFeedback_Success() {
    when(companyRepository.findById(TEST_COMPANY_ID)).thenReturn(Optional.of(testCompany));
    when(feedbackRepository.save(any(Feedback.class))).thenReturn(testFeedback);

    Feedback result = feedbackService.saveFeedback(testFeedback);

    assertEquals(testFeedback.getId(), result.getId());
    assertEquals(testFeedback.getFirstName(), result.getFirstName());
    assertEquals(testFeedback.getLastName(), result.getLastName());
    assertEquals(testFeedback.getRating(), result.getRating());
    assertEquals(testFeedback.getComments(), result.getComments());
    assertEquals(testFeedback.getCompanyId(), result.getCompanyId());

    verify(companyRepository, times(1)).findById(TEST_COMPANY_ID);
    verify(feedbackRepository, times(1)).save(testFeedback);
  }

  @Test
  public void testSaveFeedback_CompanyNotFound() {
    when(companyRepository.findById(999L)).thenReturn(Optional.empty());

    Feedback feedback = new Feedback("John", "Doe", 4, "Great service!", 999L);

    assertThrows(IllegalArgumentException.class, () -> {
      feedbackService.saveFeedback(feedback);
    });

    verify(companyRepository, times(1)).findById(999L);
    verify(feedbackRepository, times(0)).save(any(Feedback.class));
  }

  @Test
  public void testFindFeedbacksByCompanyId() {
    when(feedbackRepository.findByCompanyId(TEST_COMPANY_ID)).thenReturn(testFeedbacks);

    List<Feedback> result = feedbackService.findFeedbacksByCompanyId(TEST_COMPANY_ID);

    assertEquals(2, result.size());
    assertEquals(testFeedback.getId(), result.get(0).getId());
    assertEquals(testFeedback.getFirstName(), result.get(0).getFirstName());
    assertEquals(testFeedback.getLastName(), result.get(0).getLastName());
    assertEquals(testFeedback.getRating(), result.get(0).getRating());
    assertEquals(testFeedback.getComments(), result.get(0).getComments());
    assertEquals(testFeedback.getCompanyId(), result.get(0).getCompanyId());
    assertEquals(testFeedbacks.get(1).getId(), result.get(1).getId());

    verify(feedbackRepository, times(1)).findByCompanyId(TEST_COMPANY_ID);
  }

  @Test
  public void testIsCompanyOwner_True() {
    when(companyRepository.findById(TEST_COMPANY_ID)).thenReturn(Optional.of(testCompany));

    boolean result = feedbackService.isCompanyOwner(TEST_COMPANY_ID, TEST_USER_ID);

    assertTrue(result);

    verify(companyRepository, times(1)).findById(TEST_COMPANY_ID);
  }

  @Test
  public void testIsCompanyOwner_False_DifferentOwner() {
    when(companyRepository.findById(TEST_COMPANY_ID)).thenReturn(Optional.of(testCompany));

    boolean result = feedbackService.isCompanyOwner(TEST_COMPANY_ID, "different-user-id");

    assertFalse(result);

    verify(companyRepository, times(1)).findById(TEST_COMPANY_ID);
  }

  @Test
  public void testIsCompanyOwner_False_CompanyNotFound() {
    when(companyRepository.findById(999L)).thenReturn(Optional.empty());

    boolean result = feedbackService.isCompanyOwner(999L, TEST_USER_ID);

    assertFalse(result);

    verify(companyRepository, times(1)).findById(999L);
  }

  @Test
  public void testDeleteAllFeedbacksByCompanyId() {
    feedbackService.deleteAllFeedbacksByCompanyId(TEST_COMPANY_ID);

    verify(feedbackRepository, times(1)).deleteAllByCompanyId(TEST_COMPANY_ID);
  }
}