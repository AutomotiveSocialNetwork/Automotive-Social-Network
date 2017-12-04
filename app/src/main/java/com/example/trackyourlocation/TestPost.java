package com.example.trackyourlocation;

/**
 * Created by Speed on 04/12/2017.
 */

import android.os.Bundle;
import android.os.Handler;

import android.app.Activity;
import android.app.ProgressDialog;

import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.BaseRequestListener;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;
import com.facebook.android.SessionStore;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;


public class TestPost extends Activity{
    private static final String TAG = "TestPost";
    private Facebook mFacebook;
    private CheckBox mFacebookCb;
    private ProgressDialog mProgress;
    private ListView list;
    ArrayList<String> listdata = new ArrayList<String>();
    private Handler mRunOnUi = new Handler();

    private static final String APP_ID = "259732484519156";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.post);


        final EditText reviewEdit = (EditText) findViewById(R.id.revieew);
        mFacebookCb				  = (CheckBox) findViewById(R.id.cb_facebook);

        mProgress	= new ProgressDialog(this);

        mFacebook 	= new Facebook(APP_ID);

        SessionStore.restore(mFacebook, this);

        if (mFacebook.isSessionValid()) {
            mFacebookCb.setChecked(true);

            String name = SessionStore.getName(this);
            name		= (name.equals("")) ? "Unknown" : name;

            mFacebookCb.setText("  Facebook  (" + name + ")");
        }

        ((Button) findViewById(R.id.button1)).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String review = reviewEdit.getText().toString();

                if (review.equals("")) return;

                if (mFacebookCb.isChecked()) postToFacebook(review);
            }
        });


        ((Button) findViewById(R.id.button2)).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {



                fetchMyFacebookStatuses();


            }
        });


    }



    private void fetchMyFacebookStatuses()
    {
        Log.d(TAG, "fetchMyFacebookStatuses:  starts");
        AsyncFacebookRunner syncinfo = new AsyncFacebookRunner(mFacebook);

        syncinfo.request("me/feed",new AsyncFacebookRunner.RequestListener() {
            
            @Override
            public void onComplete(String response) {
                Log.d(TAG, "onComplete: called within request with response : "+response);
                Log.i("uo", response);


                try

                {
                    JSONObject jsonObject = new JSONObject(response);

                    JSONArray array = jsonObject.getJSONArray("data");
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object = (JSONObject) array.get(i);
                        listdata.add((String) object.get("message"));
                        //Log.d( "Message id = "+object.get("id"),"Message = "+object.get("message"));


                    }
                } catch (Exception e)

                {
                    Log.d(TAG, "onComplete: throw exception "+e.getMessage());
                    Log.i("Error in posts:", e.getMessage());


                }
            }

            @Override
            public void onIOException(IOException e) {
                Log.d(TAG, "onIOException: called with ex "+e.getMessage());

            }

            @Override
            public void onFileNotFoundException(FileNotFoundException e) {
                Log.d(TAG, "onFileNotFoundException: called with ex "+e.getMessage());

            }

            @Override
            public void onMalformedURLException(MalformedURLException e) {
                Log.d(TAG, "onMalformedURLException: called with "+e.getMessage());

            }

            @Override
            public void onFacebookError(FacebookError e) {
                Log.d(TAG, "onFacebookError: called with "+e.getMessage());

            }




        });


        list = (ListView) findViewById(R.id.list1);
        ArrayAdapter adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                listdata);

        adapter.notifyDataSetChanged();
        list.setAdapter(adapter);
        Log.d(TAG, "fetchMyFacebookStatuses: ends");

    }


    private void postToFacebook(String review) {
        Log.d(TAG, "postToFacebook: posting "+review);
        mProgress.setMessage("Posting ...");
        mProgress.show();

        AsyncFacebookRunner mAsyncFbRunner = new AsyncFacebookRunner(mFacebook);

        Bundle params = new Bundle();

        params.putString("message", review);
        //params.putString("name", "Dexter");
        //params.putString("caption", "londatiga.net");
        //params.putString("link", "http://www.londatiga.net");
        //params.putString("description", "Dexter, seven years old dachshund who loves to catch cats, eat carrot and krupuk");
        //params.putString("picture", "http://twitpic.com/show/thumb/6hqd44");*/

        mAsyncFbRunner.request("me/feed", params, "POST", new WallPostListener());
        Log.d(TAG, "postToFacebook: ends");
    }

    private final class WallPostListener extends BaseRequestListener {
        public void onComplete(final String response) {
            mRunOnUi.post(new Runnable() {
                @Override
                public void run() {
                    mProgress.cancel();

                    Toast.makeText(TestPost.this, "Posted to Facebook", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
