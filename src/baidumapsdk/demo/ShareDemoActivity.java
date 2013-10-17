package baidumapsdk.demo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.mapapi.map.PoiOverlay;
import com.baidu.mapapi.search.MKAddrInfo;
import com.baidu.mapapi.search.MKBusLineResult;
import com.baidu.mapapi.search.MKDrivingRouteResult;
import com.baidu.mapapi.search.MKPoiInfo;
import com.baidu.mapapi.search.MKPoiResult;
import com.baidu.mapapi.search.MKSearch;
import com.baidu.mapapi.search.MKSearchListener;
import com.baidu.mapapi.search.MKShareUrlResult;
import com.baidu.mapapi.search.MKSuggestionResult;
import com.baidu.mapapi.search.MKTransitRouteResult;
import com.baidu.mapapi.search.MKWalkingRouteResult;
import com.baidu.platform.comapi.basestruct.GeoPoint;

/**
 * 演示poi搜索功能 
 */
public class ShareDemoActivity extends Activity {
	
	private MapView mMapView = null;
	private MKSearch mSearch = null;   // 搜索模块，也可去掉地图模块独立使用
	//保存搜索结果地址
	private String currentAddr = null;
	//搜索城市 
	private String mCity = "北京";
	//搜索关键字
	private String searchKey = "餐馆";
	//反地理编译点坐标
	private GeoPoint mPoint = new GeoPoint((int)(40.056878*1E6),(int)(116.308141*1E6));
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	 super.onCreate(savedInstanceState);
         DemoApplication app = (DemoApplication)this.getApplication();
         if (app.mBMapManager == null) {
             app.mBMapManager = new BMapManager(this);
             app.mBMapManager.init(DemoApplication.strKey,new DemoApplication.MyGeneralListener());
         }
        setContentView(R.layout.activity_share_demo_activity);
        mMapView = (MapView)findViewById(R.id.bmapView);
		mMapView.getController().enableClick(true);
        mMapView.getController().setZoom(12);
		
		// 初始化搜索模块，注册搜索事件监听
        mSearch = new MKSearch();
        mSearch.init(app.mBMapManager, new MKSearchListener(){
        	
            @Override
            public void onGetPoiDetailSearchResult(int type, int error) {
            }
            /**
             * 在此处理poi搜索结果 , 用poioverlay 显示
             */
            public void onGetPoiResult(MKPoiResult res, int type, int error) {
                // 错误号可参考MKEvent中的定义
                if (error != 0 || res == null) {
                    Toast.makeText(ShareDemoActivity.this, "抱歉，未找到结果", Toast.LENGTH_LONG).show();
                    return;
                }
                // 将地图移动到第一个POI中心点
                if (res.getCurrentNumPois() > 0) {
                    // 将poi结果显示到地图上
                    PoiShareOverlay poiOverlay = new PoiShareOverlay(ShareDemoActivity.this, mMapView);
                    poiOverlay.setData(res.getAllPoi());
                    mMapView.getOverlays().clear();
                    mMapView.getOverlays().add(poiOverlay);
                    mMapView.refresh();
                    //当ePoiType为2（公交线路）或4（地铁线路）时， poi坐标为空
                    for( MKPoiInfo info : res.getAllPoi() ){
                    	if ( info.pt != null ){
                    		mMapView.getController().animateTo(info.pt);
                    		break;
                    	}
                    }
                } 
            }
            public void onGetDrivingRouteResult(MKDrivingRouteResult res,
                    int error) {
            }
            public void onGetTransitRouteResult(MKTransitRouteResult res,
                    int error) {
            }
            public void onGetWalkingRouteResult(MKWalkingRouteResult res,
                    int error) {
            }
            /**
             * 在此处理反地理编结果
             */
            public void onGetAddrResult(MKAddrInfo res, int error) {
            	// 错误号可参考MKEvent中的定义
                if (error != 0 || res == null) {
                    Toast.makeText(ShareDemoActivity.this, "抱歉，未找到结果", Toast.LENGTH_LONG).show();
                    return;
                }
                AddrShareOverlay addrOverlay = new AddrShareOverlay(
                		getResources().getDrawable(R.drawable.icon_marka),mMapView , res);
                mMapView.getOverlays().clear();
                mMapView.getOverlays().add(addrOverlay);
                mMapView.refresh();
                
            }
            public void onGetBusDetailResult(MKBusLineResult result, int iError) {
            }
            
            @Override
            public void onGetSuggestionResult(MKSuggestionResult res, int arg1) {
            }
			@Override
			public void onGetShareUrlResult(MKShareUrlResult result, int type,
					int error) {
				//分享短串结果
				Intent it = new Intent(Intent.ACTION_SEND);  
				it.putExtra(Intent.EXTRA_TEXT, "您的朋友通过百度地图SDK与您分享一个位置: "+
						       currentAddr+
						       " -- "+result.url);  
				it.setType("text/plain");  
				startActivity(Intent.createChooser(it, "将短串分享到"));
				
			}
        });
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
    protected void onDestroy(){
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
    
    private void initMapView() {
        mMapView.setLongClickable(true);
        mMapView.getController().setZoom(14);
        mMapView.getController().enableClick(true);
        mMapView.setBuiltInZoomControls(true);
    }
   
	public void sharePoi(View view){
		//发起poi搜索
    	mSearch.poiSearchInCity(mCity, searchKey);
    	Toast.makeText(this,
    			"在"+mCity+"搜索 "+searchKey,
    			Toast.LENGTH_SHORT).show();
    }
    
    public void shareAddr(View view){
    	//发起反地理编码请求
    	mSearch.reverseGeocode(mPoint);
    	Toast.makeText(this,
    			String.format("搜索位置： %f，%f", (mPoint.getLatitudeE6()*1E-6),(mPoint.getLongitudeE6()*1E-6)),
    			Toast.LENGTH_SHORT).show();
    }
    
    /**
     * 使用PoiOverlay 展示poi点，在poi被点击时发起短串请求.
     * @author kehongfeng
     *
     */
    private class PoiShareOverlay extends PoiOverlay {

        public PoiShareOverlay(Activity activity, MapView mapView) {
            super(activity, mapView);
        }

        @Override
        protected boolean onTap(int i) {
            MKPoiInfo info = getPoi(i);
            currentAddr = info.address;  	
            mSearch.poiDetailShareURLSearch(info.uid);
            return true;
        }        
    }
    /**
     * 使用ItemizevOvelray展示反地理编码点位置，当该点被点击时发起短串请求.
     *
     */
  private class AddrShareOverlay extends ItemizedOverlay {

	  private MKAddrInfo addrInfo ;
	  public AddrShareOverlay(Drawable defaultMarker, MapView mapView , MKAddrInfo addrInfo) {
		super(defaultMarker, mapView);
		this.addrInfo = addrInfo;
		addItem(new OverlayItem(addrInfo.geoPt,addrInfo.strAddr,addrInfo.strAddr));
	}
	  
	@Override
	public boolean onTap(int index){
		currentAddr = addrInfo.strAddr;
	    mSearch.poiRGCShareURLSearch(addrInfo.geoPt, "分享地址", addrInfo.strAddr);	
	    return true;
	}
	  
  }
}
