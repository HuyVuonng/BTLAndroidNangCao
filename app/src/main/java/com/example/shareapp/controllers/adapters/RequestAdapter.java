package com.example.shareapp.controllers.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shareapp.R;
import com.example.shareapp.controllers.constant.NotifiTypeConstant;
import com.example.shareapp.controllers.methods.DateTimeMethod;
import com.example.shareapp.models.Notification;
import com.example.shareapp.models.Post;
import com.example.shareapp.models.Request;

import java.util.List;
import java.util.UUID;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.RequestViewHolder>{
    private View mView;
    private List<Request> mListRequest;
    public RequestAdapter(List<Request> listRequest) {
        mListRequest = listRequest;
    }
    @NonNull
    @Override
    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_request, parent, false);

        return new RequestViewHolder(mView);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestViewHolder holder, int position) {
        Request request = mListRequest.get(position);
        if(request == null)
            return;

        Notification.getNotificationById(request.getNotificationId(), new Notification.INotificationReceivedListener() {
            @Override
            public void onNotifiDataReceived(Notification notifi) {
                if(notifi != null) {
                    holder.tvTitle.setText(notifi.getTitle());
                    holder.tvMessage.setText(notifi.getContent());
                    holder.tvCreatedAt.setText(DateTimeMethod.timeDifference(notifi.getCreatedAt()));
                }
            }
        });

        holder.btnDeny.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickHandleReplyRequest(request.getRequestId(),
                        NotifiTypeConstant.TYPE_DENY,
                        "Yêu cầu của bạn đã bị từ chối.",
                        holder);
            }
        });

        holder.btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickHandleReplyRequest(request.getRequestId(),
                        NotifiTypeConstant.TYPE_ACCEPT,
                        "Yêu cầu của bạn đã được chấp thuận.",
                        holder);
            }
        });
    }

    private void onClickHandleReplyRequest(String requestId, String type, String contentNotifi, RequestViewHolder holder) {
        Request.replyRequestById(requestId, type);
        Request.getRequestById(requestId, new Request.IRequestReceivedListener() {
            @Override
            public void onRequestDataReceived(Request request) {
                if(request != null) {
                    Post.getPostById(request.getPostId(), new Post.IPostDataReceivedListener() {
                        @Override
                        public void onPostDataReceived(Post post) {
                            if(post != null) {
                                Notification notification = new Notification();
                                notification.setNotifiId(UUID.randomUUID().toString());
                                notification.setTargetId(request.getUserId());
                                notification.setType(type);
                                notification.setTitle("Yêu cầu nhận '" + post.getTitle() + "' của bạn.");
                                notification.setContent(contentNotifi);
                                notification.setStatus(false);
                                notification.setCreatedAt(System.currentTimeMillis());

                                Notification.createNotification(notification);

                                // cập nhật số lượng
                                if(type.equals(NotifiTypeConstant.TYPE_ACCEPT))
                                    Post.reduceCountProduct(post.getPostId());
                            }
                        }
                    });
                }
            }
        });

        String result;
        if(type.equals(NotifiTypeConstant.TYPE_DENY)) {
            result = "Bạn đã từ chối đề nghị này.";
        } else {
            result = "Bạn đã chấp thuận đề nghị này.";
        }
        holder.tvMsgResult.setVisibility(View.VISIBLE);
        holder.btnDeny.setVisibility(View.GONE);
        holder.btnAccept.setVisibility(View.GONE);
        holder.tvMsgResult.setText(result);
    }

    @Override
    public int getItemCount() {
        return mListRequest.size();
    }

    public class RequestViewHolder extends RecyclerView.ViewHolder {
        private TextView tvTitle, tvMessage, tvCreatedAt, tvMsgResult;
        private Button btnDeny, btnAccept;
        public RequestViewHolder(@NonNull View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.tv_title_request);
            tvMessage = itemView.findViewById(R.id.tv_message_request);
            tvCreatedAt = itemView.findViewById(R.id.tv_created_at);
            tvMsgResult = itemView.findViewById(R.id.tv_message_result);
            btnDeny = itemView.findViewById(R.id.btn_deny);
            btnAccept = itemView.findViewById(R.id.btn_accept);
        }
    }
}
