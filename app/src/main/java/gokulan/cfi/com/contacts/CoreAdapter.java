package gokulan.cfi.com.contacts;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by gokulan on 4/19/17.
 */

public class CoreAdapter extends RecyclerView.Adapter<CoreAdapter.viewHolder>{

    public List<Core> coreList;
    public Context context;
    public CoreAdapter(Context mcontext, List<Core> eventlist)
    {
        coreList = eventlist;
        context = mcontext;
    }

    public viewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.core_layout, parent, false);
        return new viewHolder(view);
    }

    public void onBindViewHolder(viewHolder holder, int position)
    {
        Core e = coreList.get(position);
        holder.name_view.setText(e.getName());
        holder.id_view.setText(e.getDepartment());
    }

    public int getItemCount()
    {return coreList.size();}

    public class viewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public TextView name_view, id_view;
        public viewHolder(View view)
        {
            super(view);
            view.setOnClickListener(this);
            name_view = (TextView) view.findViewById(R.id.core_name);
            id_view = (TextView) view.findViewById(R.id.core_department);
        }
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(context, ContactActivity.class);
            intent.putExtra("name", name_view.getText());
            context.startActivity(intent);
        }
    }

}
