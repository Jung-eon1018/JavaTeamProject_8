package LIM;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class WrongNotes {
    private final Set<Word> set = new HashSet<>();

    public void add(Word w){
        set.add(w);
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
                set.add(new Word(temp[0].trim(),temp[1].trim()));
                System.out.println("오답 단어장 불러오기 완료");
            }
        } catch (FileNotFoundException e){
            System.out.println("오답 파일이 존재하지 않습니다");
        }
    }

    public Map<String, Word> toMap(){
        Map<String, Word> map = new HashMap<>();

        for(Word word : set){
            map.put(word.getEng(),word);
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
                for (Word w : set) out.println(w);
            }
            System.out.println("저장 완료: " + file.getAbsolutePath());
        } catch (IOException e) {
            System.out.println("오답 저장 실패: " + e.getMessage());
        }
    }

    public Set<Word> getSet(){
        return set;
    }

    public void printAll(){
        System.out.println("===== 오답목록 =====");
        for(Word w:set){
            System.out.println(w);
        }
    }
}
