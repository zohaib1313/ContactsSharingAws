package com.amplifyframework.datastore.generated.model;

import com.amplifyframework.core.model.annotations.BelongsTo;

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

/** This is an auto generated class representing the ContactSharingWith type in your schema. */
@SuppressWarnings("all")
@ModelConfig(pluralName = "ContactSharingWiths")
public final class ContactSharingWith implements Model {
  public static final QueryField ID = field("ContactSharingWith", "id");
  public static final QueryField USER_ID = field("ContactSharingWith", "user_id");
  public static final QueryField FILE_PATH = field("ContactSharingWith", "file_path");
  public static final QueryField SHARE_WITH = field("ContactSharingWith", "share_with");
  public static final QueryField FILE_TIME = field("ContactSharingWith", "file_time");
  public static final QueryField STATUS = field("ContactSharingWith", "status");
  public static final QueryField USER = field("ContactSharingWith", "contactSharingWithUserId");
  private final @ModelField(targetType="ID", isRequired = true) String id;
  private final @ModelField(targetType="String", isRequired = true) String user_id;
  private final @ModelField(targetType="String", isRequired = true) String file_path;
  private final @ModelField(targetType="String", isRequired = true) String share_with;
  private final @ModelField(targetType="String", isRequired = true) String file_time;
  private final @ModelField(targetType="String") String status;
  private final @ModelField(targetType="UserContactSharing", isRequired = true) @BelongsTo(targetName = "contactSharingWithUserId", type = UserContactSharing.class) UserContactSharing user;
  public String getId() {
      return id;
  }
  
  public String getUserId() {
      return user_id;
  }
  
  public String getFilePath() {
      return file_path;
  }
  
  public String getShareWith() {
      return share_with;
  }
  
  public String getFileTime() {
      return file_time;
  }
  
  public String getStatus() {
      return status;
  }
  
  public UserContactSharing getUser() {
      return user;
  }
  
  private ContactSharingWith(String id, String user_id, String file_path, String share_with, String file_time, String status, UserContactSharing user) {
    this.id = id;
    this.user_id = user_id;
    this.file_path = file_path;
    this.share_with = share_with;
    this.file_time = file_time;
    this.status = status;
    this.user = user;
  }
  
