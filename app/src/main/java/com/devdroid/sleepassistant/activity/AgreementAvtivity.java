package com.devdroid.sleepassistant.activity;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;
import com.devdroid.sleepassistant.R;
import com.devdroid.sleepassistant.base.BaseActivity;

public class AgreementAvtivity extends BaseActivity {
	private TextView container;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_agreement);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		container = (TextView)findViewById(R.id.tv_agreement_container);
		initData();
	}
	private void initData() {
		try {
			InputStream is=getAssets().open("agreement");
			ByteArrayOutputStream outStream = new ByteArrayOutputStream();
			byte[] b=new byte[1024];
			int count=-1;
			while((count=is.read(b,0,1024))!=-1) outStream.write(b, 0, count);
			b=null;
			container.setText(new String(outStream.toByteArray()));
		} catch (IOException e) {
			e.printStackTrace();
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
}
