package com.hoos.around;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import com.facebook.android.*;
import com.facebook.android.AsyncFacebookRunner.RequestListener;
import com.facebook.android.Facebook.*;
import com.facebook.android.AsyncFacebookRunner;

public class Login extends Activity{
	Facebook facebook = new Facebook("332459203518890");
    AsyncFacebookRunner mAsyncRunner = new AsyncFacebookRunner(facebook);
    String fb_id = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        facebook.authorize(this, new DialogListener() {
            @Override
            public void onComplete(Bundle values) {}

            @Override
            public void onFacebookError(FacebookError error) {}

            @Override
            public void onError(DialogError e) {}

            @Override
            public void onCancel() {}
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        facebook.authorizeCallback(requestCode, resultCode, data);        
        
        //LOG THE FACEBOOK INFORMATION HERE
        //THIS IS A PLACEHOLDER
        mAsyncRunner.request("me", new IdRequestListener());
        finish();
    }
    
    private class IdRequestListener implements RequestListener{

		@Override
		public void onComplete(String response, Object state) {
			try {
				JSONObject json = Util.parseJson(response);
				StaticUserInfo.setFbID(json.getString("id"));
				System.out.println(StaticUserInfo.getFbID());
			} catch (FacebookError e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}

		@Override
		public void onIOException(IOException e, Object state) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onFileNotFoundException(FileNotFoundException e,
				Object state) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onMalformedURLException(MalformedURLException e,
				Object state) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onFacebookError(FacebookError e, Object state) {
			// TODO Auto-generated method stub
			
		}
    	
    }
}


