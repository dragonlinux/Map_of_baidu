package baidumapsdk.demo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.MKMapTouchListener;
import com.baidu.mapapi.map.MKMapViewListener;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapView;
import com.baidu.platform.comapi.basestruct.GeoPoint;

/**
 * 演示地图缩放，旋转，视角控制
 */
public class MapControlDemo extends Activity {

	/**
	 *  MapView 是地图主控件
	 */
	private MapView mMapView = null;
	/**
	 *  用MapController完成地图控制 
	 */
	private MapController mMapController = null;
	/**
	 *  MKMapViewListener 用于处理地图事件回调
	 */
	MKMapViewListener mMapListener = null;
	/**
	 * 用于截获屏坐标
	 */
	MKMapTouchListener mapTouchListener = null; 
	/**
	 * 当前地点击点
	 */
	private GeoPoint currentPt = null; 
	/**
	 * 控制按钮
	 */
	private Button zoomButton = null;
	private Button rotateButton = null;
	private Button overlookButton =null;
	private Button saveScreenButton = null;
	/**
	 * 
	 */
	private String touchType = null;
	/**
	 * 用于显示地图状态的面板
	 */
	private TextView mStateBar = null;
	
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
        setContentView(R.layout.activity_mapcontrol);
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
        
        mStateBar = (TextView) findViewById(R.id.state);
        /**
         * 初始化地图事件监听
         */
        initListener();
       
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
    
    private void initListener() {
    	/**
         * 设置地图点击事件监听 
         */
        mapTouchListener = new MKMapTouchListener(){
			@Override
			public void onMapClick(GeoPoint point) {
				touchType = "单击";
				currentPt = point;
				updateMapState();
				
			}

			@Override
			public void onMapDoubleClick(GeoPoint point) {
				touchType = "双击";
				currentPt = point;
				updateMapState();
			}

			@Override
			public void onMapLongClick(GeoPoint point) {
				touchType = "长按";
				currentPt = point;
				updateMapState();
			}
        };
        mMapView.regMapTouchListner(mapTouchListener);
        /**
         * 设置地图事件监听
         */
        mMapListener = new MKMapViewListener() {
			@Override
			public void onMapMoveFinish() {
				/**
				 * 在此处理地图移动完成回调
				 * 缩放，平移等操作完成后，此回调被触发
				 */
				updateMapState();
			}
			
			@Override
			public void onClickMapPoi(MapPoi mapPoiInfo) {
				/**
				 * 在此处理底图poi点击事件
				 * 显示底图poi名称并移动至该点
				 * 设置过： mMapController.enableClick(true); 时，此回调才能被触发
				 * 
				 */
				
			}

			@Override
			public void onGetCurrentMap(Bitmap b) {
				/**
				 *  当调用过 mMapView.getCurrentMap()后，此回调会被触发
				 *  可在此保存截图至存储设备
				 */
				File file = new File("/mnt/sdcard/test.png");
                FileOutputStream out;
                try{
                    out = new FileOutputStream(file);
                    if(b.compress(Bitmap.CompressFormat.PNG, 70, out)) 
                    {
                        out.flush();
                        out.close();
                    }
                    Toast.makeText(MapControlDemo.this, 
                    	    "屏幕截图成功，图片存在: "+file.toString(),	
                    		 Toast.LENGTH_SHORT)
                         .show();
                } 
                catch (FileNotFoundException e) 
                {
                    e.printStackTrace();
                } 
                catch (IOException e) 
                {
                    e.printStackTrace(); 
                }
                
			}

			@Override
			public void onMapAnimationFinish() {
				/**
				 *  地图完成带动画的操作（如: animationTo()）后，此回调被触发
				 */
				updateMapState();
			}

			@Override
			public void onMapLoadFinish() {
				// TODO Auto-generated method stub
				
			}
		};
		mMapView.regMapViewListener(DemoApplication.getInstance().mBMapManager, mMapListener);
		/**
		 * 设置按键监听
		 */
		zoomButton        = (Button)findViewById(R.id.zoombutton);
		rotateButton      = (Button)findViewById(R.id.rotatebutton);
		overlookButton    = (Button)findViewById(R.id.overlookbutton);
		saveScreenButton  = (Button)findViewById(R.id.savescreen);
		OnClickListener onClickListener = new OnClickListener(){
			@Override
			public void onClick(View view) {
				if ( view.equals(zoomButton)){
					perfomZoom();
				}
				else if( view.equals(rotateButton)){
					perfomRotate();
				}
				else if( view.equals(overlookButton)){
					perfomOverlook();
				}
				else if ( view.equals(saveScreenButton)){
					//截图，在MKMapViewListener中保存图片
				     mMapView.getCurrentMap();	
				     Toast.makeText(MapControlDemo.this, 
				    		 "正在截取屏幕图片...", 
				    		 Toast.LENGTH_SHORT ).show();
				          
				}
				updateMapState();
			}
			
		};
		zoomButton.setOnClickListener(onClickListener);
		rotateButton.setOnClickListener(onClickListener);
		overlookButton.setOnClickListener(onClickListener);
		saveScreenButton.setOnClickListener(onClickListener);
    }
    /**
     * 处理缩放
     * sdk 缩放级别范围： [3.0,19.0]
     */
    private void perfomZoom(){
    	EditText  t  = (EditText) findViewById(R.id.zoomlevel);
    	try{
    	    float zoomLevel = Float.parseFloat(t.getText().toString());
    	    mMapController.setZoom(zoomLevel);
    	}catch(NumberFormatException e){
    		Toast.makeText(this, 
    	         "请输入正确的缩放级别", Toast.LENGTH_SHORT)
    		     .show();
    	}
    }
    /**
     * 处理旋转 
     * 旋转角范围： -180 ~ 180 , 单位：度   逆时针旋转  
     */
    private void perfomRotate(){
    	EditText  t  = (EditText) findViewById(R.id.rotateangle);
    	try{
    	    int rotateAngle = Integer.parseInt(t.getText().toString());
    	    mMapController.setRotation(rotateAngle);
    	}catch(NumberFormatException e){
    		Toast.makeText(this, 
    	         "请输入正确的旋转角度", Toast.LENGTH_SHORT)
    		     .show();
    	}
    }
    /**
     * 处理俯视
     * 俯角范围：  -45 ~ 0 , 单位： 度
     */
    private void perfomOverlook(){
    	EditText  t  = (EditText) findViewById(R.id.overlookangle);
    	try{
    	    int overlookAngle = Integer.parseInt(t.getText().toString());
    	    mMapController.setOverlooking(overlookAngle);
    	}catch(NumberFormatException e){
    		Toast.makeText(this, 
    	         "请输入正确的俯角", Toast.LENGTH_SHORT)
    		     .show();
    	}	
    }
    
    /**
     * 更新地图状态显示面板
     */
    private void updateMapState(){
    	   if ( mStateBar == null){
    		   return ;
    	   }
    		String state  = "";
    		if ( currentPt == null ){
    			state = "点击、长按、双击地图以获取经纬度和地图状态";
    		}
    		else{
    			state = String.format(touchType+",当前经度 ： %f 当前纬度：%f",currentPt.getLongitudeE6()*1E-6,currentPt.getLatitudeE6()*1E-6);
    		}
    		state += "\n";
    		state += String.format("zoom level= %.1f    rotate angle= %d   overlaylook angle=  %d",
                mMapView.getZoomLevel(), 
                mMapView.getMapRotation(),
                mMapView.getMapOverlooking() 
    	    );
    		mStateBar.setText(state);
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
