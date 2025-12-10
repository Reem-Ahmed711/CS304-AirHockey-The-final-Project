package airhockey.audio;

import javax.sound.sampled.*;
import java.io.*;
import java.net.URL;

public class AudioManager {
    private Clip hitSound;
    private Clip goalSound;
    private Clip backgroundMusic;
    private Clip powerUpSound;

    private boolean soundEnabled = true;
    private boolean musicEnabled = true;
    private float volume = 0.8f;

    public AudioManager() {
        loadSounds();
    }

    private void loadSounds() {
        try {
            // Hit sound (beep)
            hitSound = createBeepSound(800, 50, 0.7f);

            // Goal sound (higher beep)
            goalSound = createBeepSound(1200, 150, 0.8f);

            // Power-up sound
            powerUpSound = createBeepSound(600, 100, 0.6f);

            // Try to load actual sound files if available
            tryLoadExternalSounds();

        } catch (Exception e) {
            System.out.println("Audio initialization: " + e.getMessage());
        }
    }

    private void tryLoadExternalSounds() {
        String[] soundFiles = {
                "sounds/Audio/Air Hockey HD (mp3cut.net).wav",
                "sounds/Audio/Funny_Background_Music_For_Videos_-_Instrumental_F.wav",
                "sounds/Audio/zapsplat_magic_wand_zap_spell_005_12559.wav"
        };

        for (String filePath : soundFiles) {
            try {
                File soundFile = new File(filePath);
                if (soundFile.exists()) {
                    AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundFile);
                    Clip clip = AudioSystem.getClip();
                    clip.open(audioStream);

                    if (filePath.contains("magic_wand")) {
                        powerUpSound = clip;
                    } else if (filePath.contains("Background")) {
                        backgroundMusic = clip;
                    }
                }
            } catch (Exception e) {
                // Continue with synthetic sounds
            }
        }
    }

    private Clip createBeepSound(int frequency, int duration, float amplitude) throws LineUnavailableException {
        Clip clip = AudioSystem.getClip();

        // Create a sine wave
        int sampleRate = 44100;
        int numSamples = duration * sampleRate / 1000;
        byte[] buffer = new byte[numSamples];

        for (int i = 0; i < numSamples; i++) {
            double angle = 2.0 * Math.PI * i * frequency / sampleRate;
            // Add some harmonics for better sound
            double sample = Math.sin(angle) * 0.7;
            sample += Math.sin(2 * angle) * 0.2;
            sample += Math.sin(3 * angle) * 0.1;

            // Apply envelope
            double envelope = 1.0;
            if (i < numSamples * 0.1) {
                // Attack
                envelope = i / (numSamples * 0.1);
            } else if (i > numSamples * 0.8) {
                // Release
                envelope = 1.0 - (i - numSamples * 0.8) / (numSamples * 0.2);
            }

            sample *= envelope * amplitude;
            buffer[i] = (byte)(sample * 127);
        }

        AudioFormat format = new AudioFormat(sampleRate, 8, 1, true, false);
        clip.open(format, buffer, 0, buffer.length);

        return clip;
    }

    public void playHit() {
        if (!soundEnabled || hitSound == null) return;

        hitSound.setFramePosition(0);
        setVolume(hitSound, volume);
        hitSound.start();
    }

    public void playGoal() {
        if (!soundEnabled || goalSound == null) return;

        goalSound.setFramePosition(0);
        setVolume(goalSound, volume);
        goalSound.start();
    }

    public void playPowerUp() {
        if (!soundEnabled || powerUpSound == null) return;

        powerUpSound.setFramePosition(0);
        setVolume(powerUpSound, volume * 1.2f);
        powerUpSound.start();
    }

    public void playBackgroundMusic() {
        if (!musicEnabled || backgroundMusic == null) return;

        if (!backgroundMusic.isRunning()) {
            backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
            setVolume(backgroundMusic, volume * 0.5f);
            backgroundMusic.start();
        }
    }

    public void stopBackgroundMusic() {
        if (backgroundMusic != null && backgroundMusic.isRunning()) {
            backgroundMusic.stop();
        }
    }

    private void setVolume(Clip clip, float volume) {
        if (clip == null) return;

        FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        float dB = (float) (Math.log(volume) / Math.log(10.0) * 20.0);
        dB = Math.max(gainControl.getMinimum(), Math.min(gainControl.getMaximum(), dB));
        gainControl.setValue(dB);
    }

    public void setSoundEnabled(boolean enabled) {
        this.soundEnabled = enabled;
    }

    public void setMusicEnabled(boolean enabled) {
        this.musicEnabled = enabled;
        if (enabled) {
            playBackgroundMusic();
        } else {
            stopBackgroundMusic();
        }
    }

    public void setVolume(float volume) {
        this.volume = Math.max(0, Math.min(1, volume));
    }

    public boolean isSoundEnabled() {
        return soundEnabled;
    }

    public boolean isMusicEnabled() {
        return musicEnabled;
    }

    public float getVolume() {
        return volume;
    }

    public void dispose() {
        if (hitSound != null) hitSound.close();
        if (goalSound != null) goalSound.close();
        if (powerUpSound != null) powerUpSound.close();
        if (backgroundMusic != null) backgroundMusic.close();
    }
}