package com.chaoxing.video;


import java.io.File;

import com.chaoxing.video.fayuan.R;

import roboguice.activity.RoboActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

public class UpdateActivity extends RoboActivity{

	public static final String ACTION =  UpdateActivity.class.getName();
	
	private Button installButton;
	private Button cancelButton;
	private String updateFile;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_LEFT_ICON);
		setContentView(R.layout.update_activity);
        getWindow().setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, 
                android.R.drawable.ic_dialog_info);
        
        installButton = (Button)findViewById(R.id.btn_install);
        cancelButton = (Button)findViewById(R.id.btn_cancel);
        
        updateFile = this.getIntent().getStringExtra("updateFile");
        
        installButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(updateFile != null){
					Uri uri = Uri.fromFile(new File(updateFile));
					Intent installIntent = new Intent(Intent.ACTION_VIEW);
					installIntent.setDataAndType(uri, "application/vnd.android.package-archive");
					startActivity(installIntent);
				}
				finish();
			}
		});
        
        cancelButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

}
