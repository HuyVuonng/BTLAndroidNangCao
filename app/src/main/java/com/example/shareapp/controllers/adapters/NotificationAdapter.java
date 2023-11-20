package com.example.shareapp.controllers.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
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

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {
    private List<Notification> mListNotifi;
    private Context mContext;
    public NotificationAdapter(List<Notification> listNotifi, Context context) {
        mListNotifi = listNotifi;
        mContext = context;
    }
    private View mView;
    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);

        return new NotificationViewHolder(mView);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        Notification notifi = mListNotifi.get(position);
        if(notifi == null)
            return;

        if(notifi.getType().equals(NotifiTypeConstant.TYPE_REQUEST)) {
            Request.getRequestByNotificationId(notifi.getNotifiId(), new Request.IRequestReceivedListener() {
                @Override
                public void onRequestDataReceived(Request request) {
                    if(request == null)
                        return;

                    if(request.isDeny() || request.isStatus()) {
                        notificationReplyRequest(holder);
                        if(request.isDeny()) {
                            holder.tvMsgRequest.setText("Bạn đã từ chối đề nghị này");
                        } else {
                            holder.tvMsgRequest.setText("Bạn đã chấp thuận đề nghị này");
                        }
                    } else {
                        notificationRequest(holder);
                    }
                }
            });

        }

        holder.tvTitle.setText(notifi.getTitle());
        holder.tvMessage.setText(notifi.getContent());
        holder.tvCreatedAt.setText(DateTimeMethod.timeDifference(notifi.getCreatedAt()));
        
        holder.btnDeny.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickDeny(notifi.getNotifiId(), holder);
            }
        });
        
        holder.btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickAccept(notifi.getNotifiId(), holder);
            }
        });
    }

    private void onClickHandleReplyRequest(String notifiId, String type, String contentNotifi) {
        Request.replyRequest(notifiId, type);

        Request.getRequestByNotificationId(notifiId, new Request.IRequestReceivedListener() {
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


    }

    private void onClickDeny(String notifiId, NotificationViewHolder holder) {
        onClickHandleReplyRequest(notifiId,
                NotifiTypeConstant.TYPE_DENY,
                "Yêu cầu của bạn đã bị từ chối.");

        notificationReplyRequest(holder);
        holder.tvMsgRequest.setText("Bạn đã từ chối đề nghị này.");
    }

    private void onClickAccept(String notifiId, NotificationViewHolder holder) {
        onClickHandleReplyRequest(notifiId,
                NotifiTypeConstant.TYPE_ACCEPT,
                "Yêu cầu đã được chấp thuận.");

        notificationReplyRequest(holder);
        holder.tvMsgRequest.setText("Bạn đã chấp thuận đề nghị này.");
    }

    private void notificationReplyRequest(NotificationViewHolder holder) {
        holder.linearLayout.setVisibility(View.VISIBLE);

        holder.tvMsgRequest.setVisibility(View.VISIBLE);

        holder.btnDeny.setVisibility(View.GONE);
        holder.btnAccept.setVisibility(View.GONE);
    }

    private void notificationRequest(NotificationViewHolder holder) {
        holder.linearLayout.setVisibility(View.VISIBLE);

        holder.btnDeny.setVisibility(View.VISIBLE);
        holder.btnAccept.setVisibility(View.VISIBLE);

        holder.tvMsgRequest.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return mListNotifi.size();
    }

    public class NotificationViewHolder extends RecyclerView.ViewHolder {
        LinearLayout linearLayout;
        private TextView tvTitle, tvMessage, tvCreatedAt, tvMsgRequest;
        private Button btnDeny, btnAccept;
        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);

            linearLayout = itemView.findViewById(R.id.handle_request);
            tvTitle = itemView.findViewById(R.id.tv_title_notifi);
            tvMessage = itemView.findViewById(R.id.tv_message_notifi);
            tvCreatedAt = itemView.findViewById(R.id.tv_created_at);
            tvMsgRequest = itemView.findViewById(R.id.tv_message_request);
            btnDeny = itemView.findViewById(R.id.btn_deny);
            btnAccept = itemView.findViewById(R.id.btn_accept);
        }
    }
}
