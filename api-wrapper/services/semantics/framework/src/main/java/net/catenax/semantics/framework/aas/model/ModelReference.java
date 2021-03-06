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
import java.util.ArrayList;
import java.util.List;

/**
 * ModelReference
 */

@javax.annotation.Generated(value = "io.swagger.codegen.v3.generators.java.JavaClientCodegen", date = "2022-03-04T18:11:20.840352600+01:00[Europe/Berlin]")
public class ModelReference {
  @JsonProperty("referredSemanticId")
  private Reference referredSemanticId = null;

  @JsonProperty("keys")
  private List<Key> keys = new ArrayList<>();

  public ModelReference referredSemanticId(Reference referredSemanticId) {
    this.referredSemanticId = referredSemanticId;
    return this;
  }

   /**
   * Get referredSemanticId
   * @return referredSemanticId
  **/
  @Schema(description = "")
  public Reference getReferredSemanticId() {
    return referredSemanticId;
  }

  public void setReferredSemanticId(Reference referredSemanticId) {
    this.referredSemanticId = referredSemanticId;
  }

  public ModelReference keys(List<Key> keys) {
    this.keys = keys;
    return this;
  }

  public ModelReference addKeysItem(Key keysItem) {
    this.keys.add(keysItem);
    return this;
  }

   /**
   * Get keys
   * @return keys
  **/
  @Schema(required = true, description = "")
  public List<Key> getKeys() {
    return keys;
  }

  public void setKeys(List<Key> keys) {
    this.keys = keys;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ModelReference modelReference = (ModelReference) o;
    return Objects.equals(this.referredSemanticId, modelReference.referredSemanticId) &&
        Objects.equals(this.keys, modelReference.keys);
  }

  @Override
  public int hashCode() {
    return Objects.hash(referredSemanticId, keys);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ModelReference {\n");
    
    sb.append("    referredSemanticId: ").append(toIndentedString(referredSemanticId)).append("\n");
    sb.append("    keys: ").append(toIndentedString(keys)).append("\n");
    sb.append("}");
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
