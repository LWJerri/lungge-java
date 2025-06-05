package ua.lwjerri.lungge.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import ua.lwjerri.lungge.entity.Company;
import ua.lwjerri.lungge.service.CompanyService;

@RestController
@RequestMapping("/api/companies")
@Tag(name = "Company Management", description = "APIs for managing companies")
@SecurityRequirement(name = "keycloak")
public class CompanyController {

  @Autowired
  private CompanyService companyService;

  @Operation(summary = "Get all companies", description = "Returns a list of all companies owned by the authenticated user")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully retrieved list of companies"),
      @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token is missing or invalid", content = @Content)
  })
  @GetMapping
  public ResponseEntity<List<Company>> getMyCompanies() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String userId;

    if (authentication.getPrincipal() instanceof Jwt) {
      userId = ((Jwt) authentication.getPrincipal()).getSubject();
    } else if (authentication.getPrincipal() instanceof OidcUser) {
      userId = ((OidcUser) authentication.getPrincipal()).getSubject();
    } else {
      return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    List<Company> companies = companyService.findCompaniesByOwnerId(userId);

    return new ResponseEntity<>(companies, HttpStatus.OK);
  }

  @Operation(summary = "Get company by ID", description = "Returns a company by ID if owned by the authenticated user")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully retrieved company"),
      @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token is missing or invalid", content = @Content),
      @ApiResponse(responseCode = "403", description = "Forbidden - User is not the owner of the company", content = @Content),
      @ApiResponse(responseCode = "404", description = "Company not found", content = @Content)
  })
  @GetMapping("/{id}")
  public ResponseEntity<Company> getCompanyById(@PathVariable Long id) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String userId;

    if (authentication.getPrincipal() instanceof Jwt) {
      userId = ((Jwt) authentication.getPrincipal()).getSubject();
    } else if (authentication.getPrincipal() instanceof OidcUser) {
      userId = ((OidcUser) authentication.getPrincipal()).getSubject();
    } else {
      return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    Optional<Company> company = companyService.findCompanyById(id);

    if (company.isPresent()) {
      Company companyData = company.get();

      if (companyData.getOwnerId().equals(userId)) {
        return new ResponseEntity<>(companyData, HttpStatus.OK);
      } else {
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
      }
    } else {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }

  @Operation(summary = "Create a new company", description = "Creates a new company for the authenticated user")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Company successfully created"),
      @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token is missing or invalid", content = @Content)
  })
  @PostMapping
  public ResponseEntity<Company> createCompany(@RequestBody Company company) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String userId;

    if (authentication.getPrincipal() instanceof Jwt) {
      userId = ((Jwt) authentication.getPrincipal()).getSubject();
    } else if (authentication.getPrincipal() instanceof OidcUser) {
      userId = ((OidcUser) authentication.getPrincipal()).getSubject();
    } else {
      return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    company.setId(null);
    company.setOwnerId(userId);

    Company savedCompany = companyService.saveCompany(company);

    return new ResponseEntity<>(savedCompany, HttpStatus.CREATED);
  }

  @Operation(summary = "Update an existing company", description = "Updates an existing company if owned by the authenticated user")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Company successfully updated"),
      @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token is missing or invalid", content = @Content),
      @ApiResponse(responseCode = "403", description = "Forbidden - User is not the owner of the company", content = @Content),
      @ApiResponse(responseCode = "404", description = "Company not found", content = @Content)
  })
  @PutMapping("/{id}")
  public ResponseEntity<Company> updateCompany(@PathVariable Long id, @RequestBody Company company) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String userId;

    if (authentication.getPrincipal() instanceof Jwt) {
      userId = ((Jwt) authentication.getPrincipal()).getSubject();
    } else if (authentication.getPrincipal() instanceof OidcUser) {
      userId = ((OidcUser) authentication.getPrincipal()).getSubject();
    } else {
      return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    Optional<Company> existingCompany = companyService.findCompanyById(id);

    if (existingCompany.isPresent()) {
      Company companyData = existingCompany.get();

      if (companyData.getOwnerId().equals(userId)) {
        company.setId(id);
        company.setOwnerId(userId);

        Company updatedCompany = companyService.saveCompany(company);

        return new ResponseEntity<>(updatedCompany, HttpStatus.OK);
      } else {
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
      }
    } else {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }

  @Operation(summary = "Delete a company", description = "Deletes a company if owned by the authenticated user")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Company successfully deleted"),
      @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token is missing or invalid", content = @Content),
      @ApiResponse(responseCode = "403", description = "Forbidden - User is not the owner of the company", content = @Content),
      @ApiResponse(responseCode = "404", description = "Company not found", content = @Content)
  })
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteCompany(@PathVariable Long id) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String userId;

    if (authentication.getPrincipal() instanceof Jwt) {
      userId = ((Jwt) authentication.getPrincipal()).getSubject();
    } else if (authentication.getPrincipal() instanceof OidcUser) {
      userId = ((OidcUser) authentication.getPrincipal()).getSubject();
    } else {
      return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }
    Optional<Company> existingCompany = companyService.findCompanyById(id);

    if (existingCompany.isPresent()) {
      Company companyData = existingCompany.get();

      if (companyData.getOwnerId().equals(userId)) {
        companyService.deleteCompany(id);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
      } else {
        return new ResponseEntity<>(HttpStatus.FORBIDDEN);
      }
    } else {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }
}