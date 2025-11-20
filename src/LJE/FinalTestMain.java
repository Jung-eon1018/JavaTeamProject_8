package LJE;

import Jjh.Manager2;
import LIM.Manager2_game;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class FinalTestMain {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Manager2 manager = new Manager2();
        Manager2_game game = new Manager2_game();

        File file = new File("data/word.txt");
        File wrongfile = new File("data/word.txt");

        try {
            if (!file.exists()) {

                file.getParentFile().mkdirs(); // data 폴더가 없으면 생성
                file.createNewFile();
                System.out.println("단어 파일이 존재하지 않아 새로 생성합니다.");
            }
        } catch (IOException e) {
            System.out.println("파일 생성 중 오류 발생: " + e.getMessage());
        }

        manager.voc(file);
        game.wrongvoc(wrongfile);

        while(true){
            System.out.println("\n=== 나만의 영어 단어장 ===");
            System.out.println("1. 단어 관리자");
            System.out.println("2. 단어 게임");
            System.out.println("3. 종료");
            System.out.print("선택: ");
            String menu = sc.nextLine();

            switch (menu){
                case "1"-> Word_Manager(manager,file);
                case "2"-> Word_Game(game,file,wrongfile);
                case "3"-> {
                    System.out.println("프로그램을 종료합니다");
                    return;
                }
                default -> System.out.println("잘못된 입력입니다");
            }
        }

    }

    public static void Word_Manager(Manager2 manager, File file) {
        Scanner sc = new Scanner(System.in);
        System.out.println("\n단어 관리자를 실행합니다.");
        System.out.println("1.단어 목록");
        System.out.println("2.단어 추가");
        System.out.println("3.단어 수정");
        System.out.println("4.단어 삭제");
        System.out.println("5.단어 검색");
        System.out.println("6.부분 단어 검색");
        String manager_menu = sc.nextLine();

        switch (manager_menu){
            case "2" -> {
                manager.add();
                manager.save(file);
            }
            case "3" -> {
                manager.correct();
                manager.save(file);
            }
            case "4" -> {
                manager.delete();
                manager.save(file);
            }
            case "5" ->{
                manager.search();
            }
            case "6" ->{
                manager.search2();
            }
        }

    }

    public static void Word_Game(Manager2_game game, File file, File wrongfile){
        Scanner sc = new Scanner(System.in);
        System.out.println("\n단어 게임을 실행합니다.");
        System.out.println("1.모든단어 게임");
        System.out.println("2.오답단어 게임");
        System.out.println("3.오답단어 보기");
        System.out.println("4.오답단어 제거하기 게임");
        System.out.println("5.게임 종료");
        String game_menu = sc.nextLine();

        switch (game_menu){
            case "1" -> {
                game.quizprint(game,game.getWordMap());
            }
            case "2" -> {
                if(game.getWordMap().size()==0){
                    System.out.println("현재 오답이 없습니다.");
                }
                game.quizprint(game,game.getWrongWordMap());
            }
            case "3"->{
                game.showWrongWords();
            }
            case "4"->{
                game.deleteWrongwords();
            }
            case "5"->{
                game.saveFiles(file,wrongfile);
            }
        }
    }
}
