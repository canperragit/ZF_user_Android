package com.example.zf_android.activity;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zf_android.BaseActivity;
import com.example.zf_android.Config;
import com.example.zf_android.MyApplication;
import com.example.zf_android.R;
import com.example.zf_android.entity.PosEntity;
import com.example.zf_android.entity.PosItem;
import com.example.zf_android.entity.PosSelectEntity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class PosSelect extends BaseActivity implements  OnClickListener{
	private ImageView img_on_off;
	private Boolean isOpen_mineset;
	private SharedPreferences mySharedPreferences;
	private Editor editor;
	private EditText et1,et2;
 
	private PosSelectEntity pse;
	private List<PosItem> mylist=new ArrayList<PosItem>();
	private LinearLayout ll_pp,ll_type, ll_pay_type,ll_paycard_type,ll_jy_type,ll_qgd_type,ll_time,ll_zdj,ll_zgj;
	private TextView next_sure,tv_back,tv_pp,tv_type,tv_pay_type,tv_paycard_type,tv_jy_type,tv_qgd_type,tv_time,tv_zdj,tv_zgj;
	@Override
	protected void onCreate(Bundle savedInstanceState) { 
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.posselect);
		initView();
		gateData();
	}
	private void gateData() {
		// TODO Auto-generated method stub

		// TODO Auto-generated method stub
		String url = "http://114.215.149.242:18080/ZFMerchant/api/good/search";
		RequestParams params = new RequestParams("city_id", 1);
	 
		 
		 
		params.setUseJsonStreamer(true);

		MyApplication.getInstance().getClient()
				.post(url, params, new AsyncHttpResponseHandler() {

					@Override
					public void onSuccess(int statusCode, Header[] headers,
							byte[] responseBody) {
						String responseMsg = new String(responseBody)
								.toString();
						Log.e("print", responseMsg);

					 
						 
						Gson gson = new Gson();
						
						JSONObject jsonobject = null;
						String code = null;
						try {
							jsonobject = new JSONObject(responseMsg);
							code = jsonobject.getString("code");
							int a =jsonobject.getInt("code");
							if(a==Config.CODE){  
					 
								String res =jsonobject.getString("result");
								 
								
								pse= gson.fromJson(res, new TypeToken <PosSelectEntity> () {
			 					}.getType());
								MyApplication.pse = pse;
								System.out.println(MyApplication.pse.getBrands().size()+"---"+pse.getBrands().size());
								
//								myList.addAll(moreList);
//				 				handler.sendEmptyMessage(0);
//			 					  
//			 				 
			 			 
							}else{
								code = jsonobject.getString("message");
								Toast.makeText(getApplicationContext(), code, 1000).show();
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							 ;	
							e.printStackTrace();
							
						}

					}

					@Override
					public void onFailure(int statusCode, Header[] headers,
							byte[] responseBody, Throwable error) {
						// TODO Auto-generated method stub
						System.out.println("-onFailure---");
						Log.e("print", "-onFailure---" + error);
					}
				});
 
		 
	
	}
	private void initView() {
		
		
		mySharedPreferences = getSharedPreferences(Config.SHARED, MODE_PRIVATE);
		editor = mySharedPreferences.edit();
		isOpen_mineset=mySharedPreferences.getBoolean("isOpen_ps", true);
		img_on_off=(ImageView) findViewById(R.id.img_on_off);
		if(!isOpen_mineset){
			img_on_off.setBackgroundResource(R.drawable.pos_off);
		 
		}
		img_on_off.setOnClickListener(this);
		
		// TODO Auto-generated method stub
		et1=(EditText) findViewById(R.id.et_zdj);
		et2=(EditText) findViewById(R.id.et_zgj);
		
		tv_time=(TextView) findViewById(R.id.tv_time);
		tv_qgd_type=(TextView) findViewById(R.id.tv_qgd_type);
		tv_jy_type=(TextView) findViewById(R.id.tv_jy_type);
		tv_paycard_type=(TextView) findViewById(R.id.tv_paycard_type);
		tv_pay_type=(TextView) findViewById(R.id.tv_pay_type);
		tv_type=(TextView) findViewById(R.id.tv_type);
		tv_pp=(TextView) findViewById(R.id.tv_pp);
		img_on_off=(ImageView) findViewById(R.id.img_on_off);
		img_on_off.setOnClickListener(this);
		ll_pp=(LinearLayout) findViewById(R.id.ll_pp);
		ll_pp.setOnClickListener(this);
		ll_type=(LinearLayout) findViewById(R.id.ll_type);
		ll_type.setOnClickListener(this);
		ll_pay_type=(LinearLayout) findViewById(R.id.ll_pay_type);
		ll_pay_type.setOnClickListener(this);
		ll_paycard_type=(LinearLayout) findViewById(R.id.ll_paycard_type);
		ll_paycard_type.setOnClickListener(this);
		ll_jy_type=(LinearLayout) findViewById(R.id.ll_jy_type);
		ll_jy_type.setOnClickListener(this);
		ll_qgd_type=(LinearLayout) findViewById(R.id.ll_qgd_type);
		ll_qgd_type.setOnClickListener(this);
		ll_time=(LinearLayout) findViewById(R.id.ll_time);
		ll_time.setOnClickListener(this);
//		ll_zdj=(LinearLayout) findViewById(R.id.ll_zdj);
//		ll_zdj.setOnClickListener(this);
//		ll_zgj=(LinearLayout) findViewById(R.id.ll_zgj);
//		ll_zgj.setOnClickListener(this);
		next_sure=(TextView) findViewById(R.id.next_sure);
		next_sure.setOnClickListener(this);
		tv_back=(TextView) findViewById(R.id.tv_back);
		tv_back.setOnClickListener(this);
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.next_sure:  // 
			int a=0 ;int b=0;
			if(et1.getText().toString()==null){
				  a=0;
			}else{
				  a=Integer.parseInt(  et1.getText().toString());
			}
			if( et2.getText().toString()==null){
				  b=0;
			}else{
				  b=Integer.parseInt(  et2.getText().toString());
			}
			
			 if(a==0||b==0){
					Intent intent2 = new Intent();
					intent2.putExtra("minPrice", a);
					intent2.putExtra("maxPrice", b);
					PosSelect.this.setResult(1, intent2);
					finish();
			 }else{
					if(a>b){
						Toast.makeText(getApplicationContext(), "最低价必须比最高价低", 1000).show();
						break;
					}else{
						Intent intent2 = new Intent();
						intent2.putExtra("minPrice", a);
						intent2.putExtra("maxPrice", b);
						PosSelect.this.setResult(1, intent2);
						finish();
					}
			 }
		 
 
			
			break;
		case R.id.tv_back:  // next_sure
			finish();
			break;
		case R.id.ll_pp:  // 
			Intent ll_pp=new Intent(PosSelect.this, PosSelecList.class);
			ll_pp.putExtra("key", "选择POS品牌");
			ll_pp.putExtra("index", 100);
			 
			startActivityForResult(ll_pp, 100);
			break;
		case R.id.ll_type:  //  
			Intent ll_type=new Intent(PosSelect.this, PosSelecList.class);
			ll_type.putExtra("key", "选择POS类型");
			ll_type.putExtra("index", 101);
			 
			startActivityForResult(ll_type, 101);
			break;
		case R.id.ll_pay_type:  //  
			Intent ll_pay_type=new Intent(PosSelect.this, PosSelecList.class);
			ll_pay_type.putExtra("key", "选择支付通道");
			ll_pay_type.putExtra("index", 102);
			 
			startActivityForResult(ll_pay_type, 102);
			break;
		case R.id.ll_paycard_type:  
			Intent ll_paycard_type=new Intent(PosSelect.this, PosSelecList.class);
			ll_paycard_type.putExtra("key", "选择支付卡类型");
			ll_paycard_type.putExtra("index", 103);
			 
			startActivityForResult(ll_paycard_type, 103);
			break;
		case R.id.ll_qgd_type:   
			 
			break;
		case R.id.ll_time:   
		 
		break;
		case R.id.img_on_off:
			
			if(isOpen_mineset){
				isOpen_mineset=false;
				//img_on_off.setBackgroundDrawable(getResources().getDrawable(R.drawable.pos_off));
				img_on_off.setBackgroundResource(R.drawable.pos_off);
				editor.putBoolean("isOpen_ps",false);
					editor.commit();
				
			}else{
				isOpen_mineset=true;
				img_on_off.setBackgroundResource(R.drawable.pos_on);		 
				editor.putBoolean("isOpen_ps",true);
					editor.commit();
			}
			
			
			
			break;
		default:
			break;
		}
	}
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		switch (resultCode) {
		case 100:
			if(data!=null){
			 
				tv_pp.setText(data.getStringExtra("text"));
				
			}
			break;
		case 101:
			if(data!=null){
				 
				tv_type.setText(data.getStringExtra("text"));
			}
		 
			break;
		case 102: 
			if(data!=null){
				 
				tv_pay_type.setText(data.getStringExtra("text"));
			}
		 
			break;
		case 103: 
		if(data!=null){
			 
			tv_paycard_type.setText(data.getStringExtra("text"));
		}
	 
		break;
		default:
			break;

		}
		super.onActivityResult(requestCode, resultCode, data);

	}
}
