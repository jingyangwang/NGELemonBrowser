/*
 * Zirco Browser for Android
 * 
 * Copyright (C) 2010 - 2011 J. Devauchelle and contributors.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * version 3 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */

package io.github.mthli.Ninja.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import org.zirco.R;
import io.github.mthli.Ninja.Ad.SubAdTools;

public class Welcome extends BaseActivity {

	private boolean finish;
	private SharedPreferences sp;
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.welcome);

		sp = PreferenceManager.getDefaultSharedPreferences(this); 
		new Thread(new Runnable() {

			@Override
			public void run() {
				String oldJson = sp.getString("data_json", null);
				if (TextUtils.isEmpty(oldJson)) {
					String json = SubAdTools.getDataJson(getApplicationContext());
					if (!finish) {
						Message message = new Message();
						message.what = 1;
						message.obj = json;
						mHandler.sendMessage(message);
					}
				}
			}
		}).start();
		mHandler.removeMessages(0);
		mHandler.sendEmptyMessageDelayed(0, 2000);
	}

	Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				finish = true;
				startActivity(new Intent(Welcome.this, BrowserActivity.class));
				finish();
				break;
			case 1:
				String json = (String) msg.obj;
				sp.edit().putString("data_json", json).commit();
			default:
				break;
			}
		}

	};

}
