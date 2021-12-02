package com.devdroid.sleepassistant.activity;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.devdroid.sleepassistant.R;
import com.devdroid.sleepassistant.application.LauncherModel;
import com.devdroid.sleepassistant.base.BaseActivity;
import com.devdroid.sleepassistant.constant.CustomConstant;
import com.devdroid.sleepassistant.database.DatabaseBackupTask;
import com.devdroid.sleepassistant.eventbus.OnUpdateProgressBackup;
import com.devdroid.sleepassistant.utils.FileUtils;
public class SettingsActivity extends BaseActivity {
    private DatabaseBackupTask mBackupTask;
    private Dialog mBackupDialog;
    private Object mEventSubscriber = new Object() {
        @SuppressWarnings("unused")
        public void onEventMainThread(OnUpdateProgressBackup event) {

            if(event.getTypeProgress() == 0){  //数据还原
                if(mBackupDialog != null){
                    mBackupDialog.dismiss();
                }
                if(event.getProgressNum() == 100){
                    Toast.makeText(SettingsActivity.this, getString(R.string.ime_setting_restore_success), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SettingsActivity.this, getString(R.string.restore_data_data_bad), Toast.LENGTH_SHORT).show();
                }
            } else {   //数据备份
                if(event.getProgressNum() == 100){
                    Toast.makeText(SettingsActivity.this, getString(R.string.ime_setting_backup_success), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SettingsActivity.this, getString(R.string.ime_setting_backup_failed), Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if(!LauncherModel.getInstance().getGlobalEventBus().isRegistered(mEventSubscriber)){
            LauncherModel.getInstance().getGlobalEventBus().register(mEventSubscriber);
        }
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    public void onClick(View view){
        switch (view.getId()){
            case R.id.ll_setting_data_export:
                new DatabaseBackupTask(this).execute(CustomConstant.COMMAND_BACKUP_INTERNAL_STORAGE);
                Toast.makeText(this, getString(R.string.ime_setting_backuping), Toast.LENGTH_SHORT).show();
                break;
            case R.id.ll_setting_data_import:
                showFileChooser();
                break;
            case R.id.ll_setting_about:
                startActivity(new Intent(this, AboutActivity.class));
                break;
            case R.id.ll_setting_feedback:
                startActivity(new Intent(this, FeedbackTXActivity.class));
                break;

            case R.id.ll_setting_change_icon:
                PackageManager pm = getPackageManager();
                if(PackageManager.COMPONENT_ENABLED_STATE_DISABLED != pm.getComponentEnabledSetting(new ComponentName(this, "com.devdroid.sleepassistant.activity.GuideActivity"))) {
                    pm.setComponentEnabledSetting(new ComponentName(this, "com.devdroid.sleepassistant.activity.GuideActivity"),
                        PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
                    pm.setComponentEnabledSetting(new ComponentName(this, "com.devdroid.sleepassistant.activity.GuideActivity1"),
                        PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
                } else {
                    pm.setComponentEnabledSetting(new ComponentName(this, "com.devdroid.sleepassistant.activity.GuideActivity1"),
                        PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
                    pm.setComponentEnabledSetting(new ComponentName(this, "com.devdroid.sleepassistant.activity.GuideActivity"),
                        PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
                }
                break;
            case R.id.ll_setting_logout:
                finish();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)  {
        switch (requestCode) {
            case 1111:
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    final String path = FileUtils.getPath(this, uri);
                    if(path != null && path.endsWith(".back")) {
                        showBackupDialog(path);
                    } else {
                        Toast.makeText(this, getString(R.string.restore_data_select_error), Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }

    /**
     * 注销账号时进行密码验证
     */
    private void showBackupDialog(final String path) {
        mBackupDialog = new Dialog(this, R.style.dialogCustom);
        mBackupDialog.setContentView(R.layout.dialog_add_to_file);
        final ProgressBar mPbProgressBar = (ProgressBar) mBackupDialog.findViewById(R.id.pb_addconstact_progress);
        final TextView mTvProgressBarNum = (TextView) mBackupDialog.findViewById(R.id.addconstact_progress);
        final TextView mContactPhone = (TextView) mBackupDialog.findViewById(R.id.contactsphone);
        Button addfile_cancle = (Button) mBackupDialog.findViewById(R.id.addfile_cancle);
        addfile_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBackupDialog.dismiss();
                if(mBackupTask != null){
                    mBackupTask.cancel(true);
                }
            }
        });
        findViewById(R.id.ll_setting_logout).postDelayed(new Runnable() {
            @Override
            public void run() {
                mBackupTask = new DatabaseBackupTask(SettingsActivity.this, mPbProgressBar, mTvProgressBarNum, mContactPhone);
                mBackupTask.execute(CustomConstant.COMMAND_RESTORE_INTERNAL_STORAGE, path);
            }
        }, 500);
        mBackupDialog.show();
    }

    /**
     * 打开文件选择器
     */
    private void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            startActivityForResult(Intent.createChooser(intent, "Select a File to Import"), 1111);
        } catch (ActivityNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(LauncherModel.getInstance().getGlobalEventBus().isRegistered(mEventSubscriber)){
            LauncherModel.getInstance().getGlobalEventBus().unregister(mEventSubscriber);
        }
    }
}
