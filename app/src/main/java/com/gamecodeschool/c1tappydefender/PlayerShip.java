package com.gamecodeschool.c1tappydefender;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

public class PlayerShip {

    private Bitmap bitmap;
    private int x;
    private int y;
    private int speed = 0;
    private boolean boosting;

    private int minY;
    private int maxY;

    private int GRAVITY = -12;
    private int MIN_SPEED = 1;
    private int MAX_SPEED = 20;

    private Rect hitBox;

    private int shieldStrength;

    public PlayerShip(Context context, int screenX, int screenY) {
        x = 50;
        y = 50;
        speed = 1;
        boosting = false;
        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ship);
        minY = 0;
        maxY = screenY - bitmap.getHeight();
        hitBox = new Rect(x,y,bitmap.getWidth(),bitmap.getHeight());
        shieldStrength = 2;
        scaleBitmap(screenX);
    }

    public void update() {
        if (boosting) {
            //speed up
            speed += 2;
        } else {
            //slow down
            speed -= 5;
        }

        if (speed > MAX_SPEED) {
            speed = MAX_SPEED;
        }

        if (speed < MIN_SPEED) {
            speed = MIN_SPEED;
        }

        y -= speed + GRAVITY;

        if (y < minY) {
            y = minY;
        }

        if (y > maxY) {
            y = maxY;
        }


        hitBox.left = x;
        hitBox.right = x + bitmap.getWidth();
        hitBox.top = y;
        hitBox.bottom = y + bitmap.getHeight();
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public int getSpeed() {
        return speed;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getShieldStrength() {
        return shieldStrength;
    }

    public void reduceShieldStrength() {
        shieldStrength--;
    }

    public Rect getHitBox() {
        return hitBox;
    }

    public void stopBoosting() {
        boosting = false;
    }

    public void setBoosting() {
        boosting = true;
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
