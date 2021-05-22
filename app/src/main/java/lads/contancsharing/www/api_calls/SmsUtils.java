package lads.contancsharing.www.api_calls;

import android.util.Base64;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SmsUtils {
    private static final String ACCOUNT_SID = "ACb92d91e5885ef6a6f415566d4d10764c";
    private static final String AUTH_TOKEN = "260230db0b9c09af3f901ee9f985de4f";
    private static String PLAY_STORE_URL = "play_store_url";

    public static void sendMessage(String toNumberWithCountryCode) {
        String from = "+15715260764";

        String base64EncodedCredentials = "Basic " + Base64.encodeToString(
                (ACCOUNT_SID + ":" + AUTH_TOKEN).getBytes(), Base64.NO_WRAP
        );
        String MESSAGE = toNumberWithCountryCode.toString() + " Shared some file(s) with you. You can get files by downloading File Sharing Manager from: " + PLAY_STORE_URL;

        Map<String, String> smsData = new HashMap<>();
        smsData.put("From", from);
        smsData.put("To", toNumberWithCountryCode);
        smsData.put("Body", MESSAGE);

        Log.d("com.lads.contactsharing", "sending sms  message=" + MESSAGE + "  \nto= " + toNumberWithCountryCode);
        ApiInterface api = ApiClient.getRetrofit().create(ApiInterface.class);

        api.sendMessage(ACCOUNT_SID, base64EncodedCredentials, smsData).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Log.d("com.lads.contactsharing", "twillio sms sent to " + toNumberWithCountryCode);
                } else {
                    Log.d("com.lads.contactsharing", "twillio sms on failure " + response.message());
                    Log.d("com.lads.contactsharing", "twillio sms on failure code " + response.code());

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("com.lads.contactsharing", "Twillio onFailure " + t.getLocalizedMessage());
            }
        });
    }
}
