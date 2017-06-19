package batezganpitorbank.mobileclient;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.widget.AdapterView.*;

public class BalanceInquiry extends AppCompatActivity {

    String uri = "http://"+ User.ipAddress+":8080/Final_WebServer/webresources/com.server.account/";
    String function = "Accounts?custId=";

    ArrayAdapter<String> spinnerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balance_inquiry);


        Button b = (Button) findViewById(R.id.btnBack_bal);
        b.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Spinner spinner = (Spinner) findViewById(R.id.spinner_accounts_bal);
        spinner.setAdapter(null);

        spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
        spinnerAdapter.notifyDataSetChanged();
        new RestBalanceActivity().execute();

    }

    protected void onStart() {
        super.onStart();
    }

    HashMap<String,Integer> CARD = new HashMap<>();
    class RestBalanceActivity extends AsyncTask<Void, Void,HashMap<Integer,String>> {
        List<JSONObject> obj = null;


        protected HashMap<Integer,String> doInBackground(Void... params) {
            JSONArray reader;
            HashMap<Integer, String>  BAL= new HashMap<>();
            try {
                //get content from url + convert to json data

                String in = uri + function + User.currentUserID;
                InputStream is = new URL(in).openStream();
                BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
                StringBuilder sb = new StringBuilder();
                int cp;
                while ((cp = rd.read()) != -1) {
                    sb.append((char) cp);
                }
                reader = new JSONArray(sb.toString());
                is.close();

                //place raw json data to list

                obj = new ArrayList<>();

                for (int i = 0; i < reader.length(); i++) {
                    obj.add(reader.getJSONObject(i));
                }


                final List<JSONObject> finalObj = obj;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        try {

                            for (JSONObject i : finalObj) {
                                spinnerAdapter.add(i.getString("cardNumber"));
                            }
                        } catch (Exception ex) {
                        }
                    }
                });


                for (JSONObject i : obj) {
                    String url = "http://"+ User.ipAddress+":8080/Final_WebServer/webresources/com.server.account/balance?accountId=" + i.get("accountId");
                    is = new URL(url).openStream();
                    rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
                    sb = new StringBuilder();
                    while ((cp = rd.read()) != -1) {
                        sb.append((char) cp);
                    }

                    JSONObject balances = new JSONObject(sb.toString());

                    CARD.put(i.getString("cardNumber"),Integer.parseInt(i.getString("accountId")));
                    System.out.println("HASHMAP CARD: "+ i.getString("cardNumber")+ " to "+i.getString("accountId"));
                    BAL.put(Integer.parseInt(i.getString("accountId")), balances.getString("balance"));
                    System.out.println("HASHMAP BAL: " + i.getString("accountId")+ " my bal =  " + balances.getString("balance"));
                    System.out.println("SIZE: "+ BAL.size());
                    //if(!BAL.containsKey(Integer.parseInt(i.getString("accountId"))))
//                            BAL.put(Integer.parseInt(i.getString("accountId")), "0.00");

                    is.close();
                }
                return BAL;
            }
            catch (Exception ex)
            {
                System.out.println("error:" + ex.getMessage());
            }

            return BAL;
        }

        @Override
        protected void onPostExecute(final HashMap<Integer,String> BALANCE) {

            Spinner spinner = (Spinner) findViewById(R.id.spinner_accounts_bal);
            spinner.setOnItemSelectedListener(new OnItemSelectedListener() {



                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String selected = parent.getSelectedItem().toString();
                    System.out.println("card - "+ selected);
                    String balance="fuck " + selected;
                    EditText tv = (EditText) findViewById(R.id.text_balance);
                    System.out.println("SIZE: "+ BALANCE.size());
                    try{

                        balance= BALANCE.get(CARD.get(selected));
                        tv.setText(balance);
                        if(balance.isEmpty()||balance==null||balance.equals("")||balance.length()==0){
                            throw new Exception();
                        }
                    }
                    catch(Exception ex){
                        tv.setText("0.0");
                        System.out.println("error: "+ ex.getClass() + " "+ ex.getMessage());
                    }

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
    }
}