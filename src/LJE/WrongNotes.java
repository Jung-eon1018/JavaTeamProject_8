package LJE;

import LIM.Word;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class WrongNotes {
    private final Set<String> set = new HashSet<>();

    public void markWrong(String eng, String kor){
        String e = eng == null ? "" : eng.trim().toLowerCase();
        String k = kor == null ? "" : kor.trim();
        set.add(e+"\t"+k);
    }

    public void loadFromFile (String filename){
        try(Scanner file = new Scanner(new File(filename))){
            while(file.hasNextLine()){
                String str = file.nextLine();
                set.add(str.trim());
                System.out.println("오답 단어장 불러오기 완료");
            }
        } catch (FileNotFoundException e){
            System.out.println("오답 파일이 존재하지 않습니다");
        }
    }

    public Map<String, Word> toMap(){
        Map<String, Word> map = new HashMap<>();

        for(String word : set){
            String[] temp = word.split("\t");
            if(temp.length == 2){
                String eng = temp[0].trim();
                String kor = temp[0].trim();
                map.put(eng,new Word(eng,kor));
            }
        }
        return map;
    }


    public void save(String path) {

        try {
            File file = new File(path);

            File parent = file.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
                System.out.println("폴더 자동 생성됨: " + parent.getAbsolutePath());
            }

            try (PrintWriter out = new PrintWriter(file, "UTF-8")) {
                out.println("===== 오답노트 =====");
                for (String w : set) out.println(w);
            }
            System.out.println("저장 완료: " + file.getAbsolutePath());
        } catch (IOException e) {
            System.out.println("오답 저장 실패: " + e.getMessage());
        }
    }

    public void printAll(){
        System.out.println("===== 오답목록 =====");
        for(String w:set){
            System.out.println(w);
        }
    }
}