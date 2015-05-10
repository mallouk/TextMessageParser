package mallouk.textmessage;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;


public class MainActivity extends ActionBarActivity {

    private Button smsButton;
    private ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        smsButton = (Button) findViewById(R.id.smsButton);
        listView = (ListView) findViewById(R.id.listView);

        String name = "", phoneNumber = "";
        Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        Cursor inbox = this.getContentResolver().query(Uri.parse("content://sms/inbox"),
                null, null, null, null);
        Cursor sent = this.getContentResolver().query(Uri.parse("content://sms/sent"),
                null, null, null, null);
        Cursor conversation = this.getContentResolver().query(Uri.parse("content://sms/conversations"),
                null, null, null, null);

        conversation.moveToFirst();
        final int numConvos = conversation.getCount();
        //Toast.makeText(getApplicationContext(), conversation.getCount() + "", Toast.LENGTH_LONG).show();

        int x = 0;
        Conversation[] convo = new Conversation[numConvos];

        final Conversation[] conversation00 = new Conversation[numConvos];
        String[] contactNames = new String[numConvos];
        String[] contactNames1 = new String[phones.getCount()];
        phones.moveToFirst();

        for (int t = 0; t < numConvos; t++){
            phones.moveToFirst();
            convo[t] = new Conversation();
            String convoThreadID = conversation.getString(conversation.getColumnIndex("thread_id")).toString();

            String inboxQueryPhoneNum = "";
            String inboxQueryDate = "";
            String inboxQueryMessage = "";
            String inboxQueryThreadID = "";
            inbox.moveToFirst();
            sent.moveToFirst();


            while (sent.moveToNext()){
                inboxQueryThreadID = sent.getString(sent.getColumnIndex("thread_id")).toString();
                if (convoThreadID.equals(inboxQueryThreadID)) {
                    inboxQueryPhoneNum = sent.getString(sent.getColumnIndex("address")).toString();

                    Date date = new Date(Long.parseLong(sent.getString(sent.getColumnIndex("date")).toString()));
                    inboxQueryDate = new SimpleDateFormat("yyyy/MM/dd:a:hh:mm - EEEE - MMMM").format(date);
                    inboxQueryMessage = sent.getString(sent.getColumnIndex("body")).toString();
                    inboxQueryPhoneNum = inboxQueryPhoneNum.substring(inboxQueryPhoneNum.length() - 10,
                            inboxQueryPhoneNum.length()).trim();
                    Message message = new Message("", inboxQueryPhoneNum, inboxQueryDate, inboxQueryMessage);
                    convo[t].add(message);
                }
            }

            while (inbox.moveToNext()){
                inboxQueryThreadID = inbox.getString(inbox.getColumnIndex("thread_id")).toString();
                if (convoThreadID.equals(inboxQueryThreadID)) {
                    inboxQueryPhoneNum = inbox.getString(inbox.getColumnIndex("address")).toString();

                    Date date = new Date(Long.parseLong(inbox.getString(inbox.getColumnIndex("date")).toString()));
                    inboxQueryDate = new SimpleDateFormat("yyyy/MM/dd:a:hh:mm - EEEE - MMMM").format(date);
                    inboxQueryMessage = inbox.getString(inbox.getColumnIndex("body")).toString();
                    inboxQueryPhoneNum = inboxQueryPhoneNum.substring(inboxQueryPhoneNum.length() - 10,
                            inboxQueryPhoneNum.length()).trim();

                    Message message = new Message("", inboxQueryPhoneNum, inboxQueryDate, inboxQueryMessage);
                    convo[t].add(message);
                }
            }
            Collections.sort(convo[t], new MessageSorter());


            phones.moveToFirst();
            for (int r = 0; r < phones.getCount(); r++){
                phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                phoneNumber = phoneNumber.replace(" ", "");
                phoneNumber = phoneNumber.replace("(", "");
                phoneNumber = phoneNumber.replace(")", "");
                phoneNumber = phoneNumber.replace("-", "");
                phoneNumber = phoneNumber.replace("+1", "");
                phoneNumber = phoneNumber.replace("+0", "");
                if (phoneNumber.equals(inboxQueryPhoneNum)) {
                    name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    break;
                }else{
                    name = phoneNumber + ":" +
                            phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                }
                phones.moveToNext();
            }
            conversation00[t] = (Conversation) convo[x].clone();
            conversation00[t].setContactName(name);

            x++;
            conversation.moveToNext();
        }

      /*  for (int u = 0; u < phones.getCount(); u++){
            phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            phoneNumber = phoneNumber.replace(" ", "");
            phoneNumber = phoneNumber.replace("(", "");
            phoneNumber = phoneNumber.replace(")", "");
            phoneNumber = phoneNumber.replace("-", "");
            phoneNumber = phoneNumber.replace("+1", "");
            phoneNumber = phoneNumber.replace("+0", "");
            *//*if (!phoneNumber.contains("*")){
                phoneNumber = phoneNumber.substring(phoneNumber.length() - 10,
                        phoneNumber.length()).trim();
            }*//*

            name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            contactNames1[u] = phoneNumber + ":" +  name;
            phones.moveToNext();
        }

        ListAdapter list = new ArrayAdapter<String>(getApplicationContext(),
                android.R.layout.simple_list_item_multiple_choice, contactNames1);
        listView.setAdapter(list);*/

        smsButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                File folder = new File(Environment.getExternalStorageDirectory() + "/.AWS");
                folder.mkdirs();
                String fileName = "/Mess.txt";
                File keyFile = new File(folder + fileName);

                try {
                    FileWriter fw = new FileWriter(folder + fileName);
                    BufferedWriter printWriter = new BufferedWriter(fw);
                    int x = 0;
                    for (int y = 0; y < numConvos; y++) {
                        printWriter.write(y + "/" + numConvos + " " + conversation00[y].getContactName() + "\n");
                        for (int i = 0; i < conversation00[y].size(); i++) {
                            Message mess = (Message) conversation00[y].get(i);
                            printWriter.write(mess.getActualMessage() + " - " + mess.getMessageDate() + "\n");
                        }
                        printWriter.write("\n\n\n");
                        printWriter.flush();
                        x = y;
                    }

                    printWriter.close();
                    Toast.makeText(getApplicationContext(), "File written." + x,
                            Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Error in writing file.",
                            Toast.LENGTH_LONG).show();
                }
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
