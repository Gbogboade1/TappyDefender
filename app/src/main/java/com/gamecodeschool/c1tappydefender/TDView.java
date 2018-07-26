package com.gamecodeschool.c1tappydefender;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;

public class TDView extends SurfaceView implements Runnable {
    volatile boolean playing;

    Thread gameThread = null;
    private Paint paint;
    private Canvas canvas;
    private SurfaceHolder ourHolder;

    private PlayerShip player;
    public EnemyShip enemy1;
    public EnemyShip enemy2;
    public EnemyShip enemy3;
    public EnemyShip enemy4;
    public EnemyShip enemy5;

    public ArrayList<SpaceDust> dustList = new ArrayList<>();
    int numSpecs = 50;
    int screenX;
    int screenY;

    private float distanceRemaining;
    private long timeTaken;
    private long timeStarted;
    private long fastestTime;

    //ADD SOUND
    private SoundPool soundPool;
    int start = -1;
    int bump = -1;
    int destroyed = -1;
    int win = -1;


    private boolean hitDetected = false;

    private boolean gameEnded;

    private Context context;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor sharedPrefEditor;

    public final static String FASTEST_TIME = "fastestTime";
    public final static String HIGH_SCORES = "HighScores";


    public TDView(Context context, int x, int y) {
        super(context);
        this.context = context;

        initializeSoundPool();

        screenX = x;
        screenY = y;
        ourHolder = getHolder();
        paint = new Paint();

        startGame();

        sharedPreferences = context.getSharedPreferences(HIGH_SCORES, Context.MODE_PRIVATE);
        sharedPrefEditor = sharedPreferences.edit();

        fastestTime = sharedPreferences.getLong(FASTEST_TIME, 1000000);
    }

