//パッケージのインポート
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.*;
import java.io.*;

public class Client extends JFrame implements MouseListener {
	private JButton buttonArray[];//オセロ盤用のボタン配列
	private JButton stop, pass; //停止、スキップ用ボタン
	private JLabel colorLabel; // 色表示用ラベル
	private JLabel turnLabel; // 手番表示用ラベル
	private Container c; // コンテナ
	private ImageIcon blackIcon, whiteIcon, boardIcon; //アイコン
    private PrintWriter out;//データ送信用オブジェクト
	private Receiver receiver; //データ受信用オブジェクト
	private Othello game;
	/*private Player player;*/

	private static final int BLACK = 0;
    private static final int WHITE = 1;
    private static final int EMPTY = -1;
	private final String SEPARATOR = " ";//区切り文字
	private final int row = 8;

    public Client(Othello game/*,Player player */){
		this.game = game;
		//this.player = player;
		int [] grids = game.get_board(); //getGridメソッドにより局面情報を取得
		//ウィンドウ設定
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//ウィンドウを閉じる場合の処理
		setTitle("ネットワーク対戦型オセロゲーム");//ウィンドウのタイトル
		setSize(row * 45 + 10, row * 45 + 200);//ウィンドウのサイズを設定
		c.setLayout(null);//
		//オセロ盤の生成
		buttonArray = new JButton[row * row];//ボタンの配列を作成
		for(int i = 0 ; i < row * row ; i++){
			if(grids[i]==BLACK){ buttonArray[i] = new JButton(blackIcon);}//盤面状態に応じたアイコンを設定
			if(grids[i]==WHITE){ buttonArray[i] = new JButton(whiteIcon);}//盤面状態に応じたアイコンを設定
			if(grids[i]==EMPTY){ buttonArray[i] = new JButton(boardIcon);}//盤面状態に応じたアイコンを設定
			c.add(buttonArray[i]);//ボタンの配列をペインに貼り付け
			// ボタンを配置する
			int x = (i % row) * 45;
			int y = (int) (i / row) * 45;
			buttonArray[i].setBounds(x, y, 45, 45);//ボタンの大きさと位置を設定する．
			buttonArray[i].addMouseListener(this);//マウス操作を認識できるようにする
			buttonArray[i].setActionCommand(Integer.toString(i));//ボタンを識別するための名前(番号)を付加する
		}
		//終了ボタン
		stop = new JButton("終了");//終了ボタンを作成
		c.add(stop); //終了ボタンをペインに貼り付け
		stop.setBounds(0, row * 45 + 30, (row * 45 + 10) / 2, 30);//終了ボタンの境界を設定
		stop.addMouseListener(this);//マウス操作を認識できるようにする
		stop.setActionCommand("stop");//ボタンを識別するための名前を付加する
		//パスボタン
		pass = new JButton("パス");//パスボタンを作成
		c.add(pass); //パスボタンをペインに貼り付け
		pass.setBounds((row * 45 + 10) / 2, row * 45 + 30, (row * 45 + 10 ) / 2, 30);//パスボタンの境界を設定
		pass.addMouseListener(this);//マウス操作を認識できるようにする
		pass.setActionCommand("pass");//ボタンを識別するための名前を付加する
		//色表示用ラベル
		String myName = "Kari"/*player.getName()*/;
		colorLabel = new JLabel(myName + "さんの色は未定です");//色情報を表示するためのラベルを作成
		colorLabel.setBounds(10, row * 45 + 60 , row * 45 + 10, 30);//境界を設定
		c.add(colorLabel);//色表示用ラベルをペインに貼り付け
		//手番表示用ラベル
		turnLabel = new JLabel("手番は未定です");//手番情報を表示するためのラベルを作成
		turnLabel.setBounds(10, row * 45 + 120, row * 45 + 10, 30);//境界を設定
		c.add(turnLabel);//手番情報ラベルをペインに貼り付け

    }

    // メソッド

	/*データ送受信系メソッド */

