package com.proyectos.ivanm.appunraf;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MainActivity extends Activity {

    TextView txtCiudad, txtTemp, txtHumedad, txtTiempo, txtDescripcion;
    ImageView ivIcono;
    Button btnConsultar;
    EditText etCiudad;

    public String readJSONFeed(String URL) {
        StringBuilder stringBuilder = new StringBuilder();
        HttpClient client = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(URL);
        try {
            HttpResponse response = client.execute(httpGet);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if (statusCode == 200) {
                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(content));
                String line;
                while ((line = reader.readLine()) != null) {
                    stringBuilder.append(line);
                }
            } else {
                Log.e("JSON", "Failed to download file");
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    private class ReadJSONFeedTask extends AsyncTask<String, Void, String> {
        protected String doInBackground(String... urls) {
            return readJSONFeed(urls[0]);
        }
        protected void onPostExecute(String result) {
            try {
                JSONObject jsonResult = new JSONObject(result);

                JSONObject jsonDatosClima = jsonResult.getJSONObject("main");


                //Toast.makeText(getBaseContext(), jsonDatosClima.getString("temp"),
                //        Toast.LENGTH_LONG).show();

                txtTemp.setText("Temperatura: " + jsonDatosClima.getString("temp") + "C°");
                txtHumedad.setText("Humedad: " + jsonDatosClima.getString("humidity") + "%");
                txtCiudad.setText(jsonResult.getString("name"));
                ivIcono.setImageResource(R.drawable.lluvia);


                JSONArray jsonArray = new JSONArray("weather");
                for(int i = 0 ; i < jsonArray.length(); i++)
                {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    // 

                    String id = jsonObject.getString("id");
                    String main = jsonObject.getString("main");
                    String description = jsonObject.getString("description");
                    String icon = jsonObject.getString("icon");
                }






            }catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        etCiudad = (EditText) findViewById(R.id.etCiudad);
        txtCiudad = (TextView) findViewById(R.id.cityText);
        txtTemp = (TextView) findViewById(R.id.txtTemp);
        txtHumedad = (TextView) findViewById(R.id.txtHumedad);
        ivIcono = (ImageView) findViewById(R.id.ivIcono);
        btnConsultar = (Button) findViewById(R.id.btnConsultar);

        txtDescripcion = (TextView) findViewById(R.id.txtDescripcion);
        txtTiempo = (TextView) findViewById(R.id.txtTiempo);



        etCiudad.setHint("Escribir ciudad");
        btnConsultar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ReadJSONFeedTask().execute(
                        "http://api.openweathermap.org/data/2.5/weather?q="+etCiudad.getText().toString().replace(" ", "+")+",ar&units=metric&appid=14fe3eb5c45bf0b3fd2ff96c12710d29");
            }
        });



    }


}