    private void initializeSoundPool() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_GAME).build();
            soundPool = new SoundPool.Builder().
                    setMaxStreams(2)
                    .setAudioAttributes(audioAttributes)
                    .build();
            start = soundPool.load(context, R.raw.start, 1);
            win = soundPool.load(context, R.raw.win, 1);
            destroyed = soundPool.load(context, R.raw.destroyed, 1);
            bump = soundPool.load(context, R.raw.bump, 1);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_UP:
                player.stopBoosting();
                break;
            case MotionEvent.ACTION_DOWN:
                player.setBoosting();
                if (gameEnded) {
                    startGame();
                }
                break;
        }
        return true;
    }

    @Override
    public void run() {
        while (playing) {
            update();
            draw();
            control();
        }
    }

    private void startGame() {

        soundPool.play(start, 1, 1, 0, 0, 1);

        player = new PlayerShip(context, screenX, screenY);
        enemy1 = new EnemyShip(context, screenX, screenY);
        enemy2 = new EnemyShip(context, screenX, screenY);
        enemy3 = new EnemyShip(context, screenX, screenY);

        if (screenX > 1000) {
            enemy4 = new EnemyShip(context, screenX, screenY);
        }

        if (screenX > 1200) {
            enemy5 = new EnemyShip(context, screenX, screenY);
        }

        dustList = new ArrayList<>();

        for (int i = 0; i < numSpecs; i++) {
            SpaceDust dust = new SpaceDust(screenX, screenY);
            dustList.add(dust);
        }

        distanceRemaining = 10000;//10km
        timeTaken = 0;

        timeStarted = System.currentTimeMillis();

        gameEnded = false;
    }

    public void pause() {
        playing = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {

        }
    }

    public void resume() {
        playing = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    private void draw() {
        if (ourHolder.getSurface().isValid()) {
            canvas = ourHolder.lockCanvas();
            canvas.drawColor(Color.argb(255, 0, 0, 0));

            paint.setColor(Color.argb(255, 255, 255, 255));

            for (SpaceDust d : dustList) {
                canvas.drawPoint(d.getX(), d.getY(), paint);
            }

            canvas.drawBitmap(player.getBitmap(),
                    player.getX(),
                    player.getY(),
                    paint);

            canvas.drawBitmap(
                    enemy1.getBitmap(),
                    enemy1.getX(),
                    enemy1.getY(),
                    paint
            );
            canvas.drawBitmap(
                    enemy2.getBitmap(),
                    enemy2.getX(),
                    enemy2.getY(),
                    paint
            );
            canvas.drawBitmap(
                    enemy3.getBitmap(),
                    enemy3.getX(),
                    enemy3.getY(),
                    paint
            );

            if (!gameEnded) {
                //draw the hud
                paint.setTextAlign(Paint.Align.LEFT);
                paint.setColor(Color.argb(255, 255, 255, 255));
                paint.setTextSize(25);
                canvas.drawText("Fastest: " + fastestTime + "s", 10, 20, paint);
                canvas.drawText("Time: " + formatTime(timeTaken) + "s", screenX / 2, 20, paint);
                canvas.drawText("Distance: " + distanceRemaining / 1000 + " KM",
                        screenX / 3, screenY - 20, paint);
                canvas.drawText("Shield: " + player.getShieldStrength(),
                        10, screenY - 20, paint);
                canvas.drawText("Speed: " + player.getSpeed() * 60 + " MPS",
                        (screenX / 3) * 2, screenY - 20, paint);
            } else {
                paint.setTextSize(80);
                paint.setTextAlign(Paint.Align.CENTER);
                canvas.drawText("Game Over", screenX / 2, 100, paint);
                paint.setTextSize(25);
                canvas.drawText("Fastest: " + fastestTime + "s", screenX / 2, 160, paint);
                canvas.drawText("Time: " + formatTime(timeTaken) + "s", screenX / 2, 200, paint);
                canvas.drawText("Distance Remainig: " + distanceRemaining / 1000 + "KM", screenX / 2, 240, paint);

                paint.setTextSize(80);
                canvas.drawText("Tap to replay!", screenX / 2, 350, paint);
            }

            ourHolder.unlockCanvasAndPost(canvas);
        }
    }

    private void control() {
        try {
            Thread.sleep(17);
        } catch (InterruptedException e) {

        }
    }

    private void update() {

        if (Rect.intersects(player.getHitBox(), enemy1.getHitBox())) {
            hitDetected = true;
            enemy1.setX(-100);
        }

        if (Rect.intersects(player.getHitBox(), enemy2.getHitBox())) {
            hitDetected = true;
            enemy2.setX(-100);
        }

        if (Rect.intersects(player.getHitBox(), enemy3.getHitBox())) {
            hitDetected = true;
            enemy3.setX(-100);
        }
        if (screenX > 1000) {
            if (Rect.intersects(player.getHitBox(), enemy4.getHitBox())) {
                hitDetected = true;
                enemy4.setX(-100);
            }
        }


        if (screenX > 1200) {
            if (Rect.intersects(player.getHitBox(), enemy5.getHitBox())) {
                hitDetected = true;
                enemy5.setX(-100);
            }
        }


        if (hitDetected) {
            soundPool.play(bump, 1, 1, 0, 0, 1);
            player.reduceShieldStrength();
            if (player.getShieldStrength() < 0) {
                soundPool.play(destroyed, 1, 1, 0, 0, 1);
                gameEnded = true;
            }
            hitDetected = false;
        }


        player.update();
        enemy1.update(player.getSpeed());
        enemy2.update(player.getSpeed());
        enemy3.update(player.getSpeed());

        for (SpaceDust dust : dustList) {
            dust.update(player.getSpeed());
        }

        if (!gameEnded) {
            distanceRemaining -= player.getSpeed();
            timeTaken = System.currentTimeMillis() - timeStarted;
        }

        if (distanceRemaining < 0) {
            soundPool.play(win, 1, 1, 0, 0, 1);
            if (timeTaken < fastestTime) {
                sharedPrefEditor.putLong(FASTEST_TIME, timeTaken);
                sharedPrefEditor.commit();
                fastestTime = timeTaken;
            }

            distanceRemaining = 0;
            gameEnded = true;
        }
    }

    private String formatTime(long time) {
        long second = (time) / 1000;
        long thousandth = (time) - (second * 1000);
        String strThousandths = "" + thousandth;
        if (thousandth < 100) {
            strThousandths = "0" + thousandth;
        }
        if (thousandth < 10) {
            strThousandths = "0" + strThousandths;
        }
        String stringTime = "" + second + "." + strThousandths;
        return stringTime;
    }

}
