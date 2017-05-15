package io.github.mthli.Ninja.custom;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import org.zirco.R;
/**
 * @author 庄宏岩
 *
 */
public class BaseDialog extends Dialog {

    protected Context mContext;
    private LinearLayout mPanel;
    private View mBg;
    private boolean mDismissed = true;
    private boolean mCancelableOnTouchOutside = true;


    /**
     * Create a Dialog window that uses the default dialog frame style.
     *
     * @param context The Context the Dialog is to run it.  In particular, it
     *                uses the window manager and theme in this context to
     *                present its UI.
     */
    public BaseDialog(Context context) {
        super(context, android.R.style.Theme_Light_NoTitleBar);// 全屏
        this.mContext = context;

        getWindow().setGravity(Gravity.BOTTOM);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        initViews();
    }

    private void initViews() {
        /* 隐藏软键盘 */
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            View focusView = ((Activity) mContext).getCurrentFocus();
            if (focusView != null)
                imm.hideSoftInputFromWindow(focusView.getWindowToken(), 0);
        }

        getWindow().setContentView(createView());

    }


    /**
     * 创建基本的背景视图
     */
    private View createView() {
        FrameLayout parent = new FrameLayout(mContext);
        FrameLayout.LayoutParams parentParams = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT);
        parentParams.gravity = Gravity.BOTTOM;
        parent.setLayoutParams(parentParams);


        mBg = new View(mContext);
        mBg.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        mBg.setBackgroundResource(R.color.translucent);
        mBg.setVisibility(View.INVISIBLE);
        mBg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCancelableOnTouchOutside) {
                    dismiss();
                }
            }
        });

        mPanel = new LinearLayout(mContext);
        FrameLayout.LayoutParams mPanelParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        mPanelParams.gravity = Gravity.BOTTOM;
        mPanel.setLayoutParams(mPanelParams);
        mPanel.setOrientation(LinearLayout.VERTICAL);
        mPanel.setOnClickListener(null);
        mPanel.setVisibility(View.INVISIBLE);

        View v = customPanel();
        if (v != null)
            mPanel.addView(v);

        parent.addView(mBg);
        parent.addView(mPanel);
        return parent;
    }

    /**
     * 重写这个方法添加自定义view
     *
     * @return view
     */
    protected View customPanel() {
        return null;
    }


    /**
     * Start the dialog and display it on screen.  The window is placed in the
     * application layer and opaque.  Note that you should not override this
     * method to do initialization when the dialog is shown, instead implement
     * that in {@link #onStart}.
     */
    @Override
    public void show() {
        if (!mDismissed)
            return;

        super.show();

        mBg.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.overflow_menu_fade_in));
        mPanel.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.grow_from_bottom));
        mPanel.setVisibility(View.VISIBLE);
        mBg.setVisibility(View.VISIBLE);

        mDismissed = false;
    }

    /**
     * Dismiss this dialog, removing it from the screen. This method can be
     * invoked safely from any thread.  Note that you should not override this
     * method to do cleanup when the dialog is dismissed, instead implement
     * that in {@link #onStop}.
     */
    @Override
    public void dismiss() {
        if (mDismissed)
            return;

        Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.shrink_from_bottom);
        animation.setAnimationListener(new dismissAnimationListener());
        mPanel.startAnimation(animation);
        mBg.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.overflow_menu_fade_out));
        mPanel.setVisibility(View.INVISIBLE);
        mBg.setVisibility(View.INVISIBLE);
    }

    /**
     * 点击外部边缘是否可取消
     *
     * @param cancelable
     * @return
     */
    public BaseDialog setCancelableOnTouchMenuOutside(boolean cancelable) {
        mCancelableOnTouchOutside = cancelable;
        return this;
    }

    /**
     * dismiss回调
     */
    protected void onDismissed() {

    }


    private class dismissAnimationListener implements Animation.AnimationListener {
        /**
         * <p>Notifies the start of the animation.</p>
         *
         * @param animation The started animation.
         */
        @Override
        public void onAnimationStart(Animation animation) {

        }

        /**
         * <p>Notifies the repetition of the animation.</p>
         *
         * @param animation The animation which was repeated.
         */
        @Override
        public void onAnimationRepeat(Animation animation) {

        }

        /**
         * <p>Notifies the end of the animation. This callback is not invoked
         * for animations with repeat count set to INFINITE.</p>
         *
         * @param animation The animation which reached its end.
         */
        @Override
        public void onAnimationEnd(Animation animation) {
            BaseDialog.super.dismiss();
            mDismissed = true;
            onDismissed();
        }
    }
}
