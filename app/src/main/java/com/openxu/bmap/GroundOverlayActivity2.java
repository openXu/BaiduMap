package com.openxu.bmap;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.GroundOverlayOptions;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.offline.MKOLUpdateElement;
import com.baidu.mapapi.map.offline.MKOfflineMap;
import com.baidu.mapapi.map.offline.MKOfflineMapListener;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.LatLngBounds;
import com.openxu.bmap.bean.MakerPoint;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class GroundOverlayActivity2 extends AppCompatActivity implements MKOfflineMapListener {

    private FrameLayout layout;

    private MapView mMapView;
    private TextView tv_status;

    private MKOfflineMap mOffline;

    private int bjCityid = 131;

    //用于设置个性化地图的样式文件
    // 精简为1套样式模板:
    // "custom_config_dark.json"
    private static String PATH = "custom_config_dark.json";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setMapCustomFile(this, PATH);

        layout = new FrameLayout(this);
        mMapView = new MapView(this, new BaiduMapOptions());
        tv_status = new TextView(this);
        layout.addView(mMapView);
        layout.addView(tv_status);
        setContentView(layout);

        //支持个性化地图
        MapView.setMapCustomEnable(true);

        BaiduMap mBaiduMap = mMapView.getMap();
        mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);

        //隐藏地图上百度地图logo图标
        mMapView.removeViewAt(1);
        //控制缩放按钮是否显示
        mMapView.showZoomControls(false);
        //隐藏地图上比例尺
        mMapView.removeViewAt(2);
        //控制是否一并禁止所有手势
//        mBaiduMap.getUiSettings().setAllGesturesEnabled(false);

        /**定位、缩放比例*/
        MapStatus.Builder builder = new MapStatus.Builder();
        LatLng center = new LatLng(39.915071, 116.403907); // 默认 天安门
