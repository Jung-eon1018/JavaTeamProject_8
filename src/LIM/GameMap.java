package LIM;

import java.util.*;

public class GameMap {
    // 1 : 몹 나오는 구간
    // 2 : 스폰
    // 3 : 보물방
    // 4 : 상점 // 일단 삭제함
    // 5 :보스
    public int[][][] maparr;
    public int[][][] clearmap;
    public List<Entity> map1e;
    public List<Entity> map2e;
    public List<Entity> map3e;
    public List<Entity> map4e;

    public GameMap(){
        this.maparr = new int[][][]
              {{{0, 0, 0, 0}, // lvl 1
                {0, 0, 0, 0},
                {0, 0, 0, 0}},


                {{0, 0, 0, 0, 0}, // lvl 2
                {0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0}},


                {{0, 0, 0, 0, 0}, // lvl 3
                {0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0}},

                {{0, 0, 0, 0, 0, 0}, // lvl 4
                {0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0}}
        };

        this.clearmap = new int[][][]
                {{{0, 0, 0, 0}, // lvl 1
                {0, 0, 0, 0},
                {0, 0, 0, 0}},


                {{0, 0, 0, 0, 0}, // lvl 2
                {0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0}},


                {{0, 0, 0, 0, 0}, // lvl 3
                {0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0}},

                {{0, 0, 0, 0, 0, 0}, // lvl 4
                {0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0}}
        };
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (int lvl = 0; lvl < maparr.length; lvl++) {
            for (int row = 0; row < maparr[lvl].length; row++) {
                sb.append("[");
                for (int col = 0; col < maparr[lvl][row].length; col++) {
                    sb.append(maparr[lvl][row][col]);
                    if (col < maparr[lvl][row].length - 1) sb.append(", ");
                }
                sb.append("]");
                sb.append("\n");
            }
            if (lvl < maparr.length - 1) sb.append("\n"); // 레벨 사이 한 줄 띄우기
        }

        return sb.toString();
    }
}
