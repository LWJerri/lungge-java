package ua.lwjerri.lungge.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
import ua.lwjerri.lungge.repository.CompanyRepository;

@ExtendWith(MockitoExtension.class)
public class CompanyServiceTest {

  @Mock
  private CompanyRepository companyRepository;

  @Mock
  private FeedbackService feedbackService;

  @InjectMocks
  private CompanyService companyService;

  private Company testCompany;
  private List<Company> testCompanies;
  private final String TEST_USER_ID = "test-user-id";

  @BeforeEach
  public void setup() {
    testCompany = new Company("Test Company", "Test Description", "Test Address", TEST_USER_ID);
    testCompany.setId(1L);

    Company company2 = new Company("Company 2", "Description 2", "Address 2", TEST_USER_ID);
    company2.setId(2L);

    testCompanies = Arrays.asList(testCompany, company2);
  }

  @Test
  public void testFindCompaniesByOwnerId() {
    when(companyRepository.findByOwnerId(TEST_USER_ID)).thenReturn(testCompanies);

    List<Company> result = companyService.findCompaniesByOwnerId(TEST_USER_ID);

    assertEquals(2, result.size());
    assertEquals(testCompany.getId(), result.get(0).getId());
    assertEquals(testCompany.getName(), result.get(0).getName());
    assertEquals(testCompany.getDescription(), result.get(0).getDescription());
    assertEquals(testCompany.getAddress(), result.get(0).getAddress());
    assertEquals(testCompany.getOwnerId(), result.get(0).getOwnerId());
    assertEquals(testCompanies.get(1).getId(), result.get(1).getId());

    verify(companyRepository, times(1)).findByOwnerId(TEST_USER_ID);
  }

  @Test
  public void testFindCompanyById() {
    when(companyRepository.findById(1L)).thenReturn(Optional.of(testCompany));

    Optional<Company> result = companyService.findCompanyById(1L);

    assertTrue(result.isPresent());
    assertEquals(testCompany.getId(), result.get().getId());
    assertEquals(testCompany.getName(), result.get().getName());
    assertEquals(testCompany.getDescription(), result.get().getDescription());
    assertEquals(testCompany.getAddress(), result.get().getAddress());
    assertEquals(testCompany.getOwnerId(), result.get().getOwnerId());

    verify(companyRepository, times(1)).findById(1L);
  }

  @Test
  public void testFindCompanyById_NotFound() {
    when(companyRepository.findById(999L)).thenReturn(Optional.empty());

    Optional<Company> result = companyService.findCompanyById(999L);

    assertFalse(result.isPresent());

    verify(companyRepository, times(1)).findById(999L);
  }

  @Test
  public void testSaveCompany() {
    when(companyRepository.save(any(Company.class))).thenReturn(testCompany);

    Company result = companyService.saveCompany(testCompany);

    assertEquals(testCompany.getId(), result.getId());
    assertEquals(testCompany.getName(), result.getName());
    assertEquals(testCompany.getDescription(), result.getDescription());
    assertEquals(testCompany.getAddress(), result.getAddress());
    assertEquals(testCompany.getOwnerId(), result.getOwnerId());

    verify(companyRepository, times(1)).save(testCompany);
  }

  @Test
  public void testDeleteCompany() {
    companyService.deleteCompany(1L);

    verify(feedbackService, times(1)).deleteAllFeedbacksByCompanyId(1L);
    verify(companyRepository, times(1)).deleteById(1L);
  }
}