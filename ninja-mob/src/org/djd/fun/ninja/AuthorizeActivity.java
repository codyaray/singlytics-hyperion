package org.djd.fun.ninja;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import com.singly.sdk.AuthorizedListener;
import com.singly.sdk.SinglyClient;

/**
 * Created with IntelliJ IDEA.
 * User: acorn
 * Date: 9/27/12
 * Time: 9:59 PM
 * To change this template use File | Settings | File Templates.
 */
public class AuthorizeActivity extends Activity {

  private final Activity activity = this;
  private SinglyClient api = null;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_authorize);
    api = new SinglyClient(this, Constants.CLIENT_ID, Constants.CLIENT_SECRET);
    findViewById(R.id.facebook).setOnClickListener(new MyOnClickListener(Constants.SERVICE_NAME_FACEBOOK));
    findViewById(R.id.flickr).setOnClickListener(new MyOnClickListener(Constants.SERVICE_NAME_FLICKR));
    findViewById(R.id.github).setOnClickListener(new MyOnClickListener(Constants.SERVICE_NAME_GITHUB));
    findViewById(R.id.linkedin).setOnClickListener(new MyOnClickListener(Constants.SERVICE_NAME_LINKEDIN));
  }

  @Override
  public void onStop(){
    super.onStop();
    if(api != null) {
      api.shutdown();
    }
  }

  private class MyOnClickListener implements View.OnClickListener {

    private final String serviceName;

    MyOnClickListener(String serviceName) {
      this.serviceName = serviceName;
    }

    public void onClick(View v) {

      final ProgressDialog progressDialog = new ProgressDialog(activity);
      progressDialog.setMessage("Loading Authentication");
      progressDialog.setCancelable(false);
      progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
      progressDialog.setProgress(0); // set percentage completed to 0%

      api.authorize(serviceName, new AuthorizedListener() {

        public void onStart() {
          progressDialog.show();
        }

        public void onProgress(int progress) {
          progressDialog.setProgress(progress);
        }

        public void onPageLoaded() {
          progressDialog.dismiss();
        }

        public void onAuthorized() {
          Toast.makeText(activity, R.string.msg_authorization_completed, Toast.LENGTH_SHORT).show();
        }

        public void onError(AuthorizedListener.Errors error) {
          String msg = error.toString();
          Toast.makeText(activity, msg, Toast.LENGTH_LONG).show();
        }

        public void onCancel() {
          progressDialog.dismiss();
          Toast.makeText(activity, R.string.msg_authorization_cancelled, Toast.LENGTH_LONG).show();
        }
      });
    }
  }
}