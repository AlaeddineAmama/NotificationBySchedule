package klogi.com.notificationbyschedule;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;


import klogi.com.notificationbyschedule.broadcast_receivers.NotificationEventReceiver;
import klogi.com.notificationbyschedule.model.DBHandler;
import klogi.com.notificationbyschedule.model.GPS;
import klogi.com.notificationbyschedule.model.Users;
import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/***
 * Created by Alaeddine Amama
 *
 * One-pager app which sends the notification every 2 hours. This is the main Activity,
 * whose only purpose is to provide a button to start the notification service
 */
public class MainActivity extends AppCompatActivity {

    public  Spinner  spinnerPlanification=null;

    ArrayAdapter<CharSequence> adapterSpinnerPlanification;
    private Switch switchAcquisition;
    TextView textView=null;
    public  TimePicker timePickerBegin=null,timePickerEnd=null;
    public  int  switchAcquisitionValue=1;
    DBHandler db=null;
    Users user=null;
    String login=null;
    String password=null;
    private LocationManager locationManager;
    private LocationListener locationListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView=(TextView)findViewById(R.id.textView5);
        spinnerPlanification=(Spinner) findViewById(R.id.spinnerPlanification);
        switchAcquisition=(Switch) findViewById(R.id.switchAcquisition);
        timePickerBegin =(TimePicker)findViewById(R.id.timePickerBegin);
        timePickerEnd =(TimePicker)findViewById(R.id.timePickerEnd);
        login= getIntent().getExtras().getString("login");
        password = getIntent().getExtras().getString("password");
    //    db = RunLyout.getDb();
        db = new DBHandler(this);
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


         user=db.getUserByLoginAndPassword(login, password);
        NotificationEventReceiver.setupAlarm(getApplicationContext());
     //   Toast.makeText(this.getBaseContext(),"ok "+user.toString(),Toast.LENGTH_LONG).show();
        //intitilisation hourBegin on 07:00 and hourEnd on 19:00
       timePikerInit(user.getHourWork());
        //initilisation Acquisation des données
        switchAcquisitionInit();
      //initilisation pinnerPlanification : on 15 min
        spinnerPlanificationInit(user.getFreq());

    }

    public void timePikerInit(String hourWork){
        timePickerBegin.setIs24HourView(true);
        timePickerEnd.setIs24HourView(true);
        String[] hourWorkBeginAndEnd=hourWork.split("-");
        String timeBegin=hourWorkBeginAndEnd[0];
        String timeEnd=hourWorkBeginAndEnd[1];
        setTimePikerBeginAndEnd(timeBegin, timeEnd);
    }
    public void spinnerPlanificationInit(String frequ){
        adapterSpinnerPlanification=ArrayAdapter.createFromResource(this,R.array.planification,android.R.layout.simple_spinner_item);
        spinnerPlanification.setAdapter(adapterSpinnerPlanification);
        spinnerPlanification.setSelection(Integer.parseInt(frequ));

    }
    public void switchAcquisitionInit() {
        if(user.getAcquisition().equals("1")){
         switchAcquisition.setChecked(true);}
        else {
            switchAcquisition.setChecked(false);
        }
         switchAcquisition.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

             @Override
             public void onCheckedChanged(CompoundButton buttonView,
                                          boolean isChecked) {

                 if (isChecked) {
                     textView.setText("Acquisition des données Act");
                     switchAcquisitionValue = 1;
                 } else {
                     textView.setText("Acquisition des données Dés");
                     switchAcquisitionValue = 0;
                 }

             }
         });
    }

    public void setTimePikerBeginAndEnd(String timePickerBeginTime,String timePickerEndTime){
        try {

        timePickerBegin.setCurrentHour(Integer.parseInt(timePickerBeginTime.substring(0,2)));
        timePickerBegin.setCurrentMinute(Integer.parseInt(timePickerBeginTime.substring(2, 4)));
        timePickerEnd.setCurrentHour(Integer.parseInt(timePickerEndTime.substring(0, 2)));
        timePickerEnd.setCurrentMinute(Integer.parseInt(timePickerEndTime.substring(2, 4)));
        }catch (Exception e){

        }

    }
  public  String  getSwitchAcquisitionValue() {
      try {

                  return switchAcquisitionValue+"";
      }catch (Exception e){

      }
      return null;
    }
    public   String  getHeurDebut() {
        try {


        String hour    = StringUtils.leftPad( Integer.toString(timePickerBegin.getCurrentHour()), 2, "0" );
        String minutes = StringUtils.leftPad( Integer.toString(timePickerBegin.getCurrentMinute()), 2, "0" );
       return hour+minutes;
        }catch (Exception e){

        }
        return null;

    }
    public  String  getHeurFin() {

        try{
        String hour    = StringUtils.leftPad( Integer.toString(timePickerEnd.getCurrentHour()), 2, "0" );
        String minutes = StringUtils.leftPad( Integer.toString(timePickerEnd.getCurrentMinute()), 2, "0" );
        return hour+minutes;
        }catch (Exception e){

        }
        return null;
    }
    public  String getPlanification() {
        try{

        return   spinnerPlanification.getSelectedItemPosition()+"";

        }catch (Exception e){

        }
        return null;
    }

    public void onSendNotificationsButtonClick(View view) {
      if (user.getModification().equals("true")){
          dialogeSauvgarderModification();
            }else {
          alertDialogMessag("pas d'autorisation !","Désolé. Vous n'avez pas la permission de modifier les paramètres !");
          //intitilisation hourBegin on 07:00 and hourEnd on 19:00
          timePikerInit(user.getHourWork());
          //initilisation Acquisation des données
          switchAcquisitionInit();
          //initilisation pinnerPlanification : on 15 min
          spinnerPlanificationInit(user.getFreq());

      }
        NotificationEventReceiver.setupAlarm(getApplicationContext());
    }
    public void dialogeSauvgarderModification(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Voulez-vous sauvegarder les paramètres ?")
                .setCancelable(false)
                .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //finish();
                        Users userUpdate=new Users(login,password,getHeurDebut()+"-"+getHeurFin(),getPlanification(),user.getDateFin(),user.getModification(),getSwitchAcquisitionValue());
                        db.updateUser(userUpdate);

                    }
                })
                .setNegativeButton("Non", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();


                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
    public void alertDialogMessag(String title,String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);

        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alertDialog.show();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    // To prevent crash on resuming activity  : interaction with fragments allowed only after Fragments Resumed or in OnCreate
    // http://www.androiddesignpatterns.com/2013/08/fragment-transaction-commit-state-loss.html
    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        // handleIntent();
    }
    public  String getDateTimeforVuNotif(){
        Calendar c = Calendar.getInstance();
        SimpleDateFormat dateformat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        String datetime = dateformat.format(c.getTime());
        return datetime;
    }
}
