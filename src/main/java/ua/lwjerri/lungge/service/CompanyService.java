package ua.lwjerri.lungge.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ua.lwjerri.lungge.entity.Company;
import ua.lwjerri.lungge.repository.CompanyRepository;

@Service
public class CompanyService {
  private final CompanyRepository companyRepository;
  private final FeedbackService feedbackService;

  @Autowired
  public CompanyService(CompanyRepository companyRepository, FeedbackService feedbackService) {
    this.companyRepository = companyRepository;
    this.feedbackService = feedbackService;
  }

  public List<Company> findAllCompanies() {
    return companyRepository.findAll();
  }

  public List<Company> findCompaniesByOwnerId(String ownerId) {
    return companyRepository.findByOwnerId(ownerId);
  }

  public Optional<Company> findCompanyById(Long id) {
    return companyRepository.findById(id);
  }

  public Company saveCompany(Company company) {
    return companyRepository.save(company);
  }

  @Transactional
  public void deleteCompany(Long id) {

    feedbackService.deleteAllFeedbacksByCompanyId(id);

    companyRepository.deleteById(id);
  }

  public boolean isOwner(Long companyId, String ownerId) {
    Optional<Company> company = companyRepository.findById(companyId);

    return company.isPresent() && company.get().getOwnerId().equals(ownerId);
  }
}