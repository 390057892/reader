package com.novel.read.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.novel.read.R;
import com.novel.read.utlis.PermissionUtil;
import com.novel.read.utlis.StatusBarUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class NovelSplashActivity extends AppCompatActivity implements PermissionUtil.PermissionCallBack {
    @BindView(R.id.tvSkip)
    TextView tvSkip;

    private boolean flag = false;
    private Runnable runnable;

    protected PermissionUtil mPermissionUtil;
    private static final int PERMISSION_CODE = 999;
    private Unbinder unbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtil.setBarsStyle(this, R.color.colorPrimary, true);
        setContentView(R.layout.activity_splash);
        unbinder = ButterKnife.bind(this);

        mPermissionUtil = PermissionUtil.getInstance();
        mPermissionUtil.requestPermissions(this, PERMISSION_CODE, this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mPermissionUtil.requestResult(this, permissions, grantResults, this);
    }

    private void init() {
        runnable = new Runnable() {
            @Override
            public void run() {
                goHome();
            }
        };
        tvSkip.postDelayed(runnable, 2000);
        tvSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goHome();
            }
        });
    }

    private synchronized void goHome() {
        if (!flag) {
            flag = true;
            startActivity(new Intent(this, NovelMainActivity.class));
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        flag = true;
        tvSkip.removeCallbacks(runnable);
        unbinder.unbind();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onPermissionSuccess() {
        init();
    }

    @Override
    public void onPermissionReject(String strMessage) {
        finish();
    }

    @Override
    public void onPermissionFail() {
        mPermissionUtil.requestPermissions(this, PERMISSION_CODE,this);
    }
}
