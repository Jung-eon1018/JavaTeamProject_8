package LIM;

import java.util.*;


public class Gamemanager {
    public GameMap map = new GameMap();
    public List<Entity> enemies = new ArrayList<>();
    Random rd = new Random();

    public void MapSetting(){
        int[] spawn = {0, 0, 0, 0};
        int[] boss = {0, 0, 0, 0};

        List<List<Integer>> gapchoice = new ArrayList<>();
        // 스폰 & 보스방 설정
        for(int i = 0; i < 4; i++){
            int spawnpoint = rd.nextInt(map.maparr[i].length);
            map.maparr[i][spawnpoint][0] = 2;
            spawn[i] = spawnpoint;

            int bosspoint = rd.nextInt(map.maparr[i].length);
            map.maparr[i][bosspoint][map.maparr[i][0].length-1] = 5;
            boss[i] = bosspoint;
        }

        for(int i = 0; i < 4; i++){
            gapchoice.add(new ArrayList<>()); // 레벨용 리스트 하나 생성
             // 실제로 꺾일 부분

            int gap = Math.abs(spawn[i] - boss[i]);
            List<Integer> cur = gapchoice.get(i);// 보스~스폰의 위아래 높이차
            while(cur.size() < gap){ // 방의 행 중 gap만큼을 고름
                int temp = rd.nextInt(map.maparr[i][0].length);
                if(cur.contains(temp)){
                    continue;
                } else{
                    cur.add(temp);
                }
            }
        }

        for(int i = 0; i < 4; i++){
            int changecount = 0;
            if(spawn[i] - boss[i] < 0){ // 상승해야 한다면
                for(int j = 0; j < map.maparr[i][0].length; j++){
                    if(map.maparr[i][spawn[i]+changecount][j] == 0 && gapchoice.get(i).contains(j)) {
                        map.maparr[i][spawn[i]+changecount][j] = 1; // gap인 지점 : 전 줄과 같은 높이 & 높이 +-1에 적 생성
                        changecount++;
                    }

                    if(map.maparr[i][spawn[i]+changecount][j] == 0) map.maparr[i][spawn[i]+changecount][j] = 1;
                }

            } else if(spawn[i] - boss[i] > 0){
                for(int j = 0; j < map.maparr[i][0].length; j++){
                    if(map.maparr[i][spawn[i]+changecount][j] == 0 && gapchoice.get(i).contains(j)) {
                        map.maparr[i][spawn[i]+changecount][j] = 1; // gap인 지점 : 전 줄과 같은 높이 & 높이 +-1에 적 생성
                        changecount--;
                    }

                    if(map.maparr[i][spawn[i]+changecount][j] == 0) map.maparr[i][spawn[i]+changecount][j] = 1;
                }
            } else{ // gap == 0
                for(int j = 1; j < map.maparr[i][0].length-1; j++){
                    map.maparr[i][spawn[i]+changecount][j] = 1;
                }
            }
        } // 여기까지 하면 시작~끝, 이를 잇는 길이 만들어짐

        for(int i = 0; i < 4; i++) { // 보물, 상점 가는 용도
            List<Integer[]> near = new ArrayList<>();
            for (int j = 0; j < map.maparr[i].length; j++) {
                for (int k = 0; k < map.maparr[i].length; k++) {
                    if (map.maparr[i][j][k] == 1) near.add(new Integer[]{j, k}); // 방이 1인 위치 전부 모음
                }
            }
            int first;
            int second;

            while (true) { // 보물방 설정
                first = rd.nextInt(near.size());
                for (int j = 0; j < 4; j++) {

                }
                if ((near.get(first)[0] - 1) >= 0 && // 위가 비어있냐
                        map.maparr[i][near.get(first)[0] - 1][near.get(first)[1]] == 0) {
                    map.maparr[i][near.get(first)[0] - 1][near.get(first)[1]] = 3;
                    break;
                } else if ((near.get(first)[0] + 1) < map.maparr[i].length && // 아래
                        map.maparr[i][near.get(first)[0] + 1][near.get(first)[1]] == 0) {
                    map.maparr[i][near.get(first)[0] + 1][near.get(first)[1]] = 3;
                    break;
                } else if ((near.get(first)[1] - 1) >= 0 && // 왼쪽
                        map.maparr[i][near.get(first)[0]][near.get(first)[1] - 1] == 0) {
                    map.maparr[i][near.get(first)[0]][near.get(first)[1] - 1] = 3;
                    break;
                } else if ((near.get(first)[1] + 1) < map.maparr[i][0].length && // 오른쪽
                        map.maparr[i][near.get(first)[0]][near.get(first)[1] + 1] == 0) {
                    map.maparr[i][near.get(first)[0]][near.get(first)[1] + 1] = 3;
                    break;

                } else {
                    continue;
                }

            }

        }
    }
}
