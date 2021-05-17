package com.amplifyframework.datastore.generated.model;


import java.util.List;
import java.util.UUID;
import java.util.Objects;

import androidx.core.util.ObjectsCompat;

import com.amplifyframework.core.model.Model;
import com.amplifyframework.core.model.annotations.Index;
import com.amplifyframework.core.model.annotations.ModelConfig;
import com.amplifyframework.core.model.annotations.ModelField;
import com.amplifyframework.core.model.query.predicate.QueryField;

import static com.amplifyframework.core.model.query.predicate.QueryField.field;

/** This is an auto generated class representing the UserContactSharing type in your schema. */
@SuppressWarnings("all")
@ModelConfig(pluralName = "UserContactSharings")
public final class UserContactSharing implements Model {
  public static final QueryField ID = field("UserContactSharing", "id");
  public static final QueryField NAME = field("UserContactSharing", "name");
  public static final QueryField PHONE = field("UserContactSharing", "phone");
  public static final QueryField IMAGE = field("UserContactSharing", "image");
  public static final QueryField DEVICE_TOKEN = field("UserContactSharing", "device_token");
  public static final QueryField COUNTRY_CODE = field("UserContactSharing", "country_code");
  private final @ModelField(targetType="ID", isRequired = true) String id;
  private final @ModelField(targetType="String", isRequired = true) String name;
  private final @ModelField(targetType="String", isRequired = true) String phone;
  private final @ModelField(targetType="String", isRequired = true) String image;
  private final @ModelField(targetType="String", isRequired = true) String device_token;
  private final @ModelField(targetType="String") String country_code;
  public String getId() {
      return id;
  }
  
  public String getName() {
      return name;
  }
  
  public String getPhone() {
      return phone;
  }
  
  public String getImage() {
      return image;
  }
  
  public String getDeviceToken() {
      return device_token;
  }
  
  public String getCountryCode() {
      return country_code;
  }
  
  private UserContactSharing(String id, String name, String phone, String image, String device_token, String country_code) {
    this.id = id;
    this.name = name;
    this.phone = phone;
    this.image = image;
    this.device_token = device_token;
    this.country_code = country_code;
  }
  
