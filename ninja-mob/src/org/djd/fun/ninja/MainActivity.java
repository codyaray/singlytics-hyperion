package org.djd.fun.ninja;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity {
  private static final String TAG = MainActivity.class.getSimpleName();

  private final Activity activity = this;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    setContentView(R.layout.activity_main);

    findViewById(R.id.btn_authorize).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        startActivity(new Intent(activity, AuthorizeActivity.class));
      }
    });

    findViewById(R.id.btn_servicehub).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        startActivity(new Intent(activity, ServicehubActivity.class));
      }
    });

    findViewById(R.id.btn_profiles).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        startActivity(new Intent(activity, ProfilesActivity.class));
      }
    });
  }
}