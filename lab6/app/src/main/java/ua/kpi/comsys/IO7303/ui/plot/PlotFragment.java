package ua.kpi.comsys.IO7303.ui.plot;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.ArrayList;
import java.util.List;

import ua.kpi.comsys.IO7303.R;

public class PlotFragment extends Fragment {
    LineGraphSeries<DataPoint> ser;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_sec_tab_trend, container, false);

        int pointNum = 60;
        double min = -3.1415, max = 3.1415, currentPointX=min, currentPointY, step;
        pointNum -= 1;
        DataPoint[] cords = new DataPoint[pointNum+1];
        step = (max-min)/pointNum;

        for (int i = 0; i <= pointNum; i++) {
            currentPointY = Math.cos(currentPointX);
            cords[i] = new DataPoint(currentPointX, currentPointY);
            currentPointX += step;
        }

        GraphView graph = root.findViewById(R.id.trend);
        PieChart pieChart = root.findViewById(R.id.chart);

        graph.setFocusable(true);
        ser = new LineGraphSeries<>(cords);
        graph.addSeries(ser);

        graph.getViewport().setMinX(min-1);
        graph.getViewport().setMaxX(max+1);
        graph.getViewport().setMinY(-2);
        graph.getViewport().setMaxY(2);

        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setYAxisBoundsManual(true);

        pieChart.setUsePercentValues(true);
        Description desc = new Description();
        desc.setText("Variant 2");
        desc.setTextSize(20f);

        List<PieEntry> value = new ArrayList<>();
        value.add(new PieEntry(45f, "blue"));
        value.add(new PieEntry(5f, "purple"));
        value.add(new PieEntry(25f, "yellow"));
        value.add(new PieEntry(25f, "gray"));

        int[] colors = {getResources().getColor(R.color.blue), getResources().getColor(R.color.purple_500), getResources().getColor(R.color.yellow), getResources().getColor(R.color.gray)};

        PieDataSet pieDataSet = new PieDataSet(value, "Chart");
        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);

        pieDataSet.setValueTextSize(15f);
        pieDataSet.setColors(colors);
        pieChart.setDescription(desc);

        return root;
    }
}