  @Override
   public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      } else if(obj == null || getClass() != obj.getClass()) {
        return false;
      } else {
      ContactSharingWith contactSharingWith = (ContactSharingWith) obj;
      return ObjectsCompat.equals(getId(), contactSharingWith.getId()) &&
              ObjectsCompat.equals(getUserId(), contactSharingWith.getUserId()) &&
              ObjectsCompat.equals(getFilePath(), contactSharingWith.getFilePath()) &&
              ObjectsCompat.equals(getShareWith(), contactSharingWith.getShareWith()) &&
              ObjectsCompat.equals(getFileTime(), contactSharingWith.getFileTime()) &&
              ObjectsCompat.equals(getStatus(), contactSharingWith.getStatus()) &&
              ObjectsCompat.equals(getUser(), contactSharingWith.getUser());
      }
  }
  
  @Override
   public int hashCode() {
    return new StringBuilder()
      .append(getId())
      .append(getUserId())
      .append(getFilePath())
      .append(getShareWith())
      .append(getFileTime())
      .append(getStatus())
      .append(getUser())
      .toString()
      .hashCode();
  }
  
  @Override
   public String toString() {
    return new StringBuilder()
      .append("ContactSharingWith {")
      .append("id=" + String.valueOf(getId()) + ", ")
      .append("user_id=" + String.valueOf(getUserId()) + ", ")
      .append("file_path=" + String.valueOf(getFilePath()) + ", ")
      .append("share_with=" + String.valueOf(getShareWith()) + ", ")
      .append("file_time=" + String.valueOf(getFileTime()) + ", ")
      .append("status=" + String.valueOf(getStatus()) + ", ")
      .append("user=" + String.valueOf(getUser()))
      .append("}")
      .toString();
  }
  
  public static UserIdStep builder() {
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
  public static ContactSharingWith justId(String id) {
    try {
      UUID.fromString(id); // Check that ID is in the UUID format - if not an exception is thrown
    } catch (Exception exception) {
      throw new IllegalArgumentException(
              "Model IDs must be unique in the format of UUID. This method is for creating instances " +
              "of an existing object with only its ID field for sending as a mutation parameter. When " +
              "creating a new object, use the standard builder method and leave the ID field blank."
      );
    }
    return new ContactSharingWith(
      id,
      null,
      null,
      null,
      null,
      null,
      null
    );
  }
  
  public CopyOfBuilder copyOfBuilder() {
    return new CopyOfBuilder(id,
      user_id,
      file_path,
      share_with,
      file_time,
      status,
      user);
  }
  public interface UserIdStep {
    FilePathStep userId(String userId);
  }
  

  public interface FilePathStep {
    ShareWithStep filePath(String filePath);
  }
  

  public interface ShareWithStep {
    FileTimeStep shareWith(String shareWith);
  }
  

  public interface FileTimeStep {
    UserStep fileTime(String fileTime);
  }
  

  public interface UserStep {
    BuildStep user(UserContactSharing user);
  }
  

  public interface BuildStep {
    ContactSharingWith build();
    BuildStep id(String id) throws IllegalArgumentException;
    BuildStep status(String status);
  }
  

  public static class Builder implements UserIdStep, FilePathStep, ShareWithStep, FileTimeStep, UserStep, BuildStep {
    private String id;
    private String user_id;
    private String file_path;
    private String share_with;
    private String file_time;
    private UserContactSharing user;
    private String status;
    @Override
     public ContactSharingWith build() {
        String id = this.id != null ? this.id : UUID.randomUUID().toString();
        
        return new ContactSharingWith(
          id,
          user_id,
          file_path,
          share_with,
          file_time,
          status,
          user);
    }
    
    @Override
     public FilePathStep userId(String userId) {
        Objects.requireNonNull(userId);
        this.user_id = userId;
        return this;
    }
    
    @Override
     public ShareWithStep filePath(String filePath) {
        Objects.requireNonNull(filePath);
        this.file_path = filePath;
        return this;
    }
    
    @Override
     public FileTimeStep shareWith(String shareWith) {
        Objects.requireNonNull(shareWith);
        this.share_with = shareWith;
        return this;
    }
    
    @Override
     public UserStep fileTime(String fileTime) {
        Objects.requireNonNull(fileTime);
        this.file_time = fileTime;
        return this;
    }
    
    @Override
     public BuildStep user(UserContactSharing user) {
        Objects.requireNonNull(user);
        this.user = user;
        return this;
    }
    
    @Override
     public BuildStep status(String status) {
        this.status = status;
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
    private CopyOfBuilder(String id, String userId, String filePath, String shareWith, String fileTime, String status, UserContactSharing user) {
      super.id(id);
      super.userId(userId)
        .filePath(filePath)
        .shareWith(shareWith)
        .fileTime(fileTime)
        .user(user)
        .status(status);
    }
    
    @Override
     public CopyOfBuilder userId(String userId) {
      return (CopyOfBuilder) super.userId(userId);
    }
    
    @Override
     public CopyOfBuilder filePath(String filePath) {
      return (CopyOfBuilder) super.filePath(filePath);
    }
    
    @Override
     public CopyOfBuilder shareWith(String shareWith) {
      return (CopyOfBuilder) super.shareWith(shareWith);
    }
    
    @Override
     public CopyOfBuilder fileTime(String fileTime) {
      return (CopyOfBuilder) super.fileTime(fileTime);
    }
    
    @Override
     public CopyOfBuilder user(UserContactSharing user) {
      return (CopyOfBuilder) super.user(user);
    }
    
    @Override
     public CopyOfBuilder status(String status) {
      return (CopyOfBuilder) super.status(status);
    }
  }
  
}
