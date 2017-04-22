package gokulan.cfi.com.contacts;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by gokulan on 4/19/17.
 */

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.viewHolder> {

    public List<String> contactList;
    public Context context;

    public ContactAdapter(Context mcontext, List<String> eventlist) {
        contactList = eventlist;
        context = mcontext;
    }

    public ContactAdapter.viewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_layout, parent, false);
        return new ContactAdapter.viewHolder(view);
    }

    public void onBindViewHolder(ContactAdapter.viewHolder holder, int position) {
        String e = contactList.get(position);
        holder.contact_view.setText(e);
    }

    public int getItemCount() {
        return contactList.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView contact_view;

        public viewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            contact_view = (TextView) view.findViewById(R.id.contact_view);
        }

        @Override
        public void onClick(View view) {
            if (contact_view.getText().toString().contains("@")) {
                String email = contact_view.getText().toString();
                Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("plain/text");
                i.putExtra(Intent.EXTRA_EMAIL, email);
                context.startActivity(i);
            } else {
                String number = contact_view.getText().toString();
                Intent i = new Intent(Intent.ACTION_DIAL,Uri.parse("tel:" + number));
                context.startActivity(i);
            }
        }
    }

}
