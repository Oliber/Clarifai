package com.allentran.clarifai;

                import android.app.Activity;
                import android.content.Context;
                import android.content.Intent;
                import android.graphics.Bitmap;
                import android.graphics.BitmapFactory;
                import android.net.Uri;
                import android.os.AsyncTask;
                import android.os.Bundle;
                import android.view.Menu;
                import android.view.MenuItem;
                import android.view.View;
                import android.widget.Button;
                import android.widget.ImageView;
                import android.widget.TextView;

                import com.clarifai.api.ClarifaiClient;
                import com.clarifai.api.RecognitionRequest;
                import com.clarifai.api.RecognitionResult;
                import com.clarifai.api.Tag;
                import com.clarifai.api.exception.ClarifaiException;

                import java.io.ByteArrayOutputStream;
                import java.io.IOException;
                import java.util.ArrayList;

                import java.util.List;

                public class MainActivity extends Activity {
                    private int PICK_IMAGE_REQUEST = 1;
                    private final String APP_ID = "hjerQocu0N0dt324XLZw4agpUf-PuMibm5ILwW77";
                    private final String APP_SECRET = "jhEaJ74h1dEfVy8MUuM3uZ5vsm_VeSsfaO5TCHA1";

                    private final ClarifaiClient clar = new ClarifaiClient(APP_ID, APP_SECRET);
                    private Button findTags;
                    private Button chooseFile;
                    private ImageView imageView;
                    private Bitmap bitmap;
                    private TextView textView;

                    @Override
                    protected void onCreate(Bundle savedInstanceState) {
                        super.onCreate(savedInstanceState);
                        setContentView(R.layout.activity_main);
                        // Assign Fields
                        textView = (TextView)findViewById(R.id.textView2);
                        imageView = (ImageView)findViewById(R.id.imageView);
                        chooseFile = (Button) findViewById(R.id.button);
                        chooseFile.setOnClickListener(new View.OnClickListener() {
                            @Override
                            // Select Image from Gallery
                            public void onClick(View v) {
                                Intent i = new Intent();
                                i.setType("image/*");
                                i.setAction(Intent.ACTION_GET_CONTENT);
                                startActivityForResult(Intent.createChooser(i, "Select Image"), PICK_IMAGE_REQUEST);
                            }
                        });
                    }

                    @Override
                    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
                        super.onActivityResult(requestCode, resultCode, data);
                        Bundle bundle = data.getExtras();
                        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK &&
                                data!= null && data.getData() != null) {

                            // The image
                            Uri uri = data.getData();

                            bitmap = loadBitmapFromUri(data.getData());
                            imageView.setMaxHeight(300);
                            imageView.setMaxWidth(600);
                            imageView.setImageBitmap(bitmap);
                            // Don't understand AsyncTask
                            // Run recognition on a background thread since it makes a network call.
                            new AsyncTask<Bitmap, Void, RecognitionResult>() {
                                @Override protected RecognitionResult doInBackground(Bitmap... bitmaps) {
                                    return recognizeBitmap(bitmaps[0]);
                                }
                                @Override protected void onPostExecute(RecognitionResult result) {
                                    updateUIForResult(result);
                                }
                            }.execute(bitmap);
                        }
                    }

                    // From Demo
                    private Bitmap loadBitmapFromUri(Uri uri) {
                        try {
                            // The image may be large. Load an image that is sized for display. This follows best
                            // practices from http://developer.android.com/training/displaying-bitmaps/load-bitmap.html
                            BitmapFactory.Options opts = new BitmapFactory.Options();
                            //opts.inJustDecodeBounds = true;
                            //opts = new BitmapFactory.Options();
                            return BitmapFactory.decodeStream(getContentResolver().openInputStream(uri), null, opts);
                        } catch (IOException e) {
                           // Log.e(TAG, "Error loading image: " + uri, e);
                        }
                        return null;
                    }

                    // From Demo
                    private RecognitionResult recognizeBitmap(Bitmap bitmap) {
                        try {
                            // Scale down the image. This step is optional. However, sending large images over the
                            // network is slow and  does not significantly improve recognition performance.
                            Bitmap scaled = Bitmap.createScaledBitmap(bitmap, 320,
                                    320 * bitmap.getHeight() / bitmap.getWidth(), true);

                            // Compress the image as a JPEG.
                            ByteArrayOutputStream out = new ByteArrayOutputStream();
                            scaled.compress(Bitmap.CompressFormat.JPEG, 90, out);
                            byte[] jpeg = out.toByteArray();

                            // Send the JPEG to Clarifai and return the result.
                            return clar.recognize(new RecognitionRequest(jpeg)).get(0);
                        } catch (ClarifaiException e) {

                            return null;
                        }
                    }

                    /** Updates the UI by displaying tags for the given result. */
                    private void updateUIForResult(RecognitionResult result) {
                        if (result != null) {
                            if (result.getStatusCode() == RecognitionResult.StatusCode.OK) {
                                // Display the list of tags in the UI.
                                StringBuilder b = new StringBuilder();
                                for (Tag tag : result.getTags()) {
                                    b.append(b.length() > 0 ? ", " : "").append(tag.getName());
                                }
                                textView.setText("Tags:\n" + b);
                            } else {

                                textView.setText("Sorry, there was an error recognizing your image.");
                            }
                        } else {
                            textView.setText("Sorry, there was an error recognizing your image.");
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
