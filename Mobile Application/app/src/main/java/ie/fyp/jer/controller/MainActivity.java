package ie.fyp.jer.controller;

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
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Stack;

import ie.fyp.jer.config.Website;
import ie.fyp.jer.model.Logged;
import ie.fyp.jer.model.Response;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Stack<String> titles;

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

        WebView webView = findViewById(R.id.webView);
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

        Bundle data = getIntent().getExtras();
        Logged acc = ((Response) data.getParcelable("response")).getLog();
        View headerView = navigationView.getHeaderView(0);
        TextView emailView = headerView.findViewById(R.id.emailView);
        emailView.setText(acc.getEmail());

        titles = new Stack<>();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        WebView webView = findViewById(R.id.webView);
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
            WebView webView = findViewById(R.id.webView);
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
        if (id != R.id.nav_house) {
            if (id == R.id.nav_dash) {
                setDash();
            } else if (id == R.id.nav_building) {
                setBuilding();
            } else if (id == R.id.nav_settings) {
                setSettings();
            } else if (id == R.id.nav_log) {
                setLog();
            }
            DrawerLayout drawer = findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
            return true;
        }
        item.expandActionView();
        return false;
    }

    public void setDash() {
        WebView webView = findViewById(R.id.webView);
        webView.loadUrl(Website.url);
    }

    public void setBuilding() {
        WebView webView = findViewById(R.id.webView);
        webView.loadUrl(Website.url + "/building");
    }

    public void setSettings() {
        WebView webView = findViewById(R.id.webView);
        webView.loadUrl(Website.url + "/settings");
    }

    public void setLog() {
        WebView webView = findViewById(R.id.webView);
        webView.loadUrl(Website.url + "/logout");
        finish();
    }
}
