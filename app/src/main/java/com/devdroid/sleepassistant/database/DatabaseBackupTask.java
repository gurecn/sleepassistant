package com.devdroid.sleepassistant.database;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.devdroid.sleepassistant.BuildConfig;
import com.devdroid.sleepassistant.R;
import com.devdroid.sleepassistant.application.LauncherModel;
import com.devdroid.sleepassistant.constant.CustomConstant;
import com.devdroid.sleepassistant.eventbus.OnUpdateProgressBackup;
import com.devdroid.sleepassistant.mode.SleepDataMode;
import com.devdroid.sleepassistant.utils.ByteUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class DatabaseBackupTask extends AsyncTask<String, String, Integer> {
    private Context mContext;
    private ProgressBar mPbProgressBar;
    private TextView mTvProgressBarNum;
    private TextView mTvContactPhone;
    public DatabaseBackupTask(Context mContext) {
        this.mContext = mContext;
    }
    public DatabaseBackupTask(Context context, ProgressBar pbProgressBar, TextView tvProgressBarNum, TextView tvContactPhone) {
        this.mContext = context;
        this.mPbProgressBar = pbProgressBar;
        this.mTvProgressBarNum = tvProgressBarNum;
        this.mTvContactPhone = tvContactPhone;
    }
    @Override
    protected Integer doInBackground(String... params) {
        String command = params[0];
        if (command.equals(CustomConstant.COMMAND_BACKUP_INTERNAL_STORAGE)) {  //备份到内存
            try {
                File exportDirInternalStorage = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator);
                if (!exportDirInternalStorage.exists()) {
                    exportDirInternalStorage.mkdirs();
                }
                File dbFile = new File("/data/data/com.devdroid.sleepassistant/databases/", "boost.db");
                Date date = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
                String fileName = mContext.getString(R.string.app_name) + "_" + sdf.format(date) + ".back";
                File backupdb = new File(exportDirInternalStorage, fileName);
                if(!backupdb.exists()) backupdb.createNewFile();
                if(dbFile.exists()) {
                    fileCopy(dbFile, backupdb);
                }
                StringBuilder sb = new StringBuilder();
                sb.append(BuildConfig.VERSION_CODE).append("_").append(BaseDatabaseHelper.DB_MIN_VERSION);
                saveExtraData(backupdb.getAbsolutePath(), sb.toString().getBytes());  //保存附加数据
                LauncherModel.getInstance().postEvent(new OnUpdateProgressBackup(100, 1));
            } catch (Exception e) {
                e.printStackTrace();
                LauncherModel.getInstance().postEvent(new OnUpdateProgressBackup(-1, 1));
            }
        } else if (command.equals(CustomConstant.COMMAND_RESTORE_INTERNAL_STORAGE)) { //读取内存数据
            String restoreDataName = params[1];
            String extraData= getExtraData(restoreDataName);
            assert extraData != null;
            String[] restoreNames = extraData.split("_");
            if(restoreNames.length != 2){
                LauncherModel.getInstance().postEvent(new OnUpdateProgressBackup(-1, 0));
                return null;
            }
            try {
                File backupdb = new File(restoreDataName);
                if(backupdb.exists()) {
                    File dbBackFile = new File("/data/data/com.devdroid.sleepassistant/databases/", "boost_backup.db");
                    if(!dbBackFile.exists()) {
                        dbBackFile.getParentFile().mkdirs();
                        dbBackFile.createNewFile();
                    }
                    fileCopy(backupdb, dbBackFile);
                    SleepDataDao backupSleepDataDao = LauncherModel.getInstance().getBackupContactsDao(Integer.valueOf(restoreNames[1]));
                    SleepDataDao sleepDataDao = LauncherModel.getInstance().getSnssdkTextDao();
                    List<SleepDataMode> backupSleepDatas = backupSleepDataDao.querySleepDataInfo();
                    List<SleepDataMode> sleepDatas = new LinkedList<>();
                    int backupTotal = backupSleepDatas.size();
                    int mProgress = 0;
                    for (int i = 0;i < backupTotal;i++){
                        SleepDataMode backupSleepData = backupSleepDatas.get(i);
                        SleepDataMode contact = sleepDataDao.querySleepDataInfo(backupSleepData.getYear(), backupSleepData.getMonth(), backupSleepData.getDay());
                        if(contact == null || contact.getHour() == -1) {
                            sleepDatas.add(backupSleepData);
                            int newprogress = 100 * (i + 1) / backupTotal;
                            if (mProgress < newprogress){
                                mProgress = newprogress;
                                publishProgress(String.valueOf(mProgress), backupSleepData.getYear() + "-" + backupSleepData.getMonth() + "-" + backupSleepData.getDay());
                            }
                            if(mProgress % 10 == 0 && !this.isCancelled()){
                                sleepDataDao.insertSleepDataItem(sleepDatas);
                                sleepDatas.clear();
                            } else if(this.isCancelled()){
                                LauncherModel.getInstance().postEvent(new OnUpdateProgressBackup(100, 0));
                                return null;
                            }
                        }
                    }
                    backupSleepDataDao.closeBackup();
                    if(dbBackFile.exists()) {
                        dbBackFile.delete();
                    }
                }
                LauncherModel.getInstance().postEvent(new OnUpdateProgressBackup(100, 0));
            } catch (Exception e) {
                e.printStackTrace();
                LauncherModel.getInstance().postEvent(new OnUpdateProgressBackup(-1, 0));
            }
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
        if(mPbProgressBar != null){
            mTvProgressBarNum.setText(values[0] + "%");
            mTvContactPhone.setText(values[1]);
            mPbProgressBar.setProgress(Integer.valueOf(values[0]));
        }
    }

    private void fileCopy(File dbFile, File backup) throws IOException {
        FileChannel inChannel = new FileInputStream(dbFile).getChannel();
        FileChannel outChannel = new FileOutputStream(backup).getChannel();
        try {
            inChannel.transferTo(0, inChannel.size(), outChannel);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inChannel != null) inChannel.close();
            if (outChannel != null) outChannel.close();
        }
    }

    /**
     * 追加附加数据
     */
    private void saveExtraData(String path, byte[] encryptData){
        try{
            /**以读写的方式建立一个RandomAccessFile对象**/
            RandomAccessFile raf=new RandomAccessFile(path, "rw");
            raf.seek(raf.length());
            raf.write(encryptData);
            byte[] encryptDataLength = ByteUtils.int2byte(encryptData.length);
            raf.write(encryptDataLength);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 读取附加数据
     * @param path 文件路径
     */
    private String getExtraData(String path){
        try{
            RandomAccessFile raf = new RandomAccessFile(path, "r");
            raf.seek(raf.length() - 2);//移动文件指针位置
            byte[]  buff=new byte[2];
            raf.read(buff);
            int anInt = ByteUtils.byte2int(buff);
            byte[] mi = new byte[anInt];
            raf.seek(raf.length() - 2 - anInt);//移动文件指针位置
            raf.read(mi);
            return new String(mi);
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
}