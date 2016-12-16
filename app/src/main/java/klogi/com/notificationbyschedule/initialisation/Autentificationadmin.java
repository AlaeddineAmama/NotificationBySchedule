package klogi.com.notificationbyschedule.initialisation;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import klogi.com.notificationbyschedule.R;

/**
 * Created by alaeddine on 28/09/16.
 */
public class Autentificationadmin extends AppCompatActivity{
    EditText loginInput=null,passwordInput=null;
    Button connexionAdmin=null;
    Context context=null;
    Intent intentConfigurationServer=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.autentificationadmin);
        loginInput=(EditText)findViewById(R.id.loginInputAdmin);
        passwordInput=(EditText)findViewById(R.id.passwordInputAdmin);
        connexionAdmin=(Button)findViewById(R.id.connextionAdmin);
        context=this.getBaseContext();
         intentConfigurationServer= new Intent(this,Configuration.class);

          connexionAdmin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
             String login=loginInput.getText().toString().trim();
                String password=  passwordInput.getText().toString().trim();
              //  startActivity(intentConfigurationServer);
                  if(login.equals("") && password.equals("")){
                      huniINvalidInput();
                  }
                else if(login.equals("admin") && password.equals("admin")){

                      startActivity(intentConfigurationServer);
                      finish();
               }


                else {alertDialogMessagwithOKButton("Login ou mot de passe incorrecte","Login saisir ne semble appartenir à aucun compte d'Admin. Veuillez le vérifier et réessayer.");
                         }
            }
        });
       }
    public void huniINvalidInput(){
        loginInput.setText("");
        loginInput.setHintTextColor(Color.RED);
        loginInput.setHint("login ou mot de passe invalide");
        passwordInput.setText("");


    }
    public void  alertDialogMessagwithOKButton(String title, String message){
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();

        alertDialog.setTitle(title);
        alertDialog.setMessage(message);

        alertDialog.setButton("réessayer", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialoge, int which) {
                   try {

                }catch (Exception e){

                }

            }
        });
        alertDialog.show();
    }
    }
