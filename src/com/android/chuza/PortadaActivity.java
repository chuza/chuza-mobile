package com.android.chuza;

import java.io.File;

import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.Toast;

public class PortadaActivity extends Activity {
	private String foto;
	private static int TAKE_PICTURE = 1;
	double aleatorio=0;
	private WebView myWebView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_portada);
        
        ActionBar actionBar=getActionBar();
    	actionBar.setDisplayHomeAsUpEnabled(true);
    	
    	this.myWebView = (WebView) this.findViewById(R.id.webview);

    	
    	WebSettings webSettings = myWebView.getSettings();
    	
    	webSettings.setJavaScriptEnabled(true);

    	//carga a web de inicio
    	myWebView.setWebViewClient(new MyWebViewClient());

    	myWebView.loadUrl("http://m.chuza.gl");
        
    }

    private class MyWebViewClient extends WebViewClient {

    	@Override //para manter os enlaces dentro do visor
    	public boolean shouldOverrideUrlLoading(WebView view, String url) {

    		if (Uri.parse(url.substring(0, 9)).getHost().equals("m.chuza.gl")) {
    		
    			return false;
    		}

    		
    		     view.loadUrl(url);
    	        return true;
    	    }
    	}
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(event.getAction() == KeyEvent.ACTION_DOWN){
            switch(keyCode)
            {
            case KeyEvent.KEYCODE_BACK:
                if(myWebView.canGoBack() == true){
                    myWebView.goBack();
                }else{
                    finish();
                }
                return true;
            }

        }
        return super.onKeyDown(keyCode, event);
    }
    
    protected void onDestroy(){super.onDestroy();

    try {
        trimCache(this);
       // Toast.makeText(this,"onDestroy " ,Toast.LENGTH_LONG).show();
    } catch (Exception e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    }

    }
    public static void trimCache(Context context) {
    try {
       File dir = context.getCacheDir();
       if (dir != null && dir.isDirectory()) {
          deleteDir(dir);
       }
    } catch (Exception e) {
    }
    }


    public static boolean deleteDir(File dir) {
    if (dir != null && dir.isDirectory()) {
       String[] children = dir.list();
       for (int i = 0; i < children.length; i++) {
          boolean success = deleteDir(new File(dir, children[i]));
          if (!success) {
             return false;
          }
       }
    }

    return dir.delete();
    }
    
   
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.portada, menu);
        return true;
    }
    
    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
    	case R.id.action_settings:{Intent intent = new Intent(this, PortadaActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Reuse the
        // existing
        // instance
startActivity(intent);}
    	return true;
	    case R.id.camara:{
    	aleatorio = new Double(Math.random() * 10000).intValue();
		
    	File direct = new File(Environment.getExternalStorageDirectory() + "/Chuza");

    	   if(!direct.exists())
    	    {
    	        if(direct.mkdir()) 
    	          {
    	           //directory is created;
    	          }

    	    }
    	
    	setFoto(Environment.getExternalStorageDirectory() + "/Chuza/imx"+ aleatorio +".jpg");
		Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    	Uri output = Uri.fromFile(new File(foto));
		intent.putExtra(MediaStore.EXTRA_OUTPUT, output);
		startActivityForResult(intent, TAKE_PICTURE);
	    }
    	return super.onOptionsItemSelected(item);
	    case android.R.id.home:{
	        // App icon in Action Bar clicked; go up
	        Intent intent = new Intent(this, PortadaActivity.class);
	        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	        startActivity(intent);}

	        return true;
	    
	        }
		return super.onOptionsItemSelected(item);
    }


	public String getFoto() {
		return foto;
	}


	public void setFoto(String foto) {
		this.foto = foto;
	}
	
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		ImageView iv = (ImageView) findViewById(R.id.imageView1);
		iv.setImageBitmap(BitmapFactory.decodeFile(foto));

		
		
		File file = new File(foto);
		
		
		if (file.exists()) {
			UploaderFoto nuevaTarea = new UploaderFoto();
			nuevaTarea.execute(foto);
			iv.setImageDrawable(null);
					}
		else
			Toast.makeText(getApplicationContext(), "Non se realizou a foto", Toast.LENGTH_SHORT).show();

	}
	/*
	 * Clase asincrona para subir la foto
	 */
	class UploaderFoto extends AsyncTask<String, Void, Void>{

		ProgressDialog pDialog;
		String miFoto = "";
		
		@Override
		protected Void doInBackground(String... params) {
			miFoto = params[0];
			try { 
				HttpClient httpclient = new DefaultHttpClient();
				httpclient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
				HttpPost httppost = new HttpPost("http://chuza.gl/backend/uploadphoto.php");
				File file = new File(miFoto);
				MultipartEntity mpEntity = new MultipartEntity();
				ContentBody foto = new FileBody(file, "image/jpeg");
				mpEntity.addPart("fotoUp", foto);
				httppost.setEntity(mpEntity);
				httpclient.execute(httppost);
				httpclient.getConnectionManager().shutdown();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
		
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(PortadaActivity.this);
	        pDialog.setMessage("Chuzando imaxe...");
	        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
	        pDialog.setCancelable(true);
	        pDialog.show();
		}
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			pDialog.dismiss();
			
		}
	}
}
