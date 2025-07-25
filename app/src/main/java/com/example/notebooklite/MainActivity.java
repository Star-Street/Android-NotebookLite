package com.example.notebooklite;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.OpenableColumns;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_OPEN_FILE = 1;
    private static final int REQUEST_CODE_CREATE_FILE = 2;

    private EditText editText;
    private Uri currentFileUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = findViewById(R.id.editText);
        Button createFileButton = findViewById(R.id.createFileButton);
        Button openFileButton = findViewById(R.id.openFileButton);
        Button saveFileButton = findViewById(R.id.saveFileButton);

        // Создание нового файла
        createFileButton.setOnClickListener(v -> createFile());

        // Открытие файла
        openFileButton.setOnClickListener(v -> openFile());

        // Сохранение файла
        saveFileButton.setOnClickListener(v -> saveFile());
    }

    // Метод для открытия файлов
    private void openFile() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/plain");
        startActivityForResult(intent, REQUEST_CODE_OPEN_FILE);
    }

    // Метод для сохранения изменений в текущем файле
    private void saveFile() {
        if (currentFileUri == null) {
            Toast.makeText(this, "Блокнот не открыт", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            String content = editText.getText().toString();
            FileOutputStream outputStream = (FileOutputStream) getContentResolver()
                    .openOutputStream(currentFileUri, "wt");
            if (outputStream != null) {
                outputStream.write(content.getBytes());
                outputStream.close();
                Toast.makeText(this, "Блокнот успешно сохранён", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Ошибка сохранения блокнота", Toast.LENGTH_SHORT).show();
            Log.e("TextEditorApp", "Ошибка сохранения блокнота", e);
        }
    }

    // Метод для создания нового файла
    private void createFile() {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TITLE, "newfile.txt"); // Имя нового файла
        startActivityForResult(intent, REQUEST_CODE_CREATE_FILE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();

            if (requestCode == REQUEST_CODE_OPEN_FILE) {
                // Открытие файла
                currentFileUri = uri;
                readFile(currentFileUri);
            } else if (requestCode == REQUEST_CODE_CREATE_FILE) {
                // Создание нового файла
                currentFileUri = uri;
                saveFile(); // Сохраняем текст, введённый в EditText, в новый файл
            }
        }
    }

    // Метод для чтения файла
    private void readFile(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            if (inputStream == null) return;

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder stringBuilder = new StringBuilder();

            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
            reader.close();

            editText.setText(stringBuilder.toString());
            Toast.makeText(this, "Блокнот открыт", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Ошибка открытия блокнота", Toast.LENGTH_SHORT).show();
            Log.e("TextEditorApp", "Ошибка открытия блокнота", e);
        }
    }
}
