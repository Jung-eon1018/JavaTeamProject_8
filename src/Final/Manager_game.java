package Final;


import java.io.*;
import java.util.*;

public class Manager_game {
    HashMap<String, Word> commonword = new HashMap<>();
    WrongNotes wns = new WrongNotes();
    static Scanner scanner = new Scanner(System.in);

    private HashMap<String, Word> word = new HashMap<>();
    private HashMap<String, Word> wrongword = new HashMap<>();
    WrongNotes wrongnotes = new WrongNotes();

    public HashMap<String, Word> getWordMap() {
        return word;
    }

    public HashMap<String, Word> getWrongWordMap() {
        return wrongword;
    }

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

    public void wrongvoc(File filename){
        wrongnotes.loadFromFile(filename.getPath());
        wrongword = (HashMap<String, Word>) wrongnotes.toMap();
    }


    public static void quizprint(Manager_game wdm, HashMap<String, Word> quizlist){
        Scanner sc = new Scanner(System.in);
        System.out.println("실행하실 퀴즈 모드를 선택해주세요");
        System.out.println("1) 객관식");
        System.out.println("2) 주관식");
        int ans = sc.nextInt();
        sc.nextLine();
        switch (ans){
            case 1 -> {
                System.out.println("실행하실 퀴즈 설정을 선택해주세요");
                System.out.println("1) 영어 단어(정답 : 한글 뜻)");
                System.out.println("2) 한글 뜻(정답 : 영어 단어)");
                int ans2 = sc.nextInt();
                sc.nextLine();
                switch (ans2) {
                    case 1 -> wdm.wordgame1_1(quizlist);
                    case 2 -> wdm.wordgame1_2(quizlist);
                }
            }
            case 2 -> {
                System.out.println("실행하실 퀴즈 설정을 선택해주세요");
                System.out.println("1) 영어 단어(정답 : 한글 뜻)");
                System.out.println("2) 한글 뜻(정답 : 영어 단어)");
                int ans2 = sc.nextInt();
                sc.nextLine();
                switch (ans2) {
                    case 1 -> wdm.wordgame2_1(quizlist);
                    case 2 -> wdm.wordgame2_2(quizlist);
                }
            }
        }
    }


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
        try (Scanner file = new Scanner(new File(filename))) {

            while (file.hasNextLine()) {
                String str = file.nextLine();
                String[] temp = str.split("\t");

                Word w = new Word(temp[0].trim(), temp[1].trim());
                wrongword.put(temp[0].trim(), w);
            }

            System.out.println("오답 단어장 불러오기 완료");
        } catch (FileNotFoundException e) {
            System.out.println("오답이 존재하지 않습니다.");
        }
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
            System.out.print("정답 : ");
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
            System.out.print("정답 : ");
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
            System.out.println("영어 단어 : \t\t\t"+ ans.getEng()+"\t\t\t");
            System.out.print("정답(한글 뜻) : ");
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
            System.out.print("정답(영어 단어) : ");
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
            System.out.println("영어 단어 : \t\t\t"+ ans.getEng()+"\t\t\t");
            System.out.print("정답(한글 뜻) : ");
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
            System.out.print("정답(영어 단어) : ");
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

    public void saveFiles(File wordfile, File wrongfile) {
        saveMapToFile(wordfile, this.word);
        saveMapToFile(wrongfile, this.wrongword);

        System.out.println("모든 단어가 파일에 저장되었습니다.");
    }

    private static void saveMapToFile(File filename, HashMap<String, Word> map) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(filename.getPath()))) {
            for (Word w : map.values()) {
                pw.println(w.getEng() + "\t" + w.getKor());
            }
        } catch (IOException e) {
            System.out.println("저장 중 오류 발생 (" + filename + "): " + e.getMessage());
        }
    }

    public void hintgame(HashMap<String, Word> wordmap){

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
            int hintspellnum = (ans.eng.length() * 2) / 5; // 정답 글자 수의 40%을 힌트로 줄 예정
            List<Integer> numlist = new ArrayList<>();
            List<Character> hint = new ArrayList<>();
            for(int j = 0; j < ans.eng.length(); j++){
                numlist.add(j);
                hint.add('_');
            }
            Collections.shuffle(numlist);
            for(int j = 0; j < hintspellnum; j++){
                hint.set(numlist.get(j), ans.eng.charAt(numlist.get(j)));
            }


            donelist.add(ans);


            int wrongnow = 0;
            for(int j = 0; j < 2; j++){
                System.out.println("=================단어퀴즈("+(i+1)+"/10)=================");
                System.out.println("한글 뜻 : \t\t\t"+ ans.getKor()+"\t\t\t");
                if(wrongnow != 0) {
                    System.out.print("힌트 : " );
                    for(Character hintc : hint){
                        System.out.print(hintc + " ");
                    }
                    System.out.println();
                }
                System.out.print("정답(영어 단어) : ");
                String uans = scanner.nextLine().trim();
                if(uans.equals(ans.getEng())){
                    System.out.println("정답입니다!");
                    break;
                } else if(wrongnow == 0){
                    System.out.println("오답입니다 (기회 1/2)");
                    wrongnow++;
                } else{
                    System.out.println("오답입니다 (기회 2/2)");
                    wronglist.add(ans);
                    wrongnum++;
                }
            }

        }

        System.out.println("==================게임이 끝났습니다==================");
        System.out.println("틀린 횟수 : "+wrongnum);
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
}


