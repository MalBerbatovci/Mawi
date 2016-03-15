package com.megamal.framework.util;

import com.megamal.game.model.Enemy;
import com.megamal.game.model.Hedgehog;
import com.megamal.mawi.GameMainActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by malberbatovci on 15/03/16.
 */
public class EnemyFactory {

    //method to parse text file data in enemyArray data
    public static Enemy[] parseEnemyFileIntoData(String fileName) throws IOException{

        boolean sizeFound = false;
        int enemyArraySize;
        int enemyX, enemyY;
        String[] values;

        Enemy[] enemyArray = new Enemy[0];
        int arrayIndex = 0;
        String currentLine;

        try {
            BufferedReader bReader = new BufferedReader(new InputStreamReader
                    (GameMainActivity.assets.open(fileName)));

            while ((currentLine = bReader.readLine()) != null) {
                if (sizeFound) {
                    values = currentLine.split(",");

                    enemyX = Integer.parseInt(values[0]);
                    enemyY = Integer.parseInt(values[1]);

                    enemyArray[arrayIndex] = new Hedgehog(enemyX, enemyY, 0, 0);
                    arrayIndex++;


                //in this case, not found yet - find size
                } else {
                    enemyArraySize = Integer.parseInt(currentLine);

                    if(enemyArraySize == 0) {
                        return enemyArray;
                    }

                    else {
                        enemyArray = new Enemy[enemyArraySize];
                        sizeFound = true;
                    }
                }
            }

            bReader.close();


        }

        catch (IOException e) {
            System.err.println("Unable to parse map from file :" + fileName);
        }


        return enemyArray;
    }
}
