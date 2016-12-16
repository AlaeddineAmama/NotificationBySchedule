package klogi.com.notificationbyschedule;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableRow;
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
import java.util.ArrayList;
import java.util.Random;

import klogi.com.notificationbyschedule.broadcast_receivers.NotificationEventReceiver;
import klogi.com.notificationbyschedule.initialisation.Autentificationadmin;
import klogi.com.notificationbyschedule.model.DBHandler;
import klogi.com.notificationbyschedule.model.ServerConnection;
import klogi.com.notificationbyschedule.model.Users;

/**
 * Created by alaeddine on 23/09/16.
 */
public class RunLyout extends AppCompatActivity {
    EditText loginInput,passwordInput;
    Button buttonConnexion,buttonConfiguration;
    Intent intentMain,AutentificationAdmin;
    Context cntx;
    String loginText="", passwordText="";
    public ProgressDialog dialog=null;
    public static DBHandler  db=null;
    ArrayList<ServerConnection> urlListIpandPortServer;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.runlayout);
        cntx=this.getBaseContext();
        loginInput=(EditText)findViewById(R.id.loginTextInput);
        passwordInput=(EditText)findViewById(R.id.passwordInputText);
        buttonConfiguration=(Button)findViewById(R.id.buttonConfiguaration);
        urlListIpandPortServer=new ArrayList<ServerConnection>();
        AutentificationAdmin = new Intent(this,Autentificationadmin.class);



        buttonConfiguration.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(AutentificationAdmin);
         }
        });





        buttonConnexion=(Button)findViewById(R.id.buttonConnect);
        db = new DBHandler(cntx);

        intentMain = new Intent(this,MainActivity.class);
        buttonConnexion.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(isConnected()) {
                db.deleteUser();// delete all users ;
                loginText=loginInput.getText().toString().trim();
                     passwordText=passwordInput.getText().toString().trim();

                  authentificationUser(loginText, passwordText);
                    }else
                    {

                        alertDialogMessagwithOKButton("La connexion a échoué","Désolé, impossible de se connecter. Veuillez vérifier votre connexion ou réessayer plus tard.");
                    }


            }
        });
    }
    public  void authentificationUser(String login,String password){



        if(login.equals("")||password.equals("")){
            huniINvalidInput();


        }else {

            Users user=db.getUserByLoginAndPassword(login,MD5(password));
            if(user!=null){

                intentMain.putExtra("login", login);
                intentMain.putExtra("password", MD5(password));
                startActivity(intentMain);
                finish();

            }else{
                 stockeServerSecondaire();
                 new HttpAsyncTaskTestServerConnection(this).execute(urlListIpandPortServer);



                    }
        }

    }

    private class HttpAsyncTaskTestServerConnection extends AsyncTask<ArrayList<ServerConnection>,String, ServerConnection> {
        Activity activity;
        public HttpAsyncTaskTestServerConnection(Activity activity) {
            this.activity=activity;
            dialog = new ProgressDialog(activity);

        }
        @Override
        protected void onPreExecute() {

            dialog.setMessage("veuillez patienter ...");
            dialog.show();
        }
        @Override
        protected ServerConnection doInBackground(ArrayList<ServerConnection>... urls) {

            return  isURLReachable(urls[0]);
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(ServerConnection serverConnection) {


            if (dialog.isShowing()  ) {
                dialog.dismiss();
            }
            if(serverConnection==null){
                alertDialogMessagwithOKButton("Serveur indisponible ","Connexion au serveur est echoué ...");
            }else {
                String ptfndr=serverConnection.getProfondeurServer();

                if (!ptfndr.equals("")){
                    ptfndr="/"+ptfndr;
                }
                String prt=serverConnection.getPortServer();
                if (!prt.equals("")){
                    prt=":"+prt;
                }
                new HttpAsyncTask(activity).execute("http://" + serverConnection.getIpServer() + prt + ptfndr+"/get_User.php?user=" + loginText + "&password=" + MD5(passwordText));

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

    public String MD5(String md5) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] array = md.digest(md5.getBytes());
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1,3));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
        }

        return null;
    }

   public void  alertDialogMessagwithOKButton(String title,String message){
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();

        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        //alertDialog.setIcon(R.drawable.welcome);

        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialoge, int which) {
                //Toast.makeText(getApplicationContext(), "You clicked on OK", Toast.LENGTH_SHORT).show();
                try {

                } catch (Exception e) {
                    dialog.dismiss();
                }

            }
        });
        alertDialog.show();
    }

    public void huniINvalidInput(){
        loginInput.setText("");
        loginInput.setHintTextColor(Color.RED);
        loginInput.setHint("login ou mot de passe invalide");
        passwordInput.setText("");


    }
    public  JSONObject GET(String url){


        InputStream inputStream = null;
        JSONObject result = null;
        try {

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
            // return result.getString("notif");
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
    public boolean isConnected(){
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        }else{
            Log.d("Exception","vérifier votre connexion  !");
            return false;
        }
    }
    public void stockeServerSecondaire() {
        try {
            ServerConnection serverConnection = db.getServer();
            String ptfndr = serverConnection.getProfondeurServer();
            String prt = serverConnection.getPortServer();
            String ipServerPrincipale = serverConnection.getIpServer();
            urlListIpandPortServer.add(new ServerConnection(ipServerPrincipale, prt, ptfndr));
            String ipandPortServersSecondaire = serverConnection.getIpServerSecondaire();


            String[] tabServerScondaires = ipandPortServersSecondaire.toString().trim().split(" ");

            if (tabServerScondaires.length > 1) {
                for (int idServerSecondaire = 0; idServerSecondaire < tabServerScondaires.length; idServerSecondaire += 2) {
                    urlListIpandPortServer.add(new ServerConnection(tabServerScondaires[idServerSecondaire], tabServerScondaires[idServerSecondaire + 1], ptfndr));
                    //   Toast.makeText(cntx, tabServerScondaires[idServerSecondaire] + "ok", Toast.LENGTH_LONG).show();
                }
            }
        }catch (Exception e){

        }
    }


    private class HttpAsyncTask extends AsyncTask<String, Void, JSONObject> {

        public HttpAsyncTask(Activity activity) {
            dialog = new ProgressDialog(activity);
        }
        @Override
        protected void onPreExecute() {

            dialog.setMessage("veuillez patienter ...");
            dialog.show();
        }
        @Override
        protected JSONObject doInBackground(String... urls) {

            return GET(urls[0]);
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(JSONObject result) {

            if(result!=null ){
                int success=0;
                try {
                    success=result.getInt("success");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (success == 1) {
                    try {

                        String login = result.getString("login");
                        String password = result.getString("password");

                        String hourWork = result.getString("hourWork");
                        String frequ = result.getString("freq");
                        String dateFin = result.getString("dateFin");
                        String modification = result.getString("modification");

                        Users usersFromServer = new Users(login, password, hourWork, frequ, dateFin, modification, "1");////////////////////////////////// Added ////////////
                        db.addUser(usersFromServer);
                        intentMain.putExtra("login", login);
                        intentMain.putExtra("password", password);
                        startActivity(intentMain);
                        finish();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }else {
                    huniINvalidInput();
                }

            }else {
                try {

                    alertDialogMessagwithOKButton("Serveur indisponible ","Connexion au serveur est echoué ...");
                }catch (Exception e){
                    Log.d("Exception :","RunLyout.dialogwithOKButton :is not valid; is your activity running?");
                }
            }
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }
    }

}
