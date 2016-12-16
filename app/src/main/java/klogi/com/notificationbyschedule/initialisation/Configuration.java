package klogi.com.notificationbyschedule.initialisation;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import klogi.com.notificationbyschedule.R;
import klogi.com.notificationbyschedule.model.DBHandler;
import klogi.com.notificationbyschedule.model.ServerConnection;

/**
 * Created by alaeddine on 28/09/16.
 */
public class Configuration extends AppCompatActivity {
    EditText urlserver=null,portserver=null,profondeur=null;
    String newServer="",newPort="",newProfondeur="";
    Button sauvgarderConfigServer=null,buttonAddNewRowForIpSecond=null;
    Context context=null;
    DBHandler dbHandler=null;
    TableLayout tableLayoutConfiguration=null;
    private int idServerSecondaire=0;
    LinearLayout.LayoutParams mLparams=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.configuration);
        context=this.getBaseContext();
        mLparams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        urlserver=(EditText)findViewById(R.id.urlServerInput);
        portserver=(EditText)findViewById(R.id.serverPortIntout);
        profondeur=(EditText)findViewById(R.id.profondeur);
        sauvgarderConfigServer=(Button)findViewById(R.id.sauvgarderConfigServer);
        buttonAddNewRowForIpSecond=(Button)findViewById(R.id.buttonAddNewRowForIpSecond);
        tableLayoutConfiguration=(TableLayout)findViewById(R.id.tableLayoutConfiguration);
        dbHandler= new DBHandler(this.getBaseContext());
        ServerConnection serverConnection=dbHandler.getServer();
        if(serverConnection!=null){
            urlserver.setText(serverConnection.getIpServer());
            profondeur.setText(serverConnection.getProfondeurServer());
            portserver.setText(serverConnection.getPortServer());
            displayServerSecondaire(serverConnection.getIpServerSecondaire());
        }
        buttonAddNewRowForIpSecond.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                LinearLayout linearLayout1Horizontale = new LinearLayout(context);
                linearLayout1Horizontale.setOrientation(LinearLayout.HORIZONTAL);
                linearLayout1Horizontale.setLayoutParams(mLparams);
                linearLayout1Horizontale.setVisibility(View.VISIBLE);

                TableRow tableRow = new TableRow(context);
                TextView textView  = new TextView(context);
                textView.setText(":");
                textView.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);

                textView.setTextColor(Color.BLACK);


                EditText editTextIpServer = new EditText(context);
                editTextIpServer.setTextColor(Color.BLACK);
                editTextIpServer.setHintTextColor(Color.GRAY);
                editTextIpServer.setHint("IP secondaire");
                editTextIpServer.setEms(10);

                editTextIpServer.setInputType(InputType.TYPE_CLASS_TEXT);
                editTextIpServer.setId(idServerSecondaire++);

                EditText editTextPort = new EditText(context);
                editTextPort.setTextColor(Color.BLACK);
                editTextPort.setHintTextColor(Color.GRAY);
                editTextPort.setHint("Port         ");
                editTextPort.setEms(10);

                editTextPort.setInputType(InputType.TYPE_CLASS_TEXT);
                editTextPort.setId(idServerSecondaire++);



               //linearLayout1Horizontale.addView(textView);
                //linearLayout1Horizontale.addView(editTextPort);




                tableRow.addView(editTextIpServer);
                tableRow.addView(editTextPort);


                tableLayoutConfiguration.addView(tableRow,5+(idServerSecondaire/2));

            }
        });
        sauvgarderConfigServer.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                 newServer=urlserver.getText().toString().trim();
                 newProfondeur=profondeur.getText().toString().trim();
                if(portserver.getText().toString().trim().equals("")){
                    newPort="80";
                }else {
                    newPort =portserver.getText().toString().trim();
                }
                dialogeSauvgarderModification();
            }
        });
    }
    public void displayServerSecondaire(String serversSecondaire){
        if(!serversSecondaire.equals("")) {
            String[] tabServerScondaires = serversSecondaire.toString().trim().split(" ");
            for (idServerSecondaire = 0; idServerSecondaire < tabServerScondaires.length; idServerSecondaire+=2) {
                LinearLayout linearLayout1Horizontale = new LinearLayout(context);
                linearLayout1Horizontale.setOrientation(LinearLayout.HORIZONTAL);
                linearLayout1Horizontale.setLayoutParams(mLparams);

                TableRow tableRow = new TableRow(context);
                TextView textView = new TextView(context);
                textView.setText(":");
                textView.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);

                  textView.setTextColor(Color.BLACK);
                EditText editTextIpServer = new EditText(context);
                editTextIpServer.setTextColor(Color.BLACK);
                editTextIpServer.setHintTextColor(Color.GRAY);
                editTextIpServer.setText(tabServerScondaires[idServerSecondaire]);
                editTextIpServer.setHint("IP secondaire");

                editTextIpServer.setInputType(InputType.TYPE_CLASS_TEXT);
                editTextIpServer.setId(idServerSecondaire);



                EditText editTextPort = new EditText(context);
                editTextPort.setTextColor(Color.BLACK);
                editTextPort.setHintTextColor(Color.GRAY);
                editTextPort.setText(tabServerScondaires[idServerSecondaire + 1]);
                editTextPort.setEms(10);
                editTextPort.setInputType(InputType.TYPE_CLASS_TEXT);
                editTextPort.setId(idServerSecondaire + 1);
                editTextPort.setHint("Port         ");
               // linearLayout1Horizontale.addView(textView);
              //  linearLayout1Horizontale.addView(editTextPort);
                tableRow.addView(editTextIpServer);
                tableRow.addView(editTextPort);
                tableLayoutConfiguration.addView(tableRow,6+(idServerSecondaire/2));

            }
        }

    }
    public void dialogeSauvgarderModification(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Voulez-vous sauvegarder les paramètres ?")
                .setCancelable(false)
                .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //finish();
                        dbHandler.deleteServer();
                        String ipServers ="";
                        if(idServerSecondaire>0) {
                            String port="";
                            for (int indexIpServerSecondaire = 0; indexIpServerSecondaire < idServerSecondaire; indexIpServerSecondaire+=2) {
                                EditText editTextIpServerSecondaire = (EditText) findViewById(indexIpServerSecondaire);
                                EditText editTextPortSecondaire = (EditText) findViewById(indexIpServerSecondaire+1);

                         if(!editTextIpServerSecondaire.getText().toString().trim().equals("")){
                             if(editTextPortSecondaire.getText().toString().trim().equals("")){
                                 port="80";
                             }else{
                                 port=editTextPortSecondaire.getText().toString().trim();
                             }
                                ipServers +=" "+editTextIpServerSecondaire.getText().toString().trim() + " "+port;
                            }
                            }
                        }
                        ServerConnection serverConnection = new ServerConnection(newServer, newPort, newProfondeur,ipServers.trim());
                            dbHandler.addServer(serverConnection);

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
}
