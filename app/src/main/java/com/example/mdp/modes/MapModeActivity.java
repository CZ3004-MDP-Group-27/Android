package com.example.mdp.modes;

import static android.os.SystemClock.sleep;
import static com.example.mdp.R.menu.menu_grid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mdp.R;
import com.example.mdp.SplashActivity;
import com.example.mdp.bluetooth.BTService;
import com.example.mdp.bluetooth.messageActivity;
import com.example.mdp.map.ExplorationArea;
import com.example.mdp.map.MapCanvas;
import com.example.mdp.map.Obstacle;
import com.example.mdp.map.Robot;

import java.lang.annotation.Target;
import java.util.ArrayList;

public class MapModeActivity extends AppCompatActivity {
    Button startStopwatch, stopStopwatch, resetStopwatch, sendPos;
    TextView stopwatchText, statusText;
    private boolean isResume;
    Handler handler, statusHandler;
    long tMilliSec, tStart, tBuff, tUpdate = 0L;
    int milliSec, sec, min;
    MapCanvas mapCanvas;
    private static ExplorationArea _map = new ExplorationArea();
    String command, outgoing;
    Context context;

    private static int xOffset = 0;
    private static int yOffset = 0;
    private static Robot robot = _map.getRobo();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setCustomView(R.layout.abs_layout);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_mode);
        context = this;

        mapCanvas = findViewById(R.id.pathGrid);
        _map = mapCanvas.getFinder();

        stopwatchText = findViewById(R.id.imageStopwatch);
