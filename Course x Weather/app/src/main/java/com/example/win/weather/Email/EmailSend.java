package com.example.win.weather.Email;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.win.weather.R;

public class EmailSend extends Activity {

    private Button send;
    private EditText content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_send);

        send = (Button) findViewById(R.id.send);
        content = (EditText) findViewById(R.id.content);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String problem = content.getText().toString();
                Intent intent = new Intent(EmailSend.this,Login.class);
                intent.putExtra("content",problem);
                startActivity(intent);
            }
        });
    }
}
