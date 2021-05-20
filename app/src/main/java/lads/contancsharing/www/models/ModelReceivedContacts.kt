package lads.contancsharing.www.models


import com.amplifyframework.datastore.generated.model.ContactSharingWith
import com.amplifyframework.datastore.generated.model.UserContactSharing

data class ModelReceivedContacts(var user: UserContactSharing, var size: String,var dateTime:String,var contactSharingWith: ContactSharingWith)



