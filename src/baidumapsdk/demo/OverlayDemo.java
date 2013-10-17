package baidumapsdk.demo;

import java.util.ArrayList;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.Ground;
import com.baidu.mapapi.map.GroundOverlay;
import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.mapapi.map.PopupClickListener;
import com.baidu.mapapi.map.PopupOverlay;
import com.baidu.platform.comapi.basestruct.GeoPoint;

/**
 * 演示覆盖物的用法
 */
public class OverlayDemo extends Activity {

	/**
	 *  MapView 是地图主控件
	 */
	private MapView mMapView = null;
	/**
	 *  用MapController完成地图控制 
	 */
	private MapController mMapController = null;
	private MyOverlay mOverlay = null;
	private PopupOverlay   pop  = null;
	private ArrayList<OverlayItem>  mItems = null; 
	private TextView  popupText = null;
	private View viewCache = null;
	private View popupInfo = null;
	private View popupLeft = null;
	private View popupRight = null;
	private Button button = null;
	private MapView.LayoutParams layoutParam = null;
	private OverlayItem mCurItem = null;
	/**
	 * overlay 位置坐标
	 */
	double mLon1 = 116.400244 ;
	double mLat1 = 39.963175 ;
	double mLon2 = 116.369199;
	double mLat2 = 39.942821;
	double mLon3 = 116.425541;
	double mLat3 = 39.939723;
	double mLon4 = 116.401394;
	double mLat4 = 39.906965;

	// ground overlay
	private GroundOverlay mGroundOverlay;
	private Ground mGround;
	private double mLon5 = 116.380338;
	private double mLat5 = 39.92235;
	private double mLon6 = 116.414977;
	private double mLat6 = 39.947246;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /**
         * 使用地图sdk前需先初始化BMapManager.
         * BMapManager是全局的，可为多个MapView共用，它需要地图模块创建前创建，
         * 并在地图地图模块销毁后销毁，只要还有地图模块在使用，BMapManager就不应该销毁
         */
        DemoApplication app = (DemoApplication)this.getApplication();
        if (app.mBMapManager == null) {
            app.mBMapManager = new BMapManager(this);
            /**
             * 如果BMapManager没有初始化则初始化BMapManager
             */
            app.mBMapManager.init(DemoApplication.strKey,new DemoApplication.MyGeneralListener());
        }
        /**
          * 由于MapView在setContentView()中初始化,所以它需要在BMapManager初始化之后
          */
        setContentView(R.layout.activity_overlay);
        mMapView = (MapView)findViewById(R.id.bmapView);
        /**
         * 获取地图控制器
         */
        mMapController = mMapView.getController();
        /**
         *  设置地图是否响应点击事件  .
         */
        mMapController.enableClick(true);
        /**
         * 设置地图缩放级别
         */
        mMapController.setZoom(14);
        /**
         * 显示内置缩放控件
         */
        mMapView.setBuiltInZoomControls(true);
        
        initOverlay();

