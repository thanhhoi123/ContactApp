package com.example.contactapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.room.Delete;
import androidx.room.Insert;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.contactapp.databinding.ActivityCreateContactBinding;
import com.example.contactapp.databinding.ActivityEditContactBinding;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class EditContact extends AppCompatActivity {

    private int id;
    private ActivityEditContactBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditContactBinding.inflate(getLayoutInflater());
        View viewRoot = binding.getRoot();
        setContentView(viewRoot);

        Intent receive = getIntent();
        if(receive != null){
            int ID = receive.getIntExtra("id", 0);
            id = ID;
            String name = receive.getStringExtra("name");
            String phone = receive.getStringExtra("phone");
            String email = receive.getStringExtra("email");
            byte[] avatar = receive.getByteArrayExtra("avatar");
            binding.edtName.setText(name);
            binding.edtPhone.setText(phone);
            binding.edtMail.setText(email);

            if(avatar != null) { //Thiết lập ảnh trên giao diện edit khi được gửi giao diện chính
                Bitmap decodedByte = BitmapFactory.decodeByteArray(avatar, 0, avatar.length);
                Bitmap bMapScaled = Bitmap.createScaledBitmap(decodedByte, 85, 100, true);
                binding.imvAvatar.setImageBitmap(bMapScaled);
            }
        }

        binding.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                //Trả về id để xoá
                intent.putExtra("id",id);
                setResult(RESULT_CANCELED,intent);
                finish();
            }
        });
        binding.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = binding.edtName.getText().toString();
                String phone = binding.edtPhone.getText().toString();
                String email = binding.edtMail.getText().toString();

                //Chuyển ảnh trên giao diện thành bitmap
                Bitmap bitmapImage = ((BitmapDrawable) binding.imvAvatar.getDrawable()).getBitmap();
                //Chuyển bitmap thành bite array
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();

                Intent intent = new Intent();
                intent.putExtra("id",id);
                intent.putExtra("name",name);
                intent.putExtra("phone",phone);
                intent.putExtra("email",email);
                intent.putExtra("avatar",byteArray);
                setResult(CONTEXT_INCLUDE_CODE,intent);
                finish();
            }
        });

        binding.btnPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choosePictue();
            }
        });

    }
    //Hàm để chọn ảnh từ thư viện hoặc load ảnh từ camera
    private void choosePictue(){
        AlertDialog.Builder builder = new AlertDialog.Builder(EditContact.this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alert_dialog_profile_picture,null);
        builder.setCancelable(false);
        builder.setView(dialogView);

        ImageView imv_camera = dialogView.findViewById(R.id.imv_camera);
        ImageView imv_gallery = dialogView.findViewById(R.id.imv_gallery);

        final AlertDialog alertDialogProfilePicture = builder.create();
        alertDialogProfilePicture.show();

        imv_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkAndRequestPermission()){
                    takePictureFromCamera();
                    alertDialogProfilePicture.cancel();
                }
            }
        });

        imv_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takePictureFromGallery();
                alertDialogProfilePicture.cancel();
            }
        });
    }
    private void takePictureFromGallery(){
        Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto,1);
    }

    private void takePictureFromCamera(){
        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(takePicture.resolveActivity(getPackageManager()) != null){
            startActivityForResult(takePicture,2);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 1:
                if(resultCode == RESULT_OK){
                    Uri selectedImageUri = data.getData(); //Nếu lấy ảnh từ thư viện thì nhận URI của ảnh đó
                    try {
                        Bitmap bitmapImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri); //Chuyển Uri thành bitmap
                        Bitmap bMapScaled = Bitmap.createScaledBitmap(bitmapImage, 85, 100, true); //Định dạng lại ảnh
                        binding.imvAvatar.setImageBitmap(bMapScaled); // thiết lập ảnh từ bitmap
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                }
            case 2:
                if(resultCode == RESULT_OK){
                    Bundle bundle = data.getExtras(); // Nếu load ảnh từ camera thì nhận bundle
                    Bitmap bitmapImage = (Bitmap) bundle.get("data");
                    binding.imvAvatar.setImageBitmap(bitmapImage);
                    break;
                }
        }
    }

    private boolean checkAndRequestPermission(){
        if(Build.VERSION.SDK_INT >= 23){
            int cameraPermission = ActivityCompat.checkSelfPermission(EditContact.this, Manifest.permission.CAMERA);
            if (cameraPermission == PackageManager.PERMISSION_DENIED){
                ActivityCompat.requestPermissions(EditContact.this, new String[]{Manifest.permission.CAMERA},20);
                return false;
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 20 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            takePictureFromCamera();
        }
        else{
            Toast.makeText(EditContact.this,"Yeu cau bi tu choi",Toast.LENGTH_SHORT).show();
        }
    }
}