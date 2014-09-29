package com.amlcurran.messages;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class AboutFragment extends Fragment implements Entitled {

    private final List<Point> points = new ArrayList<Point>();

    private TextView versionText;
    private ViewGroup pointsLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about, container, false);
        versionText = ((TextView) view.findViewById(R.id.about_version));
        pointsLayout = ((ViewGroup) view.findViewById(R.id.about_points));
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        versionText.setText(createVersionString());

        points.add(new Point("Github Project", "View the app's open source project", "https://github.com/amlcurran/Messages"));
        points.add(new Point("Calligraphy", "Used under Apache 2.0 License", "https://github.com/chrisjenx/Calligraphy"));
        points.add(new Point("SourceBinder", "Used under Apache 2.0 License", "https://github.com/amlcurran/SourceBinder"));

        ArrayAdapter<Point> pointArrayAdapter = new PointsArrayAdapter(getActivity());
        pointArrayAdapter.addAll(points);
        injectAdapter(pointsLayout, pointArrayAdapter);
    }

    private void injectAdapter(ViewGroup viewGroup, Adapter arrayAdapter) {
        for (int i = 0; i < arrayAdapter.getCount(); i++) {
            viewGroup.addView(arrayAdapter.getView(i, null, viewGroup));
        }
    }

    private CharSequence createVersionString() {
        String versionName = "error";
        try {
            versionName = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return getString(R.string.version_name_label, versionName);
    }

    @Override
    public int getTitleResource() {
        return R.string.title_about;
    }

    private class Point {

        private final String title;
        private final String secondary;
        private final String url;

        public Point(String title, String secondary, String url) {
            this.title = title;
            this.secondary = secondary;
            this.url = url;
        }
    }

    private class PointsArrayAdapter extends ArrayAdapter<Point> {

        public PointsArrayAdapter(Context activity) {
            super(activity, R.layout.item_point);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            PointViewHolder viewHolder = PointViewHolder.fromView(convertView);

            if (viewHolder == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_point, parent, false);
                viewHolder = new PointViewHolder(convertView);
            }

            final Point currentPoint = getItem(position);
            viewHolder.title.setText(currentPoint.title);
            viewHolder.secondary.setText(currentPoint.secondary);

            viewHolder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    launchUrl(currentPoint.url);
                }
            });

            return convertView;
        }

    }

    private static class PointViewHolder {

        private final TextView title;
        private final TextView secondary;
        private final View view;

        public PointViewHolder(View view) {
            title = ((TextView) view.findViewById(R.id.point_title));
            secondary = ((TextView) view.findViewById(R.id.point_secondary));
            this.view = view;
            this.view.setTag(this);
        }

        public static PointViewHolder fromView(View view) {
            if (view == null || view.getTag() == null) {
                return null;
            }
            return (PointViewHolder) view.getTag();
        }

    }

    private void launchUrl(String url) {
        Intent urlIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(urlIntent);
    }
}
