package game.evgeha.logicalquiz;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import static game.evgeha.logicalquiz.Activity_Main.click_sound;
import static game.evgeha.logicalquiz.Activity_Main.coin_count;
import static game.evgeha.logicalquiz.Activity_Main.pop_sound;
import static game.evgeha.logicalquiz.Activity_Main.soundPool;

public class Level extends AppCompatActivity {

    public Button ans1, ans2, ans3, ans4, btn_continue, btn_end_continue, btn_end_play_again;
    public TextView question_txt, fact_txt, right_ans, end_txt, hint_txt;
    public ProgressBar countDown_timer, level_progress; // Обратный отсчёт

    public String code = "animals_", name, hint;
    public String[] questions, facts, png_codes, hints;

    public int btn_id = -1, heartsCnt = 3, hint_cost, penalty = 0, time_progress, progress = 0,  pos, stage, end, right_ans_cnt = 0, record;
    public final int TIME = 3; // Время на ответ
    public int cur_time = 0;

    public ImageView fact_png, end_png, hint_img;
    public ImageView[] hearts = new ImageView[3]; // Сердечки на экране

    public Dialog dialogFact, dialogHint, dialogEnd;

    public Intent intent;

    private SharedPreferences spCnts, spRecords;

    public boolean stopped = false; // Если преждевременно выйти из уровня, таймер всё равно продолжит отсчёт,
                                    // по окончании которого обновиться UI, которо нет и прилжение умрёт
    public void setFullScreen(){
        Window window = getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }
    // Слушатель кнопок
    public void listener() {
        btn_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogFact.dismiss();
                cur_time = 0;
                playSound(click_sound);
                if(stage == end)
                    levelEnding();
            }
        });
        ans1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_id = 0;
                playSound(click_sound);
            }
        });
        ans2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_id = 1;
                playSound(click_sound);
            }
        });
        ans3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_id = 2;
                playSound(click_sound);
            }
        });
        ans4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_id = 3;
                playSound(click_sound);
            }
        });
        btn_end_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                startActivity(intent);
                playSound(click_sound);
            }
        });
        btn_end_play_again.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playSound(click_sound);
                finish();
                startActivity(getIntent());
            }
        });

        hint_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playSound(pop_sound);
                penalty += hint_cost;
                showDialogHint(hints[stage]);
                disableHint();
            }
        });

    }
    public void playSound(int sound_id){
        soundPool.play(sound_id,1,1,0,0,1);
    }
    // Получаем массивы, с которыми будем работать
    public void getArrays() throws Exception{
        questions = getResources().getStringArray(getResources().getIdentifier(code + "questions", "array", getPackageName()));
        facts = getResources().getStringArray(getResources().getIdentifier(code + "facts", "array", getPackageName()));
        png_codes = getResources().getStringArray(getResources().getIdentifier(code + "ans_codes", "array", getPackageName()));
        hints = getResources().getStringArray(getResources().getIdentifier(code + "hints", "array", getPackageName()));
        end = questions.length / 5;
        if(facts.length != end || png_codes.length != end || hints.length != end)
            throw new Exception("Не совпадает кол-во вопросов / картинок / подсказок");
    }
    // Настройка уровня
    public void levelMainSetUp(Context context){
        setFullScreen();
        ans1 = (Button) findViewById(R.id.ans1);
        ans2 = (Button) findViewById(R.id.ans2);
        ans3 = (Button) findViewById(R.id.ans3);
        ans4 = (Button) findViewById(R.id.ans4);
        question_txt = (TextView) findViewById(R.id.question_txt);

        hearts[0] = (ImageView) findViewById(R.id.heart1);
        hearts[1] = (ImageView) findViewById(R.id.heart2);
        hearts[2] = (ImageView) findViewById(R.id.heart3);

        hint_img = (ImageView) findViewById(R.id.hint_img);

        pos = getIntent().getIntExtra("ID", 0) + 1;

        LevelInfo levelInfo = getIntent().getParcelableExtra("levelInfo");
        code = levelInfo.getCode();
        record = levelInfo.getRecord();
        name = levelInfo.getName();
        hint_cost = pos;

        countDown_timer = (ProgressBar) findViewById(R.id.timer);
        level_progress = (ProgressBar) findViewById(R.id.level_progress);

        createDialogFact(context);
        createDialogHint(context);
        createDialogEnd(context);
        listener();
        try {
            getArrays();
        }catch (Exception e){
            createDialogEnd(context);
            listener();
            btn_end_play_again.setVisibility(View.INVISIBLE);
            btn_end_continue.setText("Вернуться в лобби");
            showDialogEnd(e.getMessage(), R.drawable.cross);
        }

        level_progress.setMax(end);
    }
    // Обновить прогресс таймера на экране
    public void updateCurTime(){
        countDown_timer.setProgress(time_progress);
    }
    public void updateProgressBar(int progress){
        level_progress.setProgress(progress);
        level_progress.setSecondaryProgress(progress + 1);
    }
    // Обновить интерфейс всей активности
    public void updateQuestionUi(String question, String[] vars){
        if(stopped)
            return;
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                question_txt.setText(question);
                ans1.setText(vars[0]);
                ans2.setText(vars[1]);
                ans3.setText(vars[2]);
                ans4.setText(vars[3]);
                if(heartsCnt < 3)
                    hearts[heartsCnt].setImageResource(R.drawable.empty_heart);
            }
        };
        runOnUiThread(runnable);
    }
    // Первоначальная настройка окна
    public void dialogSetUp(Dialog dialog, int id, boolean cancelable){
        dialog.setContentView(id); // Что будет показывать диалоговое окно
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // Сделаем задний фон прозрачным
        dialog.setCancelable(cancelable);
    }
    // Создание диалогового окна
    public void createDialogFact(Context context){
        dialogFact = new Dialog(context);
        dialogSetUp(dialogFact, R.layout.dialog_window_facts, false);
        fact_txt = (TextView) dialogFact.findViewById(R.id.fact_description);
        right_ans = (TextView) dialogFact.findViewById(R.id.right_ans);
        fact_png = (ImageView) dialogFact.findViewById(R.id.fact_img);
        btn_continue = (Button) dialogFact.findViewById(R.id.question_continue);
    }
    public void createDialogHint(Context context){
        dialogHint = new Dialog(context);
        dialogSetUp(dialogHint, R.layout.dialog_window_hint, true);
        hint_txt = (TextView) dialogHint.findViewById(R.id.hint_txt);
    }
    public void showDialogHint(String hint){
        dialogUpdateUi(hint);
    }
    public void disableHint(){
        if(stopped)
            return;
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                hint_img.setClickable(false);
                hint_img.setImageResource(R.drawable.key_off);
            }
        };
        runOnUiThread(runnable);
    }
    public void enableHint(){
        if(stopped)
            return;
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                hint_img.setClickable(true);
                hint_img.setImageResource(R.drawable.key_on);
            }
        };
        runOnUiThread(runnable);
    }
    public void showDialogFact(String ans, String txt, String png_code){
        int id = getResources().getIdentifier(png_code, "drawable", getPackageName());
        dialogUpdateUi(ans, txt, id);
        cur_time = -1000000;
    }
    // Обновить интерфейс окна
    public void dialogUpdateUi(String ans, String txt, int id){
        if(stopped)
            return;
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                right_ans.setText(ans);
                fact_txt.setText(txt);
                fact_png.setImageResource(id);
                dialogFact.show();
            }
        };
        runOnUiThread(runnable);
    }
    public void dialogUpdateUi(String hint){
        if(stopped)
            return;
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                hint_txt.setText(hint);
                dialogHint.show();
            }
        };
        runOnUiThread(runnable);
    }
    // Добавить монеты
    public void addCoin(int coins){
        spCnts = getSharedPreferences("Counts", Context.MODE_PRIVATE);
        coin_count += coins;
        SharedPreferences.Editor editorCnt = spCnts.edit();
        editorCnt.putInt("Coins", coin_count);
        editorCnt.commit();
    }
    public void createDialogEnd(Context context){
        dialogEnd = new Dialog(context);
        dialogSetUp(dialogEnd, R.layout.dialog_window_level_end, false);
        end_png = (ImageView)dialogEnd.findViewById(R.id.end_img);
        end_txt = (TextView)dialogEnd.findViewById(R.id.end_txt);
        btn_end_continue = (Button)dialogEnd.findViewById(R.id.end_continue) ;
        btn_end_play_again = (Button)dialogEnd.findViewById(R.id.play_again);
    }
    public void showDialogEnd(String txt, int img_id){
        if(stopped)
            return;
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                end_png.setImageResource(img_id);
                end_txt.setText(txt);
            }
        };
        runOnUiThread(runnable);
        dialogEnd.show();
    }
    // Завершение уровня
    public void levelEnding(){
        int id, right_cnt, wrong_cnt;
        String txt;
        if(heartsCnt != 0) { // Если все жизни потрачены, то не добавляем монеты
            int coins = (questions.length / 5 - (3 - heartsCnt)) * pos;
            id = R.drawable.successful_level;
            if(record == 0) {
                txt = getResources().getString(R.string.Succ_end) + " " + Integer.toString(coins) + " " + getResources().getString(R.string.Money);
                record = coins;
            }
            else if(record >= coins) {
                coins = 0;
                txt = getResources().getString(R.string.Old_record) + " " + Integer.toString(record) + " " + getResources().getString(R.string.Money);
            }
            else{
                int copy = coins;
                coins -= record;
                record = copy;
                txt = getResources().getString(R.string.New_record) + " " + Integer.toString(coins) + " " + getResources().getString(R.string.Money);
            }
            addCoin(coins);
            spRecords = getSharedPreferences("Records", Context.MODE_PRIVATE);
            SharedPreferences.Editor edRecord =  spRecords.edit();
            edRecord.putInt(name, record);
            edRecord.commit();
        }
        else{
            id = R.drawable.cross;
            txt = getResources().getString(R.string.UnSucc_end);
        }
        spCnts = getSharedPreferences("Counts", Context.MODE_PRIVATE);
        right_cnt = spCnts.getInt("Right_cnt", 0) + right_ans_cnt;
        wrong_cnt = spCnts.getInt("Wrong_cnt", 0) + 3 - heartsCnt;
        SharedPreferences.Editor ed = spCnts.edit();
        ed.putInt("Right_cnt", right_cnt);
        ed.putInt("Wrong_cnt", wrong_cnt);
        ed.commit();
        showDialogEnd(txt, id);
    }

    @Override
    protected void onStop() {
        stopped = true;
        super.onStop();
    }
}
