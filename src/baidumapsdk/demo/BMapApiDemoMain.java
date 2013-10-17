package baidumapsdk.demo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.baidu.mapapi.VersionInfo;

public class BMapApiDemoMain extends Activity {
	
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

		ListView mListView = (ListView)findViewById(R.id.listView); 
		// 添加ListItem，设置事件响应
        mListView.setAdapter(new DemoListAdapter());
        mListView.setOnItemClickListener(new OnItemClickListener() {  
            public void onItemClick(AdapterView<?> arg0, View v, int index, long arg3) {  
            	onListItemClick(index);
            }  
        });  
    }

    void onListItemClick(int index) {
		Intent intent = null;
		intent = new Intent(BMapApiDemoMain.this, demos[index].demoClass);
		this.startActivity(intent);
    }
	
	private static final DemoInfo[] demos = {
		new DemoInfo(R.string.demo_title_basemap, R.string.demo_desc_basemap,
				     BaseMapDemo.class),
        new DemoInfo(R.string.demo_title_layers, R.string.demo_desc_layers,
				     LayersDemo.class),
        new DemoInfo(R.string.demo_title_control, R.string.demo_desc_control,
				     MapControlDemo.class),
        new DemoInfo(R.string.demo_title_ui, R.string.demo_desc_ui,
				     UISettingDemo.class),
        new DemoInfo(R.string.demo_title_location, R.string.demo_desc_location,
				     LocationOverlayDemo.class),
        new DemoInfo(R.string.demo_title_geometry, R.string.demo_desc_geometry,
				     GeometryDemo.class),
        new DemoInfo(R.string.demo_title_overlay, R.string.demo_desc_overlay,
				     OverlayDemo.class),
        new DemoInfo(R.string.demo_title_poi, R.string.demo_desc_poi,
				     PoiSearchDemo.class),
        new DemoInfo(R.string.demo_title_geocode, R.string.demo_desc_geocode,
				     GeoCoderDemo.class),
        new DemoInfo(R.string.demo_title_route, R.string.demo_desc_route,
				     RoutePlanDemo.class),
        new DemoInfo(R.string.demo_title_bus, R.string.demo_desc_bus,
				     BusLineSearchDemo.class),
        new DemoInfo(R.string.demo_title_offline, R.string.demo_desc_offline,
				     OfflineDemo.class),
        new DemoInfo(R.string.demo_title_navi, R.string.demo_desc_navi,
				     NaviDemo.class),
        new DemoInfo(R.string.demo_title_cloud, R.string.demo_desc_cloud,
				     CloudSearchDemo.class),
	    new DemoInfo(R.string.demo_title_share, R.string.demo_desc_share,
						     ShareDemo.class)
	};
	
	@Override
	protected void onResume() {
	    DemoApplication app = (DemoApplication)this.getApplication();
	    TextView text = (TextView)findViewById(R.id.text_Info);
		if (!app.m_bKeyRight) {
            text.setText(R.string.key_error);
            text.setTextColor(Color.RED);
		}
		else{
			text.setTextColor(Color.YELLOW);
			text.setText("欢迎使用百度地图Android SDK v"+VersionInfo.getApiVersion());
		}
		super.onResume();
	}

	@Override
	// 建议在APP整体退出之前调用MapApi的destroy()函数，不要在每个activity的OnDestroy中调用，
    // 避免MapApi重复创建初始化，提高效率
	protected void onDestroy() {
	    DemoApplication app = (DemoApplication)this.getApplication();
		if (app.mBMapManager != null) {
			app.mBMapManager.destroy();
			app.mBMapManager = null;
		}
		super.onDestroy();
		System.exit(0);
	}
	
	private  class DemoListAdapter extends BaseAdapter {
		public DemoListAdapter() {
			super();
		}

		@Override
		public View getView(int index, View convertView, ViewGroup parent) {
			convertView = View.inflate(BMapApiDemoMain.this, R.layout.demo_info_item, null);
			TextView title = (TextView)convertView.findViewById(R.id.title);
			TextView desc = (TextView)convertView.findViewById(R.id.desc);
			if ( demos[index].demoClass == NaviDemo.class  
					|| demos[index].demoClass == CloudSearchDemo.class 
					|| demos[index].demoClass == ShareDemo.class 
					){
				title.setTextColor(Color.YELLOW);
				desc.setTextColor(Color.YELLOW);
			}
			title.setText(demos[index].title);
			desc.setText(demos[index].desc);
			return convertView;
		}
		@Override
		public int getCount() {
			return demos.length;
		}
		@Override
		public Object getItem(int index) {
			return  demos[index];
		}

		@Override
		public long getItemId(int id) {
			return id;
		}
	}
	
   private static class DemoInfo{
		private final int title;
		private final int desc;
		private final Class<? extends android.app.Activity> demoClass;

		public DemoInfo(int title , int desc,Class<? extends android.app.Activity> demoClass) {
			this.title = title;
			this.desc  = desc;
			this.demoClass = demoClass;
		}
	}
}