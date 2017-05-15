package io.github.mthli.Ninja.custom;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import org.zirco.R;

/**
 * @author 庄宏岩
 *
 */
public class CustomMenu extends BaseDialog implements View.OnClickListener {

    private OnMenuClickListener menuClickListener;
    private View mView = null;
    public CustomMenu(Context context) {
        super(context);
        menuClickListener = (OnMenuClickListener) context;
    }

    /**
     * 重写这个方法添加自定义view
     *
     * @return view
     */
    @Override
    protected View customPanel() {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        LinearLayout linearLayout = (LinearLayout) inflater.inflate(R.layout.menu_layout, null);

        Button menu_bookmarks = (Button) linearLayout.findViewById(R.id.menu_bookmarks);
        menu_bookmarks.setOnClickListener(this);
        Button menu_history = (Button) linearLayout.findViewById(R.id.menu_history);
        menu_history.setOnClickListener(this);
        Button menu_setting = (Button) linearLayout.findViewById(R.id.menu_setting);
        menu_setting.setOnClickListener(this);
        Button menu_exit = (Button) linearLayout.findViewById(R.id.menu_exit);
        menu_exit.setOnClickListener(this);


        return linearLayout;
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        dismiss();
        mView = v;
    }

    /**
     * dismiss回调
     */
    @Override
    protected void onDismissed() {
        super.onDismissed();

        if (mView != null) {
            menuClickListener.OnMenuClick(mView.getId());
            mView = null;
        }
    }

    public interface OnMenuClickListener {
        public void OnMenuClick(int id);
    }
}
