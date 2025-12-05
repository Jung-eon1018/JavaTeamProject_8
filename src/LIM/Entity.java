package LIM;

public class Entity {
    public String name;
    public int sizex; // 실제 크기
    public int sizey;
    public int attackstyle; // 공격 종류          2 : 활, 3 : 폭발
    public int dmg; // 데미지
    public int armor; // 방어도
    public int x; // 좌표
    public int y;
    public int hp;


    public String quizEng; // 정답 단어 관련
    public String quizKor;

    public boolean attacking; // 활 쏘는애 용
    public long attackStartTime; // 활 쏘기 시작
    public long lastShotTime; // 활 쏘고 잠깐 딜레이
    public int laserTargetX; // 활 선 고정용
    public int laserTargetY;

    public boolean bombPrimed = false;   // 폭발 준비 상태인지
    public long bombStartTime = 0;       // 폭발 준비 시작 시간

    public long lastHitTime = 0; // 마지막으로 데미지 받은 시간
    public long invincibleMs = 1000; // 무적시간


    // armor : 50-armor/50 을 데미지에 곱할 예정

    public Entity(int x, int y, int sizex, int sizey) {
        this.x = x;
        this.y = y;
        this.sizex = sizex;
        this.sizey = sizey;
        this.attackstyle = 1;
        this.dmg = 10;
        this.armor = 0;
    }
    public Entity(Entity e){
        this.name = e.name;
        this.sizex = e.sizex;
        this.sizey = e.sizey;
        this.attackstyle = e.attackstyle;
        this.dmg = e.dmg;
        this.armor = e.armor;
        this.x = 0;
        this.y = 0;
        this.hp = e.hp;
    }

    public Entity(String name, int hp, int dmg, int armor){
        this.name = name;
        this.attackstyle = 1;
        this.dmg = dmg;
        this.armor = armor;
        this.hp = hp;
    }

    public Entity(String name, int hp, int dmg, int armor, int attackstyle){
        this.name = name;
        this.attackstyle = attackstyle;
        this.dmg = dmg;
        this.armor = armor;
        this.hp = hp;
    }
}
