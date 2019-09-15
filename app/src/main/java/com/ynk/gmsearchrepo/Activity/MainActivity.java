package com.ynk.gmsearchrepo.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.ynk.gmsearchrepo.Adapter.AdapterRepository;
import com.ynk.gmsearchrepo.Listener.EndlessRecyclerViewScrollListener;
import com.ynk.gmsearchrepo.Listener.RecyclerViewItemClick;
import com.ynk.gmsearchrepo.Model.GeneralRepository;
import com.ynk.gmsearchrepo.Model.Repository;
import com.ynk.gmsearchrepo.Model.User;
import com.ynk.gmsearchrepo.Presenter.GeneralRepositoryPresenter;
import com.ynk.gmsearchrepo.Presenter.IGeneralRepositoryPresenter;
import com.ynk.gmsearchrepo.R;
import com.ynk.gmsearchrepo.Tools.Utils;
import com.ynk.gmsearchrepo.View.IGeneralRepositoryView;

import java.util.ArrayList;
import java.util.List;

import muyan.snacktoa.SnackToa;

public class MainActivity extends AppCompatActivity implements IGeneralRepositoryView {

    private EditText etSearchRepository;
    private View rlEmptyLayout, llProgressLayout, llLazyLoadProgress;
    private RecyclerView rvRepos;
    private FloatingActionButton fabRecyclerScroolTop;

    private AdapterRepository adapterRepository;
    private List<Repository> repositories;
    private int totalRepositoryCount = 0;
    private int currentPageIndex = 1;

    private String keyword = "";

    private IGeneralRepositoryPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_repo);

        //Init Variable
        repositories = new ArrayList<>();
        adapterRepository = new AdapterRepository(MainActivity.this, repositories, new RecyclerViewItemClick() {

            @Override
            public void viewClickForUser(View view, Object item, int position) {
                User user = ((Repository) item).getOwner();
                Intent intent = new Intent(MainActivity.this, UserDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("userName", user.getLogin());
                intent.putExtras(bundle);
                startActivity(intent);
            }

            @Override
            public void viewClickForRepo(View view, Object item, int position) {
                Repository repository = (Repository) item;
                Intent intent = new Intent(MainActivity.this, RepoDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("repoFullName", repository.getFull_name());
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        //Init Presenter
        presenter = new GeneralRepositoryPresenter(MainActivity.this, this);

        //Init View
        etSearchRepository = findViewById(R.id.etSearchRepository);
        rlEmptyLayout = findViewById(R.id.rlEmptyLayout);
        llProgressLayout = findViewById(R.id.llProgressLayout);
        llLazyLoadProgress = findViewById(R.id.llLazyLoadProgress);
        rvRepos = findViewById(R.id.rvRepos);

        //View Actions
        LinearLayoutManager manager = new LinearLayoutManager(this);
        rvRepos.setLayoutManager(manager);
        //rvRepos.setHasFixedSize(true);
        rvRepos.setAdapter(adapterRepository);
        rvRepos.addOnScrollListener(new EndlessRecyclerViewScrollListener(manager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                if (totalRepositoryCount > 0 && totalRepositoryCount > repositories.size()) {
                    llLazyLoadProgress.setVisibility(View.VISIBLE);
                    currentPageIndex++;
                    presenter.getRepositories(keyword, currentPageIndex);
                }
            }
        });

        fabRecyclerScroolTop = findViewById(R.id.fabRecyclerScroolTop);
        rvRepos.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy < 0)
                    fabRecyclerScroolTop.hide();
                else
                    fabRecyclerScroolTop.show();
                super.onScrolled(recyclerView, dx, dy);
            }
        });

        etSearchRepository.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEARCH) {
                    repositories.clear();
                    adapterRepository.notifyDataSetChanged();
                    currentPageIndex = 1;
                    totalRepositoryCount = 1;
                    keyword = etSearchRepository.getText().toString();
                    new Utils().hideSoftKeyboard(MainActivity.this, etSearchRepository);
                    llProgressLayout.setVisibility(View.VISIBLE);
                    rlEmptyLayout.setVisibility(View.GONE);
                    fabRecyclerScroolTop.hide();
                    presenter.getRepositories(keyword, 1);
                    return true;
                }
                return false;
            }
        });

        fabRecyclerScroolTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rvRepos.smoothScrollToPosition(0);
            }
        });

    }

    @Override
    public void getRepositoriesResult(GeneralRepository generalRepository) {
        llProgressLayout.setVisibility(View.GONE);
        llLazyLoadProgress.setVisibility(View.GONE);
        if (generalRepository != null) {
            totalRepositoryCount = generalRepository.getTotal_count();
            repositories.addAll(generalRepository.getItems());
            adapterRepository.notifyDataSetChanged();
            if (repositories.isEmpty()) {
                fabRecyclerScroolTop.hide();
                rvRepos.setVisibility(View.GONE);
                rlEmptyLayout.setVisibility(View.VISIBLE);
            } else {
                rvRepos.setVisibility(View.VISIBLE);
                rlEmptyLayout.setVisibility(View.GONE);
            }
        } else {
            if (repositories.isEmpty()) {
                fabRecyclerScroolTop.hide();
                rvRepos.setVisibility(View.GONE);
                rlEmptyLayout.setVisibility(View.VISIBLE);
            } else {
                rvRepos.setVisibility(View.VISIBLE);
                rlEmptyLayout.setVisibility(View.GONE);
            }
            SnackToa.snackBarError(this, "Connection Error!");
        }
    }
}
