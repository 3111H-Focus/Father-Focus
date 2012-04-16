package hkust.comp3111h.focus.ui;

import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class AdminReceiver extends DeviceAdminReceiver{
	@Override
	public DevicePolicyManager getManager(Context context) {
		return super.getManager(context);
	}
	@Override
	public ComponentName getWho(Context context) {
		return super.getWho(context);
	}
	@Override
	public void onDisabled(Context context, Intent intent) {
		super.onDisabled(context, intent);
	}
	@Override
	public CharSequence onDisableRequested(Context context, Intent intent) {
		return super.onDisableRequested(context, intent);
	}
	@Override
	public void onEnabled(Context context, Intent intent) {
		super.onEnabled(context, intent);
	}
	@Override
	public void onPasswordChanged(Context context, Intent intent) {
		super.onPasswordChanged(context, intent);
	}
	@Override
	public void onPasswordFailed(Context context, Intent intent) {
		super.onPasswordFailed(context, intent);
	}
	@Override
	public void onPasswordSucceeded(Context context, Intent intent) {
		super.onPasswordSucceeded(context, intent);
	}
	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);
	}
	@Override
	public IBinder peekService(Context myContext, Intent service) {
		return super.peekService(myContext, service);
	}
}
