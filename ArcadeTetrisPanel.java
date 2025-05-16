import javax.swing.*;

public class ArcadeTetrisPanel extends AbstractTetrisPanel {
    public ArcadeTetrisPanel(JFrame frame) {
        super(frame);
    }

    @Override
    protected void initializeGameMode(JFrame frame) {
        // 街機模式無特定初始化，僅使用基類標準俄羅斯方塊邏輯
        // 未來可添加模式特定設置（例如，自定義計分或速度）
    }
}