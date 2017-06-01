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
        Bundle extras=null;
        extras = this.getIntent().getExtras();
        String name_user=extras.getString("name");
        String detailofuser=extras.getString("details");
        Bitmap bmp = (Bitmap) extras.getParcelable("imagebitmap");
        ImageView img=(ImageView)findViewById(R.id.userpic);
        CardView c=(CardView)findViewById(R.id.card_detailsview);
        TextView t1=(TextView)findViewById(R.id.username);
        TextView t2=(TextView)findViewById(R.id.user_detail);

        t1.setText(name_user);
        t2.setText(detailofuser);
        img.setImageBitmap(bmp );

    }
}
