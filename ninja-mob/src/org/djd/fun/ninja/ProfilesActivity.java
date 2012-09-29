package org.djd.fun.ninja;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.widget.Toast;
import com.singly.sdk.APICallListener;
import com.singly.sdk.SinglyClient;
import com.singly.util.HttpException;
import com.singly.util.SinglyHttpClient;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ProfilesActivity extends Activity {
  private static final String TAG = ProfilesActivity.class.getSimpleName();
  private SinglyClient api;
  private WebView webView;
  private SinglyHttpClient httpClient;

  @Override
  public void onCreate(Bundle savedInstanceState) {

    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_profiles);
    httpClient = new SinglyHttpClient();
    webView = (WebView)super.findViewById(R.id.wv_profiles);
    webView.getSettings().setBuiltInZoomControls(true);

    final Map<String, String> paramMap = new HashMap<String, String>();
    paramMap.put("data", "true"); // for profiles query with data.
    api = new SinglyClient(this, Constants.CLIENT_ID, Constants.CLIENT_SECRET);

    Toast.makeText(getBaseContext(), R.string.msg_loading_data, Toast.LENGTH_SHORT).show();
    api.apiCall(Constants.END_POINT_PROFILES, paramMap, new CallBackListenerWithPush());
  }

  @Override
  public void onStop(){
    super.onStop();
    if(api != null) {
      api.shutdown();
    }
  }

  private class CallBackListenerWithPush implements APICallListener {
    @Override
    public void onSuccess(JSONObject jsonObject) {
      display(jsonObject, R.string.title_activity_profiles);
      transformAndDisplay(new Transformer(jsonObject, Constants.PROFILE_ATTRIBUTES_FILTER));
    }
    @Override
    public void onError(String message) {
      Toast.makeText(getBaseContext(), message, Toast.LENGTH_LONG).show();
    }
  }

  private void transformAndDisplay(Transformer transformer) {
    try {
      transformer.transform();
    } catch (TransformerException e) {
      Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
    }
    push(transformer);
    display(transformer.getResultJsonObject(), R.string.title_activity_transformed_profiles);
    Toast.makeText(getBaseContext(), R.string.msg_pushing_to_hyperion, Toast.LENGTH_SHORT).show();
  }

  private void display(JSONObject jsonObj, int titleId) {
    try {
      if (jsonObj != null) {
        String json = jsonObj.toString(2);
//        new AlertDialog.Builder(this).setMessage(json).setTitle(titleId).create().show();
        webView.scrollTo(0, 0);
        webView.loadData(wrapHtml(json), HTTP.PLAIN_TEXT_TYPE, HTTP.UTF_8);
      }
    } catch (JSONException e) {
      Log.e(TAG, e.getMessage());
    }
  }

  private String wrapHtml(String content){
    return "<pre>"+content.replaceAll("\n", "</br>") + "</pre>";
  }

  private void push(Transformer transformer){
    String endpoint = Constants.HYPERION_ENDPOINT + transformer.getResultId();
    Log.i(TAG, endpoint);
    try {
      byte[] response = httpClient.postAsJson(endpoint, transformer.getResultJsonObject());
      Log.i(TAG, new String(response));
    } catch (HttpException e) {
      Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
    }

//    http://stackoverflow.com/questions/6028981/using-httpclient-and-httppost-in-android-with-post-parameters
  }
}
