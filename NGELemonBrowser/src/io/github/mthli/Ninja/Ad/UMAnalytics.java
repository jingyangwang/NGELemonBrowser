package io.github.mthli.Ninja.Ad;

import java.util.HashMap;
import java.util.Map;

import com.umeng.analytics.MobclickAgent;

import android.content.Context;
import io.github.mthli.Ninja.Unit.DLog;

public class UMAnalytics {
	public static final String site_click = "site_click";
	public static final String video_click = "video_click";
	public static final String games_click = "games_click";
	public static final String notify_click = "notify_click";
    /**
     * 计数事件
     * @param context
     * @param eventId
     * @param map
     */
    public static void commit(Context context, String eventId, Map map) {
        try {
            MobclickAgent.onEvent(context, eventId, map);
            MobclickAgent.flush(context);

            DLog.i("debug","umeng event: " + eventId + "|" + map);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void commit(Context context, String eventId, String param, String value) {
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put(param, value);
        UMAnalytics.commit(context, eventId, hashMap);
    }
    
    public static void commit(Context context, String eventId) {
    	DLog.i("debug","umeng event: " + eventId);
    	MobclickAgent.onEvent(context, eventId);
        MobclickAgent.flush(context);
    }


}
