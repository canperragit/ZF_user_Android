package com.example.zf_android.trade;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.examlpe.zf_android.util.TitleMenuUtil;
import com.example.zf_android.R;
import com.example.zf_android.trade.common.CommonUtil;
import com.example.zf_android.trade.common.HttpCallback;
import com.example.zf_android.trade.common.StringUtil;
import com.example.zf_android.trade.entity.ApplyChooseItem;
import com.example.zf_android.trade.entity.ApplyCustomerDetail;
import com.example.zf_android.trade.entity.ApplyDetail;
import com.example.zf_android.trade.entity.ApplyMaterial;
import com.example.zf_android.trade.entity.ApplyTerminalDetail;
import com.example.zf_android.trade.entity.City;
import com.example.zf_android.trade.entity.Merchant;
import com.example.zf_android.trade.entity.Province;
import com.example.zf_android.trade.widget.MyTabWidget;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.zf_android.trade.Constants.ApplyIntent.CHOOSE_ITEMS;
import static com.example.zf_android.trade.Constants.ApplyIntent.CHOOSE_TITLE;
import static com.example.zf_android.trade.Constants.ApplyIntent.REQUEST_CHOOSE_CHANNEL;
import static com.example.zf_android.trade.Constants.ApplyIntent.REQUEST_CHOOSE_CITY;
import static com.example.zf_android.trade.Constants.ApplyIntent.REQUEST_CHOOSE_MERCHANT;
import static com.example.zf_android.trade.Constants.ApplyIntent.REQUEST_UPLOAD_IMAGE;
import static com.example.zf_android.trade.Constants.ApplyIntent.SELECTED_ID;
import static com.example.zf_android.trade.Constants.ApplyIntent.SELECTED_TITLE;
import static com.example.zf_android.trade.Constants.CityIntent.SELECTED_CITY;
import static com.example.zf_android.trade.Constants.CityIntent.SELECTED_PROVINCE;
import static com.example.zf_android.trade.Constants.ShowWebImageIntent.IMAGE_NAMES;
import static com.example.zf_android.trade.Constants.ShowWebImageIntent.IMAGE_URLS;
import static com.example.zf_android.trade.Constants.ShowWebImageIntent.POSITION;
import static com.example.zf_android.trade.Constants.TerminalIntent.TERMINAL_ID;

/**
 * Created by Leo on 2015/3/5.
 */
public class ApplyDetailActivity extends FragmentActivity {

    private static final int TYPE_TEXT = 1;
    private static final int TYPE_IMAGE = 2;
    private static final int TYPE_BANK = 3;

    private static final int APPLY_PUBLIC = 1;
    private static final int APPLY_PRIVATE = 2;
    private int mApplyType;

    private static final int ITEM_EDIT = 1;
    private static final int ITEM_CHOOSE = 2;
    private static final int ITEM_UPLOAD = 3;
    private static final int ITEM_VIEW = 4;

    private int mTerminalId;

    private LayoutInflater mInflater;

    private TextView mPosBrand;
    private TextView mPosModel;
    private TextView mSerialNum;
    private TextView mPayChannel;

    private String[] mMerchantKeys;
    private LinearLayout mContainer;
    private LinearLayout mMerchantContainer;
    private LinearLayout mCustomerContainer;
    private LinearLayout mMaterialContainer;
    private Button mApplySubmit;

    private MyTabWidget mTab;

    private int mMerchantId;
    private String mMerchantGender;
    private String mMerchantBirthday;
    private Province mMerchantProvince;
    private City mMerchantCity;
    private int mChannelId;

    private ArrayList<ApplyChooseItem> mChannelItems = new ArrayList<ApplyChooseItem>();

    private List<String> mImageUrls = new ArrayList<String>();
    private List<String> mImageNames = new ArrayList<String>();

