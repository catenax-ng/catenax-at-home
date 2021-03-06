/*
 * DotAAS Part 2 | HTTP/REST | Registry and Discovery
 * The registry and discovery interface as part of Details of the Asset Administration Shell Part 2
 *
 * OpenAPI spec version: Final-Draft
 * 
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */

package net.catenax.semantics.framework.aas.model;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * IdentifierKeyValuePair
 */

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen", date = "2022-03-04T18:11:20.840352600+01:00[Europe/Berlin]")
public class IdentifierKeyValuePair extends HasSemantics {
  @JsonProperty("key")
  private String key = null;

  @JsonProperty("subjectId")
  private Reference subjectId = null;

  @JsonProperty("value")
  private String value = null;

  public IdentifierKeyValuePair key(String key) {
    this.key = key;
    return this;
  }

   /**
   * Get key
   * @return key
  **/
  @Schema(required = true, description = "")
  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public IdentifierKeyValuePair subjectId(Reference subjectId) {
    this.subjectId = subjectId;
    return this;
  }

   /**
   * Get subjectId
   * @return subjectId
  **/
  @Schema(description = "")
  public Reference getSubjectId() {
    return subjectId;
  }

  public void setSubjectId(Reference subjectId) {
    this.subjectId = subjectId;
  }

  public IdentifierKeyValuePair value(String value) {
    this.value = value;
    return this;
  }

   /**
   * Get value
   * @return value
  **/
  @Schema(required = true, description = "")
  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    IdentifierKeyValuePair identifierKeyValuePair = (IdentifierKeyValuePair) o;
    return Objects.equals(this.key, identifierKeyValuePair.key) &&
        Objects.equals(this.subjectId, identifierKeyValuePair.subjectId) &&
        Objects.equals(this.value, identifierKeyValuePair.value) &&
        super.equals(o);
  }

  @Override
  public int hashCode() {
    return Objects.hash(key, subjectId, value, super.hashCode());
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("{\"key\":\"");
    sb.append(getKey());
    sb.append("\",\"value\":\"");
    sb.append(getValue());
    sb.append("\"}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }

}
