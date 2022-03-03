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
    TextView stopwatchText;
    private static TextView statusText;
    private boolean isResume;
    Handler handler;
    long tMilliSec, tStart, tBuff, tUpdate = 0L;
    int milliSec, sec, min;
    MapCanvas mapCanvas;
    private static ExplorationArea _map = new ExplorationArea();
    String command, outgoing;
    Context context;
    private static Handler movementHandler = new Handler();
    private static int xOffset = 0;
    private static int yOffset = 0;
//    private static Robot robot = _map.getRobo();


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
                    updateStatusText("Task 1 Begin");
                    Log.d("MAP MODE","START IMAGE RECOGNITION CHALLENGE");
                    command = "map-ROB:15,15;";
                    int xPos, yPos, direction, number;
                    for (int i = 0; i < _map.getTargets().size(); i++) {
                        xPos = (_map.getTargets().get(i).getX()) * 10 - 5;
                        yPos = (21 - _map.getTargets().get(i).getY()) * 10 - 5;
                        direction = _map.getTargets().get(i).getF() * 90;
                        number = i +1;
                        command += "OBS" + number + ":" + xPos + "," + yPos + "," + direction;
                        if (i != _map.getTargets().size() - 1) {
                            command += ";";
                        }
                    }
//                    command += "\n";
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
                    updateStatusText("Stopped");
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

    static void forward(int distance, boolean isForwardTurnRight, boolean isForwardTurnLeft) {
        int reps = Math.abs(distance/10);
        int currentDir = _map.getRobo().getFacing();
        if (isForwardTurnRight) {
            if (currentDir > 0) {
                currentDir = currentDir - 1;
            }
            else {
                currentDir = 3;
            }
        } else if (isForwardTurnLeft) {
            if (currentDir < 3) {
                currentDir = currentDir + 1;
            }
            else {
                currentDir = 0;
            }
        }
        double radians = Math.toRadians(currentDir * 90);
        int xFlag =  (int) Math.cos(radians);
        int yFlag = (int) Math.sin(radians);
        if(xFlag != 0 && distance % 10 != 0)  {
            distance = xFlag * distance + xOffset;
            xOffset = distance % 10;
        } else if (yFlag != 0 && distance % 10 != 0){
            distance = yFlag * distance + yOffset;
            yOffset = distance % 10;
        }
        for (int i = 0; i < reps; i++) {
            int delay;
            if (isForwardTurnRight || isForwardTurnLeft) {
                delay = 560 + 200 * (i + 1); // Need to edit time for turn
            } else {
                delay = 200 * (i + 1);
            }
            movementHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    _map.getRobo().setY(_map.getRobo().getY() - yFlag);
                    _map.getRobo().setX(_map.getRobo().getX() + xFlag);
                    Log.d("Forward", "Current X,Y: " + _map.getRobo().getX() + ", " + _map.getRobo().getY() + "Facing: " + _map.getRobo().getFacingText() + " (" + _map.getRobo().getFacing() + ")");
                    updateStatusText("Moving forward");
                }
            }, delay);
        }

    }

    static void backward(int distance, boolean isBackwardTurnRight, boolean isBackwardTurnLeft) {
        int currentDir = _map.getRobo().getFacing();
        if (isBackwardTurnRight) {
            if (currentDir < 3) {
                currentDir = currentDir + 1;
            }
            else {
                currentDir = 0;
            }
        } else if (isBackwardTurnLeft) {
            if (currentDir > 0) {
                currentDir = currentDir - 1;
            }
            else {
                currentDir = 3;
            }
        }
        double radians = Math.toRadians(currentDir * 90);
        int xFlag = (int) Math.cos(radians);
        int yFlag = (int) Math.sin(radians);
        if(xFlag != 0 && distance % 10 != 0)  {
            distance =  -xFlag * distance + xOffset;
            xOffset = distance % 10;
        } else if (yFlag != 0 && distance % 10 != 0){
            distance = -yFlag * distance + yOffset;
            yOffset = distance % 10;
        }
        for (int i = 0; i < Math.abs(distance/10); i++) {
            int delay;
            if (isBackwardTurnRight || isBackwardTurnLeft) {
                delay = 560 + 200 * (i + 1);// Need to edit time for turn
            } else {
                delay = 200 * (i + 1);
            }
            movementHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    _map.getRobo().setY(_map.getRobo().getY() + yFlag);
                    _map.getRobo().setX(_map.getRobo().getX() - xFlag);
                    Log.d("Forward", "Current X,Y: " + _map.getRobo().getX() + ", " + _map.getRobo().getY() + "Facing: " + _map.getRobo().getFacingText() + " (" + _map.getRobo().getFacing() + ")");
                    updateStatusText("Reversing");
                }
            }, delay);
        }
    }

    static void inplaceRight(boolean notInplace) {
        int currentDir = _map.getRobo().getFacing();
        int delay;
        if (notInplace) {
            delay = 560;
        } else {
            delay = 160;
        }
        movementHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (currentDir > 0) {
                    _map.getRobo().setFacing(currentDir - 1);
                }
                else {
                    _map.getRobo().setFacing(3);
                }
                Log.d("Forward", "Current X,Y: " + _map.getRobo().getX() + ", " + _map.getRobo().getY() + "Facing: " + _map.getRobo().getFacingText() + " (" + _map.getRobo().getFacing() + ")");
                updateStatusText("In-place right turn");
            }
        }, delay);

    }

    static  void inplaceLeft(boolean notInplace) {
        int currentDir = _map.getRobo().getFacing();
        int delay;
        if (notInplace) {
            delay = 560;
        } else {
            delay = 160;
        }
        movementHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (currentDir < 3) {
                    _map.getRobo().setFacing(currentDir + 1);
                }
                else {
                    _map.getRobo().setFacing(0);
                }
                Log.d("Forward", "Current X,Y: " + _map.getRobo().getX() + ", " + _map.getRobo().getY() + "Facing: " + _map.getRobo().getFacingText() + " (" + _map.getRobo().getFacing() + ")");
                updateStatusText("In-place left turn");
            }
        }, delay);

    }

    public static void moveRobot(String instructions) {
        Log.d("MapMode", "moveRobot: " + instructions);
        String[] instruction = instructions.split(" ",2);
        int distance = 0;
        // Speed 23.63ms / 10cm
        // Turn 20 35
        int currentX = _map.getRobo().getX();
        int currentY = _map.getRobo().getY();
        int currentDir = _map.getRobo().getFacing();
        Log.d("MapMode", "Current X,Y: " + currentX + ", " +currentY + "Facing: " + currentDir);
        if (instruction.length > 1) {
            distance = Integer.parseInt(instruction[1]);
        }
        switch (instruction[0]) {
            case "FORWARD":
                // Flags, Distance, offsets
                forward(distance, false,false);

                break;
            case "FORWARDTURNLEFT":
                forward(20,false,false);
                inplaceLeft(true);
                forward(35,false,true);
                break;
            case "FORWARDTURNRIGHT":
                forward(20,false,false);
                inplaceRight(true);
                forward(35,true, false);
                break;
            case "BACKWARD":
                backward(distance,false,false);
                break;
            case "BACKWARDTURNLEFT":
                backward(20,false,false);
                inplaceRight(true);
                backward(35,false,true);
                break;
            case "BACKWARDTURNRIGHT":
                backward(20,false,false);
                inplaceLeft(true);
                backward(35,true,false);
                break;
            case "INPLACELEFT":
                inplaceLeft(false);
                break;
            case "INPLACERIGHT":
                inplaceRight(false);
                break;
        }
    }

    public static void updateTarget(String information) {
        Log.d("MapMode", "updateTarget: " + information);
        String[] targetImage = information.split(",",2);
        int obstacleId = Integer.parseInt(targetImage[0]) - 1;
        int imageId = Integer.parseInt(targetImage[1]);
        ArrayList<Obstacle> targets = _map.getTargets();
        targets.get(obstacleId).setImg(imageId);
        updateStatusText("Image on Obstacle" + obstacleId + 1 + " recognised  as Image ID: " + imageId);
    }

    @SuppressLint("SetTextI18n")
    public static void updateStatusText(String status) {
        statusText.setText("Status: " + status);
    }
}