package LJE;

import LIM.Word;
import LIM.WrongNotes;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class CommonWordsNotes {
    private final Set<Word> commonset = new HashSet<>();
    WrongNotes wrongnotes = new WrongNotes();


    public void add(Word word) {
        commonset.add(word);
    }

    public void loadFromFile (String filename){
        try(Scanner file = new Scanner(new File(filename))){
            while(file.hasNextLine()){
                String str = file.nextLine().trim();
                if (str.isEmpty()) {
                    continue;
                }
                String[] temp = str.split("\\s{2,}");
                if (temp.length < 2) {
                    System.out.println("파싱 실패 라인: [" + str + "], length=" + temp.length);
                    continue;
                }
                commonset.add(new Word(temp[0].trim(),temp[1].trim()));
                System.out.println("빈출 단어장 불러오기 완료");
            }
        } catch (FileNotFoundException e){
            System.out.println("빈출 단어장 파일이 존재하지 않습니다");
        }
    }

    public void printAll() {
        for (Word word : commonset) {
            System.out.println(word.getEng()+"\t"+word.getKor());
        }
    }

    public void retainWords(){
        commonset.retainAll(wrongnotes.getSet());
    }

    public void printRetainWords() {
        System.out.println("빈출 오답 단어");
        for(Word w : commonset){
            System.out.println(w.getEng()+"\t"+w.getKor());
        }
    }

    public void save(String filename) {
        try {
            File file = new File(filename);
            try (PrintWriter pw = new PrintWriter(file)) {
                for (Word w : commonset) {
                    pw.println(w.getEng() + "\t" + w.getKor());
                }
            }
            System.out.println("빈출 단어장 파일 저장 완료");
        } catch (Exception e) {
            System.out.println("빈출 단어장 파일 저장 실패");
        }
    }


}
