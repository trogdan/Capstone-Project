package com.xanadu.marker.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.EditText;

public class BackEditText extends EditText {
    /* Must use this constructor in order for the layout files to instantiate the class properly */
    public BackEditText(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    @Override
    public boolean onKeyPreIme (int keyCode, KeyEvent event)
    {
        this.setVisibility(INVISIBLE);

        return false;
    }

}