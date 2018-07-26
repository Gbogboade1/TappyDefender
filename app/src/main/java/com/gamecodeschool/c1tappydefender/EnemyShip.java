package com.gamecodeschool.c1tappydefender;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

import java.util.Random;

public class EnemyShip {
    private Bitmap bitmap;
    private int x;
    private int y;
    private int speed = 1;

    private int maxX;
    private int minX;

    private int maxY;
    private int minY;

    private Rect hitBox;

    public EnemyShip(Context context, int screenX, int screenY) {
        Random generator = new Random();
        int type = generator.nextInt(3);
        switch (type) {
            case 0:
                bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.enemy);
                break;
            case 1:
                bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.enemy2);
                break;
            case 2:
                bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.enemy3);
                break;
        }
        maxX = screenX;
        maxY = screenY;
        minX = 0;
        minY = 0;

        speed = generator.nextInt(6) + 10;

        x = screenX;
        y = generator.nextInt(maxY) - bitmap.getHeight();



        hitBox = new Rect(x, y, bitmap.getWidth(), bitmap.getHeight());
        scaleBitmap(screenX);
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Rect getHitBox() {
        return hitBox;
    }

    public void update(int playerSpeed) {
        x -= playerSpeed;
        x -= speed;

        if (x < minX - bitmap.getWidth()) {
            Random generator = new Random();
            speed = generator.nextInt(10) + 10;
            x = maxX;
            y = generator.nextInt(maxY) - bitmap.getHeight();
        }

        hitBox.left = x;
        hitBox.right = x + bitmap.getWidth();
        hitBox.top = y;
        hitBox.bottom = y + bitmap.getHeight();
    }

    public void scaleBitmap(int x) {
        if (x < 1000) {
            bitmap = Bitmap.createScaledBitmap(bitmap,
                    bitmap.getWidth() / 3,
                    bitmap.getHeight() / 3,
                    false);
        } else {
            bitmap = Bitmap.createScaledBitmap(bitmap,
                    bitmap.getWidth() / 2,
                    bitmap.getHeight() / 2,
                    false);
        }
    }
}
