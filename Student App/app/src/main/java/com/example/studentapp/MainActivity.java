package com.example.studentapp;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.studentapp.Models.Ticket;
import com.example.studentapp.Models.User;
import com.example.studentapp.Utils.SingletonRequestQueue;
import com.example.studentapp.Utils.UserLocalStore;
import com.google.gson.Gson;

import org.json.JSONObject;

//import okhttp3.OkHttpClient;
//import okhttp3.WebSocket;
//import okhttp3.WebSocketListener;
public class MainActivity extends AppCompatActivity {

//    private class EchoWebSocketListener extends WebSocketListener {
//        private static final String TAG = "EchoWebSocketListener";
//        private static final int NORMAL_CLOSURE_STATUS = 1000;
//
//        @Override
//        public void onOpen(WebSocket webSocket, okhttp3.Response response) {
//            Log.v(TAG, "WebSocket oppened!");
//        }
//
//        @Override
//        public void onMessage(WebSocket webSocket, String text) {
//            refreshText();
//        }
//
//        @Override
//        public void onClosing(WebSocket webSocket, int code, String reason) {
//            webSocket.close(NORMAL_CLOSURE_STATUS, null);
//            Log.v(TAG, "Closing WebSocket: " + code + " / " + reason);
//        }
//
//        @Override
//        public void onFailure(WebSocket webSocket, Throwable t, okhttp3.Response response) {
//            Log.e(TAG, "WebSocket error : " + t.getMessage());
//        }
//    }

    public static final String TAG = "MainActivity";
    private TextView status;
    private final Gson gson = new Gson();
    private SingletonRequestQueue singleton;
    private User userStored;

    private Socket socket;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        OkHttpClient client = new OkHttpClient();
//        okhttp3.Request request = new okhttp3.Request.Builder().url("http://yursilv.alwaysdata.net").build();
//        EchoWebSocketListener listener = new EchoWebSocketListener();
//        WebSocket ws = client.newWebSocket(request, listener);
        final UserLocalStore userLocalStore = new UserLocalStore(this);
        userLocalStore.storeUserData(new User("yury", 1, "Yury", "Silvestrov-Henocq"));
        userStored = userLocalStore.getStoredUser();

        this.status = (TextView)this.findViewById(R.id.status);
        final Button refresh = (Button)this.findViewById(R.id.btn);

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshText();
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (singleton.getRequestQueue() != null) {
            singleton.getRequestQueue().cancelAll(TAG);
        }
    }

    private void refreshText() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                // Instantiate the RequestQueue.
                singleton = SingletonRequestQueue.getInstance(getApplicationContext());
                String url = "http://yursilv.alwaysdata.net/api/queues/" + userStored.getAbonnement() + "/currentTicket";

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                        (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {

                                /*Type data_type = new TypeToken<ArrayList<Queue>>(){}.getType();
                                ArrayList<Queue> data = gson.fromJson(response.toString(), data_type);
                                StringBuilder str = new StringBuilder();
                                for (Queue q : data) {
                                    str.append(q.toString() + "\n");
                                }*/
                                Ticket currentTicket = gson.fromJson(response.toString(), Ticket.class);
                                if (currentTicket.getUserLogin().equals(userStored.getLoggin())) {
                                    status.setText("Oui vous êtes attendu");
                                } else {
                                    status.setText("Non vous n'êtes pas attendu");
                                }
                                Log.d(TAG, currentTicket.toString());
                                Log.d(TAG, userStored.toString());
                            }
                        }, new Response.ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError error) {
                                // TODO: Handle error
                                status.setText("Oups! ça marche pas très bien...");
                                Log.d(TAG, error.toString());
                            }
                        });

                // Access the RequestQueue through your singleton class.
                singleton.addToRequestQueue(jsonObjectRequest);
            }
        });
    }

    private void startWebSocket() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    socket = IO.socket("http://yursilv.alwaysdata.net");
                    socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {

                        @Override
                        public void call(Object... args) {
                            Log.d(TAG, "Conected");
                            refreshText();
                        }

                    }).on("update", new Emitter.Listener() {

                        @Override
                        public void call(Object... args) {
                            refreshText();
                        }

                    }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {

                        @Override
                        public void call(Object... args) {
                            Log.d(TAG, "Disconnected");
                        }

                    }).on(Socket.EVENT_ERROR, new Emitter.Listener() {
                        @Override
                        public void call(Object... args) {

                        }
                    });
                    socket.connect();
                } catch (URISyntaxException e) {
                    Log.e(TAG, e.getMessage());
                }
            }
        });
    }
}
