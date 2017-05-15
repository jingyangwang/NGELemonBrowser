package io.github.mthli.Ninja.Unit;

import android.util.Log;

public class DLog {

	// true为打开log日志，false为关闭log日志
	public static boolean EnableLog = false;

	public static void i(final String tag, final Object obj) {
		if (EnableLog)
			Log.i(tag, "" + obj);
	}

	public static void v(final String tag, final Object obj) {
		if (EnableLog)
			Log.v(tag, "" + obj);
	}

	public static void e(final String tag, final Object obj) {
		if (EnableLog)
			Log.e(tag, "" + obj);
	}

	public static void d(final String tag, final Object obj) {
		if (EnableLog)
			Log.d(tag, "" + obj);
	}
}
