package com.coursec.prafu.contactsmanager;

import java.io.File;
import java.io.FileOutputStream;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    SimpleCursorAdapter mAdapter;
    MatrixCursor mMatrixCursor;
    ProgressDialog progressDialog;
    ListView lstContacts;
    String displayName="";
    String nickName="";
    String homePhone="";
    String mobilePhone="";
    String workPhone="";
    String photoPath="" + R.drawable.user;
    byte[] photoByte=null;
    String homeEmail="";
    String workEmail="";
    String companyName="";
    String title="";
    String details = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Contact Manager");
        // The contacts from the contacts content provider is stored in this cursor
        mMatrixCursor = new MatrixCursor(new String[] { "_id","name","photo","details"} );

        // Adapter to set data in the listview
        mAdapter = new SimpleCursorAdapter(getBaseContext(),
                R.layout.lv_layout,
                null,
                new String[] { "name","photo","details"},
                new int[] { R.id.tv_name,R.id.iv_photo,R.id.tv_details}, 0);

        // Getting reference to listview
         lstContacts = (ListView) findViewById(R.id.lst_contacts);

        // Setting the adapter to listview
        lstContacts.setAdapter(mAdapter);

        // Creating an AsyncTask object to retrieve and load listview with contacts
        ListViewContactsLoader listViewContactsLoader = new ListViewContactsLoader();

        // Starting the AsyncTask process to retrieve and load listview with contacts
        listViewContactsLoader.execute();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Fetching contacts...");
        progressDialog.setCancelable(false);
        progressDialog.show();

    }

    /** An AsyncTask class to retrieve and load listview with contacts */
    private class ListViewContactsLoader extends AsyncTask<Void, Void, Cursor>{

        @Override
        protected Cursor doInBackground(Void... params) {

            Uri contactsUri = ContactsContract.Contacts.CONTENT_URI;

            // Querying the table ContactsContract.Contacts to retrieve all the contacts
            Cursor contactsCursor = getContentResolver().query(contactsUri, null, null, null,
                    ContactsContract.Contacts.DISPLAY_NAME + " ASC ");

            if(contactsCursor.moveToFirst()){
                do{
                    long contactId = contactsCursor.getLong(contactsCursor.getColumnIndex("_ID"));

                    Uri dataUri = ContactsContract.Data.CONTENT_URI;

                    // Querying the table ContactsContract.Data to retrieve individual items like
                    // home phone, mobile phone, work email etc corresponding to each contact
                    Cursor dataCursor = getContentResolver().query(dataUri, null,
                            ContactsContract.Data.CONTACT_ID + "=" + contactId,
                            null, null);

                     displayName="";
                     nickName="";
                     homePhone="";
                     mobilePhone="";
                     workPhone="";
                     photoPath="" + R.drawable.user;
                     photoByte=null;
                     homeEmail="";
                     workEmail="";
                    companyName="";
                     title="";

                    if(dataCursor.moveToFirst()){
                        // Getting Display Name
                        displayName = dataCursor.getString(dataCursor.getColumnIndex(ContactsContract.Data.DISPLAY_NAME ));
                        do{

                            // Getting NickName
                            if(dataCursor.getString(dataCursor.getColumnIndex("mimetype")).equals(ContactsContract.CommonDataKinds.Nickname.CONTENT_ITEM_TYPE))
                                nickName = dataCursor.getString(dataCursor.getColumnIndex("data1"));

                            // Getting Phone numbers
                            if(dataCursor.getString(dataCursor.getColumnIndex("mimetype")).equals(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)){
                                switch(dataCursor.getInt(dataCursor.getColumnIndex("data2"))){
                                    case ContactsContract.CommonDataKinds.Phone.TYPE_HOME :
                                        homePhone = dataCursor.getString(dataCursor.getColumnIndex("data1"));
                                        break;
                                    case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE :
                                        mobilePhone = dataCursor.getString(dataCursor.getColumnIndex("data1"));
                                        break;
                                    case ContactsContract.CommonDataKinds.Phone.TYPE_WORK :
                                        workPhone = dataCursor.getString(dataCursor.getColumnIndex("data1"));
                                        break;
                                }
                            }

                            // Getting EMails
                            if(dataCursor.getString(dataCursor.getColumnIndex("mimetype")).equals(ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE ) ) {
                                switch(dataCursor.getInt(dataCursor.getColumnIndex("data2"))){
                                    case ContactsContract.CommonDataKinds.Email.TYPE_HOME :
                                        homeEmail = dataCursor.getString(dataCursor.getColumnIndex("data1"));
                                        break;
                                    case ContactsContract.CommonDataKinds.Email.TYPE_WORK :
                                        workEmail = dataCursor.getString(dataCursor.getColumnIndex("data1"));
                                        break;
                                }
                            }

                            // Getting Organization details
                            if(dataCursor.getString(dataCursor.getColumnIndex("mimetype")).equals(ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE)){
                                companyName = dataCursor.getString(dataCursor.getColumnIndex("data1"));
                                title = dataCursor.getString(dataCursor.getColumnIndex("data4"));
                            }

                            // Getting Photo
                            if(dataCursor.getString(dataCursor.getColumnIndex("mimetype")).equals(ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE)){
                                photoByte = dataCursor.getBlob(dataCursor.getColumnIndex("data15"));

                                if(photoByte != null) {
                                    Bitmap bitmap = BitmapFactory.decodeByteArray(photoByte, 0, photoByte.length);

                                    // Getting Caching directory
                                    File cacheDirectory = getBaseContext().getCacheDir();

                                    // Temporary file to store the contact image
                                    File tmpFile = new File(cacheDirectory.getPath() + "/wpta_"+contactId+".png");

                                    // The FileOutputStream to the temporary file
                                    try {
                                        FileOutputStream fOutStream = new FileOutputStream(tmpFile);

                                        // Writing the bitmap to the temporary file as png file
                                        bitmap.compress(Bitmap.CompressFormat.PNG,100, fOutStream);

                                        // Flush the FileOutputStream
                                        fOutStream.flush();

                                        //Close the FileOutputStream
                                        fOutStream.close();

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    photoPath = tmpFile.getPath();
                                }
                            }
                        }while(dataCursor.moveToNext());
                         details = "";

                        // Concatenating various information to single string
                        if(homePhone != null && !homePhone.equals("") )
                            details = "HomePhone : " + homePhone + "\n";
                        if(mobilePhone != null && !mobilePhone.equals("") )
                            details += "MobilePhone : " + mobilePhone + "\n";
                        if(workPhone != null && !workPhone.equals("") )
                            details += "WorkPhone : " + workPhone + "\n";
                        if(nickName != null && !nickName.equals("") )
                            details += "NickName : " + nickName + "\n";
                        if(homeEmail != null && !homeEmail.equals("") )
                            details += "HomeEmail : " + homeEmail + "\n";
                        if(workEmail != null && !workEmail.equals("") )
                            details += "WorkEmail : " + workEmail + "\n";
                        if(companyName != null && !companyName.equals("") )
                            details += "CompanyName : " + companyName + "\n";
                        if(title != null && !title.equals("") )
                            details += "Title : " + title + "\n";

                        // Adding id, display name, path to photo and other details to cursor
                        mMatrixCursor.addRow(new Object[]{ Long.toString(contactId),displayName,photoPath,details});
                        Log.d("Contactid","is="+contactId);
                        Log.d("diplayname","is="+displayName);
                        Log.d("photopath","is="+photoPath);
                        Log.d("details","is="+details);
                    }
                }while(contactsCursor.moveToNext());
            }

            return mMatrixCursor;
        }

        @Override
        protected void onPostExecute(Cursor result) {
            // Setting the cursor containing contacts to listview

            mAdapter.swapCursor(result);
            progressDialog.dismiss();
            Toast.makeText(MainActivity.this,"Contacts loaded successfully",Toast.LENGTH_LONG).show();
            setlisteners();
        }


    }
public void setlisteners()
{
    lstContacts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            //int itemPosition     = position;
            Intent intent = new Intent(MainActivity.this, detail.class);

            TextView text1 = (TextView) view.findViewById(R.id.tv_name);
            TextView text2 = (TextView) view.findViewById(R.id.tv_details);
            String name = text1.getText().toString();
            String det = text2.getText().toString();
            ImageView im=(ImageView) view.findViewById(R.id.iv_photo);
            im.buildDrawingCache();
            Bitmap image= im.getDrawingCache();

            Bundle extras = new Bundle();
            extras.putParcelable("imagebitmap", image);
            intent.putExtras(extras);




            intent.putExtra("name", name); //this should pass the SQLite ROW_ID
            intent.putExtra("details", det); //this should pass the value of R.id.text1
            Log.d("name","alldata="+name+" "+det+""+im);
            startActivity(intent);

        }
    });
}



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }
}