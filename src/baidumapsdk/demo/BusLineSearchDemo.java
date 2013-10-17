package baidumapsdk.demo;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.map.MKMapTouchListener;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.PopupClickListener;
import com.baidu.mapapi.map.PopupOverlay;
import com.baidu.mapapi.map.RouteOverlay;
import com.baidu.mapapi.search.MKAddrInfo;
import com.baidu.mapapi.search.MKBusLineResult;
import com.baidu.mapapi.search.MKDrivingRouteResult;
import com.baidu.mapapi.search.MKPoiInfo;
import com.baidu.mapapi.search.MKPoiResult;
import com.baidu.mapapi.search.MKRoute;
import com.baidu.mapapi.search.MKSearch;
import com.baidu.mapapi.search.MKSearchListener;
import com.baidu.mapapi.search.MKShareUrlResult;
import com.baidu.mapapi.search.MKSuggestionResult;
import com.baidu.mapapi.search.MKTransitRouteResult;
import com.baidu.mapapi.search.MKWalkingRouteResult;
import com.baidu.platform.comapi.basestruct.GeoPoint;

/**
 * 此demo用来展示如何进行公交线路详情检索，并使用RouteOverlay在地图上绘制
 * 同时展示如何浏览路线节点并弹出泡泡
 *
 */
public class BusLineSearchDemo extends Activity {
	//UI相关
	Button mBtnSearch = null;
	Button mBtnNextLine = null;
	//浏览路线节点相关
	Button mBtnPre = null;//上一个节点
	Button mBtnNext = null;//下一个节点
	int nodeIndex = -2;//节点索引,供浏览节点时使用
	MKRoute route = null;//保存驾车/步行路线数据的变量，供浏览节点时使用
	private PopupOverlay   pop  = null;//弹出泡泡图层，浏览节点时使用
	private TextView  popupText = null;//泡泡view
	private View viewCache = null;
	private List<String> busLineIDList = null;
	int busLineIndex = 0;
	
	//地图相关，使用继承MapView的MyBusLineMapView目的是重写touch事件实现泡泡处理
	//如果不处理touch事件，则无需继承，直接使用MapView即可
    MapView mMapView = null;	// 地图View	
	//搜索相关
	MKSearch mSearch = null;	// 搜索模块，也可去掉地图模块独立使用
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		DemoApplication app = (DemoApplication)this.getApplication();
		
		setContentView(R.layout.buslinesearch);
		CharSequence titleLable="公交线路查询功能";
        setTitle(titleLable);
        //地图初始化
        mMapView = (MapView)findViewById(R.id.bmapView);
        mMapView.getController().enableClick(true);
        mMapView.getController().setZoom(12);
        busLineIDList = new ArrayList<String>();
      //创建 弹出泡泡图层
        createPaopao();
        
        //地图点击事件处理
        mMapView.regMapTouchListner(new MKMapTouchListener(){

			@Override
			public void onMapClick(GeoPoint point) {
			  //在此处理地图点击事件 
			  //消隐pop
			  if ( pop != null ){
				  pop.hidePop();
			  }
			}

			@Override
			public void onMapDoubleClick(GeoPoint point) {
				
			}

			@Override
			public void onMapLongClick(GeoPoint point) {
				
			}
        	
        });
        
