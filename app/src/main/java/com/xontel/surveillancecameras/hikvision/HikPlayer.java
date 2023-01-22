package com.xontel.surveillancecameras.hikvision;

import org.MediaPlayer.PlayM4.Player;

public class HikPlayer {
    public static final int DEFAULT_HIKVISION_PORT_NUMBER = 8000;
    private final Player playerInstance = Player.getInstance();
    private Player.MPInteger stWidth;
    private Player.MPInteger stHeight;
    private Player.MPInteger stSize;


    public Player getPlayerInstance() {
        return playerInstance;
    }



}
