package com.smartdevicelink.rpcbuilder.Views.UITextField;

import android.content.Context;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.widget.EditText;

import com.smartdevicelink.rpcbuilder.DataModels.RBBaseObject;
import com.smartdevicelink.rpcbuilder.DataModels.RBParam;

/**
 * Created by austinkirk on 11/14/16.
 */

public class RBParamTextField extends EditText{
    private String mType;

    public RBParamTextField(Context context){
        super(context);
        this.setSingleLine();
    }

    public void format(RBParam rbParam){
        mType = rbParam.mType;
        InputFilter filter = null;

        if(rbParam.mType.equals(RBBaseObject.RBTypeStringKey)){
            //this.setInputType(InputType.);
        }else if(rbParam.mType.equals(RBBaseObject.RBTypeIntegerKey)){
            this.setInputType(InputType.TYPE_CLASS_NUMBER);
        }else if(rbParam.mType.equals(RBBaseObject.RBTypeLongKey) || rbParam.mType.equals(RBBaseObject.RBTypeFloatKey) ||
        rbParam.mType.equals(RBBaseObject.RBTypeDoubleKey)){
            this.setInputType(InputType.TYPE_NUMBER_VARIATION_NORMAL);
        }

        if(rbParam.mDefaultValue != null)
            this.setText(rbParam.mDefaultValue);

        if(rbParam.mMinValue != null && rbParam.mMaxValue != null){
            if(filter == null)
                filter = new InputFilterMinMax(rbParam.mMinValue.toString(), rbParam.mMaxValue.toString());
            this.setFilters(new InputFilter[]{filter});
        }

        if(rbParam.mMaxLength != null){
            if(filter != null)
                this.setFilters(new InputFilter[]{filter, new InputFilter.LengthFilter(rbParam.mMaxLength.intValue())});
            else
                this.setFilters(new InputFilter[]{new InputFilter.LengthFilter(rbParam.mMaxLength.intValue())});
        }

        if(rbParam.mDefaultValue != null){
            this.setText(rbParam.mDefaultValue);
        }
    }

    public class InputFilterMinMax implements InputFilter {

        private int min, max;

        public InputFilterMinMax(int min, int max) {
            this.min = min;
            this.max = max;
        }

        public InputFilterMinMax(String min, String max) {
            Long safe_min, safe_max; // Have to protect against an input that is bigger than an Int (Mobile_API_3.1 revealed this)
            safe_min = Long.parseLong(min);
            safe_max = Long.parseLong(max);

            if(safe_min >= Integer.MIN_VALUE)
                this.min = Integer.parseInt(min);
            else
                this.min = Integer.MIN_VALUE;
            if(safe_max <= Integer.MAX_VALUE)
                this.max = Integer.parseInt(max);
            else
                this.max = Integer.MAX_VALUE;
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

    public String getType(){
       return mType;
    }
}
