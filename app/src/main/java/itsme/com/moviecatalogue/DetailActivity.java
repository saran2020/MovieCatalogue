package itsme.com.moviecatalogue;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        if (savedInstanceState == null) {

            Bundle argument = new Bundle();
            argument.putParcelable(DetailActivityFragment.DETAIL_URI, getIntent().getData());

            DetailActivityFragment fragment = new DetailActivityFragment();
            fragment.setArguments(argument);

            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.container_DetailView, fragment)
                    .commit();
        }
    }

}
