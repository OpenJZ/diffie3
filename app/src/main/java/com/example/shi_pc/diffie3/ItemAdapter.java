package com.example.shi_pc.diffie3;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

public class ItemAdapter extends BaseAdapter {
    private Context mContext;
    //private List<Map<String,String>> mList = new ArrayList<Map<String, String>>();
    private List<? extends Map<String, ?>> mList;
    private int itemLayoutRes;

    public ItemAdapter(Context context, List<? extends Map<String, ?>> /*List<Map<String,String>>*/list, int layoutResource) {
        mContext = context;
        mList = list;
        itemLayoutRes=layoutResource;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int i) {
        return mList.get(i);
    }

    @Override //这个实现有什么用？？？
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        ViewHolder1 viewHolder1 = null;
        ViewHolder2 viewHolder2 = null;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(itemLayoutRes, null);
            if(itemLayoutRes==R.layout.request_item) {
                viewHolder1 = new ViewHolder1();
                viewHolder1.mTextDN = (TextView) view.findViewById(R.id.device_name);
                viewHolder1.mTextRT = (TextView) view.findViewById(R.id.request_type);
                viewHolder1.mButtonAcc = (Button) view.findViewById(R.id.request_accept);
                viewHolder1.mButtonDel = (Button) view.findViewById(R.id.request_delete);
                viewHolder1.mDocHash=(TextView)view.findViewById(R.id.dochash1);
                view.setTag(viewHolder1);
            }
            else if(itemLayoutRes==R.layout.device_item){
                viewHolder2=new ViewHolder2();
                viewHolder2.mTextDN=(TextView)view.findViewById(R.id.device_name2);
                viewHolder2.mTextMAC=(TextView)view.findViewById(R.id.device_MAC);
                viewHolder2.mTextDS=(TextView)view.findViewById(R.id.device_status);
                view.setTag(viewHolder2);
            }
        }
        else {
            if(itemLayoutRes==R.layout.request_item) {
                viewHolder1 = (ViewHolder1) view.getTag();
            }
            else if(itemLayoutRes==R.layout.device_item) {
                viewHolder2 = (ViewHolder2) view.getTag();
            }
        }
        if(itemLayoutRes==R.layout.request_item) {
            viewHolder1.mTextDN.setText((String) mList.get(i).get(mContext.getString(R.string.reqItem_map_key1)));
            viewHolder1.mTextRT.setText((String) mList.get(i).get(mContext.getString(R.string.reqItem_map_key2)));
            viewHolder1.mDocHash.setText((String) mList.get(i).get(mContext.getString(R.string.reqItem_map_key3)));
            viewHolder1.mButtonAcc.setText("同意");
            viewHolder1.mButtonDel.setText("删除");

            //开始监听删除按钮点击事件
            viewHolder1.mButtonDel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemDeleteListener.onDeleteClick(i);
                }
            });
            //开始监听接受按钮点击事件
            viewHolder1.mButtonAcc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemAcceptListener.onAcceptClick(i);
                }
            });
        }
        else if(itemLayoutRes==R.layout.device_item){
            viewHolder2.mTextDN.setText((String)mList.get(i).get(mContext.getString(R.string.devItem_map_key1)));
            viewHolder2.mTextMAC.setText((String)mList.get(i).get(mContext.getString(R.string.devItem_map_key2)));
            viewHolder2.mTextDS.setText((String)mList.get(i).get(mContext.getString(R.string.devItem_map_key3)));
        }
        return view;
    }

    //删除按钮的监听接口
    public interface onItemDeleteListener {
        void onDeleteClick(int i);
    }

    private onItemDeleteListener mOnItemDeleteListener;

    public void setOnItemDeleteClickListener(onItemDeleteListener L) {
        mOnItemDeleteListener = L;
    }
    //接受按钮的监听接口
    public interface onItemAcceptListener {
        void onAcceptClick(int i);
    }

    private onItemAcceptListener mOnItemAcceptListener;

    public void setOnItemAcceptClickListener(onItemAcceptListener L) {
        mOnItemAcceptListener = L;
    }
    class ViewHolder1 {
        TextView mTextDN;
        TextView mTextRT;
        Button mButtonAcc;
        Button mButtonDel;
        TextView mDocHash;
    }
    class ViewHolder2 {
        TextView mTextDN;
        TextView mTextMAC;
        TextView mTextDS;
    }
    //为适配器更换绑定数据及layout
    public void reviseAdapter(List<? extends Map<String, ?>> list, int layoutResource){
        mList=list;
        itemLayoutRes=layoutResource;
    }
}
