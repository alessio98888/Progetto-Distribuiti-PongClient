
public class PlayerInfo {

	private String playerId;

	public PlayerInfo() {
		this.playerId = Long.toString(System.currentTimeMillis());
	}

	public String getPlayerId() {
		return playerId;
	}

	public void setPlayerId(String playerId) {
		this.playerId = playerId;
	}

	

}
