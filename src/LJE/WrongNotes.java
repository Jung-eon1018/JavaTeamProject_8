package LJE;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;

public class WrongNotes {
    private final Set<String> set = new HashSet<>();

    public void markWrong(String eng, String kor){
        String e = eng == null ? "" : eng.trim().toLowerCase();
        String k = kor == null ? "" : kor.trim();
        set.add(e+"/t"+k);
    }

    public void save(String path) {
        try(PrintWriter out = new PrintWriter(path,"UTF-8")) {
            System.out.println("오답노트");
            for(String w:set){
                out.print(w);
            }
        }catch(IOException e){
            System.out.println("오답 저장에 실패했습니다."+e.getMessage());
        }
    }

    public void printAll(){
        System.out.println("=====오답목록=====");
        for(String w:set){
            System.out.println(w);
        }
    }
}
