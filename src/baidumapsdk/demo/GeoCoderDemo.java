package baidumapsdk.demo;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.mapapi.search.MKAddrInfo;
import com.baidu.mapapi.search.MKBusLineResult;
import com.baidu.mapapi.search.MKDrivingRouteResult;
import com.baidu.mapapi.search.MKPoiResult;
import com.baidu.mapapi.search.MKSearch;
import com.baidu.mapapi.search.MKSearchListener;
import com.baidu.mapapi.search.MKShareUrlResult;
import com.baidu.mapapi.search.MKSuggestionResult;
import com.baidu.mapapi.search.MKTransitRouteResult;
import com.baidu.mapapi.search.MKWalkingRouteResult;
import com.baidu.platform.comapi.basestruct.GeoPoint;
/**
 * 此demo用来展示如何进行地理编码搜索（用地址检索坐标）、反地理编码搜索（用坐标检索地址）
 * 同时展示了如何使用ItemizedOverlay在地图上标注结果点
 *
 */
public class GeoCoderDemo extends Activity {
	//UI相关
	Button mBtnReverseGeoCode = null;	// 将坐标反编码为地址
	Button mBtnGeoCode = null;	// 将地址编码为坐标
	
	//地图相关
	MapView mMapView = null;	// 地图View
	//搜索相关
	MKSearch mSearch = null;	// 搜索模块，也可去掉地图模块独立使用
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        DemoApplication app = (DemoApplication)this.getApplication();
		setContentView(R.layout.geocoder);
		CharSequence titleLable="地理编码功能";
        setTitle(titleLable);
        
        //地图初始化
        mMapView = (MapView)findViewById(R.id.bmapView);
        mMapView.getController().enableClick(true);
        mMapView.getController().setZoom(12);
        
        // 初始化搜索模块，注册事件监听
        mSearch = new MKSearch();
        mSearch.init(app.mBMapManager, new MKSearchListener() {
            @Override
            public void onGetPoiDetailSearchResult(int type, int error) {
            }
            
			public void onGetAddrResult(MKAddrInfo res, int error) {
				if (error != 0) {
					String str = String.format("错误号：%d", error);
					Toast.makeText(GeoCoderDemo.this, str, Toast.LENGTH_LONG).show();
					return;
				}
				//地图移动到该点
				mMapView.getController().animateTo(res.geoPt);	
				if (res.type == MKAddrInfo.MK_GEOCODE){
					//地理编码：通过地址检索坐标点
					String strInfo = String.format("纬度：%f 经度：%f", res.geoPt.getLatitudeE6()/1e6, res.geoPt.getLongitudeE6()/1e6);
					Toast.makeText(GeoCoderDemo.this, strInfo, Toast.LENGTH_LONG).show();
				}
				if (res.type == MKAddrInfo.MK_REVERSEGEOCODE){
					//反地理编码：通过坐标点检索详细地址及周边poi
					String strInfo = res.strAddr;
					Toast.makeText(GeoCoderDemo.this, strInfo, Toast.LENGTH_LONG).show();
					
				}
				//生成ItemizedOverlay图层用来标注结果点
				ItemizedOverlay<OverlayItem> itemOverlay = new ItemizedOverlay<OverlayItem>(null, mMapView);
				//生成Item
				OverlayItem item = new OverlayItem(res.geoPt, "", null);
				//得到需要标在地图上的资源
				Drawable marker = getResources().getDrawable(R.drawable.icon_markf);  
				//为maker定义位置和边界
				marker.setBounds(0, 0, marker.getIntrinsicWidth(), marker.getIntrinsicHeight());
				//给item设置marker
				item.setMarker(marker);
				//在图层上添加item
				itemOverlay.addItem(item);
				
				//清除地图其他图层
				mMapView.getOverlays().clear();
				//添加一个标注ItemizedOverlay图层
				mMapView.getOverlays().add(itemOverlay);
				//执行刷新使生效
				mMapView.refresh();
			}
			public void onGetPoiResult(MKPoiResult res, int type, int error) {
				
			}
			public void onGetDrivingRouteResult(MKDrivingRouteResult res, int error) {
			}
			public void onGetTransitRouteResult(MKTransitRouteResult res, int error) {
			}
			public void onGetWalkingRouteResult(MKWalkingRouteResult res, int error) {
			}
			public void onGetBusDetailResult(MKBusLineResult result, int iError) {
			}
			@Override
			public void onGetSuggestionResult(MKSuggestionResult res, int arg1) {
			}

			@Override
			public void onGetShareUrlResult(MKShareUrlResult result, int type,
					int error) {
				// TODO Auto-generated method stub
				
			}

        });
        
        // 设定地理编码及反地理编码按钮的响应
        mBtnReverseGeoCode = (Button)findViewById(R.id.reversegeocode);
        mBtnGeoCode = (Button)findViewById(R.id.geocode);
        
        OnClickListener clickListener = new OnClickListener(){
			public void onClick(View v) {
					SearchButtonProcess(v);
			}
        };
        
        mBtnReverseGeoCode.setOnClickListener(clickListener); 
        mBtnGeoCode.setOnClickListener(clickListener); 
	}

	/**
	 * 发起搜索
	 * @param v
	 */
	void SearchButtonProcess(View v) {
		if (mBtnReverseGeoCode.equals(v)) {
			EditText lat = (EditText)findViewById(R.id.lat);
			EditText lon = (EditText)findViewById(R.id.lon);
			GeoPoint ptCenter = new GeoPoint((int)(Float.valueOf(lat.getText().toString())*1e6), (int)(Float.valueOf(lon.getText().toString())*1e6));
			//反Geo搜索
			mSearch.reverseGeocode(ptCenter);
		} else if (mBtnGeoCode.equals(v)) {
			EditText editCity = (EditText)findViewById(R.id.city);
			EditText editGeoCodeKey = (EditText)findViewById(R.id.geocodekey);
			//Geo搜索
			mSearch.geocode(editGeoCodeKey.getText().toString(), editCity.getText().toString());
		}
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
