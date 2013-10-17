package baidumapsdk.demo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.baidu.mapapi.navi.BaiduMapAppNotSupportNaviException;
import com.baidu.mapapi.navi.BaiduMapNavigation;
import com.baidu.mapapi.navi.NaviPara;
import com.baidu.platform.comapi.basestruct.GeoPoint;

public class NaviDemo extends Activity {
	
	//天安门坐标
	double mLat1 = 39.915291; 
   	double mLon1 = 116.403857; 
   	//百度大厦坐标
   	double mLat2 = 40.056858;   
   	double mLon2 = 116.308194;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_navi_demo);
		TextView text = (TextView)findViewById(R.id.navi_info);
		text.setText(String.format("起点:(%f,%f)\n终点:(%f,%f)",mLat1,mLon1,mLat2,mLon2));
	}
   /**
    * 开始导航		
    * @param view
    */
   public void startNavi(View view){		
		int lat = (int) (mLat1 *1E6);
	   	int lon = (int) (mLon1 *1E6);   	
	   	GeoPoint pt1 = new GeoPoint(lat, lon);
		lat = (int) (mLat2 *1E6);
	   	lon = (int) (mLon2 *1E6);
	    GeoPoint pt2 = new GeoPoint(lat, lon);
	    // 构建 导航参数
        NaviPara para = new NaviPara();
        para.startPoint = pt1;
        para.startName= "从这里开始";
        para.endPoint  = pt2;
        para.endName   = "到这里结束";
        
        try {
        	
			 BaiduMapNavigation.openBaiduMapNavi(para, this);
			 
		} catch (BaiduMapAppNotSupportNaviException e) {
			e.printStackTrace();
			  AlertDialog.Builder builder = new AlertDialog.Builder(this);
			  builder.setMessage("您尚未安装百度地图app或app版本过低，点击确认安装？");
			  builder.setTitle("提示");
			  builder.setPositiveButton("确认", new OnClickListener() {
			   @Override
			   public void onClick(DialogInterface dialog, int which) {
				 dialog.dismiss();
				 BaiduMapNavigation.GetLatestBaiduMapApp(NaviDemo.this);
			   }
			  });

			  builder.setNegativeButton("取消", new OnClickListener() {
			   @Override
			   public void onClick(DialogInterface dialog, int which) {
			    dialog.dismiss();
			   }
			  });

			  builder.create().show();
			 }
		}
}
