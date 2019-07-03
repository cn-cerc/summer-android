package cn.sd5g.appas.android.parts.dialog;

import android.app.Activity;
import android.content.Context;


public class DialogUtil {

    /***
     * 更新服务器地址
     */
    public static void InputDialog(final Activity activity, final OnclickAddressListen listen) {

        final InputDialog dialog = new InputDialog(activity);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.setOnDialogClickListern(new InputDialog.OnDialogClick() {
            @Override
            public void onConfrim(String newsUrl) {
                listen.click(true, newsUrl);
                dialog.dismiss();
            }

            @Override
            public void onCancel() {
                listen.click(false, null);
                dialog.dismiss();
            }

        });
        dialog.show();
    }

    /***
     * 版本更新弹窗
     */
    public static void DownloadDialog(final Context context, boolean isUpdate, final OnclickUpdateListen listen) {

        final DownloadDialog dialog = new DownloadDialog(context, isUpdate);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.setOnDialogClickListern(new DownloadDialog.OnDialogClick() {
            @Override
            public void onConfrim() {
                listen.click(true);
                dialog.dismiss();
            }

            @Override
            public void onCancel() {
                listen.click(false);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    public interface OnclickAddressListen {
        void click(boolean bool, String newsUrl);
    }

    public interface OnclickUpdateListen {
        void click(boolean bool);
    }

}



