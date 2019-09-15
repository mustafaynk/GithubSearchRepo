package com.ynk.gmsearchrepo.Listener;

import android.view.View;

public interface RecyclerViewItemClick {
    void viewClickForUser(View view, Object item, int position);
    void viewClickForRepo(View view, Object item, int position);
}
