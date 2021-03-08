package game.evgeha.logicalquiz;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class CommonLevel extends AppCompatActivity {

    private Button ans1, ans2, ans3, ans4;
    private TextView question_txt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_level);
        //Убираем херню сверху
        Window window = getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        ans1 = (Button) findViewById(R.id.ans1);
        ans2 = (Button) findViewById(R.id.ans2);
        ans3 = (Button)findViewById(R.id.ans3);
        ans4 = (Button)findViewById(R.id.ans4);
        question_txt = (TextView)findViewById(R.id.question_txt);

        String[] questions = getResources().getStringArray(R.array.animals_questions);

       // for(int i = 0; i < questions.length; ++i){
            Question question = new Question(questions, 0);
            question_txt.setText(question.getName());

            String[] vars = new String[4];
            vars = question.getVars();

            ans1.setText(vars[0]);
            ans2.setText(vars[1]);
            ans3.setText(vars[2]);
            ans4.setText(vars[3]);
       // }
    }
}