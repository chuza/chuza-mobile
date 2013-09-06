package code.chuza;

import java.io.File;
import com.example.chuza.R;
import android.net.Uri;
import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends Activity {private WebView myWebView;

@Override
protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	this.setContentView(R.layout.activity_main);
	

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

//boton de home para voltar ao inicio
public boolean onOptionsItemSelected(MenuItem item) {
    // This callback is used only when mSoloFragment == true (see
    // onActivityCreated above)
    switch (item.getItemId()) {
    case android.R.id.home:
        // App icon in Action Bar clicked; go up
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Reuse the
                                                            // existing
                                                            // instance
        startActivity(intent);

        return true;
    default:
        return super.onOptionsItemSelected(item);
    }}

//ir cara atras co boton do dispositivo
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

//borrar cache ao sair da app

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

}
