package Jjh;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.Vector;

public class Manager {
    Vector<Word> word = new Vector<>();
    static Scanner scanner = new Scanner(System.in);


    void addWord(String eng, String kor){
        this.word.add(new Word(eng, kor));
    }

    void voc(String filename){
        try(Scanner file = new Scanner(new File(filename))){

            while(file.hasNextLine()){
                String str = file.nextLine();
                String[] temp = str.split("\t");

                this.addWord(temp[0].trim(), temp[1].trim());
            }

            System.out.println("단어장 불러오기 완료");
        }catch(FileNotFoundException e){
            System.out.println("파일을 찾을 수 없습니다.");
        }
    }

    void add(){
        System.out.println("===== 단어 추가 =====");
        System.out.print("추가할 영단어를 입력하세요: ");
        String term = scanner.nextLine();
        System.out.print("영단어 뜻을 입력하세요: ");
        String meaning = scanner.nextLine();
        this.addWord(term, meaning);
        System.out.println("단어를 성공적으로 등록했습니다.");
    }

    void search(){
        System.out.println("===== 영단어 검색 =====");
        System.out.print("검색할 단어를 입력하세요(영단어): ");
        String words = scanner.nextLine();

        boolean found = false;
        for(Word w : word){
            if(w.getEng().equals(words)){
                System.out.println("뜻: " + w.getKor());
                found = true;
                break;
            }
        }
        if(!found)
            System.out.println("해당 영단어는 존재하지 않습니다.");
    }

    void search2(){
        System.out.println("===== 영단어 검색 =====");
        System.out.print("검색할 부분 단어를 입력하세요(영단어): ");
        String words = scanner.nextLine();

        boolean found = false;
        for(Word w : word){
            if(w.getEng().contains(words)){
                System.out.println(w + " = " + w.getKor());
                found = true;
            }
        }
        if(!found)
            System.out.println("해당 영단어는 존재하지 않습니다.");
    }
}
