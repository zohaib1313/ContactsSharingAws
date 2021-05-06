package lads.contancsharing.www.models

class ContactsInfo {
    private var contactId: String? = null
    private var displayName: String? = null
    private var phoneNumber: String? = null
    private var userImage: String? = null
    private var status: String? = null

    constructor(
        contactId: String?,
        displayName: String?,
        phoneNumber: String?,
        userImage: String?,
        status: String?
    ) {
        this.contactId = contactId
        this.displayName = displayName
        this.phoneNumber = phoneNumber
        this.userImage = userImage
        this.status = status
    }


    @JvmName("getStatus1")
    fun getStatus(): String? {
        return status
    }

    @JvmName("setStatus1")
    fun setStatus(status: String?) {
        this.status = status
    }

    @JvmName("getContactId1")
    fun getContactId(): String? {
        return contactId
    }

    @JvmName("setContactId1")
    fun setContactId(contactId: String?) {
        this.contactId = contactId
    }

  @JvmName("getUserImage1")
    fun getUserImage(): String? {
        return userImage
    }

    @JvmName("setUserImage1")
    fun setUserImage(userImage: String?) {
        this.userImage = userImage
    }

    @JvmName("getDisplayName1")
    fun getDisplayName(): String? {
        return displayName
    }

    @JvmName("setDisplayName1")
    fun setDisplayName(displayName: String?) {
        this.displayName = displayName
    }

    @JvmName("getPhoneNumber1")
    fun getPhoneNumber(): String? {
        return phoneNumber
    }

    @JvmName("setPhoneNumber1")
    fun setPhoneNumber(phoneNumber: String?) {
        this.phoneNumber = phoneNumber
    }
}