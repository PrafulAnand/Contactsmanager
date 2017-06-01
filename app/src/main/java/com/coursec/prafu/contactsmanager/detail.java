package com.coursec.prafu.contactsmanager;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.widget.ImageView;
import android.widget.TextView;

public class detail extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ImageView img=(ImageView)findViewById(R.id.userpic);
        CardView c=(CardView)findViewById(R.id.card_detailsview);
        TextView t1=(TextView)findViewById(R.id.username);
        TextView t2=(TextView)findViewById(R.id.user_detail);
        String name_user=getIntent().getExtras().getString("name");
        t1.setText(name_user);
        String detailofuser=getIntent().getExtras().getString("details");
        t2.setText(detailofuser);
        Bundle extras = getIntent().getExtras();
        Bitmap bmp = (Bitmap) extras.getParcelable("imagebitmap");
        img.setImageBitmap(bmp );
    }
}
