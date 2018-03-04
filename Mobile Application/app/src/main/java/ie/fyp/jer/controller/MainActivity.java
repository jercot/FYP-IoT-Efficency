package ie.fyp.jer.controller;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ArrayList<String> sensorIps;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        sensorIps = new ArrayList<>();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new GetLocalIps().execute();
    }

    private class GetLocalIps extends AsyncTask<Void, String, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                BufferedReader br = new BufferedReader(new FileReader(new File("/proc/net/arp")));
                String total = "";
                String line;
                while ((line = br.readLine()) != null) {
                    System.out.println(line);
                    //if (line.contains(":") && line.contains(".") && !line.contains("00:00:00:00:00:00")) {
                        String ip[] = line.split(" ");
                        if(ip[0].charAt(ip[0].length()-1)!='1') {
                            total += ip[0] + " ";
                        }
                    //}
                }
                String request[] = {"http://", total, ":32109/settings"};
                //new SendHttpRequest().execute(request);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private class SendHttpRequest extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            String urls[] = params[1].split(" ");
            HttpURLConnection urlConnection = null;
            for(int i=0;i<urls.length;i++) {
                String rStr = "";
                try {
                    URL url = new URL(params[0] + urls[i] + params[2]);
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setConnectTimeout(10000);
                    InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                    rStr = readStream(in);
                } catch (IOException e) {
                } finally {
                    if (urlConnection != null)
                        urlConnection.disconnect();
                }
                if(rStr.contains("\"code\":\"1\"")) {
                    System.out.println("Connected to " + params[0] + urls[i] + params[2]);
                    sensorIps.add(params[0] + urls[i] + params[2]);
                }
            }
            return null;
        }
    }

    private String readStream(InputStream in) throws IOException {
        BufferedReader r = new BufferedReader(new InputStreamReader(in));
        String total = "START";
        String line;
        while ((line = r.readLine()) != null) {
            total += line;
        }
        return total;
    }
}
