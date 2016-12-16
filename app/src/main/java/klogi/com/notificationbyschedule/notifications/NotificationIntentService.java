package klogi.com.notificationbyschedule.notifications;

import android.Manifest;
import android.app.Activity;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.InputStream;
import klogi.com.notificationbyschedule.AffichageNotif;
import klogi.com.notificationbyschedule.R;
import klogi.com.notificationbyschedule.broadcast_receivers.NotificationEventReceiver;
import klogi.com.notificationbyschedule.model.DBHandler;
import klogi.com.notificationbyschedule.model.GPS;
import klogi.com.notificationbyschedule.model.ServerConnection;
import klogi.com.notificationbyschedule.model.Users;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;





/**
 * Created by Alaeddine Amamama
 *
 *
 */
public class NotificationIntentService extends IntentService {
    public static   DBHandler db =null;
    static Users users=null;
    private static  int NOTIFICATION_ID = 1;
    private static final String ACTION_START = "ACTION_START";
    private static final String ACTION_DELETE = "ACTION_DELETE";
    ArrayList<ServerConnection> urlListIpandPortServer;


    public NotificationIntentService() {
        super(NotificationIntentService.class.getSimpleName());
        urlListIpandPortServer=new ArrayList<ServerConnection>();
         }

    public static Intent createIntentStartNotificationService(Context context) {
     db=new DBHandler(context);
        users=db.getUser();
        Intent intent = new Intent(context, NotificationIntentService.class);
        intent.setAction(ACTION_START);
        return intent;
    }

    public static Intent createIntentDeleteNotification(Context context) {
        db=new DBHandler(context);
        Intent intent = new Intent(context, NotificationIntentService.class);
        intent.setAction(ACTION_DELETE);
        return intent;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(getClass().getSimpleName(), "onHandleIntent, started handling a notification event");
        try {
            String action = intent.getAction();
            if (ACTION_START.equals(action)) {
                processStartNotification();
            }
        } finally {
            WakefulBroadcastReceiver.completeWakefulIntent(intent);
        }
    }

    private void processDeleteNotification(Intent intent) {
        // Log something?
    }

