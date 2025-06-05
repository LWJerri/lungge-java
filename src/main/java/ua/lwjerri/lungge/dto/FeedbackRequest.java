package ua.lwjerri.lungge.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Request object for submitting feedback")
public class FeedbackRequest {

  @NotBlank
  @Schema(description = "First name of the person providing feedback", example = "John", required = true)
  private String firstName;

  @NotBlank
  @Schema(description = "Last name of the person providing feedback", example = "Doe", required = true)
  private String lastName;

  @NotNull
  @Min(0)
  @Max(5)
  @Schema(description = "Rating from 0 to 5", example = "4", minimum = "0", maximum = "5", required = true)
  private Integer rating;

  @Schema(description = "Additional comments", example = "Great service!")
  private String comments;

  @NotNull
  @Schema(description = "ID of the company being rated", example = "1", required = true)
  private Long companyId;

  public FeedbackRequest() {
  }

  public FeedbackRequest(String firstName, String lastName, Integer rating, String comments, Long companyId) {
    this.firstName = firstName;
    this.lastName = lastName;
    this.rating = rating;
    this.comments = comments;
    this.companyId = companyId;
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
}