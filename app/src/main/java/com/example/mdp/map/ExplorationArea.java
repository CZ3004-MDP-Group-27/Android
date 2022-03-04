package com.example.mdp.map;

import static com.example.mdp.map.Robot.ROBOT_FACE_NORTH;
import static com.example.mdp.map.Obstacle.TARGET_IMG_NULL;

import java.util.ArrayList;

public class ExplorationArea {
    Robot robo;
    ArrayList<Obstacle> obstacles = new ArrayList<Obstacle>();
    Obstacle lastTouched;

    private int rows = 21;
    private int cols = 21;

    int[][] board = new int[rows][cols];

    public static final int EMPTY_CELL_CODE = 0;
    public static final int CAR_CELL_CODE = 1;
    public static final int TARGET_CELL_CODE = 3;
    public static final int EXPLORE_CELL_CODE = 4;
    public static final int EXPLORE_HEAD_CELL_CODE = 5;
    public static final int FINAL_PATH_CELL_CODE = 6;

    public ExplorationArea() {
        super();
        robo = new Robot(this);
        board[robo.getX()][robo.getY()] = CAR_CELL_CODE;
        board[robo.getX()][robo.getY()+1] = CAR_CELL_CODE;
        board[robo.getX()][robo.getY()-1] = CAR_CELL_CODE;
        board[robo.getX()+1][robo.getY()] = CAR_CELL_CODE;
        board[robo.getX()+1][robo.getY()+1] = CAR_CELL_CODE;
        board[robo.getX()+1][robo.getY()-1] = CAR_CELL_CODE;
        board[robo.getX()-1][robo.getY()] = CAR_CELL_CODE;
        board[robo.getX()-1][robo.getY()+1] = CAR_CELL_CODE;
        board[robo.getX()-1][robo.getY()-1] = CAR_CELL_CODE;

    }

    public final void resetGrid() {
        for(int i = 1; i <= 19; ++i)
            for(int j = 1; j <= 19; ++j)
                this.board[i][j] = 0;

        this.getRobo().setX(1);
        this.getRobo().setY(19);
        this.getRobo().setFacing(ROBOT_FACE_NORTH);
        obstacles.clear();
        this.board[getRobo().getX()][getRobo().getY()] = CAR_CELL_CODE;
        this.board[robo.getX()][robo.getY()+1] = CAR_CELL_CODE;
        this.board[robo.getX()][robo.getY()-1] = CAR_CELL_CODE;
        this.board[robo.getX()+1][robo.getY()] = CAR_CELL_CODE;
        this.board[robo.getX()+1][robo.getY()+1] = CAR_CELL_CODE;
        this.board[robo.getX()+1][robo.getY()-1] = CAR_CELL_CODE;
        this.board[robo.getX()-1][robo.getY()] = CAR_CELL_CODE;
        this.board[robo.getX()-1][robo.getY()+1] = CAR_CELL_CODE;
        this.board[robo.getX()-1][robo.getY()-1] = CAR_CELL_CODE;
    }

    public Robot getRobo() { return robo;}

    public ArrayList<Obstacle> getTargets() {
        return obstacles;
    }

    public boolean hasReceivedAllTargets() {
        int targetReceived = 0;
        for(int i = 0; i < obstacles.size(); i++) {
            if (obstacles.get(i).getImg() > TARGET_IMG_NULL) {
                targetReceived++;
            }
        }
        return targetReceived == obstacles.size();
    }

    public void defaceTargets() {
        for(int i = 0; i < obstacles.size(); i++) {
            obstacles.get(i).setImg(TARGET_IMG_NULL);
        }
    }

    public void dequeueTarget(Obstacle t) {
        int delTargetIdx = t.getN();

        obstacles.remove(t.getN());

        while (delTargetIdx < obstacles.size()) {
            obstacles.get(delTargetIdx).setN(delTargetIdx);
            delTargetIdx++;
        }
    }

    public Obstacle getLastTouchedTarget() {
        return lastTouched;
    }

    public void setLastTouchedTarget(Obstacle lastT) {
        this.lastTouched = lastT;
    }

    public Obstacle findTarget(int x, int y) {
        int n = 0;
        while (n < obstacles.size()) {
            if (obstacles.get(n).getX() == x && obstacles.get(n).getY() == y)
                return obstacles.get(n);

            n++;
        }
        return null;
    }

    public int[][] getBoard() {
        return board;
    }
}
