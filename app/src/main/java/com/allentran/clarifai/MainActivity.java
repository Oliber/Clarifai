package com.allentran.clarifai;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.clarifai.api.ClarifaiClient;
import com.clarifai.api.RecognitionRequest;
import com.clarifai.api.RecognitionResult;
import com.clarifai.api.Tag;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private int PICK_IMAGE_REQUEST = 1;
    private final String APP_ID = "hjerQocu0N0dt324XLZw4agpUf-PuMibm5ILwW77";
    private final String APP_SECRET = "jhEaJ74h1dEfVy8MUuM3uZ5vsm_VeSsfaO5TCHA1";
    private String selectedImagePath = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button chooseFile = (Button) findViewById(R.id.button);
        chooseFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                i.setType("image/*");
                i.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(i, "Select Image"), PICK_IMAGE_REQUEST);
                ClarifaiClient clar = new ClarifaiClient(APP_ID, APP_SECRET);
                ImageView imageView = (ImageView) findViewById(R.id.imageView);
                List<RecognitionResult> results =
                        clar.recognize(new RecognitionRequest(selectedImagePath));
                TextView filePath = (TextView) findViewById(R.id.textView2);
                filePath.setText(selectedImagePath);
                ScrollView view = (ScrollView) findViewById(R.id.scrollView);
                GridLayout scroll = new GridLayout(getApplicationContext());
                scroll.setColumnCount(3);
                ArrayList<View> genTags = new ArrayList<View>();
                for(Tag tag : results.get(0).getTags()) {
                    TextView text = new TextView(getApplicationContext());
                    text.setText(tag.getName() + ": " + tag.getProbability());
                    genTags.add(text);
                }
                scroll.addChildrenForAccessibility(genTags);
                view.addView(scroll);
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK &&
                data!= null && data.getData() != null) {
            Uri uri = data.getData();
            selectedImagePath = uri.getPath();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);

                ImageView imageView = (ImageView) findViewById(R.id.imageView);
                imageView.setImageBitmap(bitmap);
            }catch (IOException e) {
                e.printStackTrace();
            }

            }
        }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
