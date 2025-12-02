package Final;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.*;
import java.util.List;

public class WordSwing extends JFrame {
    private final Manager manager = new Manager();
    private final WrongNotes wrongNotes = new WrongNotes();
    private final List<Word> wordlist = new ArrayList<>(); // 전체 단어 목록
    private final List<Word> wronglist = new ArrayList<>();// 오답 단어 목록
    private final Set<Word> commonlist = new LinkedHashSet<>();//빈출 단어 목록
    private final List<Word> wrongView = new ArrayList<>(); // 오답 패널의 테이블에만 보이는 단어 목록
    String filename; //현재 단어 파일 경로
    private File wordFile; //실제 단어 파일을 나타내는 객체
    private File wrongFile;
    private String nowPanel = "HOME"; // 현재 어떤 화면인지 기록
    private final CardLayout cl = new CardLayout(); //화면이 바뀔 때 사용되는 레이아웃
    private final JPanel cp = new JPanel(cl); // cl이 적용된 메인 패널
    String[] header = {"영단어", "뜻"}; //테이블 헤더
    private final HomePanel home = new HomePanel(); //타이틀 + 버튼
    private final WordPanel word = new WordPanel(); // 단어관리자 화면
    private final WrongPanel wrong = new WrongPanel(); //오답&빈출 화면
    private final SubGamePanel subGame = new SubGamePanel();
    private final ObjGamePanel objGame = new ObjGamePanel();
    private final CharacterPanel character = new CharacterPanel();
    private final WrongGame wrongGame = new WrongGame(wrongNotes, character);


    private JMenuItem itemHome = new JMenuItem("처음으로");
    private JMenuItem itemAsc = new JMenuItem("오름차순");
    private JMenuItem itemDesc = new JMenuItem("내림차순");