    private void processStartNotification() {
      if(isPerfectConditionToNotification()) {

          stockeServerSecondaire();
          new HttpAsyncTaskTestServerConnection().execute(urlListIpandPortServer);

       }

    }
    public boolean isConnected(){
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {

            // ici le client notification, émet une notification à lui même pour indiquer que la connexion n'existe pas (wifi ou 3/4g)


              return true;
        }else{
            Users users = db.getUser();

            if ((Integer.parseInt(users.getAcquisition()) == 1)  && (isJobTime() == 1)&&(dateExpirationValidate(users.getDateFin())==1)){
            notificationLikeAlert("Aucune connexion Internet","impossible de se connecter. Veuillez vérifier votre connexion ");
            }
             return false;
        }
    }
    public boolean isConnectedForTest(){
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {

            // ici le client notification, émet une notification à lui même pour indiquer que la connexion n'existe pas (wifi ou 3/4g)


            return true;
        }else{
            return false;
        }
    }
    private class HttpAsyncTask extends AsyncTask<String, Void, JSONArray> {
        @Override
        protected JSONArray doInBackground(String... urls) {

            return GET(urls[0]);
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(JSONArray result) {
            if(result!=null){
              Toast.makeText(getBaseContext(), "Vous avez une nouvelle notification  !", Toast.LENGTH_LONG).show();
                createNotification(result);

           }
        }
    }
    public void createNotification(JSONArray jsonArray){
        for (int indexObject=0;indexObject<jsonArray.length();indexObject++){
            JSONObject   jsonObjectResult= null;
            try {
                jsonObjectResult = jsonArray.getJSONObject(indexObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String id="", objet="",notifText="",url="",image="",sound="0",vibrating="0",lumiere="0";
            int success=0;
        try {
            success=jsonObjectResult.getInt("success");
        } catch (JSONException e) {
            e.printStackTrace();
        }
       if(success==1) {
           try {

                id=jsonObjectResult.getString("id");
                 objet=jsonObjectResult.getString("objet");
                notifText=jsonObjectResult.getString("notif");
                url=jsonObjectResult.getString("url");
                image=jsonObjectResult.getString("image");
                sound=jsonObjectResult.getString("sound");
                vibrating=jsonObjectResult.getString("vibrating");
                lumiere=jsonObjectResult.getString("lumiere");
               if (vibrating.equals("1")&&sound.equals("1")&&lumiere.equals("1")){
                   objet="Urgent, "+objet;
               }
            } catch (JSONException e) {
                e.printStackTrace();
            }
           /*"This notification has been triggered by Notification Service"*/
                NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
                builder.setContentTitle(objet)
                        .setAutoCancel(true)
                        .setTicker(objet)
                        .setLights(Color.RED, 1, 500)
                        .setColor(getResources().getColor(R.color.colorAccent))
                        .setContentText(notifText)
                        .setSmallIcon(R.drawable.notiflogo);
                if(vibrating.equals("1")){
                    builder.setVibrate(new long[]{0, 500, 100, 500, 100, 500});
                }
                if (sound.equals("1")){
                    builder.setSound(Uri.withAppendedPath(MediaStore.Audio.Media.INTERNAL_CONTENT_URI, "2"));
                }


                Intent mainIntent = new Intent(this, AffichageNotif.class);
                mainIntent.putExtra("id", id);
                mainIntent.putExtra("objet", objet);
                mainIntent.putExtra("notifText", notifText);
                mainIntent.putExtra("image", image);
                mainIntent.putExtra("url", url);

                int min = 1;
                int max = 5000;
                Random r = new Random();

                int NOTIFICATION_ID = r.nextInt(max - min + 1) + min;
                PendingIntent pendingIntent = PendingIntent.getActivity(this,
                        NOTIFICATION_ID,
                        mainIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
                builder.setContentIntent(pendingIntent);
                builder.setDeleteIntent(NotificationEventReceiver.getDeleteIntent(this));

                NotificationManager manager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
                manager.notify(NOTIFICATION_ID, builder.build());
            }
            else if (success==0){
                try {
                    Log.d("Exception :",jsonObjectResult.getString("message"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
    }
    }
    public  JSONArray GET(String url){


            InputStream inputStream = null;
            JSONArray result = null;
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
    private static JSONArray convertInputStreamToString(InputStream inputStream) throws IOException{
        JSONArray jObj=null;
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        try {
            jObj = new JSONArray(result);
        }catch (JSONException e){

        }
        return jObj;

    }
    public int isJobTime(){
         int currentHour=Integer.parseInt(Calendar.getInstance().get(Calendar.HOUR_OF_DAY)+"");
        String hourWork=users.getHourWork();
         int debutheur=Integer.parseInt(hourWork.split("-")[0].substring(0, 2));
         int finHeur=Integer.parseInt(hourWork.split("-")[1].substring(0, 2));
         if(debutheur<finHeur){
            if (debutheur<=currentHour && currentHour<=finHeur){
               return 1;
            }
             else{
                return 0;
            }
         }else {
             if((debutheur<=currentHour&&currentHour<=23 )||(0<=currentHour&&currentHour<=finHeur )){
                 return 1;
             }else {
                 return 0;
             }

         }
    }
    public boolean isPerfectConditionToNotification(){
        try {

            Users users = db.getUser();

            if ((Integer.parseInt(users.getAcquisition()) == 1) &&( isConnected() && isJobTime() == 1)&&(dateExpirationValidate(users.getDateFin())==1)) {
                return true;
            } else {
                return false;
            }
        }catch (Exception e){
            Log.d("Exception", "error in isPerfectConditionToNotification :Users.getAcquisition()' on a null object reference ");
        }
        return false;
    }
    public  int dateExpirationValidate(String dateExpiration){
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date strDate=null;
        try {
            strDate = sdf.parse(dateExpiration);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        if (new Date().after(strDate)) {
            Log.d("dateExpirationValidate","0");
            dialogwithOKButton("La date du certificat a expiré ou il n'est plus valide ...");
            return 0;

        }else {
            Log.d("dateExpirationValidate","1");

            return 1;
        }

    }

    public void dialogwithOKButton(String message) {
        Toast.makeText(this.getBaseContext(),message , Toast.LENGTH_LONG).show();
    }
    public void stockeServerSecondaire() {
        urlListIpandPortServer.clear();
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
    private class HttpAsyncTaskTestServerConnection extends AsyncTask<ArrayList<ServerConnection>,String, ServerConnection> {


        @Override
        protected ServerConnection doInBackground(ArrayList<ServerConnection>... urls) {

            return  isURLReachable(urls[0]);
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(ServerConnection serverConnection) {



            if(serverConnection==null && isConnectedForTest()==true ){
                // ici le client notification, émet une notification à lui même pour indiquer qu'il n ' y a pas de serveur de notification en ligne.
        notificationLikeAlert("Serveur indisponible","Connexion au serveur est echoué ...");


            }else {
                String ptfndr=serverConnection.getProfondeurServer();

                if (!ptfndr.equals("")){
                    ptfndr="/"+ptfndr;
                }
                String prt=serverConnection.getPortServer();
                if (!prt.equals("")){
                    prt=":"+prt;
                }

                new HttpAsyncTask().execute("http://" + serverConnection.getIpServer() + prt+ptfndr + "/get_notification.php?user=" + users.getLogin() + "&password=" + users.getPassword());

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
                Log.d("fuck","pas de serveur");

               // return null;
            }

            indexx++;

        }
        return null;
    }
    public  void  notificationLikeAlert(String title, String message) {
         /*"This notification has been triggered by Notification Service"*/
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setContentTitle(title)
                .setAutoCancel(true)
                .setTicker(title)
                .setLights(Color.RED, 1, 500)
                .setColor(getResources().getColor(R.color.colorAccent))
                .setContentText(message)
                .setSmallIcon(R.drawable.notiflogo)
                .setVibrate(new long[]{0, 500, 100, 500, 100, 500})
                .setSound(Uri.withAppendedPath(MediaStore.Audio.Media.INTERNAL_CONTENT_URI, "2"));



        Intent mainIntent = new Intent(this, AffichageNotif.class);

        int min = 1;
        int max = 5000;
        Random r = new Random();

        int NOTIFICATION_ID = r.nextInt(max - min + 1) + min;
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                NOTIFICATION_ID,
                mainIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        // builder.setContentIntent(pendingIntent);
        builder.setDeleteIntent(NotificationEventReceiver.getDeleteIntent(this));

        NotificationManager manager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(NOTIFICATION_ID, builder.build());


    }

}
