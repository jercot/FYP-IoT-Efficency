package ie.fyp.jer.controller;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;


public class MainActivity_backup extends AppCompatActivity {

    private Button btnScan;
    private ArrayList<String> ipList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnScan = (Button)findViewById(R.id.scan);

        ipList = new ArrayList<>();
        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ScanIpTask().execute();
            }
        });
    }

    private class ScanIpTask extends AsyncTask<Void, String, Void>{

        public ScanIpTask() {

        }

        @Override
        protected void onPreExecute() {
            ipList.clear();
        }

        @Override
        protected Void doInBackground(Void... params) {
            String subnet = getLocalIp();
            int i = Integer.parseInt(subnet.charAt(subnet.length()-1)+"");
            subnet = subnet.substring(0, subnet.lastIndexOf('.')+1);
            getIps(subnet, i);
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            ipList.add(values[0]);
            new sendHttpRequest("http://fyp-iot-efficiency.eu-west-1.elasticbeanstalk.com/database", "");//http://" + values[0], "/settings").execute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            ArrayList<String> temp = ipList;
            System.console();
        }

        private String getLocalIp() {
            WifiManager mWifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            WifiInfo mWifiInfo = mWifiManager.getConnectionInfo();
            if(mWifiInfo!=null) {
                int address = mWifiInfo.getIpAddress();
                return (address&0xff) + "." + (address>>8&0xff) + "." + (address>>16&0xff) + "." + (address>>24&0xff);
            }
            return null;
        }

        private int[] getIps(String subnet, int ownIp) {
            int temp[] = new int[253];
            int count = 0, timeout = 5;
            try {
                for(int j=2;j<254;j++) {
                    if(j!=ownIp&&InetAddress.getByName(subnet + j).isReachable(timeout)) {
                        Log.d("IP", subnet + j);
                        publishProgress(subnet + j);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return temp;
        }
    }

    private class sendHttpRequest extends AsyncTask<Void, String, Void> {

        String req;

        public sendHttpRequest(String url, String req) {
            this.req = url+req;
        }

        public Void doInBackground(Void... params) {
            String responseString = null;
            try {
                URL url = new URL(req);
                Log.d("URL", req);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                if (conn.getResponseCode() == HttpsURLConnection.HTTP_OK) {
                    BufferedInputStream in = new BufferedInputStream(conn.getInputStream());
                    //Log.d("in", in.toString());
                } else {

                }
                return null;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
