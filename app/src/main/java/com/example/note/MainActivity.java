package com.example.note;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private EditText noteInput;
    private LinearLayout notesLayout;
    private static final String NOTES_FILE = "notes.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Tạo layout chính với nền trắng
        LinearLayout mainLayout = new LinearLayout(this);
        mainLayout.setOrientation(LinearLayout.VERTICAL);
        mainLayout.setBackgroundColor(getResources().getColor(android.R.color.white)); // Nền trắng
        setContentView(mainLayout);

        // Tạo layout để thêm ghi chú
        LinearLayout addLayout = new LinearLayout(this);
        addLayout.setOrientation(LinearLayout.HORIZONTAL);
        addLayout.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        noteInput = new EditText(this);
        noteInput.setHint("Nhập ghi chú ở đây...");
        noteInput.setTextColor(getResources().getColor(android.R.color.black)); // Màu văn bản đen
        noteInput.setHintTextColor(getResources().getColor(android.R.color.darker_gray)); // Màu gợi ý
        noteInput.setBackgroundResource(android.R.drawable.edit_text); // Khung văn bản
        noteInput.setLayoutParams(new LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                0.8f
        ));

        Button addButton = new Button(this);
        addButton.setText("Thêm");
        addButton.setLayoutParams(new LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                0.2f
        ));

        addLayout.addView(noteInput);
        addLayout.addView(addButton);

        // Tạo ScrollView để hiển thị danh sách ghi chú
        ScrollView scrollView = new ScrollView(this);
        scrollView.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));

        notesLayout = new LinearLayout(this);
        notesLayout.setOrientation(LinearLayout.VERTICAL);
        notesLayout.setPadding(16, 16, 16, 16);
        scrollView.addView(notesLayout);

        // Thêm các layout vào mainLayout
        mainLayout.addView(addLayout);
        mainLayout.addView(scrollView);

        // Xử lý sự kiện nhấn nút thêm ghi chú
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNote();
            }
        });

        // Tải ghi chú từ file
        loadNotes();
    }

    private void addNote() {
        String noteText = noteInput.getText().toString().trim();
        if (!TextUtils.isEmpty(noteText)) {
            createNoteCard(noteText);
            saveNotes();
            noteInput.setText("");
        }
    }

    private void createNoteCard(String noteText) {
        // Tạo CardView để làm đẹp giao diện ghi chú
        CardView cardView = new CardView(this);
        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        cardParams.setMargins(16, 16, 16, 0);
        cardView.setLayoutParams(cardParams);
        cardView.setRadius(10);
        cardView.setCardElevation(8);
        cardView.setBackgroundColor(getResources().getColor(android.R.color.white));

        // Tạo LinearLayout để chứa nội dung bên trong CardView
        LinearLayout cardLayout = new LinearLayout(this);
        cardLayout.setOrientation(LinearLayout.VERTICAL);
        cardLayout.setPadding(16, 16, 16, 16);
        cardView.addView(cardLayout);

        // Tạo TextView để hiển thị ghi chú
        TextView noteView = new TextView(this);
        noteView.setText(noteText);
        noteView.setTextSize(18);
        noteView.setTextColor(getResources().getColor(android.R.color.black)); // Màu văn bản đen
        cardLayout.addView(noteView);

        // Tạo nút Xoá
        Button deleteButton = new Button(this);
        deleteButton.setText("Xoá");
        deleteButton.setBackgroundColor(getResources().getColor(android.R.color.holo_red_light)); // Nền nút đỏ
        deleteButton.setTextColor(getResources().getColor(android.R.color.white)); // Chữ trắng
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notesLayout.removeView(cardView);
                saveNotes();
            }
        });
        cardLayout.addView(deleteButton);

        notesLayout.addView(cardView);
    }

    private void saveNotes() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(getNotesFile(), false))) {
            for (int i = 0; i < notesLayout.getChildCount(); i++) {
                CardView cardView = (CardView) notesLayout.getChildAt(i);
                LinearLayout cardLayout = (LinearLayout) cardView.getChildAt(0);
                TextView noteView = (TextView) cardLayout.getChildAt(0);
                writer.write(noteView.getText().toString());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadNotes() {
        File file = getNotesFile();
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    createNoteCard(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private File getNotesFile() {
        return new File(getFilesDir(), NOTES_FILE);
    }
}