    private final Map<String, Object> params = new HashMap<String, Object>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply_detail);
        new TitleMenuUtil(this, getString(R.string.title_apply_open)).show();
        mTerminalId = getIntent().getIntExtra(TERMINAL_ID, 0);

        initViews();
        loadData(mApplyType);
    }

    private void initViews() {
        mInflater = LayoutInflater.from(this);

        mTab = (MyTabWidget) findViewById(R.id.apply_detail_tab);
        mTab.addTab(getString(R.string.apply_public), 17);
        mTab.addTab(getString(R.string.apply_private), 17);
        mTab.setOnTabSelectedListener(new MyTabWidget.OnTabSelectedListener() {
            @Override
            public void onTabSelected(int position) {
                mApplyType = position + 1;
                loadData(mApplyType);
            }
        });
        mTab.updateTabs(0);
        mApplyType = APPLY_PUBLIC;

        mPosBrand = (TextView) findViewById(R.id.apply_detail_brand);
        mPosModel = (TextView) findViewById(R.id.apply_detail_model);
        mSerialNum = (TextView) findViewById(R.id.apply_detail_serial);
        mPayChannel = (TextView) findViewById(R.id.apply_detail_channel);

        mContainer = (LinearLayout) findViewById(R.id.apply_detail_container);
        mMerchantContainer = (LinearLayout) findViewById(R.id.apply_detail_merchant_container);
        mCustomerContainer = (LinearLayout) findViewById(R.id.apply_detail_customer_container);
        mMaterialContainer = (LinearLayout) findViewById(R.id.apply_detail_material_container);
        mApplySubmit = (Button) findViewById(R.id.apply_submit);
    }

    private void loadData(int applyType) {
        mMerchantContainer.removeAllViews();
        mCustomerContainer.removeAllViews();
        mMaterialContainer.removeAllViews();
        initMerchantDetailKeys();

        API.getApplyDetail(this, 80, mTerminalId, applyType, new HttpCallback<ApplyDetail>(this) {
            @Override
            public void onSuccess(ApplyDetail data) {
                ApplyTerminalDetail terminalDetail = data.getTerminalDetail();
                final List<ApplyChooseItem> merchants = data.getMerchants();
                List<ApplyMaterial> materials = data.getMaterials();
                List<ApplyCustomerDetail> customerDetails = data.getCustomerDetails();

                if (null != terminalDetail) {
                    mPosBrand.setText(terminalDetail.getBrandName());
                    mPosModel.setText(terminalDetail.getModelNumber());
                    mSerialNum.setText(terminalDetail.getSerialNumber());
                    mPayChannel.setText(terminalDetail.getChannelName());
                }
                // set the choosing merchant listener
                View merchantChoose = mMerchantContainer.findViewWithTag(mMerchantKeys[0]);
                merchantChoose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startChooseItemActivity(REQUEST_CHOOSE_MERCHANT, getString(R.string.title_apply_choose_merchant), mMerchantId, (ArrayList<ApplyChooseItem>) merchants);
                    }
                });
                // set the customer details
                setCustomerDetail(customerDetails);

            }

            @Override
            public TypeToken<ApplyDetail> getTypeToken() {
                return new TypeToken<ApplyDetail>() {
                };
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;
        switch (requestCode) {
            case REQUEST_CHOOSE_MERCHANT: {
                mMerchantId = data.getIntExtra(SELECTED_ID, 0);
                setItemValue(mMerchantKeys[0], data.getStringExtra(SELECTED_TITLE));
                API.getApplyMerchantDetail(ApplyDetailActivity.this, mMerchantId, new HttpCallback(ApplyDetailActivity.this) {
                    @Override
                    public void onSuccess(Object data) {
                    }

                    @Override
                    public TypeToken getTypeToken() {
                        return null;
                    }
                });
                break;
            }
            case REQUEST_CHOOSE_CITY: {
                mMerchantProvince = (Province) data.getSerializableExtra(SELECTED_PROVINCE);
                mMerchantCity = (City) data.getSerializableExtra(SELECTED_CITY);
                setItemValue(mMerchantKeys[8], mMerchantCity.getName());
                break;
            }
            case REQUEST_CHOOSE_CHANNEL: {
                mChannelId = data.getIntExtra(SELECTED_ID, 0);
                setItemValue(getString(R.string.apply_detail_channel), data.getStringExtra(SELECTED_TITLE));
            }
            case REQUEST_UPLOAD_IMAGE: {
                Uri uri = data.getData();
                if (uri != null) {
                    final String realPath = getRealPathFromURI(uri);
                    final Handler handler = new Handler() {
                        @Override
                        public void handleMessage(Message msg) {
                            if (msg.what == 1) {
                                CommonUtil.toastShort(ApplyDetailActivity.this, (String) msg.obj);
                            } else {
                                CommonUtil.toastShort(ApplyDetailActivity.this, getString(R.string.toast_upload_failed));
                            }
                        }
                    };
                    new Thread() {
                        @Override
                        public void run() {
                            CommonUtil.uploadFile(realPath, "img", new CommonUtil.OnUploadListener() {
                                @Override
                                public void onSuccess(String result) {
                                    try {
                                        JSONObject jo = new JSONObject(result);
                                        String url = jo.getString("result");
                                        Message msg = new Message();
                                        msg.what = 1;
                                        msg.obj = url;
                                        handler.sendMessage(msg);
                                    } catch (JSONException e) {
                                    }
                                }

                                @Override
                                public void onFailed(String message) {
                                    handler.sendEmptyMessage(0);
                                }
                            });
                        }
                    }.start();
                }
                break;
            }
        }
    }

    public String getRealPathFromURI(Uri contentUri) {
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            Cursor cursor = this.managedQuery(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } catch (Exception e) {
            return contentUri.getPath();
        }
    }

    private void setItemValue(String key, String value) {
        LinearLayout item = (LinearLayout) mContainer.findViewWithTag(key);
        TextView tvValue = (TextView) item.findViewById(R.id.apply_detail_value);
        tvValue.setText(value);
    }

    /**
     * firstly init the merchant category with item keys,
     * and after user select the merchant the values will be set
     */
    private void initMerchantDetailKeys() {
        mMerchantKeys = getResources().getStringArray(R.array.apply_detail_merchant_keys);

        mMerchantContainer.addView(getDetailItem(ITEM_CHOOSE, mMerchantKeys[0], null));
        mMerchantContainer.addView(getDetailItem(ITEM_EDIT, mMerchantKeys[1], null));
        mMerchantContainer.addView(getDetailItem(ITEM_EDIT, mMerchantKeys[2], null));

        View merchantGender = getDetailItem(ITEM_CHOOSE, mMerchantKeys[3], null);
        merchantGender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ApplyDetailActivity.this);
                builder.setIcon(R.drawable.ic_launcher);
                builder.setTitle(getString(R.string.title_apply_choose_gender));
                final String[] items = getResources().getStringArray(R.array.apply_detail_gender);
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setItemValue(mMerchantKeys[3], items[which]);
                    }
                });
                builder.show();
            }
        });
        mMerchantContainer.addView(merchantGender);

        View merchantBirthday = getDetailItem(ITEM_CHOOSE, mMerchantKeys[4], null);
        merchantBirthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommonUtil.showDatePicker(ApplyDetailActivity.this, mMerchantBirthday, new CommonUtil.OnDateSetListener() {
                    @Override
                    public void onDateSet(String date) {
                        setItemValue(mMerchantKeys[4], date);
                    }
                });
            }
        });
        mMerchantContainer.addView(merchantBirthday);
        mMerchantContainer.addView(getDetailItem(ITEM_EDIT, mMerchantKeys[5], null));
        mMerchantContainer.addView(getDetailItem(ITEM_EDIT, mMerchantKeys[6], null));
        mMerchantContainer.addView(getDetailItem(ITEM_EDIT, mMerchantKeys[7], null));

        View merchantCity = getDetailItem(ITEM_CHOOSE, mMerchantKeys[8], null);
        merchantCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ApplyDetailActivity.this, CityProvinceActivity.class);
                intent.putExtra(SELECTED_PROVINCE, mMerchantProvince);
                intent.putExtra(SELECTED_CITY, mMerchantCity);
                startActivityForResult(intent, REQUEST_CHOOSE_CITY);
            }
        });
        mMerchantContainer.addView(merchantCity);


    }

    /**
     * set the item values after user select a merchant
     *
     * @param merchant
     */
    private void setMerchantDetailValues(Merchant merchant) {

    }

    /**
     * start the {@link ApplyChooseActivity} to choose item
     *
     * @param requestCode handle the return item according to request code
     * @param title       the started activity title
     * @param selectedId  the id of the selected item
     * @param items       the items to choose
     */
    private void startChooseItemActivity(int requestCode, String title, int selectedId, ArrayList<ApplyChooseItem> items) {
        Intent intent = new Intent(ApplyDetailActivity.this, ApplyChooseActivity.class);
        intent.putExtra(CHOOSE_TITLE, title);
        intent.putExtra(SELECTED_ID, selectedId);
        intent.putExtra(CHOOSE_ITEMS, items);
        startActivityForResult(intent, requestCode);
    }

    /**
     * set the customer's details after the first request returned
     *
     * @param customerDetails
     */
    private void setCustomerDetail(List<ApplyCustomerDetail> customerDetails) {
        for (ApplyCustomerDetail customerDetail : customerDetails) {
            switch (customerDetail.getTypes()) {
                case TYPE_TEXT:
                    mCustomerContainer.addView(getDetailItem(ITEM_EDIT, customerDetail.getKey(), customerDetail.getValue()));
                    break;
                case TYPE_IMAGE:
                    String imageName = customerDetail.getKey();
                    String imageUrl = customerDetail.getValue();
                    if (!TextUtils.isEmpty(imageUrl)) {
                        mImageNames.add(customerDetail.getKey());
                        mImageUrls.add(customerDetail.getValue());
                        mMaterialContainer.addView(getDetailItem(ITEM_VIEW, imageName, imageUrl));
                    } else {
                        mMaterialContainer.addView(getDetailItem(ITEM_UPLOAD, imageName, imageUrl));
                    }
                    break;
                case TYPE_BANK:

                    break;
            }
        }
        View chooseChannel = getDetailItem(ITEM_CHOOSE, getString(R.string.apply_detail_channel), null);
        chooseChannel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mChannelItems.size() > 0) {
                    startChooseItemActivity(REQUEST_CHOOSE_CHANNEL, getString(R.string.title_apply_choose_channel), mChannelId, mChannelItems);
                } else {
                    API.getApplyChannelList(ApplyDetailActivity.this, new HttpCallback<List>(ApplyDetailActivity.this) {
                        @Override
                        public void onSuccess(List data) {
                            for (Object obj : data) {
                                LinkedTreeMap map = (LinkedTreeMap) obj;
                                ApplyChooseItem item = new ApplyChooseItem();
                                item.setId((int) Math.floor((Double) map.get("id")));
                                item.setTitle((String) map.get("name"));
                                mChannelItems.add(item);
                            }
                            startChooseItemActivity(REQUEST_CHOOSE_CHANNEL, getString(R.string.title_apply_choose_channel), mChannelId, mChannelItems);
                        }

                        @Override
                        public TypeToken<List> getTypeToken() {
                            return new TypeToken<List>() {
                            };
                        }
                    });
                }


            }
        });
        mCustomerContainer.addView(chooseChannel);
    }

    private void setMerchantItem(int itemType, String key, String value) {
        LinearLayout item = (LinearLayout) mMerchantContainer.findViewWithTag(key);
        setupItem(item, itemType, key, value);
    }

    private LinearLayout getDetailItem(int itemType, String key, String value) {
        LinearLayout item;
        switch (itemType) {
            case ITEM_EDIT:
                item = (LinearLayout) mInflater.inflate(R.layout.apply_detail_item_edit, null);
                break;
            case ITEM_CHOOSE:
                item = (LinearLayout) mInflater.inflate(R.layout.apply_detail_item_choose, null);
                break;
            case ITEM_UPLOAD:
                item = (LinearLayout) mInflater.inflate(R.layout.apply_detail_item_upload, null);
                break;
            case ITEM_VIEW:
                item = (LinearLayout) mInflater.inflate(R.layout.apply_detail_item_view, null);
                break;
            default:
                item = (LinearLayout) mInflater.inflate(R.layout.apply_detail_item_edit, null);
        }
        item.setTag(key);
        setupItem(item, itemType, key, value);
        return item;
    }

    private void setupItem(LinearLayout item, int itemType, final String key, final String value) {
        switch (itemType) {
            case ITEM_EDIT: {
                TextView tvKey = (TextView) item.findViewById(R.id.apply_detail_key);
                EditText etValue = (EditText) item.findViewById(R.id.apply_detail_value);

                if (!TextUtils.isEmpty(key))
                    tvKey.setText(key);
                if (!TextUtils.isEmpty(value))
                    etValue.setText(value);
                break;
            }
            case ITEM_CHOOSE: {
                TextView tvKey = (TextView) item.findViewById(R.id.apply_detail_key);
                TextView tvValue = (TextView) item.findViewById(R.id.apply_detail_value);

                if (!TextUtils.isEmpty(key))
                    tvKey.setText(key);
                if (!TextUtils.isEmpty(value))
                    tvValue.setText(value);
                break;
            }
            case ITEM_UPLOAD: {
                TextView tvKey = (TextView) item.findViewById(R.id.apply_detail_key);
                TextView tvValue = (TextView) item.findViewById(R.id.apply_detail_value);

                if (!TextUtils.isEmpty(key))
                    tvKey.setText(key);
                tvValue.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(intent, REQUEST_UPLOAD_IMAGE);
                    }
                });
                break;
            }
            case ITEM_VIEW: {
                TextView tvKey = (TextView) item.findViewById(R.id.apply_detail_key);
                ImageButton ibView = (ImageButton) item.findViewById(R.id.apply_detail_view);

                if (!TextUtils.isEmpty(key))
                    tvKey.setText(key);
                ibView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int position = mImageNames.indexOf(key);
                        Intent intent = new Intent(ApplyDetailActivity.this, ShowWebImageActivity.class);
                        intent.putExtra(IMAGE_NAMES, StringUtil.join(mImageNames, ","));
                        intent.putExtra(IMAGE_URLS, StringUtil.join(mImageUrls, ","));
                        intent.putExtra(POSITION, position);
                        startActivity(intent);
                    }
                });
            }
        }
    }

}