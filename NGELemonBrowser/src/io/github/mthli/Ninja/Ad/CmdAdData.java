package io.github.mthli.Ninja.Ad;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.graphics.Bitmap;

public class CmdAdData {
    /**
     * int 订阅广告
     */
    public final static int TYPE_SUBSCRIPTION = 1;
    /**
     * int 导航广告
     */
    public final static int TYPE_SITE = 2;
    /**
     * int 视频广告
     */
    public final static int TYPE_VIDEO = 3;
    /**
     * int 应用广告
     */
    public final static int TYPE_APP = 4;
    /**
     * int 游戏广告
     */
    public final static int TYPE_GAME = 5;
    /**
     * int 本次请求状态，判断后面是否有数据
     */
    public int mState;
    /**
     * List<SubscrptionData> 订阅广告数据
     */
    public List<SubscriptionData> mSubscriptionAd;
    /**
     * List<SiteAd> 	导航广告数据
     */
    public List<SiteData> mSiteAd;
    /**
     * List<VideoAd> 	视频广告数据
     */
    public List<VideoData> mVideoAd;
    /**
     * List<AppAd> 		应用广告数据
     */
    public List<AppData> mAppAd;
    /**
     * List<GameAd> 游戏广告数据
     */
    public List<GameData> mGameAd;

    /**
     * 订阅广告数据
     */
    public static class AdData {
        // 广告ID
        public Long mAdId;
        // 广告类型
        public String mAdType;
        // 广告图标
        public String mIcon;
        // 广告图标图片文件
        public Bitmap mIconPic;
        // 广告名称
        public String mName;
        // 广告描述
        public String mDescription;
        // 广告视频地址
        public String mVideo;
        // 广告URL链接
        public String mURL;
        
        public AdData(){
            
        }
    }
    
    /**
     * 订阅广告数据
     */
    public static class SubscriptionData extends AdData {
        public SubscriptionData(){
            super();
        }
    }
    
    /**
     * 导航广告数据
     */
    public static class SiteData extends AdData {
    	public SiteData(){
    		super();
    	}
    }
    
    /**
     * 视频广告数据
     */
    public static class VideoData extends AdData {
    	public VideoData(){
    		super();
    	}
    }
    
    /**
     * 应用广告数据
     */
    public static class AppData extends AdData {
    	public AppData(){
    		super();
    	}
    }
    
    /**
     * 游戏广告数据
     */
    public static class GameData extends AdData {
    	public GameData(){
    		super();
    	}
    }

    public CmdAdData() {
    	mSubscriptionAd = new ArrayList<SubscriptionData>();
    	mSiteAd 		= new ArrayList<SiteData>();
    	mVideoAd 		= new ArrayList<VideoData>();
    	mAppAd 			= new ArrayList<AppData>();
    	mGameAd 		= new ArrayList<GameData>();
    }
}
