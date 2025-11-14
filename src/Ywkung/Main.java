package Ywkung;

import java.util.Scanner;

    public class Main {
        public static void main(String[] args) {
            Scanner sc = new Scanner(System.in);
            WordFile book = new WordFile("wordbook.csv");

            boolean running = true;

            while (running) {
                System.out.println("\n=== 나만의 영어 단어장 ===");
                System.out.println("1. 단어 추가");
                System.out.println("2. 단어 보기");
                System.out.println("3. 종료");
                System.out.print("선택: ");
                String menu = sc.nextLine();

                switch (menu) {
                    case "1":
                        System.out.print("영어 단어: ");
                        String eng = sc.nextLine();
                        System.out.print("뜻: ");
                        String kor = sc.nextLine();
                        book.addWord(new Word(eng, kor));
                        System.out.println("단어가 추가되었습니다!");
                        break;

                    case "2":
                        book.showAll();
                        break;

                    case "3":
                        System.out.println("프로그램을 종료합니다...");
                        book.save();
                        System.out.println("저장 완료! 프로그램을 종료합니다.");
                        running = false;
                        break;

                    default:
                        System.out.println("잘못된 입력입니다.");
                }
            }
            book.save();
        }
    }

