package com.megamal.framework.util;

import com.megamal.mawi.GameMainActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import android.content.res.AssetManager;

/**
 * Created by malberbatovci on 08/10/15.
 */
public class TileMapFactory {
    private int mapY;
    private int mapX;

    private String currentLine;

    private boolean dimensionsFound = false;
    private String[] values;
    private String[] dimensions;
    private int currentValue;

    private InputStream inFile;
    private int[][] map;

    //method to parse a text file into a 2D array map; which is used to
    //represent the level map.
    public int[][] parseFileIntoMap(String fileName) throws IOException {
        dimensionsFound = false;
        int y = 0;
        int x = 0;
        try {
            BufferedReader bReader = new BufferedReader(new InputStreamReader
                                     (GameMainActivity.assets.open(fileName)));

            while ((currentLine = bReader.readLine()) != null) {
                if (dimensionsFound) {
                    values = currentLine.split(",");

                    for(int i = 0; i < values.length; i++) {
                        currentValue = Integer.parseInt(values[i]);
                        map[y][x] = currentValue;
                        x++;
                    }

                    y++;
                    x = 0;

                } else {
                    dimensions = currentLine.split(",");
                    mapY = Integer.parseInt(dimensions[0]);
                    mapX = Integer.parseInt(dimensions[1]);
                    map = new int[mapY][mapX];
                    dimensionsFound = true;
                }
            }

            bReader.close();

        } catch (IOException e) {
            System.err.println("Unable to parse map from file :" + fileName);
        }

        return map;

    }

    public int[][] getMap() {
        return map;
    }

    public int getMapY() {
         return mapY;
    }

    public int getMapX() {
        return mapX;
    }
}
