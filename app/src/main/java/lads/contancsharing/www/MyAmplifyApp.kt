package lads.contancsharing.www

import android.app.Application
import android.util.Log
import com.amplifyframework.AmplifyException
import com.amplifyframework.api.aws.AWSApiPlugin
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin
import com.amplifyframework.core.Amplify
import com.amplifyframework.storage.s3.AWSS3StoragePlugin

class MyAmplifyApp : Application() {
    override fun onCreate() {
        super.onCreate()

        try {

            Amplify.addPlugin(AWSApiPlugin())
            Amplify.addPlugin(AWSCognitoAuthPlugin())
//            Amplify.addPlugin(AWSS3StoragePlugin())
            Amplify.configure(applicationContext)
            Log.i(
                "com.lads.contactsharing", "Initialized Amplify")
        } catch (error: AmplifyException) {
            Log.e("com.lads.contactsharing", "Could not initialize Amplify", error)
        }


    }
}