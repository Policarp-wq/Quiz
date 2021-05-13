package game.evgeha.logicalquiz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class Activity_Main extends AppCompatActivity {

    public static int click_sound, wrong_sound, right_sound, successful_sound;
    public static int coin_count; //Кол-во монет
    public static SoundPool soundPool;

    private Button start_btn;
    private TextView right_cnt_txt, wrong_cnt_txt;
    private int right_cnt, wrong_cnt;

    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setFullScreen();
        start_btn = (Button)findViewById(R.id.start_button);
        right_cnt_txt = (TextView)findViewById(R.id.right_cnt);
        wrong_cnt_txt = (TextView)findViewById(R.id.wrong_cnt);

        setSoundPoolClick();
        setSounds();

        sp = getSharedPreferences("Counts", Context.MODE_PRIVATE);
        right_cnt = sp.getInt("Right_cnt", 0);
        wrong_cnt = sp.getInt("Wrong_cnt", 0);
        right_cnt_txt.setText(getString(R.string.Right_cnt) + ' ' + String.valueOf(right_cnt));
        right_cnt_txt.setTextColor(getColor(R.color.green));
        wrong_cnt_txt.setText(getString(R.string.Wrong_cnt) + ' ' + String.valueOf(wrong_cnt));
        wrong_cnt_txt.setTextColor(getColor(R.color.red));

        start_btn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        soundPool.play(click_sound, 1, 1, 0, 0, 1);
                        Intent intent = new Intent(Activity_Main.this, Activity_LevelSelection.class);
                        //Переходим в выбор уровня
                        startActivity(intent);
                    }
                }
        );
    }

    public void setFullScreen(){
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