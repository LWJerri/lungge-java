package ua.lwjerri.lungge.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ua.lwjerri.lungge.entity.Company;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {
  List<Company> findByOwnerId(String ownerId);
}