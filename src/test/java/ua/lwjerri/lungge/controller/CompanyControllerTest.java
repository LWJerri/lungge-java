package ua.lwjerri.lungge.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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

import ua.lwjerri.lungge.entity.Company;
import ua.lwjerri.lungge.service.CompanyService;

@WebMvcTest(CompanyController.class)
public class CompanyControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private CompanyService companyService;

  @MockBean
  private SecurityContext securityContext;

  @MockBean
  private Authentication authentication;

  @MockBean
  private Jwt jwt;

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

    SecurityContextHolder.setContext(securityContext);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(authentication.getPrincipal()).thenReturn(jwt);
    when(jwt.getSubject()).thenReturn(TEST_USER_ID);
  }

  @Test
  @WithMockUser
  public void testGetMyCompanies() throws Exception {
    when(companyService.findCompaniesByOwnerId(TEST_USER_ID)).thenReturn(testCompanies);

    mockMvc.perform(get("/api/companies")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value(testCompany.getId()))
        .andExpect(jsonPath("$[0].name").value(testCompany.getName()))
        .andExpect(jsonPath("$[0].description").value(testCompany.getDescription()))
        .andExpect(jsonPath("$[0].address").value(testCompany.getAddress()))
        .andExpect(jsonPath("$[0].ownerId").value(testCompany.getOwnerId()))
        .andExpect(jsonPath("$[1].id").value(testCompanies.get(1).getId()));

    verify(companyService, times(1)).findCompaniesByOwnerId(TEST_USER_ID);
  }

  @Test
  @WithMockUser
  public void testGetCompanyById_Success() throws Exception {
    when(companyService.findCompanyById(1L)).thenReturn(Optional.of(testCompany));

    mockMvc.perform(get("/api/companies/1")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(testCompany.getId()))
        .andExpect(jsonPath("$.name").value(testCompany.getName()))
        .andExpect(jsonPath("$.description").value(testCompany.getDescription()))
        .andExpect(jsonPath("$.address").value(testCompany.getAddress()))
        .andExpect(jsonPath("$.ownerId").value(testCompany.getOwnerId()));

    verify(companyService, times(1)).findCompanyById(1L);
  }

  @Test
  @WithMockUser
  public void testGetCompanyById_NotFound() throws Exception {
    when(companyService.findCompanyById(999L)).thenReturn(Optional.empty());

    mockMvc.perform(get("/api/companies/999")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());

    verify(companyService, times(1)).findCompanyById(999L);
  }

  @Test
  @WithMockUser
  public void testGetCompanyById_Forbidden() throws Exception {
    Company otherUserCompany = new Company("Other User Company", "Description", "Address", "other-user-id");
    otherUserCompany.setId(3L);

    when(companyService.findCompanyById(3L)).thenReturn(Optional.of(otherUserCompany));

    mockMvc.perform(get("/api/companies/3")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isForbidden());

    verify(companyService, times(1)).findCompanyById(3L);
  }

  @Test
  @WithMockUser
  public void testCreateCompany_Success() throws Exception {
    Company newCompany = new Company("New Company", "New Description", "New Address", null);
    Company savedCompany = new Company("New Company", "New Description", "New Address", TEST_USER_ID);
    savedCompany.setId(5L);

    when(companyService.saveCompany(any(Company.class))).thenReturn(savedCompany);

    mockMvc.perform(post("/api/companies")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(newCompany)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.id").value(savedCompany.getId()))
        .andExpect(jsonPath("$.name").value(savedCompany.getName()))
        .andExpect(jsonPath("$.description").value(savedCompany.getDescription()))
        .andExpect(jsonPath("$.address").value(savedCompany.getAddress()))
        .andExpect(jsonPath("$.ownerId").value(savedCompany.getOwnerId()));

    verify(companyService, times(1)).saveCompany(any(Company.class));
  }

  @Test
  @WithMockUser
  public void testUpdateCompany_Success() throws Exception {
    Company updatedCompany = new Company("Updated Company", "Updated Description", "Updated Address", TEST_USER_ID);
    updatedCompany.setId(1L);

    when(companyService.findCompanyById(1L)).thenReturn(Optional.of(testCompany));
    when(companyService.saveCompany(any(Company.class))).thenReturn(updatedCompany);

    mockMvc.perform(put("/api/companies/1")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(updatedCompany)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(updatedCompany.getId()))
        .andExpect(jsonPath("$.name").value(updatedCompany.getName()))
        .andExpect(jsonPath("$.description").value(updatedCompany.getDescription()))
        .andExpect(jsonPath("$.address").value(updatedCompany.getAddress()))
        .andExpect(jsonPath("$.ownerId").value(updatedCompany.getOwnerId()));

    verify(companyService, times(1)).findCompanyById(1L);
    verify(companyService, times(1)).saveCompany(any(Company.class));
  }

  @Test
  @WithMockUser
  public void testUpdateCompany_NotFound() throws Exception {
    Company updatedCompany = new Company("Updated Company", "Updated Description", "Updated Address", TEST_USER_ID);
    updatedCompany.setId(999L);

    when(companyService.findCompanyById(999L)).thenReturn(Optional.empty());

    mockMvc.perform(put("/api/companies/999")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(updatedCompany)))
        .andExpect(status().isNotFound());

    verify(companyService, times(1)).findCompanyById(999L);
    verify(companyService, times(0)).saveCompany(any(Company.class));
  }

  @Test
  @WithMockUser
  public void testUpdateCompany_Forbidden() throws Exception {
    Company otherUserCompany = new Company("Other User Company", "Description", "Address", "other-user-id");
    otherUserCompany.setId(3L);

    when(companyService.findCompanyById(3L)).thenReturn(Optional.of(otherUserCompany));

    mockMvc.perform(put("/api/companies/3")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(otherUserCompany)))
        .andExpect(status().isForbidden());

    verify(companyService, times(1)).findCompanyById(3L);
    verify(companyService, times(0)).saveCompany(any(Company.class));
  }

  @Test
  @WithMockUser
  public void testDeleteCompany_Success() throws Exception {
    when(companyService.findCompanyById(1L)).thenReturn(Optional.of(testCompany));
    doNothing().when(companyService).deleteCompany(1L);

    mockMvc.perform(delete("/api/companies/1")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNoContent());

    verify(companyService, times(1)).findCompanyById(1L);
    verify(companyService, times(1)).deleteCompany(1L);
  }

  @Test
  @WithMockUser
  public void testDeleteCompany_NotFound() throws Exception {
    when(companyService.findCompanyById(999L)).thenReturn(Optional.empty());

    mockMvc.perform(delete("/api/companies/999")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());

    verify(companyService, times(1)).findCompanyById(999L);
    verify(companyService, times(0)).deleteCompany(anyLong());
  }

  @Test
  @WithMockUser
  public void testDeleteCompany_Forbidden() throws Exception {
    Company otherUserCompany = new Company("Other User Company", "Description", "Address", "other-user-id");
    otherUserCompany.setId(3L);

    when(companyService.findCompanyById(3L)).thenReturn(Optional.of(otherUserCompany));

    mockMvc.perform(delete("/api/companies/3")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isForbidden());

    verify(companyService, times(1)).findCompanyById(3L);
    verify(companyService, times(0)).deleteCompany(anyLong());
  }
}