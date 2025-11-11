package LJE;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;

public class WrongNotes {
    private final Set<String> set = new HashSet<>();

    public void markWrong(String eng, String kor){
        String e = eng == null ? "" : eng.trim().toLowerCase();
        String k = kor == null ? "" : kor.trim();
        set.add(e+"\t"+k);
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
        System.out.println("=====오답목록=====");
        for(String w:set){
            System.out.println(w);
        }
    }
}
