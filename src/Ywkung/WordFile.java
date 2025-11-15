package Ywkung;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class WordFile {
    private List<Word> list = new ArrayList<>();
    private String filename;

    public WordFile(String filename) {
        this.filename = filename;
        load();
    }

    public void addWord(Word w) {
        list.add(w);
    }

    public void showAll() {
        if (list.isEmpty()) {
            System.out.println("저장된 단어가 없습니다.");
            return;
        }
        System.out.println("\n--- 단어 목록 ---");
        for (Word w : list) System.out.println(w);
    }

    // 저장
    public void save() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(filename))) {
            for (Word w : list)
                pw.println(w.getEng() + "," + w.getKor());
        } catch (IOException e) {
            System.out.println("저장 실패: " + e.getMessage());
        }
    }

    // 불러오기
    private void load() {
        File file = new File(filename);
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2)
                    list.add(new Word(parts[0], parts[1]));
            }
        } catch (IOException e) {
            System.out.println("불러오기 실패: " + e.getMessage());
        }
    }
}



