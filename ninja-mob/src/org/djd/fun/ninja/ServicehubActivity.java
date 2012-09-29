package org.djd.fun.ninja;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.singly.sdk.APICallListener;
import com.singly.sdk.SinglyClient;
import com.singly.util.JSON;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: acorn
 * Date: 9/27/12
 * Time: 9:50 PM
 * To change this template use File | Settings | File Templates.
 */
@Deprecated
public class ServicehubActivity extends Activity {
  private static final String TAG = ServicehubActivity.class.getSimpleName();
  private SinglyClient api = null;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_servicehub);
    final Map<String, String> paramMap = new HashMap<String, String>();
    paramMap.put("data", "true"); // for profiles query with data.
    api = new SinglyClient(this, Constants.CLIENT_ID, Constants.CLIENT_SECRET);

    findViewById(R.id.btn_services).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Toast.makeText(getBaseContext(), R.string.msg_loading_data, Toast.LENGTH_SHORT).show();
        api.apiCall(Constants.END_POINT_SERVICES, null, new CallBackListener());
      }
    });

    findViewById(R.id.btn_profiles).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Toast.makeText(getBaseContext(), R.string.msg_loading_data, Toast.LENGTH_SHORT).show();
        api.apiCall(Constants.END_POINT_PROFILES, paramMap, new CallBackListener());
      }
    });

    findViewById(R.id.btn_profiles_push).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Toast.makeText(getBaseContext(), R.string.msg_loading_data, Toast.LENGTH_SHORT).show();
        api.apiCall(Constants.END_POINT_PROFILES, paramMap, new CallBackListenerWithPush());
      }
    });

    findViewById(R.id.btn_github).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Toast.makeText(getBaseContext(), R.string.msg_loading_data, Toast.LENGTH_SHORT).show();
        api.apiCall(Constants.END_POINT_GITHUB, null, new CallBackListener());
      }
    });

    findViewById(R.id.btn_github_self).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Toast.makeText(getBaseContext(), R.string.msg_loading_data, Toast.LENGTH_SHORT).show();
        api.apiCall(Constants.END_POINT_GITHUB_SELF, null, new CallBackListener());
      }
    });

    findViewById(R.id.btn_facebook).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Toast.makeText(getBaseContext(), R.string.msg_loading_data, Toast.LENGTH_SHORT).show();
        api.apiCall(Constants.END_POINT_FACEBOOK, null, new CallBackListener());
      }
    });

    findViewById(R.id.btn_facebook_self).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Toast.makeText(getBaseContext(), R.string.msg_loading_data, Toast.LENGTH_SHORT).show();
        api.apiCall(Constants.END_POINT_FACEBOOK_SELF, null, new CallBackListener());
      }
    });

    findViewById(R.id.btn_linkedin).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Toast.makeText(getBaseContext(), R.string.msg_loading_data, Toast.LENGTH_SHORT).show();
        api.apiCall(Constants.END_POINT_LINKEDIN, null, new CallBackListener());
      }
    });

    findViewById(R.id.btn_linkedin_self).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Toast.makeText(getBaseContext(), R.string.msg_loading_data, Toast.LENGTH_SHORT).show();
        api.apiCall(Constants.END_POINT_LINKEDIN_SELF, null, new CallBackListener());
      }
    });

    findViewById(R.id.btn_debug_json).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        final String json = "{\"id\":\"7ca4317f9af2216c8ced0ad386298c3f\",\"github\":[{\"type\":\"User\",\"login\":\"do-i\",\"html_url\":\"https://github.com/do-i\",\"followers\":0,\"public_gists\":0,\"created_at\":\"2012-06-01T04:39:58Z\",\"company\":\"Ninjump\",\"email\":\"ninja@nin.ja\",\"hireable\":false,\"public_repos\":7,\"bio\":null,\"following\":0,\"name\":\"Ninja Brothers\",\"blog\":\"http://www.ninja.org\",\"id\":1802897,\"location\":\"Chicago\",\"url\":\"https://api.github.com/users/do-i\",\"gravatar_id\":\"29fd3bc31a6d470d009cc5ce53c9cba9\",\"avatar_url\":\"https://secure.gravatar.com/avatar/29fd3bc31a6d470d009cc5ce53c9cba9?d=https://a248.e.akamai.net/assets.github.com%2Fimages%2Fgravatars%2Fgravatar-user-420.png\"}]}";
        transformAndDisplay(new Transformer(JSON.parse(json)));
      }
    });
  }

  @Override
  public void onStop(){
    super.onStop();
    if(api != null) {
      api.shutdown();
    }
  }

  private class CallBackListener implements APICallListener {

    @Override
    public void onSuccess(JSONObject jsonObj) {
      display(jsonObj, R.string.title_activity_profiles);
    }

    @Override
    public void onError(String message) {
      Toast.makeText(getBaseContext(), message, Toast.LENGTH_LONG).show();
    }
  }

  private class CallBackListenerWithPush extends CallBackListener {
    @Override
    public void onSuccess(JSONObject jsonObject) {
      super.onSuccess(jsonObject);
      transformAndDisplay(new Transformer(jsonObject, Constants.PROFILE_ATTRIBUTES_FILTER));
    }
  }

  private void transformAndDisplay(Transformer transformer) {
    try {
      transformer.transform();
    } catch (TransformerException e) {
      Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
    }
    String endpoint = Constants.HYPERION_ENDPOINT + transformer.getResultId();
    Log.i(TAG, endpoint);

    display(transformer.getResultJsonObject(), R.string.title_activity_transformed_profiles);
    Toast.makeText(getBaseContext(), R.string.msg_pushing_to_hyperion, Toast.LENGTH_SHORT).show();
  }

  private void display(JSONObject jsonObj, int titleId) {
    try {
      if (jsonObj != null) {
        String json = jsonObj.toString(2);
        new AlertDialog.Builder(this).setMessage(json).setTitle(titleId).create().show();
      }
    } catch (JSONException e) {
      Log.e(TAG, e.getMessage());
    }
  }
}