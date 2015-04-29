package mallouk.textmessage;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Contacts;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity {

    private Button smsButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        smsButton = (Button)findViewById(R.id.smsButton);

        String key, value = "val";
        String[] columns = {Contacts.People.NAME, Contacts.People.NUMBER};
        Uri mContacts = Contacts.People.CONTENT_URI;
        Cursor cur = managedQuery(mContacts, columns, null, null, null);


        if (cur.moveToFirst()) {
            value = cur.getString(cur.getColumnIndex(Contacts.People.NAME));
            key = cur.getString(cur.getColumnIndex(Contacts.People.NUMBER));
            Toast.makeText(getApplicationContext(), "val:" + value + "  key:f" + Contacts.People.NUMBER,
                    Toast.LENGTH_LONG).show();
            if (key!=null){
                PersonContact contact1 = new PersonContact(value, key);
            }
        }


        Cursor cursor1 = this.getContentResolver().query(Uri.parse("content://sms/inbox"),
            null, null, null, null);
        String smsAddress1 = "boo";
        String smsDate1 = "name";
        String smsBody1 = "body";
        if (cursor1.moveToFirst()) {
            smsAddress1 = cursor1.getString(cursor1.getColumnIndex("address")).toString();
            smsDate1 = cursor1.getString(cursor1.getColumnIndex("date")).toString();
            smsBody1 = cursor1.getString(cursor1.getColumnIndex("body")).toString();
        }
        final String smsAddress = smsAddress1;
        final String smsName = value;
        final String smsDate = smsDate1;
        final String smsBody = smsBody1;
        //String contactnamelist = getContactDisplayNameByNumber(smsAddress);

        final int count = cursor1.getCount();

        smsButton.setOnClickListener(new Button.OnClickListener(){

            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Message count: " + count + " :" + smsAddress + " " + smsName + " " +
                                smsDate + " " + smsBody,
                        Toast.LENGTH_LONG).show();
                Log.d("WHAT", count + "");

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