        /**
         * 设定地图中心点
         */
        GeoPoint p = new GeoPoint((int)(39.933859 * 1E6), (int)(116.400191* 1E6));
        mMapController.setCenter(p);
    }
  
    public void initOverlay(){
    	/**
    	 * 创建自定义overlay
    	 */
         mOverlay = new MyOverlay(getResources().getDrawable(R.drawable.icon_marka),mMapView);	
         /**
          * 准备overlay 数据
          */
         GeoPoint p1 = new GeoPoint ((int)(mLat1*1E6),(int)(mLon1*1E6));
         OverlayItem item1 = new OverlayItem(p1,"覆盖物1","");
         /**
          * 设置overlay图标，如不设置，则使用创建ItemizedOverlay时的默认图标.
          */
         item1.setMarker(getResources().getDrawable(R.drawable.icon_marka));
         
         GeoPoint p2 = new GeoPoint ((int)(mLat2*1E6),(int)(mLon2*1E6));
         OverlayItem item2 = new OverlayItem(p2,"覆盖物2","");
         item2.setMarker(getResources().getDrawable(R.drawable.icon_markb));
         
         GeoPoint p3 = new GeoPoint ((int)(mLat3*1E6),(int)(mLon3*1E6));
         OverlayItem item3 = new OverlayItem(p3,"覆盖物3","");
         item3.setMarker(getResources().getDrawable(R.drawable.icon_markc));

         GeoPoint p4 = new GeoPoint ((int)(mLat4*1E6),(int)(mLon4*1E6));
         OverlayItem item4 = new OverlayItem(p4,"覆盖物4","");
         item4.setMarker(getResources().getDrawable(R.drawable.icon_gcoding));
         /**
          * 将item 添加到overlay中
          * 注意： 同一个itme只能add一次
          */
         mOverlay.addItem(item1);
         mOverlay.addItem(item2);
         mOverlay.addItem(item3);
         mOverlay.addItem(item4);
         /**
          * 保存所有item，以便overlay在reset后重新添加
          */
         mItems = new ArrayList<OverlayItem>();
         mItems.addAll(mOverlay.getAllItem());

		// 初始化 ground 图层
		mGroundOverlay = new GroundOverlay(mMapView);
		GeoPoint leftBottom = new GeoPoint((int) (mLat5 * 1E6),
				(int) (mLon5 * 1E6));
		GeoPoint rightTop = new GeoPoint((int) (mLat6 * 1E6),
				(int) (mLon6 * 1E6));
		Drawable d = getResources().getDrawable(R.drawable.ground_overlay);
		Bitmap bitmap = ((BitmapDrawable) d).getBitmap();
		mGround = new Ground(bitmap, leftBottom, rightTop);

         /**
          * 将overlay 添加至MapView中
          */
         mMapView.getOverlays().add(mOverlay);
         mMapView.getOverlays().add(mGroundOverlay);
         mGroundOverlay.addGround(mGround);
         /**
          * 刷新地图
          */
         mMapView.refresh();
         
         /**
          * 向地图添加自定义View.
          */
        
         
         viewCache = getLayoutInflater().inflate(R.layout.custom_text_view, null);
         popupInfo = (View) viewCache.findViewById(R.id.popinfo);
         popupLeft = (View) viewCache.findViewById(R.id.popleft);
         popupRight = (View) viewCache.findViewById(R.id.popright);
         popupText =(TextView) viewCache.findViewById(R.id.textcache);
         
         button = new Button(this);
         button.setBackgroundResource(R.drawable.popup);
         
         /**
          * 创建一个popupoverlay
          */
         PopupClickListener popListener = new PopupClickListener(){
			@Override
			public void onClickedPopup(int index) {
				if ( index == 0){
					//更新item位置
				      pop.hidePop();
				      GeoPoint p = new GeoPoint(mCurItem.getPoint().getLatitudeE6()+5000,
				    		  mCurItem.getPoint().getLongitudeE6()+5000);
				      mCurItem.setGeoPoint(p);
				      mOverlay.updateItem(mCurItem);
				      mMapView.refresh();
				}
				else if(index == 2){
					//更新图标
					mCurItem.setMarker(getResources().getDrawable(R.drawable.nav_turn_via_1));
					mOverlay.updateItem(mCurItem);
				    mMapView.refresh();
				}
			}
         };
         pop = new PopupOverlay(mMapView,popListener);
         
         
    }
    /**
     * 清除所有Overlay
     * @param view
     */
    public void clearOverlay(View view){
    	mOverlay.removeAll();
    	mGroundOverlay.removeGround(mGround);
    	if (pop != null){
            pop.hidePop();
    	}
    	mMapView.removeView(button);
    	mMapView.refresh();
    }
    /**
     * 重新添加Overlay
     * @param view
     */
    public void resetOverlay(View view){
    	clearOverlay(null);
    	//重新add overlay
    	mOverlay.addItem(mItems);
    	mGroundOverlay.addGround(mGround);
    	mMapView.refresh();
    }
   
    @Override
    protected void onPause() {
    	/**
    	 *  MapView的生命周期与Activity同步，当activity挂起时需调用MapView.onPause()
    	 */
        mMapView.onPause();
        super.onPause();
    }
    
    @Override
    protected void onResume() {
    	/**
    	 *  MapView的生命周期与Activity同步，当activity恢复时需调用MapView.onResume()
    	 */
        mMapView.onResume();
        super.onResume();
    }
    
    @Override
    protected void onDestroy() {
    	/**
    	 *  MapView的生命周期与Activity同步，当activity销毁时需调用MapView.destroy()
    	 */
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
    
    public class MyOverlay extends ItemizedOverlay{

		public MyOverlay(Drawable defaultMarker, MapView mapView) {
			super(defaultMarker, mapView);
		}
		

		@Override
		public boolean onTap(int index){
			OverlayItem item = getItem(index);
			mCurItem = item ;
			if (index == 3){
		         button.setText("这是一个系统控件");
		         GeoPoint pt = new GeoPoint ((int)(mLat4*1E6),(int)(mLon4*1E6));
		         //创建布局参数
		         layoutParam  = new MapView.LayoutParams(
		               //控件宽,继承自ViewGroup.LayoutParams
		               MapView.LayoutParams.WRAP_CONTENT,
		                //控件高,继承自ViewGroup.LayoutParams
		               MapView.LayoutParams.WRAP_CONTENT,
		               //使控件固定在某个地理位置
		                pt,
		                0,
		                -32,
		               //控件对齐方式
		                 MapView.LayoutParams.BOTTOM_CENTER);
		         //添加View到MapView中
		         mMapView.addView(button,layoutParam);
			}
			else{
			   popupText.setText(getItem(index).getTitle());
			   Bitmap[] bitMaps={
				    BMapUtil.getBitmapFromView(popupLeft), 		
				    BMapUtil.getBitmapFromView(popupInfo), 		
				    BMapUtil.getBitmapFromView(popupRight) 		
			    };
			    pop.showPopup(bitMaps,item.getPoint(),32);
			}
			return true;
		}
		
		@Override
		public boolean onTap(GeoPoint pt , MapView mMapView){
			if (pop != null){
                pop.hidePop();
                mMapView.removeView(button);
			}
			return false;
		}
    	
    }
    
}
