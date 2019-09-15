package com.ynk.gmsearchrepo.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.ynk.gmsearchrepo.Model.Repository;
import com.ynk.gmsearchrepo.Presenter.IRepositoryPresenter;
import com.ynk.gmsearchrepo.Presenter.RepositoryPresenter;
import com.ynk.gmsearchrepo.R;
import com.ynk.gmsearchrepo.Tools.Utils;
import com.ynk.gmsearchrepo.View.IRepositoryView;

import muyan.snacktoa.SnackToa;

public class RepoDetailActivity extends AppCompatActivity implements IRepositoryView {

    private ImageView ivUserImage;
    private TextView tvUserName;
    private TextView tvUserMail;
    private TextView tvRepoName;
    private TextView tvForkCount;
    private TextView tvLanguage;
    private TextView tvBranchName;
    private TextView tvRepoDescription;

    private View llProgressLayout, llContentLayout, llHeaderLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repository_detail);

        String repoFullName = "";
        if (getIntent().getExtras() != null)
            repoFullName = getIntent().getExtras().getString("repoFullName");

        //Init presenter
        IRepositoryPresenter presenter = new RepositoryPresenter(RepoDetailActivity.this, this);

        //Init View
        ivUserImage = findViewById(R.id.ivUserImage);
        tvUserName = findViewById(R.id.tvUserName);
        tvUserMail = findViewById(R.id.tvUserMail);
        tvRepoName = findViewById(R.id.tvRepoName);
        tvForkCount = findViewById(R.id.tvForkCount);
        tvLanguage = findViewById(R.id.tvLanguage);
        tvBranchName = findViewById(R.id.tvBranchName);
        tvRepoDescription = findViewById(R.id.tvRepoDescription);
        llProgressLayout = findViewById(R.id.llProgressLayout);
        llContentLayout = findViewById(R.id.llContentLayout);
        llHeaderLayout = findViewById(R.id.llHeaderLayout);
        llProgressLayout.setAlpha(1.0f);

        //View Actions
        ivUserImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RepoDetailActivity.this, UserDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("userName", tvUserName.getText().toString());
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        presenter.getRepository(repoFullName);
    }


    @Override
    public void getRepositoryResult(Repository repository) {
        if (repository == null) {
            SnackToa.snackBarError(this, "Connection Error!");
            return;
        }
        Utils utils = new Utils();
        Glide.with(RepoDetailActivity.this).load(repository.getOwner().getAvatar_url()).into(ivUserImage);
        tvUserName.setText(utils.nullControlFromString(this, repository.getOwner().getLogin()));
        tvUserMail.setText(utils.nullControlFromString(this, repository.getOwner().getEmail()));
        tvRepoName.setText(utils.nullControlFromString(this, repository.getName()));
        tvForkCount.setText(utils.nullControlFromString(this, String.valueOf(repository.getForks_count())));
        tvLanguage.setText(utils.nullControlFromString(this, String.valueOf(repository.getLanguage())));
        tvBranchName.setText(utils.nullControlFromString(this, repository.getDefault_branch()));
        tvRepoDescription.setText(utils.nullControlFromString(this, repository.getDescription()));

        llHeaderLayout.setVisibility(View.VISIBLE);
        ivUserImage.setVisibility(View.VISIBLE);
        llContentLayout.setVisibility(View.VISIBLE);
        llProgressLayout.setVisibility(View.GONE);
    }
}
