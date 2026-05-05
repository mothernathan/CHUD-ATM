package com.atmbanksimulator;

import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

//by nathan
public class soundManager {
    private static final AudioClip CLICK = load("sounds/click.wav");
    private static final AudioClip ERROR = load("sounds/error.wav");
    private static final AudioClip SUCCESS = load("sounds/success.wav");
    private static final AudioClip INTRO = load("sounds/intro.mp3");

    private static MediaPlayer menuMusic;
    private static boolean muted = false;

    private static AudioClip load(String path) {
        return new AudioClip(soundManager.class.getResource(path).toExternalForm());
    }

    public static void playClick()   { CLICK.play(); }
    public static void playError()   { ERROR.play(); }
    public static void playSuccess() { SUCCESS.play(); }

    public static void playMenuMusic() {
        var url = soundManager.class.getResource("sounds/menu.wav");
        if (url == null) { System.err.println("menu.wav not found"); return; }

        menuMusic = new MediaPlayer(new Media(url.toExternalForm()));
        menuMusic.setCycleCount(MediaPlayer.INDEFINITE); // loops forever
        menuMusic.setVolume(0.2); // 50% volume so it's not too loud
        menuMusic.play();
    }

    public static void stopMenuMusic() {
        if (menuMusic != null) menuMusic.stop();
    }

    public static void toggleMute() {
        muted = !muted;
        if (menuMusic != null) menuMusic.setMute(muted);
    }

    //get method to communicate if music is muted or not  to other methods
    public static boolean isMuted()
    {
        return muted;
    }

    public static void playIntro() {
        INTRO.play();
    }
}
