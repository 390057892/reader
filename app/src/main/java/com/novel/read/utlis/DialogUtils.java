package com.novel.read.utlis;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

/**
 * create by 赵利君 on 2019/6/19
 * describe:
 */
public class DialogUtils {

    private static DialogUtils dialogUtils;

    public static DialogUtils getInstance() {
        if (dialogUtils == null) {
            dialogUtils = new DialogUtils();
        }
        return dialogUtils;
    }

    private DialogUtils() { }

    public void showAlertDialog(Context context, String msg, DialogInterface.OnClickListener dialogListener) {
        // context = context.getApplicationContext();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("操作提示").setMessage(msg).setCancelable(false).setPositiveButton("确定", dialogListener).setNegativeButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
