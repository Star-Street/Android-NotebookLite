package com.example.notebooklite;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class EditFileActivity extends AppCompatActivity {

    private File currentFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_file);

        EditText editText = findViewById(R.id.edit_text);
        Button saveButton = findViewById(R.id.save_button);

        // Создание нового текстового файла
        try {
            File directory = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
            if (!directory.exists()) {
                directory.mkdirs();
            }
            currentFile = new File(directory, "file_" + System.currentTimeMillis() + ".txt");
            currentFile.createNewFile();
        } catch (IOException e) {
            Toast.makeText(this, "Ошибка создания блокнота", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Сохранение содержимого файла
        saveButton.setOnClickListener(v -> {
            String content = editText.getText().toString();
            try (FileOutputStream fos = new FileOutputStream(currentFile)) {
                fos.write(content.getBytes());
                Toast.makeText(this, "Блокнот сохранён", Toast.LENGTH_SHORT).show();

                // Возвращаем путь к файлу в MainActivity
                Intent resultIntent = new Intent();
                resultIntent.putExtra("filePath", currentFile.getAbsolutePath());
                setResult(RESULT_OK, resultIntent);
                finish();
            } catch (IOException e) {
                Toast.makeText(this, "Ошибка сохранения блокнота", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
