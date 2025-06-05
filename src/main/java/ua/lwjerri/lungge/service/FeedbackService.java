package ua.lwjerri.lungge.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ua.lwjerri.lungge.entity.Company;
import ua.lwjerri.lungge.entity.Feedback;
import ua.lwjerri.lungge.repository.CompanyRepository;
import ua.lwjerri.lungge.repository.FeedbackRepository;

@Service
public class FeedbackService {

  private final FeedbackRepository feedbackRepository;
  private final CompanyRepository companyRepository;

  @Autowired
  public FeedbackService(FeedbackRepository feedbackRepository, CompanyRepository companyRepository) {
    this.feedbackRepository = feedbackRepository;
    this.companyRepository = companyRepository;
  }

  public Feedback saveFeedback(Feedback feedback) {
    Optional<Company> company = companyRepository.findById(feedback.getCompanyId());

    if (company.isEmpty()) {
      throw new IllegalArgumentException("Company with ID " + feedback.getCompanyId() + " not found");
    }

    feedback.setId(null);

    return feedbackRepository.save(feedback);
  }

  public List<Feedback> findFeedbacksByCompanyId(Long companyId) {
    return feedbackRepository.findByCompanyId(companyId);
  }

  public boolean isCompanyOwner(Long companyId, String userId) {
    Optional<Company> company = companyRepository.findById(companyId);

    return company.isPresent() && company.get().getOwnerId().equals(userId);
  }

  @Transactional
  public void deleteAllFeedbacksByCompanyId(Long companyId) {
    feedbackRepository.deleteAllByCompanyId(companyId);
  }
}