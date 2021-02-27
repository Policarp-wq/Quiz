package game.evgeha.logicalquiz;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public class QuizLevel extends AppCompatActivity {

    private int item, heart_cnt; //Оставшееся кол-во жизней
    private TextView question;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_level);
        Window window = getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        item = getIntent().getExtras().getInt("Level type"); //Получаем позицию уровня в массиве
        question = (TextView) findViewById(R.id.question);
        question.setText(Integer.toString(item));
    }
}