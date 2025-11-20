package LIM;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Scanner;

//public class TestMain {
//
//    public static void main(String[] args) {
//        Scanner sc = new Scanner(System.in);
//        Manager2 wdm = new Manager2();
//        wdm.voc("src/LIM/res/word.txt");
//        wdm.wrongvoc("src/LIM/res/wrong.txt");
//
//
//
//        while (true) {
//            System.out.println("\n=== 나만의 영어 단어장 ===");
//            System.out.println("1. 단어 추가");
//            System.out.println("2. 단어 보기");
//            System.out.println("3. 단어 게임(모든 단어)");
//            System.out.println("4. 단어 게임(오답만)");
//            System.out.println("5. 오답 단어 보기");
//            System.out.println("6. 오답 단어 제거(영어 & 뜻 맞히는 퀴즈)(주관식)");
//            System.out.println("7. 종료");
//            System.out.print("선택: ");
//            String menu = sc.nextLine();
//
//            switch (menu) {
//                case "1":
//                    System.out.println("단어가 추가되었습니다!");
//                    break;
//                case "2":
//                    break;
//                case "3":
//                    quizprint(wdm, wdm.word);
//                    break;
//                case "4": {
//                    if (wdm.wrongword.size() == 0) {
//                        System.out.println("현재 오답이 없습니다");
//                        break;
//                    }
//                    quizprint(wdm, wdm.wrongword);
//                }
//                break;
//                case "5":
//                    wdm.showWrongWords();
//                    break;
//                case "6":
//                    wdm.deleteWrongwords();
//                    break;
//                case "7":
//                    System.out.println("저장 후 종료합니다...");
//                    saveFiles("src/LIM/res/word.txt", "src/LIM/res/wrong.txt");
//                    return;
//                default:
//                    System.out.println("잘못된 입력입니다.");
//            }
//        }
//    }
//
//    public static void saveFiles(String wordPath, String wrongPath) {
//        HashMap<String, Word> word = null;
//        saveMapToFile(wordPath, word);
//        HashMap<String, Word> wrongword = null;
//        //틀린 답 저장하기
//        saveMapToFile(wrongPath, wrongword);
//        System.out.println("모든 단어가 파일에 저장되었습니다.");
//    }
//
//    private static void saveMapToFile(String filename, HashMap<String, Word> map) {
//        try (PrintWriter pw = new PrintWriter(new FileWriter(filename))) {
//            for (Word w : map.values()) {
//                pw.println(w.getEng() + "," + w.getKor());
//            }
//        } catch (IOException e) {
//            System.out.println("저장 중 오류 발생 (" + filename + "): " + e.getMessage());
//        }
//    }
//
//    public static void quizprint(Manager2 wdm, HashMap<String, Word> quizlist){
//        Scanner sc = new Scanner(System.in);
//        System.out.println("실행하실 퀴즈 모드를 선택해주세요");
//        System.out.println("1) 객관식");
//        System.out.println("2) 주관식");
//        int ans = sc.nextInt();
//        sc.nextLine();
//        switch (ans){
//            case 1 -> {
//                System.out.println("실행하실 퀴즈 설정을 선택해주세요");
//                System.out.println("1) 영어 단어(정답 : 한글 뜻)");
//                System.out.println("2) 한글 뜻(정답 : 영어 단어");
//                int ans2 = sc.nextInt();
//                sc.nextLine();
//                switch (ans2) {
//                    case 1 -> wdm.wordgame1_1(quizlist);
//                    case 2 -> wdm.wordgame1_2(quizlist);
//                }
//            }
//            case 2 -> {
//                System.out.println("실행하실 퀴즈 설정을 선택해주세요");
//                System.out.println("1) 영어 단어(정답 : 한글 뜻)");
//                System.out.println("2) 한글 뜻(정답 : 영어 단어");
//                int ans2 = sc.nextInt();
//                sc.nextLine();
//                switch (ans2) {
//                    case 1 -> wdm.wordgame2_1(quizlist);
//                    case 2 -> wdm.wordgame2_2(quizlist);
//                }
//            }
//        }
//    }
//}