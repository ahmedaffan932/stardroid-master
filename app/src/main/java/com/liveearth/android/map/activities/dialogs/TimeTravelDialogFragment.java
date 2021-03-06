package com.liveearth.android.map.activities.dialogs;

import android.app.Dialog;
import android.os.Bundle;

import com.liveearth.android.map.activities.DynamicStarMapActivity;
import com.liveearth.android.map.inject.HasComponent;
import com.liveearth.android.map.util.MiscUtil;

import javax.inject.Inject;

/**
 * Time travel dialog fragment.
 * Created by johntaylor on 4/3/16.
 */
// TODO(jontayler): see if this crashes when backgrounded on older devices and use
// the fragment in this package if so.
public class TimeTravelDialogFragment extends android.app.DialogFragment {
  private static final String TAG = MiscUtil.getTag(TimeTravelDialogFragment.class);
  @Inject
  DynamicStarMapActivity parentActivity;

  public interface ActivityComponent {
    void inject(TimeTravelDialogFragment fragment);
  }

  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    // Activities using this dialog MUST implement this interface.  Obviously.
    ((HasComponent<ActivityComponent>) getActivity()).getComponent().inject(this);

    return new TimeTravelDialog(parentActivity,
        parentActivity.getModel());
  }
}
