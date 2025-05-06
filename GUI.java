import javax.swing.*;
import java.awt.*;

public class GUI extends JFrame {
    private JLabel playerColorLabel; // プレイヤーの石の色を表示するラベル
    private JLabel turnLabel; // 現在のターンを表示するラベル
    private JLabel timerLabel; // 残り時間を表示するラベル
    private JButton passButton; // パスボタン
    private JTextArea messageArea; // メッセージ表示用テキストエリア

    private static final int BOARD_SIZE = 8; // ボードサイズ

    public GUI(Client client) {
        // フレームの設定
        setTitle("ネットワーク対戦型オセロゲーム");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setSize(800, 600);

        // メインパネル（ゲームボード + 情報パネル）
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        // ボードパネル（Clientが生成したボードを取得して配置）
        JPanel boardPanel = client.getBoardPanel();
        mainPanel.add(boardPanel, BorderLayout.CENTER);

        // 情報パネル
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new GridLayout(5, 1)); // 情報パネルのラベルやボタンを縦に配置

        playerColorLabel = new JLabel("あなたの石の色: 黒", SwingConstants.CENTER);
        turnLabel = new JLabel("あなたの番です", SwingConstants.CENTER);
        timerLabel = new JLabel("残り時間: 60秒", SwingConstants.CENTER);
        passButton = new JButton("パス");
        messageArea = new JTextArea("メッセージ欄");
        messageArea.setEditable(false); // ユーザーが編集できないようにする

        infoPanel.add(playerColorLabel);
        infoPanel.add(turnLabel);
        infoPanel.add(timerLabel);
        infoPanel.add(passButton);
        infoPanel.add(new JScrollPane(messageArea)); // メッセージ欄をスクロール可能にする

        mainPanel.add(infoPanel, BorderLayout.EAST);

        // メインパネルをフレームに追加
        add(mainPanel, BorderLayout.CENTER);

        // フレームを表示
        setVisible(true);
    }

    public static void main(String[] args) {
        // Othelloゲームロジックの初期化
        Othello game = new Othello();

        // GUIクライアントの初期化
        SwingUtilities.invokeLater(() -> {
            Client client = new Client(game);
            new GUI(client); // GUIにClientを渡して初期化
        });
    }
}
