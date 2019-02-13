package com.example.win.weather.Email;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.win.weather.MainActivity;
import com.example.win.weather.R;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class Login extends Activity {

    private EditText emailEdit;
    private EditText passwordEdit;
    private Button login;
    private SharedPreferences auth;
    private SharedPreferences.Editor editor;
    private CheckBox rememberPass;
    private String content;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        login = (Button)findViewById(R.id.button);
        emailEdit = (EditText) findViewById(R.id.name);
        passwordEdit = (EditText) findViewById(R.id.password);
        rememberPass = (CheckBox) findViewById(R.id.remember_pass);
        auth = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isRemebered = auth.getBoolean("remember_pass", false);

        Intent intent = getIntent();
        content = intent.getStringExtra("content");

        if(isRemebered) {
            String account = auth.getString("email", "");
            String password = auth.getString("password", "");
            emailEdit.setText(account);
            passwordEdit.setText(password);
            rememberPass.setChecked(true);
        }

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgressDialog();
                final String email = emailEdit.getText().toString();
                final String password = passwordEdit.getText().toString();
                String str = null;
                if(email.contains("qq.com")){
                    str = "smtp.qq.com";
                }else if(email.contains("sina.com")){
                    str = "smtp.sina.com.cn";
                }else if(email.contains("sohu.com")){
                    str = "smtp.sohu.com";
                }else if(email.contains("163.com")){
                    str = "smtp.163.com";
                }else if(email.contains("126.com")){
                    str = "smtp.126.com";
                }
                final String host = str;
                Properties props = new Properties();
                props.setProperty("mail.transport.protocol", "smtp");
                props.setProperty("mail.smtp.host", host);
                props.setProperty("mail.smtp.port", "25");
                final Session session = Session.getDefaultInstance(props);
                session.setDebug(true);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Transport transport = session.getTransport();
                            transport.connect(host, email, password);
                            Message message = createEmail(session, email, content);
                            transport.sendMessage(message, message.getAllRecipients());
                            transport.close();
                            editor = auth.edit();
                            if(rememberPass.isChecked()) {
                                editor.putBoolean("remember_pass",true);
                                editor.putString("email",email);
                                editor.putString("password",password);
                            }
                            else {
                                editor.clear();
                            }
                            editor.commit();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    closeProgressDialog();
                                }
                            });
                            Intent intent = new Intent(Login.this, MainActivity.class);
                            startActivity(intent);
                        } catch (NoSuchProviderException e) {
                            e.printStackTrace();
                            Toast.makeText(Login.this, "provider wrong", Toast.LENGTH_SHORT).show();
                        } catch (MessagingException e) {
                            e.printStackTrace();
                            Toast.makeText(Login.this, "messagingException", Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();

            }
        });
    }
    public static Message createEmail(Session session,String sender,String content)throws Exception {
        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(sender));
        message.setRecipient(Message.RecipientType.TO, new InternetAddress("zjw914@163.com"));
        message.setSubject("问题反馈");
        message.setContent(content, "text/html;charset=UTF-8");
        return message;
    }
    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }
    private void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
            Toast.makeText(Login.this,"send successfully",Toast.LENGTH_SHORT).show();
        }
    }
}