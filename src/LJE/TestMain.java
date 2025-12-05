package LJE;


import javax.swing.Timer;
import java.awt.geom.Line2D;
import java.awt.BasicStroke;

import Final.Manager_game;
import Final.Word;
import Jjh.Manager2;
import LIM.Entity;
import LIM.Gamemanager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class TestMain {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Manager2 manager = new Manager2();
        //Manager2_game game = new Manager2_game();

        File file = new File("data/word.txt");
        File wrongfile = new File("data/wrong.txt");

        try {
            if (!file.exists()) {

                file.getParentFile().mkdirs(); // data 폴더가 없으면 생성
                file.createNewFile();
                System.out.println("단어 파일이 존재하지 않아 새로 생성합니다.");
            }
        } catch (IOException e) {
            System.out.println("파일 생성 중 오류 발생: " + e.getMessage());
        }

        try {
            if (!wrongfile.exists()) {

                wrongfile.getParentFile().mkdirs(); // data 폴더가 없으면 생성
                wrongfile.createNewFile();
                System.out.println("단어 파일이 존재하지 않아 새로 생성합니다.");
            }
        } catch (IOException e) {
            System.out.println("파일 생성 중 오류 발생: " + e.getMessage());
        }

        manager.voc(file);
        //game.voc(file);
        //game.wrongvoc(wrongfile);






        {// 몹들 기본 설정



        }







        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("게임");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            Gamemanager gm = new Gamemanager();
            Manager_game wordManager = new Manager_game();
            wordManager.voc(new File("data/word.txt"));
            gm.MapSetting();                 // ★ 여기서 랜덤 맵 생성
            System.out.println(gm.map);      // 디버그용(맵 찍어보기)



            GamePanel panel = new GamePanel(gm, wordManager);  // ★ Gamemanager 넘김
            frame.add(panel);

            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

            panel.requestFocusInWindow();
        });
    }



    static class GamePanel extends JPanel {


        Image[] levelBg = new Image[4]; // 레벨 0~3용
        Image chestImg;


        //근접공격 관련
        private static final int AOE_SIZE = 150;   // 150x150 사각형
        private boolean showMeleeEffect = false;
        private long meleeEffectUntil = 0L;
        private int meleeAoeX = 0;
        private int meleeAoeY = 0;


        //방향키 눌림 상태
        private boolean upPressed = false;
        private boolean downPressed = false;
        private boolean leftPressed = false;
        private boolean rightPressed = false;


        private final int imgsize = 128;
        private final int humanhitboxx = 63; // 실제 크기
        private final int humanhitboxy = 97;

        private final int SCREEN_W = 880;
        private final int SCREEN_H = 580;

        private int roomX = 100;
        private int roomY = 80;
        private int roomW = 700;
        private int roomH = 400;

        Entity player;
        Image playerImg;
        int speed = 10;

        // 맵 생성용
        Gamemanager gm;
        int currLevel = 0;
        int currRow;        // maparr[currLevel][row][col]
        int currCol;

        // 방 타입 상수
        private static final int ROOM_EMPTY    = 0;
        private static final int ROOM_NORMAL   = 1;
        private static final int ROOM_SPAWN    = 2;
        private static final int ROOM_TREASURE = 3;
        private static final int ROOM_SHOP     = 4;
        private static final int ROOM_BOSS     = 5;

        //단어 입력 & 정답체크
        private boolean typingMode = false;
        private StringBuilder inputBuffer = new StringBuilder();

        //적 멈춤 관련
        private long enemyFrozenUntil = 0;
        private long lastFreezeTryTime = 0;
        private boolean lastAnswerCorrect = false; // true면 바로 freeze 가능하게

        // ==== 보물방 관련 ====
        private boolean treasureSolved = false;          // 이 방 보물 퀴즈 이미 끝났는지
        private boolean treasureQuizActive = false;      // 번호 입력 대기 중인지
        private String treasureQuestionEng;              // 문제: 영어 단어
        private Word[] treasureOptions = new Word[4];    // 보기 4개 (한글 뜻)
        private int treasureCorrectIndex = -1;           // 정답 인덱스(0~3)
        private StringBuilder treasureInput = new StringBuilder(); // "1~4" 입력용

        // 상자 위치 (그냥 그림용, 클릭 X)
        private Rectangle[] treasureChestRects = new Rectangle[4];

        // ==== 보스방 관련 ====
        private boolean bossSolved = false;          // 보스방 클리어 여부
        private boolean bossQuizStarted = false;     // 타임어택 진행 중인지
        private long bossTimeAttackEnd = 0;          // 타임어택 종료 시간 (ms)
        private int bossCorrectStreak = 0;           // 연속 정답 수 (4 되면 클리어)

        private String bossQuestionKor;              // 문제: 한글 뜻
        private Word[] bossOptions = new Word[4];    // 보기 4개 (영단어)
        private int bossCorrectIndex = -1;           // 정답 인덱스(0~3)

        // 보스방 UI 위치
        private Rectangle bossOreRect;
        private Rectangle bossStartButton;
        private Rectangle[] bossOptionRects = new Rectangle[4];
        private Image bossOreImg;                    // 광물 이미지



        //적들
        Random rd = new Random();
        List<Entity> enemies = new ArrayList<>();

        //타이머(몹들 움직임용)
        Timer enemyTimer;
        public final int ENEMY_SPEED = 3;

        //단어
        Manager_game wordManager;
        List<Word> quizWords;




        private List<Entity> spawnedEnemies = new ArrayList<>();

        public GamePanel(Gamemanager gm, Manager_game wordManager) {

            levelBg[0] = new ImageIcon("res/bg_level1.png").getImage(); // 레벨별 배경
            levelBg[1] = new ImageIcon("res/bg_level2.png").getImage();
            levelBg[2] = new ImageIcon("res/bg_level3.png").getImage();
            levelBg[3] = new ImageIcon("res/bg_level4.png").getImage();

            chestImg = new ImageIcon("res/chest.png").getImage();

            this.gm = gm;
            this.wordManager = wordManager;
            this.quizWords = new ArrayList<>(wordManager.getWordMap().values());


            setPreferredSize(new Dimension(SCREEN_W, SCREEN_H));
            setBackground(Color.BLACK);


            //lv 1 적
            enemies.add(new Entity("slime", 10, 5, 0));
            enemies.add(new Entity("wolf", 10, 5, 0));

            // lv 2 적
            enemies.add(new Entity("zombie1", 20, 10, 0));
            enemies.add(new Entity("skeleton1", 20, 10, 0, 2));
            enemies.add(new Entity("zombie2", 20, 12, 5));
            enemies.add(new Entity("skeleton2", 20, 12, 5, 2));
            enemies.add(new Entity("spider", 20, 12, 5));


            // lv 3 적
            enemies.add(new Entity("zombie3", 30, 15, 10));
            enemies.add(new Entity("skeleton3", 30, 15, 10, 2));
            enemies.add(new Entity("creeper", 30, 40, 15, 3));

            // lv 4 적
            enemies.add(new Entity("zombie4", 30, 20, 15));
            enemies.add(new Entity("skeleton4", 30, 20, 15));



            //스폰 위치 찾기
            int[][] levelMap = gm.map.maparr[currLevel];
            for (int r = 0; r < levelMap.length; r++) {
                for (int c = 0; c < levelMap[r].length; c++) {
                    if (levelMap[r][c] == ROOM_SPAWN) {
                        currRow = r;
                        currCol = c;
                        break;
                    }
                }
            }


            // 방 가운데에 플레이어 배치
            player = new Entity(
                    roomX + roomW / 2 - humanhitboxx / 2,
                    roomY + roomH / 2 - humanhitboxy / 2,
                    humanhitboxx,
                    humanhitboxy
            );
            player.hp = 100;
            player.dmg = 10;
            player.armor = 0;


            // 이미지 로드
            playerImg = new ImageIcon("res/steve1.png").getImage();


            // 보물방 상자 관련
            int chestW = 60;
            int chestH = 60;
            int gap = 100;
            int totalWidth = chestW * 4 + gap * 3;
            int startX = roomX + (roomW - totalWidth) / 2;
            int chestY = roomY + 40;  // 방 위에서 조금 내려온 위치

            for (int i = 0; i < 4; i++) {
                int cx = startX + i * (chestW + gap);
                treasureChestRects[i] = new Rectangle(cx, chestY, chestW, chestH);
            }

            // === 보스방 UI (광물 + 버튼 + 보기 위치) ===
            int oreSize = 80;
            int oreX = roomX + roomW / 2 - oreSize / 2;
            int oreY = roomY + roomH / 2 - oreSize / 2;
            bossOreRect = new Rectangle(oreX, oreY, oreSize, oreSize);

            // 광물 아래 시작 버튼
            bossStartButton = new Rectangle(
                    oreX + oreSize / 2 - 50,
                    oreY + oreSize + 10,
                    100,
                    30
            );

            // 보기 버튼 4개 (광물 주변)
            int btnW = 140;
            int btnH = 40;
            int offsetX = 20;
            int offsetY = 20;

            // 0: 왼쪽 위
            bossOptionRects[0] = new Rectangle(
                    oreX - btnW - offsetX,
                    oreY - btnH - offsetY,
                    btnW, btnH
            );
            // 1: 오른쪽 위
            bossOptionRects[1] = new Rectangle(
                    oreX + oreSize + offsetX,
                    oreY - btnH - offsetY,
                    btnW, btnH
            );
            // 2: 왼쪽 아래
            bossOptionRects[2] = new Rectangle(
                    oreX - btnW - offsetX,
                    oreY + oreSize + offsetY,
                    btnW, btnH
            );
            // 3: 오른쪽 아래
            bossOptionRects[3] = new Rectangle(
                    oreX + oreSize + offsetX,
                    oreY + oreSize + offsetY,
                    btnW, btnH
            );

            // 광물 이미지
            bossOreImg = new ImageIcon("res/boss" + (currLevel + 1) + ".png").getImage();

            spawnEnemy();


            setFocusable(true);

            addKeyListener(new KeyAdapter() {
                @Override
                public void keyPressed(KeyEvent e) {
                    int code = e.getKeyCode();

                    // ───── 1. 보물방 처리 (Enter + 1~4) ─────
                    if (isTreasureRoom() && !treasureSolved) {
                        // Enter 로 퀴즈 시작/채점
                        if (code == KeyEvent.VK_ENTER) {
                            handleTreasureEnter();
                            return; // 여기서 처리 끝
                        }
                        // 퀴즈가 켜져 있을 때 숫자 키 처리
                        if (treasureQuizActive) {
                            handleTreasureKey(e); // 1~4, backspace
                            return;
                        }
                    }

                    // ───── 2. 보스방 처리 (키보드는 이동만) ─────
                    if (isBossRoom()) {
                        if (code == KeyEvent.VK_LEFT || code == KeyEvent.VK_RIGHT ||
                                code == KeyEvent.VK_UP   || code == KeyEvent.VK_DOWN) {
                            movePlayer(code);
                        }
                        return; // 보스방에서는 타이핑/멈추기 안 씀
                    }

                    // ───── 3. 일반 방: Enter로 멈추기 + 단어 입력 ─────
                    if (code == KeyEvent.VK_ENTER) {
                        handleEnterKey(); // 적 1초 멈추고, 단어 제출 로직
                        return;
                    }

                    if (code == KeyEvent.VK_CONTROL) {
                        performCtrlAttack();
                        return;
                    }

                    if(code == KeyEvent.VK_CAPS_LOCK){ // 발표용 코드
                        if(spawnedEnemies != null && !spawnedEnemies.isEmpty()){
                            for(int i = 0; i < spawnedEnemies.size(); i++){
                                spawnedEnemies.remove(i);
                            }
                        }
                    }

                    if (typingMode) {
                        char ch = e.getKeyChar();

                        if (code == KeyEvent.VK_BACK_SPACE) {
                            if (inputBuffer.length() > 0) {
                                inputBuffer.deleteCharAt(inputBuffer.length() - 1);
                            }
                            repaint();
                            return;
                        }

                        if (ch == ' ') { // 스페이스
                            inputBuffer.append(' ');
                            repaint();
                            return;
                        }

                        if (code == KeyEvent.VK_ESCAPE) {
                            typingMode = false;
                            inputBuffer.setLength(0);
                            repaint();
                            return;
                        }

                        if (Character.isLetterOrDigit(ch)) {
                            inputBuffer.append(Character.toLowerCase(ch));
                            repaint();
                        }
                    }
                    if (code == KeyEvent.VK_LEFT)  leftPressed = true;
                    if (code == KeyEvent.VK_RIGHT) rightPressed = true;
                    if (code == KeyEvent.VK_UP)    upPressed = true;
                    if (code == KeyEvent.VK_DOWN)  downPressed = true;


                }

                @Override
                public void keyReleased(KeyEvent e) {
                    int code = e.getKeyCode();
                    if (code == KeyEvent.VK_LEFT)  leftPressed = false;
                    if (code == KeyEvent.VK_RIGHT) rightPressed = false;
                    if (code == KeyEvent.VK_UP)    upPressed = false;
                    if (code == KeyEvent.VK_DOWN)  downPressed = false;
                }
            });

            // 보스방 마우스 클릭
            addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    handleMouseClick(e.getX(), e.getY());
                }
            });

            // 타이머
            enemyTimer = new Timer(40, e -> {
                updatePlayerMovement();
                updateEnemies();
                repaint();
            });
            enemyTimer.start();
        }

        private void updatePlayerMovement() {
            // 아무 방향키도 안 눌려 있으면 끝
            if (!leftPressed && !rightPressed && !upPressed && !downPressed) return;

            // 기존 movePlayer를 재활용해서 방향마다 한 번씩 이동
            if (leftPressed)  movePlayer(KeyEvent.VK_LEFT);
            if (rightPressed) movePlayer(KeyEvent.VK_RIGHT);
            if (upPressed)    movePlayer(KeyEvent.VK_UP);
            if (downPressed)  movePlayer(KeyEvent.VK_DOWN);
        }

        private boolean isTreasureRoom() {
            int[][] levelMap = gm.map.maparr[currLevel];
            return levelMap[currRow][currCol] == ROOM_TREASURE;
        }

        private boolean isBossRoom() {
            int[][] levelMap = gm.map.maparr[currLevel];
            return levelMap[currRow][currCol] == ROOM_BOSS;
        }

        private void prepareTreasureQuiz() {
            if (quizWords == null || quizWords.size() < 4) return;

            Random rand = rd;

            // 정답 하나 뽑기
            Word ans = quizWords.get(rand.nextInt(quizWords.size()));

            // 오답 3개
            List<Word> pool = new ArrayList<>(quizWords);
            pool.remove(ans);
            Collections.shuffle(pool);

            treasureOptions[0] = ans;
            treasureOptions[1] = pool.get(0);
            treasureOptions[2] = pool.get(1);
            treasureOptions[3] = pool.get(2);

            // 섞기
            List<Integer> idx = List.of(0, 1, 2, 3);
            List<Integer> shuffled = new ArrayList<>(idx);
            Collections.shuffle(shuffled);

            Word[] newOpts = new Word[4];
            int correctPos = -1;
            for (int i = 0; i < 4; i++) {
                newOpts[i] = treasureOptions[shuffled.get(i)];
                if (treasureOptions[shuffled.get(i)] == ans) {
                    correctPos = i;
                }
            }
            treasureOptions = newOpts;
            treasureCorrectIndex = correctPos;
            treasureQuestionEng = ans.getEng();
        }

        private void handleTreasureEnter() {
            // 이미 이 방 보물 끝났으면 아무 것도 안 함
            if (treasureSolved) return;

            // 아직 문제 안 띄웠으면 → 문제 생성 + 입력 모드 ON
            if (!treasureQuizActive) {
                prepareTreasureQuiz();
                treasureQuizActive = true;
                treasureInput.setLength(0);
                System.out.println("[보물방] 문제 시작: " + treasureQuestionEng);
                repaint();
                return;
            }

            // 문제 중인데 Enter → 입력된 번호 채점
            String s = treasureInput.toString().trim();
            if (s.isEmpty()) return;

            int choice;
            try {
                choice = Integer.parseInt(s) - 1; // 1~4 → 0~3
            } catch (NumberFormatException ex) {
                System.out.println("[보물방] 1~4 숫자만 입력하세요.");
                treasureInput.setLength(0);
                repaint();
                return;
            }

            if (choice < 0 || choice > 3) {
                System.out.println("[보물방] 1~4 숫자만 입력하세요.");
                treasureInput.setLength(0);
                repaint();
                return;
            }

            treasureQuizActive = false;
            treasureSolved = true;   // 한 번만 실행

            if (choice == treasureCorrectIndex) {
                player.dmg += 5;       // 데미지 +5
                if(player.armor == 30){
                    // 30이 상한선
                } else{
                    player.armor += 10;    // 방어력 +10
                }


                switch(player.armor){
                    case 0 -> playerImg = new ImageIcon("res/steve1.png").getImage();
                    case 10 -> playerImg = new ImageIcon("res/steve2.png").getImage();
                    case 20 -> playerImg = new ImageIcon("res/steve3.png").getImage();
                    case 30 -> playerImg = new ImageIcon("res/steve4.png").getImage();
                    default -> playerImg = new ImageIcon("res/steve1.png").getImage();
                }


            } else {
                System.out.println("[보물방] 오답... 보상 없음");
            }

            treasureInput.setLength(0);
            repaint();
        }

        private void performCtrlAttack() {
            // 플레이어 히트박스 중심 기준으로 150x150
            int phbX = player.x + (imgsize - player.sizex) / 2;
            int phbY = player.y + (imgsize - player.sizey) / 2;
            int centerX = phbX + player.sizex / 2;
            int centerY = phbY + player.sizey / 2;

            meleeAoeX = centerX - AOE_SIZE / 2;
            meleeAoeY = centerY - AOE_SIZE / 2;

            Rectangle aoeRect = new Rectangle(meleeAoeX, meleeAoeY, AOE_SIZE, AOE_SIZE);

            // 효과 0.1초 유지
            showMeleeEffect = true;
            meleeEffectUntil = System.currentTimeMillis() + 100;

            // 범위 내 적들에게 데미지 5 (armor 반영)
            for (int i = spawnedEnemies.size() - 1; i >= 0; i--) {
                Entity mob = spawnedEnemies.get(i);
                Rectangle mobRect = new Rectangle(mob.x, mob.y, mob.sizex, mob.sizey);

                if (!aoeRect.intersects(mobRect)) continue;

                int raw = 5;  // 근접 공격 기본 데미지
                int factor = 50 - mob.armor;
                if (factor < 0) factor = 0;
                int damage = raw * factor / 50;
                if (damage <= 0) damage = 1;

                mob.hp -= damage;
                System.out.println("[CTRL 근접] " + mob.name + " -" + damage + " (hp=" + mob.hp + ")");

                if (mob.hp <= 0) {
                    spawnedEnemies.remove(i);
                }
            }

            repaint();
        }


        private void handleTreasureKey(KeyEvent e) {
            int code = e.getKeyCode();

            if (code == KeyEvent.VK_BACK_SPACE) {
                if (treasureInput.length() > 0) {
                    treasureInput.deleteCharAt(treasureInput.length() - 1);
                }
                repaint();
                return;
            }

            char ch = e.getKeyChar();
            if (ch >= '1' && ch <= '4') {
                // 한 자리만 받게 하고 싶으면 이전 내용 지우고 새로 넣기
                treasureInput.setLength(0);
                treasureInput.append(ch);
                repaint();
            }
        }

        private boolean updateBomberEnemy(Entity mob, Rectangle playerHb, long now) {

            // 폭발 반경: 히트박스 x2
            int exW = mob.sizex * 2;
            int exH = mob.sizey * 2;
            int exX = mob.x + mob.sizex / 2 - exW / 2;
            int exY = mob.y + mob.sizey / 2 - exH / 2;
            Rectangle explosionArea = new Rectangle(exX, exY, exW, exH);

            // 이미 폭발 준비 상태인 경우
            if (mob.bombPrimed) {
                long elapsed = now - mob.bombStartTime;

                // 1초 기다리는 중 → 그대로 멈춰있음
                if (elapsed < 1000) {
                    return false; // 아직 안 터짐
                }

                // 1초 후 → 폭발
                System.out.println("[자폭몹] " + mob.name + " 폭발!");

                if (explosionArea.intersects(playerHb)) {
                    int raw = mob.dmg;
                    int factor = 50 - player.armor;
                    if (factor < 0) factor = 0;
                    int damage = raw * factor / 50;
                    if (damage <= 0) damage = 1;

                    applyDamageToPlayer(damage, now);
                    System.out.println("[자폭 피해] -" + damage + " → HP=" + player.hp);
                }

                // 폭발 후 몹은 사라짐
                return true;
            }

            // 아직 폭발 준비가 아닌 상태 → 플레이어 추적
            // (기존 style 3 이동과 동일하게 추적)
            int mobCenterX = mob.x + mob.sizex / 2;
            int mobCenterY = mob.y + mob.sizey / 2;
            int playerCenterX = playerHb.x + playerHb.width  / 2;
            int playerCenterY = playerHb.y + playerHb.height / 2;

            // 먼저 플레이어가 폭발 범위(히트박스 x2) 안에 있는지 체크
            if (explosionArea.intersects(playerHb)) {
                // 폭발 준비 시작
                mob.bombPrimed = true;
                mob.bombStartTime = now;
                System.out.println("[자폭몹] " + mob.name + " 폭발 준비 시작!");
                // 이 순간부터는 움직이지 않음
                return false;
            }

            // 아직 범위 밖이면 계속 추적 이동
            int vx = playerCenterX - mobCenterX;
            int vy = playerCenterY - mobCenterY;
            double len = Math.sqrt(vx * vx + vy * vy);
            int dx = 0, dy = 0;
            if (len != 0) {
                dx = (int) Math.round(vx / len * ENEMY_SPEED);
                dy = (int) Math.round(vy / len * ENEMY_SPEED);
            }

            int newX = mob.x + dx;
            int newY = mob.y + dy;

            // 방 안에만 있도록 클램프
            if (newX < roomX) newX = roomX;
            if (newX + mob.sizex > roomX + roomW) newX = roomX + roomW - mob.sizex;
            if (newY < roomY) newY = roomY;
            if (newY + mob.sizey > roomY + roomH) newY = roomY + roomH - mob.sizey;

            // 다른 몹과 겹치면 이동 취소
            Rectangle newRect = new Rectangle(newX, newY, mob.sizex, mob.sizey);
            for (Entity other : spawnedEnemies) {
                if (other == mob) continue;
                Rectangle otherRect = new Rectangle(other.x, other.y, other.sizex, other.sizey);
                if (newRect.intersects(otherRect)) {
                    return false;
                }
            }

            mob.x = newX;
            mob.y = newY;
            return false;
        }


        private void prepareBossQuestion() {
            if (quizWords == null || quizWords.size() < 4) return;

            Random rand = rd;

            // 정답
            Word ans = quizWords.get(rand.nextInt(quizWords.size()));

            // 오답 3개
            List<Word> pool = new ArrayList<>(quizWords);
            pool.remove(ans);
            Collections.shuffle(pool);

            Word[] opts = new Word[4];
            opts[0] = ans;
            opts[1] = pool.get(0);
            opts[2] = pool.get(1);
            opts[3] = pool.get(2);

            // 섞기
            List<Integer> idx = List.of(0, 1, 2, 3);
            List<Integer> shuffled = new ArrayList<>(idx);
            Collections.shuffle(shuffled);

            Word[] newOpts = new Word[4];
            int correctPos = -1;
            for (int i = 0; i < 4; i++) {
                newOpts[i] = opts[shuffled.get(i)];
                if (opts[shuffled.get(i)] == ans) correctPos = i;
            }
            bossOptions = newOpts;
            bossCorrectIndex = correctPos;
            bossQuestionKor = ans.getKor();
        }

        private void handleMouseClick(int x, int y) {
            if (!isBossRoom()) return;

            long now = System.currentTimeMillis();

            // 아직 시작 안 했고, 버튼 클릭하면 시작
            if (!bossQuizStarted && !bossSolved && bossStartButton.contains(x, y)) {
                bossQuizStarted = true;
                bossCorrectStreak = 0;
                bossTimeAttackEnd = now + 30_000; // 30초 타임어택
                prepareBossQuestion();
                System.out.println("[보스방] 타임어택 시작!");
                repaint();
                return;
            }

            // 이미 끝났으면 무시
            if (!bossQuizStarted) return;

            // 시간 초과 체크
            if (now > bossTimeAttackEnd) {
                bossQuizStarted = false;
                bossTimeAttackEnd = 0;
                bossCorrectStreak = 0;
                System.out.println("[보스방] 시간 초과! 실패");

                //죽음
                repaint();
                return;
            }

            // 보기 버튼 클릭 체크
            for (int i = 0; i < 4; i++) {
                if (bossOptionRects[i].contains(x, y)) {
                    // 정답?
                    if (i == bossCorrectIndex) {
                        bossCorrectStreak++;
                        System.out.println("[보스방] 정답! (" + bossCorrectStreak + "/4)");

                        if (bossCorrectStreak >= 4) {
                            bossSolved = true;
                            bossQuizStarted = false;
                            bossTimeAttackEnd = 0;
                            System.out.println("[보스방] 4연속 정답! 클리어!");

                            goToNextLevelOrEnd();
                        } else {
                            // 다음 문제
                            prepareBossQuestion();
                        }
                    } else {
                        // 오답 → 실패
                        bossQuizStarted = false;
                        bossTimeAttackEnd = 0;
                        bossCorrectStreak = 0;
                        System.out.println("[보스방] 오답! 실패");
                        //죽음
                    }
                    repaint();
                    return;
                }
            }
        }


        private void goToNextLevelOrEnd() {
            // 현재 보스방 클리어 처리 (문 열리도록)
            gm.map.clearmap[currLevel][currRow][currCol] = 1;

            int totalLevels = gm.map.maparr.length;

            // 마지막 레벨이면 클리어
            if (currLevel >= totalLevels - 1) {
                System.out.println("모든 레벨을 클리어했습니다!");
                return;
            }

            // 다음 레벨로 이동
            currLevel++;

            // 다음 레벨의 2번 방(ROOM_SPAWN)을 찾기
            int[][] nextLevel = gm.map.maparr[currLevel];
            for (int r = 0; r < nextLevel.length; r++) {
                for (int c = 0; c < nextLevel[r].length; c++) {
                    if (nextLevel[r][c] == ROOM_SPAWN) { // ROOM_SPAWN == 2
                        currRow = r;
                        currCol = c;
                        break;
                    }
                }
            }

            // 새 레벨 상태 초기화
            spawnedEnemies.clear();
            treasureSolved = false;
            treasureQuizActive = false;
            bossSolved = false;
            bossQuizStarted = false;
            bossTimeAttackEnd = 0;
            bossCorrectStreak = 0;

            // 플레이어를 새 방 중앙에 배치
            player.x = roomX + roomW / 2 - humanhitboxx / 2;
            player.y = roomY + roomH / 2 - humanhitboxy / 2;

            // 새 레벨 보스 이미지로 갱신
            bossOreImg = new ImageIcon("res/boss" + (currLevel + 1) + ".png").getImage();

            // 필요 시 적 스폰 (스폰방이면 spawnEnemy가 알아서 안 뽑음)
            spawnEnemy();

            System.out.println("[레벨 이동] 현재 레벨 : " + currLevel +
                    " (" + currRow + "," + currCol + ")");
            repaint();
        }






        private void handleEnterKey() {
            long now = System.currentTimeMillis();

            // 아직 입력 모드가 아니면 => 입력 시작 + 1초 멈추기 시도
            if (!typingMode) {
                typingMode = true;
                inputBuffer.setLength(0); // 새로 입력

                if (lastAnswerCorrect || now - lastFreezeTryTime >= 2000) {
                    enemyFrozenUntil = now + 1000;  // 1초 정지
                    lastFreezeTryTime = now;
                }
                repaint();
                return;
            }

            // === 이미 입력 모드일 때 Enter => 제출 ===
            String answer = inputBuffer.toString().trim();

            // 1) 비어 있는 입력이면 그냥 입력 모드 종료만
            if (answer.isEmpty()) {
                typingMode = false;
                inputBuffer.setLength(0);
                repaint();
                return;
            }

            boolean hitSomething = false;

            // 2) 정답 체크
            for (int i = spawnedEnemies.size() - 1; i >= 0; i--) {
                Entity mob = spawnedEnemies.get(i);

                if (mob.quizEng != null &&
                        mob.quizEng.equalsIgnoreCase(answer)) {

                    int raw = player.dmg;
                    int factor = 50 - mob.armor;
                    if (factor < 0) factor = 0;
                    int damage = raw * factor / 50;
                    if (damage <= 0) damage = 1;

                    mob.hp -= damage;
                    System.out.println("[정답 히트] '" + mob.quizEng + "' → " +
                            mob.name + " -" + damage + " (hp=" + mob.hp + ")");

                    if (mob.hp <= 0) {
                        spawnedEnemies.remove(i);
                    }
                    hitSomething = true;
                }
            }

            // 3) 정답 여부 저장 (다음 Enter에서 freeze 가능 여부에 사용)
            lastAnswerCorrect = hitSomething;

            // 4) 입력 모드 종료 + 버퍼 비우기
            typingMode = false;
            inputBuffer.setLength(0);
            repaint();
        }





        private Color doorColor(int roomType) {
            if (roomType == ROOM_TREASURE) return Color.YELLOW;
            if (roomType == ROOM_SHOP)     return Color.GREEN;
            if (roomType == ROOM_BOSS)     return Color.RED;
            return Color.LIGHT_GRAY;       // 나머지(1,2 등)
        }

        private void movePlayer(int key) {
            int newX = player.x;
            int newY = player.y;

            if (key == KeyEvent.VK_LEFT)  newX -= speed;
            if (key == KeyEvent.VK_RIGHT) newX += speed;
            if (key == KeyEvent.VK_UP)    newY -= speed;
            if (key == KeyEvent.VK_DOWN)  newY += speed;

            // 히트박스 중심 기준으로 벽 충돌
            if ( newX + (imgsize - player.sizex) / 2 < roomX ) {
                newX = roomX - (imgsize - player.sizex) / 2;
            }
            if ( newX + (imgsize - player.sizex) / 2 + player.sizex > roomX + roomW ) {
                newX = roomX + roomW - player.sizex - (imgsize - player.sizex) / 2;
            }
            if ( newY + (imgsize - player.sizey) / 2 < roomY ) {
                newY = roomY - (imgsize - player.sizey) / 2;
            }
            if ( newY + (imgsize - player.sizey) / 2 + player.sizey > roomY + roomH ) {
                newY = roomY + roomH - player.sizey - (imgsize - player.sizey) / 2;
            }

            player.x = newX;
            player.y = newY;

            checkDoorCollision();
            repaint();
        }

        private void updateEnemies() {
            // 보스방은 몹 안 쓰면 여기서 그냥 타임어택 시간만 체크
            if (isBossRoom()) {
                if (bossQuizStarted) {
                    long now = System.currentTimeMillis();
                    if (now > bossTimeAttackEnd) {
                        bossQuizStarted = false;
                        bossTimeAttackEnd = 0;
                        bossCorrectStreak = 0;
                        System.out.println("[보스방] 시간 초과! 실패");
                    }
                }
                return;
            }

            if (spawnedEnemies.isEmpty()) return;

            long now = System.currentTimeMillis();

            //적 멈춤 처리
            if (now < enemyFrozenUntil) {
                // 1초 멈추는 중에는 그냥 그 프레임은 아무 것도 안 함
                return;
            }

            //평소처럼 이동/공격

            // 플레이어 히트박스
            int phbX = player.x + (imgsize - player.sizex) / 2;
            int phbY = player.y + (imgsize - player.sizey) / 2;
            Rectangle playerHb = new Rectangle(phbX, phbY, player.sizex, player.sizey);

            for (int i = spawnedEnemies.size() - 1; i >= 0; i--) { // 자폭몹 제거 편하게 뒤에서부터 for문 실행
                Entity mob = spawnedEnemies.get(i);

                if (mob.attackstyle == 3) {
                    // 자폭몹 전용 로직
                    boolean exploded = updateBomberEnemy(mob, playerHb, now);
                    if (exploded) {
                        spawnedEnemies.remove(i);  // 터졌으면 몹 제거
                    }
                } else if (mob.attackstyle == 1) {
                    // 일반 근접몹
                    moveMeleeEnemy(mob, i);
                    handleContactDamage(mob, playerHb);
                } else if (mob.attackstyle == 2) {
                    // 레이저몹
                    updateLaserEnemy(mob, playerHb, now);
                }
            }
        }

        private boolean applyDamageToPlayer(int damage, long now) {

            // 1초 무적 중이면 데미지 무시
            if (now - player.lastHitTime < player.invincibleMs) {
                return false; // 데미지 X
            }

            // 정상적으로 데미지 적용
            player.hp -= damage;
            player.lastHitTime = now;

            System.out.println("[피해] -" + damage + " → HP=" + player.hp);
            return true;
        }


        private void updateLaserEnemy(Entity mob, Rectangle playerHb, long now) {

            //발사 후 0.5초 쿨타임 체크
            if (mob.lastShotTime != 0) {
                if (now - mob.lastShotTime < 500) {
                    return; // 아직 0.5초 안 지남 → 공격 불가(대기)
                }
                // 0.5초 지났으면 다시 공격 가능하도록 초기화
                mob.lastShotTime = 0;
            }

            //공격 시작
            if (!mob.attacking) {
                mob.attacking = true;
                mob.attackStartTime = now;

                // ★ 이 순간의 플레이어 위치를 "고정 타겟"으로 저장
                int px = playerHb.x + playerHb.width  / 2;
                int py = playerHb.y + playerHb.height / 2;
                mob.laserTargetX = px;
                mob.laserTargetY = py;

                return;
            }

            //조준 중(1초 동안)
            long elapsed = now - mob.attackStartTime;
            if (elapsed < 1000) {
                return;
            }

            //1초 경과 => 실제 레이저 판정
            int mx = mob.x + mob.sizex / 2;
            int my = mob.y + mob.sizey / 2;

            int px = mob.laserTargetX;
            int py = mob.laserTargetY;

            Line2D.Double line = new Line2D.Double(mx, my, px, py);

            if (playerHb.intersectsLine(line)) {
                int raw = mob.dmg;
                int factor = 50 - player.armor;
                if (factor < 0) factor = 0;
                int damage = raw * factor / 50;
                if (damage <= 0) damage = 1;

                applyDamageToPlayer(damage, now);
                System.out.println("[레이저 히트] " + mob.name + " -" + damage + " / HP=" + player.hp);
            }

            //공격 종료 => 0.5초 쿨타임 시작
            mob.attacking = false;
            mob.attackStartTime = 0;
            mob.lastShotTime = now; // ← 바로 다시 못 쏘게 0.5초 쿨 적용
        }

        private void handleContactDamage(Entity mob, Rectangle playerHb) {
            // 몹 히트박스
            Rectangle mobHb = new Rectangle(mob.x, mob.y, mob.sizex, mob.sizey);

            if (!mobHb.intersects(playerHb)) return;

            long now = System.currentTimeMillis();

            // 데미지 = mob.dmg * (50 - player.armor) / 50
            int raw = mob.dmg;
            int factor = 50 - player.armor;
            if (factor < 0) factor = 0;

            int damage = raw * factor / 50;
            if (damage <= 0) damage = 1;


            applyDamageToPlayer(damage, now);

            // 너무 빨리 갈리면 나중에 "피격 쿨타임" 변수 하나 더 두면 됨 (ex: lastHitTime)
        }

        private void moveMeleeEnemy(Entity mob, int index) {
            // 몹 중심 좌표
            int mobCenterX = mob.x + mob.sizex / 2;
            int mobCenterY = mob.y + mob.sizey / 2;

            // 플레이어 중심 좌표
            int phbX = player.x + (imgsize - player.sizex) / 2;
            int phbY = player.y + (imgsize - player.sizey) / 2;
            int playerCenterX = phbX + player.sizex / 2;
            int playerCenterY = phbY + player.sizey / 2;

            int dx = 0;
            int dy = 0;

            if (mob.attackstyle == 3) {
                // 항상 추적 (style 3)
                int vx = playerCenterX - mobCenterX;
                int vy = playerCenterY - mobCenterY;
                double len = Math.sqrt(vx * vx + vy * vy);
                if (len != 0) {
                    dx = (int) Math.round(vx / len * ENEMY_SPEED);
                    dy = (int) Math.round(vy / len * ENEMY_SPEED);
                }
            } else {
                // style 1 : 추적 또는 랜덤
                if (rd.nextBoolean()) {
                    // 50% 확률로 추적
                    int vx = playerCenterX - mobCenterX;
                    int vy = playerCenterY - mobCenterY;
                    double len = Math.sqrt(vx * vx + vy * vy);
                    if (len != 0) {
                        dx = (int) Math.round(vx / len * ENEMY_SPEED);
                        dy = (int) Math.round(vy / len * ENEMY_SPEED);
                    }
                } else {
                    // 50%는 랜덤 방향 이동
                    int dir = rd.nextInt(4);
                    switch (dir) {
                        case 0 -> dx = ENEMY_SPEED;      // 오른쪽
                        case 1 -> dx = -ENEMY_SPEED;     // 왼쪽
                        case 2 -> dy = ENEMY_SPEED;      // 아래
                        case 3 -> dy = -ENEMY_SPEED;     // 위
                    }
                }
            }

            if (dx == 0 && dy == 0) return; // 이번 프레임은 안 움직임

            int newX = mob.x + dx;
            int newY = mob.y + dy;

            // 방 경계 안에만 있도록 클램프
            if (newX < roomX) newX = roomX;
            if (newX + mob.sizex > roomX + roomW) newX = roomX + roomW - mob.sizex;
            if (newY < roomY) newY = roomY;
            if (newY + mob.sizey > roomY + roomH) newY = roomY + roomH - mob.sizey;

            // 다른 몹과 겹치면 이동 취소
            Rectangle newRect = new Rectangle(newX, newY, mob.sizex, mob.sizey);
            for (int i = 0; i < spawnedEnemies.size(); i++) {
                if (i == index) continue;
                Entity other = spawnedEnemies.get(i);
                Rectangle otherRect = new Rectangle(other.x, other.y, other.sizex, other.sizey);
                if (newRect.intersects(otherRect)) {
                    return; // 겹치므로 이동하지 않음
                }
            }

            // 최종 위치 확정
            mob.x = newX;
            mob.y = newY;
        }







        private void spawnEnemy() {
            if(gm.map.maparr[currLevel][currRow][currCol] != 1) return; // 적 스폰 방이 아니라면 스폰x


            if(gm.map.clearmap[currLevel][currRow][currCol] == 1) return; // 이미 깼으면 x


            // 100x100 으로 생각했을 때, 7x4 구역이 생김 => 여기 적 스폰 예정
            final int spawnareaX = 7;
            final int spawnareaY = 4;
            List<Integer[]> spawnAreaSet = new ArrayList<>();

            List<Integer[]> bannedAreaSet = new ArrayList<>(); // 문 근처엔 스폰 안되도록
            bannedAreaSet.add(new Integer[] {1, 0});
            bannedAreaSet.add(new Integer[] {2, 0});
            bannedAreaSet.add(new Integer[] {1, 6});
            bannedAreaSet.add(new Integer[] {2, 6});
            bannedAreaSet.add(new Integer[] {0, 3});
            bannedAreaSet.add(new Integer[] {0, 4});
            bannedAreaSet.add(new Integer[] {0, 5});
            bannedAreaSet.add(new Integer[] {3, 3});
            bannedAreaSet.add(new Integer[] {3, 4});
            bannedAreaSet.add(new Integer[] {3, 5});


            int enemyAmount = rd.nextInt(gm.map.maparr[currLevel][currRow].length + 1) + 1;
            while(spawnAreaSet.size() < enemyAmount){   // 방의 행 개수를 가지고, 그 숫자의 랜덤(4면 1~4) 수만큼 적 소환
                final int areax = rd.nextInt(spawnareaX);
                final int areay = rd.nextInt(spawnareaY);
                Integer[] area = {areax, areay};

                int count = 0;
                if(spawnAreaSet.size() != 0) {
                    for (Integer[] sets : spawnAreaSet) {
                        if (sets[0] == areax && sets[1] == areay){
                            count++;
                        }
                    }
                }
                for (Integer[] sets : bannedAreaSet) {
                    if (sets[0] == areax && sets[1] == areay){
                        count++;
                    }
                }
                if(count != 0) continue; // 이미 위치에 x

                spawnAreaSet.add(area);

            }



            final int enemyhitboxX = 60;
            final int enemyhitboxY = 60;

            final int cellsize = 100; // 각 칸 크기는 100x100

            for(Integer[] spawnarea : spawnAreaSet){
                int msx = spawnarea[0];
                int msy = spawnarea[1];

                int centerX = roomX + msx * cellsize + cellsize/2; // x위치
                int centerY = roomY + msy * cellsize + cellsize/2; // y위치


                int[] spawnableMobSet = {0, 0};
                switch(currLevel){ // 레벨에 따른 스폰 가능한 몹
                    case 0 -> spawnableMobSet = new int[]{0, 2};
                    case 1 -> spawnableMobSet = new int[]{2, 6};
                    case 2 -> spawnableMobSet = new int[]{6, 9};
                    case 3 -> spawnableMobSet = new int[]{9, 11};
                    default -> spawnableMobSet = new int[]{0, 2};
                }

                int mobIndex = rd.nextInt(spawnableMobSet[0], spawnableMobSet[1]);
                Entity base = enemies.get(mobIndex);
                Entity mob = new Entity(base);

                mob.x = centerX;
                mob.y = centerY;
                mob.sizex = enemyhitboxX;
                mob.sizey = enemyhitboxY;

                if(quizWords != null && !quizWords.isEmpty()){
                    Word w = quizWords.get(rd.nextInt(quizWords.size()));
                    mob.quizEng = w.getEng();
                    mob.quizKor = w.getKor();
                }

                spawnedEnemies.add(mob);

            }



        }




        private void checkDoorCollision() {

            if (gm.map.clearmap[currLevel][currRow][currCol] == 0) {

                // 몹이 남아있으면 문 막기
                if (!spawnedEnemies.isEmpty()) {
                    return;
                }

                // 몹이 하나도 없으면 이제 클리어 처리
                gm.map.clearmap[currLevel][currRow][currCol] = 1;
            }





            // 히트박스 실제 좌표
            int hbX = player.x + (imgsize - player.sizex) / 2;
            int hbY = player.y + (imgsize - player.sizey) / 2;
            Rectangle hb = new Rectangle(hbX, hbY, player.sizex, player.sizey);

            int[][] levelMap = gm.map.maparr[currLevel];

            int sideDoorW = 22;
            int sideDoorH = 110;
            int sideDoorY = roomY + roomH / 2 - sideDoorH / 2;

            int vertDoorW = 80;
            int vertDoorH = 22;
            int topDoorX = roomX + roomW / 2 - vertDoorW / 2;
            int topDoorY = roomY;
            int bottomDoorX = topDoorX;
            int bottomDoorY = roomY + roomH - vertDoorH;

            // 문에서 얼마나 안쪽으로 밀어 넣을지
            int margin = 15;

            // ===== 왼쪽 문 =====
            if (currCol - 1 >= 0 && levelMap[currRow][currCol - 1] != 0) {
                Rectangle leftDoorZone = new Rectangle(roomX, sideDoorY, 2, sideDoorH);
                if (hb.intersects(leftDoorZone)) {
                    currCol--; // 왼쪽 방으로 이동

                    // 새 방에서 오른쪽 벽 기준으로 margin만큼 안쪽에 히트박스가 오도록 배치
                    // 히트박스 오른쪽 = roomX + roomW - margin
                    // => player.x = roomX + roomW - margin - (imgsize + sizex) / 2
                    player.x = roomX + roomW - margin - (imgsize + player.sizex) / 2;
                    // 세로는 방 중앙
                    player.y = roomY + roomH / 2 - imgsize / 2;

                    spawnedEnemies.clear(); // 새 방이므로 몹 체크
                    spawnEnemy();


                    System.out.println("← L=" + currLevel + " R=" + currRow + " C=" + currCol);
                    return;
                }
            }

            // ===== 오른쪽 문 =====
            if (currCol + 1 < levelMap[currRow].length &&
                    levelMap[currRow][currCol + 1] != 0) {
                Rectangle rightDoorZone = new Rectangle(roomX + roomW - 2, sideDoorY, 2, sideDoorH);
                if (hb.intersects(rightDoorZone)) {
                    currCol++; // 오른쪽 방으로 이동

                    // 새 방에서 왼쪽 벽 기준으로 margin만큼 안쪽
                    // 히트박스 왼쪽 = roomX + margin
                    // => player.x = roomX + margin - (imgsize - sizex) / 2
                    player.x = roomX + margin - (imgsize - player.sizex) / 2;
                    player.y = roomY + roomH / 2 - imgsize / 2;

                    spawnedEnemies.clear(); // 새 방이므로 몹 체크
                    spawnEnemy();

                    System.out.println("→ L=" + currLevel + " R=" + currRow + " C=" + currCol);
                    return;
                }
            }

            // ===== 위쪽 문 =====
            if (currRow - 1 >= 0 && levelMap[currRow - 1][currCol] != 0) {
                // 방 안쪽으로 살짝 들어온 얇은 판정 라인
                Rectangle upDoorZone = new Rectangle(topDoorX, topDoorY + 2, vertDoorW, 2);
                if (hb.intersects(upDoorZone)) {
                    currRow--; // 위 방으로 이동

                    // 새 방에서 아래쪽 벽 기준으로 margin만큼 위쪽에 히트박스가 오도록
                    // 히트박스 아래 = roomY + roomH - margin
                    // => player.y = roomY + roomH - margin - (imgsize + sizey) / 2
                    player.x = roomX + roomW / 2 - imgsize / 2;
                    player.y = roomY + roomH - margin - (imgsize + player.sizey) / 2;

                    spawnedEnemies.clear(); // 새 방이므로 몹 체크
                    spawnEnemy();

                    System.out.println("↑ L=" + currLevel + " R=" + currRow + " C=" + currCol);
                    return;
                }
            }

            // ===== 아래쪽 문 =====
            if (currRow + 1 < levelMap.length &&
                    levelMap[currRow + 1][currCol] != 0) {
                Rectangle downDoorZone = new Rectangle(bottomDoorX, bottomDoorY - 2, vertDoorW, 2);
                if (hb.intersects(downDoorZone)) {
                    currRow++; // 아래 방으로 이동

                    // 새 방에서 위쪽 벽 기준으로 margin만큼 아래쪽에 히트박스가 오도록
                    // 히트박스 위 = roomY + margin
                    // => player.y = roomY + margin - (imgsize - sizey) / 2
                    player.x = roomX + roomW / 2 - imgsize / 2;
                    player.y = roomY + margin - (imgsize - player.sizey) / 2;

                    spawnedEnemies.clear(); // 새 방이므로 몹 체크
                    spawnEnemy();

                    System.out.println("↓ L=" + currLevel + " R=" + currRow + " C=" + currCol);
                }
            }
        }

        private void drawTreasureRoom(Graphics2D g2) {
            // 1) 상자 4개 그리기 (생성자에서 만든 treasureChestRects 사용)
            for (int i = 0; i < 4; i++) {
                Rectangle r = treasureChestRects[i];

                // chest.png가 있으면 이미지로, 없으면 노란 박스로
                if (chestImg != null) {
                    g2.drawImage(chestImg, r.x, r.y, r.width, r.height, null);
                } else {
                    g2.setColor(Color.YELLOW);
                    g2.fillRect(r.x, r.y, r.width, r.height);
                }

                // 퀴즈가 활성화된 상태일 때만 상자 "밑에" 보기 텍스트 표시
                if (treasureQuizActive && treasureOptions[i] != null) {
                    g2.setColor(Color.WHITE);
                    String text = (i + 1) + ") " + treasureOptions[i].getKor();
                    int textX = r.x;
                    int textY = r.y + r.height + 15; // 상자 아래 15px
                    g2.drawString(text, textX, textY);
                }
            }

            // 2) 퀴즈가 켜졌을 때 문제(영어 단어) 상자 위쪽 중앙에 표시
            if (treasureQuizActive && treasureQuestionEng != null) {
                g2.setColor(Color.WHITE);
                String q = "영어 단어: " + treasureQuestionEng;

                FontMetrics fm = g2.getFontMetrics();
                int qWidth = fm.stringWidth(q);
                int centerX = roomX + roomW / 2;
                int qX = centerX - qWidth / 2;
                int qY = treasureChestRects[0].y - 20; // 맨 윗 상자 위쪽에

                g2.drawString(q, qX, qY);
            }

            // 3) 아직 퀴즈 시작 전이면 안내 문구만 (한 방 한 번만)
            if (!treasureQuizActive && !treasureSolved) {
                g2.setColor(Color.LIGHT_GRAY);
                g2.drawString("Enter를 눌러 보물 퀴즈 시작", roomX + 20, roomY + 40);
            }

            // 4) 이미 풀었으면 상태 표시(원하면 나중에 연출 추가)
            if (treasureSolved) {
                g2.setColor(Color.CYAN);
                g2.drawString("보물을 획득했습니다!", roomX + 20, roomY + 100);
                g2.drawString("데미지 +5     갑옷 + 10(갑옷은 30이 상한선)", roomX + 20, roomY + 120);



            }
        }


        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;

            // 0) 레벨 배경 먼저 그리기
            if (currLevel >= 0 && currLevel < levelBg.length && levelBg[currLevel] != null) {
                g2.drawImage(levelBg[currLevel], 0, 0, SCREEN_W, SCREEN_H, null);
            } else {
                g2.setColor(Color.BLACK);
                g2.fillRect(0, 0, SCREEN_W, SCREEN_H);
            }

            // ───── ROOM 내부를 항상 검정으로 채움 (배경 위를 덮음) ─────
            g2.setColor(Color.BLACK);
            g2.fillRect(roomX, roomY, roomW, roomH);

            // 1) 방 테두리
            g2.setColor(Color.DARK_GRAY);
            g2.drawRect(roomX, roomY, roomW, roomH);

            int[][] levelMap = gm.map.maparr[currLevel];

            // ----- 좌/우 문 -----
            int sideDoorW = 22;
            int sideDoorH = 110;
            int sideDoorY = roomY + roomH / 2 - sideDoorH / 2;

            if (currCol - 1 >= 0 && levelMap[currRow][currCol - 1] != ROOM_EMPTY) {
                int t = levelMap[currRow][currCol - 1];
                g2.setColor(doorColor(t));
                g2.fillRect(roomX - sideDoorW, sideDoorY, sideDoorW, sideDoorH);
            }

            if (currCol + 1 < levelMap[currRow].length &&
                    levelMap[currRow][currCol + 1] != ROOM_EMPTY) {
                int t = levelMap[currRow][currCol + 1];
                g2.setColor(doorColor(t));
                g2.fillRect(roomX + roomW, sideDoorY, sideDoorW, sideDoorH);
            }

            // ----- 위/아래 문 -----
            int vertDoorW = 80;
            int vertDoorH = 22;
            int topDoorX = roomX + roomW / 2 - vertDoorW / 2;
            int topDoorY = roomY;
            int bottomDoorX = topDoorX;
            int bottomDoorY = roomY + roomH - vertDoorH;

            if (currRow - 1 >= 0 && levelMap[currRow - 1][currCol] != ROOM_EMPTY) {
                int t = levelMap[currRow - 1][currCol];
                g2.setColor(doorColor(t));
                g2.fillRect(topDoorX, topDoorY - vertDoorH, vertDoorW, vertDoorH);
            }

            if (currRow + 1 < levelMap.length &&
                    levelMap[currRow + 1][currCol] != ROOM_EMPTY) {
                int t = levelMap[currRow + 1][currCol];
                g2.setColor(doorColor(t));
                g2.fillRect(bottomDoorX, bottomDoorY + vertDoorH, vertDoorW, vertDoorH);
            }

            // 2) 플레이어
            g2.drawImage(playerImg, player.x, player.y, imgsize, imgsize, null);


            //체력
            g2.setColor(Color.RED);
            String hpText = "HP : " + player.hp;
            FontMetrics fm = g2.getFontMetrics();
            int hpX = player.x + imgsize / 2 - fm.stringWidth(hpText) / 2;
            int hpY = player.y - 10; // 머리 위쪽에 살짝
            g2.drawString(hpText, hpX, hpY);


            // 3) 몬스터 + 머리 위 단어
            for (Entity m : spawnedEnemies) {
                Image img = new ImageIcon("res/" + m.name + ".png").getImage();
                g2.drawImage(img, m.x, m.y, m.sizex, m.sizey, null);

                if (m.quizKor != null) {
                    g2.setColor(Color.WHITE);
                    g2.drawString(m.quizKor, m.x, m.y - 5);
                }
            }

            // 4) 레이저
            int phbX = player.x + (imgsize - player.sizex) / 2;
            int phbY = player.y + (imgsize - player.sizey) / 2;
            int playerCenterX = phbX + player.sizex / 2;
            int playerCenterY = phbY + player.sizey / 2;

            for (Entity m : spawnedEnemies) {
                if (m.attackstyle != 2 || !m.attacking) continue;

                int mx = m.x + m.sizex / 2;
                int my = m.y + m.sizey / 2;

                // ★ 타겟 좌표: 공격 시작 시점에 저장된 값 사용
                int tx, ty;
                if (m.laserTargetX != 0 || m.laserTargetY != 0) {
                    tx = m.laserTargetX;
                    ty = m.laserTargetY;
                } else {
                    // 혹시 초기화 안된 경우 안전하게 플레이어 현재 위치
                    tx = playerCenterX;
                    ty = playerCenterY;
                }

                Stroke oldStroke = g2.getStroke();
                Color  oldColor  = g2.getColor();

                g2.setColor(Color.RED);
                g2.setStroke(new BasicStroke(4f));  // 굵은 선

                g2.drawLine(mx, my, tx, ty);

                g2.setStroke(oldStroke);
                g2.setColor(oldColor);
            }

            // 5) 보물방 UI (상자 + 상자 밑 텍스트)
            if (isTreasureRoom()) {
                drawTreasureRoom(g2);   // 이건 네가 이미 만든 메서드 그대로 사용
            }

            // 6) 보스방 UI
            if (isBossRoom()) {
                // 광물
                if (bossOreImg != null) {
                    g2.drawImage(bossOreImg, bossOreRect.x, bossOreRect.y,
                            bossOreRect.width, bossOreRect.height, null);
                } else {
                    g2.setColor(Color.GRAY);
                    g2.fill(bossOreRect);
                }

                // 시작 버튼
                g2.setColor(Color.LIGHT_GRAY);
                g2.fill(bossStartButton);
                g2.setColor(Color.BLACK);
                g2.drawRect(bossStartButton.x, bossStartButton.y,
                        bossStartButton.width, bossStartButton.height);
                g2.drawString("START",
                        bossStartButton.x + 20,
                        bossStartButton.y + 20);

                if (bossQuizStarted) {
                    // 보기 버튼
                    for (int i = 0; i < 4; i++) {
                        Rectangle r = bossOptionRects[i];
                        g2.setColor(Color.WHITE);
                        g2.fill(r);
                        g2.setColor(Color.BLACK);
                        g2.drawRect(r.x, r.y, r.width, r.height);

                        if (bossOptions[i] != null) {
                            g2.drawString((i + 1) + ") " + bossOptions[i].getEng(),
                                    r.x + 5, r.y + 25);
                        }
                    }

                    // 문제(한글 뜻)
                    g2.setColor(Color.YELLOW);
                    if (bossQuestionKor != null) {
                        g2.drawString("뜻: " + bossQuestionKor,
                                roomX + 20, roomY + 30);
                    }

                    // 남은 시간
                    long now = System.currentTimeMillis();
                    long remainMs = Math.max(0, bossTimeAttackEnd - now);
                    long remainSec = remainMs / 1000;
                    g2.setColor(Color.RED);
                    g2.drawString("남은 시간: " + remainSec + "초",
                            roomX + roomW - 150, roomY + 30);

                    // 연속 정답 수
                    g2.setColor(Color.WHITE);
                    g2.drawString("연속 정답: " + bossCorrectStreak + "/4",
                            roomX + roomW - 150, roomY + 50);
                } else if (bossSolved) {
                    g2.setColor(Color.CYAN);
                    g2.drawString("보스 클리어!", roomX + 20, roomY + 30);
                }
            }

            // ctrl 공격 이펙트
            if (showMeleeEffect) {
                long now = System.currentTimeMillis();
                if (now < meleeEffectUntil) {
                    g2.setColor(new Color(255, 0, 0, 120));  // 반투명 빨간색
                    g2.fillRect(meleeAoeX, meleeAoeY, AOE_SIZE, AOE_SIZE);
                } else {
                    showMeleeEffect = false;
                }
            }





            //방 '바로 아래' 공통 입력 바
            int barHeight = 30;
            int barX = roomX;
            int barW = roomW;
            int barY = SCREEN_H - 20;
            int barTop = barY - barHeight;

            g2.setColor(new Color(20, 20, 20)); // 눈에 보이도록 진한 회색
            g2.fillRect(barX, barTop, barW, barHeight);

            g2.setColor(Color.WHITE);
            // 지금은 몹 공격용 입력만 있으니까 typingMode만 표시
            // (나중에 보물방 번호 입력도 여기서 같이 보여주면 됨)
            if (typingMode) {
                String text = "> " + inputBuffer.toString();
                g2.drawString(text, barX + 10, barTop + 25);
            }
        } //paint 끝
    }

}

