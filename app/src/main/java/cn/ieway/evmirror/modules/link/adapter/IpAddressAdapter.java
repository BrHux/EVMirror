package cn.ieway.evmirror.modules.link.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import cn.ieway.evmirror.R;
import cn.ieway.evmirror.entity.DeviceBean;

public class IpAddressAdapter extends RecyclerView.Adapter<IpAddressAdapter.VH> {

    private Context ctx;
    private List<DeviceBean> list;
    private OnItemClickListener itemClickListener;

    public void setItemClickListener(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public IpAddressAdapter(Context context){
        this.ctx = context;
        list = new ArrayList<>();
    }
    public IpAddressAdapter(Context context, List<DeviceBean> data) {
        this.ctx = context;
        this.list = data;
    }


    @NonNull
    @Override
    public IpAddressAdapter.VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(ctx).inflate(R.layout.item_ip_address,parent,false);

        return new VH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull  IpAddressAdapter.VH holder, int position) {
        holder.address.setText(list.get(position).getName());
        holder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(itemClickListener != null){
                    itemClickListener.onItemClick(v,position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class VH extends RecyclerView.ViewHolder {

        TextView address ;
        ConstraintLayout item;

        public VH(@NonNull View itemView) {
            super(itemView);
            address = itemView.findViewById(R.id.tv_device_name);
            item = itemView.findViewById(R.id.ip_address_item);
        }
    }

    public interface OnItemClickListener{
        void onItemClick(View view, int position);
    }
}