    WordSwing(){
        setTitle("단어장");
        setSize(900, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setJMenuBar(menubar());
        Container c = getContentPane();

        c.setLayout(new BorderLayout());
        //CardLayout이 적용된 패널에 각 화면을 등록 (with 이름)
        cp.add(home, "HOME"); //첫 화면
        cp.add(word,"WORD"); // 단어 관리자 화면
        cp.add(subGame,"SUB"); //주관식 게임
        cp.add(objGame,"OBJ"); // 객관식 게임
        cp.add(wrong,"WRONG"); //오답&빈출 화면
        cp.add(wrongGame, "WRONGGAME");
        cp.add(character, "CHARACTER");
        c.add(cp);
        cl.show(cp, "HOME");
        setVisible(true);
    }
    void loadFile(){
        if(filename==null){
            JOptionPane.showMessageDialog(this, "파일이 존재하지 않습니다.");
            return;
        }
        File f = new File(filename);
        manager.voc(f); //파일에서 단어 읽기
        wordlist.clear(); //기존 목록 비우기
        wordlist.addAll(manager.word.values());
        word.update(wordlist); //테이블에 반영
    }
    //wordlist내용을 manager.word에 동기화
    void syn(){
        manager.word.clear();
        for(Word w : wordlist){
            manager.word.put(w.getEng(), w);
        }
    }
    // 파일 저장
    void saved(){
        syn();
        manager.save(wordFile);
    }

    //단어장에서만 메뉴바에 오름차순 내림차순 버튼이 뜨도록 하기
    private void updateMenuByPanel(String panelName){
        nowPanel = panelName; //어떤 화면인지 불러오는 기능
        boolean isWordOrWrong = panelName.equals("WORD")||panelName.equals("WRONG");

        itemAsc.setVisible(isWordOrWrong);
        itemDesc.setVisible(isWordOrWrong);
    }


    //메뉴바 생성
    private JMenuBar menubar(){
        JMenuBar bar = new JMenuBar();
        JMenu menu = new JMenu("메뉴");
//        JMenuItem item1 = new JMenuItem("처음으로");
//        JMenuItem item2 = new JMenuItem("오름차순");
//        JMenuItem item3 = new JMenuItem("내림차순");

        itemHome.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cl.show(cp,"HOME");
                updateMenuByPanel("HOME");
            }
        });
        itemAsc.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(nowPanel.equals("WORD")) {
                    if (wordlist.isEmpty()) {
                        JOptionPane.showMessageDialog(null, "단어 파일을 불러오세요");
                        return;
                    }
                    Collections.sort(wordlist, new Comparator<Word>() {
                        @Override
                        public int compare(Word o1, Word o2) {
                            return o1.getEng().compareTo(o2.getEng());
                        }
                    });
                    word.update(wordlist);
                }else if(nowPanel.equals("WRONG")){
                    if(wrongView.isEmpty()){
                        JOptionPane.showMessageDialog(null, "단어 파일을 불러오세요");
                        return;
                    }
                    //wrongView가 오답 전체(wronglist)인지 체크, 오답 전체가 아니면 교집합
                    boolean Full = (wrongView.size() == wronglist.size() && wrongView.contains(wronglist));
                    if(Full) { // 오답 전체면 오답들 정렬
                        Collections.sort(wronglist, new Comparator<Word>() {
                            @Override
                            public int compare(Word o1, Word o2) {
                                wrongView.clear();
                                wrongView.addAll(wronglist);
                                return o1.getEng().compareTo(o2.getEng());
                            }
                        });
                    }else{
                        Collections.sort(wrongView, new Comparator<Word>() {
                            @Override
                            public int compare(Word o1, Word o2) {
                                return o1.getEng().compareTo(o2.getEng());
                            }
                        });
                    }
                    wrong.update(wrongView);
                }

            }
        });
        itemDesc.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(nowPanel.equals("WORD")) {
                    if (wordlist.isEmpty()) {
                        JOptionPane.showMessageDialog(null, "단어 파일을 불러오세요");
                        return;
                    }
                    Collections.sort(wordlist, new Comparator<Word>() {
                        @Override
                        public int compare(Word o1, Word o2) {
                            return o2.getEng().compareTo(o1.getEng());
                        }
                    });
                    word.update(wordlist);
                }else if(nowPanel.equals("WRONG")){
                    if(wronglist.isEmpty()){
                        JOptionPane.showMessageDialog(null, "단어 파일을 불러오세요");
                        return;
                    }
                    boolean Full = (wrongView.size() == wronglist.size() && wrongView.contains(wronglist));
                    if(Full) {
                        Collections.sort(wronglist, new Comparator<Word>() {
                            @Override
                            public int compare(Word o1, Word o2) {
                                wrongView.clear();
                                wrongView.addAll(wronglist);
                                return o2.getEng().compareTo(o1.getEng());
                            }
                        });
                    }else{
                        Collections.sort(wrongView, new Comparator<Word>() {
                            @Override
                            public int compare(Word o1, Word o2) {
                                return o2.getEng().compareTo(o1.getEng());
                            }
                        });
                    }
                    wrong.update(wrongView);
                }
            }
        });
        menu.add(itemHome);
        menu.add(itemAsc);
        menu.add(itemDesc);
        bar.add(menu);
        return bar;
    }


    //홈 화면 버튼 스타일
    private void buttonStyle(JButton btn) {
        btn.setFont(new Font("맑은 고딕", Font.BOLD, 18));
        btn.setBackground(Color.DARK_GRAY); //버튼 배경색
        btn.setForeground(Color.WHITE); //글자색
        btn.setFocusPainted(false); //포커스 테두리 없애기
        btn.setPreferredSize(new Dimension(350, 40)); //기본 크기
        btn.setMaximumSize(new Dimension(350, 40)); // 최대 크기
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
    }
    //캐릭터 버튼 스타일
    private void CharacterbuttonStyle(JButton btn) {
        btn.setFont(new Font("맑은 고딕", Font.BOLD, 18));
        btn.setBackground(Color.DARK_GRAY); //버튼 배경색
        btn.setForeground(Color.WHITE); //글자색
        btn.setFocusPainted(false); //포커스 테두리 없애기
        btn.setPreferredSize(new Dimension(150, 40)); //기본 크기
        btn.setMaximumSize(new Dimension(150, 40)); // 최대 크기
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
    }


    // 첫 화면 패널
    private class HomePanel extends JPanel{
        private Image img;

        HomePanel(){
            try{
                img = new ImageIcon(WordSwing.class.getResource("img.png")).getImage();
            }catch (Exception e){
                System.out.println("배경 불러오기 실패");
            }

            setLayout(new BorderLayout());

            JPanel center = new JPanel();
            center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
            center.setBorder(new EmptyBorder(100, 200, 0, 100));
            center.setOpaque(false); //투명하게 해서 배경 보이도록

            JLabel title = new JLabel("단어장");
            title.setFont(new Font("맑은 고딕", Font.BOLD,52));
            title.setForeground(Color.WHITE);
            title.setAlignmentX(Component.CENTER_ALIGNMENT); //중앙 정렬

            JPanel right = new JPanel();
            right.setLayout(new BorderLayout());
            right.setBorder(new EmptyBorder(80, 0, 80, 100));
            right.setOpaque(false);

            JPanel rightBox = new JPanel();
            rightBox.setOpaque(false);
            rightBox.setLayout(new BoxLayout(rightBox, BoxLayout.Y_AXIS));



            // 첫 화면의 다섯 개의 버튼
            JButton managerBtn = new JButton("단어관리자");
            JButton subBtn = new JButton("주관식 게임");
            JButton objBtn = new JButton("객관식 게임");
            JButton wrongBtn = new JButton("오답&빈출");
            JButton wrongGameBtn = new JButton("오답 게임");
            JButton CharacterBtn = new JButton("나의 캐릭터");

            //버튼에 스타일 적용
            buttonStyle(managerBtn);
            buttonStyle(subBtn);
            buttonStyle(objBtn);
            buttonStyle(wrongBtn);
            buttonStyle(wrongGameBtn);
            CharacterbuttonStyle(CharacterBtn);

            //캐릭터 버튼 아래 이미지 아이콘 추가
            rightBox.add(CharacterBtn);
            rightBox.add(Box.createVerticalStrut(10));

            ImageIcon charIcon = new ImageIcon(Objects.requireNonNull(WordSwing.class.getResource("steve.png")));
            JLabel charLabel = new JLabel(charIcon);
            charLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
            rightBox.add(charLabel);


            //단어관리자 버튼 클릭 시 단어 관리 화면으로
            managerBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    cl.show(cp,"WORD");
                    updateMenuByPanel("WORD");
                }
            });
            // 주관식 게임 버튼
            subBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    cl.show(cp,"SUB");
                    updateMenuByPanel("SUB");
                }
            });
            // 객관식 게임
            objBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    cl.show(cp, "OBJ");
                    updateMenuByPanel("OBJ");
                }
            });
            //오답 빈출 화면으로
            wrongBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    cl.show(cp,"WRONG");
                    updateMenuByPanel("WRONG");
                }
            });
            wrongGameBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    cl.show(cp,"WRONGGAME");
                    updateMenuByPanel("WRONGGAME");
                }
            });
            //캐릭터 화면으로 이동
            CharacterBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                   cl.show(cp,"CHARACTER");
                   updateMenuByPanel("CHARACTER");
                }
            });

            center.add(title);
            center.add(Box.createVerticalStrut(30));
            center.add(managerBtn);
            center.add(subBtn);
            center.add(objBtn);
            center.add(wrongBtn);
            center.add(wrongGameBtn);
            right.add(rightBox,BorderLayout.EAST);

            add(center,BorderLayout.CENTER);
            add(right, BorderLayout.EAST);
        }
        //배경 이미지
        @Override
        protected void paintComponent(Graphics g){
            super.paintComponent(g);
            if(img != null){
                // 패널 크기에 맞게 이미지 채우기
                g.drawImage(img,0,0,getWidth(),getHeight(),this);
            }
        }
    }
    //단어 관리 패널
    private class WordPanel extends JPanel{
        private final JTextField tf = new JTextField(15);
        private final DefaultTableModel model;
        private final JTable table;

        WordPanel(){
            setLayout(new BorderLayout());
            JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JLabel title = new JLabel("단어 관리자");
            JButton openBtn = new JButton("파일 열기");
            JButton addBtn = new JButton("추가");
            JButton editBtn = new JButton("수정");
            JButton deleteBtn = new JButton("삭제");
            JButton searchBtn = new JButton("검색");
            JButton commonBtn = new JButton("빈출 추가");
            JButton showBtn = new JButton("전체 보기");

            top.add(title);
            top.add(new JLabel("검색: "));
            top.add(tf);
            top.add(openBtn);
            top.add(addBtn);
            top.add(editBtn);
            top.add(deleteBtn);
            top.add(searchBtn);
            top.add(commonBtn);
            top.add(showBtn);

            add(top, BorderLayout.NORTH);
            // 테이블 모델 생성
            model = new DefaultTableModel(header, 0);
            table = new JTable(model);
            add(new JScrollPane(table), BorderLayout.CENTER);
            // 파일 열기 버튼 클릭 시 단어 파일 선택
            openBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    JFileChooser chooser = new JFileChooser();
                    int result = chooser.showOpenDialog(null);
                    if(result == JFileChooser.APPROVE_OPTION){
                        wordFile = chooser.getSelectedFile();
                        filename = wordFile.getPath();
                        loadFile();
                        JOptionPane.showMessageDialog(null, "단어 파일 불러오기 완료");
                    }
                }
            });


            //추가 버튼
            addBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String eng = tf.getText().trim();
                    if(eng.isEmpty()){
                        JOptionPane.showMessageDialog(null, "영단어를 입력하세요.");
                        return;
                    }
                    for(Word w : wordlist){
                        if(w.getEng().equals(eng)){
                            JOptionPane.showMessageDialog(null, "이미 존재하는 단어입니다.");
                            tf.setText("");
                            return;
                        }
                    }
                    String kor = JOptionPane.showInputDialog("뜻 입력:");
                    if(kor==null)
                        return;
                    wordlist.add(0,new Word(eng, kor));
                    saved();
                    JOptionPane.showMessageDialog(null, "추가 완료");
                    tf.setText("");
                    update(wordlist);
                }
            });
            //수정 버튼
            editBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String eng = tf.getText().trim();
                    for(Word w : wordlist){
                        if(w.getEng().equals(eng)) {
                            String kor = JOptionPane.showInputDialog("새 뜻 입력:", w.getKor());
                            if(kor == null)
                                return;
                            w.setKor(kor);
                            saved();
                            JOptionPane.showMessageDialog(null, "수정 완료");
                            update(wordlist);
                            tf.setText("");
                            return;
                        }
                    }
                    JOptionPane.showMessageDialog(null,"단어가 없습니다.");
                    tf.setText("");
                }
            });
            //삭제 버튼
            deleteBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String eng = tf.getText().trim();
                    Word target = null;
                    for(Word w : wordlist){
                        if(w.getEng().equals(eng)){
                            target = w;
                            break;
                        }
                    }
                    if(target!=null){
                        wordlist.remove(target);
                        wronglist.remove(target);
                        commonlist.remove(target);
                        JOptionPane.showMessageDialog(null,"삭제 완료");
                    }else {
                        JOptionPane.showMessageDialog(null, "단어가 없습니다.");
                    }
                    saved();
                    update(wordlist);
                    tf.setText("");
                }
            });
            //검색 버튼
            searchBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String eng = tf.getText().trim();
                    List<Word> found = new ArrayList<>();

                    for(Word w : wordlist){
                        if(w.getEng().startsWith(eng))
                            found.add(w);
                    }
                    update(found);
                    tf.setText("");
                }
            });
            //빈출 추가 버튼
            commonBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String eng = tf.getText().trim();
                    Word target = null;
                    for (Word w : wordlist) {
                        if (w.getEng().equals(eng)) {
                            target = w;
                            break;
                        }
                    }
                    int r = JOptionPane.showConfirmDialog(null, target.getEng() + "를 빈출에 추가하시겠습니까?", "확인", JOptionPane.YES_NO_OPTION);
                    if(r == JOptionPane.YES_OPTION){
                        commonlist.add(target);
                        JOptionPane.showMessageDialog(null, "빈출 추가 완료");
                        tf.setText("");
                    }
                }
            });
            //전체 보기 버튼
            showBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    update(wordlist);
                    tf.setText("");
                }
            });
        }
        // 갱신된 단어 리스트를 테이블에 반영
        void update(List<Word> list){
            model.setRowCount(0); //기존 행 삭제
            for(Word w : list){
                model.addRow(new String[]{w.getEng(), w.getKor()});
            }
        }
    }
    //주관식 게임 패널
    private class SubGamePanel extends JPanel{

    }
    //객관식 게임 패널
    private class ObjGamePanel extends JPanel{

    }
    //오답 & 빈출 관리 패널
    private class WrongPanel extends JPanel{
        private final JTable table;
        private final DefaultTableModel model;
        WrongPanel(){
            setLayout(new BorderLayout());

            JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
            JLabel title = new JLabel("오답 & 빈출 관리");
            JButton fileBtn = new JButton("오답 파일 열기");
            JButton wrongBtn = new JButton("오답 보기");
            JButton intersectionBtn = new JButton("빈출&오답 보기");
            top.add(title);
            top.add(fileBtn);
            top.add(wrongBtn);
            top.add(intersectionBtn);
            add(top, BorderLayout.NORTH);

            model = new DefaultTableModel(header, 0);
            table = new JTable(model);
            add(new JScrollPane(table), BorderLayout.CENTER);
            //오답 파일 불러오기 버튼
            fileBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    JFileChooser chooser = new JFileChooser();
                    FileNameExtensionFilter filter = new FileNameExtensionFilter("txt File","txt");
                    chooser.setFileFilter(filter);
                    int result = chooser.showOpenDialog(null);
                    if(result == JFileChooser.APPROVE_OPTION){
                        String path = chooser.getSelectedFile().getPath();
                        wrongNotes.getSet().clear();
                        wronglist.clear();

                        wrongNotes.loadFromFile(path);
                        wronglist.addAll(wrongNotes.getSet());

                        if(wronglist.isEmpty()){
                            JOptionPane.showMessageDialog(null,"오답 단어가 없습니다.");
                        }

                        update(wronglist);
                        wrongView.clear();
                        wrongView.addAll(wronglist);
                        int totalWrong = wrongNotes.getSet().size();
                        character.setWrongCount(totalWrong);
                        //wrongGame에서 사용할 오답 단어및 오답 개수 갱신
                        wrongGame.setWrongcounts(totalWrong);
                        wrongGame.refreshFromWrongNotes();
                        cp.add(character,"CHARACTER");
                    }
                }
            });
            // 오답 보기 버튼
            wrongBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if(wronglist.isEmpty()){
                        JOptionPane.showMessageDialog(null, "오답 단어가 없습니다.");
                        return;
                    }
                    update(wronglist);
                    //현재 화면 리스트를 wrongView에 동기화
                    wrongView.clear();
                    wrongView.addAll(wronglist);
                }
            });
            //오답&빈출 보기 버튼
            intersectionBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if(wronglist.isEmpty()){
                        JOptionPane.showMessageDialog(null, "오답 단어가 없습니다.");
                        return;
                    }
                    if(commonlist.isEmpty()){
                        JOptionPane.showMessageDialog(null, "빈출 단어가 없습니다.");
                        return;
                    }

                    List<Word> l = new ArrayList<>();
                    for(Word w : wronglist){
                        if(commonlist.contains(w))
                            l.add(w);
                    }
                    if(l.isEmpty()){
                        JOptionPane.showMessageDialog(null, "빈출과 오답이 겹치는 단어가 없습니다.");
                    }else{
                        wrongView.clear();
                        wrongView.addAll(l);
                        update(wrongView);
                    }

                }
            });
        }
        private void update(List<Word> list){
            model.setRowCount(0);
            for(Word w : list){
                model.addRow(new String[]{w.getEng(), w.getKor()});
            }
        }

    }


    public static void main(String[] args) {
        new WordSwing();
    }
}
