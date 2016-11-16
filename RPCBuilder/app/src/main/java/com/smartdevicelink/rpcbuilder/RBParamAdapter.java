package com.smartdevicelink.rpcbuilder;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.smartdevicelink.rpcbuilder.Views.UIStructButton.RBStructButton;
import com.smartdevicelink.rpcbuilder.Views.UISwitch.RBSwitch;
import com.smartdevicelink.rpcbuilder.Views.UITextField.RBParamTextField;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeSet;
import java.util.Vector;

/**
 * Created by austinkirk on 11/14/16.
 */

public class RBParamAdapter extends BaseAdapter {

    private ArrayList<RBParam> mData;
    private LayoutInflater mInflater;
    private Context mContext;
    private ParserHandler mParserHandler;

    public RBParamAdapter(Context context, Vector<RBParam> params, ParserHandler parserHandler) {
        mContext = context;
        mParserHandler = parserHandler;
        mInflater = (LayoutInflater) mContext.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
        mData = new ArrayList<RBParam>(params);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        //TODO: Return viewType based on parameter's mType
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        //TODO: Return the number of viewtypes total
        return 1;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public RBParam getItem(int position) {
        return (RBParam) mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    static class ViewHolder{
        TextView name;
        EditText field;
        Switch picker;
        Spinner spinner;
        Button button;
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        RBParam rbParam = (RBParam) mData.get(position);
        if (true){
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.simple_field, null);

            holder.name = (TextView)convertView.findViewById(R.id.param_name);
            holder.name.setText(rbParam.name);

            RBEnum rbEnum = mParserHandler.getEnums().get(rbParam.mType);
            RBStruct rbStruct = mParserHandler.getStructs().get(rbParam.mType);

            if(rbParam.mRequiresArray != null){
                holder.spinner = (Spinner)convertView.findViewById(R.id.param_spinner);
            }else{
                if(rbParam.mType.equals(RBBaseObject.RBTypeStringKey) || rbParam.mType.equals(RBBaseObject.RBTypeIntegerKey)
                        || rbParam.mType.equals(RBBaseObject.RBTypeFloatKey) || rbParam.mType.equals(RBBaseObject.RBTypeLongKey)
                        || rbParam.mType.equals(RBBaseObject.RBTypeDoubleKey)){
                    RBParamTextField rbParamTextField = new RBParamTextField(rbParam, (EditText)convertView.findViewById(R.id.param_field));
                    holder.field = rbParamTextField.getEditText();
                }else if(rbParam.mType.equals(RBBaseObject.RBTypeBooleanKey)) {
                    RBSwitch rbSwitch = new RBSwitch(rbParam, (Switch) convertView.findViewById(R.id.param_switch));
                    holder.picker = rbSwitch.getSwitch();
                }else if(rbStruct != null){
                    RBStructButton rbStructButton = new RBStructButton(rbStruct, (Button) convertView.findViewById(R.id.param_struct), this);
                    holder.button = rbStructButton.getButton();
                }else if(rbEnum != null){

                }
            }

            View[] views = {holder.field, holder.picker, holder.spinner, holder.button};
            int[] view_ids = {R.id.param_field, R.id.param_switch, R.id.param_spinner, R.id.param_struct};

            for(int i = 0; i < views.length; i++){
                if(views[i] == null){
                    views[i] = (View) convertView.findViewById(view_ids[i]);
                    ((ViewGroup) views[i].getParent()).removeView(views[i]);
                    views[i] = null;
                }
            }
            convertView.setTag(rbParam.name);
        }else{

        }
        return convertView;
    }

    public ParserHandler getParserHandler(){ return mParserHandler; }

    public Context getContext(){ return mContext; }

}