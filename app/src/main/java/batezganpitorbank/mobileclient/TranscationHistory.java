package batezganpitorbank.mobileclient;

import android.os.AsyncTask;
import android.support.annotation.ColorInt;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class TranscationHistory extends AppCompatActivity {

    TableLayout tb = null;
    String uri = "http://"+ User.ipAddress+":8080/Final_WebServer/webresources/com.server.account/";
    String function = "Accounts?custId=";

    ArrayAdapter<String> spinnerAdapter;
    TableRow tr;    TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transcation_history);

        Button b = (Button)findViewById(R.id.btn_back_history);
        b.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Spinner spinner = (Spinner) findViewById(R.id.spinner_accounts_history);
        //spinner.setAdapter(null);

        spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
        spinnerAdapter.notifyDataSetChanged();


        tr= new TableRow(this);
        tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT));


        tb = (TableLayout) findViewById(R.id.table_layout);

        tv = new TextView(this);
        tv.setText("DATE");
        tr.addView(tv,new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));


        tv = new TextView(this);

        tv.setText("DESC");
        tr.addView(tv,new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
        tv = new TextView(this);
        tv.setText("REF NO");
        tr.addView(tv,new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
        tv = new TextView(this);
        tv.setText("TYPE");
        tr.addView(tv,new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
        tv = new TextView(this);
        tv.setText("AMOUNT");
        tr.addView(tv,new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
        tr.setBackgroundColor(getResources().getColor(R.color.colorLightGray));

        tv = new TextView(this);
        tb.addView(tr);

    }

    @Override
    protected void onStart() {
        super.onStart();
        (new TransactionActivity()).execute();
    }


    void Selected(){

    }

    class TransactionActivity extends AsyncTask<Void,Void,List<List<String[]>>>
    {
        @Override
        protected List<List<String[]>> doInBackground(Void... params) {
            JSONArray reader;
            List<JSONObject> obj;

            List<List<String[]>> accountList = new ArrayList<>();//list of accounts for one user
            List<String[]> transactionList = new ArrayList<>();//list of transactions in one account

            try {

                String in = uri + function + User.currentUserID;
                System.out.println(in);
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
                                spinnerAdapter.add(i.getString("accountId"));
                            }
                        } catch (Exception ex) {
                            System.out.println("Exception : " + ex.getClass() + " /// " + ex.getMessage() );
                        }
                    }
                });



                for (JSONObject i : obj) {
                    String url = "http://" + User.ipAddress + ":8080/Final_WebServer/webresources/com.server.transactions/transaction?accountId=" + i.get("accountId");
                    is = new URL(url).openStream();
                    rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
                    sb = new StringBuilder();
                    while ((cp = rd.read()) != -1) {
                        sb.append((char) cp);
                    }


                    JSONArray ar = new JSONArray(sb.toString());

                    int errorcount=0,addcount=0;
                    for (int a = 0; a < ar.length(); a++) {

                        String account="";String date="";String desc="";String transId="";String type="";String amount="";
                        try{
                            JSONObject ac = ar.getJSONObject(a);
                            
                            account = i.getString("accountId");
                            System.out.println("account id: " + account);
                            date = ac.getString("transPostDate");
                            System.out.println("transPostDate: " + date);

                            transId = ac.getString("transId");
                            System.out.println("transId: " + transId);
                            type = ac.getJSONObject("transTypeId").getString("transTypeDesc");
                            System.out.println("type: " + type);
                            amount = ac.getString("transAmount");
                            System.out.println("amount: " + amount);
    
                        }
                        catch(Exception ex){
                            System.out.println("ADD EXCEPTION: "+ ex.getClass() + ex.getMessage());
                             account=""; date=""; transId=""; type=""; amount="";
                        }
                        if(date.equals(""))
                        {
                            System.out.println(++errorcount + "not added");
                        }
                        else {
                            date = date.substring(0,date.indexOf("T"));
                            transactionList.add(new String[]{account,date,desc,transId,type,amount});
                            System.out.println(++addcount + "added");

                        }
                    }

                    
                    accountList.add(transactionList);
                    is.close();
                }

            } catch (Exception ex) {
                System.out.println("ERROR: " + ex.getClass()+" : " + ex.getMessage());
            }

            return accountList;
        }

        @Override
        protected void onPostExecute(final List<List<String[]>> t) {

            Spinner spinner = (Spinner) findViewById(R.id.spinner_accounts_history);
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

              @Override
              public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String selected = parent.getSelectedItem().toString();
                  TableLayout tl = (TableLayout) findViewById(R.id.table_layout);
                  if (tl.getChildCount()>2)
                  tl.removeAllViews();
                  System.out.println("selected: "+ selected);
                    for(List<String[]>trans : t){
                        if(trans.get(0)[0].equals(selected))
                        {
                            for(int i=0;i<trans.size();i++)
                            {
                                TableRow tr = new TableRow(TranscationHistory.this);
                                for(int j=1; j<trans.get(i).length;j++){
                                    tv = new TextView(TranscationHistory.this);
                                    tv.setText(trans.get(i)[j]);
                                    tr.addView(tv,new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
                                }
                                tb.addView(tr);

                            }


                            break;
                        }

                    }
              }

              @Override
              public void onNothingSelected(AdapterView<?> parent) {

              }
          });


        }

    }
}
