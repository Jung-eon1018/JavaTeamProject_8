package Jjh;

import LIM.Word;
import LJE.CommonWordsNotes;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;

public class Manager2 {

    HashMap<String, Word> word = new HashMap<>();
    CommonWordsNotes commonnotes = new CommonWordsNotes();
    static Scanner scanner = new Scanner(System.in);

    public void voc(File filename) { //파일읽기
        try (Scanner file = new Scanner(new File(filename.getPath()))) {

            while (file.hasNextLine()) {
                String str = file.nextLine();
                String[] temp = str.split("\t");

                Word w = new Word(temp[0].trim(), temp[1].trim());
                word.put(temp[0].trim(), w);
            }

            System.out.println("단어장 불러오기 완료");
        } catch (FileNotFoundException e) {
            System.out.println("파일을 찾을 수 없습니다.");
        }
    }

    public void save(File filename) { //파일 저장
        try(PrintWriter pw = new PrintWriter(filename)){
            for(String key :word.keySet()){
                Word w = word.get(key);
                pw.println(w.getEng() + "\t" + w.getKor());
            }
            System.out.println("파일 저장 완료");
        }catch(Exception e){
            System.out.println("파일 저장 실패");
        }
    }

    void list() {

    }

    void savecommon() {
        File commonFile = new File("data/common.txt"); // 빈출 단어장 파일
        commonnotes.save(commonFile.getPath());
    }

    public void correct() { //수정
        System.out.println("===== 단어 수정 =====");
        System.out.print("뜻을 수정할 영단어을 입력하세요: ");
        String eng = scanner.nextLine();
        if (!word.containsKey(eng)) {
            System.out.println("해당 단어가 존재하지 않습니다.");
            return;
        }

        System.out.print("뜻을 입력하세요: ");
        String kor = scanner.nextLine();

        Word w = word.get(eng);
        w.setKor(kor);
        System.out.println("단어가 수정되었습니다.");
    }

    public void delete() { //삭제
        System.out.println("===== 단어 삭제 =====");
        System.out.print("삭제할 영단어를 입력하세요: ");
        String eng = scanner.nextLine();

        if (!word.containsKey(eng)) {
            System.out.println("해당 단어가 존재하지 않습니다.");
            return;
        }
        Word w = word.remove(eng);
        System.out.println("단어가 삭제되었습니다.");

    }



    public void add() { //추가
        System.out.println("===== 단어 추가 =====");
        System.out.print("추가할 영단어를 입력하세요: ");
        String term = scanner.nextLine();
        if(word.containsKey(term)) {
            System.out.println("이미 존재하는 영단어입니다.");
            return;
        }
        System.out.print("영단어 뜻을 입력하세요: ");
        String meaning = scanner.nextLine();

        word.put(term, new Word(term, meaning));
        System.out.println("단어를 성공적으로 등록했습니다.");
    }

    public void search() { //검색
        System.out.println("===== 영단어 검색 =====");
        System.out.print("검색할 단어를 입력하세요(영단어): ");
        String eng = scanner.nextLine();

        if (!word.containsKey(eng)) {
            System.out.println("해당 단어가 존재하지 않습니다.");
            return;
        }
        Word w = word.get(eng);
        System.out.println("뜻 : " + w.getKor());
        //빈출 단어장 추가 여부
        System.out.print("이 단어를 빈출 단어장에 추가하시겠습니까? (Y/N): ");
        String k = scanner.nextLine();
        if(k.equals("Y")){
            commonnotes.add(w);
            savecommon();
            System.out.println("빈출 단어장에 추가되었습니다.");
        }else
            System.out.println("빈출 단어장에 추가하지 않았습니다.");
    }

    public void search2() { //부분 검색
        System.out.println("===== 영단어 검색 =====");
        System.out.print("검색할 부분 단어를 입력하세요(영단어): ");
        String eng = scanner.nextLine();

        boolean found = false;
        for (Word w : word.values()) {
            if (w.getEng().startsWith(eng)) {
                System.out.println(w.getEng() + " = " + w.getKor());
                found = true;
                //빈출 단어장 추가 여부
                System.out.print("이 단어를 빈출 단어장에 추가하시겠습니까? (Y/N): ");
                String k = scanner.nextLine();
                if(k.equals("Y")){
                    commonnotes.add(w);
                    savecommon();
                    System.out.println("빈출 단어장에 추가되었습니다.");
                }else
                    System.out.println("빈출 단어장에 추가하지 않았습니다.");
            }
        }
        if (!found)
            System.out.println("해당 영단어는 존재하지 않습니다.");

    }


}

