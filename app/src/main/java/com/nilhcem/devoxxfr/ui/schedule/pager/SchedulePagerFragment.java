package com.nilhcem.devoxxfr.ui.schedule.pager;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.hannesdorfmann.fragmentargs.annotation.Arg;
import com.hannesdorfmann.fragmentargs.annotation.FragmentWithArgs;
import com.nilhcem.devoxxfr.DevoxxApp;
import com.nilhcem.devoxxfr.R;
import com.nilhcem.devoxxfr.data.app.DataProvider;
import com.nilhcem.devoxxfr.data.app.model.Schedule;
import com.nilhcem.devoxxfr.ui.BaseFragment;
import com.nilhcem.devoxxfr.ui.drawer.DrawerActivity;

import javax.inject.Inject;

import butterknife.Bind;

@FragmentWithArgs
public class SchedulePagerFragment extends BaseFragment<SchedulePagerPresenter> implements SchedulePagerView {

    @Arg boolean allSessions;

    @Inject DataProvider dataProvider;

    @Bind(R.id.schedule_loading) ProgressBar loading;
    @Bind(R.id.schedule_viewpager) ViewPager viewPager;

    private Snackbar errorSnackbar;

    @Override
    protected SchedulePagerPresenter newPresenter() {
        return new SchedulePagerPresenter(this, dataProvider);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        DevoxxApp.get(getContext()).component().inject(this);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.schedule_pager, container, false);
    }

    @Override
    public void onDestroyView() {
        if (errorSnackbar != null) {
            errorSnackbar.dismiss();
        }
        super.onDestroyView();
    }

    @Override
    public void displaySchedule(Schedule schedule) {
        viewPager.setAdapter(new SchedulePagerAdapter(getContext(), getChildFragmentManager(), schedule, allSessions));
        if (schedule.size() > 1) {
            ((DrawerActivity) getActivity()).setupTabLayoutWithViewPager(viewPager);
        }

        loading.setVisibility(View.GONE);
        viewPager.setVisibility(View.VISIBLE);
    }

    @Override
    public void displayLoadingError() {
        loading.setVisibility(View.GONE);
        errorSnackbar = Snackbar.make(loading, R.string.connection_error, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.connection_error_retry, v -> presenter.reloadData());
        errorSnackbar.show();
    }
}
