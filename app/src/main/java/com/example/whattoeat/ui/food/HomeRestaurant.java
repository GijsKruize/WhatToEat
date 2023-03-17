package com.example.whattoeat.ui.food;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.whattoeat.R;
import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

public class HomeRestaurant extends AppCompatActivity {
    PieChart pieChart;
    TextView viewCountOfPage;
    TextView likeCountOfPage;
    TextView skipCountOfPage;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_home_page_restaurant);
        pieChart = findViewById(R.id.piechart);
        viewCountOfPage = findViewById(R.id.viewCount);
        likeCountOfPage = findViewById(R.id.likesCount);
        skipCountOfPage = findViewById(R.id.skipCount);
        addData();
    }
    public int convertPercentage(TextView likeCountOfPage, TextView skipCountOfPage, int index) {
        int n = Integer.parseInt(likeCountOfPage.getText().toString());
        int m = Integer.parseInt(skipCountOfPage.getText().toString());
        int percentage_1 = (n / (n + m)) * 100;
        int percentage_2 = (m / (n + m)) * 100;
        int[] percentages = {percentage_1, percentage_2};

        return percentages[index];
    }



    private void addData() {


        likeCountOfPage.setText(Integer.toString(convertPercentage(likeCountOfPage, skipCountOfPage, 0)));
        pieChart.addPieSlice(
                new PieModel(
                        "Liked",
                        Integer.parseInt(likeCountOfPage.getText().toString()),
                        Color.parseColor("#0FB652")
                )
        );

        skipCountOfPage.setText(Integer.toString(convertPercentage(likeCountOfPage, skipCountOfPage, 2)));
        pieChart.addPieSlice((
                new PieModel(
                        "Skipped",
                        Integer.parseInt(skipCountOfPage.getText().toString()),
                        Color.parseColor("#7B7E7C"))
                )
        );

        viewCountOfPage.setText(Integer.toString(90));
    }

}
