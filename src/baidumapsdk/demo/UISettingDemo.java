package baidumapsdk.demo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RadioButton;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.platform.comapi.basestruct.GeoPoint;

/**
 * 演示地图UI控制功能
 */
public class UISettingDemo extends Activity {

	/**
	 *  MapView 是地图主控件
	 */
	private MapView mMapView = null;
	/**
	 *  用MapController完成地图控制 
	 */
	private MapController mMapController = null;
	
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
        setContentView(R.layout.activity_uisetting);
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
        mMapController.setZoom(12);
        /**
         * 设置地图俯角
         */
        mMapController.setOverlooking(-30);
        /**
         * 将地图移动至天安门
         * 使用百度经纬度坐标，可以通过http://api.map.baidu.com/lbsapi/getpoint/index.html查询地理坐标
         * 如果需要在百度地图上显示使用其他坐标系统的位置，请发邮件至mapapi@baidu.com申请坐标转换接口
         */
        double cLat = 39.945 ;
        double cLon = 116.404 ;
        GeoPoint p = new GeoPoint((int)(cLat * 1E6), (int)(cLon * 1E6));
        mMapController.setCenter(p);
        
    }
    
    /**
     * 是否启用缩放手势
     * @param v
     */
    public void setZoomEnable(View v){
    	mMapController.setZoomGesturesEnabled(((CheckBox) v).isChecked());
    }
    /**
     * 是否启用平移手势
     * @param v
     */
    public void setScrollEnable(View v){
    	mMapController.setScrollGesturesEnabled(((CheckBox) v).isChecked());
    }
    /**
     * 是否启用双击放大
     * @param v
     */
    public void setDoubleClickEnable(View v){
    	mMapView.setDoubleClickZooming(((CheckBox) v).isChecked());
    }
    /**
     * 是否启用旋转手势
     * @param v
     */
    public void setRotateEnable(View v){
        mMapController.setRotationGesturesEnabled(((CheckBox) v).isChecked());    	
    }
    /**
     * 是否启用俯视手势
     * @param v
     */
    public void setOverlookEnable(View v){
        mMapController.setOverlookingGesturesEnabled(((CheckBox) v).isChecked());    	
    }
    /**
     * 是否显示内置绽放控件
     * @param v
     */
    public void setBuiltInZoomControllEnable(View v){
        mMapView.setBuiltInZoomControls(((CheckBox) v).isChecked());    	
    }
    
    /**
     * 设置指南针位置,指南针在3D模式下自动显现
     * @param view
     */
    public void setCompassLocation(View view){
        boolean checked = ((RadioButton) view).isChecked();
        switch(view.getId()) {
            case R.id.lefttop:
                if (checked)
                	//设置指南针显示在左上角
                	mMapController.setCompassMargin(100, 100);
                break;
            case R.id.righttop:
                if (checked)
                	mMapController.setCompassMargin(mMapView.getWidth() - 100, 100);
                break;
        }	
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
    
}
