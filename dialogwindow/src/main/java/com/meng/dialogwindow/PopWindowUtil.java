package com.meng.dialogwindow;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;


import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class PopWindowUtil {

    private static PopWindowUtil instance;

    //private Map<String, PopUpWindow> windowMap;
    private Queue<PopWindow> windowQueue;

    private PopWindowUtil() {
    }

    public static PopWindowUtil getInstance() {
        if (instance == null) {
            instance = new PopWindowUtil();
        }
        if (instance.windowQueue == null) {
            instance.windowQueue = new ConcurrentLinkedQueue<>();
        }
       /* if (instance.windowMap == null) {
            instance.windowMap = new HashMap<>();
        }*/
        return instance;
    }


    /**
     * 插入弹窗
     *
     * @param window
     */
    public void insertPop(PopWindow window, boolean isDelay) {
        if (!isDelay) {
            window.show();
            return;
        }

        if (windowQueue.size() <= 0) {
            windowQueue.offer(window);
            start();
        } else {
            windowQueue.offer(window);
        }
    }

    /*public void insertPop(String flag, PopUpWindow window) {
        windowMap.put(flag, window);
        insertPop(window);
    }*/

    /*public PopUpWindow getPop(String flag) {
        return windowMap.get(flag);
    }
*/
    private void start() {
        PopWindow popUpWindow = windowQueue.peek();
        if (popUpWindow != null) {
            if (popUpWindow.dead) {
                windowQueue.poll();
                start();
            } else {
                popUpWindow.show();
            }
        }
    }

    private void onWindowDismiss(PopWindow popUpWindow) {
        if (windowQueue.peek() == popUpWindow) {
            windowQueue.poll();
        }
        start();
    }

    /*public void setIsNext(boolean isNext) {
        this.isNext = isNext;
    }*/

    /**
     * Builder
     */
    public static class Builder {

        private final Activity activity;

        private boolean isCover = true;
        private boolean cancelable = true;
        private boolean hasOpenAnim = true;
        private int openAnim = -1;
        //private boolean hasCloseAnim = true;
        private boolean isInput = false;
        private OnWindowDismissiListener onWindowDismissiListener;
        private PopWindow popUpWindow;

        public Builder(@NonNull Activity activity) {
            this.activity = activity;
        }

        public Builder setCover(boolean cover) {
            isCover = cover;
            return this;
        }

        public Builder setCancelable(boolean cancelable) {
            this.cancelable = cancelable;
            return this;
        }

        public Builder setHasOpenAnim(boolean hasOpenAnim) {
            this.hasOpenAnim = hasOpenAnim;
            return this;
        }

        /*public Builder setHasCloseAnim(boolean hasCloseAnim) {
            this.hasCloseAnim = hasCloseAnim;
            return this;
        }*/

        public Builder setInput(boolean input) {
            isInput = input;
            return this;
        }

        public Builder setOnWindowDismissiListener(OnWindowDismissiListener onWindowDismissiListener) {
            this.onWindowDismissiListener = onWindowDismissiListener;
            return this;
        }

        public void setOpenAnim(int openAnim) {
            this.openAnim = openAnim;
        }

        /*public void closeWindow() {
            if (popUpWindow != null) {
                popUpWindow.close();
            }
        }*/

        public <T> PopWindow create(final BaseHolder<T> holder, final OnWindowClickListener<T> listener) {
            final Dialog dialog = buildFullDialog();
            if (isInput) {
                dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            }
            final FrameLayout fl_container = new FrameLayout(activity);
            if (isCover) {
                fl_container.setBackgroundColor(0x7F000000);
            }
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.CENTER;
            fl_container.addView(holder.getRoot());

            ViewGroup.LayoutParams container_params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            dialog.addContentView(fl_container, container_params);

            final PopWindow popUpWindow = new PopWindow() {
                @Override
                public void show() {
                    if (hasOpenAnim || openAnim != -1) {
                        if (openAnim == -1) {
                            TranslateAnimation up_in = new TranslateAnimation(0, 0, 300, 0);
                            up_in.setDuration(100);
                            AlphaAnimation fade_in = new AlphaAnimation(0, 1);
                            fade_in.setDuration(100);
                            fl_container.startAnimation(fade_in);
                            holder.getRoot().startAnimation(up_in);
                        } else {
                            AnimationSet animation = (AnimationSet) AnimationUtils.loadAnimation(activity, openAnim);
                            holder.getRoot().startAnimation(animation);
                        }
                    }
                    dialog.show();
                }

                @Override
                public void close() {
                    dialog.dismiss();
                }
            };
            popUpWindow.setHolder(holder);
            popUpWindow.setDialog(dialog);
            dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    PopWindowUtil.instance.onWindowDismiss(popUpWindow);
                    if (onWindowDismissiListener != null) {
                        onWindowDismissiListener.onDismiss();
                    }
                    fl_container.removeAllViews();
                }
            });

            dialog.setCancelable(cancelable);
            if (cancelable) {
                fl_container.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popUpWindow.close();
                    }
                });
            }
            this.popUpWindow = popUpWindow;

            holder.setListener(new OnWindowClickListener<T>() {
                @Override
                public boolean onClick(T param) {
                    if (listener != null && listener.onClick(param)) {
                        Builder.this.popUpWindow.close();
                        return true;
                    }
                    return false;
                }
            });
            return popUpWindow;
        }

        private Dialog buildFullDialog() {
            Dialog dialog;
            dialog = new Dialog(activity, R.style.MyMenuStyle);
            dialog.setCanceledOnTouchOutside(false);
            return dialog;
        }

    }

    public abstract static class PopWindow {
        private boolean dead;
        private BaseHolder holder;
        private Dialog dialog;

        public void setDead(boolean dead) {
            this.dead = dead;
        }

        public BaseHolder getHolder() {
            return holder;
        }

        public boolean isShowing() {
            return (dialog != null && dialog.isShowing());
        }

        public void setHolder(BaseHolder holder) {
            this.holder = holder;
        }

        abstract void show();

        public abstract void close();

        void setDialog(Dialog dialog) {
            this.dialog = dialog;
        }
    }

    public interface OnWindowClickListener<Y> {
        boolean onClick(Y param);
    }

    public interface OnWindowDismissiListener {
        void onDismiss();
    }

}

//窗口关闭动画
/*if (hasCloseAnim) {
    TranslateAnimation down_out = new TranslateAnimation(0, 0, 0, 300);
    down_out.setDuration(100);
    down_out.setAnimationListener(new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            dialog.dismiss();
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }
    });
    AlphaAnimation fade_out = new AlphaAnimation(1, 0);
    fade_out.setDuration(100);
    fl_container.startAnimation(fade_out);
    holder.view.startAnimation(down_out);
} else {
    dialog.dismiss();
}*/
