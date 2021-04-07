package game.evgeha.logicalquiz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    public static int click_sound, wrong_sound, right_sound, successful_sound;
    public static int coin_count; //Кол-во монет
    public static SoundHandler player;
    public static SoundPool soundPool;

    private Button start_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setFullScreen();
        start_btn = (Button)findViewById(R.id.start_button);

        setSoundPoolClick();
        setSounds();

        start_btn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        soundPool.play(click_sound, 1, 1, 0, 0, 1);
                        Intent intent = new Intent(MainActivity.this, LevelSelection.class);
                        //Переходим в выбор уровня
                        startActivity(intent);
                    }
                }
        );
    }

    private void setFullScreen(){
        Window window = getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    public void setSoundPoolClick(){
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ASSISTANCE_ACCESSIBILITY)
                .setContentType(AudioAttributes.CONTENT_TYPE_MOVIE)
                .build();
        soundPool = new SoundPool.Builder()
                .setMaxStreams(6)
                .setAudioAttributes(audioAttributes)
                .build();
    }

    public void setSounds(){
        click_sound = soundPool.load(this, R.raw.click, 0);
        right_sound = soundPool.load(this, R.raw.right_sound, 0);
        wrong_sound = soundPool.load(this, R.raw.wrong_sound, 0);
        successful_sound = soundPool.load(this, R.raw.successful_sound, 0);
    }

}