package com.example.ipek.hw1;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        List<Person> personList=readContactlist();
        setListView(personList);
    }

    public void setListView(List<Person> selectedList) {
        listView = (ListView) findViewById(R.id.list);
        listView.setOnItemClickListener(this);
        PersonAdapter adapter = new PersonAdapter(this, R.layout.row_layout, selectedList);

        listView.setAdapter(adapter);
    }

    // read phone contact list
    public List<Person> readContactlist()
    {
        List<Person> personList=new ArrayList<Person>();

        ContentResolver resolver = getContentResolver();
        Cursor cursor = resolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

        while (cursor.moveToNext()) {
            String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));


            Cursor phoneCursor = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "= ?", new String[]{id}, null);


            while (phoneCursor.moveToNext()) {
                String phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                personList.add(new Person(name, phoneNumber));
            }
        }
       return personList;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        TextView selectedNumber = (TextView) view.findViewById(R.id.phoneNumber);

        Uri number = Uri.parse("tel:" + selectedNumber.getText().toString());
        Intent dialActivity = new Intent(Intent.ACTION_DIAL, number);
        startActivity(dialActivity);
    }

    // take backup
    public void backup(View view) throws FileNotFoundException {
       //delete if there was an earlier backup
        deleteFile("backup.txt");

        PrintStream out = new PrintStream(openFileOutput("backup.txt", MODE_PRIVATE));
        for (Person person : readContactlist()) {
            out.println(person.getName() + "\t" + person.getPhoneNumber());
        }

        Toast.makeText(this,"Backup is taken",Toast.LENGTH_SHORT).show();
    }

    // recover contacts
    public void recover(View view) throws IOException, RemoteException, OperationApplicationException {

        File file = new File(getFilesDir()+"/backup.txt");

        //check whether there is a backup
        if (file.exists()) {
            Scanner sc = new Scanner(openFileInput("backup.txt"));

            while (sc.hasNextLine()) {

                String contacts[] = sc.nextLine().split("\t");
                String DisplayName = contacts[0];
                String MobileNumber = contacts[1];

                // add contact to the phone contact list
                ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();
                ops.add(ContentProviderOperation.newInsert(
                        ContactsContract.RawContacts.CONTENT_URI)
                        .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, null)
                        .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, null)
                        .build());


                if (DisplayName != null) {
                    ops.add(ContentProviderOperation.newInsert(
                            ContactsContract.Data.CONTENT_URI)
                            .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                            .withValue(ContactsContract.Data.MIMETYPE,
                                    ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                            .withValue(
                                    ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME,
                                    DisplayName).build());
                }


                if (MobileNumber != null) {
                    ops.add(ContentProviderOperation.
                            newInsert(ContactsContract.Data.CONTENT_URI)
                            .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                            .withValue(ContactsContract.Data.MIMETYPE,
                                    ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                            .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, MobileNumber)
                            .withValue(ContactsContract.CommonDataKinds.Phone.TYPE,
                                    ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                            .build());
                }

                try {
                    getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Exception: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }

            RadioButton allBtn=(RadioButton)findViewById(R.id.allBtn);
            allBtn.setChecked(true);

            showContacts(allBtn);

            Toast.makeText(this,"The recovery is completed",Toast.LENGTH_SHORT).show();

        }
        else
            Toast.makeText(this,"You should take a back-up",Toast.LENGTH_SHORT).show();
    }


    public void showContacts(View view) {

        int id = view.getId();
        List<Person> selectedList=new ArrayList<>();
        List<Person> personList=readContactlist();
        switch (id) {
            case R.id.turkcellBtn:
                for(Person person:personList){
                    if(person.getPhoneNumber().startsWith("(53"))
                        selectedList.add(person);
                }
                setListView(selectedList);
                break;
            case R.id.aveaBtn:

                for(Person person:personList){
                    if(person.getPhoneNumber().startsWith("(55"))
                        selectedList.add(person);
                }
                setListView(selectedList);
                break;
                case R.id.vodafoneBtn:
                for(Person person:personList){
                    if(person.getPhoneNumber().startsWith("(54"))
                        selectedList.add(person);
                }
                    setListView(selectedList);

                break;
                case R.id.allBtn:
                    setListView(personList);

                break;
        }
    }
}



