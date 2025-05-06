public class Player{
	private String myName = "";
	private int myTurn; 
	
	public Player(String name,  int turn) {
		myName = name;
		myTurn = turn;
	}
	
	public void setName(String name) {
		myName = name;
	}
	public void setTurn(int turn) {
		myTurn = turn;
	}
	
	public static int negotiation(Player player1, Player player2) {
        Scanner scanner = new Scanner(System.in);

        System.out.println(player1.getName() + "（先手）が " + player1.getDesiredTimeLimit() + "秒 を提案しました。");

        // 後手の判断
        System.out.print(player2.getName() + "（後手）、この提案を受け入れますか？ (yes/no): ");
        String response = scanner.nextLine().trim().toLowerCase();

        if (response.equals("yes")) {
            System.out.println("提案を受け入れました。対局開始！");
            return player1.getDesiredTimeLimit();
        } else {
            System.out.print(player2.getName() + "（後手）、希望する時間（秒）を入力してください: ");
            int p2Time = Integer.parseInt(scanner.nextLine());
            player2.setDesiredTimeLimit(p2Time);

            System.out.println(player2.getName() + " が " + p2Time + "秒 を提案しました。");

            // 先手が後手の提案を承諾するか
            System.out.print(player1.getName() + "（先手）、この提案を受け入れますか？ (yes/no): ");
            String p1Response = scanner.nextLine().trim().toLowerCase();

            if (p1Response.equals("yes")) {
                System.out.println("提案を受け入れました。対局開始！");
                return p2Time;
            } else {
                // ランダム選択
                int[] options = {player1.getDesiredTimeLimit(), p2Time};
                int selected = options[new Random().nextInt(2)];
                System.out.println("両者の提案が一致しないため、ランダムで " + selected + "秒 に決定しました。対局開始！");
                return selected;
            }
        }
    }

}
