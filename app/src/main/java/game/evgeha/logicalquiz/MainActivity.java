package game.evgeha.logicalquiz;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity {

    private Button start_btn;
    public static int coin_count; //Кол-во монет
    public static SoundHandler player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Делаем полный экран
        Window window = getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        start_btn = (Button)findViewById(R.id.start_button);

        player = new SoundHandler(this);

        start_btn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        player.play(R.raw.click);
                        Intent intent = new Intent(MainActivity.this, LevelSelection.class);
                        //Переходим в выбор уровня
                        startActivity(intent);
                    }
                }
        );
    }



}