package io.github.mthli.Ninja.Activity;

import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.os.Bundle;
import io.github.mthli.Ninja.Ad.UMAnalytics;

public class WakeActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		UMAnalytics.commit(this, "WakeActivity_wake");
	}
	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
		finish();
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
	
}
