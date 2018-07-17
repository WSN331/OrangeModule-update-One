package com.qiuyi.cn.orangemoduleNew.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.qiuyi.cn.orangemoduleNew.MainActivity;
import com.qiuyi.cn.orangemoduleNew.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2018/1/4.
 *
 * 支付成功界面
 */
public class PayendActivity extends Activity{
    @BindView(R.id.backToMain)
    TextView backToMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payend);
        ButterKnife.bind(this);

        backToMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PayendActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }


}
