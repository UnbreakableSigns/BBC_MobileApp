package batezganpitorbank.mobileclient;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.SimpleXmlHttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class MainHome extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private String uri = "http://"+ User.ipAddress +":8080/Final_WebServer/webresources/com.server.onlineaccount/";
    private String function = "login?username=";



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        //new rest().execute();

        setContentView(R.layout.activity_main_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
        (new RestActivity()).execute();
    }

    @Override
    public void onBackPressed() {
/*        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            //User.currentID = null;
            //User.currentCONTENT = null;

            Intent intent = new Intent(MainHome.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Intent i;
        if (id == R.id.nav_balance) {
             i = new Intent(MainHome.this, BalanceInquiry.class);
            startActivity(i);
        } else if (id == R.id.nav_transfer) {
            i = new Intent(MainHome.this, TransferFunds.class);
            startActivity(i);
        } else if (id == R.id.nav_transaction) {
            i = new Intent(MainHome.this, TranscationHistory.class);
            startActivity(i);
        } else if (id == R.id.nav_settings) {
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    class RestActivity extends AsyncTask<Void, Void, String>{
        protected String doInBackground(Void... params) {

            String in=uri + function + User.currentUserName;
            JSONObject reader;

            try {
                //com.server.onlineaccount/login?username=
                InputStream is = new URL(in).openStream();
                System.out.println(in);
                BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));

                StringBuilder sb = new StringBuilder();
                int cp;
                while ((cp = rd.read()) != -1) {
                    sb.append((char) cp);
                }


                reader = new JSONObject(sb.toString());
                is.close();

                String name =
                        reader.getJSONObject("custId").getString("firstName") + " "
                        + reader.getJSONObject("custId").getString("lastName");
                return name;
            }
            catch(Exception ex){
                System.out.println("error:" + ex.getMessage());

            }
            return "USER";
        }

        @Override
        protected void onPostExecute(final String s) {
            if (s.equals("USER"))
                finish();
            TextView tv = (TextView)findViewById(R.id.text_name);
            tv.setText(s);
        }
    }
}
