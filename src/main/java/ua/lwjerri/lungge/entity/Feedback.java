package ua.lwjerri.lungge.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "feedbacks")
@Schema(description = "Feedback entity representing user feedback for a company")
public class Feedback {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @JsonProperty(access = Access.READ_ONLY)
  @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Feedback ID (auto-generated)")
  private Long id;

  @NotBlank
  @Column(name = "first_name", nullable = false)
  @Schema(description = "First name of the person providing feedback", example = "John", required = true)
  private String firstName;

  @NotBlank
  @Column(name = "last_name", nullable = false)
  @Schema(description = "Last name of the person providing feedback", example = "Doe", required = true)
  private String lastName;

  @NotNull
  @Min(0)
  @Max(5)
  @Column(nullable = false)
  @Schema(description = "Rating from 0 to 5", example = "4", minimum = "0", maximum = "5", required = true)
  private Integer rating;

  @Column
  @Schema(description = "Additional comments", example = "Great service!")
  private String comments;

  @NotNull
  @Column(name = "company_id", nullable = false)
  @Schema(description = "ID of the company being rated", example = "1", required = true)
  private Long companyId;

  @ManyToOne
  @JoinColumn(name = "company_id", insertable = false, updatable = false)
  @JsonProperty(access = Access.READ_ONLY)
  @Schema(accessMode = Schema.AccessMode.READ_ONLY, hidden = true)
  private Company company;

  public Feedback() {
  }

  public Feedback(String firstName, String lastName, Integer rating, String comments, Long companyId) {
    this.firstName = firstName;
    this.lastName = lastName;
    this.rating = rating;
    this.comments = comments;
    this.companyId = companyId;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public Integer getRating() {
    return rating;
  }

  public void setRating(Integer rating) {
    this.rating = rating;
  }

  public String getComments() {
    return comments;
  }

  public void setComments(String comments) {
    this.comments = comments;
  }

  public Long getCompanyId() {
    return companyId;
  }

  public void setCompanyId(Long companyId) {
    this.companyId = companyId;
  }

  public Company getCompany() {
    return company;
  }

  public void setCompany(Company company) {
    this.company = company;
  }

  @Override
  public String toString() {
    return "Feedback{" +
        "id=" + id +
        ", firstName='" + firstName + '\'' +
        ", lastName='" + lastName + '\'' +
        ", rating=" + rating +
        ", comments='" + comments + '\'' +
        ", companyId=" + companyId +
        '}';
  }
}