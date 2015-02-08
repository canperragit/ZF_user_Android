package com.example.zf_android.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.examlpe.zf_android.util.TitleMenuUtil;
import com.example.zf_android.BaseActivity;
import com.example.zf_android.R;

/***
 * 
*    
* �����ƣ�ApplyDetail   
* ��������   ������Ȳ�ѯ
* �����ˣ� ljp 
* ����ʱ�䣺2015-2-7 ����11:28:06   
* @version    
*
 */
public class ApplyDetail extends BaseActivity implements OnClickListener{
	private TextView msg_show;
	private LinearLayout login_linear_in;
	private EditText login_edit_name;
	private LinearLayout login_linear_deletename;
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			// TODO Auto-generated method stub
			super.onCreate(savedInstanceState);
			setContentView(R.layout.apply_detail);
			new TitleMenuUtil(ApplyDetail.this, "���뿪ͨ���Ȳ�ѯ").show();
			initView();
			getData();
		}
		private void getData() {
			// TODO Auto-generated method stub
			msg_show.setText("û�в�ѯ���������д��ȷ���ֻ�����");
		}
		private void initView() {
			// TODO Auto-generated method stub
			msg_show=(TextView) findViewById(R.id.msg_show);
			login_edit_name=(EditText) findViewById(R.id.login_edit_name);
			login_edit_name.addTextChangedListener(new TextWatcher() {
				
				@Override
				public void onTextChanged(CharSequence s, int arg1, int arg2, int arg3) {
					// TODO Auto-generated method stub
					if(s.length()>0){
						login_linear_deletename.setVisibility(View.VISIBLE);
					}else{
						login_linear_deletename.setVisibility(View.GONE);
					}
				}
				
				@Override
				public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
						int arg3) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void afterTextChanged(Editable arg0) {
					// TODO Auto-generated method stub
					
				}
			});
			login_linear_deletename=(LinearLayout) findViewById(R.id.login_edit_name);
			login_linear_in=(LinearLayout) findViewById(R.id.login_linear_in);
			login_linear_deletename.setOnClickListener(this);
			login_linear_in.setOnClickListener(this);
		}
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.login_linear_in: 
				getData();
				break;
			case R.id.login_linear_deletename: 
				login_edit_name.setText("");
			break;
			default:
				break;
			}
		}
}
