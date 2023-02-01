package com.xontel.surveillancecameras.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.GridLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.xontel.surveillancecameras.R;
import com.xontel.surveillancecameras.databinding.ActivityGridTestBinding;
import com.xontel.surveillancecameras.utils.PlayerView;


public class GridTestActivity extends AppCompatActivity {
    private ActivityGridTestBinding binding ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_grid_test);
        initUI();

    }

    private void initUI() {
        setupGrid();
        binding.one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawGrid(1);
            }
        });
        binding.two.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawGrid(4);
            }
        });
        binding.three.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawGrid(9);
            }
        });
        binding.four.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawGrid(16);
            }
        });
    }

    private void setupGrid() {
     drawGrid(4);
    }


    private void drawGrid(int gridCount){
        int count = (int)Math.sqrt(gridCount) ;
        int childCount = binding.grid.getChildCount();

        if(gridCount >childCount){
            binding.grid.setColumnCount(count);
            binding.grid.setRowCount(count);
            childCount = gridCount - binding.grid.getChildCount();
            addViews(childCount);
        }else{
         binding.grid.removeViews(gridCount, binding.grid.getChildCount() - gridCount);

         for(int i = 0 ; i < binding.grid.getChildCount() ; i++){
             GridLayout.LayoutParams param = new GridLayout.LayoutParams(GridLayout.spec(
                     GridLayout.UNDEFINED, GridLayout.FILL, 1f),
                     GridLayout.spec(GridLayout.UNDEFINED, GridLayout.FILL, 1f));

             binding.grid.getChildAt(i).setLayoutParams(param);

         }
            binding.grid.setColumnCount(count);
            binding.grid.setRowCount(count);
        }

    }


    private void addViews(int size) {
        for (int i = 0; i < size; i++) {
            PlayerView playerView = new PlayerView(this, i + 1);
            GridLayout.LayoutParams param = new GridLayout.LayoutParams(GridLayout.spec(
                    GridLayout.UNDEFINED, GridLayout.FILL, 1f),
                    GridLayout.spec(GridLayout.UNDEFINED, GridLayout.FILL, 1f));

            param.height = 0;
            param.width = 0;
            binding.grid.addView(playerView,param);
        }
    }


}