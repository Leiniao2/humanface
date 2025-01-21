package com.example.stackoverflowprofileavatar;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Bundle;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    ListView listView;
    ListAdapter lAdapter;
    ArrayList<String> idList = new ArrayList<String>();
    ArrayList<String> photoList = new ArrayList<String>();
    ArrayList<String> isFaceList = new ArrayList<String>();

    private final static String URL = "https://api.stackexchange.com/2.2/users?site=stackoverflow";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        new FetchDataTask().execute(URL);
    }

    private class FetchDataTask extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... params) {

            InputStream inputStream = null;
            String result= null;
            HttpClient client = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(params[0]);

            try {

                HttpResponse response = client.execute(httpGet);
                inputStream = response.getEntity().getContent();

                // convert inputstream to string
                if(inputStream != null){
                    result = convertInputStreamToString(inputStream);
                    Log.i("App", "Data received:" +result);

                }
                else
                    result = "Failed to fetch data";

                return result;

            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String dataFetched) {
            //parse the JSON data and then display
            parseJSON(dataFetched);
        }


        private String convertInputStreamToString(InputStream inputStream) throws IOException{
            BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
            String line = "";
            String result = "";
            while((line = bufferedReader.readLine()) != null)
                result += line;

            inputStream.close();
            return result;

        }

        private void parseJSON(String data){

            try{
                JSONObject main = new JSONObject(data);
                JSONArray users = main.getJSONArray("items");

                int jsonArrLength = users.length();
                jsonArrLength = Math.min(jsonArrLength, 10);

                for(int i=0; i < jsonArrLength; i++) {
                    JSONObject jsonChildNode = users.getJSONObject(i);
                    String accountId = jsonChildNode.getString("account_id");
                    String photoUrl = jsonChildNode.getString("profile_image");

                    idList.add(accountId);
                    isFaceList.add("");
                    photoList.add(photoUrl);
                }

                // Get ListView object from xml
                listView = (ListView) findViewById(R.id.list);

                // Define a new Adapter
                lAdapter = new ListAdapter(MainActivity.this, idList, isFaceList, photoList);

                // Assign adapter to ListView
                listView.setAdapter(lAdapter);

            }catch(Exception e){
                Log.i("App", "Error parsing data" +e.getMessage());

            }
        }
    }

}