	public void connectServer(String ipAddress, int port){	// サーバに接続
		Socket socket = null;
		try {
			socket = new Socket(ipAddress, port); //サーバ(ipAddress, port)に接続
			out = new PrintWriter(socket.getOutputStream(), true); //データ送信用オブジェクトの用意
			receiver = new Receiver(socket); //受信用オブジェクトの準備
			receiver.start();//受信用オブジェクト(スレッド)起動
		} catch (UnknownHostException e) {
			System.err.println("ホストのIPアドレスが判定できません: " + e);
			System.exit(-1);
		} catch (IOException e) {
			System.err.println("サーバ接続時にエラーが発生しました: " + e);
			System.exit(-1);
		}
	}

    public void sendMessage(String msg){	// サーバに操作情報を送信
		out.println(msg);//送信データをバッファに書き出す
		out.flush();//送信データを送る
		System.out.println("サーバにメッセージ " + msg + " を送信しました"); //テスト標準出力
	}

    // データ受信用スレッド(内部クラス)
	class Receiver extends Thread {
		private InputStreamReader sisr; //受信データ用文字ストリーム
		private BufferedReader br; //文字ストリーム用のバッファ

		// 内部クラスReceiverのコンストラクタ
		Receiver (Socket socket){
			try{
				sisr = new InputStreamReader(socket.getInputStream()); //受信したバイトデータを文字ストリームに
				br = new BufferedReader(sisr);//文字ストリームをバッファリングする
			} catch (IOException e) {
				System.err.println("データ受信時にエラーが発生しました: " + e);
			}
		}
		// 内部クラス Receiverのメソッド
		public void run(){
			try{
				while(true) {//データを受信し続ける
					String inputLine = br.readLine();//受信データを一行分読み込む
					if (inputLine != null){//データを受信したら
						receiveMessage(inputLine);//データ受信用メソッドを呼び出す
					}
				}
			} catch (IOException e){
				System.err.println("データ受信時にエラーが発生しました: " + e);
			}
		}
	}

    public void receiveMessage(String msg){	// メッセージの受信
		System.out.println("サーバからメッセージ " + msg + " を受信しました"); //テスト用標準出力
		readMessage(msg);
	}

	public void readMessage(String msg){
		//自分宛てのメッセージか確認
		switch(/*msg.charAt(0) == player.myNumber()*/true){
			case true:
				//メッセージの種類を確認
				int beginIndex = msg.indexOf(SEPARATOR)+1;//"1 →place ~~"
				msg = msg.substring(beginIndex);
				int endIndex = msg.indexOf(SEPARATOR);//"1 place← ~~"

				String prefix = msg.substring(beginIndex,endIndex);
				String content = msg.substring(endIndex + 1);//"1 place →~~"

				switch(prefix){
					case "place":
						int place = 0;//初期化
						try{
							place = Integer.parseInt(content);
						}
						catch(NumberFormatException e){
							System.err.println("整数変換時にエラーが発生しました: " + e);
							//sendMessage("error");
							break;
						}
						game.place(place);
						break;
					case "b":
						break;
					case "c":
						break;
					default :

				}
				break;

			case false:
				//error

		}

	}

	public void acceptOperation(String command){	// プレイヤの操作を受付
	}

	public void negotiateTimer(int time){ //タイマー折衝
	}



	/*ここから描画系メソッド */

	public void updateDisp(){	// 画面を更新する
	}

	//マウスクリック時の処理
	public void mouseClicked(MouseEvent e) {
		JButton theButton = (JButton)e.getComponent();//クリックしたオブジェクトを得る．キャストを忘れずに
		String command = theButton.getActionCommand();//ボタンの名前を取り出す
		System.out.println("マウスがクリックされました。押されたボタンは " + command + "です。");//テスト用に標準出力
		
		
		
		sendMessage(command); //テスト用にメッセージを送信
	}
	public void mouseEntered(MouseEvent e) {}//マウスがオブジェクトに入ったときの処理
	public void mouseExited(MouseEvent e) {}//マウスがオブジェクトから出たときの処理
	public void mousePressed(MouseEvent e) {}//マウスでオブジェクトを押したときの処理
	public void mouseReleased(MouseEvent e) {}//マウスで押していたオブジェクトを離したときの処理

    public static void main(String args[]){

    }



}
