package com.bagashaka.tambotv2;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChatFragment extends Fragment {
    RecyclerView recyclerView;
    TextView welcomeTextView;
    EditText messageEditText;
    ImageButton sendButton;
    List<Message> messageList;
    ImageButton logoutBtn;
    MessageAdapter messageAdapter;
    FirebaseAuth mAuth;
    private Handler handler;

    public static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");
    OkHttpClient client = new OkHttpClient();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View chtFrag = inflater.inflate(R.layout.fragment_chat, container, false);
        recyclerView = chtFrag.findViewById(R.id.recycler_view);
        welcomeTextView = chtFrag.findViewById(R.id.welcome_text);
        messageEditText = chtFrag.findViewById(R.id.message_edit_text);
        sendButton = chtFrag.findViewById(R.id.send_btn);
        logoutBtn = chtFrag.findViewById(R.id.btnLogout);

        handler = new Handler(Looper.getMainLooper());

        mAuth = FirebaseAuth.getInstance();

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Intent intent = new Intent(getActivity(),loginActivity.class);
                startActivity(intent);
            }
        });

        messageList = new ArrayList<>();
//       RecycleView
        messageAdapter = new MessageAdapter(messageList);
        recyclerView.setAdapter(messageAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String question = messageEditText.getText().toString().trim();
                addToChat(question,Message.SENT_BY_ME);
                messageEditText.setText("");
                CallAPI(question);
                welcomeTextView.setVisibility(View.GONE);
            }
        });
        return chtFrag;
    }
    void addToChat(String message,String sendby){
        if (getActivity() != null) {
            getActivity().runOnUiThread(new Runnable(){
                @Override
                public void run(){
                    messageList.add(new Message(message,sendby));
                    messageAdapter.notifyDataSetChanged();
                    recyclerView.smoothScrollToPosition(messageAdapter.getItemCount());
                }
            });
        }
    }

    void addResponse(String response){
        messageList.remove(messageList.size()-1);
        addToChat(response,Message.SENT_BY_BOT);
    }

    void CallAPI(String question){
        messageList.add(new Message("Typing...",Message.SENT_BY_BOT));
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("model","gpt-3.5-turbo");
            JSONArray messageArr = new JSONArray();
            JSONObject obj = new JSONObject();
            obj.put("role","user");
            obj.put("content",question);
            messageArr.put(obj);

            jsonObject.put("messages",messageArr);
        }catch (JSONException e){
            throw new RuntimeException(e);
        }

        RequestBody body = RequestBody.create(jsonObject.toString(),JSON);
        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/chat/completions")
                .header("Authorization","Bearer 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")
                  .post(body)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                addResponse("Fail to Load response due to "+e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if(response.isSuccessful()){
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        JSONArray jsonArray = null;
                        jsonArray = jsonObject.getJSONArray("choices");
                        String result = jsonArray.getJSONObject(0)
                                .getJSONObject("message")
                                .getString("content");
                        addResponse(result.trim());
                    }catch (JSONException e){
                        throw new RuntimeException(e);
                    }
                }else {
                    addResponse("Failed to response due to "+ response.body().string() );
                }
            }
        });
    }
}