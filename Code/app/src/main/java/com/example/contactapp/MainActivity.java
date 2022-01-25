package com.example.contactapp;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;
import android.widget.SearchView;

import com.example.contactapp.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity{

    private ActivityMainBinding binding;

    private ArrayList<Contact> contactList;
    private ContactAdapter contactAdapter;

    private AppDatabase appDatabase;
    private ContactDao contactDao;

    private static final int SECOND_ACTIVITY_REQUEST_CODE = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View viewRoot = binding.getRoot();
        setContentView(viewRoot);

        contactList = new ArrayList<Contact>();
        //Khai báo và Tạo sự kiện cho item của Recycleview
        contactAdapter = new ContactAdapter(contactList, new IonClick_rv() {
            @Override
            public void onClickItem_rv(Contact contact) {
                Intent intent = new Intent(MainActivity.this,EditContact.class);
                intent.putExtra("id",contact.getId());
                intent.putExtra("name",contact.getName());
                intent.putExtra("phone",contact.getMobile());
                intent.putExtra("email",contact.getEmail());
                intent.putExtra("avatar",contact.getAvatar());
                startActivityForResult(intent,SECOND_ACTIVITY_REQUEST_CODE);
            }
        });
        binding.rvContacts.setLayoutManager(new LinearLayoutManager(this));
        binding.rvContacts.setAdapter(contactAdapter);

        appDatabase = AppDatabase.getInstance(this);
        contactDao = appDatabase.contactDao();

        binding.butAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,CreateContact.class);
                startActivityForResult(intent,SECOND_ACTIVITY_REQUEST_CODE);
            }
        });


        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                contactList.addAll(contactDao.getAll());
                contactAdapter.notifyDataSetChanged();
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SECOND_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                String name = data.getStringExtra("name");
                String phone = data.getStringExtra("phone");
                String mail = data.getStringExtra("mail");
                byte[] avatar = data.getByteArrayExtra("avatar");
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        contactDao.Insert(new Contact(name,phone,mail,avatar));
                    }
                });
            }
            if(resultCode == RESULT_CANCELED) {
                int id = data.getIntExtra("id", 0);
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        contactDao.delete(contactDao.getContactbyID(id));
                    }
                });
            }
            if(resultCode == CONTEXT_INCLUDE_CODE){
                int id = data.getIntExtra("id", 0);
                String name = data.getStringExtra("name");
                String mobile  = data.getStringExtra("phone");
                String email = data.getStringExtra("email");
                byte[] avatar = data.getByteArrayExtra("avatar");
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        contactDao.Update(id,name,mobile,email,avatar);
                    }
                });
            }

        }
        finish();
        startActivity(getIntent());
    }

    //Thao tác để tạo nút search ở trên menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.example_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.acion_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String s) {
                if(s.isEmpty()){  //Nếu text rỗng thì trả về toàn bộ csdl
                    AsyncTask.execute(new Runnable() {
                        @Override
                        public void run() {
                            contactList.clear();
                            contactList.addAll(contactDao.getAll());
                            contactAdapter.notifyDataSetChanged();
                        }
                    });
                }
                //Nếu có kí tự nhập vào thì search
                ArrayList<Contact> list = new ArrayList<>();
                String strSearch = s.toLowerCase().trim();
                for(Contact contact : contactList){
                    if(contact.getName().toLowerCase().contains(strSearch)){
                        list.add(contact);
                    }
                }
                contactList.clear();
                contactList.addAll(list);
                contactAdapter.notifyDataSetChanged();

                return false;
            }
        });
        return true;
    }
}