package com.smartdevicelink.rpcbuilder.Views.UITextField;

import android.sax.TextElementListener;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.widget.EditText;
import android.widget.TextView;

import com.smartdevicelink.rpcbuilder.RBBaseObject;
import com.smartdevicelink.rpcbuilder.RBParam;

/**
 * Created by austinkirk on 11/14/16.
 */

public class RBParamTextField{

    private EditText RBParamEditText = null;
    private RBParam rbParam;

    public RBParamTextField(RBParam parameter, EditText et){
        RBParamEditText = et;
        rbParam = parameter;
        setParameter(parameter);
    }

    public void setParameter(RBParam parameter){
        rbParam = parameter;
        InputFilter filter = null;

        if(rbParam.mType.equals(RBBaseObject.RBTypeStringKey)){
            RBParamEditText.setInputType(InputType.TYPE_TEXT_VARIATION_NORMAL);
        }else if(rbParam.mType.equals(RBBaseObject.RBTypeIntegerKey)){
            RBParamEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
        }else if(rbParam.mType.equals(RBBaseObject.RBTypeLongKey) || rbParam.mType.equals(RBBaseObject.RBTypeFloatKey) ||
        rbParam.mType.equals(RBBaseObject.RBTypeDoubleKey)){
            RBParamEditText.setInputType(InputType.TYPE_NUMBER_VARIATION_NORMAL);
        }

        if(rbParam.mDefaultValue != null)
            RBParamEditText.setText(rbParam.mDefaultValue);

        if(rbParam.mMinValue != null && rbParam.mMaxValue != null){
            if(filter == null)
                filter = new InputFilterMinMax(rbParam.mMinValue.toString(), rbParam.mMaxValue.toString());
            RBParamEditText.setFilters(new InputFilter[]{filter});
        }

        if(rbParam.mMaxLength != null)
            RBParamEditText.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(rbParam.mMaxLength.intValue())});

        if(rbParam.mDefaultValue != null){
            RBParamEditText.setText(rbParam.mDefaultValue);
        }
    }

    public class InputFilterMinMax implements InputFilter {

        private int min, max;

        public InputFilterMinMax(int min, int max) {
            this.min = min;
            this.max = max;
        }

        public InputFilterMinMax(String min, String max) {
            this.min = Integer.parseInt(min);
            this.max = Integer.parseInt(max);
        }

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            try {
                int input = Integer.parseInt(dest.toString() + source.toString());
                if (isInRange(min, max, input))
                    return null;
            } catch (NumberFormatException nfe) { }
            return "";
        }

        private boolean isInRange(int a, int b, int c) {
            return b > a ? c >= a && c <= b : c >= b && c <= a;
        }
    }

    public EditText getEditText(){
        return RBParamEditText;
    }

}