        // 初始化搜索模块，注册事件监听
        mSearch = new MKSearch();
        mSearch.init(app.mBMapManager, new MKSearchListener(){

            @Override
            public void onGetPoiDetailSearchResult(int type, int error) {
            }
            
			public void onGetPoiResult(MKPoiResult res, int type, int error) {
				// 错误号可参考MKEvent中的定义
				if (error != 0 || res == null) {
					Toast.makeText(BusLineSearchDemo.this, "抱歉，未找到结果", Toast.LENGTH_LONG).show();
					return;
		        }
				
				// 找到公交路线poi node
                MKPoiInfo curPoi = null;
                int totalPoiNum  = res.getCurrentNumPois();
                //遍历所有poi，找到类型为公交线路的poi
                busLineIDList.clear();
				for( int idx = 0; idx < totalPoiNum; idx++ ) {
                    if ( 2 == res.getPoi(idx).ePoiType ) {
                        // poi类型，0：普通点，1：公交站，2：公交线路，3：地铁站，4：地铁线路
                        curPoi = res.getPoi(idx);
                        //使用poi的uid发起公交详情检索
                        busLineIDList.add(curPoi.uid);
                        System.out.println(curPoi.uid);
                        
                    }
				}
				SearchNextBusline();
				
				// 没有找到公交信息
                if (curPoi == null) {
                    Toast.makeText(BusLineSearchDemo.this, "抱歉，未找到结果", Toast.LENGTH_LONG).show();
                    return;
                }
				route = null;
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
			public void onGetAddrResult(MKAddrInfo res, int error) {
			}
			/**
			 * 获取公交路线结果，展示公交线路
			 */
			public void onGetBusDetailResult(MKBusLineResult result, int iError) {
				if (iError != 0 || result == null) {
					Toast.makeText(BusLineSearchDemo.this, "抱歉，未找到结果", Toast.LENGTH_LONG).show();
					return;
		        }

				RouteOverlay routeOverlay = new RouteOverlay(BusLineSearchDemo.this, mMapView);
			    // 此处仅展示一个方案作为示例
			    routeOverlay.setData(result.getBusRoute());
			    //清除其他图层
			    mMapView.getOverlays().clear();
			    //添加路线图层
			    mMapView.getOverlays().add(routeOverlay);
			    //刷新地图使生效
			    mMapView.refresh();
			    //移动地图到起点
			    mMapView.getController().animateTo(result.getBusRoute().getStart());
			  //将路线数据保存给全局变量
			    route = result.getBusRoute();
			    //重置路线节点索引，节点浏览时使用
			    nodeIndex = -1;
			    mBtnPre.setVisibility(View.VISIBLE);
				mBtnNext.setVisibility(View.VISIBLE);
				Toast.makeText(BusLineSearchDemo.this, 
						       result.getBusName(), 
						       Toast.LENGTH_SHORT).show();
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
        
        // 设定搜索按钮的响应
        mBtnSearch = (Button)findViewById(R.id.search);
        mBtnNextLine = (Button)findViewById(R.id.nextline);
        mBtnPre = (Button)findViewById(R.id.pre);
        mBtnNext = (Button)findViewById(R.id.next);
        mBtnPre.setVisibility(View.INVISIBLE);
		mBtnNext.setVisibility(View.INVISIBLE);
        
        OnClickListener clickListener = new OnClickListener(){
			public void onClick(View v) {
				//发起搜索
				SearchButtonProcess(v);
			}
        };
        OnClickListener nextLineClickListener = new OnClickListener(){
			public void onClick(View v) {
				//搜索下一条公交线
				SearchNextBusline();
			}
        };
        OnClickListener nodeClickListener = new OnClickListener(){
			public void onClick(View v) {
				//浏览路线节点
				nodeClick(v);
			}
        };
        mBtnSearch.setOnClickListener(clickListener); 
        mBtnNextLine.setOnClickListener(nextLineClickListener);
        mBtnPre.setOnClickListener(nodeClickListener);
        mBtnNext.setOnClickListener(nodeClickListener);
	}
	/**
	 * 发起检索
	 * @param v
	 */
	void SearchButtonProcess(View v) {
		busLineIDList.clear();
		busLineIndex = 0;
		mBtnPre.setVisibility(View.INVISIBLE);
		mBtnNext.setVisibility(View.INVISIBLE);
		if (mBtnSearch.equals(v)) {
			EditText editCity = (EditText)findViewById(R.id.city);
			EditText editSearchKey = (EditText)findViewById(R.id.searchkey);
			//发起poi检索，从得到所有poi中找到公交线路类型的poi，再使用该poi的uid进行公交详情搜索
			mSearch.poiSearchInCity(editCity.getText().toString(), editSearchKey.getText().toString());
		}
		
	}
	void SearchNextBusline(){
		 if ( busLineIndex >= busLineIDList.size()){
			 busLineIndex =0;
		 }
		 if ( busLineIndex >=0 && busLineIndex < busLineIDList.size() && busLineIDList.size() >0){
			 mSearch.busLineSearch(((EditText)findViewById(R.id.city)).getText().toString(), busLineIDList.get(busLineIndex));
			 busLineIndex ++;
		 }
		 
	}
	/**
	 * 创建弹出泡泡图层
	 */
	public void createPaopao(){
		viewCache = getLayoutInflater().inflate(R.layout.custom_text_view, null);
        popupText =(TextView) viewCache.findViewById(R.id.textcache);
        //泡泡点击响应回调
        PopupClickListener popListener = new PopupClickListener(){
			@Override
			public void onClickedPopup(int index) {
				Log.v("click", "clickapoapo");
			}
        };
        pop = new PopupOverlay(mMapView,popListener);
	}
	/**
	 * 节点浏览示例
	 * @param v
	 */
	public void nodeClick(View v){
	
		if (nodeIndex < -1 || route == null || nodeIndex >= route.getNumSteps())
			return;
		viewCache = getLayoutInflater().inflate(R.layout.custom_text_view, null);
        popupText =(TextView) viewCache.findViewById(R.id.textcache);
		//上一个节点
		if (mBtnPre.equals(v) && nodeIndex > 0){
			//索引减
			nodeIndex--;
			//移动到指定索引的坐标
			mMapView.getController().animateTo(route.getStep(nodeIndex).getPoint());
			//弹出泡泡
			popupText.setText(route.getStep(nodeIndex).getContent());
			popupText.setBackgroundResource(R.drawable.popup);
			pop.showPopup(BMapUtil.getBitmapFromView(popupText),
					route.getStep(nodeIndex).getPoint(),
					5);	
		}
		//下一个节点
		if (mBtnNext.equals(v) && nodeIndex < (route.getNumSteps()-1)){
			//索引加
			nodeIndex++;
			//移动到指定索引的坐标
			mMapView.getController().animateTo(route.getStep(nodeIndex).getPoint());
			//弹出泡泡
			popupText.setText(route.getStep(nodeIndex).getContent());
			popupText.setBackgroundDrawable(getResources().getDrawable(R.drawable.popup));
			pop.showPopup(BMapUtil.getBitmapFromView(popupText),
					route.getStep(nodeIndex).getPoint(),
					5);	
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
