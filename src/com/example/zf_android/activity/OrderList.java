package com.example.zf_android.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

 
 
 
import com.examlpe.zf_android.util.TitleMenuUtil;
import com.examlpe.zf_android.util.Tools;
import com.examlpe.zf_android.util.XListView;
import com.examlpe.zf_android.util.XListView.IXListViewListener;
 
 
import com.example.zf_android.BaseActivity;
import com.example.zf_android.Config;
import com.example.zf_android.R;
import com.example.zf_android.entity.TestEntitiy;
import com.example.zf_zandroid.adapter.OrderAdapter;
/***
 * 
*    
* 类名称：OrderList   
* 类描述：   订单列表
* 创建人： ljp 
* 创建时间：2015-2-4 下午3:04:31   
* @version    
*
 */
public class OrderList extends BaseActivity implements  IXListViewListener{
	//以下参数 Xlist
	private XListView Xlistview;
	private int page=1;
	private int rows=Config.ROWS;
	private LinearLayout eva_nodata;
	private boolean onRefresh_number = true;
	private OrderAdapter myAdapter;
	List<TestEntitiy>  myList = new ArrayList<TestEntitiy>();
	List<TestEntitiy>  moreList = new ArrayList<TestEntitiy>();
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				onLoad( );
				
				if(myList.size()==0){
				//	norecord_text_to.setText("您没有相关的商品");
					Xlistview.setVisibility(View.GONE);
					eva_nodata.setVisibility(View.VISIBLE);
				}
				onRefresh_number = true; 
			 	myAdapter.notifyDataSetChanged();
				break;
			case 1:
				Toast.makeText(getApplicationContext(), (String) msg.obj,
						Toast.LENGTH_SHORT).show();
			 
				break;
			case 2: // 网络有问题
				Toast.makeText(getApplicationContext(), "no 3g or wifi content",
						Toast.LENGTH_SHORT).show();
				break;
			case 3:
				Toast.makeText(getApplicationContext(),  " refresh too much",
						Toast.LENGTH_SHORT).show();
				break;
			}
		}
	};
	//个体参数
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.order_list);
		initView();
		getData();
	}

	private void initView() {
		// TODO Auto-generated method stub
		
		new TitleMenuUtil(OrderList.this, "我的订单").show();
		myAdapter=new OrderAdapter(OrderList.this, myList);
		eva_nodata=(LinearLayout) findViewById(R.id.eva_nodata);
		Xlistview=(XListView) findViewById(R.id.x_listview);
		// refund_listview.getmFooterView().getmHintView().setText("已经没有数据了");
		Xlistview.setPullLoadEnable(true);
		Xlistview.setXListViewListener(this);
		Xlistview.setDivider(null);

		Xlistview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Intent i = new Intent(OrderList.this, OrderDetail.class);
				startActivity(i);
			}
		});
		Xlistview.setAdapter(myAdapter);
	}

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		page = 1;
		 System.out.println("onRefresh1");
		myList.clear();
		 System.out.println("onRefresh2");
		getData();
	}


	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub
		if (onRefresh_number) {
			page = page+1;
			
			onRefresh_number = false;
			getData();
			
//			if (Tools.isConnect(getApplicationContext())) {
//				onRefresh_number = false;
//				getData();
//			} else {
//				onRefresh_number = true;
//				handler.sendEmptyMessage(2);
//			}
		}
		else {
			handler.sendEmptyMessage(3);
		}
	}
	private void onLoad() {
		Xlistview.stopRefresh();
		Xlistview.stopLoadMore();
		Xlistview.setRefreshTime(Tools.getHourAndMin());
	}

	public void buttonClick() {
		page = 1;
		myList.clear();
		getData();
	}
	/*
	 * 请求数据
	 */
	private void getData() {
		// TODO Auto-generated method stub
		 
	 
			 TestEntitiy te=new TestEntitiy();
				te.setContent("---content---"+page+page);
				myList.add(te);
		 
		
		System.out.println("getData");
		handler.sendEmptyMessage(0);
	}
}
