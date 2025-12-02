package Final;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WrongGame extends JPanel {
    private WrongNotes wrongNotes;
    private CharacterPanel character;
    private ArrayList<Word> wrongList;
    private int currentIndex = 0;
    private int wrongcounts;


    private JLabel count;
    private JButton wrongListBtn;
    private JLabel questionLabel;
    private JTextField answerField;
    private JLabel resultLabel;
    private JButton submitBtn;

    private boolean engToKorMode = true;


    public WrongGame(WrongNotes wrongNotes, CharacterPanel characterPanel) {
        this.wrongNotes = wrongNotes;
        this.character = characterPanel;

        initLayout();

        wrongList = new ArrayList<>(wrongNotes.getSet());
        Collections.shuffle(wrongList);

        this.wrongcounts = wrongList.size();
        setWrongcounts(this.wrongcounts);


        loadNextQuestion();
    }


    private void initLayout() {
        setLayout(new BorderLayout());

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        count = new JLabel("현재 오답이 존재하지 않습니다",SwingConstants.CENTER);
        wrongListBtn = new JButton("오답 단어 목록 보기");


        wrongListBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                wrongList = new ArrayList<>(wrongNotes.getSet());

                if (wrongList.isEmpty()) {
                    JOptionPane.showMessageDialog(WrongGame.this,
                            "오답 단어가 없습니다.");
                    return;
                }

                String[] columnNames = {"영어", "한글"};
                String[][] rowData = new String[wrongList.size()][2];

                for(int i =0; i<wrongList.size();i++) {
                    rowData[i][0] = wrongList.get(i).getEng();
                    rowData[i][1] = wrongList.get(i).getKor();
                }

                JTable table = new JTable(rowData, columnNames);
                table.setEnabled(false);

                JScrollPane scrollPane = new JScrollPane(table);
                scrollPane.setPreferredSize(new Dimension(300, 200));

                JOptionPane.showMessageDialog(
                        WrongGame.this,
                        scrollPane,
                        "오답 단어 목록",
                        JOptionPane.PLAIN_MESSAGE
                );


            }
        });

        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));

        questionLabel = new JLabel("오답 게임을 시작합니다.");
        questionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        answerField = new JTextField(15);
        answerField.setMaximumSize(answerField.getPreferredSize());
        answerField.setAlignmentX(Component.CENTER_ALIGNMENT);


        submitBtn = new JButton("정답 제출");
        submitBtn.setAlignmentX(Component.CENTER_ALIGNMENT);


        resultLabel = new JLabel(" ");
        resultLabel.setAlignmentX(Component.CENTER_ALIGNMENT);


        top.add(Box.createHorizontalStrut(5));
        top.add(count);
        top.add(Box.createHorizontalStrut(10));
        top.add(wrongListBtn);
        add(top, BorderLayout.NORTH);

        center.add(Box.createVerticalStrut(30));
        center.add(questionLabel);
        center.add(Box.createVerticalStrut(10));
        center.add(answerField);
        center.add(Box.createVerticalStrut(10));
        center.add(submitBtn);
        center.add(Box.createVerticalStrut(10));
        center.add(resultLabel);
        add(center, BorderLayout.CENTER);

        answerField.addActionListener(e -> checkAnswer());
        submitBtn.addActionListener(e -> checkAnswer());

    }



    public void setWrongcounts(int wrongcounts) {
        this.wrongcounts = wrongcounts;
        if(wrongcounts <1) {
            count.setText("현재 오답이 존재하지 않습니다.");
        }else {
            count.setText("현재 오답 개수 : " + wrongcounts);
        }
    }


    private void loadNextQuestion() {
        if (wrongList == null || wrongList.isEmpty()) {
            questionLabel.setText("오답 단어가 없습니다. 파일을 먼저 불러와 주세요.");
            submitBtn.setEnabled(false);
            answerField.setEnabled(false);
            return;
        }

        submitBtn.setEnabled(true);
        answerField.setEnabled(true);

        if (currentIndex >= wrongList.size()) {
            currentIndex = 0;
        }

        // 새 단어를 시작할 때는 항상 영어→한글부터
        engToKorMode = true;

        loadQuestionForCurrentWord();


    }


    private void checkAnswer(){
        if (wrongList == null || wrongList.isEmpty()) {
            return;
        }

        Word w = wrongList.get(currentIndex);
        String input = answerField.getText().trim();


        //빈 입력은 채점하지 않고 다시 입력 유도
        if (input.isEmpty()) {
            resultLabel.setText("정답을 입력해 주세요.");
            return;
        }

        String correct = engToKorMode ? w.getKor() : w.getEng();


        boolean isCorrect = engToKorMode
                ? input.equals(correct)
                : input.equalsIgnoreCase(correct);

        if(isCorrect) {
            w.increaseCorrectCount();

            if(w.getCorrectCount() == 1) {
                resultLabel.setText("정답입니다! 이제 반대로도 맞춰보세요.");

                engToKorMode = !engToKorMode;
                loadQuestionForCurrentWord();
                return;
            }else{
                resultLabel.setText("양쪽 모두 정답! 오답 목록에서 제거됩니다.");
                removeWord(w);

                //다음 단어 준비 , 단어가 제거되었으므로 index 0부터
                if(currentIndex >= wrongList.size()) {
                    currentIndex = 0;
                }
                engToKorMode = true;
                loadNextQuestion();
                return;
            }
        }else{
            resultLabel.setText("틀렸습니다. 정답은 ["+correct+"] 입니다.");
            w.resetCorrectCount();

            currentIndex++;
            if(currentIndex >= wrongList.size()) {
                currentIndex = 0;
            }
            engToKorMode = true;
            loadNextQuestion();
        }

    }

    private void loadQuestionForCurrentWord() {
        if(wrongList == null || wrongList.isEmpty()) {
            questionLabel.setText("오답 단어가 없습니다.");
            submitBtn.setEnabled(false);
            answerField.setEnabled(false);
            return;
        }

        if(currentIndex >= wrongList.size()) {
            currentIndex = 0;
        }

        Word w = wrongList.get(currentIndex);

        if (engToKorMode) {
            questionLabel.setText("뜻을 맞춰 보세요 : " + w.getEng());
        } else {
            questionLabel.setText("영어 단어를 맞춰 보세요 : " + w.getKor());
        }

        answerField.setText("");
        answerField.requestFocusInWindow();


    }

    private void removeWord(Word w) {
        wrongList.remove(w);
        wrongNotes.getSet().remove(w);

        int newCount = wrongNotes.getSet().size();
        setWrongcounts(newCount);

        character.removeWrong();
    }

    public void refreshFromWrongNotes() {
        wrongList = new ArrayList<>(wrongNotes.getSet());
        Collections.shuffle(wrongList);
        currentIndex = 0;
        loadNextQuestion();
    }
}
