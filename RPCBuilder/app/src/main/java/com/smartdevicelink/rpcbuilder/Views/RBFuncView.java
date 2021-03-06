package com.smartdevicelink.rpcbuilder.Views;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.print.PrintAttributes;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.smartdevicelink.rpcbuilder.Activities.BuildActivity;
import com.smartdevicelink.rpcbuilder.DataModels.RBBaseObject;
import com.smartdevicelink.rpcbuilder.DataModels.RBEnum;
import com.smartdevicelink.rpcbuilder.DataModels.RBFunction;
import com.smartdevicelink.rpcbuilder.DataModels.RBParam;
import com.smartdevicelink.rpcbuilder.DataModels.RBStruct;
import com.smartdevicelink.rpcbuilder.Fragments.ListParamsFragment;
import com.smartdevicelink.rpcbuilder.R;
import com.smartdevicelink.rpcbuilder.Views.UIEnumSpinner.RBEnumSpinner;
import com.smartdevicelink.rpcbuilder.Views.UILabel.RBNameLabel;
import com.smartdevicelink.rpcbuilder.Views.UIStructButton.RBStructButton;
import com.smartdevicelink.rpcbuilder.Views.UISwitch.RBSwitch;
import com.smartdevicelink.rpcbuilder.Views.UITextField.RBParamTextField;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;

/**
 * Created by austinkirk on 11/29/16.
 */

public class RBFuncView extends LinearLayout {
    private LinearLayout.LayoutParams wrap_params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    private LinearLayout.LayoutParams match_params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);


    public RBFuncView(Context context){
        super(context);
    }

    public LinearLayout setFuncs(Vector<RBFunction> requests){
        setOrientation(LinearLayout.VERTICAL);
        setLayoutParams(wrap_params);
        Collections.sort(requests, new Comparator<RBFunction>() {
            @Override
            public int compare(RBFunction t1, RBFunction t2) {
                return t1.name.compareTo(t2.name);
            }
        });
        for(RBFunction req : requests){
            addView(buildRow(req));
        }
        return this;
    }

    public LinearLayout buildRow(final RBFunction rbf){
        LinearLayout innerLayout = new LinearLayout(getContext());
        LinearLayout.LayoutParams layout_params = wrap_params;

        innerLayout.setOrientation(LinearLayout.HORIZONTAL);
        layout_params.width = LinearLayout.LayoutParams.MATCH_PARENT;
        layout_params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
        innerLayout.setLayoutParams(layout_params);

        ViewGroup.LayoutParams view_params;
        // add Image depending on Request Type
        ImageView imageView = new ImageView(getContext());
        layout_params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        layout_params.width = LayoutParams.WRAP_CONTENT;
        layout_params.height = LayoutParams.MATCH_PARENT;
        layout_params.weight = 0f;
        layout_params.gravity = Gravity.LEFT;
        imageView.setLayoutParams(layout_params);

        imageView.setImageDrawable(getResources().getDrawable(rbf.image()));
        innerLayout.addView(imageView);

        // add TextView depending on function name and set OnClickListener to remove current ListFuncsFragment and show ListParamsFragment for chosen function
        TextView textView = new TextView(getContext());
        layout_params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        layout_params.width = LayoutParams.WRAP_CONTENT;
        layout_params.height = LayoutParams.MATCH_PARENT;
        layout_params.weight = 1f;
        layout_params.gravity = Gravity.CENTER;
        layout_params.setMargins((int) getResources().getDimension(R.dimen.function_margin), 0, 0, 0);
        textView.setLayoutParams(layout_params);
        textView.setText(RBNameLabel.convertCamelCase(rbf.name));
        textView.setTextColor(getResources().getColor(R.color.colorAccent));
        textView.setGravity(Gravity.CENTER_VERTICAL);
        textView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                BuildActivity buildActivity = (BuildActivity) getContext();
                buildActivity.hideFragment(buildActivity.getFragmentManager().findFragmentByTag(buildActivity.LIST_FUNCS_KEY));
                buildActivity.setRBFunction(rbf);
                buildActivity.showFragment(ListParamsFragment.class);
            }
        });
        innerLayout.addView(textView);

        // add information button and set OnClickListener to Toast the function's description
        ImageButton imageButton = new ImageButton(getContext());
        layout_params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        layout_params.width = LayoutParams.WRAP_CONTENT;
        layout_params.height = LayoutParams.MATCH_PARENT;
        layout_params.weight = 0f;
        layout_params.gravity = Gravity.RIGHT;
        imageButton.setLayoutParams(layout_params);
        imageButton.setBackgroundColor(Color.TRANSPARENT);
        imageButton.setImageResource(R.drawable.ic_info_outline_white_36dp);
        imageButton.setScaleX(0.65f);
	    imageButton.setScaleY(0.65f);
        imageButton.setVisibility(INVISIBLE);
        if(rbf.objectDescription != null) {
            imageButton.setVisibility(VISIBLE);
            imageButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    // 1. Instantiate an AlertDialog.Builder with its constructor
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                    // 2. Chain together various setter methods to set the dialog characteristics
                    builder.setMessage(rbf.objectDescription)
                            .setTitle(rbf.name);

                    // 3. Add cancel or "OK" button
                    builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                        }
                    });
                    builder.setCancelable(true);

                    // 4. Get the AlertDialog from create()
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            });
        }
        innerLayout.addView(imageButton);

        return innerLayout;
    }
}

