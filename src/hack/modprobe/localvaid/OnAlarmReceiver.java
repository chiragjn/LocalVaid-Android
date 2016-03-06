package hack.modprobe.localvaid;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class OnAlarmReceiver extends BroadcastReceiver{

@Override
public void onReceive(Context context, Intent intent) {
    WakeIntentService.acquireStaticLock(context);
    Intent i = new Intent(context, AlarmService.class);
    context.startService(i);
}}