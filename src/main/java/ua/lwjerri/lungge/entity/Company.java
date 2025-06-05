package ua.lwjerri.lungge.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "companies")
@Schema(description = "Company entity representing a business organization")
public class Company {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @JsonProperty(access = Access.READ_ONLY)
  @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Company ID (auto-generated)")
  private Long id;

  @Column(nullable = false)
  @Schema(description = "Company name", example = "Acme Corporation", required = true)
  private String name;

  @Column
  @Schema(description = "Company description", example = "A global leader in innovative solutions")
  private String description;

  @Column(nullable = false)
  @Schema(description = "Company address", example = "123 Main St, Anytown, USA", required = true)
  private String address;

  @Column(name = "owner_id", nullable = false)
  @JsonProperty(access = Access.READ_ONLY)
  @Schema(accessMode = Schema.AccessMode.READ_ONLY, description = "Owner ID (set automatically from authentication)")
  private String ownerId;

  public Company() {
  }

  public Company(String name, String description, String address, String ownerId) {
    this.name = name;
    this.description = description;
    this.address = address;
    this.ownerId = ownerId;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public String getOwnerId() {
    return ownerId;
  }

  public void setOwnerId(String ownerId) {
    this.ownerId = ownerId;
  }

  @Override
  public String toString() {
    return "Company{" +
        "id=" + id +
        ", name='" + name + '\'' +
        ", description='" + description + '\'' +
        ", address='" + address + '\'' +
        ", ownerId='" + ownerId + '\'' +
        '}';
  }
}