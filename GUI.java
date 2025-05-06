import javax.swing.*;

public class GUI {
    public static void main(String[] args) {
        // Othelloゲームロジックの初期化
        Othello game = new Othello();

        // GUIクライアントの初期化
        SwingUtilities.invokeLater(() -> {
            Client client = new Client(game);
            client.setVisible(true);
        });
    }
}
