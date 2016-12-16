package klogi.com.notificationbyschedule;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import klogi.com.notificationbyschedule.model.DBHandler;
import klogi.com.notificationbyschedule.model.GPS;
import klogi.com.notificationbyschedule.model.ServerConnection;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
/**
 * Created by alaeddine on 20/09/16.
 */

public class AffichageNotif extends  AppCompatActivity {
    String textNotif="",textObjet="",textUrl="",encodedImage="",id="";
    TextView messageText=null,urlText=null,objetText=null,labelUrl=null,datenotif=null;
    ImageView imageView=null;
    ArrayList<ServerConnection> urlListIpandPortServer;
    public static DBHandler db=null;
    private LocationManager locationManager;
    private LocationListener locationListener;

    String longitude="0.0",latitude="0.0",releveHoraire="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.affichagenotif);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                try {
                    db.deleteGPS();
                    db.addGPS(new GPS(location.getLongitude() + "", location.getLatitude() + "",getDateTimeforVuNotif()));

                }catch (Exception e){

                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        };
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,

                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.INTERNET
                }, 10);


                return;
            }
        } else {
            locationManager.requestLocationUpdates("gps", 1000, 0, locationListener);

        }

        urlListIpandPortServer=new ArrayList<ServerConnection>();
        labelUrl=(TextView)findViewById(R.id.labelUrl);
        labelUrl.setVisibility(View.VISIBLE);
        id= getIntent().getExtras().getString("id");
        textObjet= getIntent().getExtras().getString("objet");
        textNotif = getIntent().getExtras().getString("notifText");
        textUrl = getIntent().getExtras().getString("url");
        encodedImage=getIntent().getExtras().getString("image");

        objetText=(TextView)findViewById(R.id.objetText);
        messageText=(TextView)findViewById(R.id.messageText);
        urlText=(TextView)findViewById(R.id.urlText);
        imageView=(ImageView)findViewById(R.id.imageViewPrincipal);
        datenotif=(TextView)findViewById(R.id.datenotif);
        datenotif.setText(getDateTime());
        objetText.setText(textObjet);
        messageText.setText(textNotif);
        urlText.setText(textUrl);

        byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        imageView.setImageBitmap(decodedByte);
 if(textUrl.equals("")) {
     labelUrl.setVisibility(View.GONE);
 }                db = new DBHandler(this.getBaseContext());
        stockeServerSecondaire();
        new HttpAsyncTaskTestServerConnection(this).execute(urlListIpandPortServer);




    }
    public JSONObject GET(String url){


        InputStream inputStream = null;
        JSONObject result = null;
        try {

            // create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // make GET request to the given URL
            HttpResponse httpResponse = httpclient.execute(new HttpGet(url));

            // receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            // convert inputstream to string
            if (inputStream != null)
                result = convertInputStreamToString(inputStream);


        } catch (Exception e) {
            Log.d("Exception", "vérifier votre connexion serveur  !");
        }


        if(result!=null){
             return result;
        }



        return null;
    }

    // convert inputstream to String
    private static JSONObject convertInputStreamToString(InputStream inputStream) throws IOException {
        JSONObject jObj=null;
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        try {
            jObj = new JSONObject(result);
        }catch (JSONException e){

        }
        return jObj;

    }
    private class HttpAsyncTask extends AsyncTask<String, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(String... urls) {

            return GET(urls[0]);
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(JSONObject result) {

        }
    }
    public  String getDateTime(){
        Calendar c = Calendar.getInstance();
        SimpleDateFormat dateformat = new SimpleDateFormat("dd MMM yyyy HH:mm:ss ");
        String datetime = dateformat.format(c.getTime());
        return datetime;
    }
    public  String getDateTimeforVuNotif(){
        Calendar c = Calendar.getInstance();
        SimpleDateFormat dateformat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        String datetime = dateformat.format(c.getTime());
        return datetime;
    }

    private class HttpAsyncTaskTestServerConnection extends AsyncTask<ArrayList<ServerConnection>,String, ServerConnection> {
        Activity activity;
        public HttpAsyncTaskTestServerConnection(Activity activity) {


        }

        @Override
        protected ServerConnection doInBackground(ArrayList<ServerConnection>... urls) {

            return  isURLReachable(urls[0]);
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(ServerConnection serverConnection) {




             if(serverConnection==null){
                 // ici le client notification, émet une notification à lui même pour indiquer qu'il n ' y a pas de serveur de notification en ligne.
                Log.d("setNotificationVue : ","Server Indisponible !");
             }else {
                String ptfndr=serverConnection.getProfondeurServer();

                if (!ptfndr.equals("")){
                    ptfndr="/"+ptfndr;
                }
                String prt=serverConnection.getPortServer();
                if (!prt.equals("")){
                    prt=":"+prt;
                }

                 GPS gps=db.getGPS();

if(gps!=null){
    longitude=gps.getLongitude();latitude=gps.getLatitude(); releveHoraire=gps.getReleveHoraire();
}
                 new HttpAsyncTask().execute("http://"+serverConnection.getIpServer()+ ptfndr+prt+"/setNnotificationVu.php?id=" +id+"&longitude="+longitude+"&latitude="+latitude+"&releveHoraire="+releveHoraire+"");

             }
        }
    }


    public ServerConnection isURLReachable(ArrayList<ServerConnection> urls) {
        int indexx=0;
        while (indexx<urls.size()) {



            try {

                URL myUrl = new URL("http://"+urls.get(indexx).getIpServer());
                URLConnection connection = myUrl.openConnection();
                connection.setConnectTimeout(30000);
                connection.connect();
                return urls.get(indexx);

            } catch (Exception e) {
                // Handle your exceptions
                //return null;
            }


            indexx++;
        }
        return null;
    }
    public void stockeServerSecondaire() {
        ServerConnection serverConnection = db.getServer();
        String ptfndr = serverConnection.getProfondeurServer();
        String prt = serverConnection.getPortServer();
        String ipServerPrincipale = serverConnection.getIpServer();
        urlListIpandPortServer.add(new ServerConnection(ipServerPrincipale, prt, ptfndr));
        String ipandPortServersSecondaire = serverConnection.getIpServerSecondaire();



        String[] tabServerScondaires = ipandPortServersSecondaire.toString().trim().split(" ");

        if (tabServerScondaires.length >1) {
            for (int idServerSecondaire = 0; idServerSecondaire < tabServerScondaires.length; idServerSecondaire += 2) {
                urlListIpandPortServer.add(new ServerConnection(tabServerScondaires[idServerSecondaire],tabServerScondaires[idServerSecondaire+1],ptfndr));
                //   Toast.makeText(cntx, tabServerScondaires[idServerSecondaire] + "ok", Toast.LENGTH_LONG).show();
            }
        }
    }




    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case 10:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                }
        }
    }

}



