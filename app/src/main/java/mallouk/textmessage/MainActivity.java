package mallouk.textmessage;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;


public class MainActivity extends ActionBarActivity {

    private Button smsButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        smsButton = (Button)findViewById(R.id.smsButton);

        String name = "", phoneNumber = "";
        //String[] columns = {Contacts.People.NAME, Contacts.People.NUMBER};
        //Uri mContacts = Contacts.People.CONTENT_URI;
        //Cursor contacts = managedQuery(mContacts, columns, null, null, null);
        Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        Cursor inbox = this.getContentResolver().query(Uri.parse("content://sms/inbox"),
            null, null, null, null);
        Cursor sent = this.getContentResolver().query(Uri.parse("content://sms/sent"),
                null, null, null, null);
        Cursor conversation = this.getContentResolver().query(Uri.parse("content://sms/conversations"),
                null, null, null, null);

        conversation.moveToFirst();
        String convoThreadID = ";";
        if (conversation.moveToFirst()){
            convoThreadID = conversation.getString(conversation.getColumnIndex("thread_id")).toString();
        }

        String inboxQueryPhoneNum = "";
        String inboxQueryDate = "";
        String inboxQueryMessage = "";
        String inboxQueryThreadID = "";
        inbox.moveToFirst();
        sent.moveToFirst();

        Conversation convo = new Conversation();

        while (sent.moveToNext()) {
            inboxQueryThreadID = sent.getString(sent.getColumnIndex("thread_id")).toString();
            if (convoThreadID.equals(inboxQueryThreadID)){
                inboxQueryPhoneNum = sent.getString(sent.getColumnIndex("address")).toString();

                Date date = new Date(Long.parseLong(sent.getString(sent.getColumnIndex("date")).toString()));
                inboxQueryDate = new SimpleDateFormat("MM/dd/yyyy:a:hh:mm").format(date);
                inboxQueryMessage = sent.getString(sent.getColumnIndex("body")).toString();
                inboxQueryPhoneNum = inboxQueryPhoneNum.substring(2, inboxQueryPhoneNum.length()).trim();

                Message message = new Message("", inboxQueryPhoneNum, inboxQueryDate, inboxQueryMessage);
                convo.add(message);
            }
        }

        while (inbox.moveToNext()) {
            inboxQueryThreadID = inbox.getString(inbox.getColumnIndex("thread_id")).toString();
            if (convoThreadID.equals(inboxQueryThreadID)){
                inboxQueryPhoneNum = inbox.getString(inbox.getColumnIndex("address")).toString();

                Date date = new Date(Long.parseLong(inbox.getString(inbox.getColumnIndex("date")).toString()));
                inboxQueryDate = new SimpleDateFormat("MM/dd/yyyy:a:hh:mm").format(date);
                inboxQueryMessage = inbox.getString(inbox.getColumnIndex("body")).toString();
                inboxQueryPhoneNum = inboxQueryPhoneNum.substring(2, inboxQueryPhoneNum.length()).trim();

                Message message = new Message("", inboxQueryPhoneNum, inboxQueryDate, inboxQueryMessage);
                convo.add(message);
            }
        }
        Collections.sort(convo, new MessageSorter());

        while (phones.moveToNext()){
            phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            if (phoneNumber.equals(inboxQueryPhoneNum)){
                name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                break;
            }
        }

        final Conversation conversation00 = (Conversation)convo.clone();

        /*StringBuffer info = new StringBuffer();
        for( int i = 0; i < sent.getColumnCount(); i++) {
            info.append("Column: " + sent.getColumnName(i) + "\n");
        }
        Toast.makeText(getApplicationContext(), info.toString(), Toast.LENGTH_LONG).show();*/


        smsButton.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v) {
                Message one = (Message)conversation00.get(0);
                Message two = (Message)conversation00.get(1);
                Message three = (Message)conversation00.get(2);
                Toast.makeText(getApplicationContext(), conversation00.size() + " " +
                        one.getActualMessage() + ":" + one.getMessageDate() +  "\n"+
                        two.getActualMessage() + ":" + two.getMessageDate() +  "\n" +
                        three.getActualMessage() + ":" + three.getMessageDate() +  "\n",
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    class MessageSorter implements Comparator<Message>{
        @Override
        public int compare(Message o1, Message o2) {
            return o1.getMessageDate().compareTo(o2.getMessageDate());
        }
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