//        sendPos = findViewById(R.id.sendObstaclePos);
        startStopwatch = findViewById(R.id.startImageRecognition);
        stopStopwatch = findViewById(R.id.stopImageRecognition);
        resetStopwatch = findViewById(R.id.resetImageRecognition);
        statusText = findViewById(R.id.statusText);
        statusText.setText(SplashActivity.status);

        handler = new Handler();

        if (BTService.BluetoothConnectionStatus) {

            startStopwatch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mapCanvas.setSolving(true);
                    Log.d("MAP MODE","START IMAGE RECOGNITION CHALLENGE");
                    command = "map-ROB:15,15;";
                    int xPos, yPos, direction, number;
                    for (int i = 0; i < _map.getTargets().size(); i++) {
                        xPos = (_map.getTargets().get(i).getX()) * 10 - 5;
                        yPos = (21 - _map.getTargets().get(i).getY()) * 10 - 5;
                        direction = _map.getTargets().get(i).getF() * 90;
                        number = i +1;
                        command += "OBS" + number + ":" + xPos + "," + yPos + "," + direction + ";";
                    }
                    command += "\n";
                    BTService.sendMessage(command);
                    outgoing = "twenty-seven: " + command + "\n";
                    messageActivity.chatHistory += outgoing;
//                    BTService.sendMessage("pc-startImage");
//                    outgoing = "twenty-seven: startImage\n";
                    messageActivity.chatHistory += outgoing;
                    if (!isResume) {
                        stopwatchText.setText("");
                        tStart = SystemClock.uptimeMillis();
                        handler.postDelayed(runnable, 0);
                        isResume = true;
                        startStopwatch.setVisibility(View.GONE);
                        stopStopwatch.setVisibility(View.VISIBLE);
                    }
                }
            });

            stopStopwatch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mapCanvas.setSolving(false);
                    Log.d("MAP MODE","END IMAGE RECOGNITION CHALLENGE");
                    BTService.sendMessage("stopImage");
                    outgoing = "twenty-seven: stopImage\n";
                    messageActivity.chatHistory += outgoing;
                    if (isResume) {
                        tBuff += tMilliSec;
                        handler.removeCallbacks(runnable);
                        isResume = false;
                        stopStopwatch.setVisibility(View.GONE);
                        resetStopwatch.setVisibility(View.VISIBLE);
                    }
                }
            });

            resetStopwatch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("MAP MODE","STOPWATCH RESET");
                    if (!isResume) {
                        tMilliSec = tBuff = tStart = tUpdate = 0L;
                        milliSec = sec = min = 0;
                        stopwatchText.setText(R.string.stopwatch_default);
                        resetStopwatch.setVisibility(View.GONE);
                        startStopwatch.setVisibility(View.VISIBLE);
                    }
                }
            });
        } else {
            Toast.makeText(context, "App is not connected to robot.", Toast.LENGTH_SHORT).show();
        }


    }

    public Runnable runnable = new Runnable() {
        @SuppressLint({"DefaultLocale", "SetTextI18n"})
        @Override
        public void run() {
            tMilliSec = SystemClock.uptimeMillis() - tStart;
            tUpdate = tBuff + tMilliSec;
            sec = (int) (tUpdate / 1000);
            min = sec / 60;
            sec = sec % 60;
            milliSec = (int) (tUpdate % 100);
            stopwatchText.setText(String.format("%02d", min) + ":" + String.format("%02d", sec) + "." +String.format("%02d", milliSec));
            handler.postDelayed(this, 60);
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(menu_grid, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.resetGrid:
                _map.resetGrid();
                Log.d("MAP MODE","resetGrid");
//                BTService.sendMessage("pc-Obstacles:cleared,Robot:origin");
                outgoing = "twenty-seven: resetGrid\n";
                messageActivity.chatHistory += outgoing;
                return true;
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    static void forward(double xFlag, double yFlag, int distance, int currentX, int currentY) {
        if(xFlag != 0 && distance % 10 != 0)  {
            distance = (int) (xFlag * distance + xOffset);
            xOffset = distance % 10;
        } else if (yFlag != 0 && distance % 10 != 0){
            distance = (int) (yFlag * distance + yOffset);
            yOffset = distance % 10;
        }
        for (int i = 0; i <= Math.abs(distance/10); i++) {
            robot.setX((int) (currentX + xFlag));
            robot.setY((int) (currentY - yFlag));
//            sleep(2000);
        }
    }

    static void backward(double xFlag, double yFlag, int distance, int currentX, int currentY) {
        if(xFlag != 0 && distance % 10 != 0)  {
            distance =  - (int) (xFlag * distance + xOffset);
            xOffset = distance % 10;
        } else if (yFlag != 0 && distance % 10 != 0){
            distance = - (int) (yFlag * distance + yOffset);
            yOffset = distance % 10;
        }
        for (int i = 0; i <= Math.abs(distance/10); i++) {
            robot.setX((int) (currentX - xFlag));
            robot.setY((int) (currentY + yFlag));
//            sleep(2000);
        }
    }

    static void inplaceRight(int currentDir) {
//        sleep(1600);
        if (currentDir > 0) {
            robot.setFacing(currentDir - 1);
        }
        else {
            robot.setFacing(3);
        }
    }

    static  void inplaceLeft(int currentDir) {
        sleep(1600);
        if (currentDir < 3) {
            robot.setFacing(currentDir + 1);
        }
        else {
            robot.setFacing(0);
        }
    }

    public static void moveRobot(String instructions) {
        Log.d("MapMode", "moveRobot: " + instructions);
        // FORWARD 15
        String[] instruction = instructions.split(" ",2);
        robot.cycleFace(true);
        robot.setY(Integer.parseInt(instruction[0]));
        robot.setX(Integer.parseInt(instruction[1]));
//        int distance = 0;
//        // Add code for robot pos based on algo instructions
//        // e.g. forward 15, ForwardTurnLeft, BACkwardTUrnLEFT,InplaceLeft, backward 15
//        // Speed 2.363s / 10cm
//        // Turn 20 35
//        // Example:
//        int currentX = robot.getX();
//        int currentY = robot.getY();
//        Log.d("MapMode", "Current X,Y: " + currentX + ", " +currentY);
//        int currentDir = robot.getFacing();
//        if (instruction.length > 1) {
//            distance = Integer.parseInt(instruction[1]);
//        }
//        double radians = Math.toRadians(currentDir * 90);
//        double xFlag = Math.cos(radians);
//        double yFlag = Math.sin(radians);
//        switch (instruction[0]) {
//            case "FORWARD":
//                // Flags, Distance, offsets
//                forward(xFlag, yFlag, distance, currentX, currentY);
//                break;
//            case "FORWARDTURNLEFT":
//                forward(xFlag, yFlag, 20, currentX, currentY);
//                inplaceLeft(currentDir);
//                forward(xFlag, yFlag, 35, currentX, currentY);
//                break;
//            case "FORWARDTURNRIGHT":
//                forward(xFlag, yFlag, 20, currentX, currentY);
//                inplaceRight(currentDir);
//                forward(xFlag, yFlag, 35, currentX, currentY);
//                break;
//            case "BACKWARD":
//                backward(xFlag, yFlag, distance, currentX, currentY);
//                break;
//            case "BACKWARDTURNLEFT":
//                backward(xFlag, yFlag, 20, currentX, currentY);
//                inplaceLeft(currentDir);
//                backward(xFlag, yFlag, 35, currentX, currentY);
//                break;
//            case "BACKWARDTURNRIGHT":
//                backward(xFlag, yFlag, 20, currentX, currentY);
//                inplaceRight(currentDir);
//                backward(xFlag, yFlag, 35, currentX, currentY);
//                break;
//            case "INPLACELEFT":
//                inplaceLeft(currentDir);
//                break;
//            case "INPLACERIGHT":
//                inplaceRight(currentDir);
//                break;
//        }
    }

    public static void updateTarget(String information) {
        Log.d("MapMode", "updateTarget: " + information);
        String[] targetImage = information.split(",",2);
        ArrayList<Obstacle> targets = _map.getTargets();
        targets.get(Integer.parseInt(targetImage[0])).setImg(Integer.parseInt(targetImage[1]));
    }
}