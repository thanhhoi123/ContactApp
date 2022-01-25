package com.example.contactapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder>{
    private ArrayList<Contact> contacts;
    private IonClick_rv ionClick_rv; //Khai báo 1 đối tượng Interface


    public ContactAdapter(ArrayList<Contact> contacts, IonClick_rv item) {
        this.contacts = contacts;
        this.ionClick_rv = item;
    }

    @NonNull
    @Override
    public ContactAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contact_row_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactAdapter.ViewHolder holder, int position) {
        Contact contact = contacts.get(position);
        holder.tv_name.setText(contact.getName());
        if(contact.getAvatar() != null) {
            Bitmap decodedByte = BitmapFactory.decodeByteArray(contact.getAvatar(), 0, contact.getAvatar().length);
            Bitmap bMapScaled = Bitmap.createScaledBitmap(decodedByte, 85, 100, true);
            holder.imv_person.setImageBitmap(bMapScaled);
        }
        holder.layout_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ionClick_rv.onClickItem_rv(contact);
            }
        });
    }


    @Override
    public int getItemCount() {
        return contacts.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_name;
        private CardView layout_item;
        private ImageView imv_person;

        public ViewHolder(View view) {
            super(view);
            layout_item = view.findViewById(R.id.layout_item);
            tv_name = view.findViewById(R.id.tv_name);
            imv_person = view.findViewById(R.id.imv_person);
        }
    }
}
