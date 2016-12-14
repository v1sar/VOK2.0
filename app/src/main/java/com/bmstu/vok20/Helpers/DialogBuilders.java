package com.bmstu.vok20.Helpers;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;

import com.bmstu.vok20.MainActivity;
import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;

/**
 * Created by qwerty on 14.12.16.
 */

public class DialogBuilders {

    private ColorPicked colorPicked;

    public interface ColorPicked {
        void picked();
    }

    public AlertDialog colorPickerDialogBuilder(final Context context, final Fragment fragment) {
        return ColorPickerDialogBuilder
                .with(context)
                .setTitle("Choose color")
                .wheelType(ColorPickerView.WHEEL_TYPE.FLOWER)
                .noSliders()
                .density(20)
                .setPositiveButton("ok", new ColorPickerClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                        PreferenceHelper.getInstance().putInt(PreferenceHelper.BACKGROUND_COLOR, selectedColor);
                        colorPicked = (ColorPicked) fragment;
                        colorPicked.picked();
                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .build();
    }
}