  @Override
   public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      } else if(obj == null || getClass() != obj.getClass()) {
        return false;
      } else {
      UserContactSharing userContactSharing = (UserContactSharing) obj;
      return ObjectsCompat.equals(getId(), userContactSharing.getId()) &&
              ObjectsCompat.equals(getName(), userContactSharing.getName()) &&
              ObjectsCompat.equals(getPhone(), userContactSharing.getPhone()) &&
              ObjectsCompat.equals(getImage(), userContactSharing.getImage()) &&
              ObjectsCompat.equals(getDeviceToken(), userContactSharing.getDeviceToken()) &&
              ObjectsCompat.equals(getCountryCode(), userContactSharing.getCountryCode());
      }
  }
  
  @Override
   public int hashCode() {
    return new StringBuilder()
      .append(getId())
      .append(getName())
      .append(getPhone())
      .append(getImage())
      .append(getDeviceToken())
      .append(getCountryCode())
      .toString()
      .hashCode();
  }
  
  @Override
   public String toString() {
    return new StringBuilder()
      .append("UserContactSharing {")
      .append("id=" + String.valueOf(getId()) + ", ")
      .append("name=" + String.valueOf(getName()) + ", ")
      .append("phone=" + String.valueOf(getPhone()) + ", ")
      .append("image=" + String.valueOf(getImage()) + ", ")
      .append("device_token=" + String.valueOf(getDeviceToken()) + ", ")
      .append("country_code=" + String.valueOf(getCountryCode()))
      .append("}")
      .toString();
  }
  
  public static NameStep builder() {
      return new Builder();
  }
  
  /** 
   * WARNING: This method should not be used to build an instance of this object for a CREATE mutation.
   * This is a convenience method to return an instance of the object with only its ID populated
   * to be used in the context of a parameter in a delete mutation or referencing a foreign key
   * in a relationship.
   * @param id the id of the existing item this instance will represent
   * @return an instance of this model with only ID populated
   * @throws IllegalArgumentException Checks that ID is in the proper format
   */
  public static UserContactSharing justId(String id) {
    try {
      UUID.fromString(id); // Check that ID is in the UUID format - if not an exception is thrown
    } catch (Exception exception) {
      throw new IllegalArgumentException(
              "Model IDs must be unique in the format of UUID. This method is for creating instances " +
              "of an existing object with only its ID field for sending as a mutation parameter. When " +
              "creating a new object, use the standard builder method and leave the ID field blank."
      );
    }
    return new UserContactSharing(
      id,
      null,
      null,
      null,
      null,
      null
    );
  }
  
  public CopyOfBuilder copyOfBuilder() {
    return new CopyOfBuilder(id,
      name,
      phone,
      image,
      device_token,
      country_code);
  }
  public interface NameStep {
    PhoneStep name(String name);
  }
  

  public interface PhoneStep {
    ImageStep phone(String phone);
  }
  

  public interface ImageStep {
    DeviceTokenStep image(String image);
  }
  

  public interface DeviceTokenStep {
    BuildStep deviceToken(String deviceToken);
  }
  

  public interface BuildStep {
    UserContactSharing build();
    BuildStep id(String id) throws IllegalArgumentException;
    BuildStep countryCode(String countryCode);
  }
  

  public static class Builder implements NameStep, PhoneStep, ImageStep, DeviceTokenStep, BuildStep {
    private String id;
    private String name;
    private String phone;
    private String image;
    private String device_token;
    private String country_code;
    @Override
     public UserContactSharing build() {
        String id = this.id != null ? this.id : UUID.randomUUID().toString();
        
        return new UserContactSharing(
          id,
          name,
          phone,
          image,
          device_token,
          country_code);
    }
    
    @Override
     public PhoneStep name(String name) {
        Objects.requireNonNull(name);
        this.name = name;
        return this;
    }
    
    @Override
     public ImageStep phone(String phone) {
        Objects.requireNonNull(phone);
        this.phone = phone;
        return this;
    }
    
    @Override
     public DeviceTokenStep image(String image) {
        Objects.requireNonNull(image);
        this.image = image;
        return this;
    }
    
    @Override
     public BuildStep deviceToken(String deviceToken) {
        Objects.requireNonNull(deviceToken);
        this.device_token = deviceToken;
        return this;
    }
    
    @Override
     public BuildStep countryCode(String countryCode) {
        this.country_code = countryCode;
        return this;
    }
    
    /** 
     * WARNING: Do not set ID when creating a new object. Leave this blank and one will be auto generated for you.
     * This should only be set when referring to an already existing object.
     * @param id id
     * @return Current Builder instance, for fluent method chaining
     * @throws IllegalArgumentException Checks that ID is in the proper format
     */
    public BuildStep id(String id) throws IllegalArgumentException {
        this.id = id;
        
        try {
            UUID.fromString(id); // Check that ID is in the UUID format - if not an exception is thrown
        } catch (Exception exception) {
          throw new IllegalArgumentException("Model IDs must be unique in the format of UUID.",
                    exception);
        }
        
        return this;
    }
  }
  

  public final class CopyOfBuilder extends Builder {
    private CopyOfBuilder(String id, String name, String phone, String image, String deviceToken, String countryCode) {
      super.id(id);
      super.name(name)
        .phone(phone)
        .image(image)
        .deviceToken(deviceToken)
        .countryCode(countryCode);
    }
    
    @Override
     public CopyOfBuilder name(String name) {
      return (CopyOfBuilder) super.name(name);
    }
    
    @Override
     public CopyOfBuilder phone(String phone) {
      return (CopyOfBuilder) super.phone(phone);
    }
    
    @Override
     public CopyOfBuilder image(String image) {
      return (CopyOfBuilder) super.image(image);
    }
    
    @Override
     public CopyOfBuilder deviceToken(String deviceToken) {
      return (CopyOfBuilder) super.deviceToken(deviceToken);
    }
    
    @Override
     public CopyOfBuilder countryCode(String countryCode) {
      return (CopyOfBuilder) super.countryCode(countryCode);
    }
  }
  
}
