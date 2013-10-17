package baidumapsdk.demo;

import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.RouteOverlay;
import com.baidu.mapapi.search.MKRoute;
import com.baidu.platform.comapi.basestruct.GeoPoint;

import android.app.Activity;
import android.os.Bundle;
/**
 * 此demo用来展示如何用自己的数据构造一条路线在地图上绘制出来
 *
 */
public class CustomRouteOverlayDemo  extends Activity{

	//地图相关
	MapView mMapView = null;	// 地图View
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_customroute);
		CharSequence titleLable="路线规划功能——自设路线示例";
        setTitle(titleLable);
		//初始化地图
        mMapView = (MapView)findViewById(R.id.bmapView);
        mMapView.getController().enableClick(true);
        mMapView.getController().setZoom(13);
        
        /** 演示自定义路线使用方法	
		 *  在北京地图上画一个北斗七星
		 *  想知道某个点的百度经纬度坐标请点击：http://api.map.baidu.com/lbsapi/getpoint/index.html	
		 */
		GeoPoint p1 = new GeoPoint((int)(39.9411 * 1E6),(int)(116.3714 * 1E6));
		GeoPoint p2 = new GeoPoint((int)(39.9498 * 1E6),(int)(116.3785 * 1E6));
		GeoPoint p3 = new GeoPoint((int)(39.9436 * 1E6),(int)(116.4029 * 1E6));
		GeoPoint p4 = new GeoPoint((int)(39.9329 * 1E6),(int)(116.4035 * 1E6));
		GeoPoint p5 = new GeoPoint((int)(39.9218 * 1E6),(int)(116.4115 * 1E6));
		GeoPoint p6 = new GeoPoint((int)(39.9144 * 1E6),(int)(116.4230 * 1E6));
		GeoPoint p7 = new GeoPoint((int)(39.9126 * 1E6),(int)(116.4387 * 1E6));
	    //起点坐标
		GeoPoint start = p1;
		//终点坐标
		GeoPoint stop  = p7;
		//第一站，站点坐标为p3,经过p1,p2
		GeoPoint[] step1 = new GeoPoint[3];
		step1[0] = p1;
		step1[1] = p2 ;
		step1[2] = p3;
		//第二站，站点坐标为p5,经过p4
		GeoPoint[] step2 = new GeoPoint[2];
		step2[0] = p4;
		step2[1] = p5;
		//第三站，站点坐标为p7,经过p6
		GeoPoint[] step3 = new GeoPoint[2];
		step3[0] = p6;
		step3[1] = p7;
		//站点数据保存在一个二维数据中
		GeoPoint [][] routeData = new GeoPoint[3][];
		routeData[0] = step1;
		routeData[1] = step2;
		routeData[2] = step3;
		//用站点数据构建一个MKRoute
		MKRoute route = new MKRoute();
		route.customizeRoute(start, stop, routeData);	
		//将包含站点信息的MKRoute添加到RouteOverlay中
		RouteOverlay routeOverlay = new RouteOverlay(CustomRouteOverlayDemo.this, mMapView);		
		routeOverlay.setData(route);
		//向地图添加构造好的RouteOverlay
		mMapView.getOverlays().add(routeOverlay);
		//执行刷新使生效
	    mMapView.refresh();
		
	}
	@Override
    protected void onPause() {
        mMapView.onPause();
        super.onPause();
    }
    @Override
    protected void onResume() {
        mMapView.onResume();
        super.onResume();
    }
    @Override
    protected void onDestroy() {
        mMapView.destroy();
        super.onDestroy();
    }
	
    @Override
    protected void onSaveInstanceState(Bundle outState) {
    	super.onSaveInstanceState(outState);
    	mMapView.onSaveInstanceState(outState);
    	
    }
    
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
    	super.onRestoreInstanceState(savedInstanceState);
    	mMapView.onRestoreInstanceState(savedInstanceState);
    }
}
