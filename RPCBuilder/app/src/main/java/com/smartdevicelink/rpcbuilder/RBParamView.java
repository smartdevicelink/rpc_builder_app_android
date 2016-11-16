package com.smartdevicelink.rpcbuilder;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

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
    private String attr_name;
    private String attr_type;
    private LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

    public RBParamView(Context context){
        super(context);
    }

    public RBParamView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.RBParamView,
                0, 0);

        try {
            attr_name = a.getString(R.styleable.RBParamView_pName);
            attr_type = a.getString(R.styleable.RBParamView_pType);
            //rbParam. = a.getString(R.styleable.RBParamView_pValue); Currently not  used
        } finally {
            a.recycle();
        }
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
            RBStructButton view = new RBStructButton(getContext());
            view.format(rbStruct);
            view.setGravity(Gravity.RIGHT);
            view.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_keyboard_arrow_right_white_36dp), null, null, null);
            addView(view);
        } else if (rbEnum != null) {
            RBEnumSpinner view = new RBEnumSpinner(getContext());
            view.format(rbEnum);
            view.setGravity(Gravity.RIGHT);
            addView(view);
        } else {

        }
    }
}
