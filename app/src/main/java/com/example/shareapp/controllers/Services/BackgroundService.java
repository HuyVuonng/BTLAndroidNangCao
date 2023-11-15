package com.example.shareapp.controllers.Services;

import static com.example.shareapp.controllers.constant.ReportTypeConstant.TYPE_POST;
import static com.example.shareapp.controllers.constant.ReportTypeConstant.TYPE_USER;
import static com.example.shareapp.models.User.blockUser;
import static com.example.shareapp.models.User.getUserInfor;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.shareapp.controllers.activities.LoginActivity;
import com.example.shareapp.controllers.activities.MainActivity;
import com.example.shareapp.models.Report;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.core.Context;

public class BackgroundService extends Service {
    Context context;
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        checkBlock();
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void checkBlock() {
        final int[] countReportUser = {0};
        final int[] countReportPost = {0};
        Report.getFirebaseReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                countReportUser[0] = 0;
                countReportPost[0] = 0;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Report report = dataSnapshot.getValue(Report.class);
                    if (report != null && report.getTargetId().equals(getUserInfor(getApplicationContext()).getUid()) && report.getType().equals(TYPE_USER)) {
                        countReportUser[0]++;
                    }
                    if (report != null && report.getTargetId().equals(getUserInfor(getApplicationContext()).getUid()) && report.getType().equals(TYPE_POST)) {
                        countReportPost[0]++;
                    }
                }
                if (countReportUser[0] >= 1 && countReportPost[0] >= 0) {
                    blockUser(getUserInfor(getApplicationContext()).getUid());
                    SharedPreferences editor = getApplicationContext().getSharedPreferences("data", MODE_PRIVATE);
                    editor.edit().clear().apply();

                    SharedPreferences editor1 = getApplicationContext().getSharedPreferences("dataPass", MODE_PRIVATE);
                    editor1.edit().clear().apply();
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    intent.putExtra("blocked", true);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    getApplicationContext().startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
