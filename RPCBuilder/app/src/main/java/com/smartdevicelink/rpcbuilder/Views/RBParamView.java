package com.smartdevicelink.rpcbuilder.Views;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.smartdevicelink.rpcbuilder.Activities.BuildActivity;
import com.smartdevicelink.rpcbuilder.DataModels.RBBaseObject;
import com.smartdevicelink.rpcbuilder.DataModels.RBEnum;
import com.smartdevicelink.rpcbuilder.DataModels.RBFunction;
import com.smartdevicelink.rpcbuilder.DataModels.RBParam;
import com.smartdevicelink.rpcbuilder.DataModels.RBStruct;
import com.smartdevicelink.rpcbuilder.Views.UIEnumSpinner.RBEnumSpinner;
import com.smartdevicelink.rpcbuilder.Views.UILabel.RBNameLabel;
import com.smartdevicelink.rpcbuilder.Views.UIStructButton.RBStructButton;
import com.smartdevicelink.rpcbuilder.Views.UISwitch.RBSwitch;
import com.smartdevicelink.rpcbuilder.Views.UITextField.RBParamTextField;

/**
 * Created by austinkirk on 11/16/16.
 */

public class RBParamView extends LinearLayout {
    private RBParam rbParam;
    private LinearLayout.LayoutParams wrap_params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    private LinearLayout.LayoutParams match_params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    private LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);


    public RBParamView(Context context){
        super(context);
    }

    public LinearLayout giveRequest(RBStruct request){
        setOrientation(LinearLayout.VERTICAL);
        setLayoutParams(lparams);
        for(RBParam rbp : request.getParams()){
            addParam(rbp);
        }

        if(request.getClass() == RBFunction.class){
            if(((RBFunction) request).requiresBulkData()){
                addBulkDataView();
            }
        }

        return this;
    }

    private void addParam(RBParam rbp){
        rbParam = rbp;

        LinearLayout linearLayout = new LinearLayout(getContext());
        linearLayout.setLayoutParams(lparams);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        RBNameLabel rbNameLabel = new RBNameLabel(getContext());
        rbNameLabel.format(rbParam);
        linearLayout.addView(rbNameLabel);

        // Try to find out if we need a struct or enum
        RBStruct rbStruct = ((BuildActivity) getContext()).getParserHandler().getStructs().get(rbParam.mType);
        RBEnum rbEnum = ((BuildActivity) getContext()).getParserHandler().getEnums().get(rbParam.mType);

        if (rbParam.mType.equals(RBBaseObject.RBTypeStringKey) || rbParam.mType.equals(RBBaseObject.RBTypeIntegerKey)
                || rbParam.mType.equals(RBBaseObject.RBTypeFloatKey) || rbParam.mType.equals(RBBaseObject.RBTypeLongKey)
                || rbParam.mType.equals(RBBaseObject.RBTypeDoubleKey)) {

            RBParamTextField view = new RBParamTextField(getContext());
            view.format(rbParam);
            view.setEnabled(rbNameLabel.isChecked());
            linearLayout.addView(view);
        } else if (rbParam.mType.equals(RBBaseObject.RBTypeBooleanKey)) {
            RBSwitch view = new RBSwitch(getContext());
            view.format(rbParam);
            wrap_params.gravity = Gravity.LEFT;
            view.setLayoutParams(wrap_params);
            view.setEnabled(rbNameLabel.isChecked());
            linearLayout.addView(view);
        } else if (rbStruct != null) {
            rbStruct.name = rbParam.name;
            RBStructButton view = new RBStructButton(getContext());
            if(rbParam.mRequiresArray != null){
                view.setArray(rbParam.mRequiresArray);
            }
            view.format(rbStruct, this);
            wrap_params.gravity = Gravity.LEFT;
            view.setLayoutParams(wrap_params);
            view.setText("UPDATE");
            view.setEnabled(rbNameLabel.isChecked());
            linearLayout.addView(view);
        } else if (rbEnum != null) {
            RBEnumSpinner view = new RBEnumSpinner(getContext());
            view.format(rbEnum);
            view.setEnabled(rbNameLabel.isChecked());
            linearLayout.addView(view);
        }

        addView(linearLayout);
    }

    private void addBulkDataView(){
        LinearLayout linearLayout = new LinearLayout(getContext());
        linearLayout.setLayoutParams(lparams);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        TextView textView = new TextView(getContext());
        textView.setTextColor(Color.WHITE);
        textView.setText("Bulk Data");
        linearLayout.addView(textView);

        Button bulkButton = new Button(getContext());
        bulkButton.setText("Add File");
        bulkButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                ((BuildActivity) getContext()).startActivityForResult(intent, ((BuildActivity) getContext()).FILE_PICKER_SUCCESS);
            }
        });
        LayoutParams image_params = wrap_params;
        image_params.gravity = Gravity.LEFT;
        bulkButton.setLayoutParams(image_params);

        linearLayout.addView(bulkButton);
        addView(linearLayout);
    }
}
