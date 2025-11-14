package LIM;

import LJE.WrongNotes;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;

public class Manager2 {
    HashMap<String, Word> word = new HashMap<>();
    HashMap<String, Word> wrongword = new HashMap<>();
    static Scanner scanner = new Scanner(System.in);
    WrongNotes wrongnotes = new WrongNotes();

    void voc(String filename) { //파일읽기
        try (Scanner file = new Scanner(new File(filename))) {

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

    void wrongvoc(String filename){
       wrongnotes.toMap();
       wrongnotes.loadFromFile(filename);
    }

    void save(String filename) { //파일 저장
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

    void correct() { //수정
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

    void delete() { //삭제
        System.out.println("===== 단어 삭제 =====");
        System.out.print("삭제할 영단어를 입력하세요: ");
        String eng = scanner.nextLine();

        if (!word.containsKey(eng)) {
            System.out.println("해당 단어가 존재하지 않습니다.");
            return;
        }
        word.remove(eng);
        System.out.println("단어가 삭제되었습니다.");
    }

    void add() { //추가
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

    void search() { //검색
        System.out.println("===== 영단어 검색 =====");
        System.out.print("검색할 단어를 입력하세요(영단어): ");
        String eng = scanner.nextLine();

        if (!word.containsKey(eng)) {
            System.out.println("해당 단어가 존재하지 않습니다.");
            return;
        }
        Word w = word.get(eng);
        System.out.println("뜻 : " + w.getKor());
    }

    void search2() { //부분 검색
        System.out.println("===== 영단어 검색 =====");
        System.out.print("검색할 부분 단어를 입력하세요(영단어): ");
        String eng = scanner.nextLine();

        boolean found = false;
        for (Word w : word.values()) {
            if (w.getEng().startsWith(eng)) {
                System.out.println(w.getEng() + " = " + w.getKor());
                found = true;
            }
        }
        if (!found)
            System.out.println("해당 영단어는 존재하지 않습니다.");
    }
    void wordgame1_1(HashMap<String, Word> wordmap){


        int howlong = 10;
        int howmany = 4;
        List<Word> wronglist = new ArrayList<>();
        List<Word> list = new ArrayList<>(wordmap.values()); // 랜덤 숫자로 뽑기 위해 list 형태로 변환
        Random rand = new Random();
        List<Word> donelist = new ArrayList<>(); // 이미 쓴 단어는 넣지 않기 위함



        int wrongnum = 0; // 틀린 횟수
        if(list.size() < howlong){ // 오답 크기가 howlong보다 클 때 조정
            howlong = wordmap.size();
        }
        for(int i = 0; i < howlong; i++){ //일단 객관식은 10번 반복

            Word ans;
            while(true){
                int donecount = 0;

                ans = list.get(rand.nextInt(list.size()));
                for(int j = 0; j < donelist.size(); j++){
                    if(ans.equals(donelist.get(j))){
                        donecount++;
                    }
                }
                if(donecount == 0){
                    break;
                }
            }
            donelist.add(ans);
            Word wr1;
            Word wr2;
            Word wr3;
            while(true){
                wr1 = list.get(rand.nextInt(list.size()));
                wr2 = list.get(rand.nextInt(list.size()));
                wr3 = list.get(rand.nextInt(list.size()));
                if(wr1.equals(ans) || wr2.equals(ans) || wr3.equals(ans)){
                    continue;
                } else{
                    break;
                }
            }

            List<Word> options = new ArrayList<>();
            options.add(ans);
            options.add(wr1);
            options.add(wr2);
            options.add(wr3);

            System.out.println("=================단어퀴즈("+(i+1)+"/10)=================");
            System.out.println("\t\t\t"+ ans.getEng()+"\t\t\t");


            int length = howmany; // 선지에서 단어 뽑을 때 컨트롤 할 용도
            List<Integer> numarr = new ArrayList<>();

            for(int j = 0;j < howmany; j++){
                numarr.add(j); // 객관식 선택지에서 n번째 단어   를 맡을 리스트의 동일 버전 => numarr_base는 이후 정답 체크에 필요
            }


            List<Integer> choicearr = new ArrayList<>();

            for(int j = 0;j < howmany; j++){
                numarr.add(j); // 선택지에 따른 실제 번호 저장하는 리스트
            }




            for(int j = 0; j < howmany; j++){


                int rnum = rand.nextInt(length);

                int value = numarr.get(rnum);
                System.out.print((j+1) +") "+ options.get(value).getKor()+ "  ");

                choicearr.add(value);


                int switchvalue = numarr.get(length-1);
                numarr.set(length-1, numarr.get(rnum));
                numarr.set(rnum, switchvalue); // 이미 사용한 번호의 위치 교환
                length--;


            }
            System.out.println(); // 줄바꿈용
            System.out.println("정답 : ");
            int uans = scanner.nextInt();
            scanner.nextLine(); // \n 버퍼에서 삭제

            if(options.get(choicearr.get(uans-1)).getKor().equals(ans.getKor())){
                System.out.println("정답입니다!");

            } else{
                System.out.println("오답입니다.");
                wronglist.add(ans);
                wrongnum++;
            }

        }

        System.out.println("==================게임이 끝났습니다==================");
        System.out.println("맞은 횟수 : "+(howlong - wrongnum));
        System.out.println("틀린 단어들 V");
        for(int i = 0; i < wronglist.size(); i++){
            if(wronglist.get(i) != null) {
                System.out.println("====================================================");
                System.out.println("단어 : " + wronglist.get(i).getEng());
                System.out.println("뜻 : " + wronglist.get(i).getKor());
            }
        }

        for(Word w : wronglist){
            int wordcount = 0;
            for(int i = 0; i < wrongword.size(); i++){
                if(w.equals(wronglist.get(i))){
                    wordcount++;
                }
            }
            if(wordcount == 0){
                wrongword.put(w.getEng(), w);
            }
        }

    }

    void wordgame1_2(HashMap<String, Word> wordmap){


        int howlong = 10;
        int howmany = 4;
        List<Word> wronglist = new ArrayList<>();
        List<Word> list = new ArrayList<>(wordmap.values()); // 랜덤 숫자로 뽑기 위해 list 형태로 변환
        Random rand = new Random();
        List<Word> donelist = new ArrayList<>(); // 이미 쓴 단어는 넣지 않기 위함



        int wrongnum = 0; // 틀린 횟수
        if(list.size() < howlong){
            howlong = list.size();
        }
        for(int i = 0; i < howlong; i++){ //일단 객관식은 10번 반복



            Word ans;
            while(true){
                int donecount = 0;

                ans = list.get(rand.nextInt(list.size()));
                for(int j = 0; j < donelist.size(); j++){
                    if(ans.equals(donelist.get(j))){
                        donecount++;
                    }
                }
                if(donecount == 0){
                    break;
                }
            }
            donelist.add(ans);
            Word wr1;
            Word wr2;
            Word wr3;
            while(true){
                wr1 = list.get(rand.nextInt(list.size()));
                wr2 = list.get(rand.nextInt(list.size()));
                wr3 = list.get(rand.nextInt(list.size()));
                if(wr1.equals(ans) || wr2.equals(ans) || wr3.equals(ans)){
                    continue;
                } else{
                    break;
                }
            }

            List<Word> options = new ArrayList<>();
            options.add(ans);
            options.add(wr1);
            options.add(wr2);
            options.add(wr3);

            System.out.println("=================단어퀴즈("+(i+1)+"/10)=================");
            System.out.println("\t\t\t"+ ans.getKor()+"\t\t\t");


            int length = howmany; // 선지에서 단어 뽑을 때 컨트롤 할 용도
            List<Integer> numarr = new ArrayList<>();

            for(int j = 0;j < howmany; j++){
                numarr.add(j); // 객관식 선택지에서 n번째 단어   를 맡을 리스트의 동일 버전 => numarr_base는 이후 정답 체크에 필요
            }


            List<Integer> choicearr = new ArrayList<>();

            for(int j = 0;j < howmany; j++){
                numarr.add(j); // 선택지에 따른 실제 번호 저장하는 리스트
            }




            for(int j = 0; j < howmany; j++){


                int rnum = rand.nextInt(length);

                int value = numarr.get(rnum);
                System.out.print((j+1) +") "+ options.get(value).getEng()+ "  ");

                choicearr.add(value);


                int switchvalue = numarr.get(length-1);
                numarr.set(length-1, numarr.get(rnum));
                numarr.set(rnum, switchvalue); // 이미 사용한 번호의 위치 교환
                length--;


            }
            System.out.println(); // 줄바꿈용
            System.out.println("정답 : ");
            int uans = scanner.nextInt();
            scanner.nextLine(); // \n 버퍼에서 삭제

            if(options.get(choicearr.get(uans-1)).getEng().equals(ans.getEng())){
                System.out.println("정답입니다!");

            } else{
                System.out.println("오답입니다.");
                wronglist.add(ans);
                wrongnum++;
            }

        }

        System.out.println("==================게임이 끝났습니다==================");
        System.out.println("맞은 횟수 : "+(howlong - wrongnum));
        System.out.println("틀린 단어들 V");
        for(int i = 0; i < wronglist.size(); i++){
            if(wronglist.get(i) != null) {
                System.out.println("====================================================");
                System.out.println("단어 : " + wronglist.get(i).getEng());
                System.out.println("뜻 : " + wronglist.get(i).getKor());
            }
        }

        for(Word w : wronglist){
            int wordcount = 0;
            for(int i = 0; i < wrongword.size(); i++){
                if(w.equals(wronglist.get(i))){
                    wordcount++;
                }
            }
            if(wordcount == 0){
                wrongword.put(w.getEng(), w);
            }
        }

    }

    void wordgame2_1(HashMap<String, Word> wordmap){


        int howlong = 10;
        int wrongnum = 0;

        List<Word> wronglist = new ArrayList<>();
        List<Word> list = new ArrayList<>(wordmap.values()); // 랜덤 숫자로 뽑기 위해 list 형태로 변환
        Random rand = new Random();
        List<Word> donelist = new ArrayList<>(); // 이미 쓴 단어는 넣지 않기 위함

        if(list.size() < howlong){
            howlong = list.size();
        }
        for(int i = 0; i < howlong; i++){

            Word ans;
            while(true){
                int donecount = 0;

                ans = list.get(rand.nextInt(list.size()));
                for(int j = 0; j < donelist.size(); j++){
                    if(ans.equals(donelist.get(j))){
                        donecount++;
                    }
                }
                if(donecount == 0){
                    break;
                }
            }
            donelist.add(ans);

            String[] ansKorSet = ans.getKor().split("/");
            for(int j = 0; j < ansKorSet.length; j++){
                ansKorSet[j] = ansKorSet[j].trim();
            } // 한글 뜻을 / 로 분리 & 그 좌우에 있을 수 있는 ' ' 제거
            System.out.println("=================단어퀴즈("+(i+1)+"/10)=================");
            System.out.println("한글 뜻 : \t\t\t"+ ans.getEng()+"\t\t\t");
            System.out.println("정답(영어 단어) : ");
            String[] uans = scanner.nextLine().split("/");
            for(int j = 0; j < uans.length; j++){
                uans[j] = uans[j].trim();
            } // 제출한 정답을 / 로 분리 & 그 좌우에 있을 수 있는 ' ' 제거


            int count = 0; // 정답 체크용
            for(int j = 0; j < uans.length; j++){
                for(int k = 0; k < ansKorSet.length; k++) {
                    if (uans[j].equals(ansKorSet[k])) { // 사용자가 한글뜻 / 한글뜻    형태로 썼을 수도 있으므로
                        System.out.println("정답입니다!");
                        count++;
                        break; // 한 번이라도 정답이면 끝내도록
                    }
                }
            }
            if(count == 0){
                System.out.println("오답입니다");
                wronglist.add(ans);
                wrongnum++;
            }
        }

        System.out.println("==================게임이 끝났습니다==================");
        System.out.println("맞은 횟수 : "+(howlong - wrongnum));
        System.out.println("틀린 단어들 V");
        for(int i = 0; i < wronglist.size(); i++){
            if(wronglist.get(i) != null) {
                System.out.println("====================================================");
                System.out.println("단어 : " + wronglist.get(i).getEng());
                System.out.println("뜻 : " + wronglist.get(i).getKor());
            }
        }


        for(Word w : wronglist){
            int wordcount = 0;
            for(int i = 0; i < wrongword.size(); i++){
                if(w.equals(wronglist.get(i))){
                    wordcount++;
                }
            }
            if(wordcount == 0){
                wrongword.put(w.getEng(), w);
            }
        }
    }

    void wordgame2_2(HashMap<String, Word> wordmap){


        int howlong = 10;
        int wrongnum = 0;

        List<Word> wronglist = new ArrayList<>();
        List<Word> list = new ArrayList<>(wordmap.values()); // 랜덤 숫자로 뽑기 위해 list 형태로 변환
        Random rand = new Random();
        List<Word> donelist = new ArrayList<>(); // 이미 쓴 단어는 넣지 않기 위함

        if(list.size() < howlong){
            howlong = list.size();
        }
        for(int i = 0; i < howlong; i++){

            Word ans;
            while(true){
                int donecount = 0;

                ans = list.get(rand.nextInt(list.size()));
                for(int j = 0; j < donelist.size(); j++){
                    if(ans.equals(donelist.get(j))){
                        donecount++;
                    }
                }
                if(donecount == 0){
                    break;
                }
            }
            donelist.add(ans);
            System.out.println("=================단어퀴즈("+(i+1)+"/10)=================");
            System.out.println("한글 뜻 : \t\t\t"+ ans.getKor()+"\t\t\t");
            System.out.println("정답(영어 단어) : ");
            String uans = scanner.nextLine().trim();
            if(uans.equals(ans.getEng())){
                System.out.println("정답입니다!");
            } else{
                System.out.println("오답입니다");
                wronglist.add(ans);
                wrongnum++;
            }
        }

        System.out.println("==================게임이 끝났습니다==================");
        System.out.println("맞은 횟수 : "+(howlong - wrongnum));
        System.out.println("틀린 단어들 V");
        for(int i = 0; i < wronglist.size(); i++){
            if(wronglist.get(i) != null) {
                System.out.println("====================================================");
                System.out.println("단어 : " + wronglist.get(i).getEng());
                System.out.println("뜻 : " + wronglist.get(i).getKor());
            }
        }


        for(Word w : wronglist){
            int wordcount = 0;
            for(int i = 0; i < wrongword.size(); i++){
                if(w.equals(wronglist.get(i))){
                    wordcount++;
                }
            }
            if(wordcount == 0){
                wrongword.put(w.getEng(), w);
            }
        }
    }

    public void showWrongWords() {
        List<Word> wronglist = new ArrayList<>(wrongword.values()); // 순서대로 출력 위해 list로 전환

        for(Word w : wronglist){
            System.out.print(w);
        }
    }

    public void deleteWrongwords() {

        if(wrongword.size() == 0){
            System.out.println("오답이 현재 없습니다.");
            return;
        }
        int howlong = wrongword.size();

        List<Word> list = new ArrayList<>(wrongword.values()); // 랜덤 숫자로 뽑기 위해 list 형태로 변환
        List<Word> list_kor = new ArrayList<>(list);
        List<Word> list_eng = new ArrayList<>(list);
        List<Boolean> kor_list_num = new ArrayList<>();
        List<Boolean> eng_list_num = new ArrayList<>();

        for(int i = 0; i < list.size(); i++){
            kor_list_num.add(false);
            eng_list_num.add(false);
        }
        Random rand = new Random();
        List<Word> donelist1 = new ArrayList<>(); // 이미 쓴 단어는 넣지 않기 위함
        List<Word> donelist2 = new ArrayList<>();


        // 영어 단어로 한글 뜻 맞추기
        for(int i = 0; i < howlong; i++){

            Word ans;
            while(true){
                int donecount = 0;

                ans = list_eng.get(rand.nextInt(list_eng.size()));
                for(int j = 0; j < donelist1.size(); j++){
                    if(ans.equals(donelist1.get(j))){
                        donecount++;
                    }
                }
                if(donecount == 0){
                    break;
                }
            }
            donelist1.add(ans);

            String[] ansKorSet = ans.getKor().split("/");
            for(int j = 0; j < ansKorSet.length; j++){
                ansKorSet[j] = ansKorSet[j].trim();
            } // 한글 뜻을 / 로 분리 & 그 좌우에 있을 수 있는 ' ' 제거
            System.out.println("=================오답퀴즈1("+(i+1)+"/"+list.size()+")=================");
            System.out.println("한글 뜻 : \t\t\t"+ ans.getEng()+"\t\t\t");
            System.out.println("정답(영어 단어) : ");
            String[] uans = scanner.nextLine().split("/");
            for(int j = 0; j < uans.length; j++){
                uans[j] = uans[j].trim();
            } // 제출한 정답을 / 로 분리 & 그 좌우에 있을 수 있는 ' ' 제거


            int count = 0; // 정답 체크용
            for(int j = 0; j < uans.length; j++){
                for(int k = 0; k < ansKorSet.length; k++) {
                    if (uans[j].equals(ansKorSet[k])) { // 사용자가 한글뜻 / 한글뜻    형태로 썼을 수도 있으므로
                        System.out.println("정답입니다!");
                        eng_list_num.set(list.indexOf(ans), true);
                        count++;
                        break; // 한 번이라도 정답이면 끝내도록
                    }
                }
            }
            if(count == 0){
                System.out.println("오답입니다");
            }
        }


        // 한글 뜻으로 영어 단어 맞추기
        for(int i = 0; i < howlong; i++){

            Word ans;
            int rnum = 0;
            while(true){
                int donecount = 0;

                rnum = rand.nextInt(list_kor.size());
                ans = list_kor.get(rnum);
                for(int j = 0; j < donelist2.size(); j++){
                    if(ans.equals(donelist2.get(j))){
                        donecount++;
                    }
                }
                if(donecount == 0){
                    break;
                }
            }
            donelist2.add(ans);
            System.out.println("=================오답퀴즈2("+(i+1)+"/"+list.size()+")=================");
            System.out.println("한글 뜻 : \t\t\t"+ ans.getKor()+"\t\t\t");
            System.out.println("정답(영어 단어) : ");
            String uans = scanner.nextLine().trim();
            if(uans.equals(ans.getEng())){
                System.out.println("정답입니다!");
                kor_list_num.set(list.indexOf(ans), true);

            } else{
                System.out.println("오답입니다");
            }
        }

        int all = list.size(); // 이후에 list.size() 가 줄어들진 않지만, 혹시 수정할 상황을 고려해
        int correct = 0;
        for(int i = 0; i < list.size(); i++){
            if(kor_list_num.get(i) && eng_list_num.get(i)){
                wrongword.remove(list.get(i).getEng());
                correct++;
            }

        }

        List<Word> yetwrong = new ArrayList<>(wrongword.values());


        System.out.println("==================오답 퀴즈가 끝났습니다==================");
        System.out.println("맞은 횟수 : "+correct+"/"+all);
        System.out.println("틀린 단어들(오답 단어장에 남을 단어들) V");
        for(Word w : yetwrong){
            System.out.println(w);
        }




    }
}