//        39.803326, 116.187119
        center = new LatLng(39.804185, 116.186955);    //长辛店 长青路
        float zoom = 18.2f; // 3-19  默认 11级
        builder.target(center).zoom(zoom);
        mBaiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));

        setGroundOverlay(mBaiduMap);
        setPoint(mBaiduMap);
        checkOfflineMap();

    }

    /***************************************图片覆盖物↓↓↓↓↓↓↓***************************************/

    private void setGroundOverlay(BaiduMap mBaiduMap){
        //定义Ground的显示地理范围
        LatLng southwest = new LatLng(39.809085, 116.181784);
        LatLng northeast = new LatLng(39.798840, 116.192388);
        LatLngBounds bounds = new LatLngBounds.Builder()
                .include(northeast)
                .include(southwest)
                .build();
        //定义Ground显示的图片
        BitmapDescriptor bdGround = BitmapDescriptorFactory
                .fromResource(R.drawable.ground_overlay_2);
         //定义Ground覆盖物选项
        OverlayOptions ooGround = new GroundOverlayOptions()
                .positionFromBounds(bounds)
                .image(bdGround)
                .transparency(0.8f);

        //在地图中添加Ground覆盖物
        mBaiduMap.addOverlay(ooGround);
    }


    /***************************************图片覆盖物↑↑↑↑↑↑↑***************************************/


    private void setPoint(BaiduMap mBaiduMap){
/*        //定义Maker坐标点
        LatLng point = new LatLng(39.804185, 116.186955);
        //构建Marker图标
        BitmapDescriptor bitmap = BitmapDescriptorFactory
                .fromResource(R.drawable.icon_geo);
        //构建MarkerOption，用于在地图上添加Marker
        OverlayOptions option = new MarkerOptions()
                .position(point)
                .icon(bitmap)
                .title("haha")
                .flat(true);
                 OverlayOptions textOption = new TextOptions()
                    .fontSize(24)
                    .fontColor(0xFFFF00FF)
                    .text("ha")
                    .position(point);
        //在地图上添加Marker，并显示
        mBaiduMap.addOverlay(option);*/

//        39.803326, 116.187119
        float x = 39.804185f;
        float y = 116.186955f;
        List<MakerPoint> mList = new ArrayList<>();
        for(int i = 1; i< 10; i++){
            mList.add(new MakerPoint(x, y, "title"+i));
            x += 1/5000.0f;
        }
        for(int i = 1; i< 10; i++) {
            mList.add(new MakerPoint(x, y, "title"+i));
            y += 1 / 5000.0f;
        }

        List<OverlayOptions> options = new ArrayList<>();
        for(MakerPoint point : mList){
            options.add(getOverlay(point));
        }
        mBaiduMap.addOverlays(options);
    }

    private OverlayOptions getOverlay(MakerPoint point){
        View view = View.inflate(this, R.layout.map_maker, null);
        ImageView iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
        TextView tv_name = (TextView) view.findViewById(R.id.tv_name);
        tv_name.setText(point.getTitle());
        // 构建BitmapDescriptor
        BitmapDescriptor bitmap = BitmapDescriptorFactory.fromView(view);
        //构建MarkerOption，用于在地图上添加Marker
        LatLng position = new LatLng(point.getX(), point.getY());
        OverlayOptions option = new MarkerOptions()
                .position(position)
                .animateType(MarkerOptions.MarkerAnimateType.grow)
                .zIndex(10)
                .period(10)
                .title("place")
                .icon(bitmap);
        return  option;
    }


    /************************************离线地图↓↓↓↓↓↓↓*********************************/

    private void checkOfflineMap(){

        //初始化离线地图
        mOffline = new MKOfflineMap();
        mOffline.init(this);// 传入接口事件，离线地图更新会触发该回调

        // 获取热门城市列表
//        ArrayList<MKOLSearchRecord> records1 = mOffline.getHotCityList();
        // 获取所有支持离线地图的城市
//        ArrayList<MKOLSearchRecord> records2 = mOffline.getOfflineCityList();
        //已下载的离线地图信息列表
        ArrayList<MKOLUpdateElement> localMapList = mOffline.getAllUpdateInfo();
        if (localMapList != null) {
            for (MKOLUpdateElement update : localMapList) {
                Log.e(getClass().getSimpleName(), "下载中："+
                        update.cityName + "(" + update.cityID + ")" +update.ratio+ "  "+ formatDataSize(update.size));
            }
        }

        MKOLUpdateElement update = mOffline.getUpdateInfo(bjCityid);
        if(null == update)
            tv_status.setText("离线地图尚未下载");
        if(null == update || MKOLUpdateElement.FINISHED != update.status){
            Log.i(getClass().getSimpleName(), "开始下载");
            mOffline.start(bjCityid);// 开始下载
        }else {
            Log.e(getClass().getSimpleName(), update.cityName+" 地图 下载完成");
//            tv_status.setText(update.cityName+" 地图 下载完成");
        }
        if(null!=update && update.update){
            tv_status.setText("离线地图有新版本");
            mOffline.update(bjCityid);  //更新
        }
    }

    public String formatDataSize(long size) {
        String ret = "";
        if (size < (1024 * 1024)) {
            ret = String.format("%dK", size / 1024);
        } else {
            ret = String.format("%.1fM", size / (1024 * 1024.0));
        }
        return ret;
    }

    @Override
    public void onGetOfflineMapState(int type, int state) {
        Log.w(getClass().getSimpleName(), "onGetOfflineMapState  "+type+  "   state"+state);
        switch (type) {
            case MKOfflineMap.TYPE_NETWORK_ERROR:
                tv_status.setText("网络错误");
                break;
            case MKOfflineMap.TYPE_DOWNLOAD_UPDATE:
                MKOLUpdateElement update = mOffline.getUpdateInfo(state);
                // 处理下载进度更新提示
                tv_status.setText(String.format("正在下载%s地图 : %d%%", update.cityName, update.ratio));
            break;
            case MKOfflineMap.TYPE_NEW_OFFLINE:
                // 有新离线地图安装
                Log.i(getClass().getSimpleName(), String.format("有新离线地图安装add offlinemap num:%d", state));
                break;
            case MKOfflineMap.TYPE_VER_UPDATE:
                // 版本更新提示
                // MKOLUpdateElement e = mOffline.getUpdateInfo(state);
                break;
            default:
                break;
        }
    }


    /************************************离线地图↑↑↑↑↑↑↑*********************************/

    /*********************************************************/

    // 设置个性化地图config文件路径
    private void setMapCustomFile(Context context, String PATH) {
        FileOutputStream out = null;
        InputStream inputStream = null;
        String moduleName = null;
        try {
            inputStream = context.getAssets()
                    .open("customConfigdir/" + PATH);
            byte[] b = new byte[inputStream.available()];
            inputStream.read(b);

            moduleName = context.getFilesDir().getAbsolutePath();
            File f = new File(moduleName + "/" + PATH);
            if (f.exists()) {
                f.delete();
            }
            f.createNewFile();
            out = new FileOutputStream(f);
            out.write(b);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        MapView.setCustomMapStylePath(moduleName + "/" + PATH);
    }



    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }
    @Override
    protected void onDestroy() {
        mOffline.pause(bjCityid); //暂停
        mOffline.destroy();       //退出时，销毁离线地图模块
        mOffline = null;
        mMapView.onDestroy();
        super.onDestroy();
    }

}
