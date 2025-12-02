package Final;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class CharacterPanel extends JPanel {
    // 상태값
    private final int ABSOLUTE_MAX_HP = 100;  // 이건 고정 상한선 개념
    private final int ABSOLUTE_MIN_HP = 10;
    private int effectiveMaxHp = ABSOLUTE_MAX_HP;  // 오답에 따라 줄어드는 최대 체력
    private int currentHp = ABSOLUTE_MAX_HP;       // 실제 현재 체력
    private int wrongCount = 0;                    // 현재 오답 개수


    private JLabel charImageLabel;  // 캐릭터 스킨 이미지
    private JProgressBar hpBar;     // 체력바
    private JLabel hpInfoLabel;
    private JLabel maxHpInfoLabel;
    private JLabel wrongInfoLabel;

    public CharacterPanel() {
        initLayout();
        updateUIState();

    }

    private void initLayout() {
        setLayout(new BorderLayout());
        setOpaque(false);

        ImageIcon icon = new ImageIcon(
                Objects.requireNonNull(
                        CharacterPanel.class.getResource("steve.png")
                )
        );
        charImageLabel = new JLabel(icon);
        charImageLabel.setHorizontalAlignment(SwingConstants.CENTER);

        //체력바
        hpBar = new JProgressBar(0, ABSOLUTE_MAX_HP);
        hpBar.setStringPainted(true);

        hpInfoLabel = new JLabel("", SwingConstants.CENTER);
        maxHpInfoLabel = new JLabel("", SwingConstants.CENTER);
        wrongInfoLabel = new JLabel("", SwingConstants.CENTER);

        JPanel bottom = new JPanel();
        bottom.add(charImageLabel);
        bottom.setLayout(new BoxLayout(bottom, BoxLayout.Y_AXIS));
        bottom.add(hpInfoLabel);
        bottom.add(Box.createVerticalStrut(5));
        bottom.add(hpBar);
        bottom.add(Box.createVerticalStrut(5));
        bottom.add(maxHpInfoLabel);
        bottom.add(Box.createVerticalStrut(5));
        bottom.add(wrongInfoLabel);

        add(bottom, BorderLayout.SOUTH);
    }

    public void addWrong() {
        wrongCount++;
        recalcEffectiveMaxHp();
        // 현재 체력은 웬만하면 유지하되,
        // "최대 체력보다 많아지면" 그때만 잘라준다
        if (currentHp > effectiveMaxHp) {
            currentHp = effectiveMaxHp;
        }
        updateUIState();
    }

    public void removeWrong() {
        if (wrongCount > 0) {
            wrongCount--;
            recalcEffectiveMaxHp();
            // currentHp는 그대로 둔다!
            // (최대 체력 상한선만 올라가는 느낌)
            updateUIState();
        }
    }

    public void setWrongCount(int wrongCount) {
        this.wrongCount = Math.max(0, wrongCount);
        recalcEffectiveMaxHp();
        if (currentHp > effectiveMaxHp) {
            currentHp = effectiveMaxHp;
        }
        updateUIState();
    }

    private void recalcEffectiveMaxHp() {
        //오답 1개당 최대체력 3씩 감소
        int penalty = wrongCount * 3;
        effectiveMaxHp = ABSOLUTE_MAX_HP - penalty;
        if (effectiveMaxHp <= 10) effectiveMaxHp = ABSOLUTE_MIN_HP;
    }

    private void updateUIState() {
        hpBar.setValue(currentHp);

        hpInfoLabel.setText(
                "현재 체력 : " + currentHp + " / " + ABSOLUTE_MAX_HP
        );
        maxHpInfoLabel.setText(
                "현재 최대 체력 : " + effectiveMaxHp + " / " + ABSOLUTE_MAX_HP
        );
        wrongInfoLabel.setText(
                "현재 오답 개수 : " + wrongCount
        );
    }


}
