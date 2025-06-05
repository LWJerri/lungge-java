package ua.lwjerri.lungge.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import ua.lwjerri.lungge.entity.Feedback;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
  List<Feedback> findByCompanyId(Long companyId);

  @Modifying
  @Query("DELETE FROM Feedback f WHERE f.companyId = :companyId")
  void deleteAllByCompanyId(@Param("companyId") Long companyId);
}