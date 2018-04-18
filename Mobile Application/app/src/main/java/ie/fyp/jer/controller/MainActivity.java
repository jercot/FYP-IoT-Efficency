package ie.fyp.jer.controller;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Stack;

import javax.net.ssl.HttpsURLConnection;

import ie.fyp.jer.config.Website;
import ie.fyp.jer.model.Logged;
import ie.fyp.jer.model.Response;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Stack<String> titles;
    private Logged acc;
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        webView = findViewById(R.id.webView);
        CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return false;
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(getApplicationContext(), "Oh no! " + description, Toast.LENGTH_SHORT).show();
            }
        });
        webView.loadUrl(Website.url);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebContentsDebuggingEnabled(true);
        webView.addJavascriptInterface(this, "Android");
        webView.getSettings().setAppCacheEnabled(false);

        Bundle data = getIntent().getExtras();
        acc = ((Response) data.getParcelable("response")).getLog();
        View headerView = navigationView.getHeaderView(0);
        TextView emailView = headerView.findViewById(R.id.emailView);
        emailView.setText(acc.getEmail());

        titles = new Stack<>();
        setHouses();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (webView.canGoBack()) {
            //setTitle(titles.pop());
            webView.goBack();
        } else {
            moveTaskToBack(true);
            //super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            Log.v("Refresh", webView.getUrl());
            webView.loadUrl(webView.getUrl());
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_local) {
            setWeb("/local", "Local Devices");
            new ScanIpTask().execute((Void) null);
        } else if (id == R.id.nav_dash) {
            setWeb("", "Dashboard");
        } else if (id == R.id.nav_building) {
            setWeb("/building", "Add Building");
        } else if (id == R.id.nav_settings) {
            setWeb("/settings", "Settings");
        } else if (id == R.id.nav_log) {
            setWeb("/logout", "Log Out");
            finish();
        } else {
            setWeb("/house?bName=" + item.getTitle(), item.getTitle().toString());
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        item.expandActionView();
        return true;
    }

    @JavascriptInterface
    public void setWeb(final String servlet, final String title) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                titles.add(title);
                setTitle(title);
                webView.loadUrl(Website.url + servlet);
            }
        });
    }

    private void setHouses() {
        NavigationView navigationView = findViewById(R.id.nav_view);
        MenuItem house = navigationView.getMenu().findItem(R.id.nav_house);
        for (String s : acc.getBuildings())
            house.getSubMenu().add(0, 0, 0, s).setIcon(R.drawable.ic_menu_house);
    }

    private class ScanIpTask extends AsyncTask<Void, String, Void> {

        ArrayList<String> ipList;

        public ScanIpTask() {
            ipList = new ArrayList<>();
        }

        @Override
        protected Void doInBackground(Void... params) {
            String subnet = getLocalIp();
            int i = Integer.parseInt(subnet.charAt(subnet.length() - 1) + "");
            subnet = subnet.substring(0, subnet.lastIndexOf('.') + 1);
            int tries = 0;
            while(ipList.size()==0&&tries<10) {
                getIps(subnet, i);
                tries++;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            webView.loadUrl("javascript:setLocal(" + new Gson().toJson(ipList) + ")");
        }

        private String getLocalIp() {
            WifiManager mWifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            WifiInfo mWifiInfo = mWifiManager.getConnectionInfo();
            if (mWifiInfo != null) {
                int address = mWifiInfo.getIpAddress();
                return (address & 0xff) + "." + (address >> 8 & 0xff) + "." + (address >> 16 & 0xff) + "." + (address >> 24 & 0xff);
            }
            return null;
        }

        private void getIps(String subnet, int ownIp) {
            int port = 32109, timeout = 5;
            for (int i = 2; i <= 254; i++) {
                try {
                    if (i != ownIp && InetAddress.getByName(subnet + i).isReachable(timeout)) {
                        URL url = new URL("http://" + subnet + i + ":" + port);
                        String method = "GET";
                        JSONObject dataParams = new JSONObject();
                        dataParams.put("", "");
                        Log.v("Attempt: " + i, url.toString());
                        new Req().send(url, method, dataParams);
                        ipList.add(subnet + i);
                    }
                } catch (Exception e) {
                    Log.v("Tried", subnet + i + " is not a valid sensor");
                }
            }
            Log.v("Done", "search");
        }
    }

    private class Req {
        public String send(URL url, String method, JSONObject dataParams) throws Exception {
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod(method);
            conn.setDoInput(true);
            conn.setDoOutput(true);
            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(getDataString(dataParams));

            writer.flush();
            writer.close();
            os.close();

            int responseCode = conn.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new
                        InputStreamReader(
                        conn.getInputStream()));
                String line;
                if ((line = in.readLine()) != null) {
                    in.close();
                    Log.v("Line", line);
                    return line;
                }
                in.close();
            }
            return null;
        }

        private String getDataString(JSONObject params) throws Exception {
            StringBuilder result = new StringBuilder();
            boolean first = true;
            Iterator<String> itr = params.keys();
            while (itr.hasNext()) {
                String key = itr.next();
                Object value = params.get(key);
                if (first)
                    first = false;
                else
                    result.append("&");
                result.append(URLEncoder.encode(key, "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode(value.toString(), "UTF-8"));
            }
            return result.toString();
        }
    }
}
