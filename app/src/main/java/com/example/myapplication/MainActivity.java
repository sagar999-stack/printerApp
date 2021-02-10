package com.example.myapplication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;



import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
public class MainActivity extends Activity {
    private Button buttonCon=null;
    private Button buttonPf=null;
    private Button buttonCash=null;
    private Button buttonCut=null;
    private EditText mTextIp=null;
    private EditText mprintfData=null;
    private EditText mprintfLog=null;
    private Socketmanager mSockManager;
    private String mydata = null;

    private Button click=null;
    private TextView data=null;
    private RequestQueue mQueue;
    String myData;
    @Override
    protected void onCreate(Bundle savedInstanceState ) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        buttonCon=(Button)findViewById(R.id.conTest);
        buttonPf=(Button)findViewById(R.id.printf);
        buttonCash=(Button)findViewById(R.id.buttonCash);
        buttonCut=(Button)findViewById(R.id.buttonCut);
        mTextIp=(EditText)findViewById(R.id.printerIp);
        mprintfData=(EditText)findViewById(R.id.printfData);
        mprintfLog=(EditText)findViewById(R.id.printfLog);
        ButtonListener buttonListener=new ButtonListener();
        buttonCon.setOnClickListener(buttonListener);
        buttonPf.setOnClickListener(buttonListener);
        buttonCash.setOnClickListener(buttonListener);
        buttonCut.setOnClickListener(buttonListener);
        mSockManager=new Socketmanager(MainActivity.this);

        click = (Button) findViewById(R.id.buttonData);
        data = (TextView) findViewById(R.id.fetcheddata);

     mQueue = Volley.newRequestQueue(this);
        jsonParse();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    class ButtonListener implements OnClickListener{

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.conTest:

                    if (conTest(mTextIp.getText().toString())) {
                        PrintfLog("connected...");
                        buttonCon.setText(getString(R.string.connected));
                        buttonPf.setEnabled(true);
                        buttonCash.setEnabled(true);
                        buttonCut.setEnabled(true);
                        click.setEnabled(true);
                    }
                    else {
                        PrintfLog("Not connected...");
                        buttonCon.setText(getString(R.string.disconnected));
                        buttonPf.setEnabled(false);
                        buttonCash.setEnabled(false);
                        buttonCut.setEnabled(false);
                    }
                    break;
                case R.id.printf:
                    try {




    if (PrintfData((mprintfData.getText().toString()+"xxxxxxxxxxxxxxxxxxxxx             ").getBytes("GBK"))) {
        PrintfLog("send successed...");
    }
    else {
        PrintfLog("send failed...");
        buttonPf.setEnabled(false);
    }


                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                        PrintfLog("send data error...");
                    }
                    break;
                case R.id.buttonCash:
                    byte SendCash[]={0x1b,0x70,0x00,0x1e,(byte)0xff,0x00};
                    if (PrintfData(SendCash)) {
                        PrintfLog("open cash successed...");
                    }
                    else {
                        PrintfLog("open cash failed");
                    }
                    break;
                case R.id.buttonCut:
                    byte SendCut[]={0x0a,0x0a,0x1d,0x56,0x01};
                    if (PrintfData(SendCut)) {
                        PrintfLog("cut paper successed...");
                    }
                    else {
                        PrintfLog("cutt paper failed...");
                    }
                    break;
                default:
                    break;
            }

        }
    }
    public boolean conTest(String printerIp) {
        mSockManager.mPort=9100;
        mSockManager.mstrIp=printerIp;
        mSockManager.threadconnect();
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (mSockManager.getIstate()) {
            return true;
        }
        else {
            return false;
        }
    }
    public void PrintfLog(String logString) {
        mprintfLog.setText(logString);
    }
    public boolean PrintfData(byte[]data) {
        mSockManager.threadconnectwrite(data);
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (mSockManager.getIstate()) {
            return true;
        }
        else {
            return false;
        }
    }


    public static String getJSONData(Context context, String textFileName) {
        String strJSON;
        StringBuilder buf = new StringBuilder();
        InputStream json;
        try {
            json = context.getAssets().open(textFileName);

            BufferedReader in = new BufferedReader(new InputStreamReader(json, "UTF-8"));

            while ((strJSON = in.readLine()) != null) {
                buf.append(strJSON);
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return buf.toString();
    }

    private void jsonParse() {
        String url = "https://devoretapi.co.uk/epos/getLastOrders/5c6974efc8bd250a10b572a0";
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url,null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        int count = 0;
                        while (count<response.length()){
                            try {
                                JSONObject obj = response.getJSONObject(count);
                                String orderData = obj.getString("delivery_address");

                                mprintfData.append(orderData );
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            count++;
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        mQueue.add(request);
    }
}

