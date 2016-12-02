package com.smartdevicelink.rpcbuilder.Views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.LinearLayout;

import com.smartdevicelink.rpcbuilder.Activities.BuildActivity;
import com.smartdevicelink.rpcbuilder.DataModels.RBBaseObject;
import com.smartdevicelink.rpcbuilder.DataModels.RBEnum;
import com.smartdevicelink.rpcbuilder.DataModels.RBParam;
import com.smartdevicelink.rpcbuilder.DataModels.RBStruct;
import com.smartdevicelink.rpcbuilder.R;
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


    public RBParamView(Context context){
        super(context);
    }

    public void addParam(RBParam rbp){
        rbParam = rbp;

        setOrientation(LinearLayout.VERTICAL);

        RBNameLabel rbNameLabel = new RBNameLabel(getContext());
        rbNameLabel.format(rbParam);
        addView(rbNameLabel);

        // Try to find out if we need a struct or enum
        RBStruct rbStruct = ((BuildActivity) getContext()).getParserHandler().getStructs().get(rbParam.mType);
        RBEnum rbEnum = ((BuildActivity) getContext()).getParserHandler().getEnums().get(rbParam.mType);

        if (rbParam.mType.equals(RBBaseObject.RBTypeStringKey) || rbParam.mType.equals(RBBaseObject.RBTypeIntegerKey)
                || rbParam.mType.equals(RBBaseObject.RBTypeFloatKey) || rbParam.mType.equals(RBBaseObject.RBTypeLongKey)
                || rbParam.mType.equals(RBBaseObject.RBTypeDoubleKey)) {

            RBParamTextField view = new RBParamTextField(getContext());
            view.format(rbParam);
            addView(view);
        } else if (rbParam.mType.equals(RBBaseObject.RBTypeBooleanKey)) {
            RBSwitch view = new RBSwitch(getContext());
            view.format(rbParam);
            addView(view);
        } else if (rbStruct != null) {
            rbStruct.name = rbParam.name;
            RBStructButton view = new RBStructButton(getContext());
            view.format(rbStruct);
            wrap_params.gravity = Gravity.LEFT;
            view.setLayoutParams(wrap_params);
            view.setBackgroundColor(Color.TRANSPARENT);
            view.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_keyboard_arrow_right_white_36dp), null, null, null);
            addView(view);
        } else if (rbEnum != null) {
            RBEnumSpinner view = new RBEnumSpinner(getContext());
            view.format(rbEnum);
            wrap_params.gravity = Gravity.RIGHT;
            view.setLayoutParams(wrap_params);
            addView(view);
        } else {

        }
    }
}
