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
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;


public class MainActivity extends ActionBarActivity {

    private Button smsButton;
    private Button backupContactsButton;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        smsButton = (Button) findViewById(R.id.smsButton);
        backupContactsButton = (Button) findViewById(R.id.backupContacts);

        runButtonListeners();
    }

    public void runButtonListeners(){
        final Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null, null, null, null);
        final Cursor inbox = this.getContentResolver().query(Uri.parse("content://sms/inbox"),
                null, null, null, null);
        final Cursor sent = this.getContentResolver().query(Uri.parse("content://sms/sent"),
                null, null, null, null);
        final Cursor conversation = this.getContentResolver().query(Uri.parse("content://sms/conversations"),
                null, null, null, null);


        backupContactsButton.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v){


                String name = "", phoneNumber = "";
                phones.moveToFirst();
                ArrayList<PhoneContact> contactList = new ArrayList<PhoneContact>();

                for (int r = 0; r < phones.getCount(); r++){
                    phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    phoneNumber = phoneNumber.replace(" ", "");
                    phoneNumber = phoneNumber.replace("(", "");
                    phoneNumber = phoneNumber.replace(")", "");
                    phoneNumber = phoneNumber.replace("-", "");
                    phoneNumber = phoneNumber.replace("+1", "");
                    phoneNumber = phoneNumber.replace("+0", "");
                    name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));

                    if (phoneNumber.length() == 10) {
                        phoneNumber = "(" + phoneNumber.substring(0, 3) + ") " + phoneNumber.substring(3, 6) + "-" +
                               phoneNumber.substring(6, phoneNumber.length());
                        contactList.add(new PhoneContact(name, phoneNumber));
                    }
                    phones.moveToNext();
                }
                Collections.sort(contactList, new ContactSorter());

                File folder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "");

                folder.mkdirs();
                String fileName = "/ContactsBackup.txt";
                File keyFile = new File(folder + fileName);

                try {
                    FileWriter fw = new FileWriter(folder + fileName);
                    BufferedWriter printWriter = new BufferedWriter(fw);
                    for (int y = 0; y < contactList.size(); y++) {
                        printWriter.write(contactList.get(y).getContactName() +  "\t" +
                                contactList.get(y).getContactNumber() + "\n");
                        printWriter.flush();
                    }
                    printWriter.close();
                    Toast.makeText(getApplicationContext(), "File written to Downloads folder.",
                            Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), e.getMessage(),
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        smsButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Processing SMS Messages....", Toast.LENGTH_LONG).show();

                String name = "", phoneNumber = "";
                conversation.moveToFirst();
                final int numConvos = conversation.getCount();
                int x = 0;
                Conversation[] convo = new Conversation[numConvos];
                final Conversation[] conversation00 = new Conversation[numConvos];
                phones.moveToFirst();

                for (int t = 0; t < numConvos; t++){
                    phones.moveToFirst();
                    convo[t] = new Conversation();
                    String convoThreadID = conversation.getString(conversation.getColumnIndex("thread_id")).toString();

                    String inboxQueryPhoneNum = "";
                    String inboxQueryDate = "";
                    String formattedDate = "";
                    String inboxQueryMessage = "";
                    String inboxQueryThreadID = "";
                    inbox.moveToFirst();
                    sent.moveToFirst();

                    while (sent.moveToNext()){
                        inboxQueryThreadID = sent.getString(sent.getColumnIndex("thread_id")).toString();
                        if (convoThreadID.equals(inboxQueryThreadID)) {
                            inboxQueryPhoneNum = sent.getString(sent.getColumnIndex("address")).toString();

                            Date date = new Date(Long.parseLong(sent.getString(sent.getColumnIndex("date")).toString()));
                            inboxQueryDate = new SimpleDateFormat("yyyy/MM/dd:a:hh:mm:ss - EEEE - MMMM").format(date);
                            inboxQueryMessage = sent.getString(sent.getColumnIndex("body")).toString();
                            inboxQueryMessage = inboxQueryMessage.replace("\n", " ");

                            formattedDate = new SimpleDateFormat("*- EEEE- MMMM dd, yyyy - hh:mm:ss a -").format(date).toString();
                            inboxQueryPhoneNum = inboxQueryPhoneNum.substring(inboxQueryPhoneNum.length() - 10,
                                    inboxQueryPhoneNum.length()).trim();
                            Message message = new Message("", inboxQueryPhoneNum, inboxQueryDate, formattedDate,
                                    inboxQueryMessage, "sent");
                            convo[t].add(message);
                        }
                    }

                    while (inbox.moveToNext()){
                        inboxQueryThreadID = inbox.getString(inbox.getColumnIndex("thread_id")).toString();
                        if (convoThreadID.equals(inboxQueryThreadID)) {
                            inboxQueryPhoneNum = inbox.getString(inbox.getColumnIndex("address")).toString();

                            Date date = new Date(Long.parseLong(inbox.getString(inbox.getColumnIndex("date")).toString()));
                            inboxQueryDate = new SimpleDateFormat("yyyy/MM/dd:a:hh:mm:ss - EEEE - MMMM").format(date);
                            formattedDate = new SimpleDateFormat("*- EEEE- MMMM dd, yyyy - hh:mm:ss a -").format(date).toString();
                            inboxQueryMessage = inbox.getString(inbox.getColumnIndex("body")).toString();
                            inboxQueryMessage = inboxQueryMessage.replace("\n", " ");

                            inboxQueryPhoneNum = inboxQueryPhoneNum.substring(inboxQueryPhoneNum.length() - 10,
                                    inboxQueryPhoneNum.length()).trim();
                            Message message = new Message("", inboxQueryPhoneNum, inboxQueryDate, formattedDate,
                                    inboxQueryMessage, "rec");
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




                File folder = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "");
                folder.mkdirs();
                String fileName = "/Mess.txt";
                File keyFile = new File(folder + fileName);

                try {
                    FileWriter fw = new FileWriter(folder + fileName);
                    BufferedWriter printWriter = new BufferedWriter(fw);
                    for (int y = 0; y < numConvos; y++) {
                        printWriter.write(")-" + conversation00[y].getContactName() + "-" + "\n");
                        printWriter.write("/001" + "\n");

                        for (int i = 0; i < conversation00[y].size(); i++) {
                            Message mess = (Message) conversation00[y].get(i);
                            printWriter.write("      " + mess.getFormattedDate() + mess.getActualMessage() + "-" +
                                    mess.getMessageType() + "-\n");
                        }
                        printWriter.write("\n\n\n");
                        printWriter.flush();
                    }

                    printWriter.close();
                    Toast.makeText(getApplicationContext(), "File written to Downloads folder.",
                            Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), e.getMessage(),
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

    class ContactSorter implements Comparator<PhoneContact>{
        @Override
        public int compare(PhoneContact o1, PhoneContact o2) {
            return o1.getContactName().compareToIgnoreCase(o2.getContactName());
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
