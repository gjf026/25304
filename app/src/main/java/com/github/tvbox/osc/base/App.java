package com.github.tvbox.osc.base;

import android.app.Activity;
import androidx.multidex.MultiDexApplication;

import com.github.tvbox.osc.bean.VodInfo;
import com.github.tvbox.osc.callback.EmptyCallback;
import com.github.tvbox.osc.callback.LoadingCallback;
import com.github.tvbox.osc.data.AppDataManager;
import com.github.tvbox.osc.server.ControlManager;
import com.github.tvbox.osc.util.AppManager;
import com.github.tvbox.osc.util.EpgUtil;
import com.github.tvbox.osc.util.FileUtils;
import com.github.tvbox.osc.util.HawkConfig;
import com.github.tvbox.osc.util.LOG;
import com.github.tvbox.osc.util.OkGoHelper;
import com.github.tvbox.osc.util.PlayerHelper;
import com.github.tvbox.osc.util.js.JSEngine;
import com.kingja.loadsir.core.LoadSir;
import com.orhanobut.hawk.Hawk;
import com.p2p.P2PClass;

import me.jessyan.autosize.AutoSizeConfig;
import me.jessyan.autosize.unit.Subunits;

/**
 * @author pj567
 * @date :2020/12/17
 * @description:
 */
public class App extends MultiDexApplication {
    private static App instance;

    private static P2PClass p;
    public static String burl;
    private static String dashData;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        initParams();
        // OKGo
        OkGoHelper.init(); //台标获取
        EpgUtil.init();
        // 初始化Web服务器
        ControlManager.init(this);
        //初始化数据库
        AppDataManager.init();
        LoadSir.beginBuilder()
                .addCallback(new EmptyCallback())
                .addCallback(new LoadingCallback())
                .commit();
        AutoSizeConfig.getInstance().setCustomFragment(true).getUnitsManager()
                .setSupportDP(false)
                .setSupportSP(false)
                .setSupportSubunits(Subunits.MM);
        PlayerHelper.init();
        JSEngine.getInstance().create();
        FileUtils.cleanPlayerCache();
    }

    private void initParams() {      //xuameng系统默认设置
        // Hawk
        Hawk.init(this).build();
        Hawk.put(HawkConfig.DEBUG_OPEN, false);
        putDefault(HawkConfig.PLAY_TYPE, 1);         //播放器: 0=系统, 1=IJK, 2=Exo
        putDefault(HawkConfig.HOME_REC, 0);          // Home Rec 0=豆瓣, 1=推荐, 2=历史
        putDefault(HawkConfig.IJK_CODEC, "硬解码");  // IJK Render 软解码, 硬解码
        putDefault(HawkConfig.HOME_NUM, 2);          // 0,30,1,50,2,70
	    putDefault(HawkConfig.SHOW_PREVIEW, true);   //窗口预览: true=开启, false=关闭
        putDefault(HawkConfig.SEARCH_VIEW, 1);       //搜索展示: 0=文字列表, 1=缩略图
	    putDefault(HawkConfig.PLAY_SCALE, 3);		 //画面缩放: 0=默认, 1=16:9, 2=4:3, 3=填充, 4=原始, 5=裁剪
	    //putDefault(HawkConfig.DOH_URL, 2);         // DNS
	    //putDefault(HawkConfig.HOME_SHOW_SOURCE, true);  //安全DNS: 0=关闭, 1=腾讯, 2=阿里, 3=360, 4=Google, 5=AdGuard, 6=Quad9
    }

    public static App getInstance() {
        return instance;
    }

    private void putDefault(String key, Object value) {	//xuameng系统默认设置方法
        if (!Hawk.contains(key)) {
            Hawk.put(key, value);
        }
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        JSEngine.getInstance().destroy();
    }


    private VodInfo vodInfo;
    public void setVodInfo(VodInfo vodinfo){
        this.vodInfo = vodinfo;
    }
    public VodInfo getVodInfo(){
        return this.vodInfo;
    }

    public static P2PClass getp2p() {
        try {
            if (p == null) {
                p = new P2PClass(instance.getExternalCacheDir().getAbsolutePath());
            }
            return p;
        } catch (Exception e) {
            LOG.e(e.toString());
            return null;
        }
    }

    public Activity getCurrentActivity() {
        return AppManager.getInstance().currentActivity();
    }

    public void setDashData(String data) {
        dashData = data;
    }
    public String getDashData() {
        return dashData;
    